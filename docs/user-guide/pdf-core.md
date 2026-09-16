# PDF Core Operations Guide

PaperForge provides high-performance PDF manipulation tools powered by Apache PDFBox.

---

## 🛠️ Operations Overview

| Operation | Description | Endpoints / Tool ID |
| :--- | :--- | :--- |
| **Merge** | Combine multiple PDF documents into a single file | `/api/v1/pdf/core/merge` |
| **Split** | Split a PDF into individual pages or page intervals | `/api/v1/pdf/core/split` |
| **Rotate** | Rotate selected pages by 90, 180, or 270 degrees | `/api/v1/pdf/core/rotate` |
| **Delete Pages** | Remove specific page numbers from a document | `/api/v1/pdf/core/delete-pages` |
| **Extract Pages** | Extract specific page ranges into a new document | `/api/v1/pdf/core/extract-pages` |
| **Reorder Pages** | Rearrange page sequence in a document | `/api/v1/pdf/core/reorder-pages` |
| **Crop** | Crop document margins and page dimensions | `/api/v1/pdf/core/crop` |

---

## 📖 Step-by-Step Instructions (Merge PDFs)

1. Navigate to **Tools** in the sidebar.
2. Select **Merge PDFs**.
3. Upload 2 or more PDF files using the dropzone.
4. Drag files to reorder document sequence.
5. Click **Process & Download**.

---

## ❓ Troubleshooting

- **Large PDF Timeout**: For documents over 500 pages, use background **Job Processing** (`/api/v1/jobs`) or the **Pipeline Builder**.
