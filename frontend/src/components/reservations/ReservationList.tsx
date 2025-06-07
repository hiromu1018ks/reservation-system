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
      setError('');
      let response;
      
      if (userId) {
        response = await reservationApi.getByUser(userId);
      } else if (facilityId) {
        response = await reservationApi.getByFacility(facilityId);
      } else {
        response = await reservationApi.getAll();
      }
      
      setReservations(response.data || []);
    } catch (err: any) {
      console.error('Reservation loading error:', err);
      let errorMessage = '';
      if (err.response?.status === 403) {
        errorMessage = '認証が必要です。ログインし直してください。';
      } else if (err.response?.status === 401) {
        errorMessage = '認証が無効です。ログインし直してください。';
      } else {
        errorMessage = err.response?.data?.message || 
                      (typeof err.response?.data === 'string' ? err.response.data : '') ||
                      `予約の読み込みに失敗しました (${err.response?.status || 'Network Error'})`;
      }
      setError(errorMessage);
      setReservations([]);
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


  const getStatusText = (status: string) => {
    switch (status) {
      case 'APPROVED': return '承認済み';
      case 'CONFIRMED': return '承認済み'; // 旧形式サポート
      case 'PENDING': return '承認待ち';
      case 'REJECTED': return '拒否';
      case 'CANCELLED': return 'キャンセル';
      default: return status;
    }
  };

  if (isLoading) return (
    <div className="flex items-center justify-center py-8">
      <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
      <span className="ml-2 text-gray-600 dark:text-gray-400">読み込み中...</span>
    </div>
  );
  
  if (error) return (
    <div className="bg-red-100 dark:bg-red-900/30 border border-red-400 dark:border-red-600 text-red-700 dark:text-red-400 px-4 py-3 rounded">
      {error}
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="card p-6">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-6">予約一覧</h2>
        
        {reservations.length === 0 ? (
          <div className="text-center py-8 text-gray-500 dark:text-gray-400">
            予約がありません。
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
              <thead className="bg-gray-50 dark:bg-gray-700">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    施設名
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    開始日時
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    終了日時
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    利用目的
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                    ステータス
                  </th>
                  {!userId && (
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                      予約者
                    </th>
                  )}
                  {showActions && (
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-300 uppercase tracking-wider">
                      操作
                    </th>
                  )}
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
                {reservations.map((reservation) => (
                  <tr key={reservation.id} className="hover:bg-gray-50 dark:hover:bg-gray-700">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-gray-100">
                      {reservation.facilityName || '不明'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                      {formatDateTime(reservation.startTime)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                      {formatDateTime(reservation.endTime)}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-900 dark:text-gray-100 max-w-xs truncate">
                      {reservation.purpose}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={
                        (reservation.status === 'APPROVED' || reservation.status === 'CONFIRMED') ? 'status-confirmed' :
                        reservation.status === 'PENDING' ? 'status-pending' :
                        'status-cancelled'
                      }>
                        {getStatusText(reservation.status)}
                      </span>
                    </td>
                    {!userId && (
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 dark:text-gray-400">
                        {reservation.username || '不明'}
                      </td>
                    )}
                    {showActions && (
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium space-x-2">
                        {user?.role === 'ADMIN' && reservation.status === 'PENDING' && (
                          <>
                            <button
                              onClick={() => handleStatusUpdate(reservation.id, 'APPROVED')}
                              className="btn-success text-xs px-2 py-1"
                            >
                              承認
                            </button>
                            <button
                              onClick={() => handleStatusUpdate(reservation.id, 'REJECTED')}
                              className="btn-danger text-xs px-2 py-1"
                            >
                              拒否
                            </button>
                          </>
                        )}
                        {(user?.role === 'ADMIN' || reservation.userId === user?.id) && (
                          <button
                            onClick={() => handleDelete(reservation.id)}
                            className="btn-danger text-xs px-2 py-1"
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
    </div>
  );
};

export default ReservationList;