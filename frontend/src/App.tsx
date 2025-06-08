import React, { useState } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';
import Header from './components/common/Header';
import Navigation from './components/common/Navigation';
import AuthPage from './pages/AuthPage';
import FacilityList from './components/facilities/FacilityList';
import FacilityManagementPage from './pages/FacilityManagementPage';
import ReservationPage from './pages/ReservationPage';
import ReservationManagementPage from './pages/ReservationManagementPage';
import MyReservationsPage from './pages/MyReservationsPage';
import UserManagementPage from './pages/UserManagementPage';
import ProfilePage from './pages/ProfilePage';

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
    return (
      <div className="min-h-screen flex items-center justify-center bg-white dark:bg-gray-800">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary-600 mx-auto mb-4"></div>
          <p className="text-lg text-gray-600 dark:text-gray-400">読み込み中...</p>
        </div>
      </div>
    );
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
      case 'profile':
        return <ProfilePage />;
      default:
        return <FacilityList />;
    }
  };

  return (
    <div className="min-h-screen bg-white dark:bg-gray-800 transition-colors duration-300">
      <Header />
      <Navigation currentPage={currentPage} onPageChange={handlePageChange} />
      <main className="container-responsive py-8">
        <div className="animate-fadeIn">
          {renderCurrentPage()}
        </div>
      </main>
    </div>
  );
};

const App: React.FC = () => {
  return (
    <ThemeProvider>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </ThemeProvider>
  );
};

export default App;