package moe.artx.test.apk;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInstaller;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 2021.04.11 by artistXenon
 *  Require android 5.0 and up. (Api level 21)
 *  Compatible up to Android 11. This may vary as new versions are released.
 *
 *  *Instructions*
 *  1. Add following to AndroidManifest.xml inside manifest tag.
        <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
        <uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES"/>
 *  2. Add attribute android:launchMode="singleTop" to activity tag in AndroidManifest.xml.
 *  3. Add following to AndroidManifest.xml inside activity tag. The activity must match Context passed to constructor.
        <intent-filter>
            <action android:exported="true" android:name="PACKAGE_NAME.SESSION_API_PACKAGE_INSTALLED" />
        </intent-filter>
 *  4. Pass IntentHandler to Activity.onNewIntent
 *  5. Invoke installation by calling Install.
 */

public class Installer {

    private static String PACKAGE_INSTALLED_ACTION_PREFIX = ".SESSION_API_PACKAGE_INSTALLED";

    private final String PACKAGE_INSTALLED_ACTION;
    private final Context context;
    private OnCompleteCallback onComplete;

    public Installer(Context context) {
        this(context, null);
    }
    public Installer(Context context, OnCompleteCallback onComplete) {
        this.context = context;
        this.PACKAGE_INSTALLED_ACTION = context.getPackageName() + PACKAGE_INSTALLED_ACTION_PREFIX;
        this.onComplete = onComplete;
    }

    public synchronized void Install(InputStream is) {
        PackageInstaller.Session session = null;
        try {
            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            int sessionId = packageInstaller.createSession(params);
            session = packageInstaller.openSession(sessionId);
            OutputStream packageInSession = session.openWrite("package", 0, -1);
            byte[] buffer = new byte[16384];
            int n;
            while ((n = is.read(buffer)) >= 0) {
                packageInSession.write(buffer, 0, n);
            }
            packageInSession.close();
            is.close();
            // Create an install status receiver.
            Intent intent = new Intent(context, context.getClass());
            intent.setAction(PACKAGE_INSTALLED_ACTION);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            IntentSender statusReceiver = pendingIntent.getIntentSender();
            // Commit the session (this will start the installation workflow).
            session.commit(statusReceiver);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't install package", e);
        } catch (RuntimeException e) {
            if (session != null) {
                session.abandon();
            }
            throw e;
        }
    }

    public void setIntentHandler(OnCompleteCallback onComplete) {

    }

    public void IntentHandler(Intent intent) {
        Bundle extras = intent.getExtras();
        if (PACKAGE_INSTALLED_ACTION.equals(intent.getAction())) {
            int status = extras.getInt(PackageInstaller.EXTRA_STATUS);
            String message = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE);
            switch (status) {
                case PackageInstaller.STATUS_PENDING_USER_ACTION:
                    // This test app isn't privileged, so the user has to confirm the install.
                    Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
                    context.startActivity(confirmIntent);
                    break;
                case PackageInstaller.STATUS_SUCCESS:
                    if (onComplete != null)
                        onComplete.onSuccess();
                    break;
                /*
                case PackageInstaller.STATUS_FAILURE:
                case PackageInstaller.STATUS_FAILURE_ABORTED:
                case PackageInstaller.STATUS_FAILURE_BLOCKED:
                case PackageInstaller.STATUS_FAILURE_CONFLICT:
                case PackageInstaller.STATUS_FAILURE_INCOMPATIBLE:
                case PackageInstaller.STATUS_FAILURE_INVALID:
                case PackageInstaller.STATUS_FAILURE_STORAGE:
                 */
                default:
                    if (onComplete != null)
                        onComplete.onFail(message);
            }
        }
    }

    public interface OnCompleteCallback {
        void onSuccess();
        void onFail(String Message);
    }
}
