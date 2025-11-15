package com.convertfile.service.microService;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class html_to_markdown_service {
    public void convertHtmlToMd(String htmlPath, String mdPath) throws Exception {
        String html = Files.readString(Paths.get(htmlPath));
        String markdown = FlexmarkHtmlConverter.builder().build().convert(html);
        Files.writeString(Paths.get(mdPath), markdown);
        System.out.println("HTML converted to Markdown.");
    }
}

