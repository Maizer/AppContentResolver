
/*

 * Licensed under the Apache License, Version 2.0 (the "License");

 * you may not use this file except in compliance with the License.

 * You may obtain a copy of the License at

 *

 * http://www.apache.org/licenses/LICENSE-2.0

 *

 * Unless required by applicable law or agreed to in writing, software

 * distributed under the License is distributed on an "AS IS" BASIS,

 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.

 * See the License for the specific language governing permissions and

 * limitations under the License.

 */
package com.maizer.appcontent;

import java.util.ArrayList;
import java.util.List;

import com.maizer.array.CombinArray;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ProviderInfo;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

/**
 * 
 * @author Maizer
 *
 */
public class AppContentResolver {

	static final java.lang.String DESCRIPTOR = AppContentResolver.class.getSimpleName() + ":" + Process.myUid();

	private static AppContentResolver sResolver;

	private CombinArray<Uri, List<AppContentObserver>> mCombinArray;

	public synchronized static AppContentResolver getAppContentResolverServicer(Context mContext) {
		if (sResolver == null) {
			PackageInfo mInfo;
			try {
				String pn = mContext.getPackageName();
				mInfo = mContext.getPackageManager().getPackageInfo(pn, PackageManager.GET_PROVIDERS);
				if (mInfo != null) {
					ProviderInfo[] infos = mInfo.providers;
					if (infos != null) {
						ActivityManager mActivityManager = (ActivityManager) mContext
								.getSystemService(Context.ACTIVITY_SERVICE);
						List<RunningAppProcessInfo> appInfos = mActivityManager.getRunningAppProcesses();
						int pid = Process.myPid();
						int uid = Process.myUid();

						for (int j = 0; j < appInfos.size(); j++) {
							RunningAppProcessInfo appInfo = appInfos.get(j);
							if (appInfo != null && appInfo.pid == pid && appInfo.uid == uid) {
								for (int i = 0; i < infos.length; i++) {
									ProviderInfo info = infos[i];
									if (info != null) {
										String mName = info.processName;
										if (mName == null) {
											mName = pn;
										}
										if (mName.equals(appInfo.processName)) {
											sResolver = new AppContentResolver();
											break;
										}
									}
								}
								break;
							}
						}
					}

				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sResolver;

	}

	AppContentResolver() {
	}

	public static boolean isCallAppContentResolver(String selection) {
		return DESCRIPTOR.equals(selection);
	}

	public static AppContentResolver queryAppContentResolver(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, DESCRIPTOR, null, null);
		if (cursor != null) {
			try {
				Bundle mBundle = cursor.getExtras();
				if (mBundle != null) {
					mBundle.setClassLoader(ParcelBinder.class.getClassLoader());
					if (mBundle.containsKey(DESCRIPTOR)) {
						ParcelBinder mBinder = mBundle.getParcelable(DESCRIPTOR);
						IBinder mIBinder = mBinder.getIBinder();
						return AppContentResolverClient.asInterface(mIBinder);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					cursor.close();
				} catch (Exception e) {
				}
			}
		} else {
			Log.w(DESCRIPTOR, "service procress may be no runing !");
		}
		return null;
	}

	public void registerAppContentObserver(AppContentObserver mContenObserver, Uri uri, boolean sync) {
		synchronized (AppContentResolver.class) {
			if (mCombinArray == null) {
				mCombinArray = new CombinArray<Uri, List<AppContentObserver>>();
			}
			List<AppContentObserver> mObservers = mCombinArray.getValueOfKey(uri);
			if (mObservers == null) {
				mObservers = new ArrayList<AppContentObserver>();
				mCombinArray.add(uri, mObservers);
			}
			if (!mObservers.contains(mContenObserver)) {
				mObservers.add(mContenObserver);
			}
		}
	}

	public void unregisterAppContentObserver(AppContentObserver mContenObserver) {
		if (mCombinArray == null) {
			return;
		}
		synchronized (AppContentResolver.class) {
			for (int i = mCombinArray.size() - 1; i >= 0; i--) {
				List<AppContentObserver> mContentObservers = mCombinArray.getValue(i);
				if (mContenObserver != null) {
					mContentObservers.remove(mContenObserver);
					if (mContentObservers.isEmpty()) {
						mCombinArray.removeAt(i);
					}
				}
			}
		}
	}

	public void notifyChange(int action, Uri notigicationUri, Uri uri, Bundle data, boolean syncToNetwork,
			AppContentObserver observer) {
		if (mCombinArray == null || mCombinArray.size() <= 0) {
			return;
		}
		if (observer == null) {
			List<AppContentObserver> mContentObservers = mCombinArray.getValueOfKey(notigicationUri);
			if (mContentObservers != null) {
				if (uri == null) {
					uri = notigicationUri;
				}
				for (int i = mContentObservers.size() - 1; i >= 0; i--) {
					AppContentObserver mObserver = mContentObservers.get(i);
					try {
						mObserver.onContentChanged(action, uri, data, syncToNetwork);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			List<AppContentObserver> mContentObservers = mCombinArray.getValueOfKey(notigicationUri);
			if (mContentObservers != null && mContentObservers.contains(observer)) {
				if (uri == null) {
					uri = notigicationUri;
				}
				try {
					observer.onContentChanged(action, uri, data, syncToNetwork);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void notifyChange(int action, Uri notigicationUri, Uri uri, Bundle data) {
		notifyChange(action, notigicationUri, uri, data, false, null);
	}

	public void clearServicer() {
		if (mCombinArray == null || mCombinArray.size() <= 0) {
			return;
		}
		synchronized (AppContentResolver.class) {
			mCombinArray.startTransaction();
			try {
				for (int i = mCombinArray.size() - 1; i >= 0; i--) {
					List<AppContentObserver> value = mCombinArray.getValue(i);
					if (value != null) {
						for (int j = value.size() - 1; j >= 0; j--) {
							AppContentObserver result = value.get(j);
							if (result instanceof AppContentObserverServicer) {
								if (!((AppContentObserverServicer) result).pingBinder()) {
									value.remove(j);
								}
							}
						}
						if (value.isEmpty()) {
							mCombinArray.removeAt(i);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				mCombinArray.endTransaction();
			}
		}
	}

	public boolean hasAppContentObserverOnUri(Uri notigicationUri) {
		return hasAppContentObserverOnUri(notigicationUri, null);
	}

	public boolean hasAppContentObserverOnUri(Uri notigicationUri, AppContentObserver observer) {
		if (notigicationUri == null) {
			throw new NullPointerException("Notification Uri Not Null !");
		}
		if (mCombinArray == null || mCombinArray.isEmpty()) {
			return false;
		}
		List<AppContentObserver> mContentObservers = mCombinArray.getValueOfKey(notigicationUri);
		if (observer == null) {
			return mContentObservers != null && !mContentObservers.isEmpty();
		}
		if (mContentObservers == null) {
			return false;
		}
		return mContentObservers.contains(observer);
	}

	public Cursor getCursor() {
		return ResolverCursor.getResolverCursor();
	}

	static AppContentResolver getAppContentResolver() {
		return sResolver;
	}

	private static final class ResolverCursor extends AbstractCursor {

		static ResolverCursor S_CURSOR;

		private Bundle mBundle;

		static synchronized ResolverCursor getResolverCursor() {
			if (S_CURSOR == null) {
				S_CURSOR = new ResolverCursor();
			}
			return S_CURSOR;
		}

		ResolverCursor() {
			mBundle = new Bundle();
			mBundle.putParcelable(DESCRIPTOR, new ParcelBinder(new AppContentResolverServicer()));
		}

		@Override
		public Bundle getExtras() {
			return mBundle;
		}

		@Override
		public int getCount() {
			return 0;
		}

		@Override
		public String[] getColumnNames() {
			return new String[] {};
		}

		@Override
		public String getString(int column) {
			return null;
		}

		@Override
		public short getShort(int column) {
			return 0;
		}

		@Override
		public int getInt(int column) {
			return 0;
		}

		@Override
		public long getLong(int column) {
			return 0;
		}

		@Override
		public float getFloat(int column) {
			return 0;
		}

		@Override
		public double getDouble(int column) {
			return 0;
		}

		@Override
		public boolean isNull(int column) {
			return false;
		}

	}

	public static class ContentResoloverError extends RuntimeException {

		/**
		 *
		 */
		private static final long serialVersionUID = 4884253366316398655L;

		public ContentResoloverError(Exception e) {
			super(e);
		}
	}

}
