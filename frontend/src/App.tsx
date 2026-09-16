import { lazy, Suspense } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { MantineProvider, Center, Loader } from '@mantine/core';
import '@mantine/core/styles.css';

import { AppLayout } from './components/layout/AppLayout';
import { AuthProvider } from './context/AuthContext';
import { DashboardPage } from './pages/DashboardPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';

const ToolsPage = lazy(() => import('./pages/ToolsPage').then((m) => ({ default: m.ToolsPage })));
const PdfEditorPage = lazy(() => import('./pages/PdfEditorPage').then((m) => ({ default: m.PdfEditorPage })));
const PipelinesPage = lazy(() => import('./pages/PipelinesPage').then((m) => ({ default: m.PipelinesPage })));
const ApiKeyPage = lazy(() => import('./pages/ApiKeyPage').then((m) => ({ default: m.ApiKeyPage })));
const AdminDashboardPage = lazy(() => import('./pages/AdminDashboardPage').then((m) => ({ default: m.AdminDashboardPage })));
const AuditLogPage = lazy(() => import('./pages/AuditLogPage').then((m) => ({ default: m.AuditLogPage })));
const SettingsPage = lazy(() => import('./pages/SettingsPage').then((m) => ({ default: m.SettingsPage })));
const HelpPage = lazy(() => import('./pages/HelpPage').then((m) => ({ default: m.HelpPage })));
const AboutPage = lazy(() => import('./pages/AboutPage').then((m) => ({ default: m.AboutPage })));

function PageLoader() {
  return (
    <Center style={{ height: '60vh' }}>
      <Loader size="lg" color="blue" />
    </Center>
  );
}

export function App() {
  return (
    <MantineProvider defaultColorScheme="light">
      <AuthProvider>
        <BrowserRouter>
          <Suspense fallback={<PageLoader />}>
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
                <Route path="admin/audit-logs" element={<AuditLogPage />} />
                <Route path="settings" element={<SettingsPage />} />
                <Route path="help" element={<HelpPage />} />
                <Route path="about" element={<AboutPage />} />
              </Route>
            </Routes>
          </Suspense>
        </BrowserRouter>
      </AuthProvider>
    </MantineProvider>
  );
}

export default App;
