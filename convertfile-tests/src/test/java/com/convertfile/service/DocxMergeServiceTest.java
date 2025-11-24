package com.convertfile.service;

import com.convertfile.service.ConvertService.docx_merge_service;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for docx_merge_service
 * Tests DOCX merging functionality
 */
public class DocxMergeServiceTest {

    private docx_merge_service service;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new docx_merge_service();
    }

    /**
     * Test merging two DOCX files
     */
    @Test
    void testMergeTwoDocxFiles() throws IOException {
        // Create first DOCX file
        Path docx1Path = tempDir.resolve("doc1.docx");
        XWPFDocument doc1 = new XWPFDocument();
        doc1.createParagraph().createRun().setText("First document content");
        try (FileOutputStream out = new FileOutputStream(docx1Path.toFile())) {
            doc1.write(out);
        }
        doc1.close();

        // Create second DOCX file
        Path docx2Path = tempDir.resolve("doc2.docx");
        XWPFDocument doc2 = new XWPFDocument();
        doc2.createParagraph().createRun().setText("Second document content");
        try (FileOutputStream out = new FileOutputStream(docx2Path.toFile())) {
            doc2.write(out);
        }
        doc2.close();

        // Merge
        Path mergedPath = tempDir.resolve("merged.docx");
        List<String> inputPaths = Arrays.asList(docx1Path.toString(), docx2Path.toString());
        service.mergeDocx(inputPaths, mergedPath.toString());

        // Verify
        assertTrue(Files.exists(mergedPath), "Merged DOCX file should be created");
        
        // Read merged file
        try (FileInputStream fis = new FileInputStream(mergedPath.toFile());
             XWPFDocument merged = new XWPFDocument(fis)) {
            assertTrue(merged.getParagraphs().size() >= 2, "Merged document should have at least 2 paragraphs");
        }
    }

    /**
     * Test merging multiple DOCX files
     */
    @Test
    void testMergeMultipleDocxFiles() throws IOException {
        // Create three DOCX files
        Path docx1Path = tempDir.resolve("doc1.docx");
        Path docx2Path = tempDir.resolve("doc2.docx");
        Path docx3Path = tempDir.resolve("doc3.docx");

        for (int i = 1; i <= 3; i++) {
            Path docxPath = tempDir.resolve("doc" + i + ".docx");
            XWPFDocument doc = new XWPFDocument();
            doc.createParagraph().createRun().setText("Document " + i + " content");
            try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
                doc.write(out);
            }
            doc.close();
        }

        // Merge
        Path mergedPath = tempDir.resolve("merged.docx");
        List<String> inputPaths = Arrays.asList(
            docx1Path.toString(), 
            docx2Path.toString(), 
            docx3Path.toString()
        );
        service.mergeDocx(inputPaths, mergedPath.toString());

        // Verify
        assertTrue(Files.exists(mergedPath), "Merged DOCX file should be created");
        
        try (FileInputStream fis = new FileInputStream(mergedPath.toFile());
             XWPFDocument merged = new XWPFDocument(fis)) {
            assertTrue(merged.getParagraphs().size() >= 3, "Merged document should have at least 3 paragraphs");
        }
    }

    /**
     * Test merging with empty list returns without error
     */
    @Test
    void testMergeEmptyList() throws IOException {
        Path mergedPath = tempDir.resolve("merged.docx");
        
        // Should not throw exception
        assertDoesNotThrow(() -> {
            service.mergeDocx(Collections.emptyList(), mergedPath.toString());
        }, "Merging empty list should not throw exception");
        
        // File should not be created
        assertFalse(Files.exists(mergedPath), "File should not be created for empty list");
    }

    /**
     * Test merging single DOCX file
     */
    @Test
    void testMergeSingleDocxFile() throws IOException {
        // Create single DOCX file
        Path docxPath = tempDir.resolve("single.docx");
        XWPFDocument doc = new XWPFDocument();
        doc.createParagraph().createRun().setText("Single document content");
        try (FileOutputStream out = new FileOutputStream(docxPath.toFile())) {
            doc.write(out);
        }
        doc.close();

        // Merge (just copy)
        Path mergedPath = tempDir.resolve("merged.docx");
        service.mergeDocx(Collections.singletonList(docxPath.toString()), mergedPath.toString());

        // Verify
        assertTrue(Files.exists(mergedPath), "Merged DOCX file should be created");
        
        try (FileInputStream fis = new FileInputStream(mergedPath.toFile());
             XWPFDocument merged = new XWPFDocument(fis)) {
            assertFalse(merged.getParagraphs().isEmpty(), "Merged document should have content");
        }
    }

    /**
     * Test merging non-existent files throws IOException
     */
    @Test
    void testMergeNonExistentFilesThrowsException() {
        Path docx1Path = tempDir.resolve("nonexistent1.docx");
        Path docx2Path = tempDir.resolve("nonexistent2.docx");
        Path mergedPath = tempDir.resolve("merged.docx");

        List<String> inputPaths = Arrays.asList(docx1Path.toString(), docx2Path.toString());

        assertThrows(IOException.class, () -> {
            service.mergeDocx(inputPaths, mergedPath.toString());
        }, "Should throw IOException for non-existent files");
    }

    /**
     * Test merged document preserves content order
     */
    @Test
    void testMergedContentOrder() throws IOException {
        // Create DOCX files with specific content
        Path docx1Path = tempDir.resolve("first.docx");
        XWPFDocument doc1 = new XWPFDocument();
        doc1.createParagraph().createRun().setText("FIRST");
        try (FileOutputStream out = new FileOutputStream(docx1Path.toFile())) {
            doc1.write(out);
        }
        doc1.close();

        Path docx2Path = tempDir.resolve("second.docx");
        XWPFDocument doc2 = new XWPFDocument();
        doc2.createParagraph().createRun().setText("SECOND");
        try (FileOutputStream out = new FileOutputStream(docx2Path.toFile())) {
            doc2.write(out);
        }
        doc2.close();

        // Merge
        Path mergedPath = tempDir.resolve("merged.docx");
        service.mergeDocx(Arrays.asList(docx1Path.toString(), docx2Path.toString()), mergedPath.toString());

        // Verify order
        try (FileInputStream fis = new FileInputStream(mergedPath.toFile());
             XWPFDocument merged = new XWPFDocument(fis)) {
            String allText = merged.getParagraphs().stream()
                .map(p -> p.getText())
                .reduce("", (a, b) -> a + b);
            
            int firstIndex = allText.indexOf("FIRST");
            int secondIndex = allText.indexOf("SECOND");
            
            assertTrue(firstIndex >= 0, "Should contain FIRST");
            assertTrue(secondIndex >= 0, "Should contain SECOND");
            assertTrue(firstIndex < secondIndex, "FIRST should come before SECOND");
        }
    }
}
