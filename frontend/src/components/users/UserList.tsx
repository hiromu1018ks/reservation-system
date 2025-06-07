import React, { useState, useEffect } from 'react';
import { userApi } from '../../services/api';
import { useAuth } from '../../contexts/AuthContext';
import type { User } from '../../types';

const UserList: React.FC = () => {
  const { user: currentUser } = useAuth();
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string>('');

  useEffect(() => {
    if (currentUser?.role !== 'ADMIN') {
      setError('管理者権限が必要です');
      return;
    }
    loadUsers();
  }, [currentUser]);

  const loadUsers = async () => {
    try {
      setIsLoading(true);
      const response = await userApi.getAll();
      setUsers(response.data);
    } catch (err: any) {
      setError('ユーザーの読み込みに失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (userId: number) => {
    if (userId === currentUser?.id) {
      alert('自分自身を削除することはできません');
      return;
    }

    if (!confirm('このユーザーを削除しますか？')) return;

    try {
      await userApi.delete(userId);
      setUsers(prev => prev.filter(u => u.id !== userId));
    } catch (err: any) {
      setError('ユーザーの削除に失敗しました');
    }
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('ja-JP');
  };

  const getRoleText = (role: string) => {
    return role === 'ADMIN' ? '管理者' : '一般ユーザー';
  };

  if (currentUser?.role !== 'ADMIN') {
    return <div className="error-message">管理者権限が必要です</div>;
  }

  if (isLoading) return <div>読み込み中...</div>;

  return (
    <div className="user-list">
      <h2>ユーザー一覧</h2>
      
      {error && <div className="error-message">{error}</div>}

      <div className="users-table">
        {users.length === 0 ? (
          <p>ユーザーが見つかりませんでした。</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>ユーザー名</th>
                <th>メールアドレス</th>
                <th>役割</th>
                <th>登録日</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.email}</td>
                  <td>{getRoleText(user.role)}</td>
                  <td>{formatDate(user.createdAt)}</td>
                  <td>
                    {user.id !== currentUser?.id && (
                      <button
                        onClick={() => handleDelete(user.id)}
                        className="delete-btn"
                      >
                        削除
                      </button>
                    )}
                    {user.id === currentUser?.id && (
                      <span className="current-user">現在のユーザー</span>
                    )}
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

export default UserList;