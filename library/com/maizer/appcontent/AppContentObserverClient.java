
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

import java.lang.ref.WeakReference;

import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * 
 * @author Maizer
 *
 */
class AppContentObserverClient extends Binder implements IInterface {

	private WeakReference<AppContentObserver> mContentObserver;

	AppContentObserverClient(AppContentObserver observer) {
		if (observer == null) {
			throw new NullPointerException("Content Observer Not Null !");
		}
		mContentObserver = new WeakReference<AppContentObserver>(observer);
		attachInterface(this, AppContentObserver.DESCRIPTOR);
	}

	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
		switch (code) {
		case AppContentObserverServicer.TRANSACTION_onContentChanged:
			data.enforceInterface(AppContentObserver.DESCRIPTOR);
			AppContentObserver observer = mContentObserver.get();
			if (observer != null) {
				int action = data.readInt();
				Uri uri = data.readParcelable(Uri.class.getClassLoader());
				int flag = data.readInt();
				Bundle bundle = null;
				if (flag == 1) {
					bundle = data.readParcelable(Bundle.class.getClassLoader());
				}
				observer.onContentChanged(action, uri, bundle);
				reply.writeNoException();
			} else {
				reply.writeException(new NullPointerException("AppContentObserver is recyled!"));
			}
			return true;
		}
		return super.onTransact(code, data, reply, flags);
	}

	public boolean equels(Object obj) {
		if (!(obj instanceof AppContentObserverClient)) {
			return false;
		}
		return hashCode() == obj.hashCode();
	}

	/**
	 * prevent object overflow
	 */
	public final int hashCode() {
		AppContentObserver observer = mContentObserver.get();
		if (observer != null) {
			return observer.getClass().getCanonicalName().hashCode();
		}
		throw new NullPointerException("AppContentObserver is recyled!");
	}

	@Override
	public IBinder asBinder() {
		return this;
	}
}
