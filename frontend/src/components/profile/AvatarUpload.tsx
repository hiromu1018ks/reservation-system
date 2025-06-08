import React, { useState, useRef } from 'react';
import { User } from '../../types';

interface AvatarUploadProps {
  user: User;
  onUpload: (file: File) => Promise<void>;
  loading?: boolean;
}

const AvatarUpload: React.FC<AvatarUploadProps> = ({ user, onUpload, loading = false }) => {
  const [preview, setPreview] = useState<string | null>(null);
  const [dragOver, setDragOver] = useState(false);
  const [error, setError] = useState<string>('');
  const fileInputRef = useRef<HTMLInputElement>(null);

  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif'];
  const maxFileSize = 5 * 1024 * 1024; // 5MB

  const validateFile = (file: File): string | null => {
    if (!allowedTypes.includes(file.type)) {
      return 'JPEG、PNG、GIF形式の画像ファイルのみアップロード可能です';
    }

    if (file.size > maxFileSize) {
      return 'ファイルサイズは5MB以下にしてください';
    }

    return null;
  };

  const handleFileSelect = (file: File) => {
    const validationError = validateFile(file);
    if (validationError) {
      setError(validationError);
      return;
    }

    setError('');
    
    // プレビュー画像を作成
    const reader = new FileReader();
    reader.onload = (e) => {
      setPreview(e.target?.result as string);
    };
    reader.readAsDataURL(file);

    // ファイルをアップロード
    onUpload(file).catch(() => {
      setPreview(null);
      setError('アップロードに失敗しました');
    });
  };

  const handleFileInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleDragOver = (e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(true);
  };

  const handleDragLeave = (e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);
  };

  const handleDrop = (e: React.DragEvent) => {
    e.preventDefault();
    setDragOver(false);

    const file = e.dataTransfer.files[0];
    if (file) {
      handleFileSelect(file);
    }
  };

  const handleClick = () => {
    fileInputRef.current?.click();
  };

  const getAvatarUrl = () => {
    if (preview) return preview;
    if (user.avatarPath) {
      // バックエンドから提供される画像パスを使用
      const url = `http://localhost:8080/${user.avatarPath}`;
      console.log('Avatar URL:', url); // デバッグ用
      return url;
    }
    return null;
  };

  const generateInitials = (username: string, displayName?: string) => {
    const name = displayName || username;
    return name.slice(0, 2).toUpperCase();
  };

  return (
    <div className="bg-white dark:bg-gray-800 p-6 rounded-lg shadow-md">
      <h3 className="text-lg font-semibold text-gray-900 dark:text-white mb-6">
        プロフィール画像
      </h3>

      <div className="flex flex-col items-center space-y-4">
        {/* アバター表示エリア */}
        <div className="relative">
          <div className="w-32 h-32 rounded-full overflow-hidden bg-gray-200 dark:bg-gray-600 flex items-center justify-center">
            {getAvatarUrl() ? (
              <img
                src={getAvatarUrl()!}
                alt="プロフィール画像"
                className="w-full h-full object-cover"
                onError={(e) => {
                  console.error('Avatar image failed to load:', getAvatarUrl());
                  console.error('User avatar path:', user.avatarPath);
                  e.currentTarget.style.display = 'none';
                }}
              />
            ) : (
              <span className="text-2xl font-semibold text-gray-500 dark:text-gray-300">
                {generateInitials(user.username, user.displayName)}
              </span>
            )}
          </div>
          
          {loading && (
            <div className="absolute inset-0 bg-black bg-opacity-50 rounded-full flex items-center justify-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-white"></div>
            </div>
          )}
        </div>

        {/* ファイル選択・ドロップエリア */}
        <div
          className={`w-full max-w-md p-6 border-2 border-dashed rounded-lg cursor-pointer transition-colors ${
            dragOver
              ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20'
              : 'border-gray-300 dark:border-gray-600 hover:border-gray-400 dark:hover:border-gray-500'
          } ${loading ? 'opacity-50 cursor-not-allowed' : ''}`}
          onDragOver={handleDragOver}
          onDragLeave={handleDragLeave}
          onDrop={handleDrop}
          onClick={!loading ? handleClick : undefined}
        >
          <div className="text-center">
            <svg
              className="mx-auto h-12 w-12 text-gray-400 dark:text-gray-500"
              stroke="currentColor"
              fill="none"
              viewBox="0 0 48 48"
            >
              <path
                d="M28 8H12a4 4 0 00-4 4v20m32-12v8m0 0v8a4 4 0 01-4 4H12a4 4 0 01-4-4v-4m32-4l-3.172-3.172a4 4 0 00-5.656 0L28 28M8 32l9.172-9.172a4 4 0 015.656 0L28 28m0 0l4 4m4-24h8m-4-4v8m-12 4h.02"
                strokeWidth={2}
                strokeLinecap="round"
                strokeLinejoin="round"
              />
            </svg>
            <div className="mt-4">
              <p className="text-sm text-gray-600 dark:text-gray-400">
                <span className="font-medium text-blue-600 dark:text-blue-400">クリック</span>
                するか、ファイルをドラッグ&ドロップ
              </p>
              <p className="text-xs text-gray-500 dark:text-gray-500 mt-1">
                JPEG、PNG、GIF（最大5MB）
              </p>
            </div>
          </div>
        </div>

        {/* エラーメッセージ */}
        {error && (
          <div className="w-full max-w-md">
            <p className="text-sm text-red-600 dark:text-red-400 text-center">{error}</p>
          </div>
        )}

        {/* 削除ボタン */}
        {(user.avatarPath || preview) && !loading && (
          <button
            onClick={() => {
              setPreview(null);
              // 実際の削除処理はここで実装（必要に応じて）
            }}
            className="text-sm text-red-600 dark:text-red-400 hover:text-red-800 dark:hover:text-red-300 transition-colors"
          >
            画像を削除
          </button>
        )}
      </div>

      {/* 隠しファイル入力 */}
      <input
        ref={fileInputRef}
        type="file"
        accept="image/jpeg,image/png,image/gif"
        onChange={handleFileInputChange}
        className="hidden"
        disabled={loading}
      />
    </div>
  );
};

export default AvatarUpload;