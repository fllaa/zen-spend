import { ThemeCircle, type ThemeOption, availableThemes } from '@/src/components/themes-content/theme-circle';
import { useHeaderHeight } from '@react-navigation/elements';
import React from 'react';
import { View } from 'react-native';
import { KeyboardAwareScrollView } from 'react-native-keyboard-controller';
import { CardContent } from '../../../components/themes-content/card-content';
import { CheckboxContent } from '../../../components/themes-content/checkbox-content';
import { RadioGroupContent } from '../../../components/themes-content/radio-group-content';
import { SwitchContent } from '../../../components/themes-content/switch-content';
import { TextInputContent } from '../../../components/themes-content/text-input-content';
import { useAppTheme } from '../../../contexts/app-theme-context';

export default function Themes() {
  const { currentTheme, setTheme, isLight } = useAppTheme();
  const headerHeight = useHeaderHeight();

  const getCurrentThemeId = () => {
    if (currentTheme === 'light' || currentTheme === 'dark') return 'default';
    if (currentTheme.startsWith('lavender')) return 'lavender';
    if (currentTheme.startsWith('mint')) return 'mint';
    if (currentTheme.startsWith('sky')) return 'sky';
    return 'default';
  };

  const handleThemeSelect = (theme: ThemeOption) => {
    const variant = isLight ? theme.lightVariant : theme.darkVariant;
    setTheme(variant as any);
  };

  return (
    <KeyboardAwareScrollView
      className="flex-1"
      contentContainerClassName="gap-12 px-5"
      contentContainerStyle={{
        paddingTop: headerHeight,
        paddingBottom: 12,
      }}
      bottomOffset={60}
    >
      <View className="flex-row justify-around pt-6">
        {availableThemes.map((theme) => (
          <ThemeCircle
            key={theme.id}
            theme={theme}
            isActive={getCurrentThemeId() === theme.id}
            onPress={() => handleThemeSelect(theme)}
          />
        ))}
      </View>
      <CardContent />
      <SwitchContent />
      <CheckboxContent />
      <RadioGroupContent />
      <TextInputContent />
    </KeyboardAwareScrollView>
  );
}
