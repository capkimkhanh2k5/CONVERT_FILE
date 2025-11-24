package com.convertfile.service;

import com.convertfile.service.ConvertService.markdown_to_html_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownToHtmlServiceTest {

    @TempDir
    Path tempDir;

    @Test
    public void testConvertSimpleMarkdownToHtml() throws Exception {
        markdown_to_html_service service = new markdown_to_html_service();

        // Create simple Markdown file
        Path mdPath = tempDir.resolve("simple.md");
        String markdownContent = "# Hello World\n\nThis is a test.";
        Files.writeString(mdPath, markdownContent);

        Path htmlPath = tempDir.resolve("simple.html");

        // Convert
        service.convertMdToHtml(mdPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath));
        String html = Files.readString(htmlPath);
        assertFalse(html.isEmpty());
        assertTrue(html.contains("Hello World"));
    }

    @Test
    public void testConvertMarkdownWithHeadingsToHtml() throws Exception {
        markdown_to_html_service service = new markdown_to_html_service();

        // Create Markdown with headings
        Path mdPath = tempDir.resolve("headings.md");
        String markdownContent = "# Title\n## Subtitle\n\nParagraph text";
        Files.writeString(mdPath, markdownContent);

        Path htmlPath = tempDir.resolve("headings.html");

        // Convert
        service.convertMdToHtml(mdPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath));
        String html = Files.readString(htmlPath);
        assertTrue(html.contains("Title"));
        assertTrue(html.contains("Subtitle"));
        assertTrue(html.contains("Paragraph"));
    }

    @Test
    public void testConvertMarkdownWithLinksToHtml() throws Exception {
        markdown_to_html_service service = new markdown_to_html_service();

        // Create Markdown with links
        Path mdPath = tempDir.resolve("links.md");
        String markdownContent = "Visit [this link](https://example.com)";
        Files.writeString(mdPath, markdownContent);

        Path htmlPath = tempDir.resolve("links.html");

        // Convert
        service.convertMdToHtml(mdPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath));
        String html = Files.readString(htmlPath);
        assertTrue(html.contains("link"));
        assertTrue(html.contains("example.com"));
    }

    @Test
    public void testConvertEmptyMarkdownToHtml() throws Exception {
        markdown_to_html_service service = new markdown_to_html_service();

        // Create empty Markdown
        Path mdPath = tempDir.resolve("empty.md");
        Files.writeString(mdPath, "");

        Path htmlPath = tempDir.resolve("empty.html");

        // Convert
        service.convertMdToHtml(mdPath.toString(), htmlPath.toString());

        // Verify file exists
        assertTrue(Files.exists(htmlPath));
    }

    @Test
    public void testConvertNonExistentMarkdownThrowsException() {
        markdown_to_html_service service = new markdown_to_html_service();

        Path mdPath = tempDir.resolve("nonexistent.md");
        Path htmlPath = tempDir.resolve("output.html");

        // Should throw exception
        assertThrows(Exception.class, () -> {
            service.convertMdToHtml(mdPath.toString(), htmlPath.toString());
        });
    }

    @Test
    public void testConvertMarkdownWithListsToHtml() throws Exception {
        markdown_to_html_service service = new markdown_to_html_service();

        // Create Markdown with lists
        Path mdPath = tempDir.resolve("lists.md");
        String markdownContent = "- Item 1\n- Item 2\n- Item 3";
        Files.writeString(mdPath, markdownContent);

        Path htmlPath = tempDir.resolve("lists.html");

        // Convert
        service.convertMdToHtml(mdPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath));
        String html = Files.readString(htmlPath);
        assertTrue(html.contains("Item 1"));
        assertTrue(html.contains("Item 2"));
        assertTrue(html.contains("Item 3"));
    }

    @Test
    public void testConvertMarkdownWithCodeToHtml() throws Exception {
        markdown_to_html_service service = new markdown_to_html_service();

        // Create Markdown with code block
        Path mdPath = tempDir.resolve("code.md");
        String markdownContent = "```java\nSystem.out.println(\"Hello\");\n```";
        Files.writeString(mdPath, markdownContent);

        Path htmlPath = tempDir.resolve("code.html");

        // Convert
        service.convertMdToHtml(mdPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath));
        String html = Files.readString(htmlPath);
        assertTrue(html.contains("println"));
    }
}
