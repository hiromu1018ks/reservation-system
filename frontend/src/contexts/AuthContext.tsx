import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import type { User, LoginRequest, UserRegistration } from '../types';
import { authApi } from '../services/api';

interface AuthContextType {
  user: User | null;
  token: string | null;
  login: (credentials: LoginRequest) => Promise<void>;
  register: (userData: UserRegistration) => Promise<void>;
  logout: () => void;
  updateUser: (userData: User) => void;
  isAuthenticated: boolean;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export { AuthContext };

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUser = localStorage.getItem('user');
    
    if (storedToken && storedUser) {
      setToken(storedToken);
      setUser(JSON.parse(storedUser));
    }
    setIsLoading(false);
  }, []);

  const login = async (credentials: LoginRequest) => {
    try {
      const response = await authApi.login(credentials);
      const { 
        token: authToken, 
        id, 
        username, 
        email, 
        role,
        displayName,
        bio,
        avatarPath,
        phoneNumber,
        createdAt,
        updatedAt
      } = response.data;
      
      const userData: User = {
        id,
        username,
        email,
        role: role as 'USER' | 'ADMIN',
        displayName,
        bio,
        avatarPath,
        phoneNumber,
        createdAt,
        updatedAt,
      };

      setToken(authToken);
      setUser(userData);
      localStorage.setItem('token', authToken);
      localStorage.setItem('user', JSON.stringify(userData));
    } catch (error) {
      throw error;
    }
  };

  const register = async (userData: UserRegistration) => {
    try {
      await authApi.register(userData);
    } catch (error) {
      throw error;
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  };

  const updateUser = (userData: User) => {
    setUser(userData);
    localStorage.setItem('user', JSON.stringify(userData));
  };

  const value: AuthContextType = {
    user,
    token,
    login,
    register,
    logout,
    updateUser,
    isAuthenticated: !!user && !!token,
    isLoading,
  };

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  );
};