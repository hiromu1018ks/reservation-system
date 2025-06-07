import React, { useState } from 'react';
import { useAuth } from '../../contexts/AuthContext';
import type { UserRegistration } from '../../types';

interface RegisterFormProps {
  onSuccess?: () => void;
}

const RegisterForm: React.FC<RegisterFormProps> = ({ onSuccess }) => {
  const { register } = useAuth();
  const [userData, setUserData] = useState<UserRegistration>({
    username: '',
    email: '',
    password: '',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    if (name === 'confirmPassword') {
      setConfirmPassword(value);
    } else {
      setUserData(prev => ({
        ...prev,
        [name]: value,
      }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (userData.password !== confirmPassword) {
      setError('パスワードが一致しません');
      return;
    }

    setIsLoading(true);

    try {
      await register(userData);
      onSuccess?.();
    } catch (err: any) {
      setError(err.response?.data || 'ユーザー登録に失敗しました');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {error && (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 text-red-700 dark:text-red-400 px-4 py-3 rounded-lg">
          {error}
        </div>
      )}
      
      <div>
        <label htmlFor="username" className="form-label">
          ユーザー名
        </label>
        <input
          type="text"
          id="username"
          name="username"
          value={userData.username}
          onChange={handleChange}
          className="form-input"
          placeholder="ユーザー名を入力"
          required
        />
      </div>

      <div>
        <label htmlFor="email" className="form-label">
          メールアドレス
        </label>
        <input
          type="email"
          id="email"
          name="email"
          value={userData.email}
          onChange={handleChange}
          className="form-input"
          placeholder="メールアドレスを入力"
          required
        />
      </div>

      <div>
        <label htmlFor="password" className="form-label">
          パスワード
        </label>
        <input
          type="password"
          id="password"
          name="password"
          value={userData.password}
          onChange={handleChange}
          className="form-input"
          placeholder="パスワードを入力"
          required
        />
      </div>

      <div>
        <label htmlFor="confirmPassword" className="form-label">
          パスワード確認
        </label>
        <input
          type="password"
          id="confirmPassword"
          name="confirmPassword"
          value={confirmPassword}
          onChange={handleChange}
          className="form-input"
          placeholder="パスワードを再入力"
          required
        />
      </div>

      <button 
        type="submit" 
        disabled={isLoading}
        className="btn-primary w-full flex items-center justify-center gap-2"
      >
        {isLoading ? (
          <>
            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
            <span>登録中...</span>
          </>
        ) : (
          <>
            <span>+</span>
            <span>アカウント作成</span>
          </>
        )}
      </button>
    </form>
  );
};

export default RegisterForm;