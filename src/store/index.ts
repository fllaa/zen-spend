import { endOfMonth, getUnixTime, startOfMonth } from 'date-fns';
import { create } from 'zustand';
import {
  addTransaction,
  deleteTransaction,
  getCategories,
  getCategorySummary,
  getMonthlySummary,
  getTransactions,
  initDatabase
} from '../db';
import { Category, CategorySummary, MonthlySummary, TransactionWithCategory } from '../types';

interface AppState {
  categories: Category[];
  recentTransactions: TransactionWithCategory[];
  monthlySummary: MonthlySummary;
  categorySummary: CategorySummary[];
  isLoading: boolean;
  error: string | null;
  
  // Actions
  initialize: () => Promise<void>;
  fetchDashboardData: () => Promise<void>;
  addNewTransaction: (amount: number, categoryId: number, date: Date, note: string, type: string) => Promise<void>;
  removeTransaction: (id: number) => Promise<void>;
  fetchAnalytics: (monthStart: Date, monthEnd: Date) => Promise<void>;
}

export const useStore = create<AppState>((set, get) => ({
  categories: [],
  recentTransactions: [],
  monthlySummary: { income: 0, expense: 0, balance: 0 },
  categorySummary: [],
  isLoading: false,
  error: null,

  initialize: async () => {
    set({ isLoading: true, error: null });
    try {
      await initDatabase();
      // Add a small delay to ensure DB is ready
      await new Promise(resolve => setTimeout(resolve, 100));
      const categories = await getCategories();
      set({ categories });
      await get().fetchDashboardData();
    } catch (error) {
      set({ error: 'Failed to initialize database' });
      console.error(error);
    } finally {
      set({ isLoading: false });
    }
  },

  fetchDashboardData: async () => {
    set({ isLoading: true, error: null });
    try {
      const now = new Date();
      const start = getUnixTime(startOfMonth(now));
      const end = getUnixTime(endOfMonth(now));

      const [transactions, summary] = await Promise.all([
        getTransactions(10, 0), // Get last 10 transactions
        getMonthlySummary(start, end)
      ]);

      set({ 
        recentTransactions: transactions,
        monthlySummary: summary
      });
    } catch (error) {
      set({ error: 'Failed to fetch dashboard data' });
      console.error(error);
    } finally {
      set({ isLoading: false });
    }
  },

  addNewTransaction: async (amount, categoryId, date, note, type) => {
    set({ isLoading: true, error: null });
    try {
      await addTransaction(amount, categoryId, getUnixTime(date), note, type);
      await get().fetchDashboardData(); // Refresh data
    } catch (error) {
      set({ error: 'Failed to add transaction' });
      console.error(error);
    } finally {
      set({ isLoading: false });
    }
  },

  removeTransaction: async (id) => {
    set({ isLoading: true, error: null });
    try {
      await deleteTransaction(id);
      await get().fetchDashboardData(); // Refresh data
    } catch (error) {
      set({ error: 'Failed to delete transaction' });
      console.error(error);
    } finally {
      set({ isLoading: false });
    }
  },

  fetchAnalytics: async (monthStart, monthEnd) => {
    set({ isLoading: true, error: null });
    try {
      const start = getUnixTime(monthStart);
      const end = getUnixTime(monthEnd);
      
      const summary = await getCategorySummary(start, end);
      
      // Calculate percentages
      const totalExpense = summary.reduce((acc: number, curr: any) => acc + curr.totalAmount, 0);
      const summaryWithPercentage = summary.map((item: any) => ({
        ...item,
        percentage: totalExpense > 0 ? (item.totalAmount / totalExpense) * 100 : 0
      }));

      set({ categorySummary: summaryWithPercentage });
    } catch (error) {
      set({ error: 'Failed to fetch analytics' });
      console.error(error);
    } finally {
      set({ isLoading: false });
    }
  }
}));