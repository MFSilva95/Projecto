package pt.it.porto.mydiabetes.ui.activities;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import pt.it.porto.mydiabetes.R;
import pt.it.porto.mydiabetes.sync.hex.HexToBytes;

public class ScanActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		// Show the Up button in the action bar.
		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		ActionBar actionBar=getSupportActionBar();
		if (actionBar != null) {
			actionBar.setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	public void scanQR(View view) {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(intent, 0);
		} else {
			openInstallLink();
		}
	}

	private void openInstallLink() {
		Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
		Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
		if (marketIntent.resolveActivity(getPackageManager()) != null) {
			startActivity(marketIntent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {

			if (resultCode == RESULT_OK) {
				Intent intent = new Intent(this, TransferActivity.class);
				String[] split = data.getStringExtra("SCAN_RESULT").split("\\|");
				if (split.length == 3) {
					intent.putExtra("host", split[0]);
					byte[] key = HexToBytes.decode(split[1]);
					byte[] iv = HexToBytes.decode(split[2]);
					intent.putExtra("key", key);
					intent.putExtra("iv", iv);
					startActivity(intent);
					finish();// avoid coming back to this one after transfers
				} else {
					Toast toast = Toast.makeText(this, getString(R.string.invalid_qr_code), Toast.LENGTH_SHORT);
					toast.show();
				}
			} else if (resultCode == RESULT_CANCELED) {
				Toast toast = Toast.makeText(this, getString(R.string.cancel_read), Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}
}
