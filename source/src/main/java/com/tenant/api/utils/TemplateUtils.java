package com.tenant.api.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;

public class TemplateUtils {
    public static String loadTemplate(String templateName) throws IOException {
        Resource resource = new ClassPathResource("templates/" + templateName);
        return Files.readString(resource.getFile().toPath());
    }
}
