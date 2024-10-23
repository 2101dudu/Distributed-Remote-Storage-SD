# Distributed Remote Storage

## Project Overview
This project implements a distributed in-memory data storage system, allowing clients to store and retrieve data remotely via TCP sockets. The server handles multiple concurrent client requests, ensuring efficient data access with minimal contention. The system uses a custom binary communication protocol for high concurrency and performance.

## Features:
- **User Authentication and Registration**: Users can sign up and authenticate using a username and password.
- **Basic Operations**:
  - `put(String key, byte[] value)`: Stores or updates a piece of data on the server.
  - `get(String key)`: Retrieves the value associated with a given key.
- **Composite Operations**:
  - `multiPut(Map<String, byte[]> data)`: Atomically stores or updates multiple entries.
  - `multiGet(Set<String> keys)`: Retrieves multiple entries in one request.
- **Concurrency Limitation**: Limits the number of simultaneous users with configurable settings.
- **Multi-Threading Support**: Clients can make multiple requests concurrently without blocking other operations.

## Group Members
- [Eduardo Faria](https://www.github.com/2101dudu)
- [Hélder Gomes](https://www.github.com/helderrrg)
- [Nuno Siva](https://www.github.com/NunoMRS7)
- [Pedro Pereira](https://www.github.com/pedrofp4444)
