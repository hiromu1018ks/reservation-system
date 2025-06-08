import React from 'react';
import { useAuth } from '../../contexts/AuthContext';
import ThemeToggle from './ThemeToggle';

const Header: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <header className="bg-white dark:bg-gray-800 shadow-soft border-b border-gray-200 dark:border-gray-700 sticky top-0 z-50">
      <div className="container-responsive">
        <div className="flex items-center justify-between h-16">
          {/* ロゴ・タイトル */}
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <h1 className="text-xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
                <div className="w-8 h-8 bg-gradient-primary rounded-lg flex items-center justify-center shadow-sm">
                  <svg 
                    className="w-5 h-5 text-white" 
                    fill="currentColor" 
                    viewBox="0 0 20 20"
                  >
                    <path fillRule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clipRule="evenodd" />
                  </svg>
                </div>
                <span className="text-gray-900 dark:text-white">予約システム</span>
              </h1>
            </div>
          </div>

          {/* 右側のコントロール */}
          <div className="flex items-center gap-4">
            {/* テーマ切り替え */}
            <ThemeToggle />
            
            {isAuthenticated && user ? (
              <div className="flex items-center gap-4">
                {/* ユーザー情報 */}
                <div className="hidden sm:flex items-center gap-2">
                  <div className="w-8 h-8 rounded-full overflow-hidden bg-gradient-primary flex items-center justify-center">
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
                      <span className="text-white font-medium text-sm">
                        {(user.displayName || user.username).charAt(0).toUpperCase()}
                      </span>
                    )}
                  </div>
                  <div className="text-sm">
                    <p className="text-gray-900 dark:text-white font-medium">
                      {user.displayName || user.username}
                    </p>
                    <p className="text-gray-500 dark:text-gray-400 text-xs">
                      {user.role === 'ADMIN' ? '管理者' : 'ユーザー'}
                    </p>
                  </div>
                </div>
                
                {/* ログアウトボタン */}
                <button
                  onClick={logout}
                  className="btn-danger text-sm px-3 py-2"
                >
                  ログアウト
                </button>
              </div>
            ) : (
              <div className="text-sm text-gray-600 dark:text-gray-400">
                ゲストユーザー
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Header;