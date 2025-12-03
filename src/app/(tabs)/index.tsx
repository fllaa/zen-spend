import { useModal } from '@/src/contexts/modal-context';
import Feather from '@expo/vector-icons/Feather';
import { useRouter } from 'expo-router';
import { Button, useThemeColor } from 'heroui-native';
import React, { useEffect } from 'react';
import { RefreshControl, View } from 'react-native';
import { AddTransaction } from '../../components/add-transaction';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { SummaryCard } from '../../components/summary-card';
import { ThemeToggle } from '../../components/theme-toggle';
import { TransactionCard } from '../../components/transaction-card';
import { useStore } from '../../store';

export default function Dashboard() {
  const router = useRouter();
  const {
    initialize,
    fetchDashboardData,
    monthlySummary,
    recentTransactions,
    isLoading,
    removeTransaction
  } = useStore();
  const textColorReversed = useThemeColor('background');
  const modal = useModal();

  useEffect(() => {
    initialize();
  }, []);

  const onRefresh = React.useCallback(() => {
    fetchDashboardData();
  }, []);

  return (
    <View className="flex-1 bg-background">
      <ScreenScrollView
        refreshControl={
          <RefreshControl refreshing={isLoading} onRefresh={onRefresh} />
        }
      >
        <View className="px-4 pt-12 pb-4">
          <View className="flex-row items-start justify-between mb-6">
            <View>
              <AppText className="text-3xl font-bold mb-1 text-foreground">Dashboard</AppText>
              <AppText className="text-muted">Overview of your finances</AppText>
            </View>
            <ThemeToggle />
          </View>

          {/* Summary Cards */}
          <View className="flex-row gap-3 mb-6">
            <SummaryCard 
              title="Income" 
              amount={monthlySummary.income} 
              type="income" 
            />
            <SummaryCard 
              title="Expense" 
              amount={monthlySummary.expense} 
              type="expense" 
            />
          </View>
          
          <View className="mb-8">
             <SummaryCard 
              title="Total Balance" 
              amount={monthlySummary.balance} 
              type="balance" 
            />
          </View>

          {/* Recent Transactions Header */}
          <View className="flex-row items-center justify-between mb-4">
            <AppText className="text-xl font-semibold text-foreground">Recent Transactions</AppText>
            <Button
              variant="ghost"
              size="sm"
              onPress={() => router.push('/(tabs)/history' as any)}
            >
              See All
            </Button>
          </View>

          {/* Transactions List */}
          {recentTransactions.length === 0 ? (
            <View className="items-center justify-center py-10 border border-dashed border-zinc-300 dark:border-zinc-700 rounded-xl">
              <Feather name="inbox" size={40} className="text-muted mb-2" />
              <AppText className="text-muted">No transactions yet</AppText>
            </View>
          ) : (
            recentTransactions.map((transaction) => (
              <TransactionCard
                key={transaction.id}
                transaction={transaction}
                onDelete={() => removeTransaction(transaction.id)}
              />
            ))
          )}
        </View>
      </ScreenScrollView>

      {/* Floating Action Button */}
      <View className="absolute bottom-8 right-8">
        <Button 
          className="w-14 h-14 rounded-full shadow-lg bg-foreground items-center justify-center p-0"
          onPress={() => modal.open(<AddTransaction close={modal.close} />)}
        >
          <Feather name="plus" size={24} color={textColorReversed} />
        </Button>
      </View>
    </View>
  );
}