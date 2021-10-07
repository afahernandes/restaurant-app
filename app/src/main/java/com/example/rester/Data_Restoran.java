package com.example.rester;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Data_Restoran extends Activity {
    private GridView gridView;
    private GridViewAdapter gridAdapter;

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    JSONArray myJSON = null;
    private static final String TAG_SUKSES = "sukses";
    private static final String TAG_record = "record";
    String id_customer = "", nama_customer = "";
    String myLati = "-6.353370";
    String myLongi = "106.832349";
    String myPosisi = "-";

    EditText txtCari;

    int jd=0;

    String ip="";
    String[]arid_restaurant;
    String[]arnama_restaurant;
    String[]ardeskripsi;
    String[]aremail;
    String[]aralamat;
    String[]arstatus;
    String[]arlatitude;
    String[]arlongitude;
    String[]argambar;
    Bitmap []arBitmap;
    double[]arJarak;
    int[]arRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_main);

        Intent i = getIntent();
        id_customer = i.getStringExtra("pk");
        myLati = i.getStringExtra("myLati");
        myLongi = i.getStringExtra("myLongi");


        ip=jParser.getIP();
        new load().execute();

        txtCari=(EditText)findViewById(R.id.txtCari);
        Button btnCari  = findViewById(R.id.btnCari);
        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { String cari= txtCari.getText().toString();


                    new load2().execute();


            }
        });

    }

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        //TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < jd; i++) {//jd/imgs.length()
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),arBitmap[i]);// imgs.getResourceId(i, -1)
            imageItems.add(new ImageItem(arBitmap[i], arnama_restaurant[i],aremail[i],aralamat[i],arJarak[i],arstatus[i],arRating[i]));
        }
        return imageItems;
    }

    class load extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Data_Restoran.this);
            pDialog.setMessage("Load data. Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(ip+"restaurant/restaurant_show.php", "GET", params);
           // Log.d("show: ", json.toString());
            try {
                int sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {
                    myJSON = json.getJSONArray(TAG_record);
                    jd=myJSON.length();
                    arid_restaurant=new String[jd];
                    arnama_restaurant=new String[jd];
                    ardeskripsi=new String[jd];
                    aremail=new String[jd];
                    aralamat=new String[jd];
                    arstatus=new String[jd];
                    arlatitude=new String[jd];
                    arlongitude=new String[jd];
                    argambar=new String[jd];
                    arBitmap=new Bitmap[jd];
                    arJarak=new double[jd];
                    arRating=new int[jd];

                    for (int i = 0; i < myJSON.length(); i++) {
                        JSONObject c = myJSON.getJSONObject(i);

                        String latitude= c.getString("latitude");
                        String longitude = c.getString("longitude");

                        double lat1=Double.parseDouble(myLati);
                        double long1=Double.parseDouble(myLongi);
                        double lat2=Double.parseDouble(latitude);
                        double long2=Double.parseDouble(longitude);

                        double jarak = 0;
                        try{
                            jarak=getJarak(lat1,long1,lat2,long2);
                        }
                        catch(Exception ees){}



                        arid_restaurant[i]= c.getString("id_restaurant");
                        arnama_restaurant[i]= c.getString("nama_restaurant");
                        ardeskripsi[i]= c.getString("deskripsi");
                        aremail[i]= c.getString("email");
                        aralamat[i]= c.getString("alamat");
                        arlatitude[i]= c.getString("latitude");
                        arlongitude[i]= c.getString("longitude");
                        arstatus[i]= c.getString("status");
                        argambar[i]= c.getString("gambar");
                        arRating[i]= c.getInt("rating");

                        String gb=ip+"ypathfile/"+ argambar[i];
                        Log.v("URL",gb);
                        Log.v("Rating",String.valueOf(arRating[i]));
                        arBitmap[i]=getBitmapFromURL(gb);
                        arJarak[i]=jarak;
                    }


                    boolean flag = true;
                    double temp=0.0;
                    String stemp="";
                    int itemp;
                    Bitmap btemp;
                    while ( flag ){
                        flag= false;
                        for( int j=0;  j < jd -1;  j++ ){
                            if ( arJarak[ j ] > arJarak[j+1] ) {
                                temp = arJarak[ j ];
                                arJarak[ j ] = arJarak[ j+1 ];
                                arJarak[ j+1 ] = temp;

                                stemp=arid_restaurant[j];
                                arid_restaurant[ j ] = arid_restaurant[ j+1 ];
                                arid_restaurant[ j+1 ] = stemp;

                                stemp=arnama_restaurant[j];
                                arnama_restaurant[ j ] = arnama_restaurant[ j+1 ];
                                arnama_restaurant[ j+1 ] = stemp;

                                stemp=ardeskripsi[j];
                                ardeskripsi[ j ] = ardeskripsi[ j+1 ];
                                ardeskripsi[ j+1 ] = stemp;

                                stemp=aremail[j];
                                aremail[ j ] = aremail[ j+1 ];
                                aremail[ j+1 ] = stemp;

                                stemp=aralamat[j];
                                aralamat[ j ] = aralamat[ j+1 ];
                                aralamat[ j+1 ] = stemp;

                                itemp=arRating[j];
                                arRating[ j ] = arRating[ j+1 ];
                                arRating[ j+1 ] = itemp;


                                stemp=arstatus[j];
                                arstatus[ j ] = arstatus[ j+1 ];
                                arstatus[ j+1 ] = stemp;

                                btemp=arBitmap[j];
                                arBitmap[ j ] = arBitmap[ j+1 ];
                                arBitmap[ j+1 ] = btemp;
                                flag = true;
                            }
                        }
                    }


                }
            }
            catch (JSONException e) {e.printStackTrace();}
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    gridView = (GridView) findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(Data_Restoran.this, R.layout.card_product, getData());
                    gridView.setAdapter(gridAdapter);

                    gridView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                            Intent intent = new Intent(Data_Restoran.this, Detail_restaurant.class);
                            intent.putExtra("pk", arid_restaurant[position]);//item.getTitle()
                            intent.putExtra("jarak", String.valueOf(arJarak[position]));//item.getTitle()
                            intent.putExtra("myLati", myLati);
                            intent.putExtra("myLongi", myLongi);
                         startActivity(intent);
                        }
                    });
                }
            });}
    }

    class load2 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Data_Restoran.this);
            pDialog.setMessage("Load data. Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            String item=txtCari.getText().toString();
            params.add(new BasicNameValuePair("item",item));
            JSONObject json = jParser.makeHttpRequest(ip+"restaurant/restaurant_show.php", "GET", params);
            // Log.d("show: ", json.toString());
            try {
                int sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {
                    myJSON = json.getJSONArray(TAG_record);
                    jd=myJSON.length();
                    arid_restaurant=new String[jd];
                    arnama_restaurant=new String[jd];
                    ardeskripsi=new String[jd];
                    aremail=new String[jd];
                    aralamat=new String[jd];
                    arstatus=new String[jd];
                    arlatitude=new String[jd];
                    arlongitude=new String[jd];
                    argambar=new String[jd];
                    arBitmap=new Bitmap[jd];
                    arJarak=new double[jd];

                    for (int i = 0; i < myJSON.length(); i++) {
                        JSONObject c = myJSON.getJSONObject(i);

                        String latitude= c.getString("latitude");
                        String longitude = c.getString("longitude");

                        double lat1=Double.parseDouble(myLati);
                        double long1=Double.parseDouble(myLongi);
                        double lat2=Double.parseDouble(latitude);
                        double long2=Double.parseDouble(longitude);

                        double jarak = 0;
                        try{
                            jarak=getJarak(lat1,long1,lat2,long2);
                        }
                        catch(Exception ees){}



                        arid_restaurant[i]= c.getString("id_restaurant");
                        arnama_restaurant[i]= c.getString("nama_restaurant");
                        ardeskripsi[i]= c.getString("deskripsi");
                        aremail[i]= c.getString("email");
                        aralamat[i]= c.getString("alamat");
                        arlatitude[i]= c.getString("latitude");
                        arlongitude[i]= c.getString("longitude");
                        arstatus[i]= c.getString("status");
                        argambar[i]= c.getString("gambar");
                        String gb=ip+"ypathfile/"+ argambar[i];
                        Log.v("URL",gb);
                        arBitmap[i]=getBitmapFromURL(gb);
                        arJarak[i]=jarak;
                    }


                    boolean flag = true;
                    double temp=0.0;
                    String stemp="";
                    Bitmap btemp;
                    while ( flag ){
                        flag= false;
                        for( int j=0;  j < jd -1;  j++ ){
                            if ( arJarak[ j ] > arJarak[j+1] ) {
                                temp = arJarak[ j ];
                                arJarak[ j ] = arJarak[ j+1 ];
                                arJarak[ j+1 ] = temp;

                                stemp=arid_restaurant[j];
                                arid_restaurant[ j ] = arid_restaurant[ j+1 ];
                                arid_restaurant[ j+1 ] = stemp;

                                stemp=arnama_restaurant[j];
                                arnama_restaurant[ j ] = arnama_restaurant[ j+1 ];
                                arnama_restaurant[ j+1 ] = stemp;

                                stemp=ardeskripsi[j];
                                ardeskripsi[ j ] = ardeskripsi[ j+1 ];
                                ardeskripsi[ j+1 ] = stemp;

                                stemp=aremail[j];
                                aremail[ j ] = aremail[ j+1 ];
                                aremail[ j+1 ] = stemp;

                                stemp=aralamat[j];
                                aralamat[ j ] = aralamat[ j+1 ];
                                aralamat[ j+1 ] = stemp;

                                btemp=arBitmap[j];
                                arBitmap[ j ] = arBitmap[ j+1 ];
                                arBitmap[ j+1 ] = btemp;
                                flag = true;
                            }
                        }
                    }


                }
            }
            catch (JSONException e) {e.printStackTrace();}
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    gridView = (GridView) findViewById(R.id.gridView);
                    gridAdapter = new GridViewAdapter(Data_Restoran.this, R.layout.card_product, getData());
                    gridView.setAdapter(gridAdapter);

                    gridView.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                            Intent intent = new Intent(Data_Restoran.this, Detail_restaurant.class);
                            intent.putExtra("pk", arid_restaurant[position]);//item.getTitle()
                            intent.putExtra("jarak", arJarak[position]);//item.getTitle()
                            intent.putExtra("myLati", myLati);
                            intent.putExtra("myLongi", myLongi);
//                            intent.putExtra("title2", ardeskripsi[position]);
//                            intent.putExtra("title3", aremail[position]);
//                            intent.putExtra("title4", arstatus[position]);
//                            intent.putExtra("gambar", argambar[position]);

                            //intent.putExtra("image", item.getImage());
                            startActivity(intent);
                        }
                    });
                }
            });}
    }

    public static double getJarak(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        int meterConversion = 1609;
        double myjr=dist * meterConversion;
        return Math.floor(myjr)/1000;

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}