import React from 'react';
import UserList from '../components/users/UserList';
import { useAuth } from '../contexts/AuthContext';

const UserManagementPage: React.FC = () => {
  const { user } = useAuth();

  if (user?.role !== 'ADMIN') {
    return <div className="error-message">管理者権限が必要です</div>;
  }

  return (
    <div className="user-management-page">
      <h1>ユーザー管理</h1>
      <p>システムに登録されているすべてのユーザーを表示しています。</p>
      <UserList />
    </div>
  );
};

export default UserManagementPage;