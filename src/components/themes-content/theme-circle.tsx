import { useThemeColor } from 'heroui-native';
import type React from 'react';
import { Pressable, Text, View } from 'react-native';

export type ThemeOption = {
  id: string;
  name: string;
  lightVariant: string;
  darkVariant: string;
  colors: { primary: string; secondary: string; tertiary: string };
};

export const availableThemes: ThemeOption[] = [
  {
    id: 'default',
    name: 'Default',
    lightVariant: 'light',
    darkVariant: 'dark',
    colors: {
      primary: '#006FEE',
      secondary: '#17C964',
      tertiary: '#F5A524',
    },
  },
  {
    id: 'lavender',
    name: 'Lavender',
    lightVariant: 'lavender-light',
    darkVariant: 'lavender-dark',
    colors: {
      primary: '#B569E0',
      secondary: '#64C9A8',
      tertiary: '#E8C765',
    },
  },
  {
    id: 'mint',
    name: 'Mint',
    lightVariant: 'mint-light',
    darkVariant: 'mint-dark',
    colors: {
      primary: '#5FC9AB',
      secondary: '#6DD597',
      tertiary: '#E5CF6C',
    },
  },
  {
    id: 'sky',
    name: 'Sky',
    lightVariant: 'sky-light',
    darkVariant: 'sky-dark',
    colors: {
      primary: '#6CA9D6',
      secondary: '#64C9B2',
      tertiary: '#E8C963',
    },
  },
];

export const ThemeCircle: React.FC<{
  theme: ThemeOption;
  isActive: boolean;
  onPress: () => void;
}> = ({ theme, isActive, onPress }) => {
  const themeColorAccent = useThemeColor('accent');

  return (
    <Pressable onPress={onPress} className="items-center">
      <View style={{ position: 'relative', padding: 4 }}>
        {/* Active ring */}
        {isActive && (
          <View
            style={{
              position: 'absolute',
              width: 68,
              height: 68,
              borderRadius: 34,
              borderWidth: 2,
              borderColor: themeColorAccent,
              top: 0,
              left: 0,
            }}
          />
        )}
        {/* Theme circle */}
        <View
          style={{
            width: 60,
            height: 60,
            borderRadius: 30,
            overflow: 'hidden',
            position: 'relative',
          }}
        >
          {/* First section - 50% */}
          <View
            style={{
              position: 'absolute',
              width: '100%',
              height: '100%',
              backgroundColor: theme.colors.primary,
            }}
          />

          {/* Second section - 25% */}
          <View
            style={{
              position: 'absolute',
              width: '100%',
              height: '50%',
              backgroundColor: theme.colors.secondary,
              bottom: 0,
            }}
          />

          {/* Third section - 25% */}
          <View
            style={{
              position: 'absolute',
              width: '50%',
              height: '50%',
              backgroundColor: theme.colors.tertiary,
              bottom: 0,
              right: 0,
            }}
          />
        </View>
      </View>
      <Text className="text-xs mt-2 text-foreground font-medium">{theme.name}</Text>
    </Pressable>
  );
};
