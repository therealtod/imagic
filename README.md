# imagic

A simple API which retrieves PNG images for MTG cards via the Scryfall API

## Quick Start

### Prerequisites

- Docker 20.10+
- Docker Compose 2.20+

### Running with Docker

1. **Clone the repository**:
   ```bash
   git clone https://github.com/therealtod/imagic.git
   cd imagic

2. **Start services**:
   ```bash
   docker-compose up --build

### Access the API

- Launch an "Operation"

 ```bash
 curl --location 'http://localhost:8080/api/v1/cards/process' \
--header 'Content-Type: application/json' \
--data '{
    "cardNames": ["Austere Command", "Black Lotus"]
}'
```

Example response:

```
{"operationId": "cfbfb821-9abe-4424-a1b8-7390a5a90a70"}
```

- Track progress/results of an operation

```bash
curl --location --request GET 'http://localhost:8080/api/v1/cards/cfbfb821-9abe-4424-a1b8-7390a5a90a70' \
--header 'Content-Type: application/json'
```

Example response:

```
{
    "operationId": "12d5c520-b949-4993-ae0b-4ab5da4843f3",
    "status": "SUCCESS",
    "results": [
        {
            "cardName": "Austere Command",
            "pngUri": "https://cards.scryfall.io/png/front/a/3/a31ffc9e-d21b-4a8f-ac67-695e38e09e3b.png?1706240553"
        },
        {
            "cardName": "Black Lotus",
            "pngUri": "https://cards.scryfall.io/png/front/b/d/bd8fa327-dd41-4737-8f19-2cf5eb1f7cdd.png?1614638838"
        }
    ],
    "failures": []
}
```
