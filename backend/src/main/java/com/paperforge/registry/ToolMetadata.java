package com.paperforge.registry;

import com.paperforge.annotation.ToolIO;
import com.paperforge.model.FileType;

public class ToolMetadata {
    private final String id;
    private final String name;
    private final String description;
    private final FileType accepts;
    private final FileType produces;

    public ToolMetadata(String id, String name, String description, FileType accepts, FileType produces) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.accepts = accepts;
        this.produces = produces;
    }

    public static ToolMetadata fromAnnotation(ToolIO toolIO) {
        return new ToolMetadata(toolIO.id(), toolIO.name(), toolIO.description(), toolIO.accepts(), toolIO.produces());
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public FileType getAccepts() { return accepts; }
    public FileType getProduces() { return produces; }
}
