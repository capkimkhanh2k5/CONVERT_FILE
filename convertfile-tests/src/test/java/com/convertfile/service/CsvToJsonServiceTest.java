package com.convertfile.service;

import com.convertfile.service.ConvertService.csv_to_json_service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CsvToJsonServiceTest {

    @TempDir
    Path tempDir;
    
    private csv_to_json_service service;
    
    @BeforeEach
    void setUp() {
        service = new csv_to_json_service();
    }

    @Test
    void testConvertCsvToJson_ValidInput() throws Exception {
        // Arrange
        Path csvFile = tempDir.resolve("test.csv");
        Files.writeString(csvFile, "Name,Age,City\nJohn,30,NYC\nJane,25,LA");

        Path jsonOutput = tempDir.resolve("output.json");

        // Act
        service.convertCsvToJson(csvFile.toString(), jsonOutput.toString());
        
        // Assert
        assertTrue(Files.exists(jsonOutput), "JSON output file should exist");
        String jsonContent = Files.readString(jsonOutput);
        
        assertFalse(jsonContent.isEmpty(), "JSON content should not be empty");
        assertTrue(jsonContent.contains("Name"), "JSON should contain Name field");
        assertTrue(jsonContent.contains("Age"), "JSON should contain Age field");
        assertTrue(jsonContent.contains("City"), "JSON should contain City field");
        assertTrue(jsonContent.contains("John"), "JSON should contain John");
        assertTrue(jsonContent.contains("30"), "JSON should contain age 30");
        assertTrue(jsonContent.contains("NYC"), "JSON should contain NYC");
        assertTrue(jsonContent.contains("Jane"), "JSON should contain Jane");
        assertTrue(jsonContent.contains("25"), "JSON should contain age 25");
        assertTrue(jsonContent.contains("LA"), "JSON should contain LA");
    }

    @Test
    void testConvertCsvToJson_EmptyFile() throws Exception {
        // Arrange
        Path csvFile = tempDir.resolve("empty.csv");
        Files.writeString(csvFile, "Name,Age,City\n");
        
        Path jsonOutput = tempDir.resolve("output_empty.json");

        // Act
        service.convertCsvToJson(csvFile.toString(), jsonOutput.toString());
        
        // Assert
        assertTrue(Files.exists(jsonOutput), "JSON output file should exist even for empty CSV");
        String jsonContent = Files.readString(jsonOutput);
        // Should be an empty array or minimal JSON structure
        assertTrue(jsonContent.contains("[") && jsonContent.contains("]"), "Should contain array brackets");
    }

    @Test
    void testConvertCsvToJson_InvalidFormat() {
        // Arrange
        Path csvFile = tempDir.resolve("invalid.csv");
        Path jsonOutput = tempDir.resolve("output_invalid.json");
        
        // Act & Assert
        // CSV with inconsistent columns - this should throw an exception
        assertThrows(Exception.class, () -> {
            Files.writeString(csvFile, "Name,Age\nJohn,30,ExtraColumn\nJane");
            service.convertCsvToJson(csvFile.toString(), jsonOutput.toString());
        }, "Invalid CSV format should throw an exception");
    }
}
