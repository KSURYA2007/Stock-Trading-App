package cmp.rest.stocktrading;

import java.math.BigDecimal;

public class Stock {
    private final int stockId;
    private String symbol;
    private String companyName;
    private String sector;
    private BigDecimal currentPrice;
    private BigDecimal previousClose;
    private StockStatus status;
    private int listedBy;

    public Stock(int stockId, String symbol, String companyName, String sector,
                 BigDecimal currentPrice, int listedBy) {
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

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.previousClose = this.currentPrice;
        this.currentPrice = currentPrice;
    }

    public BigDecimal getPreviousClose() {
        return previousClose;
    }

    public void setPreviousClose(BigDecimal previousClose) {
        this.previousClose = previousClose;
    }

    public StockStatus getStatus() {
        return status;
    }

    public void setStatus(StockStatus status) {
        this.status = status;
    }

    public int getListedBy() {
        return listedBy;
    }

    public void setListedBy(int listedBy) {
        this.listedBy = listedBy;
    }
}
