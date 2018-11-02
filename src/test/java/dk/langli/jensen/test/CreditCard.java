package dk.langli.jensen.test;

import java.time.YearMonth;

public class CreditCard {
    private String id;
    private String brand;
    private String creditCardNumber;
    private YearMonth expiration;
    private Integer cvc;
    private String cardHolderName;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public YearMonth getExpiration() {
        return expiration;
    }

    public void setExpiration(YearMonth expiration) {
        this.expiration = expiration;
    }

    public Integer getCvc() {
        return cvc;
    }

    public void setCvc(Integer cvc) {
        this.cvc = cvc;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
