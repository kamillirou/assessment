## Transaction Management ##

### Overview ###
This application is a REST API built with Spring Boot for managing purchase transactions. It allows users to save transactions, retrieve them, and convert their amounts to different currencies based on exchange rates provided by the Treasury Reporting Rates of Exchange API.

### Features ###
1. Save Transactions:

Save transactions with a description, date, and amount in USD.
Each transaction is assigned a unique identifier.
2. Retrieve Transactions with Exchange Rates:

Retrieve transactions with their original amount and a converted amount in a specified currency.
Currency conversion uses the latest exchange rate from the Treasury API within six months prior to the transaction date.
3. Database:

Uses an in-memory H2 database for simplicity.

4. API Documentation:

Endpoints are detailed with their expected payloads and responses and can also be found on http://localhost:8080/swagger-ui/index.html#/ (once the application is running)

### Getting Started ###

**Clone the Repository**

```$ git clone <repository-url>```

**Build the Application**

Compile the project and install dependencies:

````$ ./mvnw clean install````

Run the Application

Start the application:

```$ ./mvnw spring-boot:run```

The application will start at http://localhost:8080.

**Database Console**

Access the H2 database console at:

http://localhost:8080/h2-console

Use the following credentials:
```
JDBC URL: jdbc:h2:mem:wex
User: test
Password: test
```

### Endpoints

1. **Save Transaction**

**POST** /api/transactions

Request Body:

```
{
  "description": "Books purchase",
  "amount": 120.50,
  "date": "2024-12-08"
}
```

Response:

```
{
  "id": "xxxx-xxxxx-xxxxx-xxxx",
  "description": "Books purchase",
  "amount": 100.00,
  "date": "2024-12-10"
}
```

2. **Retrieve Transaction with Exchange Rate**

GET /api/transactions/{id}/{currency}

Response:

```
{
    "transaction_id": "xxxx-xxxxx-xxxxx-xxxx",
    "description": "Books Purchase",
    "transaction_date": "2024-12-08",
    "original_amount": 120.50,
    "currency": "Real",
    "exchange_rate": 6.041,
    "exchanged_amount": 727.94
}
```

3. **Retrieve all transactions**

GET /api/transactions

Response:

```
[
    {
        "id": "xxxx-xxxxx-xxxxx-xxxx",
        "description": "Purchase",
        "date": "2024-12-08",
        "amount": 150.50
    },
    {
        "id": "xxxx-xxxxx-xxxxx-xxxx",
        "description": "Purchase 2",
        "date": "2024-12-10",
        "amount": 120.50
    }
]
```

### Testing

Execute all tests:

```
$ ./mvnw test
```

**Integation Tests**

The integration tests verify:

Correct saving and retrieval of transactions.

Proper application of exchange rates to calculate converted amounts.

**Resetting Database Between Tests**

The H2 database is cleared between tests to ensure a clean state for each test case.

**Notes**

The application assumes valid inputs for simplicity (e.g., valid dates and amounts).

The Treasury API must be accessible for currency conversion to work.