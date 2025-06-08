import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

interface NavigationProps {
  currentPage: string;
  onPageChange: (page: string) => void;
}

const Navigation: React.FC<NavigationProps> = ({ currentPage, onPageChange }) => {
  const { isAuthenticated, user } = useAuth();

  const navItems = [
    { key: 'facilities', label: '施設一覧', icon: '■', requireAuth: false },
    { key: 'reservations', label: '予約する', icon: '+', requireAuth: true },
    { key: 'myReservations', label: 'マイ予約', icon: '□', requireAuth: true },
    { key: 'profile', label: 'プロフィール', icon: '👤', requireAuth: true },
  ];

  if (user?.role === 'ADMIN') {
    navItems.push(
      { key: 'reservationManagement', label: '予約管理', icon: '⚙', requireAuth: true },
      { key: 'userManagement', label: 'ユーザー管理', icon: '◯', requireAuth: true },
      { key: 'facilityManagement', label: '施設管理', icon: '△', requireAuth: true }
    );
  }

  return (
    <nav className="bg-white dark:bg-gray-800 border-b border-gray-200 dark:border-gray-700 sticky top-16 z-40">
      <div className="container-responsive">
        <div className="flex overflow-x-auto scrollbar-thin">
          {navItems
            .filter(item => !item.requireAuth || isAuthenticated)
            .map(item => (
              <button
                key={item.key}
                className={`
                  nav-item whitespace-nowrap flex items-center gap-2 min-w-fit
                  ${currentPage === item.key ? 'nav-item-active' : ''}
                `}
                onClick={() => onPageChange(item.key)}
              >
                <span className="text-lg">{item.icon}</span>
                <span>{item.label}</span>
              </button>
            ))}
        </div>
      </div>
    </nav>
  );
};

export default Navigation;