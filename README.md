# variable-length-record-storage
This project presents an implementation of a free space map for a simulated DBMS to manage disk block allocation for variable-length records.


## Features
- **Read CSV Data**: Imports data from `chips.csv` excluding the header.
  Product,Type,Release Date,Transistors (million)
  AMD 70,CPU,2012-09-01,
  Intel 110,CPU,2007-06-01,176
- **Create DB File**: Initializes `dbfile.txt` with an empty block to simulate disk storage.
- **Free-Space Map**: Maintains a map to track free space within blocks.
- **Process Records**: Inserts records from CSV into the DB file according to block availability.
- **Output Free-Space Map**: Outputs the map post-processing to reflect block status.

## Usage
1. Place `chips.csv` in the project directory.
2. Run the script to process and insert records.
3. Check `dbfile.txt` for the inserted records.
