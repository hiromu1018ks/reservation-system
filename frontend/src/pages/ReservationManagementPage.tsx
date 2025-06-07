import React from 'react';
import ReservationList from '../components/reservations/ReservationList';
import { useAuth } from '../contexts/AuthContext';

const ReservationManagementPage: React.FC = () => {
  const { user } = useAuth();

  if (user?.role !== 'ADMIN') {
    return (
      <div className="min-h-screen flex items-center justify-center bg-stone-50 dark:bg-gray-900">
        <div className="text-center">
          <div className="text-red-600 dark:text-red-400 text-lg font-medium">管理者権限が必要です</div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-stone-50 dark:bg-gray-900">
      <div className="container-responsive py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white mb-4">予約管理</h1>
          <p className="text-gray-600 dark:text-gray-400">すべての予約を表示しています。承認待ちの予約を承認または拒否できます。</p>
        </div>
        <ReservationList />
      </div>
    </div>
  );
};

export default ReservationManagementPage;