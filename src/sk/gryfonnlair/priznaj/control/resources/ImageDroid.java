package sk.gryfonnlair.priznaj.control.resources;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.widget.ImageView;


public final class ImageDroid {

	public synchronized static void loadBitmap(final Resources res, final LruCache<String, Bitmap> lruCache,
			final int resId, final ImageView imageView) {
		{
			final String imageKey = String.valueOf(resId);

			Bitmap bitmap = getBitmapFromMemCache(lruCache, imageKey);
			if (bitmap == null) {
				bitmap = decodeSampledBitmapFromResource(res, resId);
				addBitmapToMemoryCache(lruCache, imageKey, bitmap);
			}
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	public synchronized static void loadBitmapWithReqWidth(final Resources res, final LruCache<String, Bitmap> lruCache,
			final int resId, final ImageView imageView, final int reqWidth) {
		{
			final String imageKey = String.valueOf(resId);

			Bitmap bitmap = getBitmapFromMemCache(lruCache, imageKey);
			if (bitmap == null) {
				bitmap = decodeSampledBitmapFromResource(res, resId, reqWidth);
				addBitmapToMemoryCache(lruCache, imageKey, bitmap);
			}
			if (imageView != null) {
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	/**
	 * Podla id vyberie z resource img bez options
	 * 
	 * @param res
	 * @param resId
	 * @return
	 */
	private static Bitmap decodeSampledBitmapFromResource(final Resources res, final int resId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Podla id vyberie resource ale s requested WIDTH optimalizuje
	 * 
	 * @param res
	 * @param resId
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static Bitmap decodeSampledBitmapFromResource(final Resources res, final int resId, final int reqWidth) {
		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		// Calculate ratios width to requested width
		options.inSampleSize = 1;
		final int width = options.outWidth;
		if (width > reqWidth) {
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			options.inSampleSize = widthRatio;
		}
		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	private static void addBitmapToMemoryCache(final LruCache<String, Bitmap> lruCache, final String key, final Bitmap bitmap) {
		if (getBitmapFromMemCache(lruCache, key) == null) {
			lruCache.put(key, bitmap);
		}
	}

	private static Bitmap getBitmapFromMemCache(final LruCache<String, Bitmap> lruCache, final String key) {
		return lruCache.get(key);
	}
}
