import React, { useState, useContext, useEffect } from 'react';
import { AuthContext } from '../contexts/AuthContext';
import { userApi } from '../services/api';
import { User, ProfileUpdate, PasswordChange } from '../types';
import ProfileEditForm from '../components/profile/ProfileEditForm';
import AvatarUpload from '../components/profile/AvatarUpload';
import PasswordChangeForm from '../components/profile/PasswordChangeForm';

const ProfilePage: React.FC = () => {
  const { user: contextUser, updateUser } = useContext(AuthContext);
  const [user, setUser] = useState<User | null>(contextUser);
  const [activeTab, setActiveTab] = useState<'profile' | 'avatar' | 'password'>('profile');
  const [loading, setLoading] = useState({
    profile: false,
    avatar: false,
    password: false,
  });
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  useEffect(() => {
    if (contextUser) {
      setUser(contextUser);
    }
  }, [contextUser]);

  const showMessage = (type: 'success' | 'error', text: string) => {
    setMessage({ type, text });
    setTimeout(() => setMessage(null), 5000);
  };

  const handleProfileUpdate = async (profileData: ProfileUpdate) => {
    if (!user) return;

    setLoading(prev => ({ ...prev, profile: true }));
    try {
      const response = await userApi.updateProfile(profileData);
      const updatedUser = response.data;
      
      setUser(updatedUser);
      updateUser(updatedUser);
      showMessage('success', 'プロフィールを更新しました');
    } catch (error: any) {
      console.error('Profile update error:', error);
      const errorMessage = error.response?.data?.message || 'プロフィールの更新に失敗しました';
      showMessage('error', errorMessage);
    } finally {
      setLoading(prev => ({ ...prev, profile: false }));
    }
  };

  const handleAvatarUpload = async (file: File) => {
    if (!user) return;

    setLoading(prev => ({ ...prev, avatar: true }));
    try {
      const response = await userApi.uploadAvatar(file);
      const updatedUser = response.data;
      
      setUser(updatedUser);
      updateUser(updatedUser);
      showMessage('success', 'アバター画像を更新しました');
    } catch (error: any) {
      console.error('Avatar upload error:', error);
      const errorMessage = error.response?.data?.message || 'アバター画像のアップロードに失敗しました';
      showMessage('error', errorMessage);
      throw error; // AvatarUploadコンポーネントでエラーハンドリングするため
    } finally {
      setLoading(prev => ({ ...prev, avatar: false }));
    }
  };

  const handlePasswordChange = async (passwordData: PasswordChange) => {
    setLoading(prev => ({ ...prev, password: true }));
    try {
      await userApi.changePassword(passwordData);
      showMessage('success', 'パスワードを変更しました');
    } catch (error: any) {
      console.error('Password change error:', error);
      const errorMessage = error.response?.data?.message || 'パスワードの変更に失敗しました';
      showMessage('error', errorMessage);
      throw error; // PasswordChangeFormコンポーネントでエラーハンドリングするため
    } finally {
      setLoading(prev => ({ ...prev, password: false }));
    }
  };

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-32 w-32 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600 dark:text-gray-400">読み込み中...</p>
        </div>
      </div>
    );
  }

  const tabs = [
    { id: 'profile' as const, label: 'プロフィール編集', icon: '👤' },
    { id: 'avatar' as const, label: 'アバター画像', icon: '🖼️' },
    { id: 'password' as const, label: 'パスワード変更', icon: '🔒' },
  ];

  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 py-8">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
        {/* ヘッダー */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">
            プロフィール管理
          </h1>
          <p className="mt-2 text-gray-600 dark:text-gray-400">
            アカウント情報の表示・編集を行えます
          </p>
        </div>

        {/* メッセージ表示 */}
        {message && (
          <div className={`mb-6 p-4 rounded-md ${
            message.type === 'success' 
              ? 'bg-green-50 dark:bg-green-900/20 text-green-700 dark:text-green-300 border border-green-200 dark:border-green-800'
              : 'bg-red-50 dark:bg-red-900/20 text-red-700 dark:text-red-300 border border-red-200 dark:border-red-800'
          }`}>
            <div className="flex items-center">
              <span className="mr-2">
                {message.type === 'success' ? '✅' : '❌'}
              </span>
              {message.text}
            </div>
          </div>
        )}

        {/* ユーザー基本情報 */}
        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md mb-8">
          <div className="flex items-center space-x-4">
            <div className="w-16 h-16 rounded-full overflow-hidden bg-gray-200 dark:bg-gray-600 flex items-center justify-center">
              {user.avatarPath ? (
                <img
                  src={`http://localhost:8080/${user.avatarPath}`}
                  alt="プロフィール画像"
                  className="w-full h-full object-cover"
                  onError={(e) => {
                    console.error('Avatar image failed to load:', user.avatarPath);
                    e.currentTarget.style.display = 'none';
                  }}
                />
              ) : (
                <span className="text-xl font-semibold text-gray-500 dark:text-gray-300">
                  {(user.displayName || user.username).slice(0, 2).toUpperCase()}
                </span>
              )}
            </div>
            <div>
              <h2 className="text-xl font-semibold text-gray-900 dark:text-white">
                {user.displayName || user.username}
              </h2>
              <p className="text-gray-600 dark:text-gray-400">@{user.username}</p>
              <p className="text-sm text-gray-500 dark:text-gray-400">{user.email}</p>
            </div>
          </div>
        </div>

        {/* タブナビゲーション */}
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow-md mb-8">
          <div className="border-b border-gray-200 dark:border-gray-700">
            <nav className="flex">
              {tabs.map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => setActiveTab(tab.id)}
                  className={`flex-1 py-4 px-6 text-center font-medium text-sm focus:outline-none transition-colors ${
                    activeTab === tab.id
                      ? 'border-b-2 border-blue-500 text-blue-600 dark:text-blue-400'
                      : 'text-gray-500 dark:text-gray-400 hover:text-gray-700 dark:hover:text-gray-300'
                  }`}
                >
                  <span className="mr-2">{tab.icon}</span>
                  {tab.label}
                </button>
              ))}
            </nav>
          </div>

          {/* タブコンテンツ */}
          <div className="p-6">
            {activeTab === 'profile' && (
              <ProfileEditForm
                user={user}
                onSubmit={handleProfileUpdate}
                loading={loading.profile}
              />
            )}

            {activeTab === 'avatar' && (
              <AvatarUpload
                user={user}
                onUpload={handleAvatarUpload}
                loading={loading.avatar}
              />
            )}

            {activeTab === 'password' && (
              <PasswordChangeForm
                onSubmit={handlePasswordChange}
                loading={loading.password}
              />
            )}
          </div>
        </div>

        {/* 追加情報 */}
        <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
          <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            アカウント情報
          </h3>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div>
              <span className="text-gray-600 dark:text-gray-400">ユーザーID:</span>
              <span className="ml-2 font-mono">{user.id}</span>
            </div>
            <div>
              <span className="text-gray-600 dark:text-gray-400">権限:</span>
              <span className={`ml-2 px-2 py-1 rounded-full text-xs ${
                user.role === 'ADMIN' 
                  ? 'bg-red-100 dark:bg-red-900/20 text-red-700 dark:text-red-300'
                  : 'bg-blue-100 dark:bg-blue-900/20 text-blue-700 dark:text-blue-300'
              }`}>
                {user.role === 'ADMIN' ? '管理者' : '一般ユーザー'}
              </span>
            </div>
            <div>
              <span className="text-gray-600 dark:text-gray-400">作成日:</span>
              <span className="ml-2">
                {user.createdAt ? new Date(user.createdAt).toLocaleDateString('ja-JP') : '未設定'}
              </span>
            </div>
            <div>
              <span className="text-gray-600 dark:text-gray-400">最終更新:</span>
              <span className="ml-2">
                {user.updatedAt ? new Date(user.updatedAt).toLocaleDateString('ja-JP') : '未設定'}
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;