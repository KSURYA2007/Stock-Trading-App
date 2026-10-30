package stocktrading;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final TradingPlatform platform = new TradingPlatform();
    private User currentUser;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("======================================");
        System.out.println("       JAVA STOCK TRADING APP");
        System.out.println("======================================");
        while (true) {
            System.out.println("\n1. Customer registration\n2. Customer login\n3. Admin login\n0. Exit");
            String choice = prompt("Choose: ");
            try {
                switch (choice) {
                    case "1" -> register();
                    case "2" -> login(Role.CUSTOMER);
                    case "3" -> login(Role.ADMIN);
                    case "0" -> {
                        System.out.println("Goodbye.");
                        return;
                    }
                    default -> System.out.println("Choose one of the displayed options.");
                }
            } catch (TradingException exception) {
                System.out.println("Could not complete request: " + exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.out.println("Invalid input: " + exception.getMessage());
            }
        }
    }

    private void register() throws TradingException {
        System.out.println("\n--- Customer registration (KYC starts as PENDING) ---");
        String name = prompt("Full name: ");
        String email = prompt("Email: ");
        String phone = prompt("Phone: ");
        char[] password = prompt("Password (at least 6 characters): ").toCharArray();
        String bankName = prompt("Bank name: ");
        String bankAccount = prompt("Bank account number: ");
        String ifsc = prompt("IFSC code: ");
        User user = platform.register(name, email, phone, password, bankName, bankAccount, ifsc);
        System.out.println("Registration successful. User ID: " + user.getUserId()
                + ". An admin must approve KYC before trading.");
    }

    private void login(Role role) throws TradingException {
        String email = prompt("Email: ");
        char[] password = prompt("Password: ").toCharArray();
        currentUser = platform.authenticate(email, password, role);
        System.out.println("Welcome, " + currentUser.getName() + ".");
        if (role == Role.ADMIN) {
            adminMenu();
        } else {
            customerMenu();
        }
        currentUser = null;
    }

    private void customerMenu() {
        while (true) {
            System.out.println("""

                    --- CUSTOMER MENU ---
                    1. View profile and account
                    2. Add funds
                    3. Withdraw funds
                    4. View market stocks
                    5. Manage watchlist
                    6. Buy stocks (market/limit)
                    7. Sell stocks (market/limit)
                    8. Cancel pending order
                    9. View holdings
                    10. View portfolio
                    11. Set price alert
                    12. View price alerts
                    13. View daily statement
                    14. View orders
                    0. Logout""");
            try {
                switch (prompt("Choose: ")) {
                    case "1" -> viewProfile();
                    case "2" -> addFunds();
                    case "3" -> withdrawFunds();
                    case "4" -> printStocks(platform.getStocks());
                    case "5" -> watchlistMenu();
                    case "6" -> placeOrder(OrderSide.BUY);
                    case "7" -> placeOrder(OrderSide.SELL);
                    case "8" -> cancelOrder();
                    case "9" -> viewHoldings();
                    case "10" -> viewPortfolio();
                    case "11" -> setPriceAlert();
                    case "12" -> viewAlerts();
                    case "13" -> viewStatement();
                    case "14" -> viewOrders();
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Choose one of the displayed options.");
                }
            } catch (TradingException | IllegalArgumentException exception) {
                System.out.println("Could not complete request: " + exception.getMessage());
            }
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("""

                    --- ADMIN MENU ---
                    1. Approve customer KYC
                    2. Add stock
                    3. Update stock price
                    4. Suspend/reactivate stock
                    5. Block/unblock trading account
                    6. View users and accounts
                    7. View all orders
                    8. View all trades
                    9. View stocks
                    0. Logout""");
            try {
                switch (prompt("Choose: ")) {
                    case "1" -> approveKyc();
                    case "2" -> addStock();
                    case "3" -> updateStockPrice();
                    case "4" -> suspendStock();
                    case "5" -> blockAccount();
                    case "6" -> viewUsers();
                    case "7" -> printOrders(platform.getAllOrders());
                    case "8" -> printTrades(platform.getAllTrades());
                    case "9" -> printStocks(platform.getStocks());
                    case "0" -> {
                        return;
                    }
                    default -> System.out.println("Choose one of the displayed options.");
                }
            } catch (TradingException | IllegalArgumentException exception) {
                System.out.println("Could not complete request: " + exception.getMessage());
            }
        }
    }

    private void viewProfile() throws TradingException {
        TradingAccount account = platform.getTradingAccount(currentUser.getUserId());
        System.out.println("User ID: " + currentUser.getUserId() + " | Name: " + currentUser.getName()
                + " | Email: " + currentUser.getEmail() + " | Phone: " + currentUser.getPhone());
        System.out.println("Account ID: " + account.getId() + " | KYC: " + account.getKycStatus()
                + " | Status: " + account.getAccountStatus() + " | Available: "
                + TradingPlatform.money(account.getAvailableBalance()) + " | Blocked: "
                + TradingPlatform.money(account.getBlockedBalance()));
    }

    private void addFunds() throws TradingException {
        platform.addFunds(currentUser.getUserId(), readMoney("Deposit amount: "));
        System.out.println("Funds added. Available balance: "
                + TradingPlatform.money(platform.getTradingAccount(currentUser.getUserId()).getAvailableBalance()));
    }

    private void withdrawFunds() throws TradingException {
        platform.withdrawFunds(currentUser.getUserId(), readMoney("Withdrawal amount: "));
        System.out.println("Withdrawal completed. Available balance: "
                + TradingPlatform.money(platform.getTradingAccount(currentUser.getUserId()).getAvailableBalance()));
    }

    private void watchlistMenu() throws TradingException {
        while (true) {
            System.out.println("\n1. View watchlist\n2. Add stock\n3. Remove stock\n0. Back");
            switch (prompt("Choose: ")) {
                case "1" -> printStocks(platform.getWatchlistStocks(currentUser.getUserId()));
                case "2" -> {
                    platform.addToWatchlist(currentUser.getUserId(), prompt("Stock symbol: "));
                    System.out.println("Stock added.");
                }
                case "3" -> {
                    platform.removeFromWatchlist(currentUser.getUserId(), prompt("Stock symbol: "));
                    System.out.println("Stock removed.");
                }
                case "0" -> {
                    return;
                }
                default -> System.out.println("Choose one of the displayed options.");
            }
        }
    }

    private void placeOrder(OrderSide side) throws TradingException {
        String symbol = prompt("Stock symbol: ");
        OrderType type = readOrderType();
        int quantity = readInt("Quantity (1-" + TradingPlatform.MAX_ORDER_QUANTITY + "): ");
        BigDecimal limitPrice = type == OrderType.LIMIT ? readMoney("Limit price: ") : null;
        Order order = platform.placeOrder(currentUser.getUserId(), symbol, side, type, quantity, limitPrice);
        System.out.println("Order #" + order.getOrderId() + " status: " + order.getStatus()
                + (order.getExecutionPrice() == null ? "" : " at " + TradingPlatform.money(order.getExecutionPrice())));
    }

    private OrderType readOrderType() {
        while (true) {
            String input = prompt("Order type (1=Market, 2=Limit): ");
            if (input.equals("1")) {
                return OrderType.MARKET;
            }
            if (input.equals("2")) {
                return OrderType.LIMIT;
            }
            System.out.println("Enter 1 for Market or 2 for Limit.");
        }
    }

    private void cancelOrder() throws TradingException {
        printOrders(platform.getOrdersForUser(currentUser.getUserId()));
        platform.cancelOrder(currentUser.getUserId(), readInt("Pending order ID to cancel: "));
        System.out.println("Order cancelled.");
    }

    private void viewHoldings() throws TradingException {
        List<Holding> userHoldings = platform.getHoldings(currentUser.getUserId());
        if (userHoldings.isEmpty()) {
            System.out.println("No holdings.");
            return;
        }
        for (Holding holding : userHoldings) {
            Stock stock = stockById(holding.getStockId());
            System.out.println(stock.getSymbol() + " | Quantity: " + holding.getQuantity()
                    + " | Average buy: " + TradingPlatform.money(holding.getAverageBuyPrice())
                    + " | Last price: " + TradingPlatform.money(stock.getCurrentPrice()));
        }
    }

    private void viewPortfolio() throws TradingException {
        System.out.println("Market value: "
                + TradingPlatform.money(platform.getPortfolioValue(currentUser.getUserId())));
        System.out.println("Unrealized P/L (before exit charges): "
                + TradingPlatform.money(platform.getUnrealizedProfitLoss(currentUser.getUserId())));
        viewHoldings();
    }

    private void setPriceAlert() throws TradingException {
        String symbol = prompt("Stock symbol: ");
        BigDecimal target = readMoney("Target price: ");
        AlertDirection direction;
        while (true) {
            String choice = prompt("Trigger (1=at/above, 2=at/below): ");
            if (choice.equals("1")) {
                direction = AlertDirection.ABOVE;
                break;
            }
            if (choice.equals("2")) {
                direction = AlertDirection.BELOW;
                break;
            }
            System.out.println("Enter 1 or 2.");
        }
        PriceAlert alert = platform.setPriceAlert(currentUser.getUserId(), symbol, target, direction);
        System.out.println("Alert #" + alert.getAlertId()
                + (alert.isTriggered() ? " triggered immediately." : " created."));
    }

    private void viewAlerts() throws TradingException {
        List<PriceAlert> userAlerts = platform.getAlertsForUser(currentUser.getUserId());
        if (userAlerts.isEmpty()) {
            System.out.println("No price alerts.");
            return;
        }
        for (PriceAlert alert : userAlerts) {
            Stock stock = stockById(alert.getStockId());
            System.out.println("#" + alert.getAlertId() + " " + stock.getSymbol() + " "
                    + alert.getDirection() + " " + TradingPlatform.money(alert.getTargetPrice())
                    + " | " + (alert.isTriggered() ? "TRIGGERED at " + alert.getTriggeredAt() : "ACTIVE"));
        }
    }

    private void viewStatement() throws TradingException {
        List<String> statement = platform.getDailyStatement(currentUser.getUserId());
        if (statement.isEmpty()) {
            System.out.println("No activity today.");
            return;
        }
        statement.forEach(System.out::println);
    }

    private void viewOrders() throws TradingException {
        printOrders(platform.getOrdersForUser(currentUser.getUserId()));
    }

    private void approveKyc() throws TradingException {
        for (TradingAccount account : platform.getAccounts()) {
            if (account.getKycStatus() == KycStatus.PENDING) {
                User user = platform.getUser(account.getUserId());
                System.out.println("Account #" + account.getId() + " | " + user.getName()
                        + " | " + user.getEmail());
            }
        }
        platform.approveKyc(readInt("Account ID to approve: "), currentUser.getUserId());
        System.out.println("KYC approved.");
    }

    private void addStock() throws TradingException {
        Stock stock = platform.addStock(currentUser.getUserId(), prompt("Symbol: "),
                prompt("Company name: "), prompt("Sector: "), readMoney("Initial price: "));
        System.out.println("Added " + stock.getSymbol() + ".");
    }

    private void updateStockPrice() throws TradingException {
        String symbol = prompt("Symbol: ");
        BigDecimal price = readMoney("New price: ");
        platform.updateStockPrice(currentUser.getUserId(), symbol, price);
        System.out.println("Price updated. Matching pending orders and alerts were processed.");
    }

    private void suspendStock() throws TradingException {
        String symbol = prompt("Symbol: ");
        String choice = prompt("1=Suspend, 2=Reactivate: ");
        if (!choice.equals("1") && !choice.equals("2")) {
            throw new IllegalArgumentException("Enter 1 or 2.");
        }
        boolean suspend = choice.equals("1");
        platform.setStockSuspended(currentUser.getUserId(), symbol, suspend);
        System.out.println("Stock " + (suspend ? "suspended." : "reactivated."));
    }

    private void blockAccount() throws TradingException {
        for (TradingAccount account : platform.getAccounts()) {
            System.out.println("Account #" + account.getId() + " | User: "
                    + platform.getUser(account.getUserId()).getName()
                    + " | " + account.getAccountStatus());
        }
        int accountId = readInt("Account ID: ");
        String choice = prompt("1=Block, 2=Unblock: ");
        if (!choice.equals("1") && !choice.equals("2")) {
            throw new IllegalArgumentException("Enter 1 or 2.");
        }
        platform.blockAccount(accountId, currentUser.getUserId(), choice.equals("1"));
        System.out.println("Account status updated.");
    }

    private void viewUsers() throws TradingException {
        for (User user : platform.getUsers()) {
            System.out.println("User #" + user.getUserId() + " | " + user.getName() + " | "
                    + user.getEmail() + " | " + user.getRole());
            if (user.getRole() == Role.CUSTOMER) {
                TradingAccount account = platform.getTradingAccount(user.getUserId());
                System.out.println("  Trading account #" + account.getId() + " | "
                        + account.getKycStatus() + " | " + account.getAccountStatus()
                        + " | Balance " + TradingPlatform.money(account.getAvailableBalance()));
            }
        }
    }

    private void printStocks(List<Stock> stockList) {
        if (stockList.isEmpty()) {
            System.out.println("No stocks to display.");
            return;
        }
        for (Stock stock : stockList) {
            System.out.println(stock.getSymbol() + " | " + stock.getCompanyName() + " | "
                    + stock.getSector() + " | " + TradingPlatform.money(stock.getCurrentPrice())
                    + " | Previous close: " + TradingPlatform.money(stock.getPreviousClose())
                    + " | " + stock.getStatus());
        }
    }

    private void printOrders(List<Order> orderList) {
        if (orderList.isEmpty()) {
            System.out.println("No orders.");
            return;
        }
        for (Order order : orderList) {
            Stock stock = stockById(order.getStockId());
            System.out.println("#" + order.getOrderId() + " " + order.getSide() + " "
                    + order.getOrderType() + " " + stock.getSymbol() + " x" + order.getQuantity()
                    + (order.getLimitPrice() == null ? "" : " limit " + TradingPlatform.money(order.getLimitPrice()))
                    + " | " + order.getStatus() + " | " + order.getCreatedAt());
        }
    }

    private void printTrades(List<Trade> tradeList) {
        if (tradeList.isEmpty()) {
            System.out.println("No trades.");
            return;
        }
        for (Trade trade : tradeList) {
            Order order = platform.getAllOrders().stream()
                    .filter(candidate -> candidate.getOrderId() == trade.getOrderId()).findFirst().orElseThrow();
            System.out.println("Trade #" + trade.getTradeId() + " | Order #" + trade.getOrderId()
                    + " | " + order.getSide() + " " + stockById(order.getStockId()).getSymbol()
                    + " x" + trade.getQuantity() + " @ "
                    + TradingPlatform.money(trade.getExecutionPrice()) + " | Value "
                    + TradingPlatform.money(trade.getTradeValue()));
        }
    }

    private Stock stockById(int stockId) {
        try {
            return platform.getStock(stockId);
        } catch (TradingException exception) {
            throw new IllegalStateException("Data refers to an unknown stock ID.", exception);
        }
    }

    private BigDecimal readMoney(String label) {
        try {
            return new BigDecimal(prompt(label));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid decimal amount.");
        }
    }

    private int readInt(String label) {
        try {
            return Integer.parseInt(prompt(label));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a whole number.");
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }
}
