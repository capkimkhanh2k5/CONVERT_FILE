package com.convertfile.service;

import com.convertfile.service.ConvertService.xlsx_to_csv_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for xlsx_to_csv_service
 * Tests Excel to CSV conversion functionality
 */
public class XlsxToCsvServiceTest {

    @TempDir
    Path tempDir;

    /**
     * Test basic XLSX to CSV conversion with valid data
     */
    @Test
    public void testValidXlsxToCsv() throws Exception {
        // Create test XLSX file
        Path xlsxFile = tempDir.resolve("test.xlsx");
        createTestXlsx(xlsxFile, new String[][] {
            {"Name", "Age", "City"},
            {"John", "25", "New York"},
            {"Jane", "30", "London"}
        });
        
        // Convert to CSV
        Path csvFile = tempDir.resolve("output.csv");
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        service.convertXlsxToCsv(xlsxFile.toString(), csvFile.toString());
        
        // Verify CSV file exists
        assertTrue(Files.exists(csvFile), "CSV file should be created");
        
        // Verify CSV content
        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(3, lines.size(), "Should have 3 rows");
        assertEquals("Name,Age,City", lines.get(0));
        assertEquals("John,25,New York", lines.get(1));
        assertEquals("Jane,30,London", lines.get(2));
    }

    /**
     * Test XLSX with numeric values
     */
    @Test
    public void testXlsxWithNumbers() throws Exception {
        Path xlsxFile = tempDir.resolve("numbers.xlsx");
        createTestXlsxWithNumbers(xlsxFile);
        
        Path csvFile = tempDir.resolve("numbers.csv");
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        service.convertXlsxToCsv(xlsxFile.toString(), csvFile.toString());
        
        assertTrue(Files.exists(csvFile), "CSV file should be created");
        
        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(2, lines.size(), "Should have 2 rows");
        assertTrue(lines.get(1).contains("123"), "Should contain numeric value");
        assertTrue(lines.get(1).contains("456"), "Should contain numeric value");
    }

    /**
     * Test empty XLSX file
     */
    @Test
    public void testEmptyXlsx() throws Exception {
        Path xlsxFile = tempDir.resolve("empty.xlsx");
        createEmptyXlsx(xlsxFile);
        
        Path csvFile = tempDir.resolve("empty.csv");
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        service.convertXlsxToCsv(xlsxFile.toString(), csvFile.toString());
        
        assertTrue(Files.exists(csvFile), "CSV file should be created");
        
        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(0, lines.size(), "Empty XLSX should produce empty CSV");
    }

    /**
     * Test file not found error
     */
    @Test
    public void testFileNotFound() {
        Path xlsxFile = tempDir.resolve("nonexistent.xlsx");
        Path csvFile = tempDir.resolve("output.csv");
        
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        
        assertThrows(IOException.class, () -> {
            service.convertXlsxToCsv(xlsxFile.toString(), csvFile.toString());
        }, "Should throw exception for missing file");
    }

    /**
     * Test null/empty path validation
     */
    @Test
    public void testNullPaths() {
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertXlsxToCsv(null, "output.csv");
        }, "Should throw exception for null input path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertXlsxToCsv("input.xlsx", null);
        }, "Should throw exception for null output path");
        
        assertThrows(IllegalArgumentException.class, () -> {
            service.convertXlsxToCsv("", "output.csv");
        }, "Should throw exception for empty input path");
    }

    /**
     * Test XLSX with special characters
     */
    @Test
    public void testSpecialCharacters() throws Exception {
        Path xlsxFile = tempDir.resolve("special.xlsx");
        createTestXlsx(xlsxFile, new String[][] {
            {"Name", "Description"},
            {"Test", "Hello, World!"},
            {"Data", "Line1\nLine2"}
        });
        
        Path csvFile = tempDir.resolve("special.csv");
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        service.convertXlsxToCsv(xlsxFile.toString(), csvFile.toString());
        
        assertTrue(Files.exists(csvFile), "CSV file should be created");
        
        List<String> lines = Files.readAllLines(csvFile);
        assertTrue(lines.size() >= 2, "Should have at least 2 rows");
    }

    /**
     * Test XLSX with multiple columns
     */
    @Test
    public void testMultipleColumns() throws Exception {
        Path xlsxFile = tempDir.resolve("multi.xlsx");
        createTestXlsx(xlsxFile, new String[][] {
            {"Col1", "Col2", "Col3", "Col4", "Col5"},
            {"A", "B", "C", "D", "E"},
            {"1", "2", "3", "4", "5"}
        });
        
        Path csvFile = tempDir.resolve("multi.csv");
        xlsx_to_csv_service service = new xlsx_to_csv_service();
        service.convertXlsxToCsv(xlsxFile.toString(), csvFile.toString());
        
        List<String> lines = Files.readAllLines(csvFile);
        assertEquals(3, lines.size(), "Should have 3 rows");
        
        // Verify comma count (should be 4 commas for 5 columns)
        long commaCount = lines.get(0).chars().filter(ch -> ch == ',').count();
        assertEquals(4, commaCount, "Should have 4 commas for 5 columns");
    }

    // ===== Helper Methods =====

    /**
     * Create test XLSX file with given data
     */
    private void createTestXlsx(Path path, String[][] data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(data[i][j]);
                }
            }
            
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Create test XLSX with numeric values
     */
    private void createTestXlsxWithNumbers(Path path) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sheet1");
            
            Row row1 = sheet.createRow(0);
            row1.createCell(0).setCellValue("ID");
            row1.createCell(1).setCellValue("Value");
            
            Row row2 = sheet.createRow(1);
            row2.createCell(0).setCellValue(123.0);
            row2.createCell(1).setCellValue(456.789);
            
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                workbook.write(fos);
            }
        }
    }

    /**
     * Create empty XLSX file
     */
    private void createEmptyXlsx(Path path) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            workbook.createSheet("Sheet1");
            
            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                workbook.write(fos);
            }
        }
    }
}
