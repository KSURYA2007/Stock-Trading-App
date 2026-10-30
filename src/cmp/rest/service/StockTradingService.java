package cmp.rest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class StockTradingService {
    private static final ZoneId MARKET_ZONE = ZoneId.of("Asia/Kolkata");
    private static final LocalTime MARKET_OPEN = LocalTime.of(9, 15);
    private static final LocalTime MARKET_CLOSE = LocalTime.of(15, 30);
    private static final BigDecimal MIN_FUNDING = new BigDecimal("100.00");
    private static final BigDecimal MAX_FUNDING = new BigDecimal("100000.00");
    private static final BigDecimal BROKERAGE_RATE = new BigDecimal("0.0005");
    private static final BigDecimal MAX_BROKERAGE = new BigDecimal("20.00");
    private static final BigDecimal GST_RATE = new BigDecimal("0.18");

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, String> accountIdByPan = new HashMap<>();
    private final Map<String, Stock> stocks = new HashMap<>();
    private final Map<String, Order> orders = new HashMap<>();
    private final Map<String, Holding> holdings = new HashMap<>();
    private final Map<String, PriceAlert> alerts = new HashMap<>();
    private final Map<String, List<PriceHistoryView>> priceHistory = new HashMap<>();
    private final List<TradeView> trades = new ArrayList<>();
    private final List<ChargeView> charges = new ArrayList<>();
    private final List<FundTransactionView> fundTransactions = new ArrayList<>();

    public record UserView(
            String accountId, String name, String pan, String email, String phone,
            KycStatus kycStatus, boolean blocked, BigDecimal availableBalance,
            BigDecimal blockedBalance) {}

    public record BankAccountView(String bankAccountId, String accountNumber, String bankName) {}

    public record StockView(String symbol, String companyName, Sector sector,
                            BigDecimal currentPrice, BigDecimal previousClose, StockStatus status) {}

    public record PriceHistoryView(String symbol, BigDecimal price, LocalDateTime updatedAt) {}

    public record HoldingView(String symbol, int quantity, BigDecimal averageBuyPrice,
                              BigDecimal currentPrice, BigDecimal investedValue,
                              BigDecimal currentValue, BigDecimal unrealizedProfitLoss) {}

    public record PortfolioView(List<HoldingView> holdings, BigDecimal investedValue,
                                BigDecimal currentValue, BigDecimal profitLoss,
                                BigDecimal returnPercentage) {}

    public record OrderView(String orderId, String accountId, String symbol, OrderSide side,
                            OrderType type, int quantity, BigDecimal limitPrice,
                            BigDecimal executionPrice, OrderStatus status,
                            String rejectionReason, LocalDateTime createdAt, LocalDateTime updatedAt) {}

    public record TradeView(String tradeId, String orderId, String accountId, String symbol,
                            OrderSide side, BigDecimal price, int quantity,
                            BigDecimal tradeValue, BigDecimal realizedProfitLoss,
                            LocalDateTime executedAt) {}

    public record ChargeView(String chargeId, String tradeId, String orderId,
                             BigDecimal brokerage, BigDecimal gst, BigDecimal total,
                             LocalDateTime chargedAt) {}

    public record PriceAlertView(String alertId, String accountId, String symbol,
                                 BigDecimal targetPrice, boolean triggered,
                                 LocalDateTime triggeredAt) {}

    public record FundTransactionView(String transactionId, String accountId,
                                      String bankAccountId, BigDecimal amount,
                                      FundTransactionType type, LocalDateTime createdAt) {}

    public record DailyStatement(LocalDate date, List<OrderView> orders,
                                 List<TradeView> trades, List<ChargeView> charges) {}

    public enum KycStatus { PENDING, APPROVED }
    public enum Sector { IT, BANKING, PHARMA, ENERGY, AUTO }
    public enum StockStatus { ACTIVE, SUSPENDED }
    public enum OrderSide { BUY, SELL }
    public enum OrderType { MARKET, LIMIT }
    public enum OrderStatus { PENDING, EXECUTED, CANCELLED, REJECTED }
    public enum FundTransactionType { DEPOSIT, WITHDRAWAL }

    private static final class Account {
        private final String id;
        private final String name;
        private final String pan;
        private final String email;
        private final String phone;
        private final Map<String, BankAccountView> bankAccounts = new HashMap<>();
        private final List<String> watchlist = new ArrayList<>();
        private KycStatus kycStatus = KycStatus.PENDING;
        private boolean blocked;
        private BigDecimal availableBalance = money(BigDecimal.ZERO);
        private BigDecimal blockedBalance = money(BigDecimal.ZERO);

        private Account(String id, String name, String pan, String email, String phone) {
            this.id = id;
            this.name = name;
            this.pan = pan;
            this.email = email;
            this.phone = phone;
        }
    }

    private static final class Stock {
        private final String symbol;
        private final String companyName;
        private final Sector sector;
        private BigDecimal currentPrice;
        private BigDecimal previousClose;
        private LocalDate priceDate;
        private StockStatus status = StockStatus.ACTIVE;

        private Stock(String symbol, String companyName, Sector sector, BigDecimal price, LocalDate date) {
            this.symbol = symbol;
            this.companyName = companyName;
            this.sector = sector;
            this.currentPrice = price;
            this.previousClose = price;
            this.priceDate = date;
        }
    }

    private static final class Order {
        private final String id;
        private final String accountId;
        private final String symbol;
        private final OrderSide side;
        private final OrderType type;
        private final int quantity;
        private final BigDecimal limitPrice;
        private final LocalDateTime createdAt;
        private BigDecimal executionPrice;
        private String rejectionReason;
        private OrderStatus status;
        private LocalDateTime updatedAt;
        private BigDecimal reservedFunds = money(BigDecimal.ZERO);
        private int reservedShares;

        private Order(String id, String accountId, String symbol, OrderSide side, OrderType type,
                      int quantity, BigDecimal limitPrice, LocalDateTime createdAt,
                      OrderStatus status) {
            this.id = id;
            this.accountId = accountId;
            this.symbol = symbol;
            this.side = side;
            this.type = type;
            this.quantity = quantity;
            this.limitPrice = limitPrice;
            this.createdAt = createdAt;
            this.updatedAt = createdAt;
            this.status = status;
        }
    }

    private static final class Holding {
        private final String accountId;
        private final String symbol;
        private int quantity;
        private BigDecimal averageBuyPrice;

        private Holding(String accountId, String symbol, int quantity, BigDecimal averageBuyPrice) {
            this.accountId = accountId;
            this.symbol = symbol;
            this.quantity = quantity;
            this.averageBuyPrice = averageBuyPrice;
        }
    }

    private static final class PriceAlert {
        private final String id;
        private final String accountId;
        private final String symbol;
        private final BigDecimal targetPrice;
        private boolean triggered;
        private LocalDateTime triggeredAt;

        private PriceAlert(String id, String accountId, String symbol, BigDecimal targetPrice) {
            this.id = id;
            this.accountId = accountId;
            this.symbol = symbol;
            this.targetPrice = targetPrice;
        }
    }

    public synchronized UserView registerUser(
            String name, String pan, String email, String phone) {
        requireText(name, "name");
        requireText(pan, "PAN");
        requireText(email, "email");
        requireText(phone, "phone");
        String normalizedPan = pan.trim().toUpperCase();
        if (accountIdByPan.containsKey(normalizedPan)) {
            throw new IllegalArgumentException("PAN is already registered");
        }

        Account account = new Account(id(), name.trim(), normalizedPan, email.trim(), phone.trim());
        accounts.put(account.id, account);
        accountIdByPan.put(normalizedPan, account.id);
        return userView(account);
    }

    public synchronized UserView approveKyc(String accountId, boolean admin) {
        requireAdmin(admin);
        Account account = account(accountId);
        account.kycStatus = KycStatus.APPROVED;
        return userView(account);
    }

    public synchronized UserView setAccountBlocked(String accountId, boolean blocked, boolean admin) {
        requireAdmin(admin);
        Account account = account(accountId);
        account.blocked = blocked;
        return userView(account);
    }

    public synchronized BankAccountView linkBankAccount(
            String accountId, String accountNumber, String bankName) {
        Account account = account(accountId);
        requireText(accountNumber, "bank account number");
        requireText(bankName, "bank name");
        BankAccountView bank = new BankAccountView(id(), accountNumber.trim(), bankName.trim());
        account.bankAccounts.put(bank.bankAccountId(), bank);
        return bank;
    }

    public synchronized FundTransactionView addFunds(
            String accountId, String bankAccountId, BigDecimal amount, LocalDateTime at) {
        Account account = account(accountId);
        if (!account.bankAccounts.containsKey(bankAccountId)) {
            throw new IllegalArgumentException("Bank account is not linked to this trading account");
        }
        BigDecimal value = money(amount);
        if (value.compareTo(MIN_FUNDING) < 0 || value.compareTo(MAX_FUNDING) > 0) {
            throw new IllegalArgumentException("A deposit must be between Rs. 100 and Rs. 1,00,000");
        }
        account.availableBalance = account.availableBalance.add(value);
        FundTransactionView transaction = new FundTransactionView(
                id(), accountId, bankAccountId, value, FundTransactionType.DEPOSIT, at);
        fundTransactions.add(transaction);
        return transaction;
    }

    public synchronized FundTransactionView withdrawFunds(
            String accountId, String bankAccountId, BigDecimal amount, LocalDateTime at) {
        Account account = account(accountId);
        if (!account.bankAccounts.containsKey(bankAccountId)) {
            throw new IllegalArgumentException("Bank account is not linked to this trading account");
        }
        BigDecimal value = positiveMoney(amount, "withdrawal amount");
        if (account.availableBalance.compareTo(value) < 0) {
            throw new IllegalStateException("Insufficient available funds");
        }
        account.availableBalance = account.availableBalance.subtract(value);
        FundTransactionView transaction = new FundTransactionView(
                id(), accountId, bankAccountId, value, FundTransactionType.WITHDRAWAL, at);
        fundTransactions.add(transaction);
        return transaction;
    }

    public synchronized StockView listStock(
            String symbol, String companyName, Sector sector, BigDecimal price,
            boolean admin, LocalDateTime at) {
        requireAdmin(admin);
        String normalizedSymbol = normalizeSymbol(symbol);
        requireText(companyName, "company name");
        Objects.requireNonNull(sector, "sector is required");
        if (stocks.containsKey(normalizedSymbol)) {
            throw new IllegalArgumentException("Stock symbol is already listed");
        }
        Stock stock = new Stock(normalizedSymbol, companyName.trim(), sector,
                positiveMoney(price, "stock price"), at.toLocalDate());
        stocks.put(normalizedSymbol, stock);
        priceHistory.put(normalizedSymbol,
                new ArrayList<>(List.of(new PriceHistoryView(normalizedSymbol, stock.currentPrice, at))));
        return stockView(stock);
    }

    public synchronized StockView updateStockPrice(
            String symbol, BigDecimal newPrice, boolean admin, LocalDateTime at) {
        requireAdmin(admin);
        Stock stock = stock(symbol);
        BigDecimal price = positiveMoney(newPrice, "stock price");
        LocalDate updateDate = at.toLocalDate();
        BigDecimal previousClose = stock.priceDate.equals(updateDate)
                ? stock.previousClose : stock.currentPrice;
        BigDecimal lowerLimit = previousClose.multiply(new BigDecimal("0.90"));
        BigDecimal upperLimit = previousClose.multiply(new BigDecimal("1.10"));
        if (price.compareTo(lowerLimit) < 0 || price.compareTo(upperLimit) > 0) {
            throw new IllegalArgumentException("Daily price change cannot exceed 10% from previous close");
        }

        BigDecimal previousPrice = stock.currentPrice;
        stock.previousClose = previousClose;
        stock.priceDate = updateDate;
        stock.currentPrice = price;
        priceHistory.get(stock.symbol).add(new PriceHistoryView(stock.symbol, price, at));
        triggerAlerts(stock, previousPrice, price, at);
        if (stock.status == StockStatus.ACTIVE && isMarketOpen(at)) {
            executeEligibleLimitOrders(stock, at);
        }
        expirePendingLimitOrders(at);
        return stockView(stock);
    }

    public synchronized StockView setStockStatus(
            String symbol, StockStatus status, boolean admin, LocalDateTime at) {
        requireAdmin(admin);
        Stock stock = stock(symbol);
        stock.status = Objects.requireNonNull(status, "status is required");
        if (status == StockStatus.SUSPENDED) {
            for (Order order : orders.values()) {
                if (order.status == OrderStatus.PENDING && order.symbol.equals(stock.symbol)) {
                    cancelPendingOrder(order, at);
                }
            }
        }
        return stockView(stock);
    }

    public synchronized boolean addToWatchlist(String accountId, String symbol) {
        Account account = account(accountId);
        String normalizedSymbol = normalizeSymbol(symbol);
        stock(normalizedSymbol);
        if (account.watchlist.contains(normalizedSymbol)) {
            return false;
        }
        if (account.watchlist.size() >= 20) {
            throw new IllegalStateException("A watchlist can contain at most 20 stocks");
        }
        account.watchlist.add(normalizedSymbol);
        return true;
    }

    public synchronized boolean removeFromWatchlist(String accountId, String symbol) {
        return account(accountId).watchlist.remove(normalizeSymbol(symbol));
    }

    public synchronized List<StockView> getWatchlist(String accountId) {
        Account account = account(accountId);
        return account.watchlist.stream().map(this::stock).map(StockTradingService::stockView).toList();
    }

    public synchronized OrderView placeOrder(
            String accountId, String symbol, OrderSide side, OrderType type,
            int quantity, BigDecimal limitPrice, LocalDateTime at) {
        Objects.requireNonNull(side, "order side is required");
        Objects.requireNonNull(type, "order type is required");
        expirePendingLimitOrders(at);
        String normalizedSymbol = normalizeSymbol(symbol);
        Stock stock = stock(normalizedSymbol);
        Account account = account(accountId);
        validateOrderParameters(type, quantity, limitPrice);

        BigDecimal normalizedLimit = type == OrderType.LIMIT ? money(limitPrice) : null;
        Order order = new Order(id(), accountId, normalizedSymbol, side, type, quantity,
                normalizedLimit, at, OrderStatus.PENDING);
        orders.put(order.id, order);
        String rejectionReason = orderRejectionReason(account, stock, order, at);
        if (rejectionReason != null) {
            rejectOrder(order, rejectionReason, at);
            return orderView(order);
        }

        if (side == OrderSide.BUY) {
            BigDecimal reservePrice = type == OrderType.LIMIT ? normalizedLimit : stock.currentPrice;
            order.reservedFunds = totalBuyCost(reservePrice, quantity);
            if (account.availableBalance.compareTo(order.reservedFunds) < 0) {
                rejectOrder(order, "Insufficient available funds", at);
                return orderView(order);
            }
            account.availableBalance = account.availableBalance.subtract(order.reservedFunds);
            account.blockedBalance = account.blockedBalance.add(order.reservedFunds);
        } else {
            Holding holding = holdings.get(holdingKey(accountId, normalizedSymbol));
            int heldShares = holding == null ? 0 : holding.quantity;
            int availableShares = heldShares - pendingSellShares(accountId, normalizedSymbol);
            if (availableShares < quantity) {
                rejectOrder(order, "Insufficient shares not reserved by pending sell orders", at);
                return orderView(order);
            }
            order.reservedShares = quantity;
        }

        if (type == OrderType.MARKET || limitPriceReached(order, stock.currentPrice)) {
            executeOrder(order, stock.currentPrice, at);
        }
        return orderView(order);
    }

    public synchronized OrderView cancelOrder(String accountId, String orderId, LocalDateTime at) {
        Order order = order(orderId);
        if (!order.accountId.equals(accountId)) {
            throw new IllegalArgumentException("Order does not belong to this account");
        }
        if (order.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Only a pending order can be cancelled");
        }
        cancelPendingOrder(order, at);
        return orderView(order);
    }

    public synchronized List<OrderView> getOrders(String accountId) {
        account(accountId);
        return orders.values().stream()
                .filter(order -> order.accountId.equals(accountId))
                .sorted(Comparator.comparing(order -> order.createdAt))
                .map(StockTradingService::orderView)
                .toList();
    }

    public synchronized List<TradeView> getTrades(String accountId) {
        account(accountId);
        return trades.stream().filter(trade -> trade.accountId().equals(accountId)).toList();
    }

    public synchronized PortfolioView getPortfolio(String accountId) {
        account(accountId);
        List<HoldingView> accountHoldings = holdings.values().stream()
                .filter(holding -> holding.accountId.equals(accountId))
                .map(this::holdingView)
                .sorted(Comparator.comparing(HoldingView::symbol))
                .toList();
        BigDecimal invested = accountHoldings.stream().map(HoldingView::investedValue)
                .reduce(money(BigDecimal.ZERO), BigDecimal::add);
        BigDecimal current = accountHoldings.stream().map(HoldingView::currentValue)
                .reduce(money(BigDecimal.ZERO), BigDecimal::add);
        BigDecimal profitLoss = current.subtract(invested);
        BigDecimal returnPercentage = invested.signum() == 0
                ? money(BigDecimal.ZERO)
                : profitLoss.multiply(new BigDecimal("100"))
                    .divide(invested, 2, RoundingMode.HALF_UP);
        return new PortfolioView(accountHoldings, invested, current, profitLoss, returnPercentage);
    }

    public synchronized PriceAlertView createPriceAlert(
            String accountId, String symbol, BigDecimal targetPrice) {
        account(accountId);
        Stock stock = stock(symbol);
        BigDecimal target = positiveMoney(targetPrice, "target price");
        long activeAlerts = alerts.values().stream()
                .filter(alert -> alert.accountId.equals(accountId) && !alert.triggered)
                .count();
        if (activeAlerts >= 10) {
            throw new IllegalStateException("An account can have at most 10 active price alerts");
        }
        PriceAlert alert = new PriceAlert(id(), accountId, stock.symbol, target);
        alerts.put(alert.id, alert);
        return alertView(alert);
    }

    public synchronized List<PriceAlertView> getPriceAlerts(String accountId) {
        account(accountId);
        return alerts.values().stream()
                .filter(alert -> alert.accountId.equals(accountId))
                .map(StockTradingService::alertView)
                .toList();
    }

    public synchronized DailyStatement getDailyStatement(String accountId, LocalDate date) {
        account(accountId);
        List<OrderView> dailyOrders = orders.values().stream()
                .filter(order -> order.accountId.equals(accountId)
                        && order.createdAt.toLocalDate().equals(date))
                .map(StockTradingService::orderView)
                .toList();
        List<TradeView> dailyTrades = trades.stream()
                .filter(trade -> trade.accountId().equals(accountId)
                        && trade.executedAt().toLocalDate().equals(date))
                .toList();
        List<ChargeView> dailyCharges = charges.stream()
                .filter(charge -> dailyTrades.stream()
                        .anyMatch(trade -> trade.tradeId().equals(charge.tradeId())))
                .toList();
        return new DailyStatement(date, dailyOrders, dailyTrades, dailyCharges);
    }

    public synchronized List<FundTransactionView> getFundTransactions(String accountId) {
        account(accountId);
        return fundTransactions.stream()
                .filter(transaction -> transaction.accountId().equals(accountId))
                .toList();
    }

    public synchronized UserView getAccount(String accountId) {
        return userView(account(accountId));
    }

    public synchronized List<StockView> getStocks() {
        return stocks.values().stream().map(StockTradingService::stockView).toList();
    }

    public synchronized List<PriceHistoryView> getPriceHistory(String symbol) {
        return List.copyOf(priceHistory.get(stock(symbol).symbol));
    }

    public synchronized void expirePendingLimitOrders(LocalDateTime at) {
        for (Order order : orders.values()) {
            boolean previousTradingDay = order.createdAt.toLocalDate().isBefore(at.toLocalDate());
            boolean marketClosed = !at.toLocalTime().isBefore(MARKET_CLOSE);
            if (order.type == OrderType.LIMIT && order.status == OrderStatus.PENDING
                    && (previousTradingDay || marketClosed)) {
                cancelPendingOrder(order, at);
            }
        }
    }

    public static boolean isMarketOpen(LocalDateTime at) {
        DayOfWeek day = at.atZone(MARKET_ZONE).getDayOfWeek();
        LocalTime time = at.atZone(MARKET_ZONE).toLocalTime();
        return day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY
                && !time.isBefore(MARKET_OPEN) && !time.isAfter(MARKET_CLOSE);
    }

    private void validateOrderParameters(OrderType type, int quantity, BigDecimal limitPrice) {
        if (quantity < 1 || quantity > 1000) {
            throw new IllegalArgumentException("Order quantity must be between 1 and 1,000");
        }
        if (type == OrderType.LIMIT) {
            positiveMoney(limitPrice, "limit price");
        } else if (limitPrice != null) {
            throw new IllegalArgumentException("Market orders must not include a limit price");
        }
    }

    private String orderRejectionReason(Account account, Stock stock, Order order, LocalDateTime at) {
        if (!isMarketOpen(at)) {
            return "Orders can only be placed during market hours";
        }
        if (stock.status != StockStatus.ACTIVE) {
            return "A suspended stock cannot be traded";
        }
        if (account.kycStatus != KycStatus.APPROVED) {
            return "KYC approval is required before trading";
        }
        if (order.side == OrderSide.BUY && account.blocked) {
            return "This account is blocked from placing buy orders";
        }
        long pendingCount = orders.values().stream()
                .filter(existing -> existing.accountId.equals(order.accountId)
                        && existing.status == OrderStatus.PENDING)
                .count();
        if (pendingCount > 25) {
            return "An account can have at most 25 pending orders";
        }
        return null;
    }

    private void executeEligibleLimitOrders(Stock stock, LocalDateTime at) {
        List<Order> eligible = orders.values().stream()
                .filter(order -> order.status == OrderStatus.PENDING
                        && order.type == OrderType.LIMIT
                        && order.symbol.equals(stock.symbol)
                        && limitPriceReached(order, stock.currentPrice))
                .sorted(Comparator.comparing(order -> order.createdAt))
                .toList();
        for (Order order : eligible) {
            executeOrder(order, stock.currentPrice, at);
        }
    }

    private boolean limitPriceReached(Order order, BigDecimal price) {
        return order.side == OrderSide.BUY
                ? price.compareTo(order.limitPrice) <= 0
                : price.compareTo(order.limitPrice) >= 0;
    }

    private void executeOrder(Order order, BigDecimal price, LocalDateTime at) {
        Account account = account(order.accountId);
        BigDecimal tradeValue = money(price.multiply(BigDecimal.valueOf(order.quantity)));
        BigDecimal brokerage = money(tradeValue.multiply(BROKERAGE_RATE).min(MAX_BROKERAGE));
        BigDecimal gst = money(brokerage.multiply(GST_RATE));
        BigDecimal totalCharge = brokerage.add(gst);
        BigDecimal realizedProfitLoss = money(BigDecimal.ZERO);

        if (order.side == OrderSide.BUY) {
            BigDecimal totalDebit = tradeValue.add(totalCharge);
            if (totalDebit.compareTo(order.reservedFunds) > 0) {
                throw new IllegalStateException("Reserved funds are insufficient at execution price");
            }
            account.blockedBalance = account.blockedBalance.subtract(order.reservedFunds);
            account.availableBalance = account.availableBalance.add(order.reservedFunds.subtract(totalDebit));
            addBuyHolding(order.accountId, order.symbol, order.quantity, price);
        } else {
            Holding holding = holding(order.accountId, order.symbol);
            if (holding.quantity < order.quantity) {
                throw new IllegalStateException("Insufficient shares at execution");
            }
            realizedProfitLoss = money(price.subtract(holding.averageBuyPrice)
                    .multiply(BigDecimal.valueOf(order.quantity)));
            account.availableBalance = account.availableBalance.add(tradeValue.subtract(totalCharge));
            holding.quantity -= order.quantity;
            if (holding.quantity == 0) {
                holdings.remove(holdingKey(order.accountId, order.symbol));
            }
            order.reservedShares = 0;
        }

        TradeView trade = new TradeView(id(), order.id, order.accountId, order.symbol,
                order.side, price, order.quantity, tradeValue, realizedProfitLoss, at);
        trades.add(trade);
        charges.add(new ChargeView(id(), trade.tradeId(), order.id, brokerage, gst, totalCharge, at));
        order.executionPrice = price;
        order.status = OrderStatus.EXECUTED;
        order.updatedAt = at;
    }

    private void addBuyHolding(String accountId, String symbol, int quantity, BigDecimal price) {
        String key = holdingKey(accountId, symbol);
        Holding holding = holdings.get(key);
        if (holding == null) {
            holdings.put(key, new Holding(accountId, symbol, quantity, price));
            return;
        }
        BigDecimal oldCost = holding.averageBuyPrice.multiply(BigDecimal.valueOf(holding.quantity));
        BigDecimal newCost = price.multiply(BigDecimal.valueOf(quantity));
        holding.quantity += quantity;
        holding.averageBuyPrice = oldCost.add(newCost)
                .divide(BigDecimal.valueOf(holding.quantity), 8, RoundingMode.HALF_UP);
    }

    private int pendingSellShares(String accountId, String symbol) {
        return orders.values().stream()
                .filter(order -> order.accountId.equals(accountId)
                        && order.symbol.equals(symbol)
                        && order.side == OrderSide.SELL
                        && order.status == OrderStatus.PENDING)
                .mapToInt(order -> order.reservedShares)
                .sum();
    }

    private BigDecimal totalBuyCost(BigDecimal price, int quantity) {
        BigDecimal value = money(price.multiply(BigDecimal.valueOf(quantity)));
        BigDecimal brokerage = money(value.multiply(BROKERAGE_RATE).min(MAX_BROKERAGE));
        BigDecimal gst = money(brokerage.multiply(GST_RATE));
        return value.add(brokerage).add(gst);
    }

    private void cancelPendingOrder(Order order, LocalDateTime at) {
        Account account = account(order.accountId);
        if (order.side == OrderSide.BUY) {
            account.blockedBalance = account.blockedBalance.subtract(order.reservedFunds);
            account.availableBalance = account.availableBalance.add(order.reservedFunds);
            order.reservedFunds = money(BigDecimal.ZERO);
        } else {
            order.reservedShares = 0;
        }
        order.status = OrderStatus.CANCELLED;
        order.updatedAt = at;
    }

    private void triggerAlerts(Stock stock, BigDecimal previousPrice,
                               BigDecimal newPrice, LocalDateTime at) {
        for (PriceAlert alert : alerts.values()) {
            if (alert.triggered || !alert.symbol.equals(stock.symbol)) {
                continue;
            }
            int previousCompare = previousPrice.compareTo(alert.targetPrice);
            int newCompare = newPrice.compareTo(alert.targetPrice);
            if ((previousCompare < 0 && newCompare >= 0)
                    || (previousCompare > 0 && newCompare <= 0)
                    || (previousCompare == 0 && newCompare != 0)) {
                alert.triggered = true;
                alert.triggeredAt = at;
            }
        }
    }

    private HoldingView holdingView(Holding holding) {
        BigDecimal currentPrice = stock(holding.symbol).currentPrice;
        BigDecimal investedValue = money(holding.averageBuyPrice
                .multiply(BigDecimal.valueOf(holding.quantity)));
        BigDecimal currentValue = money(currentPrice.multiply(BigDecimal.valueOf(holding.quantity)));
        return new HoldingView(holding.symbol, holding.quantity, holding.averageBuyPrice,
                currentPrice, investedValue, currentValue, currentValue.subtract(investedValue));
    }

    private Account account(String accountId) {
        Account account = accounts.get(accountId);
        if (account == null) {
            throw new IllegalArgumentException("Trading account not found");
        }
        return account;
    }

    private Stock stock(String symbol) {
        Stock stock = stocks.get(normalizeSymbol(symbol));
        if (stock == null) {
            throw new IllegalArgumentException("Stock not found");
        }
        return stock;
    }

    private Order order(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }
        return order;
    }

    private Holding holding(String accountId, String symbol) {
        Holding holding = holdings.get(holdingKey(accountId, symbol));
        if (holding == null) {
            throw new IllegalStateException("No shares are held for this stock");
        }
        return holding;
    }

    private static UserView userView(Account account) {
        return new UserView(account.id, account.name, account.pan, account.email, account.phone,
                account.kycStatus, account.blocked, account.availableBalance, account.blockedBalance);
    }

    private static StockView stockView(Stock stock) {
        return new StockView(stock.symbol, stock.companyName, stock.sector, stock.currentPrice,
                stock.previousClose, stock.status);
    }

    private static OrderView orderView(Order order) {
        return new OrderView(order.id, order.accountId, order.symbol, order.side, order.type,
                order.quantity, order.limitPrice, order.executionPrice, order.status, order.rejectionReason,
                order.createdAt, order.updatedAt);
    }

    private static PriceAlertView alertView(PriceAlert alert) {
        return new PriceAlertView(alert.id, alert.accountId, alert.symbol, alert.targetPrice,
                alert.triggered, alert.triggeredAt);
    }

    private static void requireAdmin(boolean admin) {
        if (!admin) {
            throw new SecurityException("Admin privileges are required");
        }
    }

    private void rejectOrder(Order order, String reason, LocalDateTime at) {
        order.status = OrderStatus.REJECTED;
        order.rejectionReason = reason;
        order.updatedAt = at;
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }

    private static String normalizeSymbol(String symbol) {
        requireText(symbol, "stock symbol");
        return symbol.trim().toUpperCase();
    }

    private static BigDecimal positiveMoney(BigDecimal value, String field) {
        BigDecimal normalized = money(value);
        if (normalized.signum() <= 0) {
            throw new IllegalArgumentException(field + " must be greater than zero");
        }
        return normalized;
    }

    private static BigDecimal money(BigDecimal value) {
        Objects.requireNonNull(value, "amount is required");
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private static String holdingKey(String accountId, String symbol) {
        return accountId + ":" + symbol;
    }

    private static String id() {
        return UUID.randomUUID().toString();
    }
}
