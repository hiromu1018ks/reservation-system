import React from 'react';
import ReservationList from '../components/reservations/ReservationList';
import { useAuth } from '../contexts/AuthContext';

const MyReservationsPage: React.FC = () => {
  const { user } = useAuth();

  if (!user) {
    return <div className="error-message">ログインが必要です</div>;
  }

  return (
    <div className="my-reservations-page">
      <h1>マイ予約</h1>
      <ReservationList userId={user.id} />
    </div>
  );
};

export default MyReservationsPage;