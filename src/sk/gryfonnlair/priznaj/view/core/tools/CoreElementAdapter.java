package sk.gryfonnlair.priznaj.view.core.tools;

import java.util.List;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.activities.AdDroid;
import sk.gryfonnlair.priznaj.control.resources.ImageDroid;
import sk.gryfonnlair.priznaj.control.rest.RestDroid;
import sk.gryfonnlair.priznaj.control.util.ApiVersionUtil;
import sk.gryfonnlair.priznaj.model.specific.AdmissionType;
import sk.gryfonnlair.priznaj.model.specific.CoreAdmission;
import sk.gryfonnlair.priznaj.view.core.NavigationDrawerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.preference.PreferenceManager;
import android.support.v4.util.LruCache;
import android.text.Html;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;


/**
 * Adapter pre main listview
 * 
 * @author gryfonn
 * 
 */
public class CoreElementAdapter extends ArrayAdapter<CoreElement> {

	public final LayoutInflater inflater;
	public LruCache<String, Bitmap> adMemoryCache = new LruCache<String, Bitmap>(PriznajApplication.LIST_VIEW_AD_CACHE_MEMORY_IN_KILOBYTES);
	private final int highLightCount;

	private AdView adMobViewPriznaj;
	private AdView adMobViewGryfonNLair;
	/**
	 * sirka reklamy, ak port tak match, ak land tak vyska obrazovky
	 */
	private final int adCalculatedWidth;

	public CoreElementAdapter(final AdmissionType admissionsType, final Integer drawerChildId,
			final Context context, final int textViewResourceId, final List<CoreElement> objects) {
		super(context, textViewResourceId, objects);
		//podvsvietenie novych
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		if (drawerChildId == null) {
			highLightCount = NavigationDrawerAdapter.computeGroupCounts(prefs.getAll(), admissionsType == AdmissionType.UNIVERSITY ? 0 : 1);
		} else {
			if (admissionsType == AdmissionType.GIRLS_BOYS) {
				highLightCount = NavigationDrawerAdapter.computeGroupCounts(prefs.getAll(), drawerChildId == 1 ? 2 : 3);
			} else {
				highLightCount =
						NavigationDrawerAdapter.computeChildCounts(prefs.getAll(), admissionsType == AdmissionType.UNIVERSITY ? 0 : 1, drawerChildId - 1);
			}
		}
		//net check a AdMob reklama
		if (RestDroid.networkAvailable) {
			//TODO RELEASE ADMOB test request debug info prec
			final AdRequest adRequest = new AdRequest();
//			adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
//			adRequest.addTestDevice("4df193d17c308fd3");
			//Pre priznaj.sk
			adMobViewPriznaj = new AdView(AdMobHackActivity.AdMobMemoryLeakWorkAroundActivity, AdSize.BANNER, "ca-app-pub-4405733015765402/2889308423");
			adMobViewPriznaj.loadAd(adRequest);
			//GryfonN Lair
			adMobViewGryfonNLair = new AdView(AdMobHackActivity.AdMobMemoryLeakWorkAroundActivity, AdSize.BANNER, "ca-app-pub-7847962924409362/6415261134");
			adMobViewGryfonNLair.loadAd(adRequest);
		}
		//vypocet pozadovanej velkosti reklamy
		final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		final Display display = wm.getDefaultDisplay();
		if (ApiVersionUtil.hasHoneycomb2()) {
			final Point size = new Point();
			display.getSize(size);
			adCalculatedWidth = size.x;
		} else {
			adCalculatedWidth = display.getWidth();
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

			//ak mam vytvorene reklamy podla netu tak riesim az potom moznost reklamy inak staticka
			if (adMobViewPriznaj != null && adMobViewGryfonNLair != null) {
				//odstranim view, resp odtranim parent ktoreho uz ma po predoslo prideleni
				final ViewGroup parentForPriznaj = adMobViewPriznaj.getParent() instanceof ViewGroup ? ((ViewGroup) adMobViewPriznaj.getParent()) : null;
				if (parentForPriznaj != null) {
					parentForPriznaj.removeView(adMobViewPriznaj);
				}
				final ViewGroup parentForGL = adMobViewGryfonNLair.getParent() instanceof ViewGroup ? ((ViewGroup) adMobViewGryfonNLair.getParent()) : null;
				if (parentForGL != null) {
					parentForGL.removeView(adMobViewGryfonNLair);
				}

				//mam tri reklamy Priznaj, Moju, staticke takze delim tromi a default je tretia moznost a tou budu staticke
				switch (position % 3) {
					case 0:
						//davam Priznaj
						coreViewHolder.adLabel.setVisibility(View.GONE);
						coreViewHolder.adImageView.setVisibility(View.GONE);
						coreViewHolder.adLayout.addView(adMobViewPriznaj);
						break;
					case 1:
						//davam GryfonN Lair
						coreViewHolder.adLabel.setVisibility(View.GONE);
						coreViewHolder.adImageView.setVisibility(View.GONE);
						coreViewHolder.adLayout.addView(adMobViewGryfonNLair);
						break;
					default:
						//davam staticku
						coreViewHolder.adLabel.setVisibility(View.VISIBLE);
						coreViewHolder.adImageView.setVisibility(View.VISIBLE);
						coreViewHolder.adLabel.setText(AdDroid.getLabelForAd(element.imageViewResource));
						ImageDroid.loadBitmapWithReqWidth(coreViewHolder.adImageView.getResources(), adMemoryCache,
								element.imageViewResource, coreViewHolder.adImageView, adCalculatedWidth);
						coreViewHolder.adImageClickListener.resetClickListener(AdDroid.getUriForAd(element.imageViewResource));
						coreViewHolder.adImageView.setOnClickListener(coreViewHolder.adImageClickListener);
						break;
				}
			}
			else {
				coreViewHolder.adLabel.setText(AdDroid.getLabelForAd(element.imageViewResource));
				ImageDroid.loadBitmapWithReqWidth(coreViewHolder.adImageView.getResources(), adMemoryCache,
						element.imageViewResource, coreViewHolder.adImageView, adCalculatedWidth);
				coreViewHolder.adImageClickListener.resetClickListener(AdDroid.getUriForAd(element.imageViewResource));
				coreViewHolder.adImageView.setOnClickListener(coreViewHolder.adImageClickListener);
			}
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

			if (position < highLightCount) {
				coreViewHolder.coreLayout.setBackgroundColor(PriznajApplication.getContext().getResources().getColor(R.color.new_admission_bg));
			} else {
				coreViewHolder.coreLayout.setBackgroundColor(PriznajApplication.getContext().getResources().getColor(R.color.listview_background_gray));
			}
			coreViewHolder.admissionText.setText(Html.fromHtml(element.coreAdmission.text).toString());
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

	public void clearAdViews() {
		if (adMobViewPriznaj != null) {
			adMobViewPriznaj.stopLoading();
			adMobViewPriznaj.destroy();
			adMobViewPriznaj = null;
		}
		if (adMobViewGryfonNLair != null) {
			adMobViewGryfonNLair.stopLoading();
			adMobViewGryfonNLair.destroy();
			adMobViewGryfonNLair = null;
		}
	}

	/**
	 * 2 typy , reklama alebo priznanie.
	 */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	/**
	 * 0=reklama | 1= admission
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
