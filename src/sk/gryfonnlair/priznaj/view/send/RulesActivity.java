package sk.gryfonnlair.priznaj.view.send;

import sk.gryfonnlair.priznaj.R;
import sk.gryfonnlair.priznaj.control.util.FontUtil;
import sk.gryfonnlair.priznaj.control.util.ScreenUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;


/**
 * Aktivita s pravidlami
 * 
 * @author gryfonn
 * 
 */
public class RulesActivity extends ActionBarActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ScreenUtil.setScreenOrientation(this);
		setContentView(R.layout.activity_send_rules);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		FontUtil.setOswaldRegularFont(
				(TextView) findViewById(R.id.activity_send_rules_label1),
				(TextView) findViewById(R.id.activity_send_rules_label2),
				(TextView) findViewById(R.id.activity_send_rules_rule1),
				(TextView) findViewById(R.id.activity_send_rules_rule2),
				(TextView) findViewById(R.id.activity_send_rules_rule3),
				(TextView) findViewById(R.id.activity_send_rules_rule4),
				(TextView) findViewById(R.id.activity_send_rules_rule5),
				(TextView) findViewById(R.id.activity_send_rules_rule6),
				(TextView) findViewById(R.id.activity_send_rules_rule7),
				(TextView) findViewById(R.id.activity_send_rules_rule8),
				(TextView) findViewById(R.id.activity_send_rules_rule9)
				);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				break;

			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
}
