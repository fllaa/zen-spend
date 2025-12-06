import { Feather } from '@expo/vector-icons';
import { format } from 'date-fns';
import { cn, Surface, useThemeColor } from 'heroui-native';
import type React from 'react';
import { useRef } from 'react';
import { Animated, TouchableOpacity, View } from 'react-native';
import Swipeable, { type SwipeableMethods } from 'react-native-gesture-handler/ReanimatedSwipeable';
import { formatCurrency } from '../helpers/currency-helper';
import { useSettingsStore } from '../store/settings';
import type { TransactionWithCategory } from '../types';
import { AppText } from './app-text';

interface Props {
  transaction: TransactionWithCategory;

  onDelete?: () => void;
}

export const TransactionCard: React.FC<Props> = ({ transaction, onDelete }) => {
  const { currency, numberFormat, style } = useSettingsStore();
  const isExpense = transaction.type === 'expense';
  const formattedDate = format(new Date(transaction.date * 1000), 'MMM dd, yyyy');

  const swipeableRef = useRef<SwipeableMethods>(null);
  const translateX = new Animated.Value(0);

  const dangerForegroundColor = useThemeColor('danger-foreground')

  const handleDelete = () => {
    if (swipeableRef.current) {
      swipeableRef.current.close();
    }
    Animated.timing(translateX, {
      toValue: -800,
      duration: 300,
      useNativeDriver: true,
    }).start(() => {
      if (onDelete) {
        onDelete();
      }
    });
  };

  const renderRightActions = () => {
    return (
      <View className={cn("flex-row items-center justify-center bg-danger w-24 h-19 -ml-8 pl-5.5 rounded-r-3xl", style === 'bordered' ? 'border border-danger-soft' : 'border-0')}>
        <TouchableOpacity className="items-center justify-center w-full h-full" onPress={handleDelete}>
          <Feather name="trash-2" size={24} color={dangerForegroundColor} />
        </TouchableOpacity>
      </View>
    );
  };

  return (
    <Animated.View style={{ transform: [{ translateX }] }}>
      <Swipeable ref={swipeableRef} renderRightActions={renderRightActions} overshootRight={false}>
        <Surface className={cn(style === 'bordered' ? 'border border-border' : 'border-0', 'p-4 mb-3')}>
          <View className="flex-row items-center justify-between">
            <View className="flex-row items-center gap-3">
              <View
                className="w-10 h-10 rounded-full items-center justify-center"
                style={{ backgroundColor: `${transaction.category.color}20` }}
              >
                {/** biome-ignore lint/suspicious/noExplicitAny: cannot import type FeatherProps */}
                <Feather name={transaction.category.icon as any} size={20} color={transaction.category.color} />
              </View>
              <View>
                <AppText className="font-medium text-base text-foreground">{transaction.category.name}</AppText>
                <AppText className="text-sm text-muted">{formattedDate}</AppText>
              </View>
            </View>

            <View className="items-end">
              <AppText className={`font-bold text-base ${isExpense ? 'text-danger' : 'text-success'}`}>
                {isExpense ? '-' : '+'}
                {formatCurrency(transaction.amount, currency, numberFormat)}
              </AppText>
              {transaction.note ? (
                <AppText className="text-xs text-muted max-w-[100px]" numberOfLines={1}>
                  {transaction.note}
                </AppText>
              ) : null}
            </View>
          </View>
        </Surface>
      </Swipeable>
    </Animated.View>
  );
};
