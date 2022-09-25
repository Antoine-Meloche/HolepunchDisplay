package com.ethanzone.hpdisplay;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.view.KeyEvent;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

public class MediaManager {

    public AudioManager audioManager;
    private final MediaSessionManager mediaSessionManager;
    private MediaController mediaController;
    private final ComponentName notificationListener;
    private final Context context;

    public MediaManager(Context context) {
        this.audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
        this.notificationListener = new ComponentName(context, NotiService.class);
        this.context = context;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public void updateMedia() {
        UIState uiState = ((HPDisplay) this.context).uiState;
        if (checkMedia() && ((HPDisplay) this.context).notificationHandler.backedUpState == null) {
            try {
                uiState.title = getTitle();
                uiState.description = getDescription();
                if (audioManager.isMusicActive()) {
                    uiState.icon = getIcon();
                    uiState.leftbtn = this.context.getDrawable(R.drawable.skip_prev);
                    uiState.midbtn = this.context.getDrawable(R.drawable.pause);
                    uiState.rightbtn = this.context.getDrawable(R.drawable.skip_next);
                } else {
                    //uiState.icon = this.context.getDrawable(R.drawable.playbutton);
                    uiState.icon = getIcon();
                    uiState.midbtn = this.context.getDrawable(R.drawable.playbutton);
                }
                uiState.miniIcon = getIcon();
            } catch (NullPointerException e) {
                e.printStackTrace();
                uiState = ((HPDisplay) this.context).notificationHandler.backedUpState;
            }
            uiState.shape = UIState.SHAPE_NOCHANGE;
            try {
                uiState.apply();
            } catch (NullPointerException e) {
                // Music was closed
            }
        } else {
            uiState.leftbtn = this.context.getDrawable(R.drawable.black_circle);
            uiState.midbtn = this.context.getDrawable(R.drawable.black_circle);
            uiState.rightbtn = this.context.getDrawable(R.drawable.black_circle);
        }
    }

    public boolean checkMedia() {
        try {
            if (mediaSessionManager.getActiveSessions(notificationListener).size() > 0) {
                mediaController = mediaSessionManager.getActiveSessions(notificationListener).get(0);
                return true;
            }
        } catch (SecurityException e) {
            // Permissions not correctly set
        }
        return false;
    }

    public Drawable getIcon() {
        RoundedBitmapDrawable sr_icon = RoundedBitmapDrawableFactory.create(context.getResources(), mediaController.getMetadata().getDescription().getIconBitmap());
        sr_icon.setCircular(true);
        return sr_icon;
    }

    public String getTitle() {
        return this.mediaController.getMetadata().getString(MediaMetadata.METADATA_KEY_TITLE);
    }

    public String getDescription() {
        String artist = this.mediaController.getMetadata().getString(MediaMetadata.METADATA_KEY_ARTIST);
        if (artist != null) {
            return artist;
        } else {
            return "";
        }
    }

    public void pause() {
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE));
    }

    public void play() {
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY));
    }

    public void prev() {
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS));
    }

    public void next() {
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT));
        audioManager.dispatchMediaKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT));
    }
}
