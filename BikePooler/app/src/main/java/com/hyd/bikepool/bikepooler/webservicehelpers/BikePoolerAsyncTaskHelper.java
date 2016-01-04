package com.hyd.bikepool.bikepooler.webservicehelpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.hyd.bikepool.bikepooler.interfaces.BikePoolerReceiveListener;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by CHANDRASAIMOHAN on 10/23/2015.
 */
public class BikePoolerAsyncTaskHelper extends AsyncTask<String,Void,String> {
Context ctx;
    private ProgressDialog pd ;
    private BikePoolerReceiveListener inReceiveListener;
    String url;
    String jsonObject;
    JSONObject requestjsonObject;
    String type="";
    double latitude,longitude;

    public BikePoolerAsyncTaskHelper(Context ctx, BikePoolerReceiveListener inReceiveListener,double latitude,double longitude){
        this.ctx=ctx;
        this.inReceiveListener=inReceiveListener;
        this.latitude = latitude;
        this.longitude = longitude;
       // pd.setMessage(type);
        pd = new ProgressDialog(ctx);
        pd.setCancelable(false);
        pd.show();
    }
    @Override
    protected String doInBackground(String... params) {
        String response="";
//        url = params[0];
        response = doGetURLConnection();
        return response;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String response) {
        if(!TextUtils.isEmpty(response)){
            pd.dismiss();
            Log.d("TAG", "GET  Response::::" + response);
            String flag = parseGetServiceResponse(response);
            inReceiveListener.receiveResult(flag);

        }
     //   super.onPostExecute(response);
    }
    private String parseGetServiceResponse(String response){
        String location_string = "";
      //  String location_string;
        try{
           JSONObject jsonObject = new JSONObject(response);
          JSONObject  location = jsonObject.getJSONArray("results").getJSONObject(0);
           location_string = location.getString("formatted_address");
        }
        catch (JSONException e1) {
            e1.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return  location_string;
    }


    private String doGetURLConnection(){
        String result="";
        String url = "http://maps.google.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true";

        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
         //   con.setRequestProperty("User-Agent", USER_AGENT);

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            result = response.toString();
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return  result;
    }
    private String doURLConnectionPost(String loginURL){
        /*Login_Id: "test", Password: "test"
        */

        String response="";
        //do this wherever you are wanting to POST
        URL url;
        HttpURLConnection conn;
        //String loginURL = "https://dev-patientlists.meddata.com/UserLoginService.svc/ValidateUser";
        try{
            url=new URL(loginURL);


            conn=(HttpURLConnection)url.openConnection();
            //set the output to true, indicating you are outputting(uploading) POST data
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);
//once you set the output to true, you don’t really need to set the request method to post, but I’m doing it anyway
            conn.setRequestMethod("POST");
            String authorization = "NLAuth nlauth_account=TSTDRV1265376, nlauth_email=pmobileapp@curiousrubik.com, nlauth_signature=VRWCkvjghu6G, nlauth_role=3";
       //     conn.setFixedLengthStreamingMode(param.getBytes().length);
            conn.setRequestProperty("Authorization", authorization);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*" );
         //   conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.connect();

        /*    JSONObject loginJsonObject = new JSONObject();
            loginJsonObject.put("Login_Id", "test1");
            loginJsonObject.put("Password", "test@123");
            JSONObject requestJsonObject = new JSONObject();
            requestJsonObject.put("request", loginJsonObject);*/
           /* //send the POST out
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(URLEncoder.encode(requestJsonObject.toString(),"UTF-8"));
            out.flush();
            out.close();*/

            // Write serialized JSON data to output stream.
            OutputStream out = new BufferedOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(requestjsonObject.toString());

            // Close streams and disconnect.
            writer.close();
            out.close();
          //  urlConnection.disconnect();


            //build the string to store the response text from the server
          //  String response= “”;

//start listening to the stream
            Log.d("TAG","Response Code:::"+conn.getResponseCode());
            if(conn.getResponseCode() == 200) {
                Scanner inStream = new Scanner(conn.getInputStream());

//process the stream and store it in StringBuilder
                while (inStream.hasNextLine())
                    response += (inStream.nextLine());
            }
        }
        //catch some error
        catch(MalformedURLException ex){
            Toast.makeText(ctx, ex.toString(), Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return response;
    }
}
