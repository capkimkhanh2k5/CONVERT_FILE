package com.convertfile.service.microService;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import java.nio.file.Files;
import java.nio.file.Paths;

public class markdown_to_html_service {
    public void convertMdToHtml(String mdPath, String htmlPath) throws Exception {
        String markdown = Files.readString(Paths.get(mdPath));
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);
        Files.writeString(Paths.get(htmlPath), html);
        System.out.println("Markdown converted to HTML.");
    }
}

