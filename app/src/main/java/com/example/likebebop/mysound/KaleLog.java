//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.example.likebebop.mysound;

import android.util.Log;

public class KaleLog {

	public void debug(Object message, Throwable t) {
		d(message, t);
	}

	public void debug(Object message) {
		d(message);
	}

	public void info(Object message) {
		i(message);
	}

	public void info(Object message, Throwable t) {
		i(message, t);
	}

	public void warn(Object message) {
		w(message);
	}

	public void warn(Object message, Throwable t) {
		w(message, t);
	}

	public void error(Object message) {
		e(message);
	}

	public static final String TAG = "kale";

	public void error(Object message, Throwable t) {
		e(message, t);
	}

	public static final KaleLog LOG = new KaleLog(TAG);

	protected final String tag;

	public KaleLog(String tag) throws NullPointerException, IllegalArgumentException {
		if (tag == null) {
			throw new NullPointerException("tag is null.");
		} else if (23 < tag.length()) {
			throw new IllegalArgumentException("tag\'s length is over 23. : " + tag);
		} else {
			this.tag = tag;
		}
	}

	public void d(Object message) {
		if (KaleConfig.INSTANCE.logging()) {
			if (message == null) {
				Log.d(this.tag, "null");
			} else if (message instanceof Throwable) {
				Throwable t = (Throwable) message;
				Log.d(this.tag, t.getMessage(), t);
			} else {
				Log.d(this.tag, message.toString());
			}
		}

	}

	public void d(Object message, Throwable t) {
		if (KaleConfig.INSTANCE.logging()) {
			Log.d(this.tag, message == null ? "null" : message.toString(), t);
		}

	}

	public void e(Object message) {
		if (KaleConfig.INSTANCE.logging()) {
			if (message == null) {
				Log.e(this.tag, "null");
			} else if (message instanceof Throwable) {
				Throwable t = (Throwable) message;
				Log.e(this.tag, t.getMessage(), t);
			} else {
				Log.e(this.tag, message.toString());
			}
		}

	}

	public void e(Object message, Throwable t) {
		if (KaleConfig.INSTANCE.logging()) {
			Log.e(this.tag, message == null ? "null" : message.toString(), t);
		}

	}

	public void i(Object message) {
		if (KaleConfig.INSTANCE.logging()) {
			if (message == null) {
				Log.i(this.tag, "null");
			} else if (message instanceof Throwable) {
				Throwable t = (Throwable) message;
				Log.i(this.tag, t.getMessage(), t);
			} else {
				Log.i(this.tag, message.toString());
			}
		}

	}

	public void i(Object message, Throwable t) {
		if (KaleConfig.INSTANCE.logging()) {
			Log.i(this.tag, message == null ? "null" : message.toString(), t);
		}

	}

	public void v(Object message) {
		if (KaleConfig.INSTANCE.logging()) {
			if (message == null) {
				Log.v(this.tag, "null");
			} else if (message instanceof Throwable) {
				Throwable t = (Throwable) message;
				Log.v(this.tag, t.getMessage(), t);
			} else {
				Log.v(this.tag, message.toString());
			}
		}

	}

	public void v(Object message, Throwable t) {
		if (KaleConfig.INSTANCE.logging()) {
			Log.v(this.tag, message == null ? "null" : message.toString(), t);
		}

	}

	public void w(Object message) {
		if (KaleConfig.INSTANCE.logging()) {
			if (message == null) {
				Log.w(this.tag, "null");
			} else if (message instanceof Throwable) {
				Throwable t = (Throwable) message;
				Log.w(this.tag, t.getMessage(), t);
			} else {
				Log.w(this.tag, message.toString());
			}
		}

	}

	public void w(Object message, Throwable t) {
		if (KaleConfig.INSTANCE.logging()) {
			Log.w(this.tag, message == null ? "null" : message.toString(), t);
		}

	}

	public void warn(Throwable e) {
		w(e);
	}

}
