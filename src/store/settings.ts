import AsyncStorage from '@react-native-async-storage/async-storage';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { MMKV } from 'react-native-mmkv';
import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';

let mmkv: MMKV | null = null;

try {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const { createMMKV } = require('react-native-mmkv');
  mmkv = createMMKV();
} catch (e) {
  console.log(e);
}

const customStorage = {
  getItem: (name: string) => {
    const value = mmkv?.getString(name);
    return value ? JSON.parse(value) : null;
  },
  setItem: (name: string, value: string) => {
    mmkv?.set(name, value);
  },
  removeItem: (name: string) => {
    mmkv?.remove(name);
  },
};

type NumberFormat = 'dot' | 'comma';
type Language = 'en' | 'id';
type ThemeName =
  | 'light'
  | 'dark'
  | 'system'
  | 'lavender-light'
  | 'lavender-dark'
  | 'mint-light'
  | 'mint-dark'
  | 'sky-light'
  | 'sky-dark';

interface SettingsState {
  currency: string;
  numberFormat: NumberFormat;
  language: Language;
  theme: ThemeName;
  setCurrency: (currency: string) => void;
  setNumberFormat: (format: NumberFormat) => void;
  setLanguage: (language: Language) => void;
  setTheme: (theme: ThemeName) => void;
}

const _useSettingsStore = create<SettingsState>()(
  persist(
    (set) => ({
      currency: 'USD',
      numberFormat: 'dot',
      language: 'en',
      theme: 'light',
      setCurrency: (currency) => set({ currency }),
      setNumberFormat: (numberFormat) => set({ numberFormat }),
      setLanguage: (language) => set({ language }),
      setTheme: (theme) => set({ theme }),
    }),
    {
      name: 'settings-storage',
      storage: createJSONStorage(() => mmkv ? customStorage : AsyncStorage),
    }
  )
);

export const useSettingsStore = () => {
  const store = _useSettingsStore();
  const { i18n } = useTranslation();
  useEffect(() => {
    i18n.changeLanguage(store.language);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [store.language])
  return store
}