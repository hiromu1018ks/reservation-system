import React, { useState, useEffect } from 'react';
import { facilityApi } from '../../services/api';
import type { Facility } from '../../types';

interface FacilityListProps {
  onFacilitySelect?: (facility: Facility) => void;
}

const FacilityList: React.FC<FacilityListProps> = ({ onFacilitySelect }) => {
  const [facilities, setFacilities] = useState<Facility[]>([]);
  const [searchName, setSearchName] = useState('');
  const [minCapacity, setMinCapacity] = useState<number | ''>('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    loadFacilities();
  }, []);

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

  const handleSearch = async () => {
    try {
      setIsLoading(true);
      const params: { name?: string; minCapacity?: number } = {};
      if (searchName) params.name = searchName;
      if (minCapacity) params.minCapacity = Number(minCapacity);

      const response = await facilityApi.search(params);
      setFacilities(response.data);
    } catch (err: any) {
      setError('検索に失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = () => {
    setSearchName('');
    setMinCapacity('');
    loadFacilities();
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
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-sm border border-gray-200 dark:border-gray-700 p-6">
        <h2 className="text-2xl font-bold text-gray-900 dark:text-gray-100 mb-6">施設一覧</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <input
            type="text"
            placeholder="施設名で検索"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
            className="form-input"
          />
          <input
            type="number"
            placeholder="最小収容人数"
            value={minCapacity}
            onChange={(e) => setMinCapacity(e.target.value ? Number(e.target.value) : '')}
            className="form-input"
          />
          <div className="flex space-x-2">
            <button onClick={handleSearch} className="btn-primary flex-1">
              検索
            </button>
            <button onClick={handleReset} className="btn-secondary flex-1">
              リセット
            </button>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {facilities.length === 0 ? (
          <div className="col-span-full text-center py-8 text-gray-500 dark:text-gray-400">
            施設が見つかりませんでした。
          </div>
        ) : (
          facilities.map((facility) => (
            <div key={facility.id} className="card p-6 flex flex-col h-full">
              <div className="flex-grow space-y-4">
                <h3 className="text-xl font-semibold text-gray-900 dark:text-gray-100">
                  {facility.name}
                </h3>
                <p className="text-gray-600 dark:text-gray-400 text-sm line-clamp-3">
                  {facility.description}
                </p>
                <div className="space-y-2 text-sm">
                  <p className="text-gray-700 dark:text-gray-300">
                    <span className="font-medium">収容人数:</span> {facility.capacity}人
                  </p>
                  <p className="text-gray-700 dark:text-gray-300">
                    <span className="font-medium">場所:</span> {facility.location}
                  </p>
                </div>
              </div>
              {onFacilitySelect && (
                <div className="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
                  <button 
                    className="btn-primary w-full"
                    onClick={() => onFacilitySelect(facility)}
                  >
                    予約する
                  </button>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FacilityList;