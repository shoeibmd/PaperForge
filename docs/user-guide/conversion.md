# Document Conversion Guide

PaperForge handles multi-format conversions using headless LibreOffice processes and pure-Java fallbacks.

---

## 🔄 Supported Conversion Matrix

| Source Format | Target Format | Engine |
| :--- | :--- | :--- |
| Word (DOCX/DOC) | PDF | LibreOffice / PDFBox |
| Excel (XLSX/XLS) | PDF | LibreOffice / PDFBox |
| PowerPoint (PPTX) | PDF | LibreOffice / PDFBox |
| Text (TXT) | PDF | LibreOffice / PDFBox |
| PDF | TXT / DOCX | LibreOffice / PDFBox |
| PDF | PNG / JPEG / WEBP / TIFF | PDFBox + TwelveMonkeys |

---

## 🛠️ Usage Instructions

1. Select **Tools** -> **Convert to PDF** or **Convert from PDF**.
2. Upload your file.
3. Select target output format.
4. Download the converted output file.
