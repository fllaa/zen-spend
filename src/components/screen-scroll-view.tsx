import { useHeaderHeight } from '@react-navigation/elements';
import { cn } from 'heroui-native';
import type { FC, PropsWithChildren } from 'react';
import { ScrollView, type ScrollViewProps } from 'react-native';
import Animated, { type AnimatedProps } from 'react-native-reanimated';
import { useSafeAreaInsets } from 'react-native-safe-area-context';

const AnimatedScrollView = Animated.createAnimatedComponent(ScrollView);

interface Props extends AnimatedProps<ScrollViewProps> {
  className?: string;
  contentContainerClassName?: string;
  useHeaderHeight?: boolean;
}

export const ScreenScrollView: FC<PropsWithChildren<Props>> = ({
  children,
  className,
  contentContainerClassName,
  useHeaderHeight: shouldCalculateHeaderHeight = true,
  ...props
}) => {
  const insets = useSafeAreaInsets();

  // Always call the hook, but handle the error if context is missing
  let headerHeight = 0;
  try {
    // We can't conditionally call hooks, so we just wrap the call in try/catch
    // However, useHeaderHeight might throw if context is missing.
    // A better approach is to check if we are in a screen that provides it,
    // but for now let's just default to 0 if it fails or if disabled.
    // biome-ignore lint/correctness/useHookAtTopLevel: this is not conditionally called
    const height = useHeaderHeight();
    if (shouldCalculateHeaderHeight) {
      headerHeight = height;
    }
  } catch (_error) {
    // Ignore error if not inside a navigator with header
  }

  return (
    <AnimatedScrollView
      className={cn('bg-background', className)}
      contentContainerClassName={cn('px-5', contentContainerClassName)}
      contentContainerStyle={{
        paddingTop: headerHeight,
        paddingBottom: insets.bottom + 32,
      }}
      showsVerticalScrollIndicator={false}
      {...props}
    >
      {children}
    </AnimatedScrollView>
  );
};
