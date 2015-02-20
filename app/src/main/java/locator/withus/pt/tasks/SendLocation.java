package locator.withus.pt.tasks;

import android.content.Context;
import android.content.Intent;
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

import locator.withus.pt.domain.CommunicationStatus;
import locator.withus.pt.locator.MapsActivity;

/**
 * Created by ivanfrias on 20/12/14.
 */
public class SendLocation extends AsyncTask<String, Integer, Integer>{

    private String locatorWsUrl = "http://37.59.87.4:8090/mobile/location";
    private String gender = "Gender";
    private String latitude = "Lat";
    private String longitude = "Lng";
    private String id = "Id";
    private Context ctx;
    private String[] params;

    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";
    public static String GENDER = "gender";

    public SendLocation(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected Integer doInBackground(String... params) {
        Integer statusCode = CommunicationStatus.OK.getStatusCode();
        this.params = params;

        if (params != null && params.length == 3) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(locatorWsUrl);

            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair(id, "X1"));
                nameValuePairs.add(new BasicNameValuePair(gender, params[0]));
                nameValuePairs.add(new BasicNameValuePair(latitude, params[1].replace(".", ",")));
                nameValuePairs.add(new BasicNameValuePair(longitude, params[2].replace(".", ",")));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);

                nameValuePairs.get(2);
                Log.d(SendLocation.class.getName(), "Parameters Gender " + nameValuePairs.get(1).getValue());
                Log.d(SendLocation.class.getName(), "Parameters Latitude " + nameValuePairs.get(2).getValue());
                Log.d(SendLocation.class.getName(), "Parameters Longitude " + nameValuePairs.get(3).getValue());

                if (!(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)) {
                    statusCode = CommunicationStatus.NOK.getStatusCode();
                }

            } catch (ClientProtocolException e) {
                Log.e (SendLocation.class.getName(),  "An error occured " + e.getMessage());
            } catch (IOException e) {
                Log.e(SendLocation.class.getName(), "An error occured " + e.getMessage());
            }
        }
        return statusCode;
    }

    @Override
    protected void onPostExecute(Integer statusCode) {
        if(statusCode == CommunicationStatus.OK.getStatusCode()){
            Intent displayMap = new Intent(ctx, MapsActivity.class);
            displayMap.putExtra(GENDER, params[0]);
            displayMap.putExtra(LATITUDE, params[1]);
            displayMap.putExtra(LONGITUDE, params[2]);
            ctx.startActivity(displayMap);
        }
    }
}
