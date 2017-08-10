package com.example.likebebop.mysound;

import android.support.annotation.WorkerThread;

import com.annimon.stream.Stream;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

/**
 * Created by likebebop on 2017. 8. 10..
 */

public class KaleMediaSound implements Releasable {

    HashMap<Integer, HandySoundPlayer> playerMap = new HashMap<>();

    int lastMySoundId = 0;

    public int load(String path) {
        final int soundId = ++lastMySoundId;
        CountDownLatch latch = new CountDownLatch(1);
        KaleLogging.PROFILER.tick();
        try {
            KaleSound.handler.post(()->{
                //-- onCompeletion callback 때문에 handler thread에서 call해준다
                try {
                    HandySoundPlayer player = new HandySoundPlayer(path);
                    playerMap.put(soundId, player);
                } catch (Exception e) {
                    KaleLogging.CUR_LOG.warn(e);
                } finally {
                    latch.countDown();
                }
            });
            latch.await();
        } catch (Exception e) {
            KaleLogging.CUR_LOG.warn(e);
        } finally {
            return soundId;
        }
    }

    @WorkerThread
    public void play(int soundId, boolean looping) {
        KaleSound.handler.post(()->{
            getPlayer(soundId).play(looping);
        });
    }

    @WorkerThread
    public void stop(int soundId) {
        KaleSound.handler.post(()->{
            getPlayer(soundId).stop();
        });
    }

    @WorkerThread
    public void unload(int soundId) {
        KaleSound.handler.post(()->{
            getPlayer(soundId).release();
        });
    }

    private HandySoundPlayer getPlayer(int soundId) {
        HandySoundPlayer p = playerMap.get(soundId);
        if (p == null) {
            return HandySoundPlayer.NULL;
        }
        return p;
    }

    @WorkerThread
    @Override
    public void release() {
        KaleSound.handler.post(()->{
            Stream.of(playerMap.values()).forEach(HandySoundPlayer::release);
            playerMap.clear();
        });
    }
}
