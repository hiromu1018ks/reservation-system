import React, { useState, useEffect } from 'react';
import { facilityApi } from '../services/api';
import FacilityForm from '../components/facilities/FacilityForm';
import { useAuth } from '../contexts/AuthContext';
import type { Facility } from '../types';

const FacilityManagementPage: React.FC = () => {
  const { user } = useAuth();
  const [facilities, setFacilities] = useState<Facility[]>([]);
  const [selectedFacility, setSelectedFacility] = useState<Facility | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (user?.role !== 'ADMIN') {
      setError('管理者権限が必要です');
      return;
    }
    loadFacilities();
  }, [user]);

  const loadFacilities = async () => {
    try {
      setIsLoading(true);
      const response = await facilityApi.getAll();
      setFacilities(response.data);
    } catch (err: any) {
      setError('施設の読み込みに失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (facilityId: number) => {
    if (!confirm('この施設を削除しますか？')) return;

    try {
      await facilityApi.delete(facilityId);
      setFacilities(prev => prev.filter(f => f.id !== facilityId));
    } catch (err: any) {
      setError('施設の削除に失敗しました');
    }
  };

  const handleFormSuccess = () => {
    setShowForm(false);
    setSelectedFacility(null);
    loadFacilities();
  };

  const handleFormCancel = () => {
    setShowForm(false);
    setSelectedFacility(null);
  };

  if (user?.role !== 'ADMIN') {
    return <div className="error-message">管理者権限が必要です</div>;
  }

  if (isLoading) return <div>読み込み中...</div>;

  if (showForm) {
    return (
      <FacilityForm
        facility={selectedFacility || undefined}
        onSuccess={handleFormSuccess}
        onCancel={handleFormCancel}
      />
    );
  }

  return (
    <div className="facility-management">
      <div className="page-header">
        <h2>施設管理</h2>
        <button
          className="add-facility-btn"
          onClick={() => {
            setSelectedFacility(null);
            setShowForm(true);
          }}
        >
          新規施設追加
        </button>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="facilities-table">
        {facilities.length === 0 ? (
          <p>登録されている施設がありません。</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>施設名</th>
                <th>説明</th>
                <th>収容人数</th>
                <th>場所</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {facilities.map((facility) => (
                <tr key={facility.id}>
                  <td>{facility.name}</td>
                  <td>{facility.description}</td>
                  <td>{facility.capacity}人</td>
                  <td>{facility.location}</td>
                  <td>
                    <button
                      onClick={() => {
                        setSelectedFacility(facility);
                        setShowForm(true);
                      }}
                    >
                      編集
                    </button>
                    <button
                      onClick={() => handleDelete(facility.id)}
                      className="delete-btn"
                    >
                      削除
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default FacilityManagementPage;