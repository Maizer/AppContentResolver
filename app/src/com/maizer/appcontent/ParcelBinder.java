
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

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

class ParcelBinder implements Parcelable {

	private IBinder mBinder;

	public ParcelBinder(IBinder binder) {
		if (binder == null) {
			throw new NullPointerException("IBinder Can Not Null !");
		}
		mBinder = binder;
	}

	private ParcelBinder(Parcel p) {
		this(p.readStrongBinder());
	}

	public IBinder getIBinder() {
		return mBinder;
	}

	public static final Parcelable.Creator<ParcelBinder> CREATOR = new Parcelable.Creator<ParcelBinder>() {

		@Override
		public ParcelBinder[] newArray(int arg0) {
			return new ParcelBinder[arg0];
		}

		@Override
		public ParcelBinder createFromParcel(Parcel arg0) {
			return new ParcelBinder(arg0);
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeStrongBinder(mBinder);
	}

}
