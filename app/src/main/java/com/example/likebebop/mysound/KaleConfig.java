package com.example.likebebop.mysound;

import android.content.Context;

/**
 * Created by likebebop on 2016-05-27.
 */
public enum KaleConfig {
    INSTANCE;

    public Context context;
    private boolean logging = true;

    public boolean profileMode = false;
    public boolean useExternalOnly = false;
    public String networkCacheDir = "net";

    public static boolean logging() {
        return INSTANCE.logging;
    }

    public void init(Context context) {
        this.context = context;
        build();
    }
    //-- 빌드 설정에 따라서 설정 재조정
    private void build() {
    }

}
