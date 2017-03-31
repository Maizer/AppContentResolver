
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
class AppContentResolverServicer extends Binder implements IInterface {

	AppContentResolverServicer() {
		super();
		attachInterface(this, AppContentResolver.DESCRIPTOR);
	}

	@Override
	protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
		switch (code) {
		case AppContentResolverClient.TRANSACTION_notifyChange: {
			data.enforceInterface(AppContentResolver.DESCRIPTOR);
			int action = data.readInt();
			boolean sync = data.readInt() == 1;
			Bundle mValues = data.readParcelable(Bundle.class.getClassLoader());
			boolean hasUri = data.readInt() == 1;
			Uri notificationUri = data.readParcelable(Uri.class.getClassLoader());
			Uri uri;
			if (hasUri) {
				uri = data.readParcelable(Uri.class.getClassLoader());
			} else {
				uri = notificationUri;
			}
			int hash = data.readInt();
			AppContentObserverServicer mClient = null;
			if (hash != -1) {
				IBinder mBinder = data.readStrongBinder();
				mClient = new AppContentObserverServicer(mBinder, hash);
			}
			AppContentResolver.getAppContentResolver().notifyChange(action, notificationUri, uri, mValues, sync,
					mClient);
			reply.writeNoException();
			return true;
		}
		case AppContentResolverClient.TRANSACTION_registerContentObserver: {
			data.enforceInterface(AppContentResolver.DESCRIPTOR);
			boolean sync = data.readInt() == 1;
			Uri uri = data.readParcelable(Uri.class.getClassLoader());
			int hash = data.readInt();
			IBinder mBinder = data.readStrongBinder();
			AppContentObserverServicer mClient = new AppContentObserverServicer(mBinder, hash);
			mClient.syncToNetwork = sync;
			AppContentResolver.getAppContentResolver().registerAppContentObserver(mClient, uri, sync);
			reply.writeNoException();
			return true;
		}
		case AppContentResolverClient.TRANSACTION_unregisterContentObserver: {
			data.enforceInterface(AppContentResolver.DESCRIPTOR);
			int hash = data.readInt();
			IBinder mBinder = data.readStrongBinder();
			AppContentObserverServicer mClient = new AppContentObserverServicer(mBinder, hash);
			AppContentResolver.getAppContentResolver().unregisterAppContentObserver(mClient);
			reply.writeNoException();
			return true;
		}
		case AppContentResolverClient.TRANSACTION_clearServicer: {
			data.enforceInterface(AppContentResolver.DESCRIPTOR);
			AppContentResolver.getAppContentResolver().clearServicer();
			return true;
		}
		case AppContentResolverClient.TRANSACTION_hasAppContentObserverOnUri: {
			data.enforceInterface(AppContentResolver.DESCRIPTOR);
			Uri uri = data.readParcelable(Uri.class.getClassLoader());
			AppContentObserver observer = null;
			if (data.readInt() == 1) {
				int hashcode = data.readInt();
				IBinder mBinder = data.readStrongBinder();
				observer = new AppContentObserverServicer(mBinder, hashcode);
			}
			boolean result = AppContentResolver.getAppContentResolver().hasAppContentObserverOnUri(uri, observer);
			reply.writeNoException();
			reply.writeInt(result ? 1 : 0);
			return true;
		}
		}
		return super.onTransact(code, data, reply, flags);
	}

	@Override
	public IBinder asBinder() {
		return this;
	}

}
