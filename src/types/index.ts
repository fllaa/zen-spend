export type TransactionType = 'income' | 'expense';

export interface Category {
  id: number;
  name: string;
  icon: string;
  color: string;
  type: TransactionType;
}

export interface Transaction {
  id: number;
  amount: number;
  categoryId: number;
  date: number; // Unix timestamp
  note: string;
  type: TransactionType;
}

export interface TransactionWithCategory extends Transaction {
  category: Category;
}

export interface MonthlySummary {
  income: number;
  expense: number;
  balance: number;
}

export interface CategorySummary {
  categoryId: number;
  categoryName: string;
  categoryColor: string;
  totalAmount: number;
  percentage: number;
}

export interface DailyExpense {
  day: number;
  amount: number;
}
