package cmp.rest.stocktrading;

public class BankAccount extends Account {
    private String accountNumber;
    private String ifscCode;
    private String bankName;

    public BankAccount(int bankId, int userId, String accountNumber, String ifscCode, String bankName) {
        super(bankId, userId);
        this.accountNumber = accountNumber;
        this.ifscCode = ifscCode;
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public String getAccountType() {
        return "Bank";
    }
}
