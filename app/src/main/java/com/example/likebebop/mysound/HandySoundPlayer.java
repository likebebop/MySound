package com.example.likebebop.mysound;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.File;


public class HandySoundPlayer implements MediaPlayer.OnCompletionListener, Releasable {

    static public HandySoundPlayer NULL = new HandySoundPlayer();

    static public KaleLog LOG = KaleLogging.CUR_LOG;
    private MediaPlayer player;
    private String path;

    enum Status {
        PLAYING,
        STOP,
        IDLE;

        public boolean isPlaying() {
            return this == PLAYING;
        }
    }

    Status status = Status.IDLE;

    //-- sound가 repeat 길이 보다 먼저 끝나는 경우
    //-- https://bts.linecorp.com/browse/SELFIECAM-7289
    //-- looping을 위해서
    @Override
    public void onCompletion(MediaPlayer mp) {
        reload();
        reset();
    }

    private void reload() {
        HandyProfiler p = new HandyProfiler(LOG);
        try {
            player.reset();
            if (StickerHelper.isAsset(path)) {
                AssetFileDescriptor descriptor = KaleConfig.INSTANCE.context.getAssets().openFd(StickerHelper.getAssetPath(path));
                player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
                descriptor.close();
            } else {
                player.setDataSource(KaleConfig.INSTANCE.context, Uri.fromFile(new File(path)));
            }
            player.prepare();
            player.setOnCompletionListener(this);
        } catch (Exception e) {
            LOG.warn(e);
        } finally {
            p.tockWithDebug("reload");
        }
    }

    public HandySoundPlayer() {
    }

    public HandySoundPlayer(String path) {
        HandyProfiler p = new HandyProfiler(LOG);
        try {
            this.path = path;
            player = new MediaPlayer();
            reload();
        } catch (Exception e) {
            LOG.warn(e);
            player = null;
        } finally {
            p.tockWithDebug("HandySoundPlayer.build " + path);
        }
    }

    public void play(boolean looping) {
        if (player == null) {
            return;
        }
        stop();
        player.setLooping(looping);
        //-- 진짜 player가 playing 중이 아닌 경우에는 start 해야 한다.
        if (player.isPlaying() && isPlaying()) {
            return;
        }
        try {
            KaleLogging.PROFILER.tick();
            player.start();
            status = Status.PLAYING;
        } catch (Exception e) {
            LOG.warn(e);
        } finally {
            KaleLogging.PROFILER.tockWithDebug("(+) start " + path);
        }
    }

    public boolean isPlaying() {
        return status.isPlaying();
    }

    public void stop() {
        if (!status.isPlaying()) {
            return;
        }
        try {
            KaleLogging.PROFILER.tick();
            player.pause();
            player.seekTo(0);
        } catch (Exception e) {
            LOG.warn(e);
        } finally {
            KaleLogging.PROFILER.tockWithDebug("(-) stop " + path);
        }
        status = Status.STOP;
    }

    public void release() {
        if (player == null) {
            return;
        }
        try {
            stop();
            player.release();
        } catch (Exception e) {
            LOG.warn(e);
        } finally {
            player = null;
            reset();
            KaleLogging.CUR_LOG.debug("release " + path);
        }
    }

    public void reset() {
        status = Status.IDLE;
    }
}