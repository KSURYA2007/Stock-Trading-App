package stocktrading;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public final class TradingPlatform {
    private static final ZoneId MARKET_ZONE = ZoneId.of("Asia/Kolkata");
    public static final BigDecimal BROKERAGE_RATE = new BigDecimal("0.0003");
    public static final BigDecimal MAX_BROKERAGE = new BigDecimal("20.00");
    public static final BigDecimal GST_RATE = new BigDecimal("0.18");
    public static final int MAX_ORDER_QUANTITY = 10_000;

    private final Map<Integer, User> users = new HashMap<>();
    private final Map<Integer, TradingAccount> accounts = new HashMap<>();
    private final Map<Integer, BankAccount> bankAccounts = new HashMap<>();
    private final Map<Integer, Stock> stocks = new HashMap<>();
    private final Map<String, Integer> stockIdsBySymbol = new HashMap<>();
    private final Map<Integer, Watchlist> watchlists = new HashMap<>();
    private final Map<Integer, Order> orders = new HashMap<>();
    private final Map<Integer, Holding> holdings = new HashMap<>();
    private final Map<Integer, Trade> trades = new HashMap<>();
    private final Map<Integer, Charge> charges = new HashMap<>();
    private final Map<Integer, PriceAlert> alerts = new HashMap<>();
    private final Map<Integer, FundTransaction> fundTransactions = new HashMap<>();
    private final List<WatchlistItem> watchlistItems = new ArrayList<>();
    private final List<StockPriceHistory> priceHistory = new ArrayList<>();
    private final MarketHoursPolicy marketHours;
    private final Clock clock;
    private int nextUserId = 1;
    private int nextAccountId = 1;
    private int nextBankId = 1;
    private int nextStockId = 1;
    private int nextWatchlistId = 1;
    private int nextOrderId = 1;
    private int nextHoldingId = 1;
    private int nextItemId = 1;
    private int nextPriceId = 1;
    private int nextAlertId = 1;
    private int nextTransactionId = 1;
    private int nextTradeId = 1;
    private int nextChargeId = 1;

    public TradingPlatform() {
        this(new IndianMarketHours(), Clock.systemDefaultZone());
    }

    public Stock getStock(int stockId) throws TradingException {
        Stock stock = stocks.get(stockId);
        if (stock == null) {
            throw new TradingException("Stock not found.");
        }
        return stock;
    }

    public TradingPlatform(MarketHoursPolicy marketHours, Clock clock) {
        this.marketHours = Objects.requireNonNull(marketHours);
        this.clock = Objects.requireNonNull(clock);
        User admin = new User(nextUserId++, "Administrator", "admin@stock.local", "0000000000",
                Role.ADMIN, "admin123".toCharArray());
        users.put(admin.getUserId(), admin);
        addSeedStock("RELIANCE", "Reliance Industries", "Energy", "1450.00", admin.getUserId());
        addSeedStock("TCS", "Tata Consultancy Services", "Technology", "3800.00", admin.getUserId());
        addSeedStock("INFY", "Infosys", "Technology", "1550.00", admin.getUserId());
        addSeedStock("HDFCBANK", "HDFC Bank", "Banking", "1720.00", admin.getUserId());
    }

    public User register(String name, String email, String phone, char[] password,
                        String bankName, String bankAccountNumber, String ifscCode) throws TradingException {
        requireText(name, "Name");
        requireText(email, "Email");
        requireText(phone, "Phone");
        requireText(bankName, "Bank name");
        requireText(bankAccountNumber, "Bank account number");
        requireText(ifscCode, "IFSC code");
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (!normalizedEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new TradingException("Enter a valid email address.");
        }
        if (users.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(normalizedEmail))) {
            throw new TradingException("An account already uses that email.");
        }
        if (phone.trim().length() < 7) {
            throw new TradingException("Phone number must contain at least 7 characters.");
        }
        User user;
        try {
            user = new User(nextUserId++, name.trim(), normalizedEmail, phone.trim(),
                    Role.CUSTOMER, password);
        } catch (IllegalArgumentException exception) {
            throw new TradingException(exception.getMessage());
        }
        users.put(user.getUserId(), user);
        TradingAccount account = new TradingAccount(nextAccountId++, user.getUserId(), now());
        accounts.put(account.getId(), account);
        bankAccounts.put(nextBankId, new BankAccount(nextBankId++, user.getUserId(),
                bankAccountNumber.trim(), ifscCode.trim().toUpperCase(Locale.ROOT), bankName.trim()));
        Watchlist watchlist = new Watchlist(nextWatchlistId++, user.getUserId(), "My Watchlist");
        watchlists.put(watchlist.getWatchlistId(), watchlist);
        return user;
    }

    public User authenticate(String email, char[] password, Role requiredRole) throws TradingException {
        User user = users.values().stream()
                .filter(candidate -> candidate.getEmail().equalsIgnoreCase(email.trim()))
                .findFirst().orElse(null);
        if (user == null || !user.verifyPassword(password) || user.getRole() != requiredRole) {
            throw new TradingException("Invalid email or password.");
        }
        return user;
    }

    public User getUser(int userId) throws TradingException {
        User user = users.get(userId);
        if (user == null) {
            throw new TradingException("User not found.");
        }
        return user;
    }

    public TradingAccount getTradingAccount(int userId) throws TradingException {
        return accounts.values().stream().filter(account -> account.getUserId() == userId)
                .findFirst().orElseThrow(() -> new TradingException("Trading account not found."));
    }

    public List<User> getUsers() {
        return users.values().stream().sorted(Comparator.comparingInt(User::getUserId)).toList();
    }

    public List<TradingAccount> getAccounts() {
        return accounts.values().stream().sorted(Comparator.comparingInt(Account::getId)).toList();
    }

    public List<Stock> getStocks() {
        return stocks.values().stream().sorted(Comparator.comparing(Stock::getSymbol)).toList();
    }

    public List<Order> getOrdersForUser(int userId) throws TradingException {
        expirePendingOrders();
        int accountId = getTradingAccount(userId).getId();
        return orders.values().stream().filter(order -> order.getAccountId() == accountId)
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed()).toList();
    }

    public List<Order> getAllOrders() {
        expirePendingOrders();
        return orders.values().stream().sorted(Comparator.comparing(Order::getCreatedAt).reversed()).toList();
    }

    public List<Trade> getAllTrades() {
        return trades.values().stream().sorted(Comparator.comparing(Trade::getExecutedAt).reversed()).toList();
    }

    public List<Trade> getTradesForUser(int userId) throws TradingException {
        int accountId = getTradingAccount(userId).getId();
        return trades.values().stream()
                .filter(trade -> orders.get(trade.getOrderId()).getAccountId() == accountId)
                .sorted(Comparator.comparing(Trade::getExecutedAt).reversed()).toList();
    }

    public List<FundTransaction> getTransactionsForUser(int userId) throws TradingException {
        int accountId = getTradingAccount(userId).getId();
        return fundTransactions.values().stream()
                .filter(txn -> txn.getAccountId() == accountId)
                .sorted(Comparator.comparing(FundTransaction::getCreatedAt).reversed()).toList();
    }

    public List<PriceAlert> getAlertsForUser(int userId) {
        return alerts.values().stream().filter(alert -> alert.getUserId() == userId)
                .sorted(Comparator.comparing(PriceAlert::getCreatedAt).reversed()).toList();
    }

    public List<StockPriceHistory> getPriceHistory(int stockId) {
        return priceHistory.stream().filter(entry -> entry.getStockId() == stockId)
                .sorted(Comparator.comparing(StockPriceHistory::getUpdatedAt).reversed()).toList();
    }

    public void addFunds(int userId, BigDecimal amount) throws TradingException {
        validateMoney(amount);
        TradingAccount account = requireActiveAccount(userId);
        account.setAvailableBalance(account.getAvailableBalance().add(amount));
        recordFundTransaction(account, FundTransactionType.DEPOSIT, amount, "DEMO DEPOSIT");
    }

    public void withdrawFunds(int userId, BigDecimal amount) throws TradingException {
        validateMoney(amount);
        TradingAccount account = requireActiveAccount(userId);
        if (account.getKycStatus() != KycStatus.APPROVED) {
            throw new TradingException("KYC approval is required before withdrawing funds.");
        }
        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new TradingException("Insufficient available funds.");
        }
        account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
        recordFundTransaction(account, FundTransactionType.WITHDRAWAL, amount, "DEMO WITHDRAWAL");
    }

    public Order placeOrder(int userId, String symbol, OrderSide side, OrderType type,
                            int quantity, BigDecimal limitPrice) throws TradingException {
        expirePendingOrders();
        TradingAccount account = requireTradableAccount(userId);
        Stock stock = requireActiveStock(symbol);
        if (side == null || type == null) {
            throw new TradingException("Order side and type are required.");
        }
        if (quantity < 1 || quantity > MAX_ORDER_QUANTITY) {
            throw new TradingException("Order quantity must be between 1 and " + MAX_ORDER_QUANTITY + ".");
        }
        if (type == OrderType.LIMIT) {
            validateMoney(limitPrice);
        } else if (limitPrice != null) {
            throw new TradingException("Market orders cannot include a limit price.");
        }
        requireMarketOpen();
        if (side == OrderSide.SELL) {
            Holding holding = findHolding(account.getId(), stock.getStockId());
            if (holding == null || holding.getQuantity() < quantity) {
                throw new TradingException("Insufficient holdings. Short selling is not allowed.");
            }
        }
        BigDecimal estimate = type == OrderType.LIMIT ? limitPrice : stock.getCurrentPrice();
        if (side == OrderSide.BUY && totalDebit(estimate, quantity).compareTo(account.getAvailableBalance()) > 0) {
            throw new TradingException("Insufficient funds for order value plus charges.");
        }
        LocalDateTime created = now();
        Order order = new Order(nextOrderId++, account.getId(), stock.getStockId(), side, type,
                quantity, limitPrice, created, created.toLocalDate().atTime(LocalTime.of(15, 30)));
        orders.put(order.getOrderId(), order);
        tryExecuteOrder(order, stock);
        return order;
    }

    public void cancelOrder(int userId, int orderId) throws TradingException {
        expirePendingOrders();
        int accountId = getTradingAccount(userId).getId();
        Order order = orders.get(orderId);
        if (order == null || order.getAccountId() != accountId) {
            throw new TradingException("Order not found for this account.");
        }
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new TradingException("Only pending orders can be cancelled.");
        }
        order.setStatus(OrderStatus.CANCELLED);
    }

    public void addToWatchlist(int userId, String symbol) throws TradingException {
        Stock stock = requireStock(symbol);
        Watchlist watchlist = requireWatchlist(userId);
        boolean alreadyAdded = watchlistItems.stream().anyMatch(item ->
                item.getWatchlistId() == watchlist.getWatchlistId() && item.getStockId() == stock.getStockId());
        if (alreadyAdded) {
            throw new TradingException("That stock is already in your watchlist.");
        }
        watchlistItems.add(new WatchlistItem(nextItemId++, watchlist.getWatchlistId(),
                stock.getStockId(), now()));
    }

    public void removeFromWatchlist(int userId, String symbol) throws TradingException {
        Stock stock = requireStock(symbol);
        Watchlist watchlist = requireWatchlist(userId);
        boolean removed = watchlistItems.removeIf(item ->
                item.getWatchlistId() == watchlist.getWatchlistId() && item.getStockId() == stock.getStockId());
        if (!removed) {
            throw new TradingException("That stock is not in your watchlist.");
        }
    }

    public List<Stock> getWatchlistStocks(int userId) throws TradingException {
        Watchlist watchlist = requireWatchlist(userId);
        return watchlistItems.stream().filter(item -> item.getWatchlistId() == watchlist.getWatchlistId())
                .map(item -> stocks.get(item.getStockId()))
                .sorted(Comparator.comparing(Stock::getSymbol)).toList();
    }

    public List<Holding> getHoldings(int userId) throws TradingException {
        int accountId = getTradingAccount(userId).getId();
        return holdings.values().stream()
                .filter(holding -> holding.getAccountId() == accountId && holding.getQuantity() > 0)
                .sorted(Comparator.comparingInt(Holding::getStockId)).toList();
    }

    public BigDecimal getPortfolioValue(int userId) throws TradingException {
        return getHoldings(userId).stream()
                .map(holding -> stocks.get(holding.getStockId()).getCurrentPrice()
                        .multiply(BigDecimal.valueOf(holding.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getUnrealizedProfitLoss(int userId) throws TradingException {
        return getHoldings(userId).stream().map(holding -> {
            BigDecimal marketValue = stocks.get(holding.getStockId()).getCurrentPrice()
                    .multiply(BigDecimal.valueOf(holding.getQuantity()));
            BigDecimal cost = holding.getAverageBuyPrice().multiply(BigDecimal.valueOf(holding.getQuantity()));
            return marketValue.subtract(cost);
        }).reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2, RoundingMode.HALF_UP);
    }

    public PriceAlert setPriceAlert(int userId, String symbol, BigDecimal targetPrice,
                                    AlertDirection direction) throws TradingException {
        User user = getUser(userId);
        if (user.getRole() != Role.CUSTOMER) {
            throw new TradingException("Only customers can set price alerts.");
        }
        if (direction == null) {
            throw new TradingException("Alert direction is required.");
        }
        Stock stock = requireStock(symbol);
        validateMoney(targetPrice);
        PriceAlert alert = new PriceAlert(nextAlertId++, userId, stock.getStockId(),
                targetPrice, direction, now());
        alerts.put(alert.getAlertId(), alert);
        checkAlert(alert, stock);
        return alert;
    }

    public void approveKyc(int accountId, int adminId) throws TradingException {
        requireAdmin(adminId);
        TradingAccount account = accounts.get(accountId);
        if (account == null) {
            throw new TradingException("Trading account not found.");
        }
        account.setKycStatus(KycStatus.APPROVED);
        account.setApprovedBy(adminId);
    }

    public void blockAccount(int accountId, int adminId, boolean blocked) throws TradingException {
        requireAdmin(adminId);
        TradingAccount account = accounts.get(accountId);
        if (account == null) {
            throw new TradingException("Trading account not found.");
        }
        account.setAccountStatus(blocked ? AccountStatus.BLOCKED : AccountStatus.ACTIVE);
    }

    public Stock addStock(int adminId, String symbol, String companyName, String sector,
                          BigDecimal initialPrice) throws TradingException {
        requireAdmin(adminId);
        requireText(symbol, "Symbol");
        requireText(companyName, "Company name");
        requireText(sector, "Sector");
        validateMoney(initialPrice);
        String normalizedSymbol = symbol.trim().toUpperCase(Locale.ROOT);
        if (stockIdsBySymbol.containsKey(normalizedSymbol)) {
            throw new TradingException("That stock symbol already exists.");
        }
        Stock stock = new Stock(nextStockId++, normalizedSymbol, companyName.trim(), sector.trim(),
                initialPrice.setScale(2, RoundingMode.HALF_UP), adminId);
        stocks.put(stock.getStockId(), stock);
        stockIdsBySymbol.put(normalizedSymbol, stock.getStockId());
        priceHistory.add(new StockPriceHistory(nextPriceId++, stock.getStockId(),
                stock.getCurrentPrice(), now(), adminId));
        return stock;
    }

    public void updateStockPrice(int adminId, String symbol, BigDecimal price) throws TradingException {
        expirePendingOrders();
        requireAdmin(adminId);
        validateMoney(price);
        Stock stock = requireStock(symbol);
        stock.setCurrentPrice(price.setScale(2, RoundingMode.HALF_UP));
        priceHistory.add(new StockPriceHistory(nextPriceId++, stock.getStockId(),
                stock.getCurrentPrice(), now(), adminId));
        for (PriceAlert alert : alerts.values()) {
            if (alert.getStockId() == stock.getStockId() && !alert.isTriggered()) {
                checkAlert(alert, stock);
            }
        }
        processPendingOrders(stock);
    }

    public void setStockSuspended(int adminId, String symbol, boolean suspended) throws TradingException {
        requireAdmin(adminId);
        Stock stock = requireStock(symbol);
        stock.setStatus(suspended ? StockStatus.SUSPENDED : StockStatus.ACTIVE);
        if (suspended) {
            orders.values().stream()
                    .filter(order -> order.getStockId() == stock.getStockId()
                            && order.getStatus() == OrderStatus.PENDING)
                    .forEach(order -> order.setStatus(OrderStatus.CANCELLED));
        }
    }

    public List<String> getDailyStatement(int userId) throws TradingException {
        expirePendingOrders();
        int accountId = getTradingAccount(userId).getId();
        LocalDate today = LocalDate.now(clock.withZone(MARKET_ZONE));
        List<String> statement = new ArrayList<>();
        fundTransactions.values().stream()
                .filter(txn -> txn.getAccountId() == accountId && txn.getCreatedAt().toLocalDate().equals(today))
                .sorted(Comparator.comparing(FundTransaction::getCreatedAt))
                .forEach(txn -> statement.add(txn.getCreatedAt() + " FUNDS " + txn.getType()
                        + " " + money(txn.getAmount()) + " (" + txn.getStatus() + ")"));
        trades.values().stream().filter(trade -> trade.getExecutedAt().toLocalDate().equals(today))
                .filter(trade -> orders.get(trade.getOrderId()).getAccountId() == accountId)
                .sorted(Comparator.comparing(Trade::getExecutedAt))
                .forEach(trade -> {
                    Order order = orders.get(trade.getOrderId());
                    Charge charge = charges.values().stream()
                            .filter(item -> item.getTradeId() == trade.getTradeId()).findFirst().orElseThrow();
                    statement.add(trade.getExecutedAt() + " TRADE " + order.getSide() + " "
                            + stocks.get(order.getStockId()).getSymbol() + " x" + trade.getQuantity()
                            + " @ " + money(trade.getExecutionPrice()) + " value "
                            + money(trade.getTradeValue()) + " charges " + money(charge.getTotalCharge()));
                });
        return statement;
    }

    public static BigDecimal calculateBrokerage(BigDecimal tradeValue) {
        return tradeValue.multiply(BROKERAGE_RATE).min(MAX_BROKERAGE).setScale(2, RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateGst(BigDecimal brokerage) {
        return brokerage.multiply(GST_RATE).setScale(2, RoundingMode.HALF_UP);
    }

    public static String money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public boolean isMarketOpen() {
        return marketHours.isOpen(now());
    }

    private void addSeedStock(String symbol, String company, String sector, String price, int adminId) {
        try {
            addStock(adminId, symbol, company, sector, new BigDecimal(price));
        } catch (TradingException exception) {
            throw new IllegalStateException("Could not load sample stocks.", exception);
        }
    }

    private TradingAccount requireActiveAccount(int userId) throws TradingException {
        TradingAccount account = getTradingAccount(userId);
        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new TradingException("This trading account is blocked.");
        }
        return account;
    }

    private TradingAccount requireTradableAccount(int userId) throws TradingException {
        TradingAccount account = requireActiveAccount(userId);
        if (account.getKycStatus() != KycStatus.APPROVED) {
            throw new TradingException("KYC approval is required before trading.");
        }
        return account;
    }

    private Stock requireStock(String symbol) throws TradingException {
        if (symbol == null) {
            throw new TradingException("Stock symbol is required.");
        }
        Integer stockId = stockIdsBySymbol.get(symbol.trim().toUpperCase(Locale.ROOT));
        Stock stock = stockId == null ? null : stocks.get(stockId);
        if (stock == null) {
            throw new TradingException("Stock symbol not found.");
        }
        return stock;
    }

    private Stock requireActiveStock(String symbol) throws TradingException {
        Stock stock = requireStock(symbol);
        if (stock.getStatus() != StockStatus.ACTIVE) {
            throw new TradingException("This stock is suspended.");
        }
        return stock;
    }

    private Watchlist requireWatchlist(int userId) throws TradingException {
        return watchlists.values().stream().filter(list -> list.getUserId() == userId)
                .findFirst().orElseThrow(() -> new TradingException("Watchlist not found."));
    }

    private void requireAdmin(int userId) throws TradingException {
        if (getUser(userId).getRole() != Role.ADMIN) {
            throw new TradingException("Administrator access is required.");
        }
    }

    private void requireMarketOpen() throws TradingException {
        if (!marketHours.isOpen(now())) {
            throw new TradingException("Orders can only be placed during market hours (weekdays, 09:15-15:30 IST).");
        }
    }

    private static void validateMoney(BigDecimal amount) throws TradingException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0
                || amount.scale() > 2 || amount.precision() > 12) {
            throw new TradingException("Amount must be positive and have at most 2 decimal places.");
        }
    }

    private static void requireText(String value, String label) throws TradingException {
        if (value == null || value.isBlank()) {
            throw new TradingException(label + " is required.");
        }
    }

    private static BigDecimal totalDebit(BigDecimal price, int quantity) {
        BigDecimal value = price.multiply(BigDecimal.valueOf(quantity));
        return value.add(calculateBrokerage(value)).add(calculateGst(calculateBrokerage(value)));
    }

    private void recordFundTransaction(TradingAccount account, FundTransactionType type,
                                       BigDecimal amount, String mode) {
        FundTransaction transaction = new FundTransaction(nextTransactionId++, account.getId(),
                type, amount.setScale(2, RoundingMode.HALF_UP), mode, now());
        fundTransactions.put(transaction.getTransactionId(), transaction);
    }

    private void tryExecuteOrder(Order order, Stock stock) throws TradingException {
        OrderExecutionStrategy strategy = order.getOrderType() == OrderType.MARKET
                ? new MarketOrderExecutionStrategy() : new LimitOrderExecutionStrategy();
        if (strategy.canExecute(order, stock)) {
            executeOrder(order, stock);
        }
    }

    private void processPendingOrders(Stock stock) {
        if (!marketHours.isOpen(now()) || stock.getStatus() != StockStatus.ACTIVE) {
            return;
        }
        orders.values().stream()
                .filter(order -> order.getStockId() == stock.getStockId()
                        && order.getStatus() == OrderStatus.PENDING)
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .forEach(order -> {
                    try {
                        tryExecuteOrder(order, stock);
                    } catch (TradingException exception) {
                        order.setStatus(OrderStatus.REJECTED);
                    }
                });
    }

    private void executeOrder(Order order, Stock stock) throws TradingException {
        TradingAccount account = accounts.get(order.getAccountId());
        if (account.getAccountStatus() != AccountStatus.ACTIVE || account.getKycStatus() != KycStatus.APPROVED) {
            order.setStatus(OrderStatus.REJECTED);
            throw new TradingException("Account is not eligible to trade.");
        }
        BigDecimal executionPrice = stock.getCurrentPrice().setScale(2, RoundingMode.HALF_UP);
        BigDecimal value = executionPrice.multiply(BigDecimal.valueOf(order.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal brokerage = calculateBrokerage(value);
        BigDecimal gst = calculateGst(brokerage);
        BigDecimal chargeTotal = brokerage.add(gst).setScale(2, RoundingMode.HALF_UP);
        Holding holding = findHolding(account.getId(), stock.getStockId());
        if (order.getSide() == OrderSide.BUY) {
            BigDecimal debit = value.add(chargeTotal);
            if (account.getAvailableBalance().compareTo(debit) < 0) {
                order.setStatus(OrderStatus.REJECTED);
                throw new TradingException("Insufficient funds when the order became executable.");
            }
            account.setAvailableBalance(account.getAvailableBalance().subtract(debit));
            int previousQuantity = holding == null ? 0 : holding.getQuantity();
            BigDecimal previousCost = holding == null ? BigDecimal.ZERO
                    : holding.getAverageBuyPrice().multiply(BigDecimal.valueOf(previousQuantity));
            if (holding == null) {
                holding = new Holding(nextHoldingId++, account.getId(), stock.getStockId(),
                        order.getQuantity(), executionPrice);
                holdings.put(holding.getHoldingId(), holding);
            } else {
                int newQuantity = previousQuantity + order.getQuantity();
                holding.setAverageBuyPrice(previousCost.add(value)
                        .divide(BigDecimal.valueOf(newQuantity), 2, RoundingMode.HALF_UP));
                holding.setQuantity(newQuantity);
            }
            recordFundTransaction(account, FundTransactionType.BUY_DEBIT, debit, "TRADE");
        } else {
            if (holding == null || holding.getQuantity() < order.getQuantity()) {
                order.setStatus(OrderStatus.REJECTED);
                throw new TradingException("Insufficient holdings when the order became executable.");
            }
            BigDecimal credit = value.subtract(chargeTotal);
            if (credit.signum() < 0) {
                order.setStatus(OrderStatus.REJECTED);
                throw new TradingException("Charges exceed sale proceeds.");
            }
            holding.setQuantity(holding.getQuantity() - order.getQuantity());
            account.setAvailableBalance(account.getAvailableBalance().add(credit));
            recordFundTransaction(account, FundTransactionType.SELL_CREDIT, credit, "TRADE");
        }
        Trade trade = new Trade(nextTradeId++, order.getOrderId(), executionPrice,
                order.getQuantity(), value, now());
        trades.put(trade.getTradeId(), trade);
        Charge charge = new Charge(nextChargeId++, trade.getTradeId(), brokerage, gst, chargeTotal);
        charges.put(charge.getChargeId(), charge);
        order.setExecutionPrice(executionPrice);
        order.setStatus(OrderStatus.EXECUTED);
    }

    private Holding findHolding(int accountId, int stockId) {
        return holdings.values().stream().filter(holding ->
                holding.getAccountId() == accountId && holding.getStockId() == stockId)
                .findFirst().orElse(null);
    }

    private void checkAlert(PriceAlert alert, Stock stock) {
        boolean reached = alert.getDirection() == AlertDirection.ABOVE
                ? stock.getCurrentPrice().compareTo(alert.getTargetPrice()) >= 0
                : stock.getCurrentPrice().compareTo(alert.getTargetPrice()) <= 0;
        if (reached) {
            alert.setTriggered(true);
            alert.setTriggeredAt(now());
        }
    }

    private void expirePendingOrders() {
        LocalDateTime currentTime = now();
        orders.values().stream()
                .filter(order -> order.getStatus() == OrderStatus.PENDING
                        && !order.getExpiresAt().isAfter(currentTime))
                .forEach(order -> order.setStatus(OrderStatus.CANCELLED));
    }

    private LocalDateTime now() {
        return LocalDateTime.ofInstant(clock.instant(), MARKET_ZONE);
    }
}
