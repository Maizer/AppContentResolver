
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

import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

/**
 * @author Maizer
 */
class AppContentResolverClient extends AppContentResolver implements IInterface {

	private IBinder mRemote;

	private AppContentResolverClient(IBinder binder) {
		mRemote = binder;
	}

	static AppContentResolver asInterface(IBinder mIBinder) {
		if (mIBinder == null) {
			return null;
		}
		IInterface mIInterface = mIBinder.queryLocalInterface(DESCRIPTOR);
		if (mIInterface != null && mIInterface instanceof AppContentResolver) {
			return (AppContentResolver) mIInterface;
		}
		return new AppContentResolverClient(mIBinder);
	}

	public void registerAppContentObserver(AppContentObserver mContenObserver, Uri uri, boolean sync) {
		if (uri == null) {
			throw new NullPointerException("uri not null!");
		}
		android.os.Parcel _data = android.os.Parcel.obtain();
		android.os.Parcel _reply = android.os.Parcel.obtain();
		try {
			_data.writeInterfaceToken(DESCRIPTOR);
			_data.writeInt(sync ? 1 : 0);
			_data.writeParcelable(uri, 0);
			AppContentObserverClient mClient = mContenObserver.getContentObserverClient();
			_data.writeInt(mClient.hashCode());
			_data.writeStrongBinder(mClient);
			mRemote.transact(TRANSACTION_registerContentObserver, _data, _reply, 0);
			_reply.readException();
		} catch (RemoteException e) {
			throw new ContentResoloverError(e);
		} finally {
			_reply.recycle();
			_data.recycle();
		}
	}

	public void unregisterAppContentObserver(AppContentObserver mContenObserver) {
		if (mContenObserver == null) {
			throw new NullPointerException("contenObserver not null!");
		}
		android.os.Parcel _data = android.os.Parcel.obtain();
		android.os.Parcel _reply = android.os.Parcel.obtain();
		try {
			_data.writeInterfaceToken(DESCRIPTOR);
			AppContentObserverClient mClient = mContenObserver.getContentObserverClient();
			_data.writeInt(mClient.hashCode());
			_data.writeStrongBinder(mClient);
			mRemote.transact(TRANSACTION_unregisterContentObserver, _data, _reply, Binder.FLAG_ONEWAY);
			_reply.readException();
		} catch (RemoteException e) {
			throw new ContentResoloverError(e);
		} finally {
			_reply.recycle();
			_data.recycle();
		}
	}

	@Override
	public void notifyChange(int action, Uri notificationUri, Uri uri, Bundle data, boolean syncToNetwork,
			AppContentObserver observer) {
		if (uri == null) {
			throw new NullPointerException("uri not null!");
		}
		android.os.Parcel _data = android.os.Parcel.obtain();
		android.os.Parcel _reply = android.os.Parcel.obtain();
		try {
			_data.writeInterfaceToken(DESCRIPTOR);
			_data.writeInt(action);
			_data.writeInt(syncToNetwork ? 1 : 0);
			_data.writeParcelable(data, 0);
			if (notificationUri != null) {
				_data.writeInt(1);
				_data.writeParcelable(notificationUri, 0);
			} else {
				_data.writeInt(0);
			}
			_data.writeParcelable(uri, 0);
			if (observer != null) {
				AppContentObserverClient mClient = observer.getContentObserverClient();
				_data.writeInt(mClient.hashCode());
				_data.writeStrongBinder(mClient);
			} else {
				_data.writeInt(-1);
			}
			mRemote.transact(TRANSACTION_notifyChange, _data, _reply, Binder.FLAG_ONEWAY);
			_reply.readException();
		} catch (RemoteException e) {
			throw new ContentResoloverError(e);
		} finally {
			_reply.recycle();
			_data.recycle();
		}
	}

	@Override
	public void clearServicer() {
		android.os.Parcel _data = android.os.Parcel.obtain();
		try {
			_data.writeInterfaceToken(DESCRIPTOR);
			mRemote.transact(TRANSACTION_clearServicer, _data, null, Binder.FLAG_ONEWAY);
		} catch (RemoteException e) {
			throw new ContentResoloverError(e);
		} finally {
			_data.recycle();
		}
	}

	@Override
	public boolean hasAppContentObserverOnUri(Uri notigicationUri, AppContentObserver observer) {
		if (notigicationUri == null) {
			throw new NullPointerException("uri not null!");
		}
		boolean _result;
		android.os.Parcel _data = android.os.Parcel.obtain();
		android.os.Parcel _reply = android.os.Parcel.obtain();
		try {
			_data.writeInterfaceToken(DESCRIPTOR);
			_data.writeParcelable(notigicationUri, 0);
			if (observer == null) {
				_data.writeInt(0);
			} else {
				_data.writeInt(1);
				AppContentObserverClient mClient = observer.getContentObserverClient();
				_data.writeInt(mClient.hashCode());
				_data.writeStrongBinder(mClient);
			}
			mRemote.transact(TRANSACTION_hasAppContentObserverOnUri, _data, _reply, 0);
			_reply.readException();
			_result = _reply.readInt() == 1;
		} catch (RemoteException e) {
			throw new ContentResoloverError(e);
		} finally {
			_reply.recycle();
			_data.recycle();
		}
		return _result;
	}

	public boolean hasAppContentObserverOnUri(Uri notigicationUri) {
		return hasAppContentObserverOnUri(notigicationUri, null);
	}

	public Cursor getCursor() {
		throw new NoSuchMethodError("Client Nosupport the Method !");
	}

	@Override
	public IBinder asBinder() {
		return mRemote;
	}

	static final int TRANSACTION_registerContentObserver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
	static final int TRANSACTION_unregisterContentObserver = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
	static final int TRANSACTION_notifyChange = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
	static final int TRANSACTION_hasAppContentObserverOnUri = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
	static final int TRANSACTION_clearServicer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
