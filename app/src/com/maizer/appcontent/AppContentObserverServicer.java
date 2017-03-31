
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

import com.maizer.appcontent.AppContentResolver.ContentResoloverError;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * 
 * @author Maizer
 *
 */
class AppContentObserverServicer extends AppContentObserver implements IInterface {

	boolean syncToNetwork;
	private IBinder mRemote;
	private final int hashcode;

	AppContentObserverServicer(IBinder mBinder, int hashcode) {
		mRemote = mBinder;
		this.hashcode = hashcode;
	}

	public boolean equals(Object o) {
		if (!(o instanceof AppContentObserverServicer)) {
			return false;
		}
		return hashcode == ((AppContentObserverServicer) o).hashcode;
	}

	public int hashCode() {
		return hashcode;
	}

	public void onContentChanged(int action, Uri uri, Bundle data, boolean sync) {
		if (uri == null) {
			throw new NullPointerException("uri not null!");
		}
		android.os.Parcel _data = android.os.Parcel.obtain();
		android.os.Parcel _reply = android.os.Parcel.obtain();
		try {
			_data.writeInterfaceToken(AppContentObserver.DESCRIPTOR);
			_data.writeInt(action);
			_data.writeParcelable(uri, 0);
			if (data == null) {
				_data.writeInt(0);
			} else {
				_data.writeInt(1);
				_data.writeParcelable(data, 0);
			}
			mRemote.transact(TRANSACTION_onContentChanged, _data, _reply,
					sync ? 0 : syncToNetwork ? 0 : Binder.FLAG_ONEWAY);
			_reply.readException();
		} catch (RemoteException e) {
			if (!mRemote.pingBinder()) {
				try {
					AppContentResolver.getAppContentResolver().unregisterAppContentObserver(this);
				} catch (Exception es) {
					es.printStackTrace();
				}
			}
			throw new ContentResoloverError(e);
		} finally {
			_reply.recycle();
			_data.recycle();
		}

	}

	public boolean pingBinder() {
		return mRemote.pingBinder();
	}

	@Override
	public IBinder asBinder() {
		return mRemote;
	}

	static final int TRANSACTION_onContentChanged = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);

}
