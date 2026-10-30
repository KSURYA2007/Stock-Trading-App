# Java Stock Trading App

A Java 21 console application implementing the 13 entities in the supplied ER diagram:
`User`, `BankAccount`, `TradingAccount`, `Watchlist`, `Stock`, `FundTransaction`,
`Order`, `Holding`, `WatchlistItem`, `StockPriceHistory`, `PriceAlert`, `Trade`, and `Charge`.

All application state lives in `HashMap` and `ArrayList` collections and is reset when the
process exits. The bank and fund flows are a local simulation; no real payments are made.

## Requirements and run

Install a JDK 21 and open the repository folder in VS Code. In the integrated PowerShell
terminal, run:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp out stocktrading.Main
```

Default demo administrator credentials:

- Email: `admin@stock.local`
- Password: `admin123`

Register a customer, then use the admin login to approve that customer's KYC before placing
orders. The app comes with sample RELIANCE, TCS, INFY, and HDFCBANK prices. Admin price
updates are also how the console demo triggers pending limit orders and price alerts.

## Implemented behavior

- Customer registration/login, password hashing, profile and account status.
- Admin KYC approval, stock listing/prices/status, account blocking, and user/order/trade views.
- Simulated deposits/withdrawals, balances, fund transaction history, and daily statements.
- Market and limit buy/sell orders, pending-order cancellation, holdings, portfolio valuation,
  watchlists, price alerts, trade records, brokerage, and GST.
- KYC and account status gates, funds and input validation, weekday trading hours, quantity limits,
  suspended-stock restrictions, and prevention of short sales.

The uploaded ER diagram was available, but a separate problem statement was not attached.
Accordingly, unspecified numeric business rules use these documented demo defaults:

- Market hours: Monday-Friday, 09:15 inclusive to 15:30 exclusive, India Standard Time.
- Maximum order quantity: 10,000 shares.
- Brokerage: 0.03% of trade value, capped at Rs. 20 per executed order.
- GST: 18% of brokerage.
- No other statutory charges, slippage, exchange connectivity, persistence, or order reservations
  are simulated. Limit orders are checked when entered and when an admin updates a price; unmatched
  orders expire at 15:30 IST and are marked expired (cancelled) the next time orders or market
  prices are processed. Pending orders are not reserved against cash or shares; balances/holdings
  are revalidated on execution.

## OOP and tests

`Account` is an abstract base class for `BankAccount` and `TradingAccount`; their overridden
`getAccountType()` methods demonstrate polymorphism. `MarketHoursPolicy` and
`OrderExecutionStrategy` are interfaces with separate implementations. Entity fields are
encapsulated, with constructors and accessors.

Run the standalone Java test suite from the repository root:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
javac -d out (Get-ChildItem -Path src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
java -cp out stocktrading.TradingPlatformTest
```
