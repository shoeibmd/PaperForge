package com.paperforge.annotation;

import com.paperforge.model.FileType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ToolIO {
    String id();
    String name();
    String description();
    FileType accepts();
    FileType produces();
}
