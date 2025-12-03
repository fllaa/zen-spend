import Feather from '@expo/vector-icons/Feather';
import { Tabs } from 'expo-router';
import { useThemeColor } from 'heroui-native';
import { View } from 'react-native';
import { useAppTheme } from '../../contexts/app-theme-context';

export default function TabLayout() {
  const { isDark } = useAppTheme();
  const themeColorBackground = useThemeColor('background');
  const themeColorPrimary = useThemeColor('foreground');

  return (
    <Tabs
      screenOptions={{
        headerShown: false,
        tabBarStyle: {
          backgroundColor: themeColorBackground,
          borderTopWidth: 0,
          elevation: 0,
          height: 80,
          paddingTop: 12,
        },
        tabBarActiveTintColor: themeColorPrimary,
        tabBarInactiveTintColor: isDark ? '#888' : '#999',
        tabBarLabelStyle: {
          fontSize: 12,
          fontWeight: '500',
          marginTop: 4,
        },
      }}
    >
      <Tabs.Screen
        name="index"
        options={{
          title: 'Dashboard',
          tabBarIcon: ({ color, focused }) => (
            <View className={`items-center justify-center w-16 h-8 rounded-full ${focused ? 'bg-primary/10' : ''}`}>
              <Feather name="home" size={24} color={color} />
            </View>
          ),
        }}
      />
      <Tabs.Screen
        name="analytics"
        options={{
          title: 'Analytics',
          tabBarIcon: ({ color, focused }) => (
            <View className={`items-center justify-center w-16 h-8 rounded-full ${focused ? 'bg-primary/10' : ''}`}>
              <Feather name="pie-chart" size={24} color={color} />
            </View>
          ),
        }}
      />
      <Tabs.Screen
        name="history"
        options={{
          title: 'History',
          tabBarIcon: ({ color, focused }) => (
            <View className={`items-center justify-center w-16 h-8 rounded-full ${focused ? 'bg-primary/10' : ''}`}>
              <Feather name="list" size={24} color={color} />
            </View>
          ),
        }}
      />
    </Tabs>
  );
}