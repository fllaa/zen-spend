import AsyncStorage from '@react-native-async-storage/async-storage';
import { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import type { MMKV } from 'react-native-mmkv';
import { create } from 'zustand';
import { createJSONStorage, persist, type StateStorage } from 'zustand/middleware';

let mmkv: MMKV | null = null;

try {
  // eslint-disable-next-line @typescript-eslint/no-require-imports
  const { createMMKV } = require('react-native-mmkv');
  mmkv = createMMKV({
    id: 'settings',
  });
} catch (e) {
  console.log(e);
}

const MMKVStorage: StateStorage = {
  setItem: (name, value) => {
    return mmkv?.set(name, value)
  },
  getItem: (name) => {
    const value = mmkv?.getString(name)
    return value ?? null
  },
  removeItem: (name) => {
    return mmkv?.remove(name)
  },
}

type NumberFormat = 'dot' | 'comma';
type Language = 'en' | 'id';
export type ThemeName =
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
  style: string;
  setCurrency: (currency: string) => void;
  setNumberFormat: (format: NumberFormat) => void;
  setLanguage: (language: Language) => void;
  setTheme: (theme: ThemeName) => void;
  setStyle: (style: string) => void;
}

const _useSettingsStore = create<SettingsState>()(
  persist(
    (set) => ({
      currency: 'USD',
      numberFormat: 'dot',
      language: 'en',
      theme: 'light',
      style: 'default',
      setCurrency: (currency) => set({ currency }),
      setNumberFormat: (numberFormat) => set({ numberFormat }),
      setLanguage: (language) => set({ language }),
      setTheme: (theme) => set({ theme }),
      setStyle: (style) => set({ style }),
    }),
    {
      name: 'settings-storage',
      storage: createJSONStorage(() => (mmkv ? MMKVStorage : AsyncStorage)),
    },
  ),
);

export const useSettingsStore = () => {
  const store = _useSettingsStore();
  const { i18n } = useTranslation();
  useEffect(() => {
    i18n.changeLanguage(store.language);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [store.language, i18n.changeLanguage]);
  return store;
};
