import Feather from '@expo/vector-icons/Feather';
import { format } from 'date-fns';
import { Card } from 'heroui-native';
import React, { useRef } from 'react';
import { Animated, TouchableOpacity, View } from 'react-native';
import Swipeable from 'react-native-gesture-handler/ReanimatedSwipeable';
import { TransactionWithCategory } from '../types';
import { AppText } from './app-text';

interface Props {
  transaction: TransactionWithCategory;
  onPress?: () => void;
  onDelete?: () => void;
}

export const TransactionCard: React.FC<Props> = ({ transaction, onPress, onDelete }) => {
  const isExpense = transaction.type === 'expense';
  const formattedDate = format(new Date(transaction.date * 1000), 'MMM dd, yyyy');

  const swipeableRef = useRef<any>(null);
  const translateX = new Animated.Value(0);

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
      <View className="flex-row items-center justify-center bg-red-500 w-24 h-18.5 -ml-8 pl-5.5 rounded-r-3xl">
        <TouchableOpacity
          className="items-center justify-center w-full h-full"
          onPress={handleDelete}
        >
          <Feather name="trash-2" size={24} color="white" />
        </TouchableOpacity>
      </View>
    );
  };

  return (
    <Animated.View style={{ transform: [{ translateX }] }}>
      <Swipeable
        ref={swipeableRef}
        renderRightActions={renderRightActions}
        overshootRight={false}
      >
        <Card className="p-4 mb-3 border border-zinc-200 dark:border-zinc-800 bg-white dark:bg-zinc-900">
          <View className="flex-row items-center justify-between">
            <View className="flex-row items-center gap-3">
              <View
                className="w-10 h-10 rounded-full items-center justify-center"
                style={{ backgroundColor: transaction.category.color + '20' }}
              >
                <Feather
                  name={transaction.category.icon as any}
                  size={20}
                  color={transaction.category.color}
                />
              </View>
              <View>
                <AppText className="font-medium text-base text-foreground">
                  {transaction.category.name}
                </AppText>
                <AppText className="text-sm text-muted">
                  {formattedDate}
                </AppText>
              </View>
            </View>

            <View className="items-end">
              <AppText
                className={`font-bold text-base ${isExpense ? 'text-red-500' : 'text-green-500'}`}
              >
                {isExpense ? '-' : '+'}${transaction.amount.toFixed(2)}
              </AppText>
              {transaction.note ? (
                <AppText className="text-xs text-muted max-w-[100px]" numberOfLines={1}>
                  {transaction.note}
                </AppText>
              ) : null}
            </View>
          </View>
        </Card>
      </Swipeable>
    </Animated.View>
  );
};