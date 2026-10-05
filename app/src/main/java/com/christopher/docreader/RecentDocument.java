package com.christopher.docreader;

public class RecentDocument {

    private String name;
    private String uri;
    private String mimeType;
    private String extension;
    private long lastOpenedAt;
    private float readingProgress;

    public RecentDocument(
            String name,
            String uri,
            String mimeType,
            String extension,
            long lastOpenedAt,
            float readingProgress
    ) {
        this.name = name;
        this.uri = uri;
        this.mimeType = mimeType;
        this.extension = extension;
        this.lastOpenedAt = lastOpenedAt;
        this.readingProgress = readingProgress;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public long getLastOpenedAt() {
        return lastOpenedAt;
    }

    public float getReadingProgress() {
        return readingProgress;
    }
}