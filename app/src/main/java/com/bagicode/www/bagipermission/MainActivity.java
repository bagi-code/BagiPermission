package com.bagicode.www.bagipermission;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Contacts";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        insertDummyContact();
    }

    private void insertDummyContact() {
        // Two operations are needed to insert a new contact.
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>(2);

        // First, set up a new raw contact.
        ContentProviderOperation.Builder op =
                ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null);
        operations.add(op.build());

        // Next, set the name for the contact.
        op = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                .withValue(ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        "__DUMMY CONTACT from runtime permissions sample");
        operations.add(op.build());

        // Apply the operations.
        ContentResolver resolver = getContentResolver();
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, operations);
        } catch (RemoteException e) {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        } catch (OperationApplicationException e) {
            Log.d(TAG, "Could not add a new contact: " + e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        insertDummyContact();
        insertDummyContactWrapper();
    }

    /*
    Android >= S.D.K 23
    Automatically granted permissions
    There is some permission that will be automatically granted at install time
    and will not be able to revoke. We call it Normal Permission (PROTECTION_NORMAL).
    Here is the full list of them:

    android.permission.ACCESS_LOCATION_EXTRA_COMMANDS
    android.permission.ACCESS_NETWORK_STATE
    android.permission.ACCESS_NOTIFICATION_POLICY
    android.permission.ACCESS_WIFI_STATE
    android.permission.ACCESS_WIMAX_STATE
    android.permission.BLUETOOTH
    android.permission.BLUETOOTH_ADMIN
    android.permission.BROADCAST_STICKY
    android.permission.CHANGE_NETWORK_STATE
    android.permission.CHANGE_WIFI_MULTICAST_STATE
    android.permission.CHANGE_WIFI_STATE
    android.permission.CHANGE_WIMAX_STATE
    android.permission.DISABLE_KEYGUARD
    android.permission.EXPAND_STATUS_BAR
    android.permission.FLASHLIGHT
    android.permission.GET_ACCOUNTS
    android.permission.GET_PACKAGE_SIZE
    android.permission.INTERNET
    android.permission.KILL_BACKGROUND_PROCESSES
    android.permission.MODIFY_AUDIO_SETTINGS
    android.permission.NFC
    android.permission.READ_SYNC_SETTINGS
    android.permission.READ_SYNC_STATS
    android.permission.RECEIVE_BOOT_COMPLETED
    android.permission.REORDER_TASKS
    android.permission.REQUEST_INSTALL_PACKAGES
    android.permission.SET_TIME_ZONE
    android.permission.SET_WALLPAPER
    android.permission.SET_WALLPAPER_HINTS
    android.permission.SUBSCRIBED_FEEDS_READ
    android.permission.TRANSMIT_IR
    android.permission.USE_FINGERPRINT
    android.permission.VIBRATE
    android.permission.WAKE_LOCK
    android.permission.WRITE_SYNC_SETTINGS
    com.android.alarm.permission.SET_ALARM
    com.android.launcher.permission.INSTALL_SHORTCUT
    com.android.launcher.permission.UNINSTALL_SHORTCUT

    Just simply declare those permissions in AndroidManifest.xml
    and it will work just fine. No need to check for
    the permission listed above since it couldn't be revoked.
    */
}
