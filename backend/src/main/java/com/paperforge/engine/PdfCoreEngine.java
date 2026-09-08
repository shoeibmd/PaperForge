package com.paperforge.engine;

import com.paperforge.dto.PdfMetadataDto;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PdfCoreEngine {

    public void merge(List<File> pdfFiles, File outputFile) throws IOException {
        PDFMergerUtility merger = new PDFMergerUtility();
        merger.setDestinationFileName(outputFile.getAbsolutePath());
        for (File pdf : pdfFiles) {
            merger.addSource(pdf);
        }
        merger.mergeDocuments(null);
    }

    public List<PDDocument> split(File pdfFile, int splitFrequency) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            Splitter splitter = new Splitter();
            if (splitFrequency > 0) {
                splitter.setSplitAtPage(splitFrequency);
            }
            return splitter.split(document);
        }
    }

    public void rotate(File pdfFile, int angle, List<Integer> pageNumbers, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            int pageCount = document.getNumberOfPages();
            for (int i = 1; i <= pageCount; i++) {
                if (pageNumbers == null || pageNumbers.isEmpty() || pageNumbers.contains(i)) {
                    PDPage page = document.getPage(i - 1);
                    int currentRotation = page.getRotation();
                    page.setRotation((currentRotation + angle) % 360);
                }
            }
            document.save(outputFile);
        }
    }

    public void deletePages(File pdfFile, List<Integer> pageNumbers, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            // Sort page numbers descending to delete safely without shifting indices
            List<Integer> pagesToDelete = new ArrayList<>(pageNumbers);
            pagesToDelete.sort(Collections.reverseOrder());

            for (int pageNum : pagesToDelete) {
                if (pageNum >= 1 && pageNum <= document.getNumberOfPages()) {
                    document.removePage(pageNum - 1);
                }
            }
            if (document.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("Cannot delete all pages from PDF");
            }
            document.save(outputFile);
        }
    }

    public void extractPages(File pdfFile, List<Integer> pageNumbers, File outputFile) throws IOException {
        try (PDDocument sourceDoc = Loader.loadPDF(pdfFile);
             PDDocument targetDoc = new PDDocument()) {

            for (int pageNum : pageNumbers) {
                if (pageNum >= 1 && pageNum <= sourceDoc.getNumberOfPages()) {
                    targetDoc.addPage(sourceDoc.getPage(pageNum - 1));
                }
            }
            if (targetDoc.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("No valid pages selected for extraction");
            }
            targetDoc.save(outputFile);
        }
    }

    public void reorderPages(File pdfFile, List<Integer> newPageOrder, File outputFile) throws IOException {
        try (PDDocument sourceDoc = Loader.loadPDF(pdfFile);
             PDDocument targetDoc = new PDDocument()) {

            for (int pageNum : newPageOrder) {
                if (pageNum >= 1 && pageNum <= sourceDoc.getNumberOfPages()) {
                    targetDoc.addPage(sourceDoc.getPage(pageNum - 1));
                }
            }
            if (targetDoc.getNumberOfPages() == 0) {
                throw new IllegalArgumentException("Invalid page order list provided");
            }
            targetDoc.save(outputFile);
        }
    }

    public void crop(File pdfFile, float x, float y, float width, float height, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDRectangle cropBox = new PDRectangle(x, y, width, height);
            for (PDPage page : document.getPages()) {
                page.setCropBox(cropBox);
            }
            document.save(outputFile);
        }
    }

    public PdfMetadataDto getMetadata(File pdfFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDDocumentInformation info = document.getDocumentInformation();
            return new PdfMetadataDto(
                    info.getTitle(),
                    info.getAuthor(),
                    info.getSubject(),
                    info.getKeywords(),
                    info.getCreator(),
                    info.getProducer()
            );
        }
    }

    public void setMetadata(File pdfFile, PdfMetadataDto metadata, File outputFile) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfFile)) {
            PDDocumentInformation info = document.getDocumentInformation();
            if (info == null) {
                info = new PDDocumentInformation();
            }
            if (metadata.getTitle() != null) info.setTitle(metadata.getTitle());
            if (metadata.getAuthor() != null) info.setAuthor(metadata.getAuthor());
            if (metadata.getSubject() != null) info.setSubject(metadata.getSubject());
            if (metadata.getKeywords() != null) info.setKeywords(metadata.getKeywords());
            if (metadata.getCreator() != null) info.setCreator(metadata.getCreator());
            if (metadata.getProducer() != null) info.setProducer(metadata.getProducer());

            document.setDocumentInformation(info);
            document.save(outputFile);
        }
    }
}
