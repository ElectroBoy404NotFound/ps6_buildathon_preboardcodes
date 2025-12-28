package me.electronicsboy.titly.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import me.electronicsboy.titly.exceptions.ConversionFailedException;

public class VideoFragmenter {
    public static boolean convertToMSECompatible(String inputPath, String outputPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
            		"ffmpeg",
                    "-i", inputPath,
                    "-c:v", "libx264",              // re-encode video using H.264
                    "-c:a", "aac",                  // re-encode audio using AAC
                    "-movflags", "+frag_keyframe+empty_moov+default_base_moof",
                    "-f", "mp4",
                    outputPath
            );

            pb.redirectErrorStream(true); // Merge stderr into stdout
            Process process = pb.start();

            // Optionally read the output
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line); // Log or store this
                }
            }

            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static void convertToHLS(String inputPath, String outputDir) {
        List<String> command = Arrays.asList(
            "ffmpeg",
            "-i", inputPath,
            "-codec:", "copy",
            "-start_number", "0",
            "-hls_time", "10",
            "-hls_list_size", "0",
            "-f", "hls",
            outputDir + "/output.m3u8"
        );

        try {
			Files.createDirectories(Path.of(outputDir));
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConversionFailedException("Unable to create directories!");
		} 
        
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        pb.directory(new File(outputDir)); // optional

        System.out.println(outputDir);
        
		try {
			Process process = pb.start();
			// Optional: Print FFmpeg output
	        try (BufferedReader reader = new BufferedReader(
	                new InputStreamReader(process.getInputStream()))) {
	            String line;
	            while ((line = reader.readLine()) != null)
	                System.out.println(line);
	        }
	        process.waitFor();
	        if(process.exitValue() != 0)
	        	throw new ConversionFailedException("An error occured while converting file! Exit code: " + process.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConversionFailedException("Unable to do conversion! %s".formatted(e.getMessage()));
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new ConversionFailedException("Unable to do conversion! %s".formatted(e.getMessage()));
		}
    }

}
