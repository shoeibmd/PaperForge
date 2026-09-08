export interface HealthResponse {
  status: string;
  timestamp: string;
}

export interface InfoResponse {
  name: string;
  tagline: string;
  version: string;
  environment: string;
}

export interface ConfigResponse {
  maxUploadSizeBytes: number;
  featureFlags: Record<string, boolean>;
  defaultTheme: string;
  supportEmail: string;
}

export interface PdfMetadataDto {
  title?: string;
  author?: string;
  subject?: string;
  keywords?: string;
  creator?: string;
  producer?: string;
}
