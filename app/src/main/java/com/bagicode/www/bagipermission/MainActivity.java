package com.bagicode.www.bagipermission;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Contacts";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    // Tutorial dialog default permission
//    private void insertDummyContactWrapper() {
//        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
//        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
//        }
//        insertDummyContact();
//    }

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

    // tutorial 2 dialog all permission
//    private void insertDummyContactWrapper() {
//        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
//        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
//            if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)) {
//                showMessageOKCancel("You need to allow access to Contacts",
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
//                                        REQUEST_CODE_ASK_PERMISSIONS);
//                            }
//                        });
//                return;
//            }
//            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
//                    REQUEST_CODE_ASK_PERMISSIONS);
//            return;
//        }
//        insertDummyContact();
//    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    // multi permission
    private void insertDummyContactWrapper() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("GPS");
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add("Read Contacts");
        if (!addPermission(permissionsList, Manifest.permission.WRITE_CONTACTS))
            permissionsNeeded.add("Write Contacts");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);
                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        insertDummyContact();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    insertDummyContact();
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                            .show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            insertDummyContactWrapper();
        } else {
            // Pre-Marshmallow
        }
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
