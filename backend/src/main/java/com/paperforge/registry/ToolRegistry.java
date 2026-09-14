package com.paperforge.registry;

import com.paperforge.annotation.ToolIO;
import com.paperforge.model.FileType;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ToolRegistry {

    private final Map<String, ToolMetadata> registeredTools = new ConcurrentHashMap<>();

    public ToolRegistry(ApplicationContext applicationContext) {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ToolIO.class);
        for (Object bean : beans.values()) {
            ToolIO toolIO = bean.getClass().getAnnotation(ToolIO.class);
            if (toolIO != null) {
                registeredTools.put(toolIO.id(), ToolMetadata.fromAnnotation(toolIO));
            }
        }

        // Register default PaperForge engines if spring annotations not attached directly
        registerDefaultTools();
    }

    private void registerDefaultTools() {
        registerIfAbsent("merge", "Merge PDFs", "Combine multiple PDF documents", FileType.PDF, FileType.PDF);
        registerIfAbsent("split", "Split PDF", "Extract pages from PDF", FileType.PDF, FileType.PDF);
        registerIfAbsent("rotate", "Rotate PDF", "Rotate PDF pages", FileType.PDF, FileType.PDF);
        registerIfAbsent("crop", "Crop PDF", "Crop page bounds", FileType.PDF, FileType.PDF);
        registerIfAbsent("protect", "Protect PDF", "Encrypt PDF with password", FileType.PDF, FileType.PDF);
        registerIfAbsent("convert-to-pdf", "Convert to PDF", "Convert Office/Text to PDF", FileType.DOCUMENT, FileType.PDF);
        registerIfAbsent("pdf-to-doc", "PDF to Office", "Export PDF to Office/Text", FileType.PDF, FileType.DOCUMENT);
        registerIfAbsent("img-to-pdf", "Images to PDF", "Convert JPEG/PNG/WEBP to PDF", FileType.IMAGE, FileType.PDF);
        registerIfAbsent("pdf-to-img", "PDF to Images", "Render PDF pages to images", FileType.PDF, FileType.IMAGE);
        registerIfAbsent("ocr", "OCR Recognition", "Generate searchable PDF", FileType.PDF, FileType.PDF);
        registerIfAbsent("compress", "Compress PDF", "Optimize PDF stream size", FileType.PDF, FileType.PDF);
    }

    private void registerIfAbsent(String id, String name, String desc, FileType accepts, FileType produces) {
        registeredTools.putIfAbsent(id, new ToolMetadata(id, name, desc, accepts, produces));
    }

    public Optional<ToolMetadata> getTool(String id) {
        return Optional.ofNullable(registeredTools.get(id));
    }

    public List<ToolMetadata> getAllTools() {
        return new ArrayList<>(registeredTools.values());
    }
}
