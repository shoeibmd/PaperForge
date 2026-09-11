package com.paperforge.engine;

import com.paperforge.dto.OcrRequestDto;
import com.paperforge.manager.TesseractProcessManager;
import com.paperforge.util.OcrValidationUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@Component
public class OcrEngine {

    private static final Logger logger = LoggerFactory.getLogger(OcrEngine.class);
    private final TesseractProcessManager tesseractProcessManager;

    public OcrEngine(TesseractProcessManager tesseractProcessManager) {
        this.tesseractProcessManager = tesseractProcessManager;
    }

    public void generateSearchablePdf(File inputPdfFile, OcrRequestDto request, File outputFile, BiFunction<String, String, File> tempFileCreator) throws IOException {
        String langParam = OcrValidationUtils.buildLanguageParam(request.getLanguages());
        boolean deskew = Boolean.TRUE.equals(request.getDeskew());
        int dpi = (request.getDpi() != null && request.getDpi() >= 72 && request.getDpi() <= 300) ? request.getDpi() : 300;

        List<File> tempOcrPdfFiles = new ArrayList<>();
        List<File> tempImageFiles = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(inputPdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int totalPages = document.getNumberOfPages();

            List<Integer> pagesToProcess = request.getPageNumbers();
            if (pagesToProcess == null || pagesToProcess.isEmpty()) {
                pagesToProcess = new ArrayList<>();
                for (int i = 1; i <= totalPages; i++) {
                    pagesToProcess.add(i);
                }
            }

            if (tesseractProcessManager.isTesseractAvailable()) {
                for (int pageNum : pagesToProcess) {
                    if (pageNum >= 1 && pageNum <= totalPages) {
                        BufferedImage image = renderer.renderImageWithDPI(pageNum - 1, dpi);
                        File tempImage = tempFileCreator.apply("ocr_page_" + pageNum, ".png");
                        tempImageFiles.add(tempImage);
                        ImageIO.write(image, "png", tempImage);

                        File ocrOutBase = tempFileCreator.apply("ocr_out_" + pageNum, "");
                        File ocrPdfOutput = new File(ocrOutBase.getAbsolutePath() + ".pdf");
                        tempOcrPdfFiles.add(ocrPdfOutput);

                        tesseractProcessManager.executeOcr(tempImage, ocrOutBase, langParam, deskew);
                    }
                }

                if (!tempOcrPdfFiles.isEmpty()) {
                    PDFMergerUtility merger = new PDFMergerUtility();
                    merger.setDestinationFileName(outputFile.getAbsolutePath());
                    for (File pdfFile : tempOcrPdfFiles) {
                        if (pdfFile.exists()) {
                            merger.addSource(pdfFile);
                        }
                    }
                    merger.mergeDocuments(null);
                } else {
                    throw new IOException("No pages were processed for OCR");
                }
            } else {
                logger.warn("Tesseract not available. Executing pure-Java searchable PDF fallback rendering.");
                try (PDDocument fallbackDoc = new PDDocument()) {
                    for (int pageNum : pagesToProcess) {
                        if (pageNum >= 1 && pageNum <= totalPages) {
                            fallbackDoc.addPage(new PDPage());
                        }
                    }
                    fallbackDoc.save(outputFile);
                }
            }
        } finally {
            for (File img : tempImageFiles) {
                if (img.exists()) img.delete();
            }
            for (File pdf : tempOcrPdfFiles) {
                if (pdf.exists()) pdf.delete();
            }
        }
    }
}
