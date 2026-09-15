export const isTauriDesktop = (): boolean => {
  return typeof window !== 'undefined' && '__TAURI_INTERNALS__' in window;
};

export const desktopApi = {
  getSidecarStatus: async (): Promise<boolean> => {
    if (!isTauriDesktop()) return false;
    try {
      const internals = (window as any).__TAURI_INTERNALS__;
      if (internals && typeof internals.invoke === 'function') {
        return await internals.invoke('get_sidecar_status');
      }
      return false;
    } catch {
      return false;
    }
  },

  openFileDialog: async (): Promise<string | null> => {
    if (!isTauriDesktop()) return null;
    try {
      const internals = (window as any).__TAURI_INTERNALS__;
      if (internals && typeof internals.invoke === 'function') {
        return await internals.invoke('open_file_dialog');
      }
      return null;
    } catch {
      return null;
    }
  },

  getDesktopApiUrl: (): string => {
    if (isTauriDesktop()) {
      return 'http://localhost:8081';
    }
    return ''; // Relative proxy in web mode
  },
};
