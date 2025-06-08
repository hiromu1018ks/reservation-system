import React, { useState, useEffect } from 'react';
import { User, ProfileUpdate } from '../../types';

interface ProfileEditFormProps {
  user: User;
  onSubmit: (profileData: ProfileUpdate) => Promise<void>;
  loading?: boolean;
}

const ProfileEditForm: React.FC<ProfileEditFormProps> = ({ user, onSubmit, loading = false }) => {
  const [formData, setFormData] = useState<ProfileUpdate>({
    displayName: user.displayName || '',
    email: user.email || '',
    bio: user.bio || '',
    phoneNumber: user.phoneNumber || '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    setFormData({
      displayName: user.displayName || '',
      email: user.email || '',
      bio: user.bio || '',
      phoneNumber: user.phoneNumber || '',
    });
  }, [user]);

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      newErrors.email = '有効なメールアドレスを入力してください';
    }

    if (formData.displayName && formData.displayName.length > 100) {
      newErrors.displayName = '表示名は100文字以内で入力してください';
    }

    if (formData.bio && formData.bio.length > 500) {
      newErrors.bio = '自己紹介文は500文字以内で入力してください';
    }

    if (formData.phoneNumber && formData.phoneNumber.length > 20) {
      newErrors.phoneNumber = '電話番号は20文字以内で入力してください';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // エラーをクリア
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      await onSubmit(formData);
    } catch (error) {
      console.error('Profile update error:', error);
    }
  };

  return (
    <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-6">
        プロフィール編集
      </h3>
      
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* 表示名 */}
        <div>
          <label htmlFor="displayName" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            表示名
          </label>
          <input
            type="text"
            id="displayName"
            name="displayName"
            value={formData.displayName}
            onChange={handleChange}
            maxLength={100}
            className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
              errors.displayName ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
            }`}
            placeholder="表示名を入力してください"
          />
          {errors.displayName && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.displayName}</p>
          )}
        </div>

        {/* メールアドレス */}
        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            メールアドレス
          </label>
          <input
            type="email"
            id="email"
            name="email"
            value={formData.email}
            onChange={handleChange}
            maxLength={100}
            className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
              errors.email ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
            }`}
            placeholder="メールアドレスを入力してください"
          />
          {errors.email && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.email}</p>
          )}
        </div>

        {/* 電話番号 */}
        <div>
          <label htmlFor="phoneNumber" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            電話番号
          </label>
          <input
            type="tel"
            id="phoneNumber"
            name="phoneNumber"
            value={formData.phoneNumber}
            onChange={handleChange}
            maxLength={20}
            className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
              errors.phoneNumber ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
            }`}
            placeholder="電話番号を入力してください"
          />
          {errors.phoneNumber && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.phoneNumber}</p>
          )}
        </div>

        {/* 自己紹介文 */}
        <div>
          <label htmlFor="bio" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            自己紹介文
          </label>
          <textarea
            id="bio"
            name="bio"
            value={formData.bio}
            onChange={handleChange}
            maxLength={500}
            rows={4}
            className={`w-full px-3 py-2 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
              errors.bio ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
            }`}
            placeholder="自己紹介文を入力してください"
          />
          <div className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            {formData.bio.length}/500文字
          </div>
          {errors.bio && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.bio}</p>
          )}
        </div>

        {/* 送信ボタン */}
        <div className="flex justify-end">
          <button
            type="submit"
            disabled={loading}
            className={`px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 transition-colors ${
              loading ? 'opacity-50 cursor-not-allowed' : ''
            }`}
          >
            {loading ? '更新中...' : 'プロフィールを更新'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default ProfileEditForm;