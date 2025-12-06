import * as SQLite from 'expo-sqlite';
import type { Category, TransactionWithCategory } from '../types';

let db: SQLite.SQLiteDatabase;

export const initDatabase = async () => {
  try {
    db = await SQLite.openDatabaseAsync('zenspend.db');

    await db.execAsync(`
      PRAGMA journal_mode = WAL;
      CREATE TABLE IF NOT EXISTS categories (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        icon TEXT NOT NULL,
        color TEXT NOT NULL,
        type TEXT NOT NULL
      );
      CREATE TABLE IF NOT EXISTS transactions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        amount REAL NOT NULL,
        categoryId INTEGER NOT NULL,
        date INTEGER NOT NULL,
        note TEXT,
        type TEXT NOT NULL,
        FOREIGN KEY (categoryId) REFERENCES categories (id)
      );
    `);

    // Seed default categories if empty
    const result = await db.getFirstAsync<{ count: number }>('SELECT COUNT(*) as count FROM categories');
    if (result?.count === 0) {
      await seedCategories();
    }
  } catch (error) {
    console.error('Database initialization failed:', error);
  }
};

const seedCategories = async () => {
  const defaultCategories = [
    { name: 'Food', icon: 'coffee', color: '#FF6B6B', type: 'expense' },
    { name: 'Transport', icon: 'truck', color: '#4ECDC4', type: 'expense' },
    { name: 'Bills', icon: 'file-text', color: '#45B7D1', type: 'expense' },
    { name: 'Entertainment', icon: 'film', color: '#96CEB4', type: 'expense' },
    { name: 'Shopping', icon: 'shopping-bag', color: '#FFEEAD', type: 'expense' },
    { name: 'Salary', icon: 'dollar-sign', color: '#A8E6CF', type: 'income' },
    { name: 'Others', icon: 'box', color: '#D4A5A5', type: 'expense' },
  ];

  for (const cat of defaultCategories) {
    await db.runAsync(
      'INSERT INTO categories (name, icon, color, type) VALUES (?, ?, ?, ?)',
      cat.name,
      cat.icon,
      cat.color,
      cat.type,
    );
  }
};

export const getCategories = async (): Promise<Category[]> => {
  if (!db) await initDatabase();
  return await db.getAllAsync<Category>('SELECT * FROM categories');
};

export const addTransaction = async (
  amount: number,
  categoryId: number,
  date: number,
  note: string,
  type: string,
): Promise<number> => {
  if (!db) await initDatabase();
  const result = await db.runAsync(
    'INSERT INTO transactions (amount, categoryId, date, note, type) VALUES (?, ?, ?, ?, ?)',
    amount,
    categoryId,
    date,
    note,
    type,
  );
  return result.lastInsertRowId;
};

export const getTransactions = async (limit: number = 50, offset: number = 0): Promise<TransactionWithCategory[]> => {
  if (!db) await initDatabase();
  const query = `
    SELECT t.*, c.name as categoryName, c.icon as categoryIcon, c.color as categoryColor, c.type as categoryType
    FROM transactions t
    JOIN categories c ON t.categoryId = c.id
    ORDER BY t.date DESC
    LIMIT ? OFFSET ?
  `;
  const rows = await db.getAllAsync<any>(query, limit, offset);

  return rows.map((row) => ({
    id: row.id,
    amount: row.amount,
    categoryId: row.categoryId,
    date: row.date,
    note: row.note,
    type: row.type,
    category: {
      id: row.categoryId,
      name: row.categoryName,
      icon: row.categoryIcon,
      color: row.categoryColor,
      type: row.categoryType,
    },
  }));
};

export const getMonthlySummary = async (monthStart: number, monthEnd: number) => {
  if (!db) await initDatabase();
  const incomeResult = await db.getFirstAsync<{ total: number }>(
    'SELECT SUM(amount) as total FROM transactions WHERE type = ? AND date >= ? AND date <= ?',
    'income',
    monthStart,
    monthEnd,
  );
  const expenseResult = await db.getFirstAsync<{ total: number }>(
    'SELECT SUM(amount) as total FROM transactions WHERE type = ? AND date >= ? AND date <= ?',
    'expense',
    monthStart,
    monthEnd,
  );

  return {
    income: incomeResult?.total || 0,
    expense: expenseResult?.total || 0,
    balance: (incomeResult?.total || 0) - (expenseResult?.total || 0),
  };
};

export const deleteTransaction = async (id: number) => {
  if (!db) await initDatabase();
  await db.runAsync('DELETE FROM transactions WHERE id = ?', id);
};

export const getCategorySummary = async (monthStart: number, monthEnd: number) => {
  if (!db) await initDatabase();
  const query = `
      SELECT c.id as categoryId, c.name as categoryName, c.color as categoryColor, SUM(t.amount) as totalAmount
      FROM transactions t
      JOIN categories c ON t.categoryId = c.id
      WHERE t.type = 'expense' AND t.date >= ? AND t.date <= ?
      GROUP BY c.id
      ORDER BY totalAmount DESC
    `;
  return await db.getAllAsync<any>(query, monthStart, monthEnd);
};

export const getDailyExpenses = async (monthStart: number, monthEnd: number) => {
  if (!db) await initDatabase();
  const query = `
    SELECT strftime('%d', datetime(date, 'unixepoch', 'localtime')) as day, SUM(amount) as total
    FROM transactions
    WHERE type = 'expense' AND date >= ? AND date <= ?
    GROUP BY day
    ORDER BY day ASC
  `;
  const rows = await db.getAllAsync<{ day: string; total: number }>(query, monthStart, monthEnd);
  return rows.map((row) => ({
    day: Number.parseInt(row.day, 10),
    amount: row.total,
  }));
};
