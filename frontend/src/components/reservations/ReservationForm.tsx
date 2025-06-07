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
    <div className="reservation-form">
      <h2>予約作成</h2>
      
      <div className="facility-info">
        <h3>施設情報</h3>
        <p><strong>施設名:</strong> {facility.name}</p>
        <p><strong>収容人数:</strong> {facility.capacity}人</p>
        <p><strong>場所:</strong> {facility.location}</p>
      </div>

      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label htmlFor="startTime">開始日時:</label>
          <input
            type="datetime-local"
            id="startTime"
            name="startTime"
            value={formData.startTime}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="endTime">終了日時:</label>
          <input
            type="datetime-local"
            id="endTime"
            name="endTime"
            value={formData.endTime}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label htmlFor="purpose">利用目的:</label>
          <textarea
            id="purpose"
            name="purpose"
            value={formData.purpose}
            onChange={handleChange}
            rows={4}
            required
            placeholder="利用目的を入力してください"
          />
        </div>

        <div className="form-actions">
          <button type="submit" disabled={isLoading}>
            {isLoading ? '予約中...' : '予約する'}
          </button>
          <button type="button" onClick={onCancel}>
            キャンセル
          </button>
        </div>
      </form>
    </div>
  );
};

export default ReservationForm;