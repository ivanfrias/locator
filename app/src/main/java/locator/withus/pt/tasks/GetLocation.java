package locator.withus.pt.tasks;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import locator.withus.pt.domain.CommunicationStatus;

/**
 * Created by ivanfrias on 20/12/14.
 */
public class GetLocation extends AsyncTask<String, Integer, Integer>{


    private static final String TAG = GetLocation.class.getName();

    private static final String locatorWsUrl = "http://37.59.87.4:8090/mobile/location";
    public static final String GENDER = "Gender";
    private static final String LATITUDE = "Lat";
    private static final String LONGITUDE = "Lng";
    private static final String ID = "Id";

    @Override
    protected Integer doInBackground(String... params) {
        Integer statusCode = CommunicationStatus.OK.getStatusCode();

        if (params != null && params.length == 3) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(locatorWsUrl);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                String id = UUID.randomUUID().toString();
                nameValuePairs.add(new BasicNameValuePair(ID, id));
                nameValuePairs.add(new BasicNameValuePair(GENDER, params[0]));
                nameValuePairs.add(new BasicNameValuePair(LATITUDE, params[1].replace(".", ",")));
                nameValuePairs.add(new BasicNameValuePair(LONGITUDE, params[2].replace(".", ",")));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                Log.d(TAG, "ID " + nameValuePairs.get(0).getValue());
                Log.d(TAG, "GENDER " + nameValuePairs.get(1).getValue());
                Log.d(TAG, "LATITUDE " + nameValuePairs.get(2).getValue());
                Log.d(TAG, "LONGITUDE " + nameValuePairs.get(3).getValue());

                if (!(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)) {
                    statusCode = CommunicationStatus.NOK.getStatusCode();
                }

            } catch (ClientProtocolException e) {
                Log.e (GetLocation.class.getName(),  "An error occured " + e.getMessage());
            } catch (IOException e) {
                Log.e(GetLocation.class.getName(), "An error occured " + e.getMessage());
            }
        }
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer statusCode) {
        //DO nothing
    }
}
