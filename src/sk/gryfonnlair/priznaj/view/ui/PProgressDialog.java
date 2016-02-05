package sk.gryfonnlair.priznaj.view.ui;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


/**
 * PRIZNAJ Progress dialog box v ktorom sa toci Pcko
 * 
 * @author gryfonn
 * 
 */
public class PProgressDialog extends ProgressDialog {

	Context mContext;
	private View image;
	private Animation rotate;

	public PProgressDialog(final Context context) {
		super(context, R.style.PProgressDialog);
		mContext = context;
		setCancelable(false);
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final TextView v = (TextView) findViewById(R.id.title);
		FontUtil.setOswaldRegularFont(v);
		setContentView(R.layout.p_progress_dialog);
		image = findViewById(R.id.p_progress_dialog_image);
		rotate = AnimationUtils.loadAnimation(mContext, R.anim.p_progress_dialog_rotate);
	}

	@Override
	public void show() {
		super.show();
		image.startAnimation(rotate);
	}

	@Override
	public void cancel() {
		super.cancel();
		if (image != null) {
			image.clearAnimation();
		}
	}
}
