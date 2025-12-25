# Imports
import ffmpeg

# Function to extract audio from a video to a file
def extract_audio(video_file: str, audio_output_file: str):
    (
        ffmpeg
        .input(video_file)           # Input file
        .output(
            audio_output_file,       # Output file
            **{"q:a": 0, "vn": None} # Options
        )
        .run(overwrite_output=True)  # If file exists, overwrite
    )
    print(f"Audio extracted to {audio_output_file}")
