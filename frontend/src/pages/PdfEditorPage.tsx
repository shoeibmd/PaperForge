import { useState, useEffect } from 'react';
import { Title, Text, Box, Alert } from '@mantine/core';
import { IconAlertCircle } from '@tabler/icons-react';
import { EditorToolbar, EditorMode } from '../components/editor/EditorToolbar';
import { ThumbnailSidebar } from '../components/editor/ThumbnailSidebar';
import { CanvasViewer } from '../components/editor/CanvasViewer';
import { indexedDbService } from '../services/indexedDbService';
import { pdfLibService, TextAnnotation } from '../services/pdfLibService';
import { PDFDocument } from 'pdf-lib';

const DOC_ID = 'paperforge_active_doc';

export function PdfEditorPage() {
  const [pdfBytes, setPdfBytes] = useState<ArrayBuffer | null>(null);
  const [docName, setDocName] = useState<string>('paperforge_document.pdf');
  const [activePageIndex, setActivePageIndex] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [zoom, setZoom] = useState<number>(1.0);
  const [mode, setMode] = useState<EditorMode>('view');
  const [thumbnails, setThumbnails] = useState<{ pageIndex: number; dataUrl: string }[]>([]);
  const [annotations, setAnnotations] = useState<{ [pageIndex: number]: TextAnnotation[] }>({});
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadCachedDoc = async () => {
      try {
        const cached = await indexedDbService.getDocument(DOC_ID);
        if (cached) {
          setPdfBytes(cached.data);
          setDocName(cached.name);
          const pdfDoc = await PDFDocument.load(cached.data);
          const count = pdfDoc.getPageCount();
          setTotalPages(count);
          setThumbnails(Array.from({ length: count }, (_, i) => ({ pageIndex: i, dataUrl: '' })));
        }
      } catch (err) {
        console.error('Failed to load cached PDF document:', err);
      }
    };
    loadCachedDoc();
  }, []);

  const handleFileSelect = async (file: File) => {
    try {
      setError(null);
      const buffer = await file.arrayBuffer();
      const pdfDoc = await PDFDocument.load(buffer);
      const count = pdfDoc.getPageCount();

      setPdfBytes(buffer);
      setDocName(file.name);
      setTotalPages(count);
      setActivePageIndex(0);
      setThumbnails(Array.from({ length: count }, (_, i) => ({ pageIndex: i, dataUrl: '' })));
      setAnnotations({});

      await indexedDbService.saveDocument(DOC_ID, file.name, buffer);
    } catch (err: any) {
      setError('Invalid or corrupted PDF file: ' + (err.message || 'Failed to parse document'));
    }
  };

  const updateDocumentBytes = async (newBytes: Uint8Array) => {
    const buffer = newBytes.buffer.slice(newBytes.byteOffset, newBytes.byteOffset + newBytes.byteLength);
    const pdfDoc = await PDFDocument.load(buffer);
    const count = pdfDoc.getPageCount();

    setPdfBytes(buffer);
    setTotalPages(count);
    if (activePageIndex >= count) {
      setActivePageIndex(Math.max(0, count - 1));
    }
    setThumbnails(Array.from({ length: count }, (_, i) => ({ pageIndex: i, dataUrl: '' })));
    await indexedDbService.saveDocument(DOC_ID, docName, buffer);
  };

  const handleRotatePage = async (pageIdx = activePageIndex) => {
    if (!pdfBytes) return;
    try {
      const updated = await pdfLibService.rotatePage(pdfBytes, pageIdx, 90);
      await updateDocumentBytes(updated);
    } catch (err: any) {
      setError('Failed to rotate page: ' + err.message);
    }
  };

  const handleDeletePage = async (pageIdx = activePageIndex) => {
    if (!pdfBytes || totalPages <= 1) return;
    try {
      const updated = await pdfLibService.deletePage(pdfBytes, pageIdx);
      await updateDocumentBytes(updated);
    } catch (err: any) {
      setError('Failed to delete page: ' + err.message);
    }
  };

  const handleDuplicatePage = async (pageIdx = activePageIndex) => {
    if (!pdfBytes) return;
    try {
      const updated = await pdfLibService.duplicatePage(pdfBytes, pageIdx);
      await updateDocumentBytes(updated);
    } catch (err: any) {
      setError('Failed to duplicate page: ' + err.message);
    }
  };

  const handleMovePage = async (fromIndex: number, toIndex: number) => {
    if (!pdfBytes || toIndex < 0 || toIndex >= totalPages) return;
    try {
      const newOrder = Array.from({ length: totalPages }, (_, i) => i);
      const [moved] = newOrder.splice(fromIndex, 1);
      newOrder.splice(toIndex, 0, moved);

      const updated = await pdfLibService.reorderPages(pdfBytes, newOrder);
      await updateDocumentBytes(updated);
      setActivePageIndex(toIndex);
    } catch (err: any) {
      setError('Failed to reorder pages: ' + err.message);
    }
  };

  const handleAddAnnotation = (annotation: TextAnnotation) => {
    setAnnotations((prev) => ({
      ...prev,
      [activePageIndex]: [...(prev[activePageIndex] || []), annotation],
    }));
  };

  const handleSave = async () => {
    if (!pdfBytes) return;
    try {
      let currentBytes = pdfBytes;
      for (const [pageIdxStr, pageAnns] of Object.entries(annotations)) {
        const pageIdx = parseInt(pageIdxStr, 10);
        for (const ann of pageAnns) {
          currentBytes = (await pdfLibService.addTextAnnotation(currentBytes, pageIdx, ann)).buffer;
        }
      }

      const blob = new Blob([currentBytes], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = docName.endsWith('.pdf') ? docName : `${docName}.pdf`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err: any) {
      setError('Failed to export PDF: ' + err.message);
    }
  };

  const handleThumbnailRendered = (pageIndex: number, dataUrl: string) => {
    setThumbnails((prev) => {
      const copy = [...prev];
      if (copy[pageIndex]) {
        copy[pageIndex] = { pageIndex, dataUrl };
      }
      return copy;
    });
    indexedDbService.saveThumbnail(DOC_ID, pageIndex, dataUrl);
  };

  return (
    <Box style={{ height: 'calc(100vh - 80px)', display: 'flex', flexDirection: 'column' }}>
      <Box mb="xs">
        <Title order={2}>PDF Studio Editor</Title>
        <Text c="dimmed" size="sm">
          Interactive client-side PDF document manipulation, page reordering, and vector text annotations.
        </Text>
      </Box>

      {error && (
        <Alert icon={<IconAlertCircle size={16} />} title="Error" color="red" mb="xs" onClose={() => setError(null)} withCloseButton>
          {error}
        </Alert>
      )}

      <EditorToolbar
        onFileSelect={handleFileSelect}
        zoom={zoom}
        onZoomChange={setZoom}
        mode={mode}
        onModeChange={setMode}
        onSave={handleSave}
        onRotatePage={() => handleRotatePage(activePageIndex)}
        onDuplicatePage={() => handleDuplicatePage(activePageIndex)}
        onDeletePage={() => handleDeletePage(activePageIndex)}
        hasDocument={!!pdfBytes}
        activePageIndex={activePageIndex}
        totalPages={totalPages}
      />

      <Box style={{ flex: 1, display: 'flex', overflow: 'hidden' }}>
        {pdfBytes && (
          <ThumbnailSidebar
            thumbnails={thumbnails}
            activePageIndex={activePageIndex}
            onPageSelect={setActivePageIndex}
            onMovePage={handleMovePage}
            onRotatePage={(idx) => handleRotatePage(idx)}
            onDeletePage={(idx) => handleDeletePage(idx)}
          />
        )}

        <CanvasViewer
          pdfBytes={pdfBytes}
          activePageIndex={activePageIndex}
          zoom={zoom}
          mode={mode}
          annotations={annotations[activePageIndex] || []}
          onAddAnnotation={handleAddAnnotation}
          onPageRendered={(dataUrl) => handleThumbnailRendered(activePageIndex, dataUrl)}
        />
      </Box>
    </Box>
  );
}
