package com.convertfile.service;

import com.convertfile.service.ConvertService.docx_to_markdown_service;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for docx_to_markdown_service
 * Tests DOCX to Markdown conversion functionality
 */
public class DocxToMarkdownServiceTest {

    private docx_to_markdown_service service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new docx_to_markdown_service();
    }

    /**
     * Test converting simple DOCX to Markdown
     */
    @Test
    void testConvertSimpleDocxToMarkdown() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("simple.docx");
        Path mdPath = tempDir.resolve("simple.md");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Hello Markdown World");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToMarkdown(docxPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath), "Markdown file should be created");
        String content = Files.readString(mdPath);
        assertTrue(content.contains("Hello Markdown World"), "Markdown should contain text content");
    }

    /**
     * Test converting DOCX with multiple paragraphs to Markdown
     */
    @Test
    void testConvertMultipleParagraphsToMarkdown() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("multi.docx");
        Path mdPath = tempDir.resolve("multi.md");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("First paragraph");
        document.createParagraph().createRun().setText("Second paragraph");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToMarkdown(docxPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath), "Markdown file should be created");
        String content = Files.readString(mdPath);
        assertTrue(content.contains("First paragraph"), "Markdown should contain first paragraph");
        assertTrue(content.contains("Second paragraph"), "Markdown should contain second paragraph");
    }

    /**
     * Test converting empty DOCX to Markdown
     */
    @Test
    void testConvertEmptyDocxToMarkdown() throws IOException {
        // Create empty DOCX file
        Path docxPath = tempDir.resolve("empty.docx");
        Path mdPath = tempDir.resolve("empty.md");

        XWPFDocument document = new XWPFDocument();
        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToMarkdown(docxPath.toString(), mdPath.toString());

        // Verify
        assertTrue(Files.exists(mdPath), "Markdown file should be created");
    }

    /**
     * Test converting non-existent DOCX throws IOException
     */
    @Test
    void testConvertNonExistentDocxThrowsException() {
        Path docxPath = tempDir.resolve("nonexistent.docx");
        Path mdPath = tempDir.resolve("output.md");

        assertThrows(IOException.class, () -> {
            service.convertDocxToMarkdown(docxPath.toString(), mdPath.toString());
        }, "Should throw IOException for non-existent file");
    }

    /**
     * Test images folder is created
     */
    @Test
    void testImagesFolderCreation() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("test.docx");
        Path mdPath = tempDir.resolve("test.md");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Test");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToMarkdown(docxPath.toString(), mdPath.toString());

        // Verify images folder
        Path imagesFolder = tempDir.resolve("images");
        assertTrue(Files.exists(imagesFolder), "Images folder should be created");
        assertTrue(Files.isDirectory(imagesFolder), "Images path should be a directory");
    }
}
