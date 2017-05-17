package com.oksbwn.readContactsToPC;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.oksbwn.serverActivity.PostCallToServer;

public class SyncContacts extends Service {
    public SyncContacts() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate()
    {
        Toast.makeText(getApplicationContext(),"Started syncing contacts.",Toast.LENGTH_LONG).show();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        String phoneNumber = null;
                        String email = null;
                        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
                        String _ID = ContactsContract.Contacts._ID;
                        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
                        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
                        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
                        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
                        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
                        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
                        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

                        ContentResolver contentResolver = getContentResolver();
                        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
                        String userName="";
                        String mobileNo="";
                        String emailId="";
// Loop for every contact in the phone
                        if (cursor.getCount() > 0) {
                            while (cursor.moveToNext()) {
                                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                                String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
                                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                                if (hasPhoneNumber > 0) {
                                    userName= name;
                                    // Query and loop for every phone number of the contact
                                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);
                                    while (phoneCursor.moveToNext()) {
                                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                                        mobileNo= phoneNumber;
                                    }
                                    phoneCursor.close();
                                    // Query and loop for every email of the contact
                                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,    null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
                                    while (emailCursor.moveToNext()) {
                                        email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                                        emailId= email;
                                    }
                                    emailCursor.close();
                                }
                                PostCallToServer pc=  new PostCallToServer("http://192.168.0.1/smart_home/API/android/syncContacts.php",
                                        new String[]{"NAME","NO","MAIL"},
                                        new String[]{userName,mobileNo,emailId});
                                userName="";
                                mobileNo="";
                                emailId="";
                            }
                        }

                    }
                }
        ).start();
        Toast.makeText(getApplicationContext(),"Syncing contacts completed.",Toast.LENGTH_LONG).show();
    }
}
