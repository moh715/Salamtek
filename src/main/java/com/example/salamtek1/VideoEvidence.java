package com.example.salamtek1;

import java.io.File;
import java.io.IOException;

public class VideoEvidence extends Evidence {
    private File videoFile;
    private int durationSeconds;

    public VideoEvidence(String description, Customer wrong, Officer officer, File videoFile, int durationSeconds) {
        super(description, wrong, officer);
        this.videoFile = videoFile;
        this.durationSeconds = durationSeconds;
    }

    public VideoEvidence(String description, Customer wrong, Officer officer, String videoPath, int durationSeconds) throws IOException {
        this(description, wrong, officer, new File(videoPath), durationSeconds);
        if (!this.videoFile.exists()) {
            throw new IOException("Video file not found: " + videoPath);
        }
    }

    public File getVideoFile() {
        return videoFile;
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    @Override
    public String getEvidenceType() {
        return "video";
    }

    @Override
    public String toString() {
        return "video: " + videoFile.getName() + " (duration: " + durationSeconds + "s)\n"
                + description + "\n"
                + "the one who is wrong: " + wrong + "\n by Officer: " + officer;
    }
}
