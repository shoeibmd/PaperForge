import { PDFDocument, degrees, rgb, StandardFonts } from 'pdf-lib';

export interface TextAnnotation {
  text: string;
  x: number;
  y: number;
  size?: number;
  color?: { r: number; g: number; b: number };
}

export const pdfLibService = {
  rotatePage: async (pdfBytes: ArrayBuffer, pageIndex: number, rotationAngle: number): Promise<Uint8Array> => {
    const pdfDoc = await PDFDocument.load(pdfBytes);
    const page = pdfDoc.getPage(pageIndex);
    const currentRotation = page.getRotation().angle;
    page.setRotation(degrees((currentRotation + rotationAngle) % 360));
    return pdfDoc.save();
  },

  deletePage: async (pdfBytes: ArrayBuffer, pageIndex: number): Promise<Uint8Array> => {
    const pdfDoc = await PDFDocument.load(pdfBytes);
    pdfDoc.removePage(pageIndex);
    return pdfDoc.save();
  },

  duplicatePage: async (pdfBytes: ArrayBuffer, pageIndex: number): Promise<Uint8Array> => {
    const pdfDoc = await PDFDocument.load(pdfBytes);
    const [copiedPage] = await pdfDoc.copyPages(pdfDoc, [pageIndex]);
    pdfDoc.insertPage(pageIndex + 1, copiedPage);
    return pdfDoc.save();
  },

  reorderPages: async (pdfBytes: ArrayBuffer, newOrder: number[]): Promise<Uint8Array> => {
    const srcDoc = await PDFDocument.load(pdfBytes);
    const newDoc = await PDFDocument.create();
    const copiedPages = await newDoc.copyPages(srcDoc, newOrder);
    copiedPages.forEach((page) => newDoc.addPage(page));
    return newDoc.save();
  },

  extractPage: async (pdfBytes: ArrayBuffer, pageIndex: number): Promise<Uint8Array> => {
    const srcDoc = await PDFDocument.load(pdfBytes);
    const newDoc = await PDFDocument.create();
    const [copiedPage] = await newDoc.copyPages(srcDoc, [pageIndex]);
    newDoc.addPage(copiedPage);
    return newDoc.save();
  },

  addTextAnnotation: async (pdfBytes: ArrayBuffer, pageIndex: number, annotation: TextAnnotation): Promise<Uint8Array> => {
    const pdfDoc = await PDFDocument.load(pdfBytes);
    const page = pdfDoc.getPage(pageIndex);
    const font = await pdfDoc.embedFont(StandardFonts.Helvetica);
    const color = annotation.color
      ? rgb(annotation.color.r, annotation.color.g, annotation.color.b)
      : rgb(0, 0, 0);

    page.drawText(annotation.text, {
      x: annotation.x,
      y: page.getHeight() - annotation.y, // Flip Y axis for PDF coordinate space
      size: annotation.size || 14,
      font,
      color,
    });

    return pdfDoc.save();
  },
};
