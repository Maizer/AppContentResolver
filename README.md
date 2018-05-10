AppContentResolver
----------
Android App Internal ContentResolver,It can internal transmit data In your's App.Shorten time in data match,will background program changed specific data notification to foreground.

Android primitive ContentResolver only notification content is changed,If want get changed data,We must custom implements ,This is a very complicated work.

For example:

Multiple processes Media App,Has custom SqLite Database ,Need Copy System Database To this Database,if system database is changed,own database need match data change in own progress(this time very consuming),then by system ContentResolver notification own ContentObservers(in other progress) update data,then own ContentObserver match data base change,notification UI refresh,So we consuming a lot of time in match action.
sure ,has A lot of method to solve this problem .

Note : 
-
Whether or not security depending on your ContentProvider . Please do not try to modify the code ,Because security problem of the 
may trigger .
-

This program is very simple for use. 

How work:

In ContentProvider query method :
-
```Java
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (AppContentResolver.isCallAppContentResolver(selection)) {
			return AppContentResolver.getAppContentResolverServicer(getContext()).getCursor();
		}
...
	}
```

In other process :
-
```Java
//impl AppContentObserver receive background change
class AppContentObserverImpl extends AppContentObserver{
		public void onContentChanged(int action,  Uri uri, Bundle data) {
			//receive data change
		}
	} 
```

```Java
// register AppContentObserver
 AppContentResolver mResolver = AppContentResolver.queryAppContentResolver(Context,Uri);
  if(mResolver != null){
	   mResolver.registerAppContentObserver(AppContentObserver, Uri, isSync);
  }
 ```
 
 
 ## License

```
Copyright (C) 2017 Maizer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```


 Development platform : Eclipse
 
 
 [中文README](/README_China.md)
