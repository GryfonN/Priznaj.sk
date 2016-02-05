package sk.gryfonnlair.priznaj.view.favorite.tools;

import java.util.ArrayList;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.activities.AdDroid;
import sk.gryfonnlair.priznaj.control.resources.ImageDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.FavoriteAdmission;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;


/**
 * Adapter pre listView. Viewholder v osobitne triede.
 * 
 * @author gryfonn
 * 
 */
public class FavoriteElementAdapter extends ArrayAdapter<FavoriteElement> {

	public final LayoutInflater inflater;
	public final LruCache<String, Bitmap> adMemoryCache = new LruCache<String, Bitmap>(PriznajApplication.LIST_VIEW_AD_CACHE_MEMORY_IN_KILOBYTES);
	private int adCalculatedWidth;

	public FavoriteElementAdapter(final Context context, final int textViewResourceId, final ArrayList<FavoriteElement> objects) {
		super(context, textViewResourceId, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//vypocet pozadovanej velkosti reklamy
		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = wm.getDefaultDisplay();
		if (ApiVersionUtil.hasHoneycomb2()) {
			final Point size = new Point();
			display.getSize(size);
			final int width = size.x;
			final int height = size.y;
			adCalculatedWidth = width <= height ? width : height;
		} else {
			@SuppressWarnings("deprecation")
			final int width = display.getWidth();
			@SuppressWarnings("deprecation")
			final int height = display.getHeight();
			adCalculatedWidth = width <= height ? width : height;
		}
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		final FavoriteElement element = getItem(position);
		final FavoriteElementViewHolder favoriteViewHolder;
		View viewToReturn = convertView;
		//reklama
		if (element.imageViewResource != null) {
			if (viewToReturn == null || ((FavoriteElementViewHolder) viewToReturn.getTag(R.string.activity_favorite_viewholder_tag_key_hack)).needInflate) {
				viewToReturn = inflater.inflate(R.layout.activity_favorite_element, parent, false);
				favoriteViewHolder = FavoriteElementViewHolder.createAdViewHolder(viewToReturn);
				viewToReturn.setTag(R.string.activity_favorite_viewholder_tag_key_hack, favoriteViewHolder);
			}
			else {
				favoriteViewHolder = (FavoriteElementViewHolder) viewToReturn.getTag(R.string.activity_favorite_viewholder_tag_key_hack);
			}

			favoriteViewHolder.adLabel.setText(AdDroid.getLabelForAd(element.imageViewResource));
			ImageDroid.loadBitmapWithReqWidth(favoriteViewHolder.adImageView.getResources(), adMemoryCache,
					element.imageViewResource, favoriteViewHolder.adImageView, adCalculatedWidth);
			favoriteViewHolder.adImageClickListener.resetClickListener(AdDroid.getUriForAd(element.imageViewResource));
			favoriteViewHolder.adImageView.setOnClickListener(favoriteViewHolder.adImageClickListener);
		}
		//priznanie
		else {
			if (viewToReturn == null || ((FavoriteElementViewHolder) viewToReturn.getTag(R.string.activity_favorite_viewholder_tag_key_hack)).needInflate) {
				viewToReturn = inflater.inflate(R.layout.activity_favorite_element, parent, false);
				favoriteViewHolder = FavoriteElementViewHolder.createAdmissionViewHolder(viewToReturn);
				viewToReturn.setTag(R.string.activity_favorite_viewholder_tag_key_hack, favoriteViewHolder);
			}
			else {
				favoriteViewHolder = (FavoriteElementViewHolder) viewToReturn.getTag(R.string.activity_favorite_viewholder_tag_key_hack);
			}
			favoriteViewHolder.faType.setBackgroundResource(getLabelColorResource(element.favoriteAdmission));
			favoriteViewHolder.faText.setText(Html.fromHtml(element.favoriteAdmission.text));
			favoriteViewHolder.faCategory.setText(element.favoriteAdmission.categoryText);
			favoriteViewHolder.commentButtonClickListener.resetClickListener(
					element.favoriteAdmission.originId,
					element.favoriteAdmission.admissionType == AdmissionType.GIRLS_BOYS ?
							element.favoriteAdmission.categoryText.equalsIgnoreCase("GIRLS") ? 1 : 2 : null,
					element.favoriteAdmission.admissionType);
			favoriteViewHolder.commentButton.setOnClickListener(favoriteViewHolder.commentButtonClickListener);
			favoriteViewHolder.shareButtonClickListener.resetClickListener(
					element.favoriteAdmission.originId,
					element.favoriteAdmission.admissionType == AdmissionType.GIRLS_BOYS ?
							element.favoriteAdmission.categoryText.equalsIgnoreCase("GIRLS") ? 1 : 2 : null,
					element.favoriteAdmission.admissionType);
			favoriteViewHolder.shareButton.setOnClickListener(favoriteViewHolder.shareButtonClickListener);
		}
		return viewToReturn;
	}

	/**
	 * 2 typy , reklama alebo priznanie.
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/**
	 * 0=reklama | 1=FA
	 */
	@Override
	public int getItemViewType(final int position) {
		if (getItem(position).imageViewResource != null) {
			return 0;
		}
		return 1;
	}

	/**
	 * Na zaklade typu prizania vybere resource s obrazkom pre label.
	 * 
	 * @param fa
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private int getLabelColorResource(final FavoriteAdmission fa) {
		final AdmissionType type = fa.admissionType;
		return type == AdmissionType.UNIVERSITY ? R.color.drawer_uni_normal :
				type == AdmissionType.HIGH_SCHOOL ? R.color.drawer_hs_normal :
						type == AdmissionType.GIRLS_BOYS ? "boys".equals(fa.categoryText.toLowerCase()) ? R.color.drawer_b_normal
								: R.color.drawer_g_normal : R.color.drawer_g_normal;
	}

}
