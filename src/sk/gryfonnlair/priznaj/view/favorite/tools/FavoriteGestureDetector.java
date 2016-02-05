package sk.gryfonnlair.priznaj.view.favorite.tools;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;


/**
 * Zabranuje dismissovat reklamu. Povoluje len onSingleTap, to znamena ze vrati
 * false a event pokracuje dalej v listeneroch. Kde dorazi na onClick.
 * 
 * http://stackoverflow.com/questions/14030779/disable-ontouch-when-using-
 * onclick
 * 
 * @author gryfonn
 * 
 */
public class FavoriteGestureDetector extends SimpleOnGestureListener {

	@Override
	public boolean onDoubleTap(final MotionEvent e) {
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(final MotionEvent e) {
		return true;
	}

	@Override
	public boolean onDown(final MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
		return true;
	}

	@Override
	public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
		return super.onScroll(e1, e2, distanceX, distanceY);
	}

	@Override
	public void onShowPress(final MotionEvent e) {
		super.onShowPress(e);
	}

	@Override
	public boolean onSingleTapConfirmed(final MotionEvent e) {
		return false;
	}

}
