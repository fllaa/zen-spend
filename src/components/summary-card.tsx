import Feather from '@expo/vector-icons/Feather';
import { cn, Surface } from 'heroui-native';
import type React from 'react';
import { View } from 'react-native';
import { formatCurrency } from '../helpers/currency-helper';
import { useSettingsStore } from '../store/settings';
import { AppText } from './app-text';

interface Props {
  title: string;
  amount: number;
  type: 'income' | 'expense' | 'balance';
}

export const SummaryCard: React.FC<Props> = ({ title, amount, type }) => {
  const { currency, numberFormat, style } = useSettingsStore();
  const getIcon = () => {
    switch (type) {
      case 'income':
        return 'arrow-down-left';
      case 'expense':
        return 'arrow-up-right';
      default:
        return 'dollar-sign';
    }
  };

  const getColor = () => {
    switch (type) {
      case 'income':
        return 'text-success';
      case 'expense':
        return 'text-danger';
      default:
        return 'text-accent';
    }
  };

  return (
    <Surface className={cn(style === 'bordered' ? 'border border-border' : 'border-0', 'flex-1 p-3')}>
      <View className="flex-row items-center gap-2 mb-2">
        <View
          className={`p-1.5 rounded-full bg-opacity-10 ${type === 'income' ? 'bg-success' : type === 'expense' ? 'bg-danger' : 'bg-accent'}`}
        >
          <Feather name={getIcon()} size={14} className={getColor()} />
        </View>
        <AppText className="text-xs text-muted font-medium uppercase tracking-wider">{title}</AppText>
      </View>
      <AppText className="text-lg font-bold text-foreground">{formatCurrency(amount, currency, numberFormat)}</AppText>
    </Surface>
  );
};
