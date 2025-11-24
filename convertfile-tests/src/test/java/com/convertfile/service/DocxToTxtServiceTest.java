package com.convertfile.service;

import com.convertfile.service.ConvertService.docx_to_txt_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.poi.xwpf.usermodel.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for docx_to_txt_service
 * Tests DOCX to TXT conversion functionality
 */
public class DocxToTxtServiceTest {

    @TempDir
    Path tempDir;

    /**
     * Test basic DOCX to TXT conversion
     */
    @Test
    public void testValidDocxToTxt() throws Exception {
        // Create test DOCX file
        Path docxFile = tempDir.resolve("test.docx");
        createTestDocx(docxFile, "Hello World", "This is a test document.", "End of file.");
        
        // Convert to TXT
        Path txtFile = tempDir.resolve("output.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        // Verify TXT file exists
        assertTrue(Files.exists(txtFile), "TXT file should be created");
        
        // Verify TXT content
        String content = Files.readString(txtFile);
        assertTrue(content.contains("Hello World"), "Should contain first paragraph");
        assertTrue(content.contains("This is a test document"), "Should contain second paragraph");
        assertTrue(content.contains("End of file"), "Should contain third paragraph");
    }

    /**
     * Test DOCX with multiple paragraphs
     */
    @Test
    public void testMultipleParagraphs() throws Exception {
        Path docxFile = tempDir.resolve("multi.docx");
        createTestDocx(docxFile, 
            "Paragraph 1",
            "Paragraph 2", 
            "Paragraph 3",
            "Paragraph 4",
            "Paragraph 5"
        );
        
        Path txtFile = tempDir.resolve("multi.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        String content = Files.readString(txtFile);
        
        // Should contain all paragraphs
        for (int i = 1; i <= 5; i++) {
            assertTrue(content.contains("Paragraph " + i), 
                "Should contain Paragraph " + i);
        }
    }

    /**
     * Test empty DOCX file
     */
    @Test
    public void testEmptyDocx() throws Exception {
        Path docxFile = tempDir.resolve("empty.docx");
        createEmptyDocx(docxFile);
        
        Path txtFile = tempDir.resolve("empty.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        assertTrue(Files.exists(txtFile), "TXT file should be created");
        
        String content = Files.readString(txtFile);
        assertTrue(content.isEmpty() || content.trim().isEmpty(), 
            "Empty DOCX should produce empty or whitespace-only TXT");
    }

    /**
     * Test DOCX with special characters
     */
    @Test
    public void testSpecialCharacters() throws Exception {
        Path docxFile = tempDir.resolve("special.docx");
        createTestDocx(docxFile, 
            "Special: !@#$%^&*()",
            "Unicode: áéíóú ñ",
            "Symbols: © ® ™"
        );
        
        Path txtFile = tempDir.resolve("special.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        String content = Files.readString(txtFile);
        assertTrue(content.contains("!@#$%"), "Should preserve special characters");
    }

    /**
     * Test DOCX with table (should convert to text format)
     */
    @Test
    public void testDocxWithTable() throws Exception {
        Path docxFile = tempDir.resolve("table.docx");
        createDocxWithTable(docxFile);
        
        Path txtFile = tempDir.resolve("table.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        String content = Files.readString(txtFile);
        
        // Should contain table data in some format
        assertTrue(content.contains("Header1") || content.contains("Cell1"), 
            "Should contain table data");
    }

    /**
     * Test file not found error
     */
    @Test
    public void testFileNotFound() {
        Path docxFile = tempDir.resolve("nonexistent.docx");
        Path txtFile = tempDir.resolve("output.txt");
        
        docx_to_txt_service service = new docx_to_txt_service();
        
        assertThrows(IOException.class, () -> {
            service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        }, "Should throw exception for missing file");
    }

    /**
     * Test null/empty path validation
     */
    @Test
    public void testNullPaths() {
        docx_to_txt_service service = new docx_to_txt_service();
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertDocxToTxt(null, "output.txt");
        }, "Should throw exception for null input path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertDocxToTxt("input.docx", null);
        }, "Should throw exception for null output path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertDocxToTxt("", "output.txt");
        }, "Should throw exception for empty input path");
    }

    /**
     * Test invalid file format
     */
    @Test
    public void testInvalidFileFormat() throws Exception {
        // Create a non-DOCX file
        Path invalidFile = tempDir.resolve("invalid.docx");
        Files.writeString(invalidFile, "This is not a valid DOCX file");
        
        Path txtFile = tempDir.resolve("output.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        
        assertThrows(IOException.class, () -> {
            service.convertDocxToTxt(invalidFile.toString(), txtFile.toString());
        }, "Should throw exception for invalid DOCX format");
    }

    /**
     * Test DOCX with long text
     */
    @Test
    public void testLongText() throws Exception {
        Path docxFile = tempDir.resolve("long.docx");
        
        // Create document with long paragraphs
        StringBuilder longText = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            longText.append("This is sentence number ").append(i).append(". ");
        }
        
        createTestDocx(docxFile, longText.toString());
        
        Path txtFile = tempDir.resolve("long.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        String content = Files.readString(txtFile);
        assertTrue(content.contains("sentence number 0"), "Should contain first sentence");
        assertTrue(content.contains("sentence number 99"), "Should contain last sentence");
    }

    /**
     * Test DOCX with line breaks
     */
    @Test
    public void testLineBreaks() throws Exception {
        Path docxFile = tempDir.resolve("breaks.docx");
        createTestDocx(docxFile, "Line 1", "", "Line 3", "", "Line 5");
        
        Path txtFile = tempDir.resolve("breaks.txt");
        docx_to_txt_service service = new docx_to_txt_service();
        service.convertDocxToTxt(docxFile.toString(), txtFile.toString());
        
        String content = Files.readString(txtFile);
        assertTrue(content.contains("Line 1"), "Should contain Line 1");
        assertTrue(content.contains("Line 3"), "Should contain Line 3");
        assertTrue(content.contains("Line 5"), "Should contain Line 5");
    }

    // ===== Helper Methods =====

    /**
     * Create test DOCX file with given paragraphs
     */
    private void createTestDocx(Path path, String... paragraphs) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            for (String text : paragraphs) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText(text);
            }
            
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                document.write(out);
            }
        }
    }

    /**
     * Create empty DOCX file
     */
    private void createEmptyDocx(Path path) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                document.write(out);
            }
        }
    }

    /**
     * Create DOCX with table
     */
    private void createDocxWithTable(Path path) throws IOException {
        try (XWPFDocument document = new XWPFDocument()) {
            // Create a paragraph before table
            XWPFParagraph para = document.createParagraph();
            para.createRun().setText("Document with table:");
            
            // Create table
            XWPFTable table = document.createTable(2, 2);
            
            // Header row
            table.getRow(0).getCell(0).setText("Header1");
            table.getRow(0).getCell(1).setText("Header2");
            
            // Data row
            table.getRow(1).getCell(0).setText("Cell1");
            table.getRow(1).getCell(1).setText("Cell2");
            
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                document.write(out);
            }
        }
    }
}
