import React, { useState } from 'react';
import LoginForm from '../components/auth/LoginForm';
import RegisterForm from '../components/auth/RegisterForm';

interface AuthPageProps {
  onSuccess: () => void;
}

const AuthPage: React.FC<AuthPageProps> = ({ onSuccess }) => {
  const [isLoginMode, setIsLoginMode] = useState(true);

  const handleAuthSuccess = () => {
    onSuccess();
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-100 dark:from-gray-900 dark:to-gray-800 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        {/* ヘッダー */}
        <div className="text-center">
          <div className="w-16 h-16 bg-gradient-primary rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
            <span className="text-white font-bold text-2xl">□</span>
          </div>
          <h2 className="text-3xl font-bold text-gray-900 dark:text-white">
            予約システム
          </h2>
          <p className="mt-2 text-sm text-gray-600 dark:text-gray-400">
            {isLoginMode ? 'アカウントにログイン' : '新規アカウント作成'}
          </p>
        </div>

        {/* 認証フォーム */}
        <div className="card animate-slideUp">
          <div className="p-8">
            {/* タブ切り替え */}
            <div className="flex bg-gray-100 dark:bg-gray-700 rounded-lg p-1 mb-6">
              <button
                className={`
                  flex-1 py-2 px-4 text-sm font-medium rounded-md transition-all duration-200
                  ${isLoginMode 
                    ? 'bg-white dark:bg-gray-600 text-gray-900 dark:text-white shadow-sm' 
                    : 'text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
                  }
                `}
                onClick={() => setIsLoginMode(true)}
              >
                ログイン
              </button>
              <button
                className={`
                  flex-1 py-2 px-4 text-sm font-medium rounded-md transition-all duration-200
                  ${!isLoginMode 
                    ? 'bg-white dark:bg-gray-600 text-gray-900 dark:text-white shadow-sm' 
                    : 'text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
                  }
                `}
                onClick={() => setIsLoginMode(false)}
              >
                新規登録
              </button>
            </div>

            {/* フォーム */}
            <div className="transition-all duration-300">
              {isLoginMode ? (
                <LoginForm onSuccess={handleAuthSuccess} />
              ) : (
                <RegisterForm onSuccess={() => {
                  alert('ユーザー登録が完了しました。ログインしてください。');
                  setIsLoginMode(true);
                }} />
              )}
            </div>
          </div>
        </div>

        {/* フッター */}
        <div className="text-center text-sm text-gray-500 dark:text-gray-400">
          <p>© 2025 予約システム. All rights reserved.</p>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;