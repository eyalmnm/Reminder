package com.em_projects.reminder.loaders;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eyalmuchtar on 1/4/18.
 */

// Ref: https://stackoverflow.com/questions/28277210/load-all-images-from-assets-folder-dynamically

public class LoaderFromAssets {

    public static ArrayList<Drawable> getImagesByDir(Context context, String dirName) throws IOException {
        String[] images = context.getAssets().list("images");
        ArrayList<String> listImages = new ArrayList<String>(Arrays.asList(images));

        ArrayList<Drawable> drawables = new ArrayList<>(listImages.size());
        for (int position = 0; position < listImages.size(); position ++) {
            InputStream inputstream = context.getAssets().open("images/"
                    + listImages.get(position));
            Drawable drawable = Drawable.createFromStream(inputstream, null);
            drawables.add(drawable);
        }
        return drawables;
    }
}
