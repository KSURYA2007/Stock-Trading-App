package stocktrading;

import cmp.rest.cli.Account;
import cmp.rest.cli.AccountStatus;
import cmp.rest.cli.AlertDirection;
import cmp.rest.cli.BankAccount;
import cmp.rest.cli.FundTransaction;
import cmp.rest.cli.FundTransactionType;
import cmp.rest.cli.Holding;
import cmp.rest.cli.KycStatus;
import cmp.rest.cli.MarketHoursPolicy;
import cmp.rest.cli.Order;
import cmp.rest.cli.OrderSide;
import cmp.rest.cli.OrderStatus;
import cmp.rest.cli.OrderType;
import cmp.rest.cli.PriceAlert;
import cmp.rest.cli.Role;
import cmp.rest.cli.Stock;
import cmp.rest.cli.StockStatus;
import cmp.rest.cli.Trade;
import cmp.rest.cli.TradingAccount;
import cmp.rest.cli.TradingException;
import cmp.rest.cli.TradingPlatform;
import cmp.rest.cli.User;
import cmp.rest.model.Customer;
import cmp.rest.repository.InMemoryStockRepository;
import cmp.rest.repository.InMemoryUserRepository;
import cmp.rest.repository.StockRepository;
import cmp.rest.repository.UserRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

public class TradingPlatformTest {
    private static int passed = 0;
    private static int failed = 0;

    private static class AlwaysOpenMarketHours implements MarketHoursPolicy {
        @Override
        public boolean isOpen(LocalDateTime time) {
            return true;
        }
    }

    private static class AlwaysClosedMarketHours implements MarketHoursPolicy {
        @Override
        public boolean isOpen(LocalDateTime time) {
            return false;
        }
    }

