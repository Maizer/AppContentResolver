package com.maizer.content.test;

import com.maizer.appcontent.AppContentObserver;
import com.maizer.appcontent.AppContentResolver;
import com.maizer.appcontent.R;
import com.maizer.example.AppContentProvider;

import android.app.Activity;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String TAG = MainActivity.class.getCanonicalName();
	private int windowH;
	private AppDialog mAppDialog;

	private DataAdapter mAdapter = new DataAdapter();
	private AppContentObserverImpl mAppContentObserver = new AppContentObserverImpl();
	private SystemContentObserverImpl mContentObserver = new SystemContentObserverImpl();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Display mDisplay = getWindowManager().getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		mDisplay.getMetrics(metrics);
		windowH = metrics.heightPixels;
		setContentView(R.layout.activity_main);
		Button button = (Button) findViewById(R.id.dialogbutton);
		button.getLayoutParams().height = button.getLayoutParams().width = metrics.widthPixels / 5;
		button.setTextSize(TypedValue.COMPLEX_UNIT_PX, metrics.widthPixels / 8);
		button.setOnClickListener(mAppContentObserver);
		ListView listView = (ListView) findViewById(R.id.listview);
		listView.setAdapter(mAdapter);
	}

	public void onStart() {
		super.onStart();
		mContentObserver.register();
	}

	public void onStop() {
		mAppContentObserver.unregister();
		super.onStop();
	}

	public void onDestroy() {
		super.onDestroy();
	}

	private class DataAdapter extends BaseAdapter {

		private ArrayMap<String, String> mArray = new ArrayMap<String, String>();

		@Override
		public int getCount() {
			return mArray.size();
		}

		public void addData(String key, String value) {
			mArray.put(key, value);
			notifyDataSetChanged();
		}

		public void remove(String key) {
			mArray.remove(key);
			notifyDataSetChanged();
		}

		@Override
		public Object getItem(int position) {
			return mArray.valueAt(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.i_listitem, parent, false);
				convertView.getLayoutParams().height = windowH / 12;
				TextView tvData = (TextView) convertView.findViewById(R.id.item_data);
				TextView tvId = (TextView) convertView.findViewById(R.id.item_ids);
				tvData.setTextSize(TypedValue.COMPLEX_UNIT_PX, windowH / 37);
				tvId.setTextSize(TypedValue.COMPLEX_UNIT_PX, windowH / 37);
			}
			TextView tvData = (TextView) convertView.findViewById(R.id.item_data);
			TextView tvId = (TextView) convertView.findViewById(R.id.item_ids);
			tvId.setText(mArray.keyAt(position));
			tvData.setText(mArray.valueAt(position));
			return convertView;
		}

	}

	private class SystemContentObserverImpl extends ContentObserver {

		public SystemContentObserverImpl() {
			super(null);
		}

		public void register() {
			try {
				getContentResolver().registerContentObserver(AppContentProvider.AUTHORITIES, true,
						SystemContentObserverImpl.this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onChange(boolean selfChange) {
			getContentResolver().unregisterContentObserver(this);
			mAppContentObserver.register();
		}
	}

	private class AppContentObserverImpl extends AppContentObserver implements OnClickListener {

		public void register() {
			AppContentResolver mResolver = AppContentResolver.queryAppContentResolver(MainActivity.this,
					AppContentProvider.AUTHORITIES);
			if (mResolver != null) {
				mResolver.registerAppContentObserver(mAppContentObserver, AppContentProvider.AUTHORITIES, false);
			} else {
				Toast.makeText(getApplication(), "ContentResolver Is Null ! DataProvider No Running",
						Toast.LENGTH_SHORT).show();
			}
		}

		public void unregister() {
			AppContentResolver mResolver = AppContentResolver.queryAppContentResolver(MainActivity.this,
					AppContentProvider.AUTHORITIES);
			if (mResolver != null) {
				mResolver.unregisterAppContentObserver(this);
			}
		}

		public void onContentChanged(final int action, final Uri uri, final Bundle data) {
			Log.w(TAG, "Receive provider content change");
			runOnUiThread(new Runnable() {
				@Override

				public void run() {
					String id = uri.getLastPathSegment();
					switch (action) {
					case ACTION_DELETE:
						mAdapter.remove(id);
						break;
					case ACTION_INSERT:
					case ACTION_UPDATE:
						mAdapter.addData(id, data.getString(AppContentProvider.DATA));
						break;
					}
				}
			});
		}

		@Override
		public void onClick(View v) {
			if (mAppDialog == null) {
				mAppDialog = new AppDialog(MainActivity.this, windowH / 2);
			}
			mAppDialog.show();
		}

	}
}
