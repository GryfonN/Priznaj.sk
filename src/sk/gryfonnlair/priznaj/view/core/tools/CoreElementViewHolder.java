package sk.gryfonnlair.priznaj.view.core.tools;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.view.ui.LVAdImageClickListener;
import sk.gryfonnlair.priznaj.view.ui.LVCommentButtonClickListener;
import sk.gryfonnlair.priznaj.view.ui.LVFavoriteButtonClickListener;
import sk.gryfonnlair.priznaj.view.ui.LVShareButtonClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * ViewHolder objekt pre SearchElementAdapter a CoreElementAdapter, pracujem s
 * CoreElementom kedze je
 * rovnaky pre core aj search
 * 
 * @author gryfonn
 * 
 */
public class CoreElementViewHolder {

	public LinearLayout coreLayout;
	//prvky reklamy
	public LinearLayout adLayout;
	public TextView adLabel;
	public ImageView adImageView;
	public LVAdImageClickListener adImageClickListener;
	//prvky favorite admission
	public LinearLayout admissionLayout;
	public View admissionTypeHeader;
	public TextView admissionText;
	public TextView admissionCategory;
	public Button commentButton;
	public LVCommentButtonClickListener commentButtonClickListener;
	public Button shareButton;
	public LVShareButtonClickListener shareButtonClickListener;
	public ToggleButton favoriteButton;
	public LVFavoriteButtonClickListener favoriteButtonClickListener;

	/**
	 * Konstruktore reklamy
	 * 
	 * @param adLayout VISIBLE
	 * @param adLabel
	 * @param adImageView
	 * @param admissionLayout GONE
	 */
	public CoreElementViewHolder(final LinearLayout adLayout, final TextView adLabel,
			final ImageView adImageView, final LinearLayout admissionLayout) {
		this.admissionLayout = admissionLayout;
		this.admissionLayout.setVisibility(View.GONE);
		this.adLayout = adLayout;
		this.adLayout.setVisibility(View.VISIBLE);
		this.adLabel = adLabel;
		this.adImageView = adImageView;
		adImageClickListener = new LVAdImageClickListener();
		FontUtil.setOswaldRegularFont(this.adLabel);
	}

	/**
	 * konstruktor priznania
	 * 
	 * @param admissionLayout VISIBLE
	 * @param admissionText
	 * @param admissionCategory
	 * @param adLayout GONE
	 */
	public CoreElementViewHolder(final LinearLayout coreLayout, final LinearLayout admissionLayout,
			final View admissionTypeHeader, final TextView admissionText, final TextView admissionCategory,
			final Button commentButton, final Button shareButton, final ToggleButton favoriteButton,
			final LinearLayout adLayout) {
		this.coreLayout = coreLayout;
		this.adLayout = adLayout;
		this.adLayout.setVisibility(View.GONE);
		this.admissionLayout = admissionLayout;
		this.admissionLayout.setVisibility(View.VISIBLE);
		this.admissionTypeHeader = admissionTypeHeader;
		this.admissionText = admissionText;
		this.admissionCategory = admissionCategory;
		this.commentButton = commentButton;
		commentButtonClickListener = new LVCommentButtonClickListener();
		this.shareButton = shareButton;
		shareButtonClickListener = new LVShareButtonClickListener();
		this.favoriteButton = favoriteButton;
		favoriteButtonClickListener = new LVFavoriteButtonClickListener();
		FontUtil.setOswaldRegularFont(this.admissionCategory, this.favoriteButton);
	}

	/**
	 * Vytiahne z viewu co potrebuje pre viewholder reklamy
	 * 
	 * @param view kt dostanem z inflatera a chcem z neho tahat
	 * @return {@link CoreElementViewHolder} pre reklamu
	 */
	public static CoreElementViewHolder createAdViewHoder(final View view) {
		return new CoreElementViewHolder(
				(LinearLayout) view.findViewById(R.id.activity_core_element_ad_layout),
				(TextView) view.findViewById(R.id.activity_core_element_ad_label),
				(ImageView) view.findViewById(R.id.activity_core_element_ad_image),
				(LinearLayout) view.findViewById(R.id.activity_core_element_admission_layout));
	}

	/**
	 * Vytiahne z viewu co potrebuje pre viewholder priznania
	 * 
	 * @param view kt dostanem z inflatera a chcem z neho tahat
	 * @return {@link CoreElementViewHolder} pre priznanie
	 */
	public static CoreElementViewHolder createAdmissionViewHolder(final View view) {
		return new CoreElementViewHolder(
				(LinearLayout) view.findViewById(R.id.activity_core_element_layout),
				(LinearLayout) view.findViewById(R.id.activity_core_element_admission_layout),
				view.findViewById(R.id.activity_core_element_admission_type),
				(TextView) view.findViewById(R.id.activity_core_element_admission_text),
				(TextView) view.findViewById(R.id.activity_core_element_admission_category),
				(Button) view.findViewById(R.id.activity_core_element_admission_button_comment),
				(Button) view.findViewById(R.id.activity_core_element_admission_button_share),
				(ToggleButton) view.findViewById(R.id.activity_core_element_admission_button_favorite),
				(LinearLayout) view.findViewById(R.id.activity_core_element_ad_layout));
	}
}
