package com.example.likebebop.mysound;

import android.media.MediaPlayer;
import android.net.Uri;


public class SafeMediaPlayer implements MediaPlayer.OnCompletionListener {
    static public KaleLog LOG = KaleLogging.CUR_LOG;
    private boolean looping;
    private MediaPlayer player;
    private Uri uri;

    enum Status {
        PLAYING,
        STOP,
        IDLE;

        public boolean isPlaying() {
            return this == PLAYING;
        }

        public boolean isStopped() {
            return this == STOP;
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
            player.setDataSource(KaleConfig.INSTANCE.context, uri);
            player.prepare();
            player.setOnCompletionListener(this);
        } catch (Exception e) {
            LOG.warn(e);
        } finally {
            p.tockWithDebug("reload");
        }
    }

    public void SafeMediaPlayer(Uri uri, boolean looping) {
        HandyProfiler p = new HandyProfiler(LOG);
        try {
            release();
            if (uri == null) {
                return;
            }
            this.uri = uri;
            this.looping = looping;
            player = MediaPlayer.create(KaleConfig.INSTANCE.context, uri);
            if (looping) {
                player.setOnCompletionListener(this);
            }
        } catch (Exception e) {
            LOG.warn(e);
            player = null;
        } finally {
            p.tockWithDebug("build");
        }
    }

    public void play() {
        if (player == null) {
            return;
        }
        //-- 진짜 player가 playing 중이 아닌 경우에는 start 해야 한다.
        if (player.isPlaying() && isPlaying()) {
            return;
        }
        try {
            LOG.debug("(+) start " + uri);
            player.start();
            status = Status.PLAYING;
        } catch (Exception e) {
            LOG.warn(e);
        }
    }

    public boolean isPlaying() {
        return status.isPlaying();
    }

    public void stop() {
        if (!status.isPlaying()) {
            return;
        }
        LOG.debug("(-) stop " + uri);
        try {
            player.pause();
            player.seekTo(0);
        } catch (Exception e) {
            LOG.warn(e);
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
        }
    }

    public void reset() {
        status = Status.IDLE;
    }
}