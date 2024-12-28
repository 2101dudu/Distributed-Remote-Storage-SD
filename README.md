# Distributed Remote Storage

## Project Overview
This project implements a **distributed in-memory data storage system** that provides remote access through a client-server architecture. The system enables multiple clients to interact with the server concurrently, supporting efficient data manipulation while ensuring performance and scalability. Written in Java, the system employs TCP sockets and multi-threading techniques to handle simultaneous requests effectively.

## Features
### Basic Functionality
- **User Authentication and Registration**
  - Users can register and authenticate using a username and password.
  - Authentication is required before accessing any service.
- **Basic Operations**
  - `put(String key, byte[] value)`: Stores or updates a key-value pair on the server.
  - `get(String key)`: Retrieves the value associated with a given key, or `null` if the key does not exist.
- **Composite Operations**
  - `multiPut(Map<String, byte[]> data)`: Atomically stores or updates multiple key-value pairs.
  - `multiGet(Set<String> keys)`: Retrieves multiple key-value pairs in a single request.
- **Concurrency Limitation**
  - Limits the number of concurrent client sessions based on a configurable parameter (S).
  - Additional clients attempting to connect are queued until slots become available.

### Advanced Features
- **Multi-threaded Client Support**
  - The client library supports concurrent requests to the server.
  - Requests blocked on the server do not hinder other operations submitted concurrently.
- **Conditional Read Operation**
  - `getWhen(String key, String keyCond, byte[] valueCond)`: Retrieves the value of `key` only when the value of `keyCond` matches `valueCond`. The operation blocks until the condition is met.

## Architecture
- The server follows a **thread-per-connection** model, spawning a new thread for each client connection.
- Data is stored in memory, utilizing a key-value structure.
- Communication between clients and the server employs a **custom binary protocol**, ensuring efficient serialization and deserialization.
- The system optimizes resource usage through granular locking mechanisms, minimizing contention and enhancing scalability.

## Benchmarking and Performance
To evaluate the system’s performance, various workloads inspired by the Yahoo! Cloud Serving Benchmark (YCSB) were implemented, including:
- Write-intensive scenarios (e.g., `WorkloadPut` and `WorkloadMultiPut`).
- Read-intensive scenarios (e.g., `WorkloadGet` and `WorkloadMultiGet`).
- Mixed read-write workloads (e.g., `WorkloadMixedPutGet`).

## Results Highlights
- **Scalability**: The system scales effectively with an increasing number of threads, demonstrating high throughput in write-heavy and read-heavy scenarios.
- **Latency**: Read and write operations exhibit low average latency, with conditional operations showing slightly higher latency due to their blocking nature.

## Repository Structure
```
.
├── LICENSE
├── README.md
├── docs
│   └── SD TP 2425.pdf
├── out
│   ├── database.bin
│   └── log.txt
└── src
    ├── client
    │   ├── Client.java
    │   ├── ClientHandler.java
    │   └── ClientMain.java
    ├── connection
    │   └── ConnectionManager.java
    ├── entries
    │   ├── AckPacket.java
    │   ├── AuthPacket.java
    │   ├── GetPacket.java
    │   ├── GetWhenPacket.java
    │   ├── MultiGetPacket.java
    │   ├── MultiPutPacket.java
    │   ├── PacketWrapper.java
    │   └── PutPacket.java
    ├── exceptions
    │   └── ShutdownException.java
    ├── server
    │   ├── Server.java
    │   ├── ServerHandler.java
    │   └── ServerMain.java
    ├── tests
    │   ├── BenchmarkRunner.java
    │   └── workloads
    │       ├── Workload.java
    │       ├── WorkloadGet.java
    │       ├── WorkloadGetWhen.java
    │       ├── WorkloadMixedMultiPutMultiGet.java
    │       ├── WorkloadMixedPutGet.java
    │       ├── WorkloadMultiGet.java
    │       ├── WorkloadMultiPut.java
    │       ├── WorkloadPut.java
    │       └── WorkloadsConfig.java
    └── utils
        ├── CSVWriter.java
        ├── LogWriter.java
        └── PacketType.java
```

## Setup and Execution
1. **Server**
```bash
cd src
java server.serverMain <MAX_CONCURRENT USERS> <FILE_PATH> ["--load"]
```
2. **Client**
```bash
cd src
java client.ClientMain
```

## Group Members
- [Eduardo Faria](https://www.github.com/2101dudu)
- [Hélder Gomes](https://www.github.com/helderrrg)
- [Nuno Siva](https://www.github.com/NunoMRS7)
- [Pedro Pereira](https://www.github.com/pedrofp4444)

