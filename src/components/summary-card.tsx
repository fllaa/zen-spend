import Feather from '@expo/vector-icons/Feather';
import { Card } from 'heroui-native';
import React from 'react';
import { View } from 'react-native';
import { AppText } from './app-text';

interface Props {
  title: string;
  amount: number;
  type: 'income' | 'expense' | 'balance';
}

export const SummaryCard: React.FC<Props> = ({ title, amount, type }) => {
  const getIcon = () => {
    switch (type) {
      case 'income': return 'arrow-down-left';
      case 'expense': return 'arrow-up-right';
      default: return 'dollar-sign';
    }
  };

  const getColor = () => {
    switch (type) {
      case 'income': return 'text-green-500';
      case 'expense': return 'text-red-500';
      default: return 'text-blue-500';
    }
  };

  return (
    <Card className="flex-1 p-3 border border-border bg-surface">
      <View className="flex-row items-center gap-2 mb-2">
        <View className={`p-1.5 rounded-full bg-opacity-10 ${type === 'income' ? 'bg-green-500' : type === 'expense' ? 'bg-red-500' : 'bg-blue-500'}`}>
          <Feather
            name={getIcon()}
            size={14}
            className={getColor()}
          />
        </View>
        <AppText className="text-xs text-muted font-medium uppercase tracking-wider">
          {title}
        </AppText>
      </View>
      <AppText className="text-lg font-bold text-foreground">
        ${amount.toFixed(2)}
      </AppText>
    </Card>
  );
};