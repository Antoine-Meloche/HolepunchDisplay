package com.ethanzone.hpdisplay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import android.os.Handler;


public class Gestures {

    private final Context context;
    private final View display;
    private final View pill;
    private final MediaManager mediaManager;

    public Gestures(Context context, View display, View pill, MediaManager mediaManager) {
        this.context = context;
        this.display = display;
        this.pill = pill;
        this.mediaManager = mediaManager;
    }

    private float prevY;

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    public void setListeners() {

        AudioManager audioManager = mediaManager.audioManager;

        // Open display on pill click
        this.pill.findViewById(R.id.button).setOnTouchListener((view, motionEvent) -> {
            Log.v("event", "Click!");

            UIState uiState = ((HPDisplay) this.context).uiState;
            uiState.shape = UIState.SHAPE_OPEN;
            uiState.apply();

            // Show music if playing
            mediaManager.updateMedia();

            return true;
        });

        // Manage display touches and gestures
        this.display.setOnTouchListener((View view, MotionEvent event) -> {

            // Reset the auto-close timer
            ((HPDisplay) this.context).waitToClose();

            float x = event.getX();
            float y = event.getY();

            //Button icon = this.display.findViewById(R.id.icon);
            //float distance = (float) Math.sqrt(Math.pow(x - (icon.getX() - 100), 2) + Math.pow(y - icon.getY(), 2));

            Button midbtn = this.display.findViewById(R.id.midbtn);
            float middistance = (float) Math.sqrt(Math.pow(x - (midbtn.getX() + 150), 2) + Math.pow(y - (midbtn.getY() + 300), 2));

            Button leftbtn = this.display.findViewById(R.id.leftbtn);
            float leftdistance = (float) Math.sqrt(Math.pow(x - (leftbtn.getX() + 100), 2) + Math.pow(y - (leftbtn.getY() + 300), 2));

            Button rightbtn = this.display.findViewById(R.id.rightbtn);
            float rightdistance = (float) Math.sqrt(Math.pow(x - (rightbtn.getX() + 200), 2) + Math.pow(y - (rightbtn.getY() + 300), 2));

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    // Detect clicks
                    float lmmin = Math.min(leftdistance, middistance);
                    float tmin = Math.min(lmmin, rightdistance);
                    if (tmin >= 100f) { break; }
                    if (middistance == tmin) {
                        Log.v("event", "midbtn click");

                        UIState uiState = ((HPDisplay) this.context).uiState;
                        if (mediaManager.checkMedia() && ((HPDisplay) this.context).notificationHandler.backedUpState == null) {
                            if (audioManager.isMusicActive()) {

                                mediaManager.pause();
                                uiState.apply();

                            } else {

                                mediaManager.play();
                                mediaManager.updateMedia();

                            }

                        } else {

                            try {
                                // Open the notification
                                ((HPDisplay) this.context).clickAction.send();
                                // Close the display
                                uiState.shape = UIState.SHAPE_CLOSED;
                                uiState.apply();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            // Clear the display
                            ((HPDisplay) context).uiState.title = UIState.DEFAULT_TITLE;
                            ((HPDisplay) context).uiState.description = UIState.DEFAULT_DESCRIPTION;
                            ((HPDisplay) context).uiState.icon = context.getDrawable(R.drawable.checkmark);
                            ((HPDisplay) context).uiState.miniIcon = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.miniIconRight = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.shape = UIState.SHAPE_NOCHANGE;
                            ((HPDisplay) context).uiState.apply();
                            ((HPDisplay) this.context).notificationHandler.backedUpState = null;

                        }

                        this.prevY = 0;
                        return true;
                    } else if (leftdistance == tmin) {
                        Log.v("event", "leftbtn click");

                        UIState uiState = ((HPDisplay) this.context).uiState;
                        if (mediaManager.checkMedia() && ((HPDisplay) this.context).notificationHandler.backedUpState == null) {
                            mediaManager.prev();
                            uiState.apply();
                        } else {
                            // Clear the display
                            ((HPDisplay) context).uiState.title = UIState.DEFAULT_TITLE;
                            ((HPDisplay) context).uiState.description = UIState.DEFAULT_DESCRIPTION;
                            ((HPDisplay) context).uiState.icon = context.getDrawable(R.drawable.checkmark);
                            ((HPDisplay) context).uiState.miniIcon = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.miniIconRight = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.leftbtn = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.midbtn = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.rightbtn = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.shape = UIState.SHAPE_NOCHANGE;
                            ((HPDisplay) context).uiState.apply();
                            ((HPDisplay) this.context).notificationHandler.backedUpState = null;

                        }

                        this.prevY = 0;
                        return true;
                    } else if (rightdistance == tmin) {
                        Log.v("event", "rightbtn click");

                        UIState uiState = ((HPDisplay) this.context).uiState;
                        if (mediaManager.checkMedia() && ((HPDisplay) this.context).notificationHandler.backedUpState == null) {
                            mediaManager.next();
                            uiState.apply();
                        } else {
                            // Clear the display
                            ((HPDisplay) context).uiState.title = UIState.DEFAULT_TITLE;
                            ((HPDisplay) context).uiState.description = UIState.DEFAULT_DESCRIPTION;
                            ((HPDisplay) context).uiState.icon = context.getDrawable(R.drawable.checkmark);
                            ((HPDisplay) context).uiState.miniIcon = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.miniIconRight = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.leftbtn = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.midbtn = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.rightbtn = UIState.ICON_BLANK;
                            ((HPDisplay) context).uiState.shape = UIState.SHAPE_NOCHANGE;
                            ((HPDisplay) context).uiState.apply();
                            ((HPDisplay) this.context).notificationHandler.backedUpState = null;

                        }

                        this.prevY = 0;
                        return true;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:

                    float dy = y - this.prevY;

                    if (dy < -10) {
                        Log.v("event", "swipe up");

                        UIState uiState = ((HPDisplay) this.context).uiState;
                        uiState.shape = UIState.SHAPE_CLOSED;
                        uiState.apply();

                        return true;
                    }

                case MotionEvent.ACTION_UP:
                    break;
            }

            this.prevY = y;

            return true;
        });

    }

}
