import Feather from '@expo/vector-icons/Feather';
import {
  addMonths,
  eachDayOfInterval,
  endOfMonth,
  endOfWeek,
  format,
  isSameDay,
  isSameMonth,
  startOfMonth,
  startOfWeek,
  subMonths,
} from 'date-fns';
import { Button, cn, Dialog, useThemeColor } from 'heroui-native';
import type React from 'react';
import { useCallback, useMemo, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { TouchableOpacity, View } from 'react-native';
import { AppText } from './app-text';
import { DialogBlurBackdrop } from './dialog-blur-backdrop';

type DatePickerProps = {
  value: Date;
  onChange: (date: Date) => void;
};

export const DatePicker: React.FC<DatePickerProps> = ({ value, onChange }) => {
  const { t } = useTranslation();
  
  const [isOpen, setIsOpen] = useState(false);

  // Theme colors
  const themeColorForeground = useThemeColor('foreground');
  const themeColorMuted = useThemeColor('muted');

  // Calendar State
  const [currentMonth, setCurrentMonth] = useState(new Date());

  const handleOpen = useCallback(() => {
    setCurrentMonth(value); // Reset to selected date's month when opening
    setIsOpen(true);
  }, [value]);

  const handleDateSelect = useCallback(
    (date: Date) => {
      onChange(date);
      setIsOpen(false);
    },
    [onChange],
  );

  const nextMonth = useCallback(() => {
    setCurrentMonth((prev) => addMonths(prev, 1));
  }, []);

  const prevMonth = useCallback(() => {
    setCurrentMonth((prev) => subMonths(prev, 1));
  }, []);

  // Calendar Data Generation
  const calendarDays = useMemo(() => {
    const monthStart = startOfMonth(currentMonth);
    const monthEnd = endOfMonth(monthStart);
    const startDate = startOfWeek(monthStart);
    const endDate = endOfWeek(monthEnd);

    return eachDayOfInterval({
      start: startDate,
      end: endDate,
    });
  }, [currentMonth]);

  const weekDays = ['Su', 'Mo', 'Tu', 'We', 'Th', 'Fr', 'Sa'];

  return (
    <>
      <TouchableOpacity
        onPress={handleOpen}
        className="p-3 bg-background rounded-xl border border-border flex-row items-center justify-between"
      >
        <AppText className="text-foreground">{value.toLocaleDateString('id-ID')}</AppText>
        <Feather name="calendar" size={18} color={themeColorMuted} />
      </TouchableOpacity>

      <Dialog isOpen={isOpen} onOpenChange={setIsOpen}>
        <Dialog.Portal>
           <Dialog.Overlay isDefaultAnimationDisabled>
              <DialogBlurBackdrop />
            </Dialog.Overlay>
          <Dialog.Content className="bg-surface rounded-3xl p-4 w-[90%] max-w-sm mx-auto">
             {/* Header */}
            <View className="mb-4 flex-row items-center justify-between">
              <AppText className="text-xl font-bold text-foreground">{format(currentMonth, 'MMMM yyyy')}</AppText>
              <View className="flex-row gap-2">
                <Button variant="ghost" isIconOnly size="sm" onPress={prevMonth} className="w-10 h-10 rounded-full">
                  <Feather name="chevron-left" size={24} color={themeColorForeground} />
                </Button>
                <Button variant="ghost" isIconOnly size="sm" onPress={nextMonth} className="w-10 h-10 rounded-full">
                  <Feather name="chevron-right" size={24} color={themeColorForeground} />
                </Button>
              </View>
            </View>

            {/* Weekday Headers */}
            <View className="flex-row justify-between mb-2">
              {weekDays.map((day) => (
                <View key={day} className="w-[13%] items-center">
                  <AppText className="text-muted font-medium text-xs uppercase">{day}</AppText>
                </View>
              ))}
            </View>

            {/* Days Grid */}
            <View className="flex-row flex-wrap justify-between">
              {calendarDays.map((date) => {
                const isSelected = isSameDay(date, value);
                const isCurrentMonth = isSameMonth(date, currentMonth);
                const isToday = isSameDay(date, new Date());

                return (
                  <TouchableOpacity
                    key={date.toISOString()}
                    onPress={() => handleDateSelect(date)}
                    className={cn(
                      'w-[13%] aspect-square items-center justify-center rounded-full mb-2',
                      isSelected ? 'bg-accent' : 'bg-transparent',
                      !isSelected && isToday && 'border border-border',
                    )}
                  >
                    <AppText
                      className={cn(
                        'text-sm font-medium',
                        isSelected ? 'text-background' : 'text-foreground',
                        !isCurrentMonth && 'text-muted/50',
                      )}
                    >
                      {format(date, 'd')}
                    </AppText>
                  </TouchableOpacity>
                );
              })}
            </View>

            <View className="flex-row justify-end mt-4 gap-2">
                <Button 
                    onPress={() => setIsOpen(false)} 
                    variant="ghost"
                    className="flex-1"
                >
                    {t('cancel', 'Cancel')}
                </Button>
                <Button 
                    onPress={() => handleDateSelect(new Date())} 
                    variant="ghost"
                    className="flex-1"
                >
                    {t('today', 'Today')}
                </Button>
            </View>
          </Dialog.Content>
        </Dialog.Portal>
      </Dialog>
    </>
  );
};
