package com.wex.assessment.controller;

import com.wex.assessment.dto.response.TransactionResponse;
import com.wex.assessment.exception.ExchangeRateNotAvailableException;
import com.wex.assessment.model.Transaction;
import com.wex.assessment.repository.TransactionRepository;
import com.wex.assessment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @PostMapping
    public Transaction save(@RequestBody Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    @GetMapping()
    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    @GetMapping("/{id}/{currency}")
    public TransactionResponse getTransactionByIdExchanged(@PathVariable String id, @PathVariable String currency) {
        try {
            return transactionService.getTransactionWithExchangeRate(id, currency);
        } catch (ExchangeRateNotAvailableException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
