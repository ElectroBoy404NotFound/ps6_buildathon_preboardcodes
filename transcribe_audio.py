from moviepy.video.io.VideoFileClip import VideoFileClip
from moviepy.video.VideoClip import TextClip
from moviepy.video.compositing.CompositeVideoClip import CompositeVideoClip

from faster_whisper import WhisperModel

video = VideoFileClip("test_video4_2_5_mins_wellerman_song.mp4")

model = WhisperModel("small")  # tiny/base/small/medium/large
segments, info = model.transcribe("extracted_audio.wav", word_timestamps=True)

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
final.write_videofile("output.mp4", codec="libx264", audio_codec="aac")