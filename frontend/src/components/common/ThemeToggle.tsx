import React from 'react';
import { useTheme, type Theme } from '../../contexts/ThemeContext';

const ThemeToggle: React.FC = () => {
  const { theme, setTheme } = useTheme();

  const themes: { value: Theme; label: string; icon: string }[] = [
    { value: 'light', label: 'ライト', icon: '○' },
    { value: 'dark', label: 'ダーク', icon: '●' },
    { value: 'system', label: 'システム', icon: '◐' },
  ];

  return (
    <div className="relative inline-block text-left">
      <div className="flex bg-gray-100 dark:bg-gray-700 rounded-lg p-1">
        {themes.map((themeOption) => (
          <button
            key={themeOption.value}
            onClick={() => setTheme(themeOption.value)}
            className={`
              px-3 py-2 text-sm font-medium rounded-md transition-all duration-200 ease-in-out
              flex items-center gap-2 min-w-[80px] justify-center
              ${
                theme === themeOption.value
                  ? 'bg-white dark:bg-gray-600 text-gray-900 dark:text-white shadow-sm'
                  : 'text-gray-600 dark:text-gray-300 hover:text-gray-900 dark:hover:text-white'
              }
            `}
            aria-label={`${themeOption.label}モードに切り替え`}
          >
            <span className="text-lg">{themeOption.icon}</span>
            <span className="hidden sm:inline">{themeOption.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
};

export default ThemeToggle;