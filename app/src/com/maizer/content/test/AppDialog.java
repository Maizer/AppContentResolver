package com.maizer.content.test;

import com.maizer.appcontent.R;
import com.maizer.example.AppContentProvider;

import android.app.Dialog;
import android.content.ContentValues;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AppDialog extends Dialog implements OnClickListener {

	public static final String TAG = AppDialog.class.getCanonicalName();

	public AppDialog(MainActivity context, int h) {
		super(context);
		ViewGroup vg = (ViewGroup) context.getLayoutInflater().inflate(R.layout.dialog_layout, null, false);
		for (int i = 0; i < vg.getChildCount(); i++) {
			TextView tv = (TextView) vg.getChildAt(i);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, h / 15);
			if (!(tv instanceof EditText)) {
				tv.setOnClickListener(this);
			}
		}
		setContentView(vg, new android.widget.FrameLayout.LayoutParams(-1, h, Gravity.CENTER));
		getWindow().getDecorView().setBackgroundColor(Color.BLACK & 90000000);
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void onClick(View v) {
		TextView contentView = (TextView) findViewById(R.id.dialog_content);
		TextView idView = (TextView) findViewById(R.id.dialog_id);
		TextView numView = (TextView) findViewById(R.id.dialog_num);

		CharSequence num = numView.getText();
		CharSequence content = contentView.getText();

		int id = v.getId();
		if (id == R.id.dialog_insert) {
			if (num != null) {
				try {
					int n = Integer.valueOf(num.toString());
					ContentValues mContentValues = new ContentValues();
					for (int i = 0; i < n; i++) {
						mContentValues.put(AppContentProvider.DATA, content == null ? null : content.toString());
						getContext().getContentResolver().insert(AppContentProvider.AUTHORITIES, mContentValues);
					}
					return;
				} catch (Exception e) {
				}
			}
			ContentValues mContentValues = new ContentValues();
			mContentValues.put(AppContentProvider.DATA, content == null ? null : content.toString());
			getContext().getContentResolver().insert(AppContentProvider.AUTHORITIES, mContentValues);
		} else if (id == R.id.dialog_delete) {
			try {
				CharSequence md = idView.getText();
				if (!TextUtils.isEmpty(md)) {
					try {
						Integer.valueOf(md.toString());
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(getContext(), "ID Input Error !", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (TextUtils.isEmpty(content) && TextUtils.isEmpty(md)) {
					Toast.makeText(getContext(), "No Found Input Value !", Toast.LENGTH_SHORT).show();
					return;
				}
				StringBuilder mBuilder = new StringBuilder();
				if (!TextUtils.isEmpty(md)) {
					mBuilder.append(AppContentProvider.ID);
					mBuilder.append(" = ?");
					if (!TextUtils.isEmpty(content)) {
						mBuilder.append(" AND ");
						mBuilder.append(AppContentProvider.DATA);
						mBuilder.append(" = ?");
						getContext().getContentResolver().delete(AppContentProvider.AUTHORITIES, mBuilder.toString(),
								new String[] { md.toString(), content.toString() });
					} else {
						getContext().getContentResolver().delete(AppContentProvider.AUTHORITIES, mBuilder.toString(),
								new String[] { md.toString() });
					}
				} else {
					mBuilder.append(AppContentProvider.DATA);
					mBuilder.append(" = ?");
					getContext().getContentResolver().delete(AppContentProvider.AUTHORITIES, mBuilder.toString(),
							new String[] { content.toString() });
				}
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
		} else if (id == R.id.dialog_update) {
			if (TextUtils.isEmpty(num)) {
				try {
					String md = idView.getText().toString();
					Integer.valueOf(md);
					ContentValues mContentValues = new ContentValues();
					mContentValues.put(AppContentProvider.DATA, TextUtils.isEmpty(content) ? null : content.toString());
					getContext().getContentResolver().update(AppContentProvider.AUTHORITIES, mContentValues,
							AppContentProvider.ID + " =?", new String[] { md });
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(getContext(), "ID Input Error !", Toast.LENGTH_SHORT).show();
					return;
				}
			} else if (!TextUtils.isEmpty(content)) {
				try {
					ContentValues mContentValues = new ContentValues();
					mContentValues.put(AppContentProvider.DATA, num.toString());
					getContext().getContentResolver().update(AppContentProvider.AUTHORITIES, mContentValues,
							AppContentProvider.DATA + " =?", new String[] { content.toString() });
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			} else {
				Toast.makeText(getContext(), "Input Error !", Toast.LENGTH_SHORT).show();
			}
		}
	}

}
