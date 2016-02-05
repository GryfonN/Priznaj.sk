package sk.gryfonnlair.priznaj.view.search.tools;

import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.activities.AdDroid;
import sk.gryfonnlair.priznaj.control.resources.ImageDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElement;
import sk.gryfonnlair.priznaj.view.core.tools.CoreElementViewHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;


/**
 * Adapter pre listView v search aktivite
 * 
 * @author gryfonn
 * 
 */
public class SearchElementAdapter extends ArrayAdapter<CoreElement> {
	/**
	 * slovo kt sa vyznaci v texte priznania
	 */
	private String highlightWord = null;

	public final LayoutInflater inflater;
	public final LruCache<String, Bitmap> adMemoryCache = new LruCache<String, Bitmap>(PriznajApplication.LIST_VIEW_AD_CACHE_MEMORY_IN_KILOBYTES);

	private int adCalculatedWidth;

	public SearchElementAdapter(final Context context, final int textViewResourceId, final List<CoreElement> objects) {
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
		final CoreElement element = getItem(position);
		final CoreElementViewHolder coreViewHolder;
		View viewToReturn = convertView;
		//reklama
		if (element.imageViewResource != null) {
			if (viewToReturn == null) {
				viewToReturn = inflater.inflate(R.layout.activity_core_element, parent, false);
				coreViewHolder = CoreElementViewHolder.createAdViewHoder(viewToReturn);
				viewToReturn.setTag(coreViewHolder);
			}
			else {
				coreViewHolder = (CoreElementViewHolder) viewToReturn.getTag();
			}
			coreViewHolder.adLabel.setText(AdDroid.getLabelForAd(element.imageViewResource));
			ImageDroid.loadBitmapWithReqWidth(coreViewHolder.adImageView.getResources(), adMemoryCache,
					element.imageViewResource, coreViewHolder.adImageView, adCalculatedWidth);
			coreViewHolder.adImageClickListener.resetClickListener(AdDroid.getUriForAd(element.imageViewResource));
			coreViewHolder.adImageView.setOnClickListener(coreViewHolder.adImageClickListener);
		}
		//priznanie
		else {
			if (viewToReturn == null) {
				viewToReturn = inflater.inflate(R.layout.activity_core_element, parent, false);
				coreViewHolder = CoreElementViewHolder.createAdmissionViewHolder(viewToReturn);
				viewToReturn.setTag(coreViewHolder);
			}
			else {
				coreViewHolder = (CoreElementViewHolder) viewToReturn.getTag();
			}
			if (highlightWord != null) {
				final Spannable wordtoSpan = new SpannableString(Html.fromHtml(element.coreAdmission.text).toString());
				final int searchPosition = element.coreAdmission.text.toUpperCase().indexOf(highlightWord.toUpperCase());
				if (searchPosition >= 0) {
					wordtoSpan.setSpan(new ForegroundColorSpan(Color.RED),
							searchPosition, searchPosition + highlightWord.length(),
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				coreViewHolder.admissionText.setText(wordtoSpan);
			} else {
				coreViewHolder.admissionText.setText(Html.fromHtml(element.coreAdmission.text));
			}
			coreViewHolder.admissionCategory.setText(element.coreAdmission.category);
			coreViewHolder.favoriteButton.setChecked(element.coreAdmission.favorite);
			coreViewHolder.admissionTypeHeader.setBackgroundResource(getLabelColorResource(element.coreAdmission));
			coreViewHolder.commentButtonClickListener.resetClickListener(
					element.coreAdmission.id,
					element.coreAdmission.type == AdmissionType.GIRLS_BOYS ? element.coreAdmission.category.equalsIgnoreCase("GIRLS") ? 1 : 2 : null,
					element.coreAdmission.type);
			coreViewHolder.commentButton.setOnClickListener(coreViewHolder.commentButtonClickListener);
			coreViewHolder.shareButtonClickListener.resetClickListener(
					element.coreAdmission.id,
					element.coreAdmission.type == AdmissionType.GIRLS_BOYS ? element.coreAdmission.category.equalsIgnoreCase("GIRLS") ? 1 : 2 : null,
					element.coreAdmission.type);
			coreViewHolder.shareButton.setOnClickListener(coreViewHolder.shareButtonClickListener);
			coreViewHolder.favoriteButtonClickListener.resetClickListener(element.coreAdmission);
			coreViewHolder.favoriteButton.setOnClickListener(coreViewHolder.favoriteButtonClickListener);
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
	 * 0=reklama | 1=SA
	 */
	@Override
	public int getItemViewType(final int position) {
		if (getItem(position).imageViewResource != null) {
			return 0;
		}
		return 1;
	}

	/**
	 * Nasetuje adapteru slovo ktore ma zvyraznit pri vytvarani getView pre
	 * priznania
	 * 
	 * @param highlightWord
	 */
	public void setHighlightWord(final String highlightWord) {
		this.highlightWord = highlightWord;
	}

	/**
	 * Na zaklade typu prizania vybere resource s obrazkom pre label.
	 * 
	 * @param coreAdmission
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private int getLabelColorResource(final CoreAdmission coreAdmission) {
		final AdmissionType type = coreAdmission.type;
		return type == AdmissionType.UNIVERSITY ? R.color.drawer_uni_normal :
				type == AdmissionType.HIGH_SCHOOL ? R.color.drawer_hs_normal :
						type == AdmissionType.GIRLS_BOYS ? "boys".equals(coreAdmission.category.toLowerCase()) ? R.color.drawer_b_normal
								: R.color.drawer_g_normal : R.color.drawer_g_normal;
	}
}
