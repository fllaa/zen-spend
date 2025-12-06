export type NumberFormat = 'dot' | 'comma';

export const getCurrencySymbol = (currency: string): string => {
  switch (currency) {
    case 'USD':
      return '$';
    case 'IDR':
      return 'Rp';
    case 'JPY':
      return '¥';
    default:
      return '$';
  }
};

export const formatCurrency = (
  amount: number,
  currency: string,
  numberFormat: NumberFormat
): string => {
  const symbol = getCurrencySymbol(currency);
  
  // Format the number based on preference
  let formattedNumber = '';
  
  if (numberFormat === 'dot') {
    // 1,000.00
    formattedNumber = amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  } else {
    // 1.000,00
    // First fix to 2 decimal places
    const parts = amount.toFixed(2).split('.');
    // Add dots for thousands
    parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
    // Join with comma
    formattedNumber = parts.join(',');
  }

  // Remove decimals for JPY as it's typically not used
  if (currency === 'JPY') {
      formattedNumber = formattedNumber.split(/[.,]/)[0];
  }

  return `${symbol}${formattedNumber}`;
};
