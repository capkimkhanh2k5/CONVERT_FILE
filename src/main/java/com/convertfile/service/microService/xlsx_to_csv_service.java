package com.convertfile.service.microService;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.*;

public class xlsx_to_csv_service {
    public void convertXlsxToCsv(String xlsxPath, String csvPath) throws IOException {
        Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsxPath));
        Sheet sheet = workbook.getSheetAt(0);
        PrintWriter writer = new PrintWriter(new File(csvPath));

        for (Row row : sheet) {
            for (int cn = 0; cn < row.getLastCellNum(); cn++) {
                Cell cell = row.getCell(cn, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                writer.print(cell.toString());
                if (cn < row.getLastCellNum() - 1) writer.print(",");
            }
            writer.println();
        }

        writer.close();
        workbook.close();
        System.out.println("Excel converted to CSV.");
    }
}

