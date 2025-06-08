import React, { useState } from 'react';
import { PasswordChange } from '../../types';

interface PasswordChangeFormProps {
  onSubmit: (passwordData: PasswordChange) => Promise<void>;
  loading?: boolean;
}

const PasswordChangeForm: React.FC<PasswordChangeFormProps> = ({ onSubmit, loading = false }) => {
  const [formData, setFormData] = useState<PasswordChange>({
    currentPassword: '',
    newPassword: '',
    confirmPassword: '',
  });

  const [errors, setErrors] = useState<Record<string, string>>({});
  const [showPasswords, setShowPasswords] = useState({
    current: false,
    new: false,
    confirm: false,
  });

  const validateForm = (): boolean => {
    const newErrors: Record<string, string> = {};

    if (!formData.currentPassword) {
      newErrors.currentPassword = '現在のパスワードは必須です';
    }

    if (!formData.newPassword) {
      newErrors.newPassword = '新しいパスワードは必須です';
    } else if (formData.newPassword.length < 8) {
      newErrors.newPassword = 'パスワードは8文字以上で入力してください';
    } else if (formData.newPassword.length > 100) {
      newErrors.newPassword = 'パスワードは100文字以内で入力してください';
    }

    if (!formData.confirmPassword) {
      newErrors.confirmPassword = '新しいパスワード（確認）は必須です';
    } else if (formData.newPassword !== formData.confirmPassword) {
      newErrors.confirmPassword = '新しいパスワードと確認パスワードが一致しません';
    }

    if (formData.currentPassword === formData.newPassword) {
      newErrors.newPassword = '新しいパスワードは現在のパスワードと異なる必要があります';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // エラーをクリア
    if (errors[name]) {
      setErrors(prev => ({ ...prev, [name]: '' }));
    }
  };

  const togglePasswordVisibility = (field: keyof typeof showPasswords) => {
    setShowPasswords(prev => ({ ...prev, [field]: !prev[field] }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!validateForm()) {
      return;
    }

    try {
      await onSubmit(formData);
      // 成功時にフォームをクリア
      setFormData({
        currentPassword: '',
        newPassword: '',
        confirmPassword: '',
      });
    } catch (error) {
      console.error('Password change error:', error);
    }
  };

  const getPasswordStrength = (password: string): { level: number; text: string; color: string } => {
    if (password.length === 0) return { level: 0, text: '', color: '' };
    
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;

    if (strength <= 2) return { level: strength, text: '弱い', color: 'bg-red-500' };
    if (strength <= 3) return { level: strength, text: '普通', color: 'bg-yellow-500' };
    return { level: strength, text: '強い', color: 'bg-green-500' };
  };

  const passwordStrength = getPasswordStrength(formData.newPassword);

  return (
    <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-6">
        パスワード変更
      </h3>
      
      <form onSubmit={handleSubmit} className="space-y-6">
        {/* 現在のパスワード */}
        <div>
          <label htmlFor="currentPassword" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            現在のパスワード
          </label>
          <div className="relative">
            <input
              type={showPasswords.current ? 'text' : 'password'}
              id="currentPassword"
              name="currentPassword"
              value={formData.currentPassword}
              onChange={handleChange}
              className={`w-full px-3 py-2 pr-10 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
                errors.currentPassword ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
              }`}
              placeholder="現在のパスワードを入力してください"
            />
            <button
              type="button"
              onClick={() => togglePasswordVisibility('current')}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            >
              {showPasswords.current ? (
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                </svg>
              ) : (
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
          </div>
          {errors.currentPassword && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.currentPassword}</p>
          )}
        </div>

        {/* 新しいパスワード */}
        <div>
          <label htmlFor="newPassword" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            新しいパスワード
          </label>
          <div className="relative">
            <input
              type={showPasswords.new ? 'text' : 'password'}
              id="newPassword"
              name="newPassword"
              value={formData.newPassword}
              onChange={handleChange}
              className={`w-full px-3 py-2 pr-10 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
                errors.newPassword ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
              }`}
              placeholder="新しいパスワードを入力してください"
            />
            <button
              type="button"
              onClick={() => togglePasswordVisibility('new')}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            >
              {showPasswords.new ? (
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                </svg>
              ) : (
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
          </div>
          
          {/* パスワード強度インジケーター */}
          {formData.newPassword && (
            <div className="mt-2">
              <div className="flex items-center space-x-2">
                <div className="flex-1 bg-gray-200 dark:bg-gray-600 rounded-full h-2">
                  <div
                    className={`h-2 rounded-full transition-all duration-300 ${passwordStrength.color}`}
                    style={{ width: `${(passwordStrength.level / 5) * 100}%` }}
                  />
                </div>
                <span className="text-sm text-gray-600 dark:text-gray-400">
                  {passwordStrength.text}
                </span>
              </div>
              <p className="mt-1 text-xs text-gray-500 dark:text-gray-400">
                推奨: 8文字以上、大文字・小文字・数字・記号を含む
              </p>
            </div>
          )}
          
          {errors.newPassword && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.newPassword}</p>
          )}
        </div>

        {/* 新しいパスワード（確認） */}
        <div>
          <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
            新しいパスワード（確認）
          </label>
          <div className="relative">
            <input
              type={showPasswords.confirm ? 'text' : 'password'}
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              className={`w-full px-3 py-2 pr-10 border rounded-md shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:text-white ${
                errors.confirmPassword ? 'border-red-500' : 'border-gray-300 dark:border-gray-600'
              }`}
              placeholder="新しいパスワードを再度入力してください"
            />
            <button
              type="button"
              onClick={() => togglePasswordVisibility('confirm')}
              className="absolute inset-y-0 right-0 pr-3 flex items-center text-gray-400 hover:text-gray-600 dark:hover:text-gray-300"
            >
              {showPasswords.confirm ? (
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.878 9.878L3 3m6.878 6.878L21 21" />
                </svg>
              ) : (
                <svg className="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
              )}
            </button>
          </div>
          {errors.confirmPassword && (
            <p className="mt-1 text-sm text-red-600 dark:text-red-400">{errors.confirmPassword}</p>
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
            {loading ? '変更中...' : 'パスワードを変更'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default PasswordChangeForm;