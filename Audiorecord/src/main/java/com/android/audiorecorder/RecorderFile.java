package com.android.audiorecorder;

public class RecorderFile {

    public final static int MEDIA_TYPE_IMAGE = 0;
    public final static int MEDIA_TYPE_VIDEO = 1;
    public final static int MEDIA_TYPE_AUDIO = 2;
    
    private int id;
    private String path;
    private String name;
    private long size;
    private int duration;
    private String mimeType;
    private int launchType;
    private long time;
    private long progress;
    
    private int mediaType;//jpg video audio
    private int width;//resolution w
    private int height;//resolution h
    private String summary;
    
    public RecorderFile() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getLaunchType() {
        return launchType;
    }

    public void setLaunchType(int launchType) {
        this.launchType = launchType;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "RecorderFile [id=" + id + ", path=" + path + ", name=" + name
                + ", size=" + size + ", duration=" + duration + ", mimeType="
                + mimeType + ", launchType=" + launchType + ", time=" + time
                + ", progress=" + progress + ", mediaType=" + mediaType
                + ", width=" + width + ", height=" + height + ", summary="
                + summary + "]";
    }
    
}
