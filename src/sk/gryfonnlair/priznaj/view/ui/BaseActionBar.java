package sk.gryfonnlair.priznaj.view.ui;

import sk.gryfonnlair.priznaj.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public abstract class BaseActionBar extends Activity {

	private final int menuResource;

	public BaseActionBar(final int menuResource) {
		this.menuResource = menuResource;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		super.onCreateOptionsMenu(menu);
		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(menuResource, menu);
		return true;
	}

	protected boolean onMenuItemSelected(final MenuItem item) {

		switch (item.getItemId()) {
			case android.R.id.home:
				return true;
			case R.id.action_favorite:
				return true;
			default:
				break;
		}
//		if (item.getItemId() == android.R.id.home) {
//			final Intent intent = new Intent(this, MainActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			return true;
//		}
//
//		if (item.getItemId() == R.id.action_favorite) {
//			return true;
//		}
//
//		if (item.getItemId() == R.id.action_find) {
//			return true;
//		}

		return false;
	}

//	private void refresh() {
//		finish();
//		startActivity(getIntent());
//	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {

		final boolean itemSelected = onMenuItemSelected(item);
		return itemSelected ? true : super.onOptionsItemSelected(item);
//		if (itemSelected == true) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
	}

}
