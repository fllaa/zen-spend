import Feather from '@expo/vector-icons/Feather';
import { useRouter } from 'expo-router';
import { Button, TextField, useThemeColor } from 'heroui-native';
import React, { useState } from 'react';
import { TouchableOpacity, View } from 'react-native';
import { AppText } from '../components/app-text';
import { ScreenScrollView } from '../components/screen-scroll-view';
import { useStore } from '../store';

export default function AddTransaction() {
  const router = useRouter();
  const { categories, addNewTransaction, isLoading } = useStore();
  const themeColorBackground = useThemeColor('background');

  const [amount, setAmount] = useState('');
  const [note, setNote] = useState('');
  const [type, setType] = useState('expense');
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [date, setDate] = useState(new Date());

  const filteredCategories = categories.filter(c => c.type === type);

  const handleSave = async () => {
    if (!amount || !selectedCategory) return;

    await addNewTransaction(
      parseFloat(amount),
      selectedCategory,
      date,
      note,
      type
    );
    router.back();
  };

  return (
    <View className="flex-1 bg-background pt-8">
      <View className="flex-row items-center justify-between px-4 py-3 border-b border-zinc-200 dark:border-zinc-800">
        <TouchableOpacity onPress={() => router.back()}>
          <Feather name="x" size={24} className="text-foreground" />
        </TouchableOpacity>
        <AppText className="text-lg font-semibold text-foreground">Add Transaction</AppText>
        <View style={{ width: 24 }} />
      </View>

      <ScreenScrollView useHeaderHeight={false}>
        <View className="p-4 gap-6">
          {/* Type Selection */}
          <View className="flex-row bg-zinc-200 dark:bg-zinc-800 p-1 rounded-lg">
            <TouchableOpacity 
              className={`flex-1 py-2 items-center rounded-md ${type === 'expense' ? 'bg-white dark:bg-zinc-700 shadow-sm' : ''}`}
              onPress={() => {
                setType('expense');
                setSelectedCategory(null);
              }}
            >
              <AppText className={type === 'expense' ? 'font-semibold text-foreground' : 'text-muted'}>Expense</AppText>
            </TouchableOpacity>
            <TouchableOpacity 
              className={`flex-1 py-2 items-center rounded-md ${type === 'income' ? 'bg-white dark:bg-zinc-700 shadow-sm' : ''}`}
              onPress={() => {
                setType('income');
                setSelectedCategory(null);
              }}
            >
              <AppText className={type === 'income' ? 'font-semibold text-foreground' : 'text-muted'}>Income</AppText>
            </TouchableOpacity>
          </View>

          {/* Amount Input */}
          <View>
            <AppText className="text-sm font-medium mb-2 text-muted">Amount</AppText>
            <TextField>
              <TextField.Input
                placeholder="0.00"
                keyboardType="numeric"
                value={amount}
                onChangeText={setAmount}
                className="text-3xl font-bold"
              >
                <TextField.InputStartContent>
                  <AppText className="text-xl font-bold text-muted">$</AppText>
                </TextField.InputStartContent>
              </TextField.Input>
            </TextField>
          </View>

          {/* Category Selection */}
          <View>
            <AppText className="text-sm font-medium mb-3 text-muted">Category</AppText>
            <View className="flex-row flex-wrap gap-3">
              {filteredCategories.map((cat) => (
                <TouchableOpacity
                  key={cat.id}
                  onPress={() => setSelectedCategory(cat.id)}
                  className={`items-center justify-center p-3 rounded-xl border ${
                    selectedCategory === cat.id 
                      ? 'bg-foreground/20 border-foreground' 
                      : 'bg-zinc-50 dark:bg-zinc-900 border-zinc-200 dark:border-zinc-800'
                  }`}
                  style={{ width: '30%' }}
                >
                  <View 
                    className="w-10 h-10 rounded-full items-center justify-center mb-2"
                    style={{ backgroundColor: cat.color + '20' }}
                  >
                    <Feather name={cat.icon as any} size={20} color={cat.color} />
                  </View>
                  <AppText 
                    className={`text-xs font-medium ${selectedCategory === cat.id ? 'text-foreground' : 'text-foreground'}`}
                    numberOfLines={1}
                  >
                    {cat.name}
                  </AppText>
                </TouchableOpacity>
              ))}
            </View>
          </View>

          {/* Note Input */}
          <View>
            <AppText className="text-sm font-medium mb-2 text-muted">Note (Optional)</AppText>
            <TextField>
              <TextField.Input
                placeholder="What is this for?"
                value={note}
                onChangeText={setNote}
              />
            </TextField>
          </View>

          {/* Date Input (Simplified for MVP) */}
          <View>
            <AppText className="text-sm font-medium mb-2 text-muted">Date</AppText>
            <View className="p-3 bg-zinc-50 dark:bg-zinc-900 rounded-xl border border-zinc-200 dark:border-zinc-800">
              <AppText className="text-foreground">{date.toDateString()}</AppText>
            </View>
          </View>

        </View>
      </ScreenScrollView>

      <View className="p-4 border-t border-zinc-200 dark:border-zinc-800 bg-background">
        <Button 
          size="lg" 
          onPress={handleSave}
          isDisabled={!amount || !selectedCategory || isLoading}
        >
          {isLoading ? 'Saving...' : 'Save Transaction'}
        </Button>
      </View>
    </View>
  );
}