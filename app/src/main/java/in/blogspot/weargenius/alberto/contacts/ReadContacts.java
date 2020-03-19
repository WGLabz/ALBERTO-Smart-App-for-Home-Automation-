package in.blogspot.weargenius.alberto.contacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import in.blogspot.weargenius.alberto.databases.LocalContactsDB;

public class ReadContacts {
    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    String _ID = ContactsContract.Contacts._ID;
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    String DATA = ContactsContract.CommonDataKinds.Email.DATA;
    Cursor contentCursor;
    String contactPersonName;
    ContentResolver contentResolver;
    LocalContactsDB localContactsDB;
    private String phoneNoTemp;
    private String emailId = " ";

    public ReadContacts(Context con) {
        contentResolver = con.getContentResolver();
        localContactsDB = new LocalContactsDB(con);
    }

    public void syncContacts() {
        contactPersonName = "";
        contentCursor = contentResolver.query(CONTENT_URI, null, null, null, null);
        contentCursor.moveToFirst();
        localContactsDB.open();
        if (contentCursor.getCount() > 0) {
            while (contentCursor.moveToNext()) {
                contactPersonName = contentCursor.getString(contentCursor.getColumnIndex(DISPLAY_NAME));
                String contact_id = contentCursor.getString(contentCursor.getColumnIndex(_ID));
                if (contentCursor.getInt(contentCursor.getColumnIndex(HAS_PHONE_NUMBER)) > 0) {
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);
                    if (emailCursor.getCount() > 0)
                        while (emailCursor.moveToNext()) {
                            emailId = emailCursor.getString(emailCursor.getColumnIndex(DATA));
                        }
                    if (phoneCursor.getCount() > 0)
                        while (phoneCursor.moveToNext()) {
                            phoneNoTemp = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)).toString();
                            localContactsDB.addNewContact(contactPersonName, phoneNoTemp, emailId);
                            emailId = " ";
                            Log.d("Call_Manager", "Name : " + contactPersonName + " No " + phoneNoTemp + " EMail " + emailId);
                        }
                    phoneCursor.close();
                }
            }
        }
        localContactsDB.close();
    }
}
