import {
  Inter_400Regular,
  Inter_500Medium,
  Inter_600SemiBold,
  Inter_700Bold,
  useFonts,
} from '@expo-google-fonts/inter';
import { BottomSheetModalProvider } from '@gorhom/bottom-sheet';
import { Slot } from 'expo-router';
import { HeroUINativeProvider } from 'heroui-native';
import { useCallback } from 'react';
import { I18nextProvider } from 'react-i18next';
import { StyleSheet } from 'react-native';
import { GestureHandlerRootView } from 'react-native-gesture-handler';
import { KeyboardAvoidingView, KeyboardProvider } from 'react-native-keyboard-controller';
import { configureReanimatedLogger, ReanimatedLogLevel } from 'react-native-reanimated';
import '../../global.css';
import { BottomSheetModalWrapper } from '../components/bottom-sheet-modal-wrapper';
import { AppThemeProvider } from '../contexts/app-theme-context';
import { ModalProvider } from '../contexts/modal-context';
import i18n from '../i18n';

configureReanimatedLogger({
  level: ReanimatedLogLevel.warn,
  strict: false,
});

/**
 * Component that wraps app content inside KeyboardProvider
 * Contains the contentWrapper and HeroUINativeProvider configuration
 */
function AppContent() {
  const contentWrapper = useCallback(
    (children: React.ReactNode) => (
      <KeyboardAvoidingView pointerEvents="box-none" behavior="padding" keyboardVerticalOffset={12} className="flex-1">
        {children}
      </KeyboardAvoidingView>
    ),
    [],
  );

  return (
    <I18nextProvider i18n={i18n}>
      <AppThemeProvider>
        <HeroUINativeProvider
          config={{
            toast: {
              contentWrapper,
            },
          }}
        >
          <BottomSheetModalProvider>
            <ModalProvider>
              <Slot />
              <BottomSheetModalWrapper />
            </ModalProvider>
          </BottomSheetModalProvider>
        </HeroUINativeProvider>
      </AppThemeProvider>
    </I18nextProvider>
  );
}

export default function Layout() {
  const fonts = useFonts({
    Inter_400Regular,
    Inter_500Medium,
    Inter_600SemiBold,
    Inter_700Bold,
  });

  if (!fonts) {
    return null;
  }

  return (
    <GestureHandlerRootView style={styles.root}>
      <KeyboardProvider>
        <AppContent />
      </KeyboardProvider>
    </GestureHandlerRootView>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
  },
});
