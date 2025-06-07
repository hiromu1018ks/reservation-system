import React, { useState } from 'react';
import { reservationApi } from '../../services/api';
import type { ReservationCreate, Facility } from '../../types';

interface ReservationFormProps {
  facility: Facility;
  onSuccess: () => void;
  onCancel: () => void;
}

const ReservationForm: React.FC<ReservationFormProps> = ({ facility, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState<ReservationCreate>({
    facilityId: facility.id,
    startTime: '',
    endTime: '',
    purpose: '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (new Date(formData.startTime) >= new Date(formData.endTime)) {
      setError('終了時刻は開始時刻より後に設定してください');
      return;
    }

    setIsLoading(true);

    try {
      await reservationApi.create(formData);
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || '予約の作成に失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto space-y-6">
      <div className="card p-6">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-6">予約作成</h2>
        
        <div className="bg-gray-50 dark:bg-gray-700 rounded-lg p-4 mb-6">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-100 mb-3">施設情報</h3>
          <div className="space-y-2 text-sm">
            <p className="text-gray-700 dark:text-gray-300">
              <span className="font-medium">施設名:</span> {facility.name}
            </p>
            <p className="text-gray-700 dark:text-gray-300">
              <span className="font-medium">収容人数:</span> {facility.capacity}人
            </p>
            <p className="text-gray-700 dark:text-gray-300">
              <span className="font-medium">場所:</span> {facility.location}
            </p>
          </div>
        </div>

        {error && (
          <div className="bg-red-100 dark:bg-red-900/30 border border-red-400 dark:border-red-600 text-red-700 dark:text-red-400 px-4 py-3 rounded mb-6">
            {error}
          </div>
        )}
        
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label htmlFor="startTime" className="form-label">
              開始日時
            </label>
            <input
              type="datetime-local"
              id="startTime"
              name="startTime"
              value={formData.startTime}
              onChange={handleChange}
              required
              className="form-input"
            />
          </div>

          <div>
            <label htmlFor="endTime" className="form-label">
              終了日時
            </label>
            <input
              type="datetime-local"
              id="endTime"
              name="endTime"
              value={formData.endTime}
              onChange={handleChange}
              required
              className="form-input"
            />
          </div>

          <div>
            <label htmlFor="purpose" className="form-label">
              利用目的
            </label>
            <textarea
              id="purpose"
              name="purpose"
              value={formData.purpose}
              onChange={handleChange}
              rows={4}
              required
              placeholder="利用目的を入力してください"
              className="form-input resize-none"
            />
          </div>

          <div className="flex space-x-4 pt-4">
            <button 
              type="submit" 
              disabled={isLoading}
              className="btn-primary flex-1"
            >
              {isLoading ? '予約中...' : '予約する'}
            </button>
            <button 
              type="button" 
              onClick={onCancel}
              className="btn-secondary flex-1"
            >
              キャンセル
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ReservationForm;