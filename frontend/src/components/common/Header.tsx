import React from 'react';
import { useAuth } from '../../contexts/AuthContext';

const Header: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuth();

  return (
    <header className="header">
      <div className="header-content">
        <h1>予約システム</h1>
        
        {isAuthenticated && user ? (
          <div className="user-info">
            <span>ようこそ、{user.username}さん</span>
            <button onClick={logout} className="logout-btn">
              ログアウト
            </button>
          </div>
        ) : (
          <div className="auth-links">
            <span>ゲストユーザー</span>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;