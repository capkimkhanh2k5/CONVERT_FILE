package com.convertfile.service;

import com.convertfile.service.ConvertService.xml_to_docx_service;
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
 * Test class for xml_to_docx_service
 * Tests XML to DOCX conversion functionality
 */
public class XmlToDocxServiceTest {

    private xml_to_docx_service service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new xml_to_docx_service();
    }

    /**
     * Test converting simple XML to DOCX
     */
    @Test
    void testConvertSimpleXmlToDocx() throws IOException {
        // Create test XML file with Word document structure
        Path xmlPath = tempDir.resolve("simple.xml");
        Path docxPath = tempDir.resolve("simple.docx");

        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                "<w:body>" +
                "<w:p><w:r><w:t>Hello from XML</w:t></w:r></w:p>" +
                "</w:body>" +
                "</w:document>";

        Files.write(xmlPath, xmlContent.getBytes());

        // Convert
        service.convertXmlToDocx(xmlPath.toString(), docxPath.toString());

        // Verify
        assertTrue(Files.exists(docxPath), "DOCX file should be created");
        
        // Verify content
        try (FileInputStream fis = new FileInputStream(docxPath.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertFalse(doc.getParagraphs().isEmpty(), "DOCX should have paragraphs");
            String text = doc.getParagraphs().get(0).getText();
            assertTrue(text.contains("Hello from XML"), "DOCX should contain text from XML");
        }
    }

    /**
     * Test converting XML with multiple paragraphs to DOCX
     */
    @Test
    void testConvertMultipleParagraphsXmlToDocx() throws IOException {
        // Create test XML file
        Path xmlPath = tempDir.resolve("multi.xml");
        Path docxPath = tempDir.resolve("multi.docx");

        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                "<w:body>" +
                "<w:p><w:r><w:t>First paragraph</w:t></w:r></w:p>" +
                "<w:p><w:r><w:t>Second paragraph</w:t></w:r></w:p>" +
                "</w:body>" +
                "</w:document>";

        Files.write(xmlPath, xmlContent.getBytes());

        // Convert
        service.convertXmlToDocx(xmlPath.toString(), docxPath.toString());

        // Verify
        assertTrue(Files.exists(docxPath), "DOCX file should be created");
        
        try (FileInputStream fis = new FileInputStream(docxPath.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertTrue(doc.getParagraphs().size() >= 2, "DOCX should have at least 2 paragraphs");
        }
    }

    /**
     * Test converting empty XML to DOCX
     */
    @Test
    void testConvertEmptyXmlToDocx() throws IOException {
        // Create empty XML document
        Path xmlPath = tempDir.resolve("empty.xml");
        Path docxPath = tempDir.resolve("empty.docx");

        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                "<w:body>" +
                "</w:body>" +
                "</w:document>";

        Files.write(xmlPath, xmlContent.getBytes());

        // Convert
        service.convertXmlToDocx(xmlPath.toString(), docxPath.toString());

        // Verify
        assertTrue(Files.exists(docxPath), "DOCX file should be created");
        
        try (FileInputStream fis = new FileInputStream(docxPath.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertNotNull(doc, "DOCX should be valid even if empty");
        }
    }

    /**
     * Test converting non-existent XML file throws IOException
     */
    @Test
    void testConvertNonExistentXmlThrowsException() {
        Path xmlPath = tempDir.resolve("nonexistent.xml");
        Path docxPath = tempDir.resolve("output.docx");

        assertThrows(IOException.class, () -> {
            service.convertXmlToDocx(xmlPath.toString(), docxPath.toString());
        }, "Should throw IOException for non-existent file");
    }

    /**
     * Test converting invalid XML throws IOException
     */
    @Test
    void testConvertInvalidXmlThrowsException() throws IOException {
        // Create invalid XML file
        Path xmlPath = tempDir.resolve("invalid.xml");
        Path docxPath = tempDir.resolve("output.docx");

        Files.write(xmlPath, "This is not valid XML".getBytes());

        assertThrows(IOException.class, () -> {
            service.convertXmlToDocx(xmlPath.toString(), docxPath.toString());
        }, "Should throw IOException for invalid XML");
    }

    /**
     * Test round-trip conversion (DOCX -> XML -> DOCX)
     */
    @Test
    void testRoundTripConversion() throws IOException {
        // Create valid Word XML
        Path xmlPath = tempDir.resolve("roundtrip.xml");
        Path docxPath = tempDir.resolve("roundtrip.docx");

        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                "<w:body>" +
                "<w:p><w:r><w:t>Round trip test</w:t></w:r></w:p>" +
                "</w:body>" +
                "</w:document>";

        Files.write(xmlPath, xmlContent.getBytes());

        // Convert XML to DOCX
        service.convertXmlToDocx(xmlPath.toString(), docxPath.toString());

        // Verify DOCX is readable
        assertTrue(Files.exists(docxPath), "DOCX file should be created");
        try (FileInputStream fis = new FileInputStream(docxPath.toFile());
             XWPFDocument doc = new XWPFDocument(fis)) {
            assertNotNull(doc, "DOCX should be valid");
            assertFalse(doc.getParagraphs().isEmpty(), "DOCX should have content");
        }
    }
}
