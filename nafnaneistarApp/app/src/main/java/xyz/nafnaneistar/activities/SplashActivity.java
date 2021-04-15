package xyz.nafnaneistar.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import xyz.nafnaneistar.helpers.Prefs;
import xyz.nafnaneistar.loginactivity.R;
import xyz.nafnaneistar.loginactivity.databinding.ActivitySplashBinding;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import java.util.Set;

public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;
    private Prefs mPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefs = new Prefs(SplashActivity.this);
        if(mPrefs.getUser().length == 2){
            mPrefs.CheckLogin(mPrefs.getUser());
        }
        setContentView(R.layout.activity_splash);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash);
        binding.videoView.setVideoURI(video);
        binding.videoView.start();
        binding.videoView.setOnCompletionListener(mediaPlayer -> mPrefs.CheckLogin(mPrefs.getUser()));
        binding.videoView.setOnPreparedListener(mp -> {
            mp.setVolume(0,0);

        });
    }
}