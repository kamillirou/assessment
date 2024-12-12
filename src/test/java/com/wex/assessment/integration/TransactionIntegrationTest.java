package com.wex.assessment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wex.assessment.dto.response.TransactionResponse;
import com.wex.assessment.model.Transaction;
import com.wex.assessment.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionIntegrationTest {

    public static final String TEST_TRANSACTION_DESCRIPTION = "Test Transaction Description";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository repository;

    @Test
    @Transactional
    void shouldSaveAndRetrieveTransaction() throws Exception {
        String transactionJson = """
            {
              "description": "Test Transaction Description",
              "amount": 120.50,
              "date": "2024-12-08"
            }
        """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(TEST_TRANSACTION_DESCRIPTION));

        Iterable<Transaction> transactions = repository.findAll();
        assert transactions.iterator().hasNext();

        Transaction savedTransaction = transactions.iterator().next();
        assert savedTransaction.getDescription().equals(TEST_TRANSACTION_DESCRIPTION);

        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value(TEST_TRANSACTION_DESCRIPTION));
    }

    @Test
    @Transactional
    void shouldSaveAndRetrieveTransactionWithExchangedValue() throws Exception {
        String transactionJson = """
            {
              "description": "Test Transaction Description",
              "amount": 120.50,
              "date": "2024-12-08"
            }
        """;

        mockMvc.perform(post("/api/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transactionJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value(TEST_TRANSACTION_DESCRIPTION));

        Iterable<Transaction> transactions = repository.findAll();
        assert transactions.iterator().hasNext();

        Transaction savedTransaction = transactions.iterator().next();
        assert savedTransaction.getDescription().equals(TEST_TRANSACTION_DESCRIPTION);

        MvcResult result = mockMvc.perform(get("/api/transactions/" + savedTransaction.getId() + "/" + "Real"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("Real"))
                .andExpect(jsonPath("$.exchange_rate").isNotEmpty())
                .andExpect(jsonPath("$.exchanged_amount").isNotEmpty())
                .andReturn();

        TransactionResponse response = new ObjectMapper().readValue(result.getResponse().getContentAsString(), TransactionResponse.class);

        BigDecimal initialAmount = savedTransaction.getAmount();
        BigDecimal exchangeRate = response.getExchangeRate();
        BigDecimal expectedExchangedAmount = initialAmount.multiply(exchangeRate).setScale(2, RoundingMode.HALF_UP);

        assert response.getExchangedAmount().equals(expectedExchangedAmount);
    }
}