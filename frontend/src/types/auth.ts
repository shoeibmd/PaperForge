export interface AuthRequestDto {
  username?: string;
  password?: string;
}

export interface RegisterRequestDto {
  username?: string;
  email?: string;
  password?: string;
}

export interface AuthResponseDto {
  token: string;
  username: string;
  email: string;
  roles: string[];
}
