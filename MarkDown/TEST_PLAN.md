# 🧪 Test Conversion Services

## 📝 Danh sách Conversion Services đã implement:

### ✅ Document Conversions
1. **DOCX_TO_PDF** - Word → PDF
2. **PDF_TO_DOCX** - PDF → Word  
3. **DOCX_TO_XML** - Word → XML
4. **XML_TO_DOCX** - XML → Word
5. **DOCX_TO_HTML** - Word → HTML
6. **DOCX_TO_TXT** - Word → Text
7. **DOCX_TO_MARKDOWN** - Word → Markdown

### ✅ Data Conversions
8. **CSV_TO_JSON** - CSV → JSON
9. **XLSX_TO_CSV** - Excel → CSV

### ✅ Image Conversions
10. **IMAGE_TO_PDF** - Image → PDF
11. **PDF_TO_IMAGE** - PDF → Image(s)

---

## 🧪 Test Plan

### Test 1: DOCX_TO_PDF (Đã test - OK)
- Upload: `test.docx`
- Chọn: `Word → PDF`
- Kết quả: File `.pdf`

### Test 2: CSV_TO_JSON (Đã test - OK)
- Upload: `train.csv`
- Chọn: `CSV → JSON`
- Kết quả: File `.json` (không phải `.null`)
- Progress: 5% → 10% → 15% → ... → 100%

### Test 3: DOCX_TO_XML
**File test:** Tạo file `sample.docx` với nội dung đơn giản
```
Title: Test Document
Content: This is a test document for XML conversion.
```

**Steps:**
1. Upload `sample.docx`
2. Chọn: `Word → XML`
3. Convert
4. Download và mở file `.xml`
5. Verify: XML structure với content từ Word

### Test 4: XML_TO_DOCX
**File test:** Tạo file `test.xml`
```xml
<?xml version="1.0" encoding="UTF-8"?>
<document>
    <title>Test XML Document</title>
    <content>This is test content from XML.</content>
</document>
```

**Steps:**
1. Upload `test.xml`
2. Chọn: `XML → Word`
3. Convert
4. Download `.docx` và mở trong Word

### Test 5: DOCX_TO_HTML
**File test:** `sample.docx` (có formatting: bold, italic, headings)

**Steps:**
1. Upload `sample.docx`
2. Chọn: `Word → HTML`
3. Convert
4. Download `.html` và mở trong browser
5. Verify: Formatting được giữ nguyên

### Test 6: DOCX_TO_TXT
**File test:** `formatted.docx` (có nhiều formatting)

**Steps:**
1. Upload `formatted.docx`
2. Chọn: `Word → TXT`
3. Convert
4. Download `.txt`
5. Verify: Plain text, không có formatting

### Test 7: DOCX_TO_MARKDOWN
**File test:** `document.docx` (có headings, lists, bold, italic)

**Steps:**
1. Upload `document.docx`
2. Chọn: `Word → Markdown`
3. Convert
4. Download `.md`
5. Verify: Markdown syntax đúng (# heading, **bold**, *italic*)

### Test 8: XLSX_TO_CSV
**File test:** Tạo file Excel `data.xlsx`
```
| Name  | Age | City    |
|-------|-----|---------|
| John  | 25  | Hanoi   |
| Alice | 30  | Saigon  |
```

**Steps:**
1. Upload `data.xlsx`
2. Chọn: `Excel → CSV`
3. Convert
4. Download `.csv`
5. Mở bằng text editor, verify format CSV

### Test 9: IMAGE_TO_PDF
**File test:** `photo.jpg` hoặc `image.png`

**Steps:**
1. Upload image file
2. Chọn: `Image → PDF`
3. Convert
4. Download `.pdf`
5. Mở PDF, verify ảnh hiển thị đúng

### Test 10: PDF_TO_IMAGE
**File test:** `document.pdf` (có nhiều trang)

**Steps:**
1. Upload `document.pdf`
2. Chọn: `PDF → Image`
3. Convert
4. Download result
5. Verify: Tất cả trang được convert thành images

---

## ✅ Test Checklist

| Service | File Test | Status | Notes |
|---------|-----------|--------|-------|
| DOCX_TO_PDF | test.docx | ✅ | OK |
| PDF_TO_DOCX | test.pdf | ⏳ | |
| CSV_TO_JSON | train.csv | ✅ | Fixed .null extension |
| DOCX_TO_XML | sample.docx | ⏳ | |
| XML_TO_DOCX | test.xml | ⏳ | |
| DOCX_TO_HTML | formatted.docx | ⏳ | |
| DOCX_TO_TXT | document.docx | ⏳ | |
| DOCX_TO_MARKDOWN | rich.docx | ⏳ | |
| XLSX_TO_CSV | data.xlsx | ⏳ | |
| IMAGE_TO_PDF | photo.jpg | ⏳ | |
| PDF_TO_IMAGE | multi-page.pdf | ⏳ | |

---

## 🚀 Deploy & Test

1. **Deploy:**
   ```cmd
   deploy_complete.bat
   ```

2. **Access:**
   http://localhost:8080/CONVERT_FILE/

3. **Test Features:**
   - ✅ Individual file convert button
   - ✅ Convert All Files button  
   - ✅ Realistic progress bar (5% → 100%)
   - ✅ Recent Conversions scrolling with page
   - ✅ Download with correct file extension

---

## 📊 Progress Bar Stages

0% → 5% → 10% → 15% → 20% → 25% → 30% → 35% → 40% → 45% → 55% → 65% → 70% → 75% → 85% → 90% → 95% → 100%

Each stage represents a real step in conversion process.

---

**Last Updated:** November 23, 2025
