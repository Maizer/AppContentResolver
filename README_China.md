AppContentResolver
----------
Android应用程序内部ContentResolver,内部传输数据在你的App.Android原始ContentResolver只通知内容改变了,如果想要更改的数据,我们必须定制实现,这是一个非常复杂的工作。
例如:
多个进程媒体应用,定制SqLite数据库,需要将系统数据库复制到这个数据库,如果改变了系统数据库,自己的数据库需要匹配数据变化(这非常消耗时间),然后由系统ContentResolver通知自己的ContentObservers(在其他进展)更新数据,然后自己ContentObserver匹配数据库变化,通知UI刷新,我们耗费大量时间在匹配操作。
当然,有很多方法来解决这个问题。

Note : 
-
Whether or not security depending on your ContentProvider . Please do not try to modify the code ,Because security problem of the 
may trigger .
-

My english is bad,Please forgive.

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
 AppContentResolver mResolver = AppContentResolver.queryAppContentResolver(Context,Uri);
  if(mResolver != null){
	   mResolver.registerAppContentObserver(AppContentObserver, Uri, isSync);
  }
 ```

