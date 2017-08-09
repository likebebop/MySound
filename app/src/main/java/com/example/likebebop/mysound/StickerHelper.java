
package com.example.likebebop.mysound;


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
//
//	//-- 아래는 매번 호출하면 느림
//	static public String externalFilesPath = FileHelper.externalFilesDir.getAbsolutePath();
//	static public String baseDir = externalFilesPath + "/" + "sticker";
//
//	//-- 빈번하게 호출하지 말것
//	static public File getStickerDir(long stickerId) {
//		return new File(baseDir + "/" + stickerId);
//	}
//
//	static public File getJsonFile(Sticker s) {
//		return new File(String.format(Locale.US, "%s/%d/%d.json", baseDir, s.stickerId, s.stickerId));
//	}
//
//	static final public String ZIP = ".zip";
//	static final public String PNG = ".png";
//	static final public String TEX = ".tex";
//	static final public String PNG_REPLACED = "";
//
//	static public String getResourcePath(Sticker s, String name) {
//		if (s.downloadType.isLocal()) {
//			StringBuilder sb = new StringBuilder();
//			if (!isAsset(name)) {
//				sb.append(KaleStickerHelper.STICKER_RESOURCE_ON_SDCARD);
//				if (!StringUtils.isEmpty(s.downloaded.resourcePath)) {
//					sb.append(s.downloaded.resourcePath);
//					sb.append('/');
//				}
//			}
//			sb.append(name);
//			return sb.toString();
//		} else {
//			if (name.endsWith(PNG)) {
//				name = name.substring(0, name.length() - PNG.length());
//			}
//			return String.format(Locale.US, "%s/%d/%s", baseDir, s.stickerId, name);
//		}
//	}
//
//	static public String getResourcePath(StickerItem item, String name) {
//		if (item.location.isLocal()) {
//			if (item.resourceId != 0) {
//				return Integer.toString(item.resourceId);
//			}
//			StringBuilder sb = new StringBuilder();
//			if (!isAsset(name)) {
//				sb.append(KaleStickerHelper.STICKER_RESOURCE_ON_SDCARD);
//				if (!StringUtils.isEmpty(item.owner.downloaded.resourcePath)) {
//					sb.append(item.owner.downloaded.resourcePath).append('/');
//				}
//				//-- text resource 테스트 용도
//				if (item.isDirResource() && item.getDrawType().isText()) {
//					sb.append(item.resourceName).append('/');
//				}
//			}
//			sb.append(name);
//			return sb.toString();
//		} else {
//			if (name.endsWith(PNG)) {
//				name = name.substring(0, name.length() - PNG.length());
//			} else if (name.endsWith(ZIP)) {
//				name = name.substring(0, name.length() - ZIP.length());
//			}
//			StringBuilder dir = new StringBuilder();
//			//-- text resource 테스트 용도
//			if (item.isDirResource() && item.getDrawType().isText()) {
//				dir.append('/').append(item.resourceName);
//			}
//			return String.format(Locale.US, "%s/%d%s/%s", baseDir, item.owner.stickerId, dir.toString(), name);
//		}
//	}
//
//	public static String getJsonFromSticker(Sticker s) {
//		try {
//			return getStringFromFile(getJsonFile(s));
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public static String convertStreamToString(InputStream is) throws IOException {
//		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//		StringBuilder sb = new StringBuilder();
//		try {
//			String line;
//			while ((line = reader.readLine()) != null) {
//				sb.append(line).append("\n");
//			}
//			return sb.toString();
//		} finally {
//			reader.close();
//			IOUtils.close(is);
//		}
//	}
//
//	public static String getStringFromFile(File file) throws IOException {
//			return convertStreamToString(new FileInputStream(file));
//	}
//
//	public static String getJsonFromPath(String path) throws IOException {
//		if (isAsset(path)) {
//			return convertStreamToString(KaleConfig.INSTANCE.context.getAssets().open(getAssetPath(path)));
//		}
//		return getStringFromFile(new File(path));
//	}
//
//
//
//	public enum RenameType {
//		REMOVE_PNG_EXT,
//		CONVERT_PNG_TO_TEX,
//		ADD_PNG_EXT,
//	}
//	//-- https://bts.linecorp.com/browse/SELFIECAM-6678
//	static public void renameImageRecursively(File dir, RenameType type, List<String> convertDirPaths) {
//		// png 를 tex 로 변경해야 하는 directory 이면 타입을 CONVERT_PNG_TO_TEX 으로 변경한다
//		if (convertDirPaths.contains(dir.getAbsolutePath())) {
//			type = RenameType.CONVERT_PNG_TO_TEX;
//		}
//
//		File[] dirArray = dir.listFiles();
//		if (dirArray == null) {
//			return;
//		}
//		List<File> files = Arrays.asList(dirArray);
//
//		for (File file : files) {
//			if (file.isDirectory()) {
//				renameImageRecursively(file, type, convertDirPaths);
//				continue;
//			}
//			if (type == RenameType.REMOVE_PNG_EXT) {
//				if (file.getName().endsWith(PNG)) {
//					String path = file.getAbsolutePath();
//					path = path.substring(0, path.length() - PNG.length());
//					file.renameTo(new File(path));
//				}
//			} else if (type == RenameType.CONVERT_PNG_TO_TEX) {
//				if (file.getName().endsWith(PNG)) {
//					String path = file.getAbsolutePath();
//					path = path.replace(PNG, TEX);
//					file.renameTo(new File(path));
//				}
//			} else {
//				String name = file.getName();
//				//-- check extension length
//				int i = name.lastIndexOf('.');
//				if (i > 0) {
//					if (name.substring(i+1).length() >= 1) {
//						continue;
//					}
//				}
//				String path = file.getAbsolutePath();
//				file.renameTo(new File(path + PNG));
//			}
//		}
//	}
//
//	static ArrayList<String> getConvertExtDirPath(Sticker sticker, RenameType type) {
//		ArrayList<String> convertDirPaths = new ArrayList<>();
//		if (type == RenameType.REMOVE_PNG_EXT) {
//			DownloadedSticker downloaded = sticker.downloaded;
//			for (StickerItem item : downloaded.items) {
//				if (item.getDrawType().needConvertingPng2Tex()) {
//					convertDirPaths.add(getResourcePath(item, item.resourceName));
//				}
//			}
//		}
//		return convertDirPaths;
//	}
//
//	public static void deleteSticker(long stickerId) {
//		FontManager.INSTANCE.deleteSticker(stickerId);
//		FileHelper.deleteFiles(StickerHelper.getStickerDir(stickerId));
//	}
//
//	public static int findBestColNumInGridLayout(int minItemAreaWidth) {
//		int screenWidth = DeviceUtils.getDisplayWidth();
//		return screenWidth / minItemAreaWidth;
//	}
}
