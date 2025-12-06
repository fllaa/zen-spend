import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

const resources = {
  en: {
    translation: {
      // Navigation
      dashboard: 'Dashboard',
      history: 'History',
      analytics: 'Analytics',
      settings: 'Settings',

      // Dashboard
      overviewOfFinances: 'Overview of your finances',
      income: 'Income',
      expense: 'Expense',
      totalBalance: 'Total Balance',
      recentTransactions: 'Recent Transactions',
      seeAll: 'See All',
      noTransactionsYet: 'No transactions yet',

      // History
      searchTransactions: 'Search transactions...',
      noTransactionsFound: 'No transactions found',

      // Analytics
      breakdown: 'Breakdown',
      noDataForThisMonth: 'No data for this month',

      // Settings
      customizeAppExperience: 'Customize your app experience',
      appearance: 'Appearance',
      darkMode: 'Dark Mode',
      darkModeDescription: 'Enable dark mode for a comfortable experience',
      style: 'Style',
      styleDescription: 'Change your app style',
      currencyAndLocalization: 'Currency & Localization',
      currency: 'Currency',
      currencyDescription: 'Change your app currency',
      numberFormatting: 'Number Formatting',
      numberFormattingDescription: 'Change your app number format',
      language: 'Language',
      languageDescription: 'Change your app language',
      theme: 'Theme',
      light: 'Light',
      dark: 'Dark',
      system: 'System',
      english: 'English',
      indonesian: 'Bahasa Indonesia',
      dotSeparator: '1,000.00 (Dot)',
      commaSeparator: '1.000,00 (Comma)',

      // Add Transaction
      amount: 'Amount',
      category: 'Category',
      noteOptional: 'Note (Optional)',
      whatIsThisFor: 'What is this for?',
      date: 'Date',
      saveTransaction: 'Save Transaction',
      saving: 'Saving...',
    },
  },
  id: {
    translation: {
      // Navigation
      dashboard: 'Dasbor',
      history: 'Riwayat',
      analytics: 'Analitik',
      settings: 'Pengaturan',

      // Dashboard
      overviewOfFinances: 'Ringkasan keuangan Anda',
      income: 'Pemasukan',
      expense: 'Pengeluaran',
      totalBalance: 'Total Saldo',
      recentTransactions: 'Transaksi Terbaru',
      seeAll: 'Lihat Semua',
      noTransactionsYet: 'Belum ada transaksi',

      // History
      searchTransactions: 'Cari transaksi...',
      noTransactionsFound: 'Tidak ada transaksi ditemukan',

      // Analytics
      breakdown: 'Rincian',
      noDataForThisMonth: 'Tidak ada data untuk bulan ini',

      // Settings
      customizeAppExperience: 'Ubah pengalaman aplikasi Anda',
      appearance: 'Tampilan',
      darkMode: 'Mode Gelap',
      darkModeDescription: 'Aktifkan untuk pengalaman yang nyaman',
      style: 'Style',
      styleDescription: 'Ubah gaya aplikasi Anda',
      currencyAndLocalization: 'Mata Uang & Lokal',
      currency: 'Mata Uang',
      currencyDescription: 'Ubah mata uang aplikasi Anda',
      numberFormatting: 'Format Angka',
      numberFormattingDescription: 'Ubah format angka aplikasi Anda',
      language: 'Bahasa',
      languageDescription: 'Ubah bahasa aplikasi Anda',
      theme: 'Tema',
      light: 'Terang',
      dark: 'Gelap',
      system: 'Sistem',
      english: 'English',
      indonesian: 'Bahasa Indonesia',
      dotSeparator: '1,000.00 (Titik)',
      commaSeparator: '1.000,00 (Koma)',

      // Add Transaction
      amount: 'Jumlah',
      category: 'Kategori',
      noteOptional: 'Catatan (Opsional)',
      whatIsThisFor: 'Untuk apa ini?',
      date: 'Tanggal',
      saveTransaction: 'Simpan Transaksi',
      saving: 'Menyimpan...',
    },
  },
};

// eslint-disable-next-line import/no-named-as-default-member
i18n.use(initReactI18next).init({
  resources,
  lng: 'en', // default language
  fallbackLng: 'en',
  interpolation: {
    escapeValue: false,
  },
});

export default i18n;
