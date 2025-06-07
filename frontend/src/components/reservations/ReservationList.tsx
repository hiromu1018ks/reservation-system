import React, { useState, useEffect } from 'react';
import { reservationApi } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import type { Reservation } from '../../types';

interface ReservationListProps {
  userId?: number;
  facilityId?: number;
  showActions?: boolean;
}

const ReservationList: React.FC<ReservationListProps> = ({ 
  userId, 
  facilityId, 
  showActions = true 
}) => {
  const { user } = useAuth();
  const [reservations, setReservations] = useState<Reservation[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    loadReservations();
  }, [userId, facilityId]);

  const loadReservations = async () => {
    try {
      setIsLoading(true);
      let response;
      
      if (userId) {
        response = await reservationApi.getByUser(userId);
      } else if (facilityId) {
        response = await reservationApi.getByFacility(facilityId);
      } else {
        response = await reservationApi.getAll();
      }
      
      setReservations(response.data);
    } catch (err: any) {
      setError('予約の読み込みに失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  const handleStatusUpdate = async (reservationId: number, status: string) => {
    try {
      await reservationApi.updateStatus(reservationId, status);
      loadReservations();
    } catch (err: any) {
      setError('ステータスの更新に失敗しました');
    }
  };

  const handleDelete = async (reservationId: number) => {
    if (!confirm('この予約を削除しますか？')) return;

    try {
      await reservationApi.delete(reservationId);
      setReservations(prev => prev.filter(r => r.id !== reservationId));
    } catch (err: any) {
      setError('予約の削除に失敗しました');
    }
  };

  const formatDateTime = (dateTime: string) => {
    return new Date(dateTime).toLocaleString('ja-JP');
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return 'green';
      case 'PENDING': return 'orange';
      case 'CANCELLED': return 'red';
      default: return 'gray';
    }
  };

  const getStatusText = (status: string) => {
    switch (status) {
      case 'CONFIRMED': return '承認済み';
      case 'PENDING': return '承認待ち';
      case 'CANCELLED': return 'キャンセル';
      default: return status;
    }
  };

  if (isLoading) return <div>読み込み中...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="reservation-list">
      <h2>予約一覧</h2>
      
      {reservations.length === 0 ? (
        <p>予約がありません。</p>
      ) : (
        <div className="reservations-table">
          <table>
            <thead>
              <tr>
                <th>施設名</th>
                <th>開始日時</th>
                <th>終了日時</th>
                <th>利用目的</th>
                <th>ステータス</th>
                {!userId && <th>予約者</th>}
                {showActions && <th>操作</th>}
              </tr>
            </thead>
            <tbody>
              {reservations.map((reservation) => (
                <tr key={reservation.id}>
                  <td>{reservation.facility?.name || '不明'}</td>
                  <td>{formatDateTime(reservation.startTime)}</td>
                  <td>{formatDateTime(reservation.endTime)}</td>
                  <td>{reservation.purpose}</td>
                  <td>
                    <span 
                      className="status-badge" 
                      style={{ color: getStatusColor(reservation.status) }}
                    >
                      {getStatusText(reservation.status)}
                    </span>
                  </td>
                  {!userId && (
                    <td>{reservation.user?.username || '不明'}</td>
                  )}
                  {showActions && (
                    <td className="actions">
                      {user?.role === 'ADMIN' && reservation.status === 'PENDING' && (
                        <>
                          <button
                            onClick={() => handleStatusUpdate(reservation.id, 'CONFIRMED')}
                            className="approve-btn"
                          >
                            承認
                          </button>
                          <button
                            onClick={() => handleStatusUpdate(reservation.id, 'CANCELLED')}
                            className="reject-btn"
                          >
                            拒否
                          </button>
                        </>
                      )}
                      {(user?.role === 'ADMIN' || reservation.userId === user?.id) && (
                        <button
                          onClick={() => handleDelete(reservation.id)}
                          className="delete-btn"
                        >
                          削除
                        </button>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default ReservationList;