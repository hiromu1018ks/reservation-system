import React, { useState } from 'react';
import { facilityApi } from '../../services/api';
import type { Facility } from '../../types';

interface FacilityFormProps {
  facility?: Facility;
  onSuccess: () => void;
  onCancel: () => void;
}

const FacilityForm: React.FC<FacilityFormProps> = ({ facility, onSuccess, onCancel }) => {
  const [formData, setFormData] = useState({
    name: facility?.name || '',
    description: facility?.description || '',
    capacity: facility?.capacity || 0,
    location: facility?.location || '',
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string>('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'capacity' ? Number(value) : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    try {
      if (facility) {
        await facilityApi.update(facility.id, formData);
      } else {
        await facilityApi.create(formData);
      }
      onSuccess();
    } catch (err: any) {
      setError(err.response?.data?.message || '施設の保存に失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-stone-50 dark:bg-gray-900">
      <div className="container-responsive py-8">
        <div className="max-w-2xl mx-auto">
          <div className="card animate-slideUp">
            <div className="p-8">
              <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-6">
                {facility ? '施設編集' : '新規施設登録'}
              </h2>
              
              {error && (
                <div className="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
                  <div className="text-red-700 dark:text-red-400">{error}</div>
                </div>
              )}
              
              <form onSubmit={handleSubmit} className="space-y-6">
                <div>
                  <label htmlFor="name" className="form-label">施設名</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className="form-input"
                    required
                  />
                </div>

                <div>
                  <label htmlFor="description" className="form-label">説明</label>
                  <textarea
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    rows={4}
                    className="form-input"
                    required
                  />
                </div>

                <div>
                  <label htmlFor="capacity" className="form-label">収容人数</label>
                  <input
                    type="number"
                    id="capacity"
                    name="capacity"
                    value={formData.capacity}
                    onChange={handleChange}
                    min={1}
                    className="form-input"
                    required
                  />
                </div>

                <div>
                  <label htmlFor="location" className="form-label">場所</label>
                  <input
                    type="text"
                    id="location"
                    name="location"
                    value={formData.location}
                    onChange={handleChange}
                    className="form-input"
                    required
                  />
                </div>

                <div className="flex justify-end space-x-4 pt-6">
                  <button 
                    type="button" 
                    onClick={onCancel}
                    className="btn-secondary"
                  >
                    キャンセル
                  </button>
                  <button 
                    type="submit" 
                    disabled={isLoading}
                    className="btn-primary"
                  >
                    {isLoading ? '保存中...' : '保存'}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default FacilityForm;