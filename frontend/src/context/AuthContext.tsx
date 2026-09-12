import React, { createContext, useContext, useState, useEffect } from 'react';
import { AuthResponseDto } from '../types/auth';

interface AuthContextType {
  user: AuthResponseDto | null;
  token: string | null;
  login: (authData: AuthResponseDto) => void;
  logout: () => void;
  isAuthenticated: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

const TOKEN_KEY = 'paperforge_jwt_token';
const USER_KEY = 'paperforge_user_data';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<AuthResponseDto | null>(() => {
    const savedUser = localStorage.getItem(USER_KEY);
    return savedUser ? JSON.parse(savedUser) : null;
  });

  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem(TOKEN_KEY);
  });

  const login = (authData: AuthResponseDto) => {
    setUser(authData);
    setToken(authData.token);
    localStorage.setItem(TOKEN_KEY, authData.token);
    localStorage.setItem(USER_KEY, JSON.stringify(authData));
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, isAuthenticated: !!token }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
