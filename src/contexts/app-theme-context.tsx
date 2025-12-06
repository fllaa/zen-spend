import type React from 'react';
import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { Appearance } from 'react-native';
import { Uniwind } from 'uniwind';
import { useSettingsStore } from '../store/settings';

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

interface AppThemeContextType {
  selectedTheme: ThemeName;
  currentTheme: string;
  isLight: boolean;
  isDark: boolean;
  setTheme: (theme: ThemeName) => void;
  toggleTheme: () => void;
}

const AppThemeContext = createContext<AppThemeContextType | undefined>(undefined);

export const AppThemeProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { theme: selectedTheme, setTheme: setSelectedTheme } = useSettingsStore();
  const [systemColorScheme, setSystemColorScheme] = useState(Appearance.getColorScheme());

  const setTheme = useCallback(
    (newTheme: ThemeName) => {
      setSelectedTheme(newTheme);
      if (newTheme === 'system') {
        Uniwind.setTheme(systemColorScheme === 'dark' ? 'dark' : 'light');
      } else {
        Uniwind.setTheme(newTheme);
      }
      // eslint-disable-next-line react-hooks/exhaustive-deps
    },
    [systemColorScheme, setSelectedTheme],
  );

  useEffect(() => {
    const subscription = Appearance.addChangeListener(({ colorScheme }) => {
      setSystemColorScheme(colorScheme);
    });
    setTheme(selectedTheme);
    return () => subscription?.remove();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedTheme, setTheme]);

  const currentTheme = useMemo(() => {
    if (selectedTheme === 'system') {
      return systemColorScheme === 'dark' ? 'dark' : 'light';
    }
    return selectedTheme;
  }, [selectedTheme, systemColorScheme]);

  const isLight = useMemo(() => {
    return currentTheme === 'light' || currentTheme.endsWith('-light');
  }, [currentTheme]);

  const isDark = useMemo(() => {
    return currentTheme === 'dark' || currentTheme.endsWith('-dark');
  }, [currentTheme]);

  const toggleTheme = useCallback(() => {
    const actualTheme = currentTheme;
    switch (actualTheme) {
      case 'light':
        setTheme('dark');
        break;
      case 'dark':
        setTheme('light');
        break;
      case 'lavender-light':
        setTheme('lavender-dark');
        break;
      case 'lavender-dark':
        setTheme('lavender-light');
        break;
      case 'mint-light':
        setTheme('mint-dark');
        break;
      case 'mint-dark':
        setTheme('mint-light');
        break;
      case 'sky-light':
        setTheme('sky-dark');
        break;
      case 'sky-dark':
        setTheme('sky-light');
        break;
    }
  }, [currentTheme, setTheme]);

  const value = useMemo(
    () => ({
      selectedTheme,
      currentTheme,
      isLight,
      isDark,
      setTheme,
      toggleTheme,
    }),
    [selectedTheme, currentTheme, isLight, isDark, setTheme, toggleTheme],
  );

  return <AppThemeContext.Provider value={value}>{children}</AppThemeContext.Provider>;
};

export const useAppTheme = () => {
  const context = useContext(AppThemeContext);
  if (!context) {
    throw new Error('useAppTheme must be used within AppThemeProvider');
  }
  return context;
};
