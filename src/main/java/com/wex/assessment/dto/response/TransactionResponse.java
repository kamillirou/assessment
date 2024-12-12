package com.wex.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class TransactionResponse {

    @JsonProperty("transaction_id") String transactionId;
    @JsonProperty("description") String description;
    @JsonProperty("transaction_date") String transactionDate;
    @JsonProperty("original_amount") BigDecimal originalAmount;
    @JsonProperty("currency")  String currency;
    @JsonProperty("exchange_rate") BigDecimal exchangeRate;
    @JsonProperty("exchanged_amount") BigDecimal exchangedAmount;

    public TransactionResponse() {}

    public TransactionResponse(String transactionId, String description, LocalDate transactionDate, BigDecimal originalAmount, String currency, BigDecimal exchangeRate, BigDecimal exchangedAmount) {
        this.transactionId = transactionId;
        this.description = description;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        this.transactionDate = transactionDate.format(formatter);
        this.originalAmount = originalAmount;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        this.exchangedAmount = exchangedAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getDescription() {
        return description;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public String getCurrency() {
        return currency;
    }


    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public BigDecimal getExchangedAmount() {
        return exchangedAmount;
    }
}
