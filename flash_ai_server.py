import ffmpeg
import sys
from scipy.io import wavfile
import numpy as np
import time

from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.video.VideoClip import TextClip
from moviepy.video.compositing.CompositeVideoClip import CompositeVideoClip

from faster_whisper import WhisperModel

import sqlite3

import hashlib

from flask import Flask, send_file, g
app = Flask(__name__)

model = WhisperModel("small")

with sqlite3.connect("db/subtitled_files.db") as conn:
    cur = conn.cursor()
    cur.execute("""
        CREATE TABLE IF NOT EXISTS files (
            sha256 TEXT PRIMARY KEY,
            filename TEXT NOT NULL
        )
    """)

def get_db():
    if "db" not in g:
        g.db = sqlite3.connect(
            "db/subtitled_files.db",
            check_same_thread=False
        )
    return g.db

def get_sha_file(sha256):
    conn = get_db()
    cur = conn.cursor()

    cur.execute("SELECT filename FROM files WHERE sha256 = ?", (sha256,))
    row = cur.fetchone()

    if row:
        print("Exists, filename:", row[0])
        return row[0]
    else:
        print("Not found")
        return None

def add_entry(sha256, filename):
    conn = get_db()
    cur = conn.cursor()

    cur.execute(
        "INSERT OR IGNORE INTO files (sha256, filename) VALUES (?, ?)",
        (sha256, filename)
    )

    conn.commit()

def extract_audio(video_file: str, audio_output_file: str):
    (
        ffmpeg
        .input(video_file)
        .output(
            audio_output_file,
            **{"q:a": 0, "vn": None}
        )
        .run(overwrite_output=True)
    )
    print(f"Audio extracted to {audio_output_file}")

@app.route("/transcribe/<file>")
def transcribe(file):
    print(f"Subtitling file {file}")
    
    with open(f"../{file}", "rb") as f:
        file_hash = hashlib.sha256(f.read()).hexdigest()

    existing_file_name = get_sha_file(file_hash)
    if existing_file_name:
        print(f"File alredy subtitled! {file}")
        return send_file(existing_file_name, as_attachment=True)

    ts = time.time()
    extract_audio(f"../{file}", f"extracted/{ts}.wav")
    video = VideoFileClip(f"../{file}")

    segments, info = model.transcribe(f"extracted/{ts}.wav", word_timestamps=True)

    transcription = ""

    text_clips = []

    for segment in segments:
        transcription += f"{segment.start:.2f}s -> {segment.end:.2f}s: {segment.text}"
        transcription += f"\n"
        print(f"Segment: {segment.start:.2f}s -> {segment.end:.2f}s: {segment.text}")
        for word in segment.words:
            print(f"\tWords: {word.start:.2f}s -> {word.end:.2f}s: {word.word}")
            imposition = TextClip(
                text=word.word,
                font_size=40,
                color="white",
                font="/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf"
            ).with_start(word.start).with_end(word.end).with_position(("center", "bottom"))
            text_clips.append(imposition)

    print(transcription)

    final = CompositeVideoClip([video] + text_clips)
    final.write_videofile(f"subtitled/{file}.subtitled.mp4", codec="libx264", audio_codec="aac")

    add_entry(file_hash, f"subtitled/{file}.subtitled.mp4")

    return send_file(f"subtitled/{file}.subtitled.mp4", as_attachment=True)

@app.teardown_appcontext
def close_db(exception):
    db = g.pop("db", None)
    if db is not None:
        db.close()

if __name__ == "__main__":
    app.run(debug=True)