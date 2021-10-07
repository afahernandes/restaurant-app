package com.example.rester;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Menu_utama extends Activity {
    SessionManager session;
    String id_customer = "", nama_customer = "", id_reservasi = "";
    String myLati = "-6.353370";
    String myLongi = "106.832349";
    String myPosisi = "-";
    private static final int MY_PERMISSION_REQUEST = 1;


    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private GridView gridView2;
    private GridViewAdapter gridAdapter2;

    private ProgressDialog pDialog;
    JSONParser jParser = new JSONParser();
    JSONArray myJSON = null;
    private static final String TAG_SUKSES = "sukses";
    private static final String TAG_record = "record";

    int jd=5;

    String ip="";
    String[]arid_restaurant;
    String[]arnama_restaurant;
    String[]ardeskripsi;
    String[]aremail;
    String[]aralamat;
    String[]arlatitude;
    String[]arlongitude;
    double []arJarak;
    String[]arstatus;
    String[]argambar;
    Bitmap[]arBitmap;
    int[]arRating;

    int jd2=5;

    String ip2="";
    String[]arid_restaurant2;
    String[]arnama_restaurant2;
    String[]ardeskripsi2;
    String[]aremail2;
    String[]aralamat2;
    String[]arlatitude2;
    String[]arlongitude2;
    double[]arJarak2;
    String[]arstatus2;
    String[]argambar2;
    Bitmap[]arBitmap2;
    int[]arRating2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        ip=jParser.getIP();

        session = new SessionManager(getApplicationContext());
        session.checkLogin();


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Menu_utama.this);
        Boolean Registered = sharedPref.getBoolean("Registered", false);
        if (!Registered) {
            finish();
        } else {
            id_customer = sharedPref.getString("id_customer", "");
            nama_customer = sharedPref.getString("nama_customer", "");
            id_reservasi = sharedPref.getString("id_reservasi", "");
        }

        Log.v("seskod", id_customer);
        Log.v("sesnam", nama_customer);


        CardView menu1Card = (CardView) findViewById(R.id.menu1Card);
        menu1Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, Data_Restoran.class);
                i.putExtra("pk", id_customer);
                i.putExtra("myLati", myLati);
                i.putExtra("myLongi", myLongi);
                startActivity(i);
            }
        });

        CardView menu2Card = (CardView) findViewById(R.id.menu2Card);
        menu2Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, ReservasiList.class);
                i.putExtra("pk", id_customer);
                startActivity(i);
            }
        });
        CardView menu3Card = (CardView) findViewById(R.id.menu3Card);
        menu3Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, ProgressList.class);
                i.putExtra("pk", id_customer);
                startActivity(i);

            }
        }); CardView menu4Card = (CardView) findViewById(R.id.menu4Card);
        menu4Card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, HistoriList.class);
                i.putExtra("pk", id_customer);
                startActivity(i);
            }
        });

        TextView btnsee1 = (TextView) findViewById(R.id.btnsee1);
        btnsee1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, Data_Restoran.class);
                i.putExtra("pk", id_customer);
                i.putExtra("myLati", myLati);
                i.putExtra("myLongi", myLongi);
                startActivity(i);
            }
        });
        TextView btnsee2 = (TextView) findViewById(R.id.btnsee2);
        btnsee2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, Data_RestoranRating.class);
                i.putExtra("pk", id_customer);
                i.putExtra("myLati", myLati);
                i.putExtra("myLongi", myLongi);
                startActivity(i);
            }
        });

        new load().execute();

        ImageView profile_image = (ImageView) findViewById(R.id.profile_image);
        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Menu_utama.this, Profile.class);
                i.putExtra("pk", id_customer);
                i.putExtra("upload","");
                startActivity(i);
            }
        });

