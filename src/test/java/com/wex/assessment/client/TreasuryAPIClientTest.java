package com.wex.assessment.client;

import com.wex.assessment.dto.response.ExchangeRateResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class TreasuryAPIClientTest {

    @Autowired
    @InjectMocks
    private TreasuryAPIClient treasuryAPIClient;

    @Mock
    private RestTemplate restTemplate;

    @Value("https://api.fiscaldata.treasury.gov/services/api/fiscal_service/v1/accounting/od/rates_of_exchange")
    private String apiUrl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRatesWithData() {
        String currency = "Real";
        LocalDate transactionDate = LocalDate.of(2024, 12, 10);

        TreasuryAPIClient.ExchangeRateWrapper mockResponse = new TreasuryAPIClient.ExchangeRateWrapper();
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();

        exchangeRateResponse.setCurrency("Real");
        exchangeRateResponse.setExchangeRate("1.2");

        mockResponse.setData(List.of(exchangeRateResponse));

        String expectedUrl = apiUrl +
                "?filter=currency:eq:Real,record_date:gte:" + transactionDate.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "&sort=-effective_date";

        when(restTemplate.getForObject(eq(expectedUrl), eq(TreasuryAPIClient.ExchangeRateWrapper.class)))
                .thenReturn(mockResponse);

        ExchangeRateResponse result = treasuryAPIClient.getExchangeRates(currency, transactionDate);

        assertNotNull(result);
        assertEquals("Real", result.getCurrency());
        assertEquals("1.2", result.getExchangeRate());

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(TreasuryAPIClient.ExchangeRateWrapper.class));
    }

    @Test
    void testGetExchangeRatesWithEmptyData() {
        String currency = "Real";
        LocalDate transactionDate = LocalDate.of(2024, 12, 10);

        String expectedUrl = apiUrl +
                "?filter=currency:eq:Real,record_date:gte:" + transactionDate.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "&sort=-effective_date";

        TreasuryAPIClient.ExchangeRateWrapper mockResponse = new TreasuryAPIClient.ExchangeRateWrapper();
        mockResponse.setData(Collections.emptyList());

        when(restTemplate.getForObject(eq(expectedUrl), eq(TreasuryAPIClient.ExchangeRateWrapper.class)))
                .thenReturn(mockResponse);

        ExchangeRateResponse result = treasuryAPIClient.getExchangeRates(currency, transactionDate);

        assertNull(result);
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(TreasuryAPIClient.ExchangeRateWrapper.class));
    }

    @Test
    void testGetExchangeRatesWithNullData() {
        String currency = "Real";
        LocalDate transactionDate = LocalDate.of(2024, 12, 10);

        String expectedUrl = apiUrl +
                "?filter=currency:eq:Real,record_date:gte:" + transactionDate.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) +
                "&sort=-effective_date";

        when(restTemplate.getForObject(eq(expectedUrl), eq(TreasuryAPIClient.ExchangeRateWrapper.class)))
                .thenReturn(null);

        ExchangeRateResponse result = treasuryAPIClient.getExchangeRates(currency, transactionDate);

        assertNull(result);
        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(TreasuryAPIClient.ExchangeRateWrapper.class));
    }
}