
package com.example.likebebop.mysound;


import android.net.Uri;

import java.io.File;

/**
 * Created by likebebop on 2016-01-20.
 */
public class StickerHelper {
	public static final String ASSET_PREFIX = "asset://";

	public static boolean isAsset(String path) {
		if (StringUtils.isEmpty(path)) {
			return false;
		}
		return path.startsWith(StickerHelper.ASSET_PREFIX);
	}

	public static String getAssetPath(String path) {
		if (StringUtils.isEmpty(path)) {
			return null;
		}
		return path.substring(ASSET_PREFIX.length());
	}

	public static Uri getUriFromPath(String path) {
		if (isAsset(path)) {
			return Uri.parse("file://android_asset/" + getAssetPath(path));
		}
		return Uri.fromFile(new File(path));
	}
}
