import Feather from '@expo/vector-icons/Feather';
import { endOfMonth, format, startOfMonth, subMonths } from 'date-fns';
import { Button, useThemeColor } from 'heroui-native';
import React, { useEffect, useState } from 'react';
import { Dimensions, RefreshControl, View } from 'react-native';
import { PieChart } from 'react-native-gifted-charts';
import { AppText } from '../../components/app-text';
import { ScreenScrollView } from '../../components/screen-scroll-view';
import { ThemeToggle } from '../../components/theme-toggle';
import { useStore } from '../../store';

const { width } = Dimensions.get('window');

export default function Analytics() {
  const { categorySummary, fetchAnalytics, isLoading } = useStore();
  const [currentDate, setCurrentDate] = useState(new Date());
  const textColor = useThemeColor('foreground');

  useEffect(() => {
    loadData();
  }, [currentDate]);

  const loadData = () => {
    const start = startOfMonth(currentDate);
    const end = endOfMonth(currentDate);
    fetchAnalytics(start, end);
  };

  const handlePrevMonth = () => {
    setCurrentDate(subMonths(currentDate, 1));
  };

  const handleNextMonth = () => {
    setCurrentDate(subMonths(currentDate, -1));
  };

  const pieData = categorySummary.map(item => ({
    value: item.totalAmount,
    color: item.categoryColor,
    text: `${Math.round(item.percentage)}%`,
    categoryName: item.categoryName
  }));

  const renderLegend = () => {
    return (
      <View className="flex-row flex-wrap justify-center gap-4 mt-6">
        {categorySummary.map((item, index) => (
          <View key={index} className="flex-row items-center gap-2">
            <View 
              style={{ width: 12, height: 12, borderRadius: 6, backgroundColor: item.categoryColor }} 
            />
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
      <ScreenScrollView
        refreshControl={
          <RefreshControl refreshing={isLoading} onRefresh={loadData} />
        }
      >
        <View className="px-4 pt-12 pb-4">
          <View className="flex-row items-start justify-between mb-6">
            <AppText className="text-3xl font-bold text-foreground">Analytics</AppText>
            <ThemeToggle />
          </View>

          {/* Month Selector */}
          <View className="flex-row items-center justify-between mb-8 bg-zinc-50 dark:bg-zinc-900 p-2 rounded-xl border border-zinc-200 dark:border-zinc-800">
            <Button variant="ghost" size="sm" onPress={handlePrevMonth}>
              <Feather name="chevron-left" color={textColor} size={20} />
            </Button>
            <AppText className="text-lg font-semibold text-foreground">
              {format(currentDate, 'MMMM yyyy')}
            </AppText>
            <Button variant="ghost" size="sm" onPress={handleNextMonth}>
              <Feather name="chevron-right" color={textColor} size={20} />
            </Button>
          </View>

          {/* Chart Area */}
          {categorySummary.length > 0 ? (
            <View className="items-center bg-white dark:bg-zinc-900 p-6 rounded-2xl border border-zinc-200 dark:border-zinc-800 shadow-sm">
              <PieChart
                data={pieData}
                donut
                showText
                textColor={textColor}
                radius={width * 0.35}
                innerRadius={width * 0.20}
                textSize={12}
                focusOnPress
                showValuesAsLabels={false}
              />
              {renderLegend()}
            </View>
          ) : (
            <View className="items-center justify-center py-20 border border-dashed border-zinc-300 dark:border-zinc-700 rounded-xl">
              <Feather name="pie-chart" size={40} className="text-muted mb-2" />
              <AppText className="text-muted">No data for this month</AppText>
            </View>
          )}

          {/* Breakdown List */}
          <View className="mt-8">
            <AppText className="text-xl font-semibold mb-4 text-foreground">Breakdown</AppText>
            {categorySummary.map((item, index) => (
              <View 
                key={index} 
                className="flex-row items-center justify-between py-3 border-b border-zinc-100 dark:border-zinc-800"
              >
                <View className="flex-row items-center gap-3">
                  <View 
                    className="w-8 h-8 rounded-full items-center justify-center"
                    style={{ backgroundColor: item.categoryColor + '20' }}
                  >
                    <View 
                      style={{ width: 8, height: 8, borderRadius: 4, backgroundColor: item.categoryColor }} 
                    />
                  </View>
                  <AppText className="font-medium text-foreground">{item.categoryName}</AppText>
                </View>
                <AppText className="font-semibold text-foreground">
                  ${item.totalAmount.toFixed(2)}
                </AppText>
              </View>
            ))}
          </View>
        </View>
      </ScreenScrollView>
    </View>
  );
}