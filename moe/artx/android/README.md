# android-apk-installer
Single java file library that helps install apk files through ''InputStream''.

 *  Require android 5.0 and up. (Api level 21)
 *  Compatible up to Android 11. This may vary as new versions are released.
 
 *  *Instructions*
 *  1. Add following to ``AndroidManifest.xml`` inside ``<manifest>`` tag.
```xml
        <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
        <uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES"/>
```
 *  2. Add attribute ``android:launchMode="singleTop"`` to ``<activity>`` tag in ``AndroidManifest.xml``.
 *  3. Add following to ``AndroidManifest.xml`` inside ``<activity>`` tag. The Activity must match Context passed to constructor.
```xml
        <intent-filter>
            <action android:exported="true" android:name="PACKAGE_NAME.SESSION_API_PACKAGE_INSTALLED" />
        </intent-filter>
```
 *  4. Pass ``IntentHandler`` method to Activity.onNewIntent.
 *  5. Invoke installation by calling ``Install`` method.
