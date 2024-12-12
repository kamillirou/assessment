package com.wex.assessment.service;

import com.wex.assessment.client.TreasuryAPIClient;
import com.wex.assessment.dto.response.ExchangeRateResponse;
import com.wex.assessment.dto.response.TransactionResponse;
import com.wex.assessment.exception.ExchangeRateNotAvailableException;
import com.wex.assessment.model.Transaction;
import com.wex.assessment.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TreasuryAPIClient treasuryAPIClient;

    private final String transactionId = "1";
    private final String currency = "Real";

    private final Transaction transaction = new Transaction(transactionId, "Test Transaction", new BigDecimal("100.00"), LocalDate.now());
    private final ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse(currency, "0.85", "2024-10-01");

    @Test
    void shouldReturnTransactionResponseWithExchangedAmount() throws Exception, ExchangeRateNotAvailableException {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(treasuryAPIClient.getExchangeRates(currency, transaction.getDate())).thenReturn(exchangeRateResponse);

        TransactionResponse response = transactionService.getTransactionWithExchangeRate(transactionId, currency);

        assertNotNull(response);
        assertEquals(transactionId, response.getTransactionId());
        assertEquals(currency, response.getCurrency());
        assertEquals(new BigDecimal("85.00"), response.getExchangedAmount()); // 100.00 * 0.85
    }

    @Test
    void shouldThrowExceptionWhenTransactionNotFound() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.getTransactionWithExchangeRate(transactionId, currency)
        );

        assertEquals("Transaction with ID " + transactionId + " not found", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateNotAvailable() {
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(treasuryAPIClient.getExchangeRates(currency, transaction.getDate())).thenReturn(null);

        ExchangeRateNotAvailableException exception = assertThrows(ExchangeRateNotAvailableException.class, () ->
                transactionService.getTransactionWithExchangeRate(transactionId, currency)
        );

        assertEquals("Exchange rate for currency " + currency + " is not available", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenExchangeRateIsNull() {
        ExchangeRateResponse invalidExchangeRateResponse = new ExchangeRateResponse(currency, null, null);

        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
        when(treasuryAPIClient.getExchangeRates(currency, transaction.getDate())).thenReturn(null);

        ExchangeRateNotAvailableException exception = assertThrows(ExchangeRateNotAvailableException.class, () ->
                transactionService.getTransactionWithExchangeRate(transactionId, currency)
        );

        assertEquals("Exchange rate for currency " + currency + " is not available", exception.getMessage());
    }
}