package com.example.likebebop.mysound;

import android.app.Activity;
import android.os.Bundle;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity {

    KaleSound sound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KaleConfig.INSTANCE.init(getApplicationContext());
        ButterKnife.bind(this);
        sound = new KaleSound();
    }

    @OnClick(R.id.vibrate_btn)
    public void onVibrateBtn() {
        sound.vibrate(100);
    }

    int soundId = 0;
    int streamId = 0;

    @OnClick(R.id.load_btn)
    public void onLoadBtn() {
        soundId = sound.loadShortSound(StickerHelper.ASSET_PREFIX + "sound/beep.mp3");
    }

    @OnClick(R.id.load2_btn)
    public void onLoad2Btn() {
        soundId = sound.loadShortSound(StickerHelper.ASSET_PREFIX + "sound/dreaming_2_5s.aac");
    }

    @OnClick(R.id.load3_btn)
    public void onLoad3Btn() {
        soundId = sound.loadShortSound(StickerHelper.ASSET_PREFIX + "sound/dreaming_10s.aac");
    }

    @OnClick(R.id.play_btn)
    public void onPlayBtn() {
        streamId = sound.play(soundId, KaleSound.NO_LOOP);
    }

    @OnClick(R.id.play_loop_btn)
    public void onPlayLoopBtn() {
        streamId = sound.play(soundId, KaleSound.INFINITE_LOOP);
    }

    @OnClick(R.id.stop_btn)
    public void onStopBtn() {
        sound.stop(streamId);
        streamId = 0;
    }

    @OnClick(R.id.release_btn)
    public void onReleaseBtn() {
        sound.release();
        sound = new KaleSound();
    }
}
