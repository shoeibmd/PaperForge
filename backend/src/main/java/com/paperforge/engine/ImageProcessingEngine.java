package com.paperforge.engine;

import com.paperforge.dto.ImageToPdfRequestDto;
import com.paperforge.dto.PdfToImageRequestDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ImageProcessingEngine {

    public void imagesToPdf(List<File> imageFiles, ImageToPdfRequestDto request, File outputFile) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (File imageFile : imageFiles) {
                PDImageXObject pdImage = PDImageXObject.createFromFileByExtension(imageFile, document);
                PDRectangle pageSize = getPDRectangle(request.getPageSize(), request.getOrientation(), pdImage.getWidth(), pdImage.getHeight());

                PDPage page = new PDPage(pageSize);
                document.addPage(page);

                float margin = Math.max(0, request.getMargin());
                float pageWidth = pageSize.getWidth() - (2 * margin);
                float pageHeight = pageSize.getHeight() - (2 * margin);

                float imageWidth = pdImage.getWidth();
                float imageHeight = pdImage.getHeight();

                float drawWidth = imageWidth;
                float drawHeight = imageHeight;

                String fitOption = request.getFitOption() != null ? request.getFitOption().toUpperCase() : "FIT_PAGE";

                if ("FIT_PAGE".equals(fitOption)) {
                    float scale = Math.min(pageWidth / imageWidth, pageHeight / imageHeight);
                    drawWidth = imageWidth * scale;
                    drawHeight = imageHeight * scale;
                } else if ("STRETCH".equals(fitOption)) {
                    drawWidth = pageWidth;
                    drawHeight = pageHeight;
                }

                float x = margin + (pageWidth - drawWidth) / 2;
                float y = margin + (pageHeight - drawHeight) / 2;

                try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                    cs.drawImage(pdImage, x, y, drawWidth, drawHeight);
                }
            }
            document.save(outputFile);
        }
    }

    public List<File> pdfToImages(File pdfFile, PdfToImageRequestDto request, java.util.function.BiFunction<String, String, File> tempFileCreator) throws IOException {
        List<File> renderedImageFiles = new ArrayList<>();
        int dpi = Math.min(300, Math.max(72, request.getDpi()));
        String format = request.getFormat() != null ? request.getFormat().toLowerCase() : "png";

        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            List<Integer> pagesToRender = request.getPageNumbers();
            if (pagesToRender == null || pagesToRender.isEmpty()) {
                pagesToRender = new ArrayList<>();
                for (int i = 1; i <= pageCount; i++) {
                    pagesToRender.add(i);
                }
            }

            for (int pageNum : pagesToRender) {
                if (pageNum >= 1 && pageNum <= pageCount) {
                    BufferedImage image = renderer.renderImageWithDPI(pageNum - 1, dpi);
                    File imageFile = tempFileCreator.apply("page_" + pageNum, "." + format);
                    ImageIO.write(image, format, imageFile);
                    renderedImageFiles.add(imageFile);
                }
            }
        }
        return renderedImageFiles;
    }

    private PDRectangle getPDRectangle(String sizeName, String orientation, float imageWidth, float imageHeight) {
        PDRectangle size = switch (sizeName != null ? sizeName.toUpperCase() : "A4") {
            case "LETTER" -> PDRectangle.LETTER;
            case "LEGAL" -> PDRectangle.LEGAL;
            case "EXECUTIVE" -> new PDRectangle(522, 756);
            case "AUTO" -> new PDRectangle(imageWidth, imageHeight);
            default -> PDRectangle.A4;
        };

        if ("LANDSCAPE".equalsIgnoreCase(orientation) && !"AUTO".equalsIgnoreCase(sizeName)) {
            return new PDRectangle(size.getHeight(), size.getWidth());
        }
        return size;
    }
}
