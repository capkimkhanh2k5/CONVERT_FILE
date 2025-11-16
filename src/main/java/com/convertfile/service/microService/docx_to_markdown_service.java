package com.convertfile.service.microService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.stream.StreamResult;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.html.HtmlExporterNG2;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class docx_to_markdown_service {

    public void convertDocxToMarkdown(String inPath, String outMdPath) throws IOException {
        System.out.println("Start convert docx to markdown: " + inPath);

        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new File(inPath));

            // Prepare images folder next to output .md
            String outParent = new File(outMdPath).getParent();
            if (outParent == null) {
                outParent = ".";
            }
            String imageFolderPath = outParent + File.separator + "images";
            File imageDir = new File(imageFolderPath);
            if (!imageDir.exists()) {
                imageDir.mkdirs();
            }

            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setWmlPackage(wordMLPackage);

            // Export images into the created folder and make links point to "images/..."
            htmlSettings.setImageDirPath(imageFolderPath);
            htmlSettings.setImageTargetUri("images");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(baos);

            HtmlExporterNG2 htmlExporter = new HtmlExporterNG2();
            htmlExporter.html(wordMLPackage, result, htmlSettings);

            String html = baos.toString(StandardCharsets.UTF_8.name());

            // Basic clean: remove xml/xhtml doctype/header if present
            html = stripHeader(html);

            // Convert HTML -> Markdown via regex-based replacements
            String markdown = htmlToMarkdown(html);

            // Write markdown file (UTF-8)
            try (OutputStream os = new FileOutputStream(new File(outMdPath))) {
                os.write(markdown.getBytes(StandardCharsets.UTF_8));
            }

            System.out.println("End convert docx to markdown: " + outMdPath);

        } catch (Docx4JException edocx) {
            System.err.println("Docx4j Error: " + edocx.getMessage());
            edocx.printStackTrace();
            throw new IOException("Error converting docx to markdown: " + edocx.getMessage(), edocx);
        } catch (Exception ex) {
            System.err.println("Unexpected Error: " + ex.getMessage());
            ex.printStackTrace();
            throw new IOException("Error processing docx file: " + ex.getMessage(), ex);
        }
    }

    // Remove common docx4j html wrapper headers to simplify conversion
    private String stripHeader(String html) {
        // Remove <!DOCTYPE ...> and <html>...<body> wrappers leaving inner body
        int bodyStart = html.toLowerCase().indexOf("<body");
        if (bodyStart >= 0) {
            int bodyOpenEnd = html.indexOf(">", bodyStart);
            if (bodyOpenEnd >= 0) {
                int bodyClose = html.toLowerCase().indexOf("</body>", bodyOpenEnd);
                if (bodyClose >= 0) {
                    return html.substring(bodyOpenEnd + 1, bodyClose).trim();
                } else {
                    return html.substring(bodyOpenEnd + 1).trim();
                }
            }
        }
        return html;
    }

    /**
     * Simple HTML -> Markdown converter using regex replacement.
     * Processing order is important to handle nested tags correctly.
     */
    private String htmlToMarkdown(String html) {
        String md = html;

        // Step 1: Normalize line breaks
        md = md.replaceAll("(?i)<br\\s*/?>", "\n");

        // Step 2: Headings h1..h6 (process from h6 to h1 to handle nested correctly)
        for (int i = 6; i >= 1; i--) {
            String hOpen = "(?i)<h" + i + "([^>]*)>";
            String hClose = "(?i)</h" + i + ">";
            md = md.replaceAll(hOpen, "\n" + repeat("#", i) + " ");
            md = md.replaceAll(hClose, "\n\n");
        }

        // Step 3: Code blocks BEFORE inline code (to avoid conflicts)
        // <pre><code>...</code></pre> -> ```\n...\n```
        md = md.replaceAll("(?is)<pre[^>]*>\\s*<code[^>]*>(.*?)</code>\\s*</pre>", "\n```\n$1\n```\n");

        // Step 4: Images with alt text
        // <img ... src="images/foo.png" alt="bar"> -> ![bar](images/foo.png)
        md = processImages(md);

        // Step 5: Links
        // <a href="url">text</a> -> [text](url)
        md = md.replaceAll("(?i)<a[^>]*href=['\"]([^'\"]+)['\"][^>]*>(.*?)</a>", "[$2]($1)");

        // Step 6: Text formatting (process in order: bold/italic before removing tags)
        // Bold and strong
        md = md.replaceAll("(?i)<(b|strong)[^>]*>(.*?)</(b|strong)>", "**$2**");
        
        // Italic / em
        md = md.replaceAll("(?i)<(i|em)[^>]*>(.*?)</(i|em)>", "*$2*");

        // Inline code
        md = md.replaceAll("(?i)<code[^>]*>(.*?)</code>", "`$1`");

        // Step 7: Lists (process BEFORE paragraphs)
        md = processLists(md);

        // Step 8: Tables
        md = convertTables(md);

        // Step 9: Paragraphs
        md = md.replaceAll("(?i)<p[^>]*>", "");
        md = md.replaceAll("(?i)</p>", "\n\n");

        // Step 10: Remove any remaining HTML tags
        md = md.replaceAll("<[^>]+>", "");

        // Step 11: Unescape HTML entities
        md = unescapeHtmlEntities(md);

        // Step 12: Clean up whitespace
        md = cleanupWhitespace(md);

        return md.trim() + "\n";
    }

    /**
     * Process images with proper alt text extraction
     */
    private String processImages(String html) {
        Pattern imgPattern = Pattern.compile("(?i)<img[^>]*src=['\"]?([^'\"\\s>]+)['\"]?[^>]*>");
        Matcher imgMatcher = imgPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        
        while (imgMatcher.find()) {
            String src = imgMatcher.group(1);
            String tag = imgMatcher.group(0);
            
            // Extract alt attribute
            String alt = "";
            Pattern altPattern = Pattern.compile("(?i)alt=['\"]([^'\"]*)['\"]");
            Matcher altM = altPattern.matcher(tag);
            if (altM.find()) {
                alt = altM.group(1);
            } else {
                alt = "image"; // fallback
            }
            
            String replacement = "![" + escapeMarkdownInline(alt) + "](" + src + ")";
            imgMatcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        imgMatcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Process both ordered and unordered lists correctly
     */
    private String processLists(String html) {
        String result = html;
        
        // Process ordered lists first (with numbers)
        Pattern olPattern = Pattern.compile("(?is)<ol[^>]*>(.*?)</ol>");
        Matcher olMatcher = olPattern.matcher(result);
        StringBuffer sbOl = new StringBuffer();
        
        while (olMatcher.find()) {
            String listContent = olMatcher.group(1);
            StringBuilder listMd = new StringBuilder("\n");
            
            // Extract list items
            Pattern liPattern = Pattern.compile("(?is)<li[^>]*>(.*?)</li>");
            Matcher liMatcher = liPattern.matcher(listContent);
            int itemNum = 1;
            
            while (liMatcher.find()) {
                String itemText = liMatcher.group(1).replaceAll("<[^>]+>", "").trim();
                listMd.append(itemNum++).append(". ").append(itemText).append("\n");
            }
            
            olMatcher.appendReplacement(sbOl, Matcher.quoteReplacement(listMd.toString()));
        }
        olMatcher.appendTail(sbOl);
        result = sbOl.toString();
        
        // Process unordered lists (with dashes)
        Pattern ulPattern = Pattern.compile("(?is)<ul[^>]*>(.*?)</ul>");
        Matcher ulMatcher = ulPattern.matcher(result);
        StringBuffer sbUl = new StringBuffer();
        
        while (ulMatcher.find()) {
            String listContent = ulMatcher.group(1);
            StringBuilder listMd = new StringBuilder("\n");
            
            // Extract list items
            Pattern liPattern = Pattern.compile("(?is)<li[^>]*>(.*?)</li>");
            Matcher liMatcher = liPattern.matcher(listContent);
            
            while (liMatcher.find()) {
                String itemText = liMatcher.group(1).replaceAll("<[^>]+>", "").trim();
                listMd.append("- ").append(itemText).append("\n");
            }
            
            ulMatcher.appendReplacement(sbUl, Matcher.quoteReplacement(listMd.toString()));
        }
        ulMatcher.appendTail(sbUl);
        result = sbUl.toString();
        
        return result;
    }

    /**
     * Convert HTML tables to Markdown tables
     */
    private String convertTables(String html) {
        Pattern tablePattern = Pattern.compile("(?is)<table[^>]*>(.*?)</table>");
        Matcher tableMatcher = tablePattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        
        while (tableMatcher.find()) {
            String tableHtml = tableMatcher.group(1);

            // Find rows
            Pattern rowPattern = Pattern.compile("(?is)<tr[^>]*>(.*?)</tr>");
            Matcher rowMatcher = rowPattern.matcher(tableHtml);

            List<List<String>> rows = new ArrayList<>();
            boolean hasHeader = false;
            
            while (rowMatcher.find()) {
                String rowHtml = rowMatcher.group(1);
                List<String> cells = new ArrayList<>();

                // Cells can be th or td
                Pattern cellPattern = Pattern.compile("(?is)<(th|td)[^>]*>(.*?)</(th|td)>");
                Matcher cellMatcher = cellPattern.matcher(rowHtml);
                boolean headerRow = false;
                
                while (cellMatcher.find()) {
                    String cellTag = cellMatcher.group(1);
                    String cellText = cellMatcher.group(2).replaceAll("<[^>]+>", "").trim();
                    cells.add(cellText);
                    
                    if (cellTag.equalsIgnoreCase("th")) {
                        headerRow = true;
                    }
                }
                
                if (headerRow) hasHeader = true;
                if (!cells.isEmpty()) rows.add(cells);
            }

            StringBuilder tableMd = new StringBuilder("\n");
            
            if (!rows.isEmpty()) {
                // Determine column count from first row
                int colCount = rows.get(0).size();
                
                // Header row
                List<String> header = rows.get(0);
                for (int i = 0; i < header.size(); i++) {
                    tableMd.append("| ").append(escapeTableCell(header.get(i))).append(" ");
                }
                tableMd.append("|\n");

                // Separator row
                for (int i = 0; i < colCount; i++) {
                    tableMd.append("|---");
                }
                tableMd.append("|\n");

                // Data rows (skip first row if it was a header)
                int start = hasHeader ? 1 : 0;
                for (int r = start; r < rows.size(); r++) {
                    List<String> row = rows.get(r);
                    for (int c = 0; c < colCount; c++) {
                        String cell = c < row.size() ? row.get(c) : "";
                        tableMd.append("| ").append(escapeTableCell(cell)).append(" ");
                    }
                    tableMd.append("|\n");
                }
            }

            if (tableMd.length() > 1) {
                tableMatcher.appendReplacement(sb, Matcher.quoteReplacement(tableMd.toString() + "\n"));
            } else {
                // Fallback: remove table tags
                tableMatcher.appendReplacement(sb, "");
            }
        }
        tableMatcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * Unescape common HTML entities
     */
    private String unescapeHtmlEntities(String text) {
        return text
            .replaceAll("&nbsp;", " ")
            .replaceAll("&lt;", "<")
            .replaceAll("&gt;", ">")
            .replaceAll("&amp;", "&")
            .replaceAll("&quot;", "\"")
            .replaceAll("&#39;", "'")
            .replaceAll("&#x27;", "'")
            .replaceAll("&apos;", "'");
    }

    /**
     * Clean up excessive whitespace
     */
    private String cleanupWhitespace(String text) {
        return text
            .replaceAll("(?m)^[ \\t]+", "")        // Remove leading spaces on each line
            .replaceAll("[ \\t]+$", "")             // Remove trailing spaces
            .replaceAll("\\n{3,}", "\n\n")          // Max 2 consecutive newlines
            .replaceAll("(?m)^\\n+", "\n");         // Remove leading newlines
    }

    /**
     * Helper: Repeat string n times
     */
    private static String repeat(String s, int times) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < times; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Escape markdown special characters for table cells
     */
    private static String escapeTableCell(String text) {
        if (text == null) return "";
        return text.replace("|", "\\|").trim();
    }

    /**
     * Escape markdown special characters for inline text (links, alt text)
     */
    private static String escapeMarkdownInline(String text) {
        if (text == null) return "";
        return text
            .replace("[", "\\[")
            .replace("]", "\\]")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .trim();
    }
}