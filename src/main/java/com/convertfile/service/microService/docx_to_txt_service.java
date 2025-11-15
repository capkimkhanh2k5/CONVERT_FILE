package com.convertfile.service.microService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.ContentAccessor;
import org.docx4j.wml.P;
import org.docx4j.wml.R;
import org.docx4j.wml.Text;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;
import org.docx4j.wml.Br;

import javax.xml.bind.JAXBElement;

public class docx_to_txt_service {

    /**
     * Convert DOCX to plain text file.
     *
     * @param inPath     input .docx path
     * @param outTxtPath output .txt file path
     * @throws IOException on failure
     */
    public void convertDocxToTxt(String inPath, String outTxtPath) throws IOException {
        System.out.println("Start convert docx to txt: " + inPath);

        try {
            WordprocessingMLPackage pkg = WordprocessingMLPackage.load(new File(inPath));
            
            StringBuilder sb = new StringBuilder();
            
            // Process main document content
            List<Object> content = pkg.getMainDocumentPart().getContent();
            processContent(content, sb);
            
            // Clean up and write output
            String text = cleanupText(sb.toString());
            Files.write(Paths.get(outTxtPath), text.getBytes(StandardCharsets.UTF_8));
            
            System.out.println("End convert docx to txt: " + outTxtPath);
            
        } catch (Docx4JException e) {
            System.err.println("Docx4j Error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error converting docx to txt: " + e.getMessage(), e);
        } catch (Exception ex) {
            System.err.println("Unexpected Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new IOException("Error processing docx file: " + ex.getMessage(), ex);
        }
    }

    /**
     * Process document content recursively
     */
    private void processContent(List<Object> content, StringBuilder sb) {
        for (Object obj : content) {
            // Unwrap JAXBElement if needed
            Object element = obj;
            if (obj instanceof JAXBElement) {
                element = ((JAXBElement<?>) obj).getValue();
            }
            
            if (element instanceof P) {
                // Process paragraph
                processParagraph((P) element, sb);
                sb.append("\n");
                
            } else if (element instanceof Tbl) {
                // Process table
                processTable((Tbl) element, sb);
                sb.append("\n");
                
            } else if (element instanceof ContentAccessor) {
                // Recursively process nested content
                ContentAccessor ca = (ContentAccessor) element;
                processContent(ca.getContent(), sb);
            }
        }
    }

    /**
     * Process a single paragraph
     */
    private void processParagraph(P paragraph, StringBuilder sb) {
        List<Object> content = paragraph.getContent();
        
        for (Object obj : content) {
            Object element = obj;
            if (obj instanceof JAXBElement) {
                element = ((JAXBElement<?>) obj).getValue();
            }
            
            if (element instanceof R) {
                // Process run (text container)
                processRun((R) element, sb);
                
            } else if (element instanceof ContentAccessor) {
                // Process nested content
                ContentAccessor ca = (ContentAccessor) element;
                for (Object child : ca.getContent()) {
                    if (child instanceof JAXBElement) {
                        Object childValue = ((JAXBElement<?>) child).getValue();
                        if (childValue instanceof R) {
                            processRun((R) childValue, sb);
                        }
                    }
                }
            }
        }
    }

    /**
     * Process a text run
     */
    private void processRun(R run, StringBuilder sb) {
        List<Object> runContent = run.getContent();
        
        for (Object obj : runContent) {
            Object element = obj;
            if (obj instanceof JAXBElement) {
                element = ((JAXBElement<?>) obj).getValue();
            }
            
            if (element instanceof Text) {
                // Extract text content
                Text text = (Text) element;
                sb.append(text.getValue());
                
            } else if (element instanceof Br) {
                // Line break within paragraph
                sb.append("\n");
                
            } else if (element instanceof org.docx4j.wml.R.Tab) {
                // Tab character
                sb.append("\t");
            }
        }
    }

    /**
     * Process table and convert to readable text format
     */
    private void processTable(Tbl table, StringBuilder sb) {
        sb.append("\n"); // Add spacing before table
        
        List<Object> rows = table.getContent();
        for (Object rowObj : rows) {
            Object rowElement = rowObj;
            if (rowObj instanceof JAXBElement) {
                rowElement = ((JAXBElement<?>) rowObj).getValue();
            }
            
            if (rowElement instanceof Tr) {
                processTableRow((Tr) rowElement, sb);
            }
        }
    }

    /**
     * Process table row
     */
    private void processTableRow(Tr row, StringBuilder sb) {
        List<Object> cells = row.getContent();
        List<String> cellTexts = new ArrayList<>();
        
        // Extract text from each cell
        for (Object cellObj : cells) {
            Object cellElement = cellObj;
            if (cellObj instanceof JAXBElement) {
                cellElement = ((JAXBElement<?>) cellObj).getValue();
            }
            
            if (cellElement instanceof Tc) {
                Tc cell = (Tc) cellElement;
                StringBuilder cellSb = new StringBuilder();
                processContent(cell.getContent(), cellSb);
                String cellText = cellSb.toString().trim().replaceAll("\\n+", " ");
                cellTexts.add(cellText);
            }
        }
        
        // Format row as: | Cell1 | Cell2 | Cell3 |
        if (!cellTexts.isEmpty()) {
            sb.append("| ");
            sb.append(String.join(" | ", cellTexts));
            sb.append(" |\n");
        }
    }

    /**
     * Clean up extracted text
     */
    private String cleanupText(String text) {
        return text
            // Normalize line breaks
            .replaceAll("\\r\\n", "\n")
            .replaceAll("\\r", "\n")
            // Replace non-breaking spaces
            .replaceAll("\\u00A0", " ")
            // Remove excessive spaces
            .replaceAll(" +", " ")
            // Remove spaces at line start/end
            .replaceAll("(?m)^[ \\t]+", "")
            .replaceAll("(?m)[ \\t]+$", "")
            // Limit consecutive blank lines to 2
            .replaceAll("\\n{3,}", "\n\n")
            // Ensure file ends with newline
            .replaceAll("\\n*$", "\n");
    }
}