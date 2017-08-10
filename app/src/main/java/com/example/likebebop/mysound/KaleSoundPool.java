package com.example.likebebop.mysound;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by likebebop on 2017. 8. 9..
 *
 * SoundPool Wrapper로서 Threading과 API단순화, Asset리소스 처리등을 수행
 *
 */

public class KaleSoundPool implements SoundPool.OnLoadCompleteListener {


    static final int INVALID_ID = 0;
    static final int INFINITE_LOOP = -1;
    static final int NO_LOOP = 0;
    static final int TIMEOUT = 1000;
    SoundPool soundPool;

    public KaleSoundPool() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        //-- 비동기 완료는 main thread에서 받기 위해서
        soundPool.setOnLoadCompleteListener(this);
    }

    //-- https://stackoverflow.com/questions/14255019/latch-that-can-be-incremented
    //-- phaser를 쓰려고 했으나. 안됨; ㅎㅎ
    CountDownLatch latch = new CountDownLatch(0);

    @Override
    public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
        KaleLogging.CUR_LOG.debug("onLoadComplete " + soundId);
        synchronized (KaleSoundPool.this) {
            latch.countDown();
            //KaleLogging.CUR_LOG.warn("(-)latch.getCount() = " + latch.getCount());
        }
    }

    public int load(String path) {
        int soundId = INVALID_ID;
        try {
            KaleLogging.PROFILER.tick();
            synchronized (KaleSoundPool.this) {
                latch = new CountDownLatch((int) latch.getCount() + 1);
                //KaleLogging.CUR_LOG.warn("(+)latch.getCount() = " + latch.getCount());
            }
            if (StickerHelper.isAsset(path)) {
                AssetFileDescriptor fd = KaleConfig.INSTANCE.context.getAssets().openFd(StickerHelper.getAssetPath(path));
                soundId = soundPool.load(fd, 1);
                return soundId;
            }
            soundId = soundPool.load(path, 1);
        } catch (IOException e) {
            KaleLogging.CUR_LOG.warn(e);
        } finally {
            KaleLogging.PROFILER.tockWithDebug(String.format("load (%s, %d)", path, soundId));
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
        KaleSound.handler.post(()-> {
            try {
                synchronized (KaleSoundPool.this) {
                    latch.await(TIMEOUT, TimeUnit.MILLISECONDS);
                }
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
        KaleSound.handler.post(()-> {
            try {
                if (!myStreamIdMap.containsKey(streamId)) {
                    return;
                }
                synchronized (KaleSoundPool.this) {
                    latch.await(TIMEOUT, TimeUnit.MILLISECONDS);
                }
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
        float actVolume = (float) KaleSound.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) KaleSound.audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return actVolume / maxVolume;
    }

    @WorkerThread
    void release() {
        KaleSound.handler.post(()-> soundPool.release());
    }
}
