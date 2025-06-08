export interface User {
  id: number;
  username: string;
  email: string;
  role: 'USER' | 'ADMIN';
  displayName?: string;
  bio?: string;
  avatarPath?: string;
  phoneNumber?: string;
  createdAt: string;
  updatedAt: string;
}

export interface UserRegistration {
  username: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  id: number;
  username: string;
  email: string;
  role: string;
  displayName?: string;
  bio?: string;
  avatarPath?: string;
  phoneNumber?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Facility {
  id: number;
  name: string;
  description: string;
  capacity: number;
  location: string;
  createdAt: string;
  updatedAt: string;
}

export interface Reservation {
  id: number;
  userId: number;
  facilityId: number;
  facilityName: string;
  username: string;
  startTime: string;
  endTime: string;
  purpose: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED' | 'CONFIRMED'; // CONFIRMED is legacy support
  user?: User;
  facility?: Facility;
}

export interface ReservationCreate {
  facilityId: number;
  startTime: string;
  endTime: string;
  purpose: string;
}

export interface ApiError {
  message: string;
  status: number;
}

export interface ProfileUpdate {
  displayName?: string;
  email?: string;
  bio?: string;
  phoneNumber?: string;
}

export interface PasswordChange {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

