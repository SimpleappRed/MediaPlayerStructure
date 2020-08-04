package com.example.music;

public class Song {

    private String mSongTitle;
    private String mArtistName;
    private int mImageId;
    private int mAudioResourceId;


    public Song(String songTitle, String artistName, int imageId, int audioResourceId) {
        mSongTitle = songTitle;
        mArtistName = artistName;
        mImageId = imageId;
        mAudioResourceId = audioResourceId;
    }

    public String getSongTitle() {
        return mSongTitle;

    }

    public String getArtistName() {
        return mArtistName;

    }

    public int getImageId() {
        return mImageId;
    }

    public int getAudioResourceId() {
        return mAudioResourceId;
    }

}
