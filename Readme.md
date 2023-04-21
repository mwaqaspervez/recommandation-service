## Recommendation Service

To build the project make sure you are in the root directory of the project that is named as recommendation


### Installation

NOTE: Docker is required to run the below commands.

Build the application using docker by running the below command.

````docker-compose up --build -d````

Once the above process completes, Your api server will be running on port ``8080``

### Endpoints

#### Get the list of cryptocurrencies use:

``curl --location --request GET 'http://localhost:8080/api/cryptos' \
--header 'Content-Type: application/json' \
``

#### Get a single cryptocurrency use:

`` curl --location --request GET 'http://localhost:8080/api/cryptos/{name}' ``

Here name is the name of the crypto. e.g BTC

#### Get highest normalized cryptocurrency:


`` curl --location --request GET 'http://localhost:8080/api/pi/cryptos/normalized?date={date}' ``

Here date is the date for the day it is required. The format should be in yyyy-MM-dd