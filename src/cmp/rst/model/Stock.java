package cmp.rst.model;

import java.math.BigDecimal;
import cmp.rst.model.enums.Sector;
import cmp.rst.model.enums.StockStatus;

public class Stock {

    private int stockId;
    private String symbol;
    private String companyName;
    private Sector sector;

    private BigDecimal currentPrice;
    private BigDecimal previousClose;

    private StockStatus status;
    private int listedBy;

    public Stock(int stockId, String symbol,
                 String companyName, Sector sector,
                 BigDecimal currentPrice, int listedBy) {

        if (currentPrice == null ||
            currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }

        this.stockId = stockId;
        this.symbol = symbol;
        this.companyName = companyName;
        this.sector = sector;
        this.currentPrice = currentPrice;
        this.previousClose = currentPrice;
        this.status = StockStatus.ACTIVE;
        this.listedBy = listedBy;
    }

    public int getStockId() {
        return stockId;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Sector getSector() {
        return sector;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    public StockStatus getStatus() {
        return status;
    }

    public int getListedBy() {
        return listedBy;
    }

    public void updatePrice(BigDecimal newPrice) {

        if (newPrice == null ||
            newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid stock price");
        }

        this.currentPrice = newPrice;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }
}