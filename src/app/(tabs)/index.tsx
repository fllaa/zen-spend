import Feather from '@expo/vector-icons/Feather';
import { useRouter } from 'expo-router';
import { Button, useThemeColor } from 'heroui-native';
import React, { useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { RefreshControl, View } from 'react-native';
import { useModal } from '@/src/contexts/modal-context';
import { AddTransaction } from '../../components/add-transaction';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { SummaryCard } from '../../components/summary-card';
import { TransactionCard } from '../../components/transaction-card';
import { useStore } from '../../store';

export default function Dashboard() {
  const { t } = useTranslation();
  const router = useRouter();
  const { initialize, fetchDashboardData, monthlySummary, recentTransactions, isLoading, removeTransaction } =
    useStore();
  const textColor = useThemeColor('foreground');
  const backgroundColor = useThemeColor('background');
  const modal = useModal();

  useEffect(() => {
    initialize();
  }, [initialize]);

  const onRefresh = React.useCallback(() => {
    fetchDashboardData();
  }, [fetchDashboardData]);

  return (
    <View className="flex-1 bg-background">
      <ScreenScrollView refreshControl={<RefreshControl refreshing={isLoading} onRefresh={onRefresh} />}>
        <View className="pt-12 pb-4">
          <View className="flex-row items-start justify-between mb-6">
            <View>
              <AppText className="text-3xl font-bold mb-1 text-foreground">{t('dashboard')}</AppText>
              <AppText className="text-muted">{t('overviewOfFinances')}</AppText>
            </View>
          </View>

          {/* Summary Cards */}
          <View className="flex-row gap-3 mb-6">
            <SummaryCard title={t('income')} amount={monthlySummary.income} type="income" />
            <SummaryCard title={t('expense')} amount={monthlySummary.expense} type="expense" />
          </View>

          <View className="mb-8">
            <SummaryCard title={t('totalBalance')} amount={monthlySummary.balance} type="balance" />
          </View>

          {/* Recent Transactions Header */}
          <View className="flex-row items-center justify-between mb-4">
            <AppText className="text-xl font-semibold text-foreground">{t('recentTransactions')}</AppText>
            <Button variant="ghost" size="sm" onPress={() => router.push('/(tabs)/history')}>
              {t('seeAll')}
            </Button>
          </View>

          {/* Transactions List */}
          {recentTransactions.length === 0 ? (
            <View className="items-center justify-center py-10 border border-dashed border-border rounded-xl">
              <Feather name="inbox" size={40} className="text-muted mb-2" color={textColor} />
              <AppText className="text-muted">{t('noTransactionsYet')}</AppText>
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
          <Feather name="plus" size={24} color={backgroundColor} />
        </Button>
      </View>
    </View>
  );
}
