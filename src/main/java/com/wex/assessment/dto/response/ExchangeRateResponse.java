package com.wex.assessment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExchangeRateResponse {

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("exchange_rate")
    private String exchangeRate;

    @JsonProperty("effective_date")
    private String effectiveDate;

    public ExchangeRateResponse(String currency, String exchangeRate, String effectiveDate) {
        this.currency = currency;
        this.exchangeRate = exchangeRate;
        this.effectiveDate = effectiveDate;
    }

    public ExchangeRateResponse() {}

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
