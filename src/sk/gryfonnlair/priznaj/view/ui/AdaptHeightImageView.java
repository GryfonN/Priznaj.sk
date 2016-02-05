package sk.gryfonnlair.priznaj.view.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * ImageView ktoremu sa vyska setne pocas vykreslenia podla sirky, zachovav sa
 * aspekt ratio, pouzitie v priznania na reklamy a tutorial...
 * 
 * @author gryfonn
 * 
 */
public final class AdaptHeightImageView extends ImageView {

	public AdaptHeightImageView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
		final Drawable d = getDrawable();

		if (d != null) {
			// ceil not round - avoid thin vertical gaps along the left/right edges
			final int width = MeasureSpec.getSize(widthMeasureSpec);
			final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
