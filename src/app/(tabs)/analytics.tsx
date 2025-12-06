import { formatCurrency } from '@/src/helpers/currency-helper';
import { useSettingsStore } from '@/src/store/settings';
import Feather from '@expo/vector-icons/Feather';
import { endOfMonth, format, getDaysInMonth, isSameMonth, startOfMonth, subMonths } from 'date-fns';
import { Button, cn, Surface, useThemeColor } from 'heroui-native';
import { useCallback, useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { Dimensions, RefreshControl, TouchableOpacity, View } from 'react-native';
import { LineChart, PieChart } from 'react-native-gifted-charts';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { useStore } from '../../store';

const { width } = Dimensions.get('window');

export default function Analytics() {
  const { t } = useTranslation();
  const { categorySummary, dailyExpenses, fetchAnalytics, isLoading, lastUpdated } = useStore();
  const { currency, numberFormat, style } = useSettingsStore();
  const [currentDate, setCurrentDate] = useState(new Date());
  const textColor = useThemeColor('foreground');
  const dangerColor = useThemeColor('danger');
  const surfaceColor = useThemeColor('surface');
  const [activeChart, setActiveChart] = useState<'trend' | 'category'>('trend');

  const loadData = useCallback(() => {
    const start = startOfMonth(currentDate);
    const end = endOfMonth(currentDate);
    fetchAnalytics(start, end);
  }, [currentDate, fetchAnalytics]);

  // biome-ignore lint/correctness/useExhaustiveDependencies: refresh automatically when data has changes
  useEffect(() => {
    loadData();
  }, [loadData, lastUpdated]);

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

  const today = new Date();
  const isCurrentMonth = isSameMonth(currentDate, today);
  const maxDay = isCurrentMonth ? today.getDate() : getDaysInMonth(currentDate);

  const daysInMonth = Array.from({ length: maxDay }, (_, i) => i + 1);
  const lineData = daysInMonth.map((day) => {
    const expense = dailyExpenses.find((d) => d.day === day);
    return {
      value: expense ? expense.amount : 0,
      label: day % 5 === 0 || day === 1 ? day.toString() : '',
      labelTextStyle: { color: textColor, fontSize: 10 },
      day,
    };
  });

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

          {/* Charts Container */}
          <Surface
            className={cn(style === 'bordered' ? 'border border-border' : 'border-0', 'rounded-2xl shadow-sm mb-6 p-1')}
          >
            {/* Tab Selector */}
            <View className="flex-row bg-muted/20 p-1 rounded-xl mb-4">
              <TouchableOpacity
                className={`flex-1 py-2 items-center rounded-lg ${
                  activeChart === 'trend' ? 'bg-background shadow-sm' : ''
                }`}
                onPress={() => setActiveChart('trend')}
              >
                <AppText className={activeChart === 'trend' ? 'font-semibold text-foreground' : 'text-muted'}>
                  {t('dailyTrend')}
                </AppText>
              </TouchableOpacity>
              <TouchableOpacity
                className={`flex-1 py-2 items-center rounded-lg ${
                  activeChart === 'category' ? 'bg-background shadow-sm' : ''
                }`}
                onPress={() => setActiveChart('category')}
              >
                <AppText className={activeChart === 'category' ? 'font-semibold text-foreground' : 'text-muted'}>
                  {t('categories')}
                </AppText>
              </TouchableOpacity>
            </View>

            <View className="items-center p-4">
              {activeChart === 'trend' ? (
                dailyExpenses.length > 0 ? (
                  <LineChart
                    data={lineData}
                    height={200}
                    width={width - 80}
                    spacing={(width - 80) / lineData.length}
                    initialSpacing={10}
                    color={dangerColor}
                    thickness={2}
                    startFillColor={`${dangerColor}50`}
                    endFillColor={`${dangerColor}10`}
                    startOpacity={0.9}
                    endOpacity={0.2}
                    areaChart
                    curved
                    hideDataPoints
                    hideRules
                    hideYAxisText
                    xAxisColor="transparent"
                    yAxisColor="transparent"
                    textColor={textColor}
                    pointerConfig={{
                      pointerStripUptoDataPoint: true,
                      pointerStripColor: dangerColor,
                      pointerStripWidth: 2,
                      strokeDashArray: [2, 5],
                      pointerColor: dangerColor,
                      radius: 4,
                      pointerLabelWidth: 100,
                      pointerLabelHeight: 120,
                      activatePointersOnLongPress: false,
                      autoAdjustPointerLabelPosition: true,
                      pointerLabelComponent: (items: { day: number; value: number }[]) => {
                        return (
                          <View
                            className="h-16 w-32 justify-center pl-4 rounded-xl"
                            style={{ backgroundColor: surfaceColor }}
                          >
                            <AppText className="text-foreground text-xs mb-1">
                              {format(
                                new Date(currentDate.getFullYear(), currentDate.getMonth(), items[0].day),
                                'MMM dd',
                              )}
                            </AppText>
                            <AppText className="text-foreground font-bold">
                              {formatCurrency(items[0].value, currency, numberFormat)}
                            </AppText>
                          </View>
                        );
                      },
                    }}
                  />
                ) : (
                  <View className="items-center justify-center py-20 w-full">
                    <Feather name="bar-chart-2" size={40} className="text-muted mb-2" color={textColor} />
                    <AppText className="text-muted">{t('noDataForThisMonth')}</AppText>
                  </View>
                )
              ) : categorySummary.length > 0 ? (
                <>
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
                </>
              ) : (
                <View className="items-center justify-center py-20 w-full">
                  <Feather name="pie-chart" size={40} className="text-muted mb-2" color={textColor} />
                  <AppText className="text-muted">{t('noDataForThisMonth')}</AppText>
                </View>
              )}
            </View>
          </Surface>

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
