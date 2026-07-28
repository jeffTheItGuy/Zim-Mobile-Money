export const fmtCurrency = (amount, currency = 'USD') => {
  if (amount == null) return '-'
  return new Intl.NumberFormat('en-ZW', { style: 'currency', currency }).format(amount)
}

export const fmtDate = (iso) => {
  if (!iso) return '-'
  return new Date(iso).toLocaleString('en-ZW')
}

export const txTypeLabel = (type) => {
  const map = { CASH_IN: 'Cash In', CASH_OUT: 'Cash Out', TRANSFER: 'Transfer', PAYMENT: 'Payment', AIRTIME: 'Airtime', BILL_PAY: 'Bill Pay', REFUND: 'Refund' }
  return map[type] || type
}

export const txStatusClass = (status) => {
  const map = { COMPLETED: 'badge-green', PENDING: 'badge-yellow', FAILED: 'badge-red', REVERSED: 'badge-blue' }
  return map[status] || 'badge-yellow'
}
