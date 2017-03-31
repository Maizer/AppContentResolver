package com.maizer.example;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.maizer.appcontent.AppContentObserver;
import com.maizer.appcontent.AppContentResolver;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class AppContentProvider extends ContentProvider {

	public static final String TAG = AppContentProvider.class.getCanonicalName();
	public static final Uri AUTHORITIES = Uri.parse("content://com.maizer.content");

	private SQLiteDatabase mDatabase;
	public static final String TABLE = "example";
	public static final String DATA = "data";
	public static final String ID = "id";

	private String mSql = "CREATE TABLE " + TABLE + " (\n" + ID
			+ "      INTEGER PRIMARY KEY ASC AUTOINCREMENT \nNOT NULL \nUNIQUE,\n" + DATA + "  TEXT\n);";

	@Override
	public boolean onCreate() {
		mDatabase = getContext().openOrCreateDatabase("example.db", Context.MODE_PRIVATE, null);
		if (!checkTableExists(mDatabase, TABLE)) {
			mDatabase.execSQL(mSql);
		} else {
			try {
				mDatabase.execSQL("UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + TABLE + "';");
			} catch (Exception ex) {
			}
			try {
				mDatabase.delete(TABLE, null, null);
			} catch (Exception ex) {
			}
		}
		getContext().getContentResolver().notifyChange(AUTHORITIES, null);
		return true;
	}

	public static boolean checkTableExists(SQLiteDatabase db, String tableName) {
		String sql = "SELECT COUNT(*) From sqlite_master WHERE type='table' AND name='" + tableName + "'";
		SQLiteStatement mLiteStatement = db.compileStatement(sql);
		long count = mLiteStatement.simpleQueryForLong();
		mLiteStatement.clearBindings();
		mLiteStatement.close();
		return count > 0;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (AppContentResolver.isCallContentResolver(selection)) {
			Log.w(TAG, "query app content resolver");
			return AppContentResolver.getAppContentResolverServicer(getContext()).getCursor();
		}
		return mDatabase.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Log.w(TAG, "provider content insert");
		long id = mDatabase.insert(TABLE, null, values);
		uri = Uri.withAppendedPath(AUTHORITIES, Long.toString(id));
		if (id >= 0) {
			Bundle mBundle = new Bundle();
			mBundle.putString(DATA, values.getAsString(DATA));
			AppContentResolver.getAppContentResolverServicer(getContext())
					.notifyChange(AppContentObserver.ACTION_INSERT, AUTHORITIES, uri, mBundle);
		}
		return uri;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.w(TAG, "provider content delete");
		int count = 0;
		Cursor mCursor = mDatabase.query(TABLE, new String[] { ID }, selection, selectionArgs, null, null, null);
		if (mCursor != null) {
			try {
				if (mCursor.getCount() > 0) {
					SQLiteStatement mLiteStatement = getSQLiteStatementwithDelete(selection, selectionArgs);
					try {
						int bindIndex = 1;
						if (!TextUtils.isEmpty(selection) && selectionArgs != null) {
							bindIndex += selectionArgs.length;
						}
						AppContentResolver mResolver = AppContentResolver.getAppContentResolverServicer(getContext());
						while (mCursor.moveToNext()) {
							try {
								String id = Integer.toString(mCursor.getInt(0));
								mLiteStatement.bindString(bindIndex, id);
								try {
									if (mLiteStatement.executeUpdateDelete() > 0) {
										count++;
										mResolver.notifyChange(AppContentObserver.ACTION_DELETE, AUTHORITIES,
												Uri.withAppendedPath(AUTHORITIES, id), null);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} finally {
						mLiteStatement.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(mCursor);
			}
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(AUTHORITIES, null);
		}
		return count;
	}

	private void closeCursor(Cursor... cursor) {
		for (int i = 0; i < cursor.length; i++) {
			try {
				cursor[i].close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

		Log.w(TAG, "provider content update");
		int count = 0;
		Cursor mCursor = mDatabase.query(TABLE, new String[] { ID }, selection, selectionArgs, null, null, null);
		if (mCursor != null) {
			try {
				if (mCursor.getCount() > 0) {
					SQLiteStatement mLiteStatement = getSQLiteStatementwithUpdate(values, selection, selectionArgs);
					try {
						int bindIndex = 1 + values.size();
						if (!TextUtils.isEmpty(selection) && selectionArgs != null) {
							bindIndex += selectionArgs.length;
						}
						Bundle mBundle = new Bundle();
						AppContentResolver mResolver = AppContentResolver.getAppContentResolverServicer(getContext());
						while (mCursor.moveToNext()) {
							try {
								String id = Integer.toString(mCursor.getInt(0));
								mLiteStatement.bindString(bindIndex, id);
								try {
									if (mLiteStatement.executeUpdateDelete() > 0) {
										count++;
										mBundle.putString(DATA, values.getAsString(DATA));
										mResolver.notifyChange(AppContentObserver.ACTION_UPDATE, AUTHORITIES,
												Uri.withAppendedPath(AUTHORITIES, id), mBundle);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					} finally {
						mLiteStatement.close();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				closeCursor(mCursor);
			}
		}
		if (count > 0) {
			getContext().getContentResolver().notifyChange(AUTHORITIES, null);
		}
		return count;
	}

	private SQLiteStatement getSQLiteStatementwithDelete(String whereClause, String[] whereArgs) {
		StringBuilder sql = new StringBuilder(120);
		sql.append("DELETE FROM ");
		sql.append(TABLE);
		sql.append(" WHERE ");

		if (!TextUtils.isEmpty(whereClause)) {
			sql.append("( ");
			sql.append(whereClause);
			sql.append(" )");
			sql.append(" AND ");
		} else {
			whereArgs = null;
		}
		sql.append(ID);
		sql.append(" = ?");

		SQLiteStatement mLiteStatement = mDatabase.compileStatement(sql.toString());

		if (whereArgs != null) {
			for (int i = 0; i < whereArgs.length; i++) {
				if (whereArgs[i] == null) {
					mLiteStatement.bindNull(i + 1);
					continue;
				}
				mLiteStatement.bindString(i + 1, whereArgs[i]);
			}
		}
		return mLiteStatement;
	}

	private SQLiteStatement getSQLiteStatementwithUpdate(ContentValues values, String whereClause, String[] whereArgs) {
		StringBuilder sql = new StringBuilder(120);
		sql.append("UPDATE  OR ROLLBACK ");
		sql.append(TABLE);
		sql.append(" SET ");

		// move all bind args to one array
		int setValuesSize = values.size();
		int bindArgsSize = setValuesSize;
		if (!TextUtils.isEmpty(whereClause) && whereArgs != null) {
			bindArgsSize += whereArgs.length;
		} else {
			whereArgs = null;
		}
		Object[] bindArgs = new Object[bindArgsSize];
		int i = 0;
		Set<Map.Entry<String, Object>> mEntries = values.valueSet();
		for (Entry<String, Object> colName : mEntries) {
			sql.append((i > 0) ? "," : "");
			sql.append(colName.getKey());
			bindArgs[i++] = colName.getValue();
			sql.append("=?");
		}
		if (whereArgs != null) {
			for (i = setValuesSize; i < bindArgsSize; i++) {
				bindArgs[i] = whereArgs[i - setValuesSize];
			}
		}

		sql.append(" WHERE ");

		if (!TextUtils.isEmpty(whereClause)) {
			sql.append("( ");
			sql.append(whereClause);
			sql.append(" )");
			sql.append(" AND ");
		}
		sql.append(ID);
		sql.append(" = ?");

		SQLiteStatement mLiteStatement = mDatabase.compileStatement(sql.toString());

		for (int j = 0; j < bindArgs.length; j++) {
			if (bindArgs[j] == null) {
				mLiteStatement.bindNull(j + 1);
			} else {
				mLiteStatement.bindString(j + 1, bindArgs[j].toString());
			}
		}
		return mLiteStatement;
	}

}
