package com.paperforge.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    @GetMapping(value = {
            "/",
            "/login",
            "/register",
            "/tools",
            "/editor",
            "/pipelines",
            "/api-keys",
            "/admin",
            "/admin/**",
            "/settings",
            "/help",
            "/about"
    })
    public String forwardToSpaIndex() {
        return "forward:/index.html";
    }
}
