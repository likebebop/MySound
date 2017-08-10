package com.example.likebebop.mysound;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.support.annotation.WorkerThread;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;

/**
 * Created by likebebop on 2017. 8. 10..
 */

public class KaleSound {
    public static Scheduler soundScheduler;
    public static Handler handler;
    public static AudioManager audioManager;

    static {
        HandlerThread handlerThread = new HandlerThread("KaleSoundThread");
        handlerThread.start();
        KaleSound.handler = new Handler(handlerThread.getLooper());
        KaleSound.soundScheduler = AndroidSchedulers.from(handlerThread.getLooper());
        audioManager = (AudioManager) KaleConfig.INSTANCE.context.getSystemService(Context.AUDIO_SERVICE);
    }

    KaleSoundPool soundPool = new KaleSoundPool();

    @WorkerThread
    public void vibrate(int milliseconds) {
        handler.post(()->{
            KaleLogging.PROFILER.tick();
            Vibrator v = (Vibrator)KaleConfig.INSTANCE.context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(milliseconds);
            KaleLogging.PROFILER.tockWithDebug("vibrate "+ milliseconds);
        });
    }

    public void release() {
        soundPool.release();
        soundPool = new KaleSoundPool();
    }
}
