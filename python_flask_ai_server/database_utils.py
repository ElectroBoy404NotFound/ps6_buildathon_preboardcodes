# Imports
import sqlite3
from flask import g

# Ensure all the tables (subtitled files) exist
def db_init():
    with sqlite3.connect("db/subtitled_files.db") as conn:
        cur = conn.cursor()
        cur.execute("""
            CREATE TABLE IF NOT EXISTS files (
                sha256 TEXT PRIMARY KEY,
                filename TEXT NOT NULL
            )
        """)

# Get the DB instance
def get_db():
    # Create a DB instance if the instance doesn't exist
    if "db" not in g:
        g.db = sqlite3.connect(
            "db/subtitled_files.db",
            check_same_thread=False
        )
    return g.db

# Function to get the file name belonging to a subtitled file from the sha256 if it exists
# If the entry isn't found, it returns None
def get_sha_file(sha256):
    conn = get_db()
    cur = conn.cursor()

    cur.execute("SELECT filename FROM files WHERE sha256 = ?", (sha256,))
    row = cur.fetchone()

    if row:
        # SHA256 entry exists
        print("Exists, filename:", row[0])
        return row[0]
    else:
        # SHA256 entry doesn't exist
        print("Not found")
        return None

# Function to add a file entry into the database with its sha256 and filename
def add_entry(sha256, filename):
    conn = get_db()     # Get the DB instance
    cur = conn.cursor() # Get the pointer

    # Execute the query to INSERT an entry into the DB
    cur.execute(
        "INSERT OR IGNORE INTO files (sha256, filename) VALUES (?, ?)",
        (sha256, filename)
    )

    # Write changes to the DB file
    conn.commit()