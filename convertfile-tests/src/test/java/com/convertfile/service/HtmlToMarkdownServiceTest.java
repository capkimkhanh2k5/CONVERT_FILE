package com.convertfile.service;

import com.convertfile.service.ConvertService.html_to_markdown_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class HtmlToMarkdownServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testConvertSimpleHtmlToMarkdown() throws Exception {
        html_to_markdown_service service = new html_to_markdown_service();

        // Create simple HTML file
        Path htmlPath = tempDir.resolve("simple.html");
        String htmlContent = "<html><body><h1>Hello World</h1><p>This is a test.</p></body></html>";
        Files.writeString(htmlPath, htmlContent);

        Path mdPath = tempDir.resolve("simple.md");

        // Convert
        service.convertHtmlToMd(htmlPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath));
        String markdown = Files.readString(mdPath);
        assertFalse(markdown.isEmpty());
        assertTrue(markdown.contains("Hello World") || markdown.contains("# Hello"));
    }

    @Test
    public void testConvertHtmlWithHeadingsToMarkdown() throws Exception {
        html_to_markdown_service service = new html_to_markdown_service();

        // Create HTML with headings
        Path htmlPath = tempDir.resolve("headings.html");
        String htmlContent = "<html><body>" +
                "<h1>Title</h1>" +
                "<h2>Subtitle</h2>" +
                "<p>Paragraph text</p>" +
                "</body></html>";
        Files.writeString(htmlPath, htmlContent);

        Path mdPath = tempDir.resolve("headings.md");

        // Convert
        service.convertHtmlToMd(htmlPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath));
        String markdown = Files.readString(mdPath);
        assertTrue(markdown.contains("Title"));
        assertTrue(markdown.contains("Subtitle"));
    }

    @Test
    public void testConvertHtmlWithLinksToMarkdown() throws Exception {
        html_to_markdown_service service = new html_to_markdown_service();

        // Create HTML with links
        Path htmlPath = tempDir.resolve("links.html");
        String htmlContent = "<html><body>" +
                "<p>Visit <a href='https://example.com'>this link</a></p>" +
                "</body></html>";
        Files.writeString(htmlPath, htmlContent);

        Path mdPath = tempDir.resolve("links.md");

        // Convert
        service.convertHtmlToMd(htmlPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath));
        String markdown = Files.readString(mdPath);
        assertTrue(markdown.contains("link") || markdown.contains("example.com"));
    }

    @Test
    public void testConvertEmptyHtmlToMarkdown() throws Exception {
        html_to_markdown_service service = new html_to_markdown_service();

        // Create empty HTML
        Path htmlPath = tempDir.resolve("empty.html");
        Files.writeString(htmlPath, "<html><body></body></html>");

        Path mdPath = tempDir.resolve("empty.md");

        // Convert
        service.convertHtmlToMd(htmlPath.toString(), mdPath.toString());

        // Verify file exists
        assertTrue(Files.exists(mdPath));
    }

    @Test
    public void testConvertNonExistentHtmlThrowsException() {
        html_to_markdown_service service = new html_to_markdown_service();

        Path htmlPath = tempDir.resolve("nonexistent.html");
        Path mdPath = tempDir.resolve("output.md");

        // Should throw exception
        assertThrows(Exception.class, () -> {
            service.convertHtmlToMd(htmlPath.toString(), mdPath.toString());
        });
    }

    @Test
    public void testConvertHtmlWithListsToMarkdown() throws Exception {
        html_to_markdown_service service = new html_to_markdown_service();

        // Create HTML with lists
        Path htmlPath = tempDir.resolve("lists.html");
        String htmlContent = "<html><body>" +
                "<ul><li>Item 1</li><li>Item 2</li></ul>" +
                "</body></html>";
        Files.writeString(htmlPath, htmlContent);

        Path mdPath = tempDir.resolve("lists.md");

        // Convert
        service.convertHtmlToMd(htmlPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath));
        String markdown = Files.readString(mdPath);
        assertTrue(markdown.contains("Item 1"));
        assertTrue(markdown.contains("Item 2"));
    }
}
