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
    <div className="auth-page">
      <div className="auth-container">
        <div className="auth-toggle">
          <button
            className={isLoginMode ? 'active' : ''}
            onClick={() => setIsLoginMode(true)}
          >
            ログイン
          </button>
          <button
            className={!isLoginMode ? 'active' : ''}
            onClick={() => setIsLoginMode(false)}
          >
            新規登録
          </button>
        </div>

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
  );
};

export default AuthPage;