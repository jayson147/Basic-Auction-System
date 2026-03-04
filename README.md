# Distributed Auction System — Java RMI & AES Encryption

A client-server auction system built in Java, demonstrating distributed computing via Java RMI (Remote Method Invocation) with AES encryption to secure all data in transit.

---

## Overview

This project simulates an online auction platform where a server hosts auction items and clients can remotely query item details. All communication between client and server is encrypted using AES symmetric encryption — auction item data is sealed server-side before transmission and decrypted client-side using a shared key.

---

## Architecture

```
┌────────────────────┐         Java RMI (port 1099)        ┌────────────────────┐
│   AuctionClient    │  ──── SealedObject (AES encrypted) ──▶   AuctionServer   │
│                    │  ◀───────────────────────────────────                    │
│  - Looks up RMI    │                                       │  - Hosts items    │
│    registry        │                                       │  - Encrypts data  │
│  - Decrypts item   │                                       │  - Manages AES    │
│    using AES key   │                                       │    key lifecycle  │
└────────────────────┘                                       └────────────────────┘
```

**Key components:**

| File | Role |
|---|---|
| `Auction.java` | Remote interface defining the RMI contract (`getSpec`) |
| `AuctionServer.java` | Server implementation — hosts items, handles encryption, binds to RMI registry |
| `AuctionClient.java` | Client — connects to registry, retrieves and decrypts auction items |
| `AuctionItem.java` | Serialisable data model for auction items |
| `server.sh` | Shell script to start the RMI registry and launch the server |
| `keys/testKey.aes` | Shared AES secret key (serialised `SecretKey` object) |

---

## How It Works

### 1. Key Management

On startup, the server attempts to load an existing AES key from `keys/testKey.aes`. If no key is found, it generates a fresh 128-bit AES key using `KeyGenerator` and persists it to disk via `ObjectOutputStream`. The client loads the same key file independently to decrypt responses.

```java
KeyGenerator keyGen = KeyGenerator.getInstance("AES");
aesKey = keyGen.generateKey();
```

This models a pre-shared key (PSK) setup — a common pattern in symmetric encryption schemes.

### 2. Remote Interface (RMI)

The `Auction` interface extends `java.rmi.Remote`, exposing a single method:

```java
public SealedObject getSpec(int itemID) throws RemoteException;
```

The server registers itself under the name `"Auction"` in the RMI registry on port `1099`. The client performs a lookup by name and calls `getSpec()` as if it were a local method call — with the network communication handled transparently by the RMI framework.

### 3. Encryption & Sealed Objects

Before transmitting an `AuctionItem`, the server encrypts it using Java's `SealedObject`:

```java
Cipher cipher = Cipher.getInstance("AES");
cipher.init(Cipher.ENCRYPT_MODE, aesKey);
return new SealedObject(item, cipher);  // Serialises and encrypts in one step
```

`SealedObject` wraps a serialised Java object inside an encrypted envelope. The object cannot be read without the correct cipher and key — ensuring confidentiality over the RMI channel.

### 4. Client Decryption

The client retrieves the `SealedObject`, loads the shared AES key, and decrypts:

```java
Cipher cipher = Cipher.getInstance("AES");
cipher.init(Cipher.DECRYPT_MODE, aesKey);
AuctionItem auctionItem = (AuctionItem) sealedObject.getObject(cipher);
```

---

## Running the Project

### Prerequisites

- Java JDK 8+
- The `keys/` directory must exist relative to where the server and client are launched

### Start the Server

```bash
chmod +x server.sh
./server.sh
```

This script starts the RMI registry in the background, waits for it to initialise, then launches the auction server:

```bash
rmiregistry &
sleep 2
java AuctionServer
```

You should see: `Auction Server is ready`

### Compile (if needed)

```bash
javac *.java
```

### Run the Client

In a separate terminal:

```bash
java AuctionClient
```

Enter an item ID when prompted (1, 2, or 3) to retrieve encrypted auction item details.

**Example output:**
```
Enter itemID to get item details:
2
Item name: Painting   Item Description: Mona Lisa   Highest Bid: 3000
```

---

## Auction Items (Server-side)

| ID | Name | Description | Starting Bid |
|---|---|---|---|
| 1 | Vase | Exquisite colours | £1,500 |
| 2 | Painting | Mona Lisa | £3,000 |
| 3 | Table | Sturdy fine wood | £5,000 |

---

## Concepts Demonstrated

- **Java RMI** — remote object lookup, registry binding, and transparent network communication via a defined remote interface
- **Symmetric AES encryption** — key generation, persistence, and usage with `javax.crypto`
- **SealedObject** — encrypting serialised Java objects for secure transmission
- **Client-server architecture** — separation of concerns between server (data hosting, encryption) and client (querying, decryption)
- **Java serialisation** — `AuctionItem` implements `Serializable` to support both RMI transport and object sealing
- **Pre-shared key model** — both parties load the same key file independently, mirroring real-world PSK authentication patterns


