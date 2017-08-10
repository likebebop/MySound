package com.example.likebebop.mysound;

import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.WorkerThread;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

import static com.example.likebebop.mysound.KaleSound.INVALID_ID;
import static com.example.likebebop.mysound.KaleSound.handler;

/**
 * Created by likebebop on 2017. 8. 9..
 *
 * SoundPool Wrapper로서 Threading과 API단순화, Asset리소스 처리등을 수행
 *
 */

public class KaleSoundPool implements SoundPool.OnLoadCompleteListener, Releasable {
    SoundPool soundPool;

    public KaleSoundPool() {
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        //-- 비동기 완료는 sound thread에서 받기 위해서
        handler.post(()->soundPool.setOnLoadCompleteListener(this));
    }

    //-- https://stackoverflow.com/questions/14255019/latch-that-can-be-incremented
    //-- phaser를 쓰려고 했으나. 안됨; ㅎㅎ
    CountDownLatch latch = new CountDownLatch(0);

    @Override
    public void onLoadComplete(SoundPool soundPool, int soundId, int status) {
        KaleLogging.CUR_LOG.debug("onLoadComplete " + soundId);
        latch.countDown();
    }

    public int load(String path) {
        int soundId = INVALID_ID;
        try {
            KaleLogging.PROFILER.tick();
            latch = new CountDownLatch(1);
            if (StickerHelper.isAsset(path)) {
                AssetFileDescriptor fd = KaleConfig.INSTANCE.context.getAssets().openFd(StickerHelper.getAssetPath(path));
                soundId = soundPool.load(fd, 1);
            } else {
                soundId = soundPool.load(path, 1);
            }
            latch.await();
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
    public int play(int soundId, boolean looping) {
        if (soundId <= INVALID_ID) {
            return 0;
        }
        final int myStreamId = ++lastMyStreamId;
        KaleSound.handler.post(()-> {
            try {
                KaleLogging.PROFILER.tick();
                StreamIdHolder h = new StreamIdHolder(soundPool.play(soundId, 1, 1, 1, looping ? -1 : 0, 1));
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

    public void unload(int soundId) {
        if (soundId <= INVALID_ID) {
            return;
        }
        KaleSound.handler.post(()-> {
            soundPool.unload(soundId);
        });
    }

    @WorkerThread
    public void release() {
        KaleSound.handler.post(()-> soundPool.release());
    }
}
