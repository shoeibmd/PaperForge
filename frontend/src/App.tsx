import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { MantineProvider } from '@mantine/core';
import '@mantine/core/styles.css';

import { AppLayout } from './components/layout/AppLayout';
import { AuthProvider } from './context/AuthContext';
import { DashboardPage } from './pages/DashboardPage';
import { ToolsPage } from './pages/ToolsPage';
import { PdfEditorPage } from './pages/PdfEditorPage';
import { AdminDashboardPage } from './pages/AdminDashboardPage';
import { PipelinesPage } from './pages/PipelinesPage';
import { ApiKeyPage } from './pages/ApiKeyPage';
import { SettingsPage } from './pages/SettingsPage';
import { HelpPage } from './pages/HelpPage';
import { AboutPage } from './pages/AboutPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';

export function App() {
  return (
    <MantineProvider defaultColorScheme="light">
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route path="/" element={<AppLayout />}>
              <Route index element={<DashboardPage />} />
              <Route path="tools" element={<ToolsPage />} />
              <Route path="editor" element={<PdfEditorPage />} />
              <Route path="pipelines" element={<PipelinesPage />} />
              <Route path="api-keys" element={<ApiKeyPage />} />
              <Route path="admin" element={<AdminDashboardPage />} />
              <Route path="settings" element={<SettingsPage />} />
              <Route path="help" element={<HelpPage />} />
              <Route path="about" element={<AboutPage />} />
            </Route>
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </MantineProvider>
  );
}

export default App;
