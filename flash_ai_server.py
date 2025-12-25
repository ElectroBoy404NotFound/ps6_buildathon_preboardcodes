# Imports
import sys
import time
import os

# More imports
from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.video.VideoClip import TextClip
from moviepy.video.compositing.CompositeVideoClip import CompositeVideoClip

# Most imports
from faster_whisper import WhisperModel
import hashlib

# Mostest imports
from flask import Flask, send_file, g, jsonify, request
from werkzeug.utils import secure_filename

# Mosterest import
from database_utils import db_init, get_db, get_sha_file, add_entry
from audio_utils import extract_audio

# Create the flask app
app = Flask(__name__)
app.config['MAX_CONTENT_LENGTH'] = 256 * 1024 * 1024 # Configure max upload and download size (256MB)

# Initiate an instance of the Speech to Text model
model = WhisperModel("small")

db_init()

# TODO Document this function
# TODO Replace file path from string manipulation to os.path
# TODO Use secure_filename to ensure safe filename
# TODO Remove the above TODOs :)
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

# Upload file endpoint
@app.route('/upload_file', methods=['POST'])
def upload_file():
    if 'file' not in request.files:
        return '{"error": "No file uploaded! :(", "code": 1}'
    
    file = request.files['file']

    if file.filename == '':
        return '{"error": "No file uploaded! :(", "code": 2}'
    
    # Compute the SHA256 of the file
    sha256_hash = hashlib.sha256()
    for chunk in iter(lambda: file.read(4096), b""):
        sha256_hash.update(chunk)
    
    digest = sha256_hash.hexdigest()

    file.seek(0)

    # Check if file already exists
    # TODO This checks if the file exists AND has been transcribed
    # Implement functions to keep track of uploaded files too
    existing_file = get_sha_file(digest)
    if existing_file:
        # Return the filename of the existing file
        return jsonify({
            "success": "File already exists and subtitled :)",
            "code": 0,
            "filename": existing_file.removesuffix(".subtitled.mp4")
        })

    # File doesn't exist, upload it
    filename = secure_filename(file.filename) # First ensure the filename itself is safe :)
    filepath = os.path.join("originals", filename)
    file.save(filepath)

    return jsonify({
        "success": "File uploaded :)",
        "code": 0,
        "filename": filename
    })

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
    app.run(debug=True, host="0.0.0.0")