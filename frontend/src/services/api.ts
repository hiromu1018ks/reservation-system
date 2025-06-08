import axios from 'axios';
import type { 
  User, 
  UserRegistration, 
  LoginRequest, 
  LoginResponse, 
  Facility, 
  Reservation, 
  ReservationCreate,
  ProfileUpdate,
  PasswordChange
} from '../types';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export const authApi = {
  register: (userData: UserRegistration) => 
    api.post('/auth/signup', userData),
  
  login: (credentials: LoginRequest) => 
    api.post<LoginResponse>('/auth/login', credentials),
};

export const userApi = {
  getAll: () => api.get<User[]>('/users'),
  getById: (id: number) => api.get<User>(`/users/${id}`),
  getByUsername: (username: string) => api.get<User>(`/users/username/${username}`),
  register: (userData: UserRegistration) => api.post<User>('/users/register', userData),
  delete: (id: number) => api.delete(`/users/${id}`),
  
  // プロフィール管理機能
  updateProfile: (profileData: ProfileUpdate) => 
    api.put<User>('/users/profile', profileData),
  
  uploadAvatar: (avatarFile: File) => {
    const formData = new FormData();
    formData.append('avatar', avatarFile);
    return api.post<User>('/users/avatar', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
  },
  
  changePassword: (passwordData: PasswordChange) => 
    api.put('/users/password', passwordData),
};

export const facilityApi = {
  getAll: () => api.get<Facility[]>('/facilities'),
  getById: (id: number) => api.get<Facility>(`/facilities/${id}`),
  search: (params: { name?: string; minCapacity?: number }) => 
    api.get<Facility[]>('/facilities/search', { params }),
  create: (facilityData: Omit<Facility, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.post<Facility>('/facilities', facilityData),
  update: (id: number, facilityData: Omit<Facility, 'id' | 'createdAt' | 'updatedAt'>) => 
    api.put<Facility>(`/facilities/${id}`, facilityData),
  delete: (id: number) => api.delete(`/facilities/${id}`),
};

export const reservationApi = {
  getAll: () => api.get<Reservation[]>('/reservations'),
  getById: (id: number) => api.get<Reservation>(`/reservations/${id}`),
  getByFacility: (facilityId: number) => api.get<Reservation[]>(`/reservations/facility/${facilityId}`),
  getByUser: (userId: number) => api.get<Reservation[]>(`/reservations/user/${userId}`),
  getByStatus: (status: string) => api.get<Reservation[]>(`/reservations/status/${status}`),
  create: (reservationData: ReservationCreate) => 
    api.post<Reservation>('/reservations', reservationData),
  updateStatus: (id: number, status: string) => 
    api.patch<Reservation>(`/reservations/${id}/status`, null, { params: { status } }),
  delete: (id: number) => api.delete(`/reservations/${id}`),
};

export default api;