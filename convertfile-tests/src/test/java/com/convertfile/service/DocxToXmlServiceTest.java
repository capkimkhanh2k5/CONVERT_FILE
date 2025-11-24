package com.convertfile.service;

import com.convertfile.service.ConvertService.docx_to_xml_service;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for docx_to_xml_service
 * Tests DOCX to XML conversion functionality
 */
public class DocxToXmlServiceTest {

    private docx_to_xml_service service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new docx_to_xml_service();
    }

    /**
     * Test converting a simple DOCX file to XML
     */
    @Test
    void testConvertSimpleDocxToXml() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("simple.docx");
        Path xmlPath = tempDir.resolve("simple.xml");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Hello XML World");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToXml(docxPath.toString(), xmlPath.toString());

        // Verify
        assertTrue(Files.exists(xmlPath), "XML file should be created");
        String content = Files.readString(xmlPath);
        assertTrue(content.contains("<w:document") || content.contains("document"), "Should contain document element");
        assertTrue(content.contains("Hello XML World"), "Should contain text content");
    }

    /**
     * Test converting DOCX with multiple paragraphs to XML
     */
    @Test
    void testConvertMultipleParagraphsToXml() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("multi.docx");
        Path xmlPath = tempDir.resolve("multi.xml");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("First paragraph");
        document.createParagraph().createRun().setText("Second paragraph");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToXml(docxPath.toString(), xmlPath.toString());

        // Verify
        assertTrue(Files.exists(xmlPath), "XML file should be created");
        String content = Files.readString(xmlPath);
        assertTrue(content.contains("First paragraph"), "Should contain first paragraph");
        assertTrue(content.contains("Second paragraph"), "Should contain second paragraph");
    }

    /**
     * Test converting empty DOCX to XML
     */
    @Test
    void testConvertEmptyDocxToXml() throws IOException {
        // Create empty DOCX file
        Path docxPath = tempDir.resolve("empty.docx");
        Path xmlPath = tempDir.resolve("empty.xml");

        XWPFDocument document = new XWPFDocument();
        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToXml(docxPath.toString(), xmlPath.toString());

        // Verify
        assertTrue(Files.exists(xmlPath), "XML file should be created");
        String content = Files.readString(xmlPath);
        assertTrue(content.contains("<w:document") || content.contains("document") || content.contains("body"), "Should contain document structure even if empty");
    }

    /**
     * Test converting non-existent DOCX file throws IOException
     */
    @Test
    void testConvertNonExistentDocxThrowsException() {
        Path docxPath = tempDir.resolve("nonexistent.docx");
        Path xmlPath = tempDir.resolve("output.xml");

        assertThrows(IOException.class, () -> {
            service.convertDocxToXml(docxPath.toString(), xmlPath.toString());
        }, "Should throw IOException for non-existent file");
    }

    /**
     * Test XML output contains proper XML structure
     */
    @Test
    void testXmlOutputStructure() throws IOException {
        // Create test DOCX file
        Path docxPath = tempDir.resolve("structure.docx");
        Path xmlPath = tempDir.resolve("structure.xml");

        XWPFDocument document = new XWPFDocument();
        document.createParagraph().createRun().setText("Test content");

        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            document.write(out);
        }
        document.close();

        // Convert
        service.convertDocxToXml(docxPath.toString(), xmlPath.toString());

        // Verify XML structure
        String content = Files.readString(xmlPath);
        assertTrue(content.contains("document") || content.contains("<w:document"), "Should have document element");
        assertTrue(content.contains("body") || content.contains("<w:body"), "Should have body element");
        assertTrue(content.length() > 0, "XML should not be empty");
    }
}
