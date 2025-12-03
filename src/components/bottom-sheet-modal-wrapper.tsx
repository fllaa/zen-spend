import { BottomSheetBackdrop, BottomSheetModal, BottomSheetView } from '@gorhom/bottom-sheet';
import { Button, useThemeColor } from 'heroui-native';
import React, { useCallback, useMemo } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { useAppTheme } from '../contexts/app-theme-context';
import { useModal } from '../contexts/modal-context';

export const BottomSheetModalWrapper = () => {
  const { isDark } = useAppTheme();
  const themeColorBackground = useThemeColor('background');
  const snapPoints = useMemo(() => ['85%'], []);
  const modal = useModal();

const handleSheetChanges = useCallback((index: number) => {
    if (index === -1) {
      modal.close();
    }
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const renderBackdrop = useCallback(
    (props: any) => (
      <BottomSheetBackdrop
        {...props}
        disappearsOnIndex={-1}
        appearsOnIndex={0}
        opacity={0.5}
      />
    ),
    []
  );

  return (
    <BottomSheetModal
      ref={modal.ref}
      index={0}
      snapPoints={snapPoints}
      backdropComponent={renderBackdrop}
      onChange={handleSheetChanges}
      backgroundStyle={{ backgroundColor: themeColorBackground }}
      handleIndicatorStyle={{ backgroundColor: isDark ? '#555' : '#ccc' }}
      onDismiss={modal.close}
    >
      <BottomSheetView style={styles.contentContainer}>
        {modal.child ?? (
          <View className="flex-1 items-center">
            <Text>Not Found</Text>

            <Button onPress={modal.close}>Close</Button>
          </View>
        )}
      </BottomSheetView>
    </BottomSheetModal>
  );
}

const styles = StyleSheet.create({
  contentContainer: {
    flex: 1,
  },
});