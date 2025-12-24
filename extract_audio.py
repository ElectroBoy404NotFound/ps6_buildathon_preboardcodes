import ffmpeg
import sys
from scipy.io import wavfile
import noisereduce as nr
import numpy as np

def extract_audio(video_file: str, audio_output_file: str):
    (
        ffmpeg
        .input(video_file)
        .output(
            audio_output_file,
            **{"q:a": 0, "vn": None}  # <- THIS is the important fix
        )
        .run(overwrite_output=True)
    )
    print(f"Audio extracted to {audio_output_file}")

def reduce_noise_stereo(rate: int, data: np.ndarray) -> np.ndarray:
    """
    Perform noise reduction per channel for stereo or just directly for mono.
    """
    if data.ndim == 1:
        # Mono
        return nr.reduce_noise(y=data, sr=rate)
    else:
        # Stereo (or more channels). Apply noise reduction channel-wise.
        channels = []
        for ch in range(data.shape[1]):
            channels.append(nr.reduce_noise(y=data[:, ch], sr=rate))
        # Stack back and ensure shape is (samples, channels)
        return np.stack(channels, axis=1)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python extract_audio.py <input_video.mp4>")
        sys.exit(1)

    video_file = sys.argv[1]
    audio_output_file = "extracted_audio.wav"
    
    extract_audio(video_file, audio_output_file)

    rate, data = wavfile.read(audio_output_file)
    print(f"Read WAV: rate={rate}, shape={data.shape}")

    # reduce noise safely no matter mono/stereo
    reduced = reduce_noise_stereo(rate, data)

    # Make sure we cast back to original dtype
    reduced = np.asarray(reduced, dtype=data.dtype)
    out_name = "extracted_audio_noise_reduced.wav"
    wavfile.write(out_name, rate, reduced)
    print(f"Noise-reduced audio written to {out_name}")
