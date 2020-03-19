package in.blogspot.weargenius.alberto.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDateTime {
    public String[] getDateTime() {
        SimpleDateFormat dateMyFormat = new SimpleDateFormat("dd/MMM/yyyy");
        SimpleDateFormat timeMyFormat = new SimpleDateFormat("HH:mm");

        String[] dateTime = new String[2];
        Date todayDate = new Date();
        dateTime[0] = dateMyFormat.format(todayDate);
        dateTime[1] = timeMyFormat.format(todayDate);

        return dateTime;
    }
}
