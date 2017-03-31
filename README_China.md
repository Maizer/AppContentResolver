AppContentResolver
----------

Android应用程序内部ContentResolver,内部传输数据在你的App.缩短了前台匹配数据变化的时间,可以直接将后台改变的具体数据通知到前台.

Android原始ContentResolver只通知内容改变了,如果想要获取到更改的具体数据,我们必须定制实现,这是一个非常复杂的工作.

例如:

媒体应用有多个进程,并且拥有自己定制的SqLite数据库,需要将系统数据库复制到这个数据库,如果改变了系统数据库,自己的数据库需要匹配数据变化(这非常消耗时间),然后由系统ContentResolver通知自己的ContentObservers(在其他进程)更新数据,然后自己ContentObserver匹配数据库变化,通知UI刷新,我们耗费大量时间在匹配操作。
当然,有很多方法来解决这个问题。


Note : 
-
是否安全取决于你的ContentProvider。请不要试图修改代码(或者在Broadcast中使用),因为如果你未做权限检查,可能会触发数据的安全问题。
-

怎样使用?

在 ContentProvider 查询方法中注册我们的AppContentResolver :
-
```Java
public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		if (AppContentResolver.isCallAppContentResolver(selection)) {
			return AppContentResolver.getAppContentResolverServicer(getContext()).getCursor();
		}
...
	}
```

在其他进程中使用AppContentObserver进行数据接受 :

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
//register AppContentObserver
 AppContentResolver mResolver = AppContentResolver.queryAppContentResolver(Context,Uri);
  if(mResolver != null){
	   mResolver.registerAppContentObserver(AppContentObserver, Uri, isSync);
  }
 ```
 
 包含开源库:[CombinArray](https://github.com/Maizer/MaizerArray/blob/master/CombinArray.java)
 
 开发平台:Eclipse
 
 
[English README](/README.md)