    private static TradingPlatform createTestPlatform() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-30T05:00:00Z"), ZoneId.of("Asia/Kolkata"));
        return new TradingPlatform(new AlwaysOpenMarketHours(), fixedClock);
    }

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   RUNNING STOCK TRADING APP TEST SUITE");
        System.out.println("=================================================");

        testCustomerRegistrationAndAuth();
        testAdminLogin();
        testKycApproval();
        testAccountBlocking();
        testDepositAndWithdrawal();
        testMarketHoursGate();
        testStockManagement();
        testMarketBuyAndSellOrders();
        testShortSellingPrevention();
        testInsufficientFundsPrevention();
        testLimitOrderAndPriceTrigger();
        testOrderCancellation();
        testWatchlistManagement();
        testPriceAlerts();
        testPolymorphism();
        testRepositoryAndModelIntegration();

        System.out.println("=================================================");
        System.out.printf("   TEST SUMMARY: Total: %d, Passed: %d, Failed: %d%n", passed + failed, passed, failed);
        System.out.println("=================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println(" [PASS] " + testName);
            passed++;
        } else {
            System.err.println(" [FAIL] " + testName);
            failed++;
        }
    }

    private static void testCustomerRegistrationAndAuth() {
        TradingPlatform platform = createTestPlatform();
        try {
            User user = platform.register("Surya K", "surya@example.com", "9876543210",
                    "secret123".toCharArray(), "HDFC Bank", "1234567890", "HDFC0001");
            check("User registered with ID > 0", user.getUserId() > 0);
            check("User role is CUSTOMER", user.getRole() == Role.CUSTOMER);

            TradingAccount account = platform.getTradingAccount(user.getUserId());
            check("Trading account initialized with 0 balance",
                    account.getAvailableBalance().compareTo(BigDecimal.ZERO) == 0);
            check("Initial KYC is PENDING", account.getKycStatus() == KycStatus.PENDING);

            User authUser = platform.authenticate("surya@example.com", "secret123".toCharArray(), Role.CUSTOMER);
            check("Authentication succeeds with valid credentials", authUser != null);

            boolean duplicateRejected = false;
            try {
                platform.register("Other", "surya@example.com", "9876543210",
                        "password".toCharArray(), "Bank", "111", "IFSC");
            } catch (TradingException e) {
                duplicateRejected = true;
            }
            check("Duplicate email rejected", duplicateRejected);

            boolean invalidEmailRejected = false;
            try {
                platform.register("Invalid", "invalid-email", "9876543210",
                        "password".toCharArray(), "Bank", "111", "IFSC");
            } catch (TradingException e) {
                invalidEmailRejected = true;
            }
            check("Invalid email format rejected", invalidEmailRejected);

        } catch (Exception e) {
            check("Customer registration exception: " + e.getMessage(), false);
        }
    }

    private static void testAdminLogin() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            check("Admin authentication succeeds with default credentials", admin != null && admin.getRole() == Role.ADMIN);

            boolean invalidPasswordRejected = false;
            try {
                platform.authenticate("admin@stock.local", "wrongpassword".toCharArray(), Role.ADMIN);
            } catch (TradingException e) {
                invalidPasswordRejected = true;
            }
            check("Admin login rejects wrong password", invalidPasswordRejected);

            boolean wrongRoleRejected = false;
            try {
                platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.CUSTOMER);
            } catch (TradingException e) {
                wrongRoleRejected = true;
            }
            check("Admin cannot login under CUSTOMER role", wrongRoleRejected);
        } catch (Exception e) {
            check("Admin login exception: " + e.getMessage(), false);
        }
    }

    private static void testKycApproval() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Alice", "alice@example.com", "9876543211",
                    "password123".toCharArray(), "SBI", "2222222", "SBIN0001");
            TradingAccount account = platform.getTradingAccount(user.getUserId());
            check("KYC starts as PENDING", account.getKycStatus() == KycStatus.PENDING);

            platform.approveKyc(account.getId(), admin.getUserId());
            check("KYC becomes APPROVED after admin approval", account.getKycStatus() == KycStatus.APPROVED);
        } catch (Exception e) {
            check("KYC approval exception: " + e.getMessage(), false);
        }
    }

    private static void testAccountBlocking() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Bob", "bob@example.com", "9876543212",
                    "password123".toCharArray(), "ICICI", "3333333", "ICIC0001");
            TradingAccount account = platform.getTradingAccount(user.getUserId());
            platform.approveKyc(account.getId(), admin.getUserId());

            platform.blockAccount(account.getId(), admin.getUserId(), true);
            check("Account status changed to BLOCKED", account.getAccountStatus() == AccountStatus.BLOCKED);

            boolean blockedActionRejected = false;
            try {
                platform.addFunds(user.getUserId(), new BigDecimal("1000.00"));
            } catch (TradingException e) {
                blockedActionRejected = true;
            }
            check("Blocked account cannot deposit funds", blockedActionRejected);

            platform.blockAccount(account.getId(), admin.getUserId(), false);
            check("Account status restored to ACTIVE", account.getAccountStatus() == AccountStatus.ACTIVE);
        } catch (Exception e) {
            check("Account blocking exception: " + e.getMessage(), false);
        }
    }

    private static void testDepositAndWithdrawal() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Charlie", "charlie@example.com", "9876543213",
                    "password123".toCharArray(), "Axis", "4444444", "UTIB0001");
            TradingAccount account = platform.getTradingAccount(user.getUserId());

            platform.addFunds(user.getUserId(), new BigDecimal("5000.00"));
            check("Deposit increases available balance",
                    account.getAvailableBalance().compareTo(new BigDecimal("5000.00")) == 0);

            List<FundTransaction> txns = platform.getTransactionsForUser(user.getUserId());
            check("Deposit recorded in fund transactions",
                    !txns.isEmpty() && txns.get(0).getType() == FundTransactionType.DEPOSIT);

            boolean withdrawWithoutKycRejected = false;
            try {
                platform.withdrawFunds(user.getUserId(), new BigDecimal("1000.00"));
            } catch (TradingException e) {
                withdrawWithoutKycRejected = true;
            }
            check("Withdrawal rejected before KYC approval", withdrawWithoutKycRejected);

            platform.approveKyc(account.getId(), admin.getUserId());
            platform.withdrawFunds(user.getUserId(), new BigDecimal("2000.00"));
            check("Withdrawal decreases balance after KYC approval",
                    account.getAvailableBalance().compareTo(new BigDecimal("3000.00")) == 0);

            boolean overdraftRejected = false;
            try {
                platform.withdrawFunds(user.getUserId(), new BigDecimal("10000.00"));
            } catch (TradingException e) {
                overdraftRejected = true;
            }
            check("Withdrawal exceeding balance is rejected", overdraftRejected);
        } catch (Exception e) {
            check("Deposit/Withdrawal exception: " + e.getMessage(), false);
        }
    }

    private static void testMarketHoursGate() {
        Clock fixedClock = Clock.fixed(Instant.parse("2026-10-30T10:00:00Z"), ZoneId.of("Asia/Kolkata"));
        TradingPlatform closedPlatform = new TradingPlatform(new AlwaysClosedMarketHours(), fixedClock);
        try {
            User admin = closedPlatform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = closedPlatform.register("David", "david@example.com", "9876543214",
                    "password123".toCharArray(), "HDFC", "5555555", "HDFC0001");
            closedPlatform.approveKyc(closedPlatform.getTradingAccount(user.getUserId()).getId(), admin.getUserId());
            closedPlatform.addFunds(user.getUserId(), new BigDecimal("50000.00"));

            boolean orderRejectedOutsideHours = false;
            try {
                closedPlatform.placeOrder(user.getUserId(), "RELIANCE", OrderSide.BUY,
                        OrderType.MARKET, 10, null);
            } catch (TradingException e) {
                orderRejectedOutsideHours = true;
            }
            check("Order rejected outside market hours", orderRejectedOutsideHours);
        } catch (Exception e) {
            check("Market hours gate exception: " + e.getMessage(), false);
        }
    }

    private static void testStockManagement() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            Stock newStock = platform.addStock(admin.getUserId(), "WIPRO", "Wipro Limited", "Technology",
                    new BigDecimal("450.00"));
            check("Admin can add new stock", newStock != null && newStock.getSymbol().equals("WIPRO"));

            platform.updateStockPrice(admin.getUserId(), "WIPRO", new BigDecimal("475.00"));
            Stock updatedStock = platform.getStock(newStock.getStockId());
            check("Stock price updated successfully",
                    updatedStock.getCurrentPrice().compareTo(new BigDecimal("475.00")) == 0);
            check("Price history recorded",
                    !platform.getPriceHistory(newStock.getStockId()).isEmpty());

            platform.setStockSuspended(admin.getUserId(), "WIPRO", true);
            check("Stock status changed to SUSPENDED",
                    platform.getStock(newStock.getStockId()).getStatus() == StockStatus.SUSPENDED);

            platform.setStockSuspended(admin.getUserId(), "WIPRO", false);
            check("Stock reactivated to ACTIVE",
                    platform.getStock(newStock.getStockId()).getStatus() == StockStatus.ACTIVE);
        } catch (Exception e) {
            check("Stock management exception: " + e.getMessage(), false);
        }
    }

    private static void testMarketBuyAndSellOrders() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Emma", "emma@example.com", "9876543215",
                    "password123".toCharArray(), "SBI", "6666666", "SBIN0001");
            platform.approveKyc(platform.getTradingAccount(user.getUserId()).getId(), admin.getUserId());
            platform.addFunds(user.getUserId(), new BigDecimal("50000.00"));

            Order buyOrder = platform.placeOrder(user.getUserId(), "INFY", OrderSide.BUY,
                    OrderType.MARKET, 10, null);
            check("Market buy order executes immediately", buyOrder.getStatus() == OrderStatus.EXECUTED);

            List<Holding> holdings = platform.getHoldings(user.getUserId());
            check("Holding created with quantity 10",
                    !holdings.isEmpty() && holdings.get(0).getQuantity() == 10);

            List<Trade> trades = platform.getTradesForUser(user.getUserId());
            check("Trade record created for execution", !trades.isEmpty());

            Order sellOrder = platform.placeOrder(user.getUserId(), "INFY", OrderSide.SELL,
                    OrderType.MARKET, 5, null);
            check("Market sell order executes immediately", sellOrder.getStatus() == OrderStatus.EXECUTED);

            holdings = platform.getHoldings(user.getUserId());
            check("Holding quantity updated to 5 after sell",
                    holdings.get(0).getQuantity() == 5);
        } catch (Exception e) {
            check("Market order execution exception: " + e.getMessage(), false);
        }
    }

    private static void testShortSellingPrevention() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Frank", "frank@example.com", "9876543216",
                    "password123".toCharArray(), "HDFC", "7777777", "HDFC0001");
            platform.approveKyc(platform.getTradingAccount(user.getUserId()).getId(), admin.getUserId());
            platform.addFunds(user.getUserId(), new BigDecimal("50000.00"));

            boolean shortSellRejected = false;
            try {
                platform.placeOrder(user.getUserId(), "TCS", OrderSide.SELL,
                        OrderType.MARKET, 5, null);
            } catch (TradingException e) {
                shortSellRejected = true;
            }
            check("Short selling without holdings rejected", shortSellRejected);
        } catch (Exception e) {
            check("Short selling test exception: " + e.getMessage(), false);
        }
    }

    private static void testInsufficientFundsPrevention() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Grace", "grace@example.com", "9876543217",
                    "password123".toCharArray(), "ICICI", "8888888", "ICIC0001");
            platform.approveKyc(platform.getTradingAccount(user.getUserId()).getId(), admin.getUserId());
            platform.addFunds(user.getUserId(), new BigDecimal("100.00"));

            boolean buyWithoutFundsRejected = false;
            try {
                platform.placeOrder(user.getUserId(), "TCS", OrderSide.BUY,
                        OrderType.MARKET, 10, null);
            } catch (TradingException e) {
                buyWithoutFundsRejected = true;
            }
            check("Buy order exceeding funds rejected", buyWithoutFundsRejected);
        } catch (Exception e) {
            check("Insufficient funds test exception: " + e.getMessage(), false);
        }
    }

    private static void testLimitOrderAndPriceTrigger() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Henry", "henry@example.com", "9876543218",
                    "password123".toCharArray(), "Axis", "9999999", "UTIB0001");
            platform.approveKyc(platform.getTradingAccount(user.getUserId()).getId(), admin.getUserId());
            platform.addFunds(user.getUserId(), new BigDecimal("50000.00"));

            // TCS is seeded at 3800.00. Place limit buy at 3500.00
            Order limitOrder = platform.placeOrder(user.getUserId(), "TCS", OrderSide.BUY,
                    OrderType.LIMIT, 2, new BigDecimal("3500.00"));
            check("Limit buy order below market remains PENDING",
                    limitOrder.getStatus() == OrderStatus.PENDING);

            // Update TCS price to 3450.00
            platform.updateStockPrice(admin.getUserId(), "TCS", new BigDecimal("3450.00"));

            List<Order> orders = platform.getOrdersForUser(user.getUserId());
            check("Limit order executes when market price falls to or below limit",
                    orders.stream().anyMatch(o -> o.getOrderId() == limitOrder.getOrderId()
                            && o.getStatus() == OrderStatus.EXECUTED));
        } catch (Exception e) {
            check("Limit order test exception: " + e.getMessage(), false);
        }
    }

    private static void testOrderCancellation() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Ivy", "ivy@example.com", "9876543219",
                    "password123".toCharArray(), "SBI", "1010101", "SBIN0001");
            platform.approveKyc(platform.getTradingAccount(user.getUserId()).getId(), admin.getUserId());
            platform.addFunds(user.getUserId(), new BigDecimal("50000.00"));

            Order limitOrder = platform.placeOrder(user.getUserId(), "INFY", OrderSide.BUY,
                    OrderType.LIMIT, 5, new BigDecimal("1000.00"));
            check("Limit order is pending", limitOrder.getStatus() == OrderStatus.PENDING);

            platform.cancelOrder(user.getUserId(), limitOrder.getOrderId());
            List<Order> orders = platform.getOrdersForUser(user.getUserId());
            check("Cancelled order status is CANCELLED",
                    orders.stream().anyMatch(o -> o.getOrderId() == limitOrder.getOrderId()
                            && o.getStatus() == OrderStatus.CANCELLED));
        } catch (Exception e) {
            check("Order cancellation test exception: " + e.getMessage(), false);
        }
    }

    private static void testWatchlistManagement() {
        TradingPlatform platform = createTestPlatform();
        try {
            User user = platform.register("Jack", "jack@example.com", "9876543220",
                    "password123".toCharArray(), "HDFC", "2020202", "HDFC0001");
            platform.addToWatchlist(user.getUserId(), "RELIANCE");
            platform.addToWatchlist(user.getUserId(), "TCS");

            List<Stock> watchlist = platform.getWatchlistStocks(user.getUserId());
            check("Watchlist contains 2 stocks", watchlist.size() == 2);

            platform.removeFromWatchlist(user.getUserId(), "RELIANCE");
            watchlist = platform.getWatchlistStocks(user.getUserId());
            check("Watchlist contains 1 stock after removal", watchlist.size() == 1
                    && watchlist.get(0).getSymbol().equals("TCS"));
        } catch (Exception e) {
            check("Watchlist test exception: " + e.getMessage(), false);
        }
    }

    private static void testPriceAlerts() {
        TradingPlatform platform = createTestPlatform();
        try {
            User admin = platform.authenticate("admin@stock.local", "admin123".toCharArray(), Role.ADMIN);
            User user = platform.register("Karen", "karen@example.com", "9876543221",
                    "password123".toCharArray(), "Axis", "3030303", "UTIB0001");

            // RELIANCE is 1450. Set alert >= 1500.
            PriceAlert alert = platform.setPriceAlert(user.getUserId(), "RELIANCE",
                    new BigDecimal("1500.00"), AlertDirection.ABOVE);
            check("Price alert created and not yet triggered", !alert.isTriggered());

            platform.updateStockPrice(admin.getUserId(), "RELIANCE", new BigDecimal("1550.00"));
            List<PriceAlert> alerts = platform.getAlertsForUser(user.getUserId());
            check("Price alert triggers after price crosses target",
                    alerts.stream().anyMatch(a -> a.getAlertId() == alert.getAlertId() && a.isTriggered()));
        } catch (Exception e) {
            check("Price alert test exception: " + e.getMessage(), false);
        }
    }

    private static void testPolymorphism() {
        Account bank = new BankAccount(1, 10, "12345", "IFSC001", "Bank A");
        Account trading = new TradingAccount(2, 10, LocalDateTime.now());
        check("Account polymorphism: BankAccount returns Bank", "Bank".equals(bank.getAccountType()));
        check("Account polymorphism: TradingAccount returns Trading", "Trading".equals(trading.getAccountType()));
    }

    private static void testRepositoryAndModelIntegration() {
        UserRepository<Customer> userRepo = new InMemoryUserRepository<>(
                Customer::getUserId,
                Customer::getEmail,
                Customer::getPanNumber
        );

        Customer c1 = new Customer(1, "Customer One", "ABCDE1234F", "c1@test.com", "9999999991");
        userRepo.save(c1);

        Optional<Customer> foundByEmail = userRepo.findByEmail("c1@test.com");
        check("Repository findByEmail works", foundByEmail.isPresent() && foundByEmail.get().getUserId() == 1);

        Optional<Customer> foundByPan = userRepo.findByPanNumber("ABCDE1234F");
        check("Repository findByPanNumber works", foundByPan.isPresent());

        StockRepository<cmp.rest.model.Stock> stockRepo = new InMemoryStockRepository<>(
                cmp.rest.model.Stock::getStockId,
                cmp.rest.model.Stock::getSymbol,
                s -> s.getSector().name(),
                s -> s.getStatus().name()
        );

        cmp.rest.model.Stock s1 = new cmp.rest.model.Stock(1, "INFY", "Infosys",
                cmp.rest.model.enums.Sector.IT, new BigDecimal("1500.00"), 1);
        stockRepo.save(s1);

        Optional<cmp.rest.model.Stock> foundStock = stockRepo.findBySymbol("INFY");
        check("StockRepository findBySymbol works", foundStock.isPresent());
    }
}
