import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

interface NavigationProps {
  currentPage: string;
  onPageChange: (page: string) => void;
}

const Navigation: React.FC<NavigationProps> = ({ currentPage, onPageChange }) => {
  const { isAuthenticated, user } = useAuth();

  const navItems = [
    { key: 'facilities', label: '施設一覧', requireAuth: false },
    { key: 'reservations', label: '予約する', requireAuth: true },
    { key: 'myReservations', label: 'マイ予約', requireAuth: true },
  ];

  if (user?.role === 'ADMIN') {
    navItems.push(
      { key: 'reservationManagement', label: '予約管理', requireAuth: true },
      { key: 'userManagement', label: 'ユーザー管理', requireAuth: true },
      { key: 'facilityManagement', label: '施設管理', requireAuth: true }
    );
  }

  return (
    <nav className="navigation">
      {navItems
        .filter(item => !item.requireAuth || isAuthenticated)
        .map(item => (
          <button
            key={item.key}
            className={`nav-item ${currentPage === item.key ? 'active' : ''}`}
            onClick={() => onPageChange(item.key)}
          >
            {item.label}
          </button>
        ))}
    </nav>
  );
};

export default Navigation;