import { openDB, DBSchema, IDBPDatabase } from 'idb';

interface PaperForgeEditorDB extends DBSchema {
  documents: {
    key: string;
    value: {
      id: string;
      name: string;
      data: ArrayBuffer;
      updatedAt: number;
    };
  };
  thumbnails: {
    key: string;
    value: {
      id: string; // docId_pageIndex
      docId: string;
      pageIndex: number;
      dataUrl: string;
    };
    indexes: { 'by-docId': string };
  };
}

const DB_NAME = 'PaperForgeEditorDB';
const DB_VERSION = 1;

let dbPromise: Promise<IDBPDatabase<PaperForgeEditorDB>> | null = null;

function getDB() {
  if (!dbPromise) {
    dbPromise = openDB<PaperForgeEditorDB>(DB_NAME, DB_VERSION, {
      upgrade(db) {
        if (!db.objectStoreNames.contains('documents')) {
          db.createObjectStore('documents', { keyPath: 'id' });
        }
        if (!db.objectStoreNames.contains('thumbnails')) {
          const thumbStore = db.createObjectStore('thumbnails', { keyPath: 'id' });
          thumbStore.createIndex('by-docId', 'docId');
        }
      },
    });
  }
  return dbPromise;
}

export const indexedDbService = {
  saveDocument: async (id: string, name: string, data: ArrayBuffer): Promise<void> => {
    const db = await getDB();
    await db.put('documents', {
      id,
      name,
      data,
      updatedAt: Date.now(),
    });
  },

  getDocument: async (id: string): Promise<{ id: string; name: string; data: ArrayBuffer } | undefined> => {
    const db = await getDB();
    return db.get('documents', id);
  },

  saveThumbnail: async (docId: string, pageIndex: number, dataUrl: string): Promise<void> => {
    const db = await getDB();
    const id = `${docId}_${pageIndex}`;
    await db.put('thumbnails', {
      id,
      docId,
      pageIndex,
      dataUrl,
    });
  },

  getThumbnails: async (docId: string): Promise<{ pageIndex: number; dataUrl: string }[]> => {
    const db = await getDB();
    const thumbs = await db.getAllFromIndex('thumbnails', 'by-docId', docId);
    return thumbs.map((t) => ({ pageIndex: t.pageIndex, dataUrl: t.dataUrl }));
  },

  clearDocument: async (id: string): Promise<void> => {
    const db = await getDB();
    await db.delete('documents', id);
    const thumbs = await db.getAllKeysFromIndex('thumbnails', 'by-docId', id);
    for (const key of thumbs) {
      await db.delete('thumbnails', key);
    }
  },
};
