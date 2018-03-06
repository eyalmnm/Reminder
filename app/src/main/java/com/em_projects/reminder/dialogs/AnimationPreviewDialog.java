package com.em_projects.reminder.dialogs;

import android.app.DialogFragment;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.em_projects.reminder.R;
import com.em_projects.reminder.loaders.LoaderFromAssets;
import com.em_projects.reminder.storage.db.DbConstants;
import com.em_projects.reminder.ui.AnimationCreator;
import com.em_projects.reminder.utils.StringUtils;
import com.google.firebase.crash.FirebaseCrash;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by eyalmuchtar on 1/6/18.
 */

public class AnimationPreviewDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = "AnimationPreviewDialog";

    private String animationName;

    private ImageView animationPreviewImageView;
    private Button okButton;
    private AnimationDrawable animDrawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        animationName = (String) getArguments().get(DbConstants.EVENTS_ANIMATION_NAME);
        if (true == StringUtils.isNullOrEmpty(animationName)) dismiss();
        return inflater.inflate(R.layout.dialog_animation_priview, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        animationPreviewImageView = view.findViewById(R.id.animationPreviewImageView);
        okButton = view.findViewById(R.id.okButton);
        okButton.setOnClickListener(this);

        ArrayList<Drawable> drawables = null;
        try {
            drawables = LoaderFromAssets.getImagesByDir(getActivity(), animationName);
            animDrawable = AnimationCreator.createAnimationDrawable(drawables, 50, false);
            AnimationCreator.playAnimation(animationPreviewImageView, animDrawable);
        } catch (IOException e) {
            FirebaseCrash.logcat(Log.ERROR, TAG, "onViewCreated");
            FirebaseCrash.report(e);
            Log.e(TAG, "onViewCreated", e);
            Toast.makeText(getActivity(), "Failed to play animation", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        animDrawable.stop();
        dismiss();
    }
}
