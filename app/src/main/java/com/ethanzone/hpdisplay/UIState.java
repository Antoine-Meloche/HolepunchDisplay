package com.ethanzone.hpdisplay;

import static com.ethanzone.hpdisplay.HPDisplay.DEFAULT_DELAY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

public class UIState {

    public String title;
    public String description;
    public Drawable icon;
    public Drawable miniIcon;
    public Drawable miniIconRight;
    public int shape;

    // Define the shape constants
    public static final int SHAPE_NULL = -1;
    public static final int SHAPE_NOCHANGE = 0;
    public static final int SHAPE_CLOSED = 1;
    public static final int SHAPE_OPEN = 2;

    // Other constants
    public static Drawable ICON_BLANK = null;

    public static final String DEFAULT_TITLE = "Nothing to display";
    public static final String DEFAULT_DESCRIPTION = "Check back later";

    public static UIState nullState() {
        return new UIState("", "",  null, null, null,  SHAPE_NULL);
    }

    public UIState(String title, String description, Drawable icon, Drawable miniIcon, Drawable miniIconRight, int shape) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.miniIcon = miniIcon;
        this.miniIconRight = miniIconRight;
        this.shape = shape;
    }

    public static UIState getCurrentState(Context context) {
        View display = ((HPDisplay) context).display;
        View pill = ((HPDisplay) context).pill;

        String title = ((TextView) display.findViewById(R.id.label)).getText().toString();
        String description = ((TextView) display.findViewById(R.id.description)).getText().toString();
        Drawable icon = display.findViewById(R.id.icon).getBackground();
        Drawable miniIcon = pill.findViewById(R.id.icon).getBackground();
        Drawable miniIconRight = pill.findViewById(R.id.icon2).getBackground();

        int shape;
        if (display.getVisibility() == View.VISIBLE){
            shape = SHAPE_OPEN;
        } else {
            shape = SHAPE_CLOSED;
        }

        return new UIState(title, description,  icon, miniIcon, miniIconRight, shape);
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    public void apply(Context context) {
        // Apply the UIState to the UI
        if (this.shape != SHAPE_NULL) {

            ICON_BLANK = context.getDrawable(R.drawable.black_circle);

            View display = ((HPDisplay) context).display;
            View pill = ((HPDisplay) context).pill;

            ((TextView) display.findViewById(R.id.label)).setText(this.title);
            ((TextView) display.findViewById(R.id.description)).setText(this.description);
            display.findViewById(R.id.icon).setBackground(this.icon);
            pill.findViewById(R.id.icon).setBackground(this.miniIcon);
            pill.findViewById(R.id.icon2).setBackground(this.miniIconRight);

            // Size the display

            switch (this.shape) {
                case SHAPE_CLOSED:
                    contract(display, pill);
                    break;
                case SHAPE_OPEN:
                    expand(display);
                    break;
                case SHAPE_NOCHANGE:
                    break;
            }
        }
    }

    public final static WindowManager.LayoutParams smallParams = new WindowManager.LayoutParams(
            1000,
            150,
            0, -15,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
            , PixelFormat.TRANSPARENT);

    public final static WindowManager.LayoutParams largeParams = new WindowManager.LayoutParams(
            1000,
            500,
            0, -190,
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
            , PixelFormat.TRANSPARENT);

    private boolean expanding;

    private void expand(View display) {
        if (!this.expanding) {
            this.expanding = true;

            // Reset auto-close timer
            ((HPDisplay) display.getContext()).waitToClose();

            // Show the display
            display.setVisibility(View.VISIBLE);

            // Animate the window

            // Fade in the text
            display.findViewById(R.id.label).animate()
                    .alpha(1)
                    .setInterpolator(new LinearInterpolator())
                    .setStartDelay(400)
                    .setDuration(300);

            display.findViewById(R.id.description).animate()
                    .alpha(1)
                    .setInterpolator(new LinearInterpolator())
                    .setStartDelay(400)
                    .setDuration(300);

            // Expand the window
            display.animate()
                    .scaleX(3)
                    .scaleY(3)
                    .translationY(100)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(800);


            // Prevent double-contracts
            Handler handler = new Handler();
            handler.postDelayed(() -> this.expanding = false, 800);
        }
    }

    private boolean contracting;

    private void contract(View display, View pill) {

        if (!this.contracting) {
            this.contracting = true;


            // Animate the window

            // Fade out text
            display.findViewById(R.id.label).animate()
                    .alpha(0)
                    .setInterpolator(new LinearInterpolator())
                    .setStartDelay(400)
                    .setDuration(300);

            display.findViewById(R.id.description).animate()
                    .alpha(0)
                    .setInterpolator(new LinearInterpolator())
                    .setStartDelay(400)
                    .setDuration(300);

            // Shrink window
            display.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .translationY(0)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setDuration(800);

            // Prevent double-contracts
            Handler handler = new Handler();
            handler.postDelayed(() -> {

                // Ensure the pill's button is on the bottom
                pill.findViewById(R.id.button).setElevation(-10);

                // Hide the Display
                display.setVisibility(View.GONE);

                this.contracting = false;

            }, 800);
        }
    }
}
