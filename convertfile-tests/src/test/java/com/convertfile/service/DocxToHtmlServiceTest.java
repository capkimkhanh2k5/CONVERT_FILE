package com.convertfile.service;

import com.convertfile.service.ConvertService.docx_to_html_service;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for docx_to_html_service
 * Tests DOCX to HTML conversion functionality
 */
public class DocxToHtmlServiceTest {

    private docx_to_html_service service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new docx_to_html_service();
    }

    /**
     * Test converting simple DOCX to HTML
     */
    @Test
    void testConvertSimpleDocxToHtml() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("simple.docx");
        Path htmlPath = tempDir.resolve("simple.html");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Hello HTML World");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxtoHtml(docxPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath), "HTML file should be created");
        String content = Files.readString(htmlPath);
        assertTrue(content.contains("Hello HTML World"), "HTML should contain text content");
    }

    /**
     * Test converting DOCX with multiple paragraphs to HTML
     */
    @Test
    void testConvertMultipleParagraphsToHtml() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("multi.docx");
        Path htmlPath = tempDir.resolve("multi.html");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("First paragraph");
        document.createParagraph().createRun().setText("Second paragraph");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxtoHtml(docxPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath), "HTML file should be created");
        String content = Files.readString(htmlPath);
        assertTrue(content.contains("First paragraph"), "HTML should contain first paragraph");
        assertTrue(content.contains("Second paragraph"), "HTML should contain second paragraph");
    }

    /**
     * Test converting empty DOCX to HTML
     */
    @Test
    void testConvertEmptyDocxToHtml() throws IOException {
        // Create empty DOCX file
        Path docxPath = tempDir.resolve("empty.docx");
        Path htmlPath = tempDir.resolve("empty.html");

        XWPFDocument document = new XWPFDocument();
        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxtoHtml(docxPath.toString(), htmlPath.toString());

        // Verify
        assertTrue(Files.exists(htmlPath), "HTML file should be created");
        String content = Files.readString(htmlPath);
        assertNotNull(content, "HTML content should not be null");
    }

    /**
     * Test converting non-existent DOCX throws IOException
     */
    @Test
    void testConvertNonExistentDocxThrowsException() {
        Path docxPath = tempDir.resolve("nonexistent.docx");
        Path htmlPath = tempDir.resolve("output.html");

        assertThrows(IOException.class, () -> {
            service.convertDocxtoHtml(docxPath.toString(), htmlPath.toString());
        }, "Should throw IOException for non-existent file");
    }

    /**
     * Test HTML output contains proper HTML structure
     */
    @Test
    void testHtmlOutputStructure() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("structure.docx");
        Path htmlPath = tempDir.resolve("structure.html");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Test content");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxtoHtml(docxPath.toString(), htmlPath.toString());

        // Verify HTML structure
        String content = Files.readString(htmlPath);
        assertTrue(content.contains("<html") || content.contains("<HTML"), "Should have HTML tag");
        assertTrue(content.contains("Test content"), "Should contain test content");
    }

    /**
     * Test images folder is created
     */
    @Test
    void testImagesFolderCreation() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("test.docx");
        Path htmlPath = tempDir.resolve("test.html");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Test");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxtoHtml(docxPath.toString(), htmlPath.toString());

        // Verify images folder
        Path imagesFolder = tempDir.resolve("images");
        assertTrue(Files.exists(imagesFolder), "Images folder should be created");
        assertTrue(Files.isDirectory(imagesFolder), "Images path should be a directory");
    }
}
