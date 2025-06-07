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

  if (isLoading) return <div>読み込み中...</div>;
  if (error) return <div className="error-message">{error}</div>;

  return (
    <div className="facility-list">
      <h2>施設一覧</h2>
      
      <div className="search-form">
        <div className="search-group">
          <input
            type="text"
            placeholder="施設名で検索"
            value={searchName}
            onChange={(e) => setSearchName(e.target.value)}
          />
          <input
            type="number"
            placeholder="最小収容人数"
            value={minCapacity}
            onChange={(e) => setMinCapacity(e.target.value ? Number(e.target.value) : '')}
          />
          <button onClick={handleSearch}>検索</button>
          <button onClick={handleReset}>リセット</button>
        </div>
      </div>

      <div className="facilities-grid">
        {facilities.length === 0 ? (
          <p>施設が見つかりませんでした。</p>
        ) : (
          facilities.map((facility) => (
            <div key={facility.id} className="facility-card">
              <h3>{facility.name}</h3>
              <p className="facility-description">{facility.description}</p>
              <div className="facility-details">
                <p><strong>収容人数:</strong> {facility.capacity}人</p>
                <p><strong>場所:</strong> {facility.location}</p>
              </div>
              {onFacilitySelect && (
                <button 
                  className="select-facility-btn"
                  onClick={() => onFacilitySelect(facility)}
                >
                  予約する
                </button>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default FacilityList;