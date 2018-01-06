package com.em_projects.reminder.ui;

import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 1/4/18.
 */

// Ref: https://stackoverflow.com/questions/10149487/animationdrawable-programmatically-without-animation-list-xml

public class AnimationCreator {

    public static AnimationDrawable createAnimationDrawable(ArrayList<Drawable> drawables, int duration, boolean setOneShot) {
        if (0 < drawables.size()) {
            AnimationDrawable animationDrawable = new AnimationDrawable();
            animationDrawable.setOneShot(setOneShot);

            for (int position = 0; position < drawables.size(); position ++) {
                animationDrawable.addFrame(drawables.get(position), 30 < duration ? duration : 50);
            }
            return animationDrawable;
        }
        return null;
    }

    public static void playAnimation(ImageView imageView, AnimationDrawable animation) {
        imageView.setBackground(animation);
        animation.start();
    }
}
