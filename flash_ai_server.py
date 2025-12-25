# Imports
import sys
import time

# More imports
from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.video.VideoClip import TextClip
from moviepy.video.compositing.CompositeVideoClip import CompositeVideoClip

# Most imports
from faster_whisper import WhisperModel
import hashlib

# Mostest imports
from flask import Flask, send_file, g

# Mosterest import
from database_utils import db_init, get_db, get_sha_file, add_entry
from audio_utils import extract_audio

# Create the flask app
app = Flask(__name__)

# Initiate an instance of the Speech to Text model
model = WhisperModel("small")

db_init()

# TODO Document this function
@app.route("/transcribe/<file>")
def transcribe(file):
    print(f"Subtitling file {file}")
    
    with open(f"originals/{file}", "rb") as f:
        file_hash = hashlib.sha256(f.read()).hexdigest()

    existing_file_name = get_sha_file(file_hash)
    if existing_file_name:
        print(f"File alredy subtitled! {file}")
        return send_file(existing_file_name, as_attachment=True)

    ts = time.time()
    extract_audio(f"originals/{file}", f"extracted/{ts}.wav")
    video = VideoFileClip(f"originals/{file}")

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

# Clean up function
@app.teardown_appcontext
def close_db(exception):
    # Get the database instance
    db = g.pop("db", None)

    # If it exists, close it
    if db is not None:
        db.close()

# Main function
if __name__ == "__main__":
    app.run(debug=True)