
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
import android.os.Bundle;
import android.os.Process;

/**
 * Receives call backs for changes to content.
 * 
 * @author Maizer
 *
 */
public class AppContentObserver {

	private AppContentObserverClient mClient = new AppContentObserverClient(this);

	static final java.lang.String DESCRIPTOR = AppContentObserver.class.getSimpleName() + ":" + Process.myUid();

	public final static int ACTION_DELETE = 0;
	public final static int ACTION_UPDATE = 1;
	public final static int ACTION_INSERT = 2;

	public void onContentChanged(int action, Uri uri, Bundle data) {
	}

	void dispatchContentChanged(int action, Uri uri, Bundle data, boolean sync) {
		onContentChanged(action, uri, data);
	}

	AppContentObserverClient getContentObserverClient() {
		return mClient;
	}

}
