import React from 'react';
import ReservationList from '../components/reservations/ReservationList';
import { useAuth } from '../contexts/AuthContext';

const MyReservationsPage: React.FC = () => {
  const { user } = useAuth();

  if (!user) {
    return (
      <div className="bg-red-100 dark:bg-red-900/30 border border-red-400 dark:border-red-600 text-red-700 dark:text-red-400 px-4 py-3 rounded">
        ログインが必要です
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="card p-6">
        <h1 className="text-2xl font-bold text-gray-900 dark:text-gray-100">マイ予約</h1>
      </div>
      <ReservationList userId={user.id} />
    </div>
  );
};

export default MyReservationsPage;