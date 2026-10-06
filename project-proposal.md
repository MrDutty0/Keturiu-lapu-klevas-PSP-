# Non-relational Database Management System

## Must-Have features

1. **Collections.** Supports named collections storing data in **Binary JSON** format.

2. **Database Page.** A fixed-size (**4KB**) contiguous block of binary storage within a database file.

3. **Slotted array optimization.** Keeps track of records using fixed page slots, making lookups fast and allowing data cleanup without breaking links.

4. **Record Limit.** When a record does not fit into a single page, an overflow pointer is written into the record's primary page slot, linking it to a dedicated chain of 4 KB overflow pages that store the document's payload. Maximum allowed record size is **4 MB**.

5. **Page compaction.** Clean up fragmented pages based on:
    1. **On-Demand.** If new record would fit into page if compaction was performed.
    2. **Threshold-Based.** When dead space exceeds **40%**.

6. **Page-Level Concurrency Control.** Manages concurrent read and write access using page-scope locks (Shared/Exclusive), allowing multiple operations to execute safely without conflicting.

7. **Hash-based indexing** — supports primary and secondary indexing.
    * **Primary indexing:** Each record has a primary field (a unique identifier per record). The record's unique key is hashed, and the modulo operator decides which bucket the record's data is placed into. This allows $O(1)$ operations instead of $O(N)$ time complexity, where $N$ is the size of the whole database.
    * **Secondary indexing:** Non-primary fields can optionally be indexed, allowing fast operations on those fields too. A dedicated set of pages is allocated per additional non-primary index. All records in the relevant collection are scanned, their values for the indexed field are hashed, and their entries are placed into buckets. Unlike primary indexing, secondary index pages do not store the full record data — only an array of the corresponding primary keys.

8. **Transactions.** A sequence of one or more database operations (reads, inserts, updates, or deletes) executed as a single, indivisible unit of work.

9. CRUD (Create, Read, Update, Delete) API over HTTP

   Supports document management and filtering via a RESTful HTTP interface:

   - `POST   /{collection}`          — Inserts a new JSON record into the specified collection.
   - `GET    /{collection}?query=...` — Fetches records matching filter conditions (e.g., "(name=John&surname=Doe)|(name=Bob&surname=Smith)").
   - `PUT    /{collection}/{id}`     — Updates an existing record by its primary key using a JSON body.
   - `DELETE /{collection}/{id}`     — Removes a specific record by its primary key.
   - `DELETE /{collection}?query=...` — Deletes records matching the query expression.

10. **Cross Platform.** Support for Linux, Windows and macOS operating systems. 

## Should-Have features

1. **In-Memory Query Result Caching.** Caches the JSON outputs of frequent GET queries in an LRU memory buffer, automatically clearing or deleting stale query results from the RAM cache whenever data in the database changes.

2. **Shared Key Dictionary.** Stores JSON keys in a collection-scoped lookup table and uses an availability bitmask to track slot usage. Periodically recycles unreferenced key slots.

3. **Write-Ahead Logging (WAL).**

## Could-Have features

1. **Data compression.** Compresses raw 4 KB page blocks before writing to disk using standard compression algorithms.

## Won't Have features

1. **Multi-computer networked database.**
2. **Sorted/range-based indexing.**
