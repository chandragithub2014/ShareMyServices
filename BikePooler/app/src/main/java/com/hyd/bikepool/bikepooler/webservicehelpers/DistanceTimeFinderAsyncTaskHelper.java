package com.hyd.bikepool.bikepooler.webservicehelpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.hyd.bikepool.bikepooler.interfaces.BikePoolerReceiveListener;
import com.hyd.bikepool.bikepooler.interfaces.DistanceFinderReceiveListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by CHANDRASAIMOHAN on 10/23/2015.
 */
public class DistanceTimeFinderAsyncTaskHelper extends AsyncTask<String,Void,String> {
Context ctx;
    private ProgressDialog pd ;
    private DistanceFinderReceiveListener inReceiveListener;
    String url;
    String jsonObject;
    JSONObject requestjsonObject;
    String type="";
    String fromAddress,toAddress;

    public DistanceTimeFinderAsyncTaskHelper(Context ctx, DistanceFinderReceiveListener inReceiveListener, String fromAddress, String toAddress){
        this.ctx=ctx;
        this.inReceiveListener=inReceiveListener;
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
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
        //    HashMap<String,String> jsonResonseHash = parseJSONResponse(response);
            HashMap<String,String> jsonResonseHash = parseMatrixJSONResponse(response);
            inReceiveListener.receiveDistanceDurationHash(jsonResonseHash);

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
        //http://maps.googleapis.com/maps/api/directions/json?origin=fromAddress&destination=toAddress&sensor=false
        // "http://maps.google.com/maps/api/geocode/json?latlng="+latitude+","+longitude+"&sensor=true";
       //

   //     fromAddress = "Sri Laxmi Narasimha Swamy Nilayam K P H B Phase 7 Kukatpally Hyderabad Telangana 500085 India";
   //     toAddress = "Kukatpally Hyderabad Telangana India";
        try {
            String str_origin = "origins=" + URLEncoder.encode(fromAddress, "UTF-8");
            String str_dest = "destinations=" + URLEncoder.encode(toAddress, "UTF-8");

        // Output format
        String output = "json";
// Sensor enabled
       // String sensor = "sensor=false";
            String apikey = "AIzaSyDDV6NVf7bCKELYyrwXHJsN5ugLzzVOAhM";
        String parameters = str_origin+"&"+str_dest+"&"+apikey;
    //    String url = "http://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
            String url = "https://maps.googleapis.com/maps/api/distancematrix/"+output+"?"+parameters;
    //    String url = "http://maps.googleapis.com/maps/api/directions/json?origin=Sri%20Laxmi%20Narasimha%20Swamy%20Nilayam%20K%20P%20H%20B%20Phase%207%20Kukatpally%20Hyderabad%20Telangana%20500085%20India&destination=Kukatpally%20Hyderabad%20Telangana%20India&sensor=false";

        //



   //     String url = "http://maps.googleapis.com/maps/api/directions/json?origin = "+fromAddress+" & destination = " +toAddress+" & sensor=false";


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
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return  result;
    }


    private HashMap<String,String> parseMatrixJSONResponse(String jsonResponse){
        HashMap<String,String> distanceDurationHash = new HashMap<String,String>();
        try{
            JSONObject mainjson = new JSONObject(jsonResponse);
            if(mainjson!=null){
                JSONArray routesJSONArray = mainjson.getJSONArray("rows");
                        if(routesJSONArray!=null && routesJSONArray.length()>0){
                            for(int i = 0 ; i<routesJSONArray.length();i++){
                                JSONObject elem = routesJSONArray.getJSONObject(i);
                                if(elem!=null){
                                    JSONArray legsJSONArray  = elem.getJSONArray("elements");
                                      if(legsJSONArray!=null && legsJSONArray.length()>0){
                                          for(int j=0;j<legsJSONArray.length();j++) {
                                              JSONObject innerElem = legsJSONArray.getJSONObject(j);
                                              if(innerElem!=null){
                                                  JSONObject distanceJSONObject = innerElem.getJSONObject("distance");
                                                  if(distanceJSONObject!=null){
                                                      String distance = distanceJSONObject.getString("text");
                                                      Log.d("DistanceTimeFinder","Distance:::"+distance);
                                                      distanceDurationHash.put("distance",distance);
                                                      int distanceVal  =  distanceJSONObject.getInt("value");
                                                      distanceDurationHash.put("distanceValue",""+distanceVal);

                                                  }
                                                  JSONObject durationJSONObject = innerElem.getJSONObject("duration");
                                                  if(durationJSONObject!=null){
                                                      String duration =  durationJSONObject.getString("text");
                                                      Log.d("DistanceTimeFinder","Duration:::"+duration);
                                                      distanceDurationHash.put("duration", duration);

                                                  }
                                              }
                                          }
                                      }

                                }
                            }
                        }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return  distanceDurationHash;
    }

    private HashMap<String,String> parseJSONResponse(String jsonResponse){
        HashMap<String,String> distanceDurationHash = new HashMap<String,String>();
        try {
            JSONObject mainjson = new JSONObject(jsonResponse);
            if(mainjson!=null) {
                JSONArray routesJSONArray = mainjson.getJSONArray("routes");
                if(routesJSONArray!=null && routesJSONArray.length()>0){
                    for(int i = 0 ; i<routesJSONArray.length();i++){
                        JSONObject elem = routesJSONArray.getJSONObject(i);
                        if(elem!=null){
                            JSONArray legsJSONArray  = elem.getJSONArray("legs");
                            if(legsJSONArray!=null){
                                for(int j = 0;j< legsJSONArray.length();j++){
                                    JSONObject innerElem = legsJSONArray.getJSONObject(j);
                                    if(innerElem!=null){
                                        JSONObject distanceJSONObject = innerElem.getJSONObject("distance");
                                         if(distanceJSONObject!=null){
                                             String distance = distanceJSONObject.getString("text");
                                             Log.d("DistanceTimeFinder","Distance:::"+distance);
                                             distanceDurationHash.put("distance",distance);
                                             int distanceVal  =  distanceJSONObject.getInt("value");
                                             distanceDurationHash.put("distanceValue",""+distanceVal);

                                         }
                                        JSONObject durationJSONObject = innerElem.getJSONObject("duration");
                                        if(durationJSONObject!=null){
                                            String duration =  durationJSONObject.getString("text");
                                            Log.d("DistanceTimeFinder","Duration:::"+duration);
                                            distanceDurationHash.put("duration", duration);

                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return distanceDurationHash;
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
