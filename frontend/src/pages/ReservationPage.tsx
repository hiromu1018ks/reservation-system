import React, { useState } from 'react';
import FacilityList from '../components/facilities/FacilityList';
import ReservationForm from '../components/reservations/ReservationForm';
import type { Facility } from '../types';

const ReservationPage: React.FC = () => {
  const [selectedFacility, setSelectedFacility] = useState<Facility | null>(null);

  const handleFacilitySelect = (facility: Facility) => {
    setSelectedFacility(facility);
  };

  const handleReservationSuccess = () => {
    setSelectedFacility(null);
    alert('予約が完了しました。管理者の承認をお待ちください。');
  };

  const handleReservationCancel = () => {
    setSelectedFacility(null);
  };

  if (selectedFacility) {
    return (
      <ReservationForm
        facility={selectedFacility}
        onSuccess={handleReservationSuccess}
        onCancel={handleReservationCancel}
      />
    );
  }

  return (
    <div className="min-h-screen bg-stone-50 dark:bg-gray-900">
      <div className="container-responsive py-8">
        <FacilityList onFacilitySelect={handleFacilitySelect} />
      </div>
    </div>
  );
};

export default ReservationPage;