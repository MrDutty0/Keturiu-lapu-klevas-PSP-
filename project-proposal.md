# Non-relational Database Management System

## Definitions

- **Page** — a segment of a file.
- **Collection** — a set of data that follows JSON format.
- **Record** — a single instance of a data object.

## Must-Have Features

1. **Data storage in fixed-size pages.** The whole database is held in a single file, divided into fixed-size pages (e.g. 1KB). This allows fast searching via a hash strategy, and will allow multiple users to modify data in different pages concurrently, instead of locking the whole database — the latter is planned as a future feature.

2. **Flexible record layout within a page.** Pages hold multiple records of different sizes.

3. **Page compaction.** Deletion does not happen immediately — records are marked as deleted. When a page is filled with 70% deleted slots, the whole page is rewritten without the deleted records.

4. **Hash-based indexing** — supports primary and secondary indexing.

   - **Primary indexing:** each record has a primary field (a unique identifier per record). The record's unique key is hashed, and the modulo operator decides which bucket the record's data is placed in. This allows O(1) operations instead of O(n), where n is the size of the whole database.
   - **Secondary indexing:** non-primary fields can optionally be indexed, allowing fast operations on those fields too. A dedicated set of pages is allocated per additional non-primary index. All records in the relevant collection are scanned, their value for the indexed field is hashed, and placed into buckets. Unlike primary indexing, secondary index pages do not store the full record data — only an array of the corresponding primary keys.

5. **CRUD (create, read, update, delete) API over HTTP**, supporting our query language.

   The API looks like:

   - `POST /people` — JSON body
   - `GET /collection?query="(name=John&surname=Doe)|(name=Bob&surname=Smith)"`
   - `PUT /collection-name` — JSON body
   - `DELETE /collection?query="id=1"`

6. **Persistent storage.** Data is written to persistent storage on the drive.

7. **Support for multiple collections.**

8. **Supports major operating systems** — Linux, Windows, macOS.

## Nice-to-Have Features

- Multiple readers at once
- Result caching
- Data compression
- Slotted array optimization

## Out of Scope

- Sorted/range-based indexing
- Fixed schemas / column types
- Database locking per page (instead of the whole database)
- Multi-computer networked database
- Crash recovery — the database needs to be able to recover after an unexpected shutdown. When wanting to make a data change, a log is written, and only then is the change applied, so the database recovers safely after a crash.
