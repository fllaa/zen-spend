import Feather from '@expo/vector-icons/Feather';
import { TextField } from 'heroui-native';
import React, { useEffect, useState } from 'react';
import { RefreshControl, View } from 'react-native';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { ThemeToggle } from '../../components/theme-toggle';
import { TransactionCard } from '../../components/transaction-card';
import { getTransactions } from '../../db';
import { useStore } from '../../store';
import { TransactionWithCategory } from '../../types';

export default function History() {
  const { isLoading } = useStore();
  const [transactions, setTransactions] = useState<TransactionWithCategory[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [filteredTransactions, setFilteredTransactions] = useState<TransactionWithCategory[]>([]);
  const [isRefreshing, setIsRefreshing] = useState(false);

  const loadTransactions = async () => {
    setIsRefreshing(true);
    try {
      const data = await getTransactions(100, 0); // Get last 100 transactions
      setTransactions(data);
      setFilteredTransactions(data);
    } catch (error) {
      console.error(error);
    } finally {
      setIsRefreshing(false);
    }
  };

  useEffect(() => {
    loadTransactions();
  }, []);

  useEffect(() => {
    if (searchQuery) {
      const filtered = transactions.filter(t => 
        t.note?.toLowerCase().includes(searchQuery.toLowerCase()) ||
        t.category.name.toLowerCase().includes(searchQuery.toLowerCase())
      );
      setFilteredTransactions(filtered);
    } else {
      setFilteredTransactions(transactions);
    }
  }, [searchQuery, transactions]);

  return (
    <View className="flex-1 bg-background">
      <ScreenScrollView
        refreshControl={
          <RefreshControl refreshing={isRefreshing} onRefresh={loadTransactions} />
        }
      >
        <View className="px-4 pt-12 pb-4">
          <View className="flex-row items-start justify-between mb-4">
            <AppText className="text-3xl font-bold text-foreground">History</AppText>
            <ThemeToggle />
          </View>

          {/* Search Bar */}
          <View className="mb-4">
            <TextField>
              <TextField.Input
                placeholder="Search transactions..."
                value={searchQuery}
                onChangeText={setSearchQuery}
              >
                <TextField.InputStartContent>
                  <Feather name="search" size={20} className="text-muted" />
                </TextField.InputStartContent>
              </TextField.Input>
            </TextField>
          </View>

          {/* Transactions List */}
          {filteredTransactions.length === 0 ? (
            <View className="items-center justify-center py-20">
              <Feather name="list" size={40} className="text-muted mb-2" />
              <AppText className="text-muted">No transactions found</AppText>
            </View>
          ) : (
            filteredTransactions.map((item) => (
              <TransactionCard key={item.id} transaction={item} />
            ))
          )}
        </View>
      </ScreenScrollView>
    </View>
  );
}
