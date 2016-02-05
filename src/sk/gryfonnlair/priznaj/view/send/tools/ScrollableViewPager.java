package sk.gryfonnlair.priznaj.view.send.tools;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;


/**
 * Vytvoril som si vlastny ViewPager aby som si vedel setovat povolenie
 * posuvania pageov pomocou property<br>
 * <p>
 * V layoute pouzit v tagu
 * sk.gryfonnlair.priznaj.view.send.tools.ScrollableViewPager
 * 
 * @author gryfonn
 * 
 */
public class ScrollableViewPager extends ViewPager {

	private boolean isScrollEnabled;

	public ScrollableViewPager(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setScrollEnabled(true);
	}

	@Override
	public boolean onTouchEvent(final MotionEvent event) {
		if (isScrollEnabled()) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public void fakeDragBy(final float xOffset) {
		if (isScrollEnabled()) {
			super.fakeDragBy(xOffset);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent event) {
		if (isScrollEnabled()) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

	/**
	 * Getter pre proterty ktora urcuje ci sa moze ViewPager posuvat alebo nie
	 * 
	 * @return
	 */
	public boolean isScrollEnabled() {
		return isScrollEnabled;
	}

	/**
	 * Setter pre property ktora urcuje ci sa moze ViewPager posuvat alebo nie
	 * 
	 * @param isScrollEnabled
	 */
	public void setScrollEnabled(final boolean isScrollEnabled) {
		this.isScrollEnabled = isScrollEnabled;
	}

}
