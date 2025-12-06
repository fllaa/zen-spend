import Feather from '@expo/vector-icons/Feather';
import { Button, TextField } from 'heroui-native';
import type React from 'react';
import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { TouchableOpacity, View } from 'react-native';
import { getCurrencySymbol } from '../helpers/currency-helper';
import { useStore } from '../store';
import { useSettingsStore } from '../store/settings';
import { AppText } from './app-text';

import { DatePicker } from './date-picker';

export const AddTransaction: React.FC<{ close: () => void }> = (props) => {
  const { t } = useTranslation();
  const { categories, addNewTransaction, isLoading } = useStore();
  const { currency } = useSettingsStore();

  const [amount, setAmount] = useState('');
  const [note, setNote] = useState('');
  const [type, setType] = useState('expense');
  const [selectedCategory, setSelectedCategory] = useState<number | null>(null);
  const [date, setDate] = useState(new Date());

  const filteredCategories = categories.filter((c) => c.type === type);

  const handleSave = async () => {
    if (!amount || !selectedCategory) return;

    await addNewTransaction(Number.parseFloat(amount), selectedCategory, date, note, type);
    props.close();
  };

  return (
    <View className="flex-1">
      <View className="flex-1 p-4 gap-6">
        {/* Type Selection */}
        <View className="flex-row bg-surface p-1 rounded-lg">
          <TouchableOpacity
            className={`flex-1 py-2 items-center rounded-md ${type === 'expense' ? 'bg-background shadow-sm' : ''}`}
            onPress={() => {
              setType('expense');
              setSelectedCategory(null);
            }}
          >
            <AppText className={type === 'expense' ? 'font-semibold text-foreground' : 'text-muted'}>
              {t('expense')}
            </AppText>
          </TouchableOpacity>
          <TouchableOpacity
            className={`flex-1 py-2 items-center rounded-md ${type === 'income' ? 'bg-background shadow-sm' : ''}`}
            onPress={() => {
              setType('income');
              setSelectedCategory(null);
            }}
          >
            <AppText className={type === 'income' ? 'font-semibold text-foreground' : 'text-muted'}>
              {t('income')}
            </AppText>
          </TouchableOpacity>
        </View>

        {/* Amount Input */}
        <View>
          <AppText className="text-sm font-medium mb-2 text-muted">{t('amount')}</AppText>
          <TextField>
            <TextField.Input
              placeholder="0.00"
              keyboardType="numeric"
              value={amount}
              onChangeText={setAmount}
              className="text-3xl font-bold"
            >
              <TextField.InputStartContent>
                <AppText className="text-xl font-bold text-muted">{getCurrencySymbol(currency)}</AppText>
              </TextField.InputStartContent>
            </TextField.Input>
          </TextField>
        </View>

        {/* Category Selection */}
        <View>
          <AppText className="text-sm font-medium mb-3 text-muted">{t('category')}</AppText>
          <View className="flex-row flex-wrap gap-3">
            {filteredCategories.map((cat) => (
              <TouchableOpacity
                key={cat.id}
                onPress={() => setSelectedCategory(cat.id)}
                className={`items-center justify-center p-3 rounded-xl border ${
                  selectedCategory === cat.id ? 'bg-surface border-accent-foreground' : 'bg-background border-border'
                }`}
                style={{ width: '30%' }}
              >
                <View
                  className="w-10 h-10 rounded-full items-center justify-center mb-2"
                  style={{ backgroundColor: `${cat.color}20` }}
                >
                  <Feather name={cat.icon as any} size={20} color={cat.color} />
                </View>
                <AppText className="text-xs font-medium text-foreground" numberOfLines={1}>
                  {cat.name}
                </AppText>
              </TouchableOpacity>
            ))}
          </View>
        </View>

        {/* Note Input */}
        <View>
          <AppText className="text-sm font-medium mb-2 text-muted">{t('noteOptional')}</AppText>
          <TextField>
            <TextField.Input placeholder={t('whatIsThisFor')} value={note} onChangeText={setNote} />
          </TextField>
        </View>

        {/* Date Input */}
        <View>
          <AppText className="text-sm font-medium mb-2 text-muted">{t('date')}</AppText>
          <DatePicker value={date} onChange={setDate} />
        </View>
      </View>

      <View className="p-4 border-t border-border">
        <Button size="lg" onPress={handleSave} isDisabled={!amount || !selectedCategory || isLoading}>
          {isLoading ? t('saving') : t('saveTransaction')}
        </Button>
      </View>
    </View>
  );
};