//        CardView menu6Card = (CardView) findViewById(R.id.menu6Card);
//        menu6Card.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                session.logout();
//                finish();
//            }
//        });


        LocationManager locationManager;
        String context = Context.LOCATION_SERVICE;
        locationManager = (LocationManager) getSystemService(context);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);

        //meminta izin penyimpanan
        if (ContextCompat.checkSelfPermission(Menu_utama.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Menu_utama.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(Menu_utama.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(Menu_utama.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
            }
        } else {
            //DO NOTHING
        }


        Location location = locationManager.getLastKnownLocation(provider);
        updateWithNewLocation(location);

        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider){
            updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider){ }
        public void onStatusChanged(String provider, int status,
                                    Bundle extras){ }
    };

    private void updateWithNewLocation(Location location) {
        double latitude=Double.parseDouble(myLati);
        double longitude=Double.parseDouble(myLongi);
        String addressString = "No address found";

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Geocoder gc = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);

                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");

                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());
                }
                addressString = sb.toString();
            } catch (IOException e) {}
        } else {
            myLati="-6.353370";
            myLongi="106.832349";
            addressString="Posisi awal";
        }

        myPosisi=addressString;
        myLati=String.valueOf(latitude);
        myLongi=String.valueOf(longitude);


//        TextView  txtMarquee=(TextView)findViewById(R.id.txtMarquee);
//        txtMarquee.setSelected(true);
//        String kata="Posisi Anda :"+myLati+"/"+myLongi+" "+myPosisi+"#";
//        String kalimat=String.format("%1$s", TextUtils.htmlEncode(kata));
//        txtMarquee.setText(Html.fromHtml(kalimat+kalimat+kalimat));
    }



    public void keluar(){
        new AlertDialog.Builder(this)
                .setTitle("Menutup Aplikasi")
                .setMessage("Terimakasih... Anda Telah Menggunakan Aplikasi Ini")
                .setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                        finish();
                    }})
                .show();
    }
    public void keluarYN(){
        AlertDialog.Builder ad=new AlertDialog.Builder(Menu_utama.this);
        ad.setTitle("Konfirmasi");
        ad.setMessage("Apakah benar ingin keluar?");

        ad.setPositiveButton("OK",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
               session.logout();
            }});

        ad.setNegativeButton("No",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface arg0, int arg1) {
            }});

        ad.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keluarYN();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    //============================================================================================== Data resto1

    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        //TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < 5; i++) {//jd/imgs.length()
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),arBitmap[i]);// imgs.getResourceId(i, -1)
          imageItems.add(new ImageItem(arBitmap[i], arnama_restaurant[i],aremail[i],aralamat[i],arJarak[i],arstatus[i],arRating[i]));
        }
        return imageItems;
    }

    class load extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Menu_utama.this);
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

                    for (int i = 0; i < jd; i++) {
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
                        arBitmap[i]=getBitmapFromURL(gb);
                        arJarak[i]=jarak;
                    }


                    boolean flag = true;
                    double temp=0.0;
                    String stemp="";
                    Bitmap btemp;
                    int itemp;
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
                    gridAdapter = new GridViewAdapter(Menu_utama.this, R.layout.card_product, getData());
                    gridView.setAdapter(gridAdapter);

                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                            Intent intent = new Intent(Menu_utama.this, Detail_restaurant.class);
                            intent.putExtra("pk", arid_restaurant[position]);//item.getTitle()
                            intent.putExtra("jarak", String.valueOf(arJarak[position]));//item.getTitle()
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
            });

            new load2().execute();
        }

    }


    //============================================================================================== Data resto1

    private ArrayList<ImageItem> getData2() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        //TypedArray imgs = getResources().obtainTypedArray(R.array.image_ids);
        for (int i = 0; i < jd2; i++) {//jd/imgs.length()
            //Bitmap bitmap = BitmapFactory.decodeResource(getResources(),arBitmap[i]);// imgs.getResourceId(i, -1)
            imageItems.add(new ImageItem(arBitmap2[i], arnama_restaurant2[i],aremail2[i],aralamat2[i],arJarak2[i],arstatus2[i],arRating2[i]));
        }
        return imageItems;
    }

    class load2 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            JSONObject json = jParser.makeHttpRequest(ip+"restaurant/restaurant_show.php", "GET", params);
            // Log.d("show: ", json.toString());
            try {
                int sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {
                    myJSON = json.getJSONArray(TAG_record);
                    jd2=myJSON.length();
                    arid_restaurant2=new String[jd2];
                    arnama_restaurant2=new String[jd2];
                    ardeskripsi2=new String[jd2];
                    aremail2=new String[jd2];
                    aralamat2=new String[jd2];
                    arstatus2=new String[jd2];
                    arlatitude2=new String[jd2];
                    arlongitude2=new String[jd2];
                    argambar2=new String[jd2];
                    arBitmap2=new Bitmap[jd2];
                    arJarak2=new double[jd2];
                    arRating2=new int[jd2];

                    for (int i = 0; i < jd2; i++) {
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



                        arid_restaurant2[i]= c.getString("id_restaurant");
                        arnama_restaurant2[i]= c.getString("nama_restaurant");
                        ardeskripsi2[i]= c.getString("deskripsi");
                        aremail2[i]= c.getString("email");
                        aralamat2[i]= c.getString("alamat");
                        arlatitude2[i]= c.getString("latitude");
                        arlongitude2[i]= c.getString("longitude");
                        arstatus2[i]= c.getString("status");
                        argambar2[i]= c.getString("gambar");
                        arRating2[i]= c.getInt("rating");
                        String gb=ip+"ypathfile/"+ argambar2[i];
                        Log.v("URL",gb);
                        arBitmap2[i]=getBitmapFromURL(gb);
                        arJarak2[i]=jarak;
                    }


                    boolean flag = true;
                    int temp=0;
                    String stemp="";
                    Bitmap btemp;
                    Double itemp;
                    while ( flag ){
                        flag= false;
                        for( int j=0;  j < jd2 -1;  j++ ){
                            if ( arRating2[ j ] < arRating2[j+1] ) {
                                temp = arRating2[ j ];
                                arRating2[ j ] = arRating2[ j+1 ];
                                arRating2[ j+1 ] = temp;

                                stemp=arid_restaurant2[j];
                                arid_restaurant2[ j ] = arid_restaurant2[ j+1 ];
                                arid_restaurant2[ j+1 ] = stemp;

                                stemp=arnama_restaurant2[j];
                                arnama_restaurant2[ j ] = arnama_restaurant2[ j+1 ];
                                arnama_restaurant2[ j+1 ] = stemp;

                                stemp=ardeskripsi2[j];
                                ardeskripsi2[ j ] = ardeskripsi2[ j+1 ];
                                ardeskripsi2[ j+1 ] = stemp;

                                stemp=aremail2[j];
                                aremail2[ j ] = aremail2[ j+1 ];
                                aremail2[ j+1 ] = stemp;

                                stemp=aralamat2[j];
                                aralamat2[ j ] = aralamat2[ j+1 ];
                                aralamat2[ j+1 ] = stemp;

                                itemp=arJarak2[j];
                                arJarak2[ j ] = arJarak2[ j+1 ];
                                arJarak2[ j+1 ] = itemp;

                                btemp=arBitmap2[j];
                                arBitmap2[ j ] = arBitmap2[ j+1 ];
                                arBitmap2[ j+1 ] = btemp;
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
       //     pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    gridView2 = (GridView) findViewById(R.id.gridView2);
                    gridAdapter2 = new GridViewAdapter(Menu_utama.this, R.layout.card_product, getData2());
                    gridView2.setAdapter(gridAdapter2);

                    gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                            ImageItem item = (ImageItem) parent.getItemAtPosition(position);
                            Intent intent = new Intent(Menu_utama.this, Detail_restaurant.class);
                            intent.putExtra("pk", arid_restaurant2[position]);//item.getTitle()
                            intent.putExtra("myLati", myLati);
                            intent.putExtra("jarak", String.valueOf(arJarak2[position]));//item.getTitle()
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
            });

            new load2().execute();
        }

    }
//===================================================================================================================

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

}
