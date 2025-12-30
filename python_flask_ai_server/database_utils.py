import mysql.connector
from mysql.connector import Error

# MySQL credentials
DB_CONFIG = {
    "host": "localhost",
    "user": "titly",
    "password": "Titly_2025@",
    "database": "titly"
}

conn = None
cursor = None

def db_init():
    """Initialize the database table if it doesn't exist."""
    global conn, cursor
    try:
        conn = mysql.connector.connect(**DB_CONFIG)
        cursor = conn.cursor()
        cursor.execute("""
            CREATE TABLE IF NOT EXISTS subtitled_files (
                id INT AUTO_INCREMENT PRIMARY KEY,
                file_path VARCHAR(1024) NOT NULL,
                sha256_hash CHAR(64) NOT NULL UNIQUE
            )
        """)
        conn.commit()
    except Error as e:
        print(f"Error initializing DB: {e}")

def add_entry(sha256_hash, file_path):
    """Add a file path and its SHA256 hash to the database."""
    global conn, cursor
    if not conn or not cursor:
        print("Database not initialized.")
        return
    try:
        cursor.execute(
            "INSERT INTO subtitled_files (file_path, sha256_hash) VALUES (%s, %s)",
            (file_path, sha256_hash)
        )
        conn.commit()
    except Error as e:
        print(f"Error adding entry: {e}")

def get_sha_file(sha256_hash):
    """Return the file path for a given SHA256 hash, or None if not found."""
    global cursor
    if not cursor:
        print("Database not initialized.")
        return None
    result = None
    try:
        cursor.execute(
            "SELECT file_path FROM subtitled_files WHERE sha256_hash=%s",
            (sha256_hash,)
        )
        row = cursor.fetchone()
        if row:
            result = row[0]
    except Error as e:
        print(f"Error fetching SHA: {e}")
    return result