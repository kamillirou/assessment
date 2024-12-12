package com.wex.assessment.client;

import com.wex.assessment.dto.response.ExchangeRateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Component
public class TreasuryAPIClient {

    private final RestTemplate restTemplate;

    @Value("${treasure.api.url}")
    private String apiUrl;

    @Autowired
    public TreasuryAPIClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String FILTER_CURRENCY = "currency";
    private static final String FILTER_RECORD_DATE = "record_date";
    private static final String FILTER_EFFECTIVE_DATE = "effective_date";

    public ExchangeRateResponse getExchangeRates(String currency, LocalDate transactionDate) {
        String url = getUrl(currency, transactionDate);

        ExchangeRateWrapper exchangeRates = restTemplate.getForObject(url, ExchangeRateWrapper.class);

        if(exchangeRates != null && exchangeRates.getData() != null && !exchangeRates.getData().isEmpty()) {
            return exchangeRates.getData().get(0);
        }

        return null;
    }

    private String getUrl(String currency, LocalDate transactionDate) {
        LocalDate dateThreshold = transactionDate.minusMonths(6);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = dateThreshold.format(formatter);

        return apiUrl +
                "?filter=" +
                FILTER_CURRENCY + ":eq:" + currency +
                "," + FILTER_RECORD_DATE + ":gte:" + formattedDate +
                "&sort=-" + FILTER_EFFECTIVE_DATE;
    }

    public static class ExchangeRateWrapper {
        private List<ExchangeRateResponse> data;

        public List<ExchangeRateResponse> getData() {
            return data;
        }

        public void setData(List<ExchangeRateResponse> data) {
            this.data = data;
        }
    }
}
