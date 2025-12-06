# ZenSpend

ZenSpend is a modern, mobile-first money tracking application built with [React Native](https://reactnative.dev/) and [Expo](https://expo.dev/). It helps you track your expenses, visualize your spending habits, and manage your budget with a beautiful, intuitive interface.

## Features

- **Dashboard**: Get a quick overview of your current balance, income, and expenses.
- **Analytics**: Visualize your spending trends with interactive charts and category breakdowns.
- **Transaction History**: Detailed view of all your past transactions with filtering options.
- **Smart Categories**: Organize your spending with customizable categories.
- **Multi-Currency Support**: Dynamic currency formatting based on your preferences.
- **Dark/Light Mode**: Seamless theme switching optimized for any time of day.
- **Local First**: Your data is stored locally on your device for privacy and speed.
- **Internationalization (i18n)**: Support for multiple languages.

## Tech Stack

- **Framework**: [Expo](https://expo.dev/) (React Native)
- **Routing**: [Expo Router](https://docs.expo.dev/router/introduction/)
- **UI Components**: [HeroUI Native](https://github.com/heroui-inc/heroui-native), `expo-blur`, `expo-glass-effect`
- **Styling**: [Tailwind CSS v4](https://tailwindcss.com/) with `uniwind` and `tailwind-variants`
- **State Management**: [Zustand](https://github.com/pmndrs/zustand)
- **Data Persistence**: `expo-sqlite`, `react-native-mmkv`
- **Charts**: `react-native-gifted-charts`
- **Icons**: `@expo/vector-icons`, `expo-symbols` (SF Symbols)
- **Date Handling**: `date-fns`

## Get Started

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd zen-spend
   ```

2. **Install dependencies**

   ```bash
   bun install
   ```

3. **Start the app**

   ```bash
   bun run start
   ```

4. **Run on Device/Emulator**
   - Press `a` for Android (requires Android Studio/Emulator)
   - Press `i` for iOS (requires Xcode/Simulator, macOS only)
   - Scan the QR code with Expo Go app on your physical device

## Project Structure

- `src/app`: Application routes (Expo Router)
- `src/components`: Reusable UI components
- `src/store`: Global state management stores
- `src/types`: TypeScript type definitions
- `src/db`: Database configuration
- `src/i18n`: Internationalization setup
