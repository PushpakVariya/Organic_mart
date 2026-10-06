package com.organicmart.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the current page's URI available to every Thymeleaf template as
 * "currentUri", so the language switcher (EN/GU buttons in the navbar)
 * can build a link back to the same page with ?lang=xx appended.
 *
 * This avoids relying on Thymeleaf's "#request" expression object, whose
 * behavior differs across Thymeleaf/Spring Boot versions and caused a
 * page-rendering crash in an earlier build.
 */
@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("currentUri")
    public String currentUri(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
