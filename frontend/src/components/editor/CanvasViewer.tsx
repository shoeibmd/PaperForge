import React, { useEffect, useRef } from 'react';
import * as pdfjsLib from 'pdfjs-dist';
import { EditorMode } from './EditorToolbar';
import { TextAnnotation } from '../../services/pdfLibService';

// Configure pdfjs worker
pdfjsLib.GlobalWorkerOptions.workerSrc = `https://cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjsLib.version}/pdf.worker.min.mjs`;

interface CanvasViewerProps {
  pdfBytes: ArrayBuffer | null;
  activePageIndex: number;
  zoom: number;
  mode: EditorMode;
  annotations: TextAnnotation[];
  onAddAnnotation: (annotation: TextAnnotation) => void;
  onPageRendered?: (dataUrl: string) => void;
}

export const CanvasViewer: React.FC<CanvasViewerProps> = ({
  pdfBytes,
  activePageIndex,
  zoom,
  mode,
  annotations,
  onAddAnnotation,
  onPageRendered,
}) => {
  const canvasRef = useRef<HTMLCanvasElement | null>(null);
  const containerRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!pdfBytes) return;

    let isCancelled = false;

    const renderPage = async () => {
      try {
        const loadingTask = pdfjsLib.getDocument({ data: new Uint8Array(pdfBytes) });
        const pdf = await loadingTask.promise;

        if (isCancelled || activePageIndex >= pdf.numPages) return;

        const page = await pdf.getPage(activePageIndex + 1);
        const viewport = page.getViewport({ scale: zoom });

        const canvas = canvasRef.current;
        if (!canvas) return;

        const context = canvas.getContext('2d');
        if (!context) return;

        canvas.height = viewport.height;
        canvas.width = viewport.width;

        const renderContext = {
          canvasContext: context,
          viewport: viewport,
          canvas: canvas,
        };

        await page.render(renderContext).promise;

        if (!isCancelled && onPageRendered) {
          onPageRendered(canvas.toDataURL());
        }
      } catch (err) {
        console.error('Error rendering PDF page on canvas:', err);
      }
    };

    renderPage();

    return () => {
      isCancelled = true;
    };
  }, [pdfBytes, activePageIndex, zoom]);

  const handleCanvasClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (mode === 'text' && canvasRef.current) {
      const rect = canvasRef.current.getBoundingClientRect();
      const x = (e.clientX - rect.left) / zoom;
      const y = (e.clientY - rect.top) / zoom;

      const text = prompt('Enter annotation text:');
      if (text && text.trim()) {
        onAddAnnotation({ text: text.trim(), x, y });
      }
    }
  };

  return (
    <div
      ref={containerRef}
      style={{
        flex: 1,
        overflow: 'auto',
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'flex-start',
        padding: 20,
        backgroundColor: '#e9ecef',
        position: 'relative',
      }}
      onClick={handleCanvasClick}
    >
      <div style={{ position: 'relative', boxShadow: '0 4px 12px rgba(0,0,0,0.15)', backgroundColor: '#fff' }}>
        <canvas ref={canvasRef} />

        {/* Overlay Annotations */}
        {annotations.map((ann, idx) => (
          <div
            key={`ann-${idx}`}
            style={{
              position: 'absolute',
              left: ann.x * zoom,
              top: ann.y * zoom,
              color: '#000',
              backgroundColor: 'rgba(255, 243, 191, 0.85)',
              padding: '2px 6px',
              borderRadius: 3,
              fontSize: (ann.size || 14) * zoom,
              border: '1px solid #ffd43b',
              pointerEvents: 'none',
            }}
          >
            {ann.text}
          </div>
        ))}
      </div>
    </div>
  );
};
