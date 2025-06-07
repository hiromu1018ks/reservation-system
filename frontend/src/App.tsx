import React, { useState } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Header from './components/common/Header';
import Navigation from './components/common/Navigation';
import AuthPage from './pages/AuthPage';
import FacilityList from './components/facilities/FacilityList';
import FacilityManagementPage from './pages/FacilityManagementPage';
import ReservationPage from './pages/ReservationPage';
import ReservationManagementPage from './pages/ReservationManagementPage';
import MyReservationsPage from './pages/MyReservationsPage';
import UserManagementPage from './pages/UserManagementPage';
import './App.css';

const AppContent: React.FC = () => {
  const { isAuthenticated, isLoading } = useAuth();
  const [currentPage, setCurrentPage] = useState('facilities');

  const handlePageChange = (page: string) => {
    setCurrentPage(page);
  };

  const handleAuthSuccess = () => {
    setCurrentPage('facilities');
  };

  if (isLoading) {
    return <div className="loading">読み込み中...</div>;
  }

  if (!isAuthenticated) {
    return <AuthPage onSuccess={handleAuthSuccess} />;
  }

  const renderCurrentPage = () => {
    switch (currentPage) {
      case 'facilities':
        return <FacilityList />;
      case 'reservations':
        return <ReservationPage />;
      case 'myReservations':
        return <MyReservationsPage />;
      case 'reservationManagement':
        return <ReservationManagementPage />;
      case 'userManagement':
        return <UserManagementPage />;
      case 'facilityManagement':
        return <FacilityManagementPage />;
      default:
        return <FacilityList />;
    }
  };

  return (
    <div className="app">
      <Header />
      <Navigation currentPage={currentPage} onPageChange={handlePageChange} />
      <main className="main-content">
        {renderCurrentPage()}
      </main>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <AuthProvider>
      <AppContent />
    </AuthProvider>
  );
};

export default App;