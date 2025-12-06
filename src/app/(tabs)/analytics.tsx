import Feather from '@expo/vector-icons/Feather';
import { endOfMonth, format, startOfMonth, subMonths } from 'date-fns';
import { Button, cn, Surface, useThemeColor } from 'heroui-native';
import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Dimensions, RefreshControl, View } from 'react-native';
import { PieChart } from 'react-native-gifted-charts';
import { formatCurrency } from '@/src/helpers/currency-helper';
import { useSettingsStore } from '@/src/store/settings';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { useStore } from '../../store';

const { width } = Dimensions.get('window');

export default function Analytics() {
  const { t } = useTranslation();
  const { categorySummary, fetchAnalytics, isLoading } = useStore();
  const { currency, numberFormat, style } = useSettingsStore();
  const [currentDate, setCurrentDate] = useState(new Date());
  const textColor = useThemeColor('foreground');

  const loadData = useCallback(() => {
    const start = startOfMonth(currentDate);
    const end = endOfMonth(currentDate);
    fetchAnalytics(start, end);
  }, [currentDate, fetchAnalytics]);

  useEffect(() => {
    loadData();
  }, [loadData]);

  const handlePrevMonth = () => {
    setCurrentDate(subMonths(currentDate, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(subMonths(currentDate, -1));
  };

  const pieData = categorySummary.map((item) => ({
    value: item.totalAmount,
    color: item.categoryColor,
    text: `${Math.round(item.percentage)}%`,
    categoryName: item.categoryName,
  }));

  const renderLegend = () => {
    return (
      <View className="flex-row flex-wrap justify-center gap-4 mt-6">
        {categorySummary.map((item) => (
          <View key={item.categoryId} className="flex-row items-center gap-2">
            <View style={{ width: 12, height: 12, borderRadius: 6, backgroundColor: item.categoryColor }} />
            <AppText className="text-sm text-muted">
              {item.categoryName} ({Math.round(item.percentage)}%)
            </AppText>
          </View>
        ))}
      </View>
    );
  };

  return (
    <View className="flex-1 bg-background">
      <ScreenScrollView refreshControl={<RefreshControl refreshing={isLoading} onRefresh={loadData} />}>
        <View className="pt-12 pb-4">
          <View className="flex-row items-start justify-between mb-6">
            <AppText className="text-3xl font-bold text-foreground">{t('analytics')}</AppText>
          </View>

          {/* Month Selector */}
          <Surface
            className={cn(
              style === 'bordered' ? 'border border-border' : 'border-0',
              'flex-row items-center justify-between mb-8 p-2 rounded-xl',
            )}
          >
            <Button variant="ghost" size="sm" onPress={handlePrevMonth}>
              <Feather name="chevron-left" color={textColor} size={20} />
            </Button>
            <AppText className="text-lg font-semibold text-foreground">{format(currentDate, 'MMMM yyyy')}</AppText>
            <Button variant="ghost" size="sm" onPress={handleNextMonth}>
              <Feather name="chevron-right" color={textColor} size={20} />
            </Button>
          </Surface>

          {/* Chart Area */}
          {categorySummary.length > 0 ? (
            <Surface
              className={cn(
                style === 'bordered' ? 'border border-border' : 'border-0',
                'items-center p-6 rounded-2xl shadow-sm',
              )}
            >
              <PieChart
                data={pieData}
                donut
                showText
                backgroundColor="transparent"
                textColor={textColor}
                radius={width * 0.35}
                innerRadius={width * 0.2}
                textSize={12}
                focusOnPress
                showValuesAsLabels={false}
              />
              {renderLegend()}
            </Surface>
          ) : (
            <View className="items-center justify-center py-20 border border-dashed border-border rounded-xl">
              <Feather name="pie-chart" size={40} className="text-muted mb-2" color={textColor} />
              <AppText className="text-muted">{t('noDataForThisMonth')}</AppText>
            </View>
          )}

          {/* Breakdown List */}
          <View className="mt-8">
            <AppText className="text-xl font-semibold mb-4 text-foreground">{t('breakdown')}</AppText>
            {categorySummary.map((item) => (
              <View key={item.categoryId} className="flex-row items-center justify-between py-3 border-b border-border">
                <View className="flex-row items-center gap-3">
                  <View
                    className="w-8 h-8 rounded-full items-center justify-center"
                    style={{ backgroundColor: `${item.categoryColor}20` }}
                  >
                    <View style={{ width: 8, height: 8, borderRadius: 4, backgroundColor: item.categoryColor }} />
                  </View>
                  <AppText className="font-medium text-foreground">{item.categoryName}</AppText>
                </View>
                <AppText className="font-semibold text-foreground">
                  {formatCurrency(item.totalAmount, currency, numberFormat)}
                </AppText>
              </View>
            ))}
          </View>
        </View>
      </ScreenScrollView>
    </View>
  );
}
