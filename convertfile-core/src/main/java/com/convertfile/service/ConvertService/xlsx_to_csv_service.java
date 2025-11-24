package com.convertfile.service.ConvertService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

/**
 * Service for converting Excel (XLSX) files to CSV format
 */
public class xlsx_to_csv_service {
    
    /**
     * Convert XLSX file to CSV format
     * 
     * @param xlsxPath Path to input XLSX file
     * @param csvPath Path to output CSV file
     * @throws IOException If conversion fails
     */
    public void convertXlsxToCsv(String xlsxPath, String csvPath) throws IOException {
        if (xlsxPath == null || xlsxPath.isEmpty()) {
            throw new IllegalArgumentException("Input path cannot be null or empty");
        }
        if (csvPath == null || csvPath.isEmpty()) {
            throw new IllegalArgumentException("Output path cannot be null or empty");
        }
        
        File inputFile = new File(xlsxPath);
        if (!inputFile.exists()) {
            throw new FileNotFoundException("Input file not found: " + xlsxPath);
        }
        
        Workbook workbook = null;
        PrintWriter writer = null;
        
        try {
            workbook = new XSSFWorkbook(new FileInputStream(xlsxPath));
            Sheet sheet = workbook.getSheetAt(0);
            writer = new PrintWriter(new File(csvPath));

            for (Row row : sheet) {
                for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                    Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    writer.print(cell.toString());
                    if (cn < row.getLastCellNum() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
            
            System.out.println("Excel converted to CSV: " + csvPath);
            
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (workbook != null) {
                workbook.close();
            }
        }
    }
}
