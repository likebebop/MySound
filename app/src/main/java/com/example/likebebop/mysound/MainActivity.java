package com.example.likebebop.mysound;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class MainActivity extends Activity {

    KaleSound sound;
    Model m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KaleConfig.INSTANCE.init(getApplicationContext());
        ButterKnife.bind(this);
        init();
        m = new Model();
        m.init();
    }

    private void init() {
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
        soundId = sound.soundPool.load(StickerHelper.ASSET_PREFIX + "sound/beep.mp3");
    }

    @OnClick(R.id.unload_btn)
    public void onUnloadBtn() {
        sound.soundPool.unload(soundId);
        soundId = 0;
    }

    @OnClick(R.id.load2_btn)
    public void onLoad2Btn() {
        soundId = sound.soundPool.load(StickerHelper.ASSET_PREFIX + "sound/dreaming_2_5s.aac");
    }

    @OnClick(R.id.load3_btn)
    public void onLoad3Btn() {
        soundId = sound.soundPool.load(StickerHelper.ASSET_PREFIX + "sound/dreaming_10s.aac");
    }

    @OnClick(R.id.play_btn)
    public void onPlayBtn() {
        streamId = sound.soundPool.play(soundId, false);
    }

    @OnClick(R.id.play_loop_btn)
    public void onPlayLoopBtn() {
        streamId = sound.soundPool.play(soundId, true);
    }

    @OnClick(R.id.stop_btn)
    public void onStopBtn() {
        sound.soundPool.stop(streamId);
        streamId = 0;
    }

    @OnClick(R.id.release_btn)
    public void onReleaseBtn() {
        sound.release();
    }

    int mediaId = 0;

    @OnClick(R.id.load_m_btn)
    public void onLoadMBtn() {
        mediaId = sound.mediaSound.load(StickerHelper.ASSET_PREFIX + "sound/audio.mp4");
    }

    @OnClick(R.id.unload_m_btn)
    public void onUnloadMBtn() {
        sound.mediaSound.unload(mediaId);
        soundId = 0;
    }

    @OnClick(R.id.load2_m_btn)
    public void onLoad2MBtn() {
        mediaId = sound.mediaSound.load(StickerHelper.ASSET_PREFIX + "sound/dreaming_2_5s.aac");
    }

    @OnClick(R.id.play_m_btn)
    public void onPlayMBtn() {
        sound.mediaSound.play(mediaId, false);
    }

    @OnClick(R.id.play_m_loop_btn)
    public void onPlayMLoopBtn() {
        sound.mediaSound.play(mediaId, true);
    }



    @OnClick(R.id.stop_m_btn)
    public void onStopMBtn() {
        sound.mediaSound.stop(mediaId);
        streamId = 0;
    }

    //- backpressure test
    class Model extends BaseViewModel {
        public BehaviorSubject<Integer> rawTestRx = BehaviorSubject.create(1);
        public Observable<Integer> rawObj = rawTestRx.onBackpressureBuffer();
        public BehaviorSubject<Integer> testRx = behaviorSubject(()->rawObj, 1);
    }

    //-- onBackpressureBuffer 는 consume하는 녀석 바로 앞에 붙여야한다

    @OnClick(R.id.rx_btn)
    public void onRxBtn() {
        //--  아래는 크래시 괜찮을것 같지만..
        //m.testRx.observeOn(Schedulers.io()).subscribe((l)->
        m.testRx.onBackpressureBuffer().observeOn(Schedulers.io()).subscribe((l)->
       // m.rawObj.observeOn(Schedulers.io()).subscribe((l)->
            {
                SystemClock.sleep(1000);
                KaleLogging.CUR_LOG.debug("timer " + l);
        });
    }

    @OnClick(R.id.rx_emit_btn)
    public void onRxEmitBtn() {

        m.rawTestRx.onNext(m.rawTestRx.getValue() + 1);
    }

}
