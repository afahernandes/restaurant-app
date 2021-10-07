package com.example.rester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Lupa_pass extends Activity {
	
	
     EditText txtemail,txttelepon;
     String ip="";
     int sukses;
     private ProgressDialog pDialog;
	    JSONParser jsonParser = new JSONParser();

	    private static final String TAG_SUKSES = "sukses";
	    private static final String TAG_record = "record";
	    
	    String id_customer="",nama_customer,email;
	    String user, pass;
	    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lupa_pass);
        ip=jsonParser.getIP();
        
        
        txtemail=(EditText)findViewById(R.id.txtemail);
        txttelepon=(EditText)findViewById(R.id.txttelepon);


        Button btnLogin= (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user=txtemail.getText().toString();
                String pass=txttelepon.getText().toString();
                if(user.length()<1){lengkapi("email");}
                else if(pass.length()<1){lengkapi("telepon");}
                else{
                	new ceklogin().execute();
                         }
                       
                  }

        });


		Button txtBuat=(Button)findViewById(R.id.btnbatal);
		txtBuat.setOnClickListener(new View.OnClickListener() {
		public void onClick(View arg0) {
			finish();
		}});
		
		


    }
    public void gagal(){
        new AlertDialog.Builder(this)
                .setTitle("Gagal")
                .setMessage("Silakan Cek Data Anda Kembali")
                .setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }})
                .show();
    }
  
 


    class ceklogin extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Lupa_pass.this);
            pDialog.setMessage("Proses Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        protected String doInBackground(String... params) {
          
            try {
            	String	lemail=txtemail.getText().toString().trim();
           		String	ltelepon=txttelepon.getText().toString().trim();
           		
                List<NameValuePair> myparams = new ArrayList<NameValuePair>();
                myparams.add(new BasicNameValuePair("email", lemail));
                myparams.add(new BasicNameValuePair("telepon", ltelepon));

                String url=ip+"customer/customer_lupa.php";
                Log.v("detail",url);
                JSONObject json = jsonParser.makeHttpRequest(url, "GET", myparams);
                Log.d("detail", json.toString());
                sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {
                    JSONArray myObj = json.getJSONArray(TAG_record); // JSON Array
                    final JSONObject myJSON = myObj.getJSONObject(0);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {

                            	id_customer=myJSON.getString("id_customer");
                            	nama_customer=myJSON.getString("nama_customer");
                            	email=myJSON.getString("email");
                            	user=myJSON.getString("username");
                            	pass=myJSON.getString("password");
                                    }
                            catch (JSONException e) {e.printStackTrace();}
                        }});
                }
              
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @SuppressLint("NewApi")
		protected void onPostExecute(String file_url) {
        	
        	pDialog.dismiss();
	        Log.v("SUKSES",id_customer);

        	if(sukses==1){
		       berhasil(nama_customer, user, pass);
		       
        	}
        	else{
        		gagal("Login");
        	}
        }
    }

   
    public void berhasil(String nama,String user, String pass){
        new AlertDialog.Builder(this)
                .setTitle("Sukses "+nama)
                .setMessage("Yth "+nama +", Gunakan Username: "+user+" dan Password: "+pass+" Untuk login Anda.....")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
//                        Intent i = new Intent(login.this,MainActivity.class);
//                        i.putExtra("xmlbio", xmlbio);
//                        startActivity(i);
                    	 finish();
                    }})
                .show();
    }

    
        public void lengkapi(String item){
	    	new AlertDialog.Builder(this)
			.setTitle("Lengkapi Data")
			.setMessage("Silakan lengkapi data "+item)
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dlg, int sumthin) {
				}})
			.show();
	    }

	

	  public void gagal(String item){
	    	new AlertDialog.Builder(this)
			.setTitle("Gagal Login")
			.setMessage("Login "+item+" ,, Gagal")
			.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dlg, int sumthin) {
				}})
			.show();
	    }


	  public boolean onKeyDown(int keyCode, KeyEvent event) {
	        if (keyCode == KeyEvent.KEYCODE_BACK) {
	        	finish();
	                return true;
	        }
	    return super.onKeyDown(keyCode, event);
	}  
        
	}  
