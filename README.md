<p align="center">
  <a href="https://codely.com">
    <picture>
      <source media="(prefers-color-scheme: dark)" srcset="https://codely.com/logo/codely_logo-dark.svg">
      <source media="(prefers-color-scheme: light)" srcset="https://codely.com/logo/codely_logo-light.svg">
      <img alt="Codely logo" src="https://codely.com/logo/codely_logo.svg">
    </picture>
  </a>
</p>

<h1 align="center">
  🏷 Event Generator for Kafka
</h1>

<p align="center">
    <a href="https://github.com/CodelyTV"><img src="https://img.shields.io/badge/CodelyTV-OS-green.svg?style=flat-square" alt="codely.tv"/></a>
    <a href="http://pro.codely.tv"><img src="https://img.shields.io/badge/CodelyTV-PRO-black.svg?style=flat-square" alt="CodelyTV Courses"/></a>
</p>

<p align="center">
    Generate and send random events to Kafka
</p>

## 🚀 Usage

1. **Create a JSON file**: This file will define the event specifications and generation rules.

2. **Run the Event Producer**: Execute the application by passing the path to the JSON configuration file as an argument.

```sh
sbt "run path/to/your/events.json"
```

## 🐳 Docker Usage

You can also run the application using Docker, which does not require `sbt` to be installed on your machine.

### Prerequisites

- Docker must be installed and running on your machine.
- Ensure Kafka and Zookeeper are running. You can use Docker to start them up:

```sh
docker compose up -d
```

### Running the Application with Docker

There are two ways to provide the JSON configuration file to the application:

1. **Using a sample JSON file included in the Docker image:**

```sh
docker run events-fakery:0.1.0-SNAPSHOT --eventsConfigPath /opt/docker/samples/events.json
```

2. **Mounting a directory with your custom JSON file:**

```sh
docker run -v $(pwd)/json:/json events-fakery:0.1.0-SNAPSHOT --eventsConfigPath /json/events.json
```

In this command, replace `$(pwd)/json` with the path to your local directory containing the `events.json` file.

## 🛠 Configuration

### JSON Specification

The JSON configuration file must follow this structure:

```json
{
  "eventTypes": [
    {
      "eventType": "PurchaseCompleted",
      "fields": {
        "eventType": {
          "type": "string",
          "valueType": "constant",
          "constant": "PurchaseCompleted"
        },
        "timestamp": {
          "type": "string",
          "valueType": "format",
          "format": "date-time"
        },
        "userId": {
          "type": "string",
          "valueType": "pattern",
          "pattern": "user[0-9]+"
        },
        "transactionId": {
          "type": "string",
          "valueType": "pattern",
          "pattern": "trans[0-9]+"
        },
        "products": {
          "type": "array",
          "items": {
            "productId": {
              "type": "string",
              "valueType": "pattern",
              "pattern": "prod[0-9]+"
            },
            "quantity": {
              "type": "integer",
              "valueType": "range",
              "minimum": 1,
              "maximum": 100
            },
            "description": {
              "type": "string",
              "valueType": "examples",
              "examples": ["Smart Light Switch", "Smart Thermostat"]
            },
            "category": {
              "type": "string",
              "valueType": "enum",
              "enum": ["Smart Home", "Electronics", "Furniture"]
            },
            "price": {
              "type": "integer",
              "valueType": "range",
              "minimum": 1000,
              "maximum": 100000
            }
          }
        },
        "eventId": {
          "type": "string",
          "valueType": "format",
          "format": "uuid"
        }
      }
    }
  ],
  "generationSettings": {
    "numberOfEvents": 100,
    "sleepTimeMs": 1000,
    "duplicatePercentage": 5,
    "missingFieldsPercentage": 10,
    "incorrectTypesPercentage": 2
  },
  "kafkaSettings": {
    "bootstrapServers": "localhost:9093",
    "topic": "events"
  }
}
```

### Field Specifications

- **type**: The data type of the field. Supported types are `string`, `integer`, `array`.
- **valueType**: Defines how the value should be generated. Supported value types are:
  - `constant`: A fixed value specified in the `constant` field.
  - `pattern`: A string pattern (e.g., `user[0-9]+`).
  - `enum`: A list of possible values specified in the `enum` field.
  - `range`: A numeric range defined by `minimum` and `maximum`.
  - `format`: Predefined formats like `uuid` or `date-time`.
  - `examples`: A list of example values to randomly pick from.

### Array Field

- **type**: Must be `array`.
- **items**: Defines the structure of items within the array.

### Generation Settings

- **numberOfEvents**: The number of events to generate.
- **sleepTimeMs**: (Optional) Time to wait between generating each event in milliseconds.
- **duplicatePercentage**: (Optional) Percentage of events that should be duplicates.
- **missingFieldsPercentage**: (Optional) Percentage of events that should have missing fields.
- **incorrectTypesPercentage**: (Optional) Percentage of events that should have incorrect types.

### Kafka Settings

- **bootstrapServers**: The Kafka bootstrap servers to connect to.
- **topic**: The Kafka topic to which events will be sent.

## 📦 How to Run

1. **Ensure Kafka and Zookeeper are running**: You can use Docker to start them up.

```sh
docker compose up -d
```

2. **Run the Event Producer**: Execute the `EventProducer` application by providing the path to your JSON configuration file.

```sh
sbt "run path/to/your/events.json"
```

## 🛠️ Troubleshooting

- **Connection Issues**: Ensure Kafka is accessible at the configured `bootstrapServers` and the topic exists.
- **Serialization Errors**: Check the JSON configuration and ensure all fields are correctly defined.

## Contributing

If you would like to help improve the project, please read the [contribution guidelines](#).

## ⚖️ License

[MIT](LICENSE)