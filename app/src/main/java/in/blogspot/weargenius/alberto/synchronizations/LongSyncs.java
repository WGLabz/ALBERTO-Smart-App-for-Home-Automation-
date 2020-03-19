package in.blogspot.weargenius.alberto.synchronizations;

import android.content.Context;

import in.blogspot.weargenius.alberto.contacts.ReadContacts;
import in.blogspot.weargenius.alberto.foods.SyncFoodDetails;

/**
 * Created by oksbwn on 1/4/2016.
 */
public class LongSyncs {
    public LongSyncs(Context con) {

        ReadContacts rc = new ReadContacts(con);
        rc.syncContacts();
        new SyncFoodDetails(con);
    }
}
