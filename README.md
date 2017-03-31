
AppContentResolver
----------
Android App Internal ContentResolver,It can internal transmit data In your's App.Android primitive ContentResolver only notification content is changed,If want get changed data,We must custom implements ,This is a very complicated work.
For example:
Multiple processes Media App,Has custom SqLite Database ,Need Copy System Database To this Database,if system database is changed,own database need match data change in own progress(this time very consuming),then by system ContentResolver notification own ContentObservers(in other progress) update data,then own ContentObserver match data base change,notification UI refresh,So we consuming a lot of time in match action.
sure ,has A lot of method to solve this problem .

Note : 
-
Whether or not security depending on your ContentProvider . Please do not try to modify the code ,Because security problem of the 
may trigger .
-

My english is bad,Please forgive.

This program is very simple for use. 

How work:

In ContentProvider query method :
----------
```Java
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (AppContentResolver.isCallContentResolver(selection)) {
			return AppContentResolver.getAppContentResolverServicer(getContext()).getCursor();
		}
...
	}
```

In other process :
----------
```Java
 AppContentResolver mResolver = AppContentResolver.queryAppContentResolver(Context,Uri);
 mResolver.registerAppContentObserver(AppContentObserver, Uri, isSync);
 ```




