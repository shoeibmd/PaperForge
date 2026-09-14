export interface AuditLogDto {
  id: number;
  timestamp: string;
  eventType: string;
  username: string | null;
  clientIp: string;
  userAgent: string | null;
  resourceId: string | null;
  detailsJson: string;
  success: boolean;
}

export interface AuditLogPageResponse {
  content: AuditLogDto[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}

export interface AuditLogFilterParams {
  eventType?: string;
  username?: string;
  clientIp?: string;
  success?: boolean;
  startDate?: string;
  endDate?: string;
  query?: string;
  page?: number;
  size?: number;
}

export const auditLogApi = {
  getAuditLogs: async (params: AuditLogFilterParams, token: string): Promise<AuditLogPageResponse> => {
    const queryParams = new URLSearchParams();
    if (params.eventType) queryParams.append('eventType', params.eventType);
    if (params.username) queryParams.append('username', params.username);
    if (params.clientIp) queryParams.append('clientIp', params.clientIp);
    if (params.success !== undefined) queryParams.append('success', String(params.success));
    if (params.startDate) queryParams.append('startDate', params.startDate);
    if (params.endDate) queryParams.append('endDate', params.endDate);
    if (params.query) queryParams.append('query', params.query);
    if (params.page !== undefined) queryParams.append('page', String(params.page));
    if (params.size !== undefined) queryParams.append('size', String(params.size));

    const res = await fetch(`/api/v1/admin/audit-logs?${queryParams.toString()}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Failed to fetch audit logs');
    return res.json();
  },

  downloadCsvExport: async (params: AuditLogFilterParams, token: string): Promise<void> => {
    const queryParams = new URLSearchParams();
    if (params.eventType) queryParams.append('eventType', params.eventType);
    if (params.username) queryParams.append('username', params.username);
    if (params.clientIp) queryParams.append('clientIp', params.clientIp);
    if (params.query) queryParams.append('query', params.query);

    const res = await fetch(`/api/v1/admin/audit-logs/export?${queryParams.toString()}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error('Failed to export CSV audit logs');

    const blob = await res.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'paperforge_audit_logs.csv';
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    document.body.removeChild(a);
  },
};
