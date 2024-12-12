package com.wex.assessment.service;

import com.wex.assessment.client.TreasuryAPIClient;
import com.wex.assessment.dto.response.ExchangeRateResponse;
import com.wex.assessment.dto.response.TransactionResponse;
import com.wex.assessment.exception.ExchangeRateNotAvailableException;
import com.wex.assessment.model.Transaction;
import com.wex.assessment.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class TransactionService {

    @Autowired
    private final TransactionRepository transactionRepository;

    @Autowired
    private TreasuryAPIClient treasuryAPIClient;

    TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionResponse getTransactionWithExchangeRate(String id, String currency) throws ExchangeRateNotAvailableException {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaction with ID " + id + " not found"));

        ExchangeRateResponse exchangeRateResponse = treasuryAPIClient.getExchangeRates(currency, transaction.getDate());
        if (exchangeRateResponse == null || exchangeRateResponse.getExchangeRate() == null) {
            throw new ExchangeRateNotAvailableException("Exchange rate for currency " + currency + " is not available");
        }

        BigDecimal exchangeRate = new BigDecimal(exchangeRateResponse.getExchangeRate());
        BigDecimal exchangedAmount = transaction.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        return new TransactionResponse(transaction.getId(), transaction.getDescription(), transaction.getDate(), transaction.getAmount(), currency, exchangeRate, exchangedAmount);
    }
}
