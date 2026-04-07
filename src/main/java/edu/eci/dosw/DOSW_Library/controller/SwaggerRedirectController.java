package edu.eci.dosw.DOSW_Library.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerRedirectController {

    @GetMapping("/swagger-ui.htm")
    public String redirectLegacySwaggerPath() {
        return "redirect:/swagger-ui.html";
    }
}
