package com.paperforge.dto;

public class CompressionResultDto {

    private byte[] pdfData;
    private long originalSizeBytes;
    private long compressedSizeBytes;
    private long savedBytes;
    private double compressionPercentage;

    public CompressionResultDto() {}

    public CompressionResultDto(byte[] pdfData, long originalSizeBytes, long compressedSizeBytes, long savedBytes, double compressionPercentage) {
        this.pdfData = pdfData;
        this.originalSizeBytes = originalSizeBytes;
        this.compressedSizeBytes = compressedSizeBytes;
        this.savedBytes = savedBytes;
        this.compressionPercentage = compressionPercentage;
    }

    public byte[] getPdfData() {
        return pdfData;
    }

    public void setPdfData(byte[] pdfData) {
        this.pdfData = pdfData;
    }

    public long getOriginalSizeBytes() {
        return originalSizeBytes;
    }

    public void setOriginalSizeBytes(long originalSizeBytes) {
        this.originalSizeBytes = originalSizeBytes;
    }

    public long getCompressedSizeBytes() {
        return compressedSizeBytes;
    }

    public void setCompressedSizeBytes(long compressedSizeBytes) {
        this.compressedSizeBytes = compressedSizeBytes;
    }

    public long getSavedBytes() {
        return savedBytes;
    }

    public void setSavedBytes(long savedBytes) {
        this.savedBytes = savedBytes;
    }

    public double getCompressionPercentage() {
        return compressionPercentage;
    }

    public void setCompressionPercentage(double compressionPercentage) {
        this.compressionPercentage = compressionPercentage;
    }
}
