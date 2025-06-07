import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'

// 初期テーマ設定
const savedTheme = localStorage.getItem('theme');
const systemDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
const effectiveTheme = savedTheme === 'system' || !savedTheme 
  ? (systemDark ? 'dark' : 'light') 
  : savedTheme;

// HTMLとbodyの両方にクラスを追加
document.documentElement.classList.remove('light', 'dark');
document.documentElement.classList.add(effectiveTheme);
document.body.classList.remove('light', 'dark');
document.body.classList.add(effectiveTheme);

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
