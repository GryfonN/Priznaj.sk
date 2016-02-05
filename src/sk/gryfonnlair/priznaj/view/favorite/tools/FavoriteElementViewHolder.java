package sk.gryfonnlair.priznaj.view.favorite.tools;

import sk.gryfonnlair.priznaj.PriznajApplication;
import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.view.ui.LVAdImageClickListener;
import sk.gryfonnlair.priznaj.view.ui.LVCommentButtonClickListener;
import sk.gryfonnlair.priznaj.view.ui.LVShareButtonClickListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * ViewHolder objekt pre FavoriteElementAdapter<br>
 * http://logc.at/2011/10/10/handling-listviews-with-multiple-row-types/
 * 
 * @author gryfonn
 * 
 */
class FavoriteElementViewHolder {

	boolean needInflate = false;
	//prvky reklamy
	LinearLayout adLayout;
	TextView adLabel;
	ImageView adImageView;
	LVAdImageClickListener adImageClickListener;
	//prvky favorite admission
	LinearLayout faLayout;
	View faType;
	TextView faText;
	TextView faCategory;
	Button commentButton;
	LVCommentButtonClickListener commentButtonClickListener;
	Button shareButton;
	LVShareButtonClickListener shareButtonClickListener;

	/**
	 * Konstruktor reklamy
	 * 
	 * @param adLayout VISIBLE
	 * @param adImageView
	 * @param faLayout GONE
	 */
	public FavoriteElementViewHolder(final LinearLayout adLayout, final TextView adLabel, final ImageView adImageView,
			final LinearLayout faLayout) {
		this.faLayout = faLayout;
		this.faLayout.setVisibility(View.GONE);
		this.adLayout = adLayout;
		this.adLayout.setVisibility(View.VISIBLE);
		//potlacam onTouch v SwipeDismissliste
		this.adLayout.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(final View v, final MotionEvent event) {
				return new GestureDetector(PriznajApplication.getContext(), new FavoriteGestureDetector()).onTouchEvent(event);
			}
		});
		this.adLabel = adLabel;
		this.adImageView = adImageView;
		adImageClickListener = new LVAdImageClickListener();
		FontUtil.setOswaldRegularFont(this.adLabel);
	}

	/**
	 * Konstruktor priznania
	 * 
	 * @param faLayout VISIBLE
	 * @param faType
	 * @param faText
	 * @param faCategory
	 * @param adLayout GONE
	 */
	public FavoriteElementViewHolder(final LinearLayout faLayout, final View faType, final TextView faText,
			final TextView faCategory, final LinearLayout adLayout,
			final Button commentButton, final Button shareButton) {
		this.adLayout = adLayout;
		this.adLayout.setVisibility(View.GONE);
		this.faLayout = faLayout;
		this.faLayout.setVisibility(View.VISIBLE);
		this.faType = faType;
		this.faText = faText;
		this.faCategory = faCategory;
		this.commentButton = commentButton;
		commentButtonClickListener = new LVCommentButtonClickListener();
		this.shareButton = shareButton;
		shareButtonClickListener = new LVShareButtonClickListener();
		FontUtil.setOswaldRegularFont(this.faCategory);
	}

	/**
	 * Vytiahne z viewu co potrebuje pre reklamny view holder
	 * 
	 * @param view kt dostanem z inflatera a chcem z neho tahat
	 * @return FavoriteViewHolder pre reklamu nasetovany
	 */
	static FavoriteElementViewHolder createAdViewHolder(final View view) {
		return new FavoriteElementViewHolder(
				(LinearLayout) view.findViewById(R.id.activity_favorite_element_ad_layout),
				(TextView) view.findViewById(R.id.activity_favorite_element_ad_label),
				(ImageView) view.findViewById(R.id.activity_favorite_element_ad_image),
				(LinearLayout) view.findViewById(R.id.activity_favorite_element_fa_layout));
	}

	/**
	 * Vytiahne z viewu co potrebuje pre priznanie view holder
	 * 
	 * @param view kt dostanem z inflatera a chcem z neho tahat
	 * @return FavoriteViewHolder pre priznanie nasetovany
	 */
	static FavoriteElementViewHolder createAdmissionViewHolder(final View view) {
		return new FavoriteElementViewHolder(
				(LinearLayout) view.findViewById(R.id.activity_favorite_element_fa_layout),
				view.findViewById(R.id.activity_favorite_element_fa_type),
				(TextView) view.findViewById(R.id.activity_favorite_element_fa_text),
				(TextView) view.findViewById(R.id.activity_favorite_element_fa_category),
				(LinearLayout) view.findViewById(R.id.activity_favorite_element_ad_layout),
				(Button) view.findViewById(R.id.activity_favorite_element_fa_button_comment),
				(Button) view.findViewById(R.id.activity_favorite_element_fa_button_share));
	}
}
