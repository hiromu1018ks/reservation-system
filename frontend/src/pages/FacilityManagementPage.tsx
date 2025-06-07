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
    return <div className="min-h-screen flex items-center justify-center bg-stone-50 dark:bg-gray-900">
      <div className="text-center">
        <div className="text-red-600 dark:text-red-400 text-lg font-medium">管理者権限が必要です</div>
      </div>
    </div>;
  }

  if (isLoading) return (
    <div className="min-h-screen flex items-center justify-center bg-stone-50 dark:bg-gray-900">
      <div className="text-gray-600 dark:text-gray-400">読み込み中...</div>
    </div>
  );

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
    <div className="min-h-screen bg-stone-50 dark:bg-gray-900">
      <div className="container-responsive py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">施設管理</h1>
          <button
            className="btn-primary"
            onClick={() => {
              setSelectedFacility(null);
              setShowForm(true);
            }}
          >
            新規施設追加
          </button>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg">
            <div className="text-red-700 dark:text-red-400">{error}</div>
          </div>
        )}

        <div className="card">
          {facilities.length === 0 ? (
            <div className="p-8 text-center text-gray-500 dark:text-gray-400">
              登録されている施設がありません。
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 dark:bg-gray-800">
                  <tr>
                    <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">施設名</th>
                    <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">説明</th>
                    <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">収容人数</th>
                    <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">場所</th>
                    <th className="px-6 py-4 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200 dark:divide-gray-700">
                  {facilities.map((facility) => (
                    <tr key={facility.id} className="hover:bg-gray-50 dark:hover:bg-gray-800 transition-colors">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">{facility.name}</td>
                      <td className="px-6 py-4 text-sm text-gray-600 dark:text-gray-300 max-w-xs truncate">{facility.description}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-300">{facility.capacity}人</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-300">{facility.location}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm space-x-2">
                        <button
                          onClick={() => {
                            setSelectedFacility(facility);
                            setShowForm(true);
                          }}
                          className="btn-secondary"
                        >
                          編集
                        </button>
                        <button
                          onClick={() => handleDelete(facility.id)}
                          className="btn-danger"
                        >
                          削除
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default FacilityManagementPage;