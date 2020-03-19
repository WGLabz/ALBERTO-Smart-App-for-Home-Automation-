package in.blogspot.weargenius.alberto.utilities;

import android.util.Log;

import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oksbwn on 7/19/2015.
 */
public class PostCallToServer extends Thread {
    String ids[];
    String data[];
    String api;
    String responseFromServer = null;

    public PostCallToServer(String server, String[] ids, String[] data) {
        start();
        this.ids = ids;
        this.data = data;
        this.api = server;
    }

    public void run() {
        try {
            HttpParams params = new BasicHttpParams();
            params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
                    HttpVersion.HTTP_1_1);
            HttpClient client = new DefaultHttpClient(params);
            HttpPost post = new HttpPost(api);

            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            for (int i = 0; i < ids.length; i++)
                nameValuePairs.add(new BasicNameValuePair(ids[i], data[i]));

            post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseFromServer = client.execute(post, responseHandler);
            Log.d("Server_Response", responseFromServer);
            /*
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer res = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            res.append(line);
        }
        responseFromServer=res.toString();*/
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
    }

    public String getResponse() {

        return this.responseFromServer;
    }
}
