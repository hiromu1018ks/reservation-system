import React from 'react';
import ReservationList from '../components/reservations/ReservationList';
import { useAuth } from '../contexts/AuthContext';

const ReservationManagementPage: React.FC = () => {
  const { user } = useAuth();

  if (user?.role !== 'ADMIN') {
    return <div className="error-message">管理者権限が必要です</div>;
  }

  return (
    <div className="reservation-management-page">
      <h1>予約管理</h1>
      <p>すべての予約を表示しています。承認待ちの予約を承認または拒否できます。</p>
      <ReservationList />
    </div>
  );
};

export default ReservationManagementPage;