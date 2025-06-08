import React, { createContext, type ReactNode, useContext, useEffect, useState } from 'react';

export type Theme = 'light' | 'dark' | 'system';

interface ThemeContextType {
  theme : Theme;
  effectiveTheme : 'light' | 'dark';
  setTheme : (theme : Theme) => void;
}

const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

export const useTheme = () => {
  const context = useContext(ThemeContext);
  if ( context === undefined ) {
    throw new Error('useTheme must be used within a ThemeProvider');
  }
  return context;
};

interface ThemeProviderProps {
  children : ReactNode;
}

export const ThemeProvider : React.FC<ThemeProviderProps> = ({ children }) => {
  // システム設定から初期テーマを取得
  const getInitialTheme = () : Theme => {
    const savedTheme = localStorage.getItem('theme') as Theme;
    if ( savedTheme && [ 'light', 'dark', 'system' ].includes(savedTheme) ) {
      return savedTheme;
    }
    return 'system';
  };

  const [ theme, setTheme ] = useState<Theme>(getInitialTheme);

  // システムのダークモード設定を監視
  const [ systemTheme, setSystemTheme ] = useState<'light' | 'dark'>(
    window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  );

  // 実際に適用されるテーマを計算
  const effectiveTheme : 'light' | 'dark' = theme === 'system' ? systemTheme : theme;

  // システムテーマの変更を監視
  useEffect(() => {
    const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');

    const handleChange = (e : MediaQueryListEvent) => {
      setSystemTheme(e.matches ? 'dark' : 'light');
    };

    // モダンブラウザ対応
    if ( mediaQuery.addEventListener ) {
      mediaQuery.addEventListener('change', handleChange);
    } else {
      // 古いブラウザ対応
      mediaQuery.addListener(handleChange);
    }

    return () => {
      if ( mediaQuery.removeEventListener ) {
        mediaQuery.removeEventListener('change', handleChange);
      } else {
        mediaQuery.removeListener(handleChange);
      }
    };
  }, []);

  // テーマ変更時の処理
  useEffect(() => {
    // DOMにクラスを追加/削除
    const root = window.document.documentElement;
    root.classList.remove('light', 'dark');
    root.classList.add(effectiveTheme);

    // bodyにもクラスを追加（Tailwind CSS v4対応）
    document.body.classList.remove('light', 'dark');
    document.body.classList.add(effectiveTheme);

    // localStorageに保存
    localStorage.setItem('theme', theme);
  }, [ theme, effectiveTheme ]);

  const handleSetTheme = (newTheme : Theme) => {
    setTheme(newTheme);
  };

  const value : ThemeContextType = {
    theme,
    effectiveTheme,
    setTheme : handleSetTheme,
  };

  return (
    <ThemeContext.Provider value={ value }>
      { children }
    </ThemeContext.Provider>
  );
};