import Feather from '@expo/vector-icons/Feather';
import { format } from 'date-fns';
import { Card } from 'heroui-native';
import React from 'react';
import { View } from 'react-native';
import { TransactionWithCategory } from '../types';
import { AppText } from './app-text';

interface Props {
  transaction: TransactionWithCategory;
  onPress?: () => void;
}

export const TransactionCard: React.FC<Props> = ({ transaction, onPress }) => {
  const isExpense = transaction.type === 'expense';
  const formattedDate = format(new Date(transaction.date * 1000), 'MMM dd, yyyy');

  return (
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
  );
};