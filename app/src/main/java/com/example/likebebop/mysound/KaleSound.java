package com.example.likebebop.mysound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Phaser;

import rx.Scheduler;
import rx.android.schedulers.HandlerScheduler;

/**
 * Created by likebebop on 2017. 8. 9..
 */

public class KaleSound implements SoundPool.OnLoadCompleteListener {

    private final Context context;
    public static Scheduler soundScheduler;
    public static Handler handler;
    public static AudioManager audioManager;

    static final int INVALID_ID = 0;
    static final int INFINITE_LOOP = -1;
    static final int NO_LOOP = 0;
    SoundPool soundPool;

    static {
        HandlerThread handlerThread = new HandlerThread("KaleSoundThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        soundScheduler = HandlerScheduler.from(new Handler(handlerThread.getLooper()));
    }

    public KaleSound() {
        context = KaleConfig.INSTANCE.context;
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        //-- 비동기 완료는 main thread에서 받기 위해서
        soundPool.setOnLoadCompleteListener(this);
        handler.post(()->{
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        });
    }

    //-- https://stackoverflow.com/questions/14255019/latch-that-can-be-incremented
    Phaser phaser = new Phaser(1);

    @WorkerThread
    public void vibrate(int milliseconds) {
        handler.post(()->{
            KaleLogging.PROFILER.tick();
            Vibrator v = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(milliseconds);
            KaleLogging.PROFILER.tockWithDebug("vibrate "+ milliseconds);
        });
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
        KaleLogging.CUR_LOG.debug("onLoadComplete " + soundId);
        phaser.arriveAndDeregister();
    }

    public int loadShortSound(String path) {
        int soundId = INVALID_ID;
        try {
            KaleLogging.PROFILER.tick();
            phaser.register();
            if (StickerHelper.isAsset(path)) {
                AssetFileDescriptor fd = context.getAssets().openFd(StickerHelper.getAssetPath(path));
                soundId = soundPool.load(fd, 1);
                return soundId;
            }
            soundId = soundPool.load(path, 1);
        } catch (IOException e) {
            KaleLogging.CUR_LOG.warn(e);
        } finally {
            KaleLogging.PROFILER.tockWithDebug(String.format("loadShortSound (%s, %d)", path, soundId));
            return soundId;
        }
    }

    class StreamIdHolder {
        public int streamId = 0;
        public StreamIdHolder(int id) {
            this.streamId = id;
        }
    }

    int lastMyStreamId = 0;
    HashMap<Integer, StreamIdHolder> myStreamIdMap = new HashMap<>();

    @WorkerThread
    public int play(int soundId, int loop) {
        if (soundId <= INVALID_ID) {
            return 0;
        }
        final int myStreamId = ++lastMyStreamId;
        handler.post(()-> {
            try {
                phaser.arriveAndAwaitAdvance();
                KaleLogging.PROFILER.tick();
                float v = getVolume();
                StreamIdHolder h = new StreamIdHolder(soundPool.play(soundId, v, v, 1, loop, 1));
                myStreamIdMap.put(myStreamId, h);
                KaleLogging.PROFILER.tockWithDebug(String.format("play (%d) = %d", soundId, h.streamId));
            } catch (Exception e) {
                KaleLogging.CUR_LOG.warn(e);
            }
        });
        return myStreamId;
    }

    @WorkerThread
    public void stop(int streamId) {
        handler.post(()-> {
            try {
                if (!myStreamIdMap.containsKey(streamId)) {
                    return;
                }
                phaser.arriveAndAwaitAdvance();
                StreamIdHolder holder = myStreamIdMap.get(streamId);
                KaleLogging.PROFILER.tick();
                soundPool.stop(holder.streamId);
                KaleLogging.PROFILER.tockWithDebug("stop " + holder.streamId);
                myStreamIdMap.remove(streamId);
            } catch (Exception e) {
                KaleLogging.CUR_LOG.warn(e);
            }
        });
    }

    float getVolume() {
        float actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actVolume / maxVolume;
    }

    @WorkerThread
    void release() {
        handler.post(()-> soundPool.release());
    }
}
