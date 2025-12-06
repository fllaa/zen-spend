import { cn, Divider, FormField, Select, Surface, Switch } from 'heroui-native';
import React from 'react';
import { useTranslation } from 'react-i18next';
import { View } from 'react-native';
import { availableThemes, ThemeCircle, type ThemeOption } from '@/src/components/themes-content/theme-circle';
import { type ThemeName, useSettingsStore } from '@/src/store/settings';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { useAppTheme } from '../../contexts/app-theme-context';

type NumberFormat = 'dot' | 'comma';
type Language = 'en' | 'id';

const currencies = [
  { value: 'USD', label: 'USD ($)' },
  { value: 'IDR', label: 'IDR (Rp)' },
  { value: 'JPY', label: 'JPY (¥)' },
];

const numberFormats: { value: NumberFormat; label: string }[] = [
  { value: 'dot', label: '1,000.00' },
  { value: 'comma', label: '1.000,00' },
];

const languages: { value: Language; label: string }[] = [
  { value: 'en', label: 'English' },
  { value: 'id', label: 'Bahasa Indonesia' },
];

const styles: { value: string; label: string }[] = [
  { value: 'default', label: 'Default' },
  { value: 'bordered', label: 'Bordered' },
];

export default function Settings() {
  const { t } = useTranslation();
  const { setTheme: setAppTheme, isLight, isDark, toggleTheme } = useAppTheme();
  const {
    currency,
    setCurrency,
    numberFormat,
    setNumberFormat,
    language,
    setLanguage,
    theme,
    setTheme,
    style,
    setStyle,
  } = useSettingsStore();

  const getCurrentThemeId = () => {
    if (theme === 'light' || theme === 'dark') return 'default';
    if (theme.startsWith('lavender')) return 'lavender';
    if (theme.startsWith('mint')) return 'mint';
    if (theme.startsWith('sky')) return 'sky';
    return 'default';
  };

  const handleLanguageChange = (newLang: Language) => {
    setLanguage(newLang);
  };

  const handleThemeChange = (theme: ThemeOption) => {
    const variant = (isLight ? theme.lightVariant : theme.darkVariant) as ThemeName;
    setTheme(variant);
    setAppTheme(variant);
  };

  return (
    <View className="flex-1 bg-background">
      <ScreenScrollView>
        <View className="pt-12 pb-4">
          <View className="flex-row items-start justify-between mb-6">
            <View>
              <AppText className="text-3xl font-bold mb-1 text-foreground">{t('settings')}</AppText>
              <AppText className="text-muted">{t('customizeAppExperience')}</AppText>
            </View>
          </View>

          {/* Appearance */}
          <View className="mb-8">
            <AppText className="text-xl font-semibold text-foreground mb-4">{t('appearance')}</AppText>
            <Surface className={cn(style === 'bordered' ? 'border border-border' : 'border-0')}>
              {/* Theme Picker */}
              <View className="flex-row justify-around">
                {availableThemes.map((theme) => (
                  <ThemeCircle
                    key={theme.id}
                    theme={theme}
                    isActive={getCurrentThemeId() === theme.id}
                    onPress={() => handleThemeChange(theme)}
                  />
                ))}
              </View>
              <Divider className="my-4" />

              {/* Dark Mode */}
              <FormField isSelected={isDark} onSelectedChange={toggleTheme}>
                <FormField.Content>
                  <FormField.Title>{t('darkMode')}</FormField.Title>
                  <FormField.Description>{t('darkModeDescription')}</FormField.Description>
                </FormField.Content>
                <FormField.Indicator>
                  <Switch />
                </FormField.Indicator>
              </FormField>
              <Divider className="my-4" />

              {/* Style */}
              <Select
                value={styles.find((f) => f.value === style)}
                onValueChange={(newValue) => setStyle((newValue?.value as string) || 'default')}
              >
                <FormField>
                  <FormField.Content>
                    <FormField.Title>{t('style')}</FormField.Title>
                    <FormField.Description>{t('styleDescription')}</FormField.Description>
                  </FormField.Content>
                  <FormField.Indicator>
                    <Select.Trigger>
                      <View className="bg-background h-10 px-2 rounded-xl justify-center border border-border">
                        <Select.Value placeholder="Select style" className="text-sm" />
                      </View>
                    </Select.Trigger>
                  </FormField.Indicator>
                </FormField>
                <Select.Portal>
                  <Select.Overlay />
                  <Select.Content width="full" className="bg-background text-foreground">
                    {styles.map((style, index) => (
                      <React.Fragment key={style.value}>
                        <Select.Item value={style.value} label={style.label} />
                        {index < styles.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </Select.Content>
                </Select.Portal>
              </Select>
            </Surface>
          </View>

          {/* Currency & Localization */}
          <View className="mb-8">
            <AppText className="text-xl font-semibold text-foreground mb-4">{t('currencyAndLocalization')}</AppText>
            <Surface className={cn(style === 'bordered' ? 'border border-border' : 'border-0')}>
              {/* Currency */}
              <Select
                value={currencies.find((c) => c.value === currency)}
                onValueChange={(newValue) => setCurrency(newValue?.value || 'USD')}
              >
                <FormField>
                  <FormField.Content>
                    <FormField.Title>{t('currency')}</FormField.Title>
                    <FormField.Description>{t('currencyDescription')}</FormField.Description>
                  </FormField.Content>
                  <FormField.Indicator>
                    <Select.Trigger>
                      <View className="bg-background h-10 px-2 rounded-xl justify-center border border-border">
                        <Select.Value placeholder="Select currency" className="text-sm" />
                      </View>
                    </Select.Trigger>
                  </FormField.Indicator>
                </FormField>
                <Select.Portal>
                  <Select.Overlay />
                  <Select.Content width="full" className="bg-background text-foreground">
                    {currencies.map((curr, index) => (
                      <React.Fragment key={curr.value}>
                        <Select.Item value={curr.value} label={curr.label} />
                        {index < currencies.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </Select.Content>
                </Select.Portal>
              </Select>
              <Divider className="my-4" />

              {/* Number Formatting */}
              <Select
                value={numberFormats.find((f) => f.value === numberFormat)}
                onValueChange={(newValue) => setNumberFormat((newValue?.value as NumberFormat) || 'dot')}
              >
                <FormField>
                  <FormField.Content>
                    <FormField.Title>{t('numberFormatting')}</FormField.Title>
                    <FormField.Description>{t('numberFormattingDescription')}</FormField.Description>
                  </FormField.Content>
                  <FormField.Indicator>
                    <Select.Trigger>
                      <View className="bg-background h-10 px-2 rounded-xl justify-center border border-border">
                        <Select.Value placeholder="Select number format" className="text-sm" />
                      </View>
                    </Select.Trigger>
                  </FormField.Indicator>
                </FormField>
                <Select.Portal>
                  <Select.Overlay />
                  <Select.Content width="full" className="bg-background text-foreground">
                    {numberFormats.map((format, index) => (
                      <React.Fragment key={format.value}>
                        <Select.Item value={format.value} label={format.label} />
                        {index < numberFormats.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </Select.Content>
                </Select.Portal>
              </Select>
              <Divider className="my-4" />

              {/* Language */}
              <Select
                value={languages.find((l) => l.value === language)}
                onValueChange={(newValue) => handleLanguageChange((newValue?.value as Language) || 'en')}
              >
                <FormField>
                  <FormField.Content>
                    <FormField.Title>{t('language')}</FormField.Title>
                    <FormField.Description>{t('languageDescription')}</FormField.Description>
                  </FormField.Content>
                  <FormField.Indicator>
                    <Select.Trigger>
                      <View className="bg-background h-10 px-2 rounded-xl justify-center border border-border">
                        <Select.Value placeholder="Select language" className="text-sm" />
                      </View>
                    </Select.Trigger>
                  </FormField.Indicator>
                </FormField>
                <Select.Portal>
                  <Select.Overlay />
                  <Select.Content width="full" className="bg-background text-foreground">
                    {languages.map((lang, index) => (
                      <React.Fragment key={lang.value}>
                        <Select.Item value={lang.value} label={lang.label} />
                        {index < languages.length - 1 && <Divider />}
                      </React.Fragment>
                    ))}
                  </Select.Content>
                </Select.Portal>
              </Select>
            </Surface>
          </View>
        </View>
      </ScreenScrollView>
    </View>
  );
}
