package com.example.rester;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Login extends Activity {


	String id_reservasi;
	SessionManager session;
	EditText txtusername,txtpassword;
	String ip="";
	int sukses;
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final int MY_PERMISSION_REQUEST = 1;
	String id_customer="",nama_customer,status,id_jamaah,nama_jamaah;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		ip=jsonParser.getIP();


		//meminta izin penyimpanan
		if (ContextCompat.checkSelfPermission(Login.this,
				Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
			} else {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST);
			}
		} else {
			//DO NOTHING
		}

		//meminta izin penyimpanan
		if (ContextCompat.checkSelfPermission(Login.this,
				Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
					Manifest.permission.CAMERA)) {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST);
			} else {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.CAMERA}, MY_PERMISSION_REQUEST);
			}
		} else {
			//DO NOTHING
		}


		//meminta izin penyimpanan
		if (ContextCompat.checkSelfPermission(Login.this,
				Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
					Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
			} else {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
			}
		} else {
			//DO NOTHING
		}

		//meminta izin penyimpanan
		if (ContextCompat.checkSelfPermission(Login.this,
				Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
					Manifest.permission.READ_EXTERNAL_STORAGE)) {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
			} else {
				ActivityCompat.requestPermissions(Login.this,
						new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_REQUEST);
			}
		} else {
			//DO NOTHING
		}
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		session = new SessionManager(getApplicationContext());
		txtusername=(EditText)findViewById(R.id.txtusername);
		txtpassword=(EditText)findViewById(R.id.txtpassword);


		Button btnLogin= (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String customer=txtusername.getText().toString();
				String pass=txtpassword.getText().toString();
				if(customer.length()<1){lengkapi("username");}
				else if(pass.length()<1){lengkapi("Password");}
				else{
//					Intent i=new Intent(Login.this,Menu_utama.class);
//					startActivity(i);
					new ceklogin().execute();
				}

			}

		});

		Button btnRegistrasi= (Button) findViewById(R.id.btndaftar);
		btnRegistrasi.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(Login.this, Registrasi.class);
				i.putExtra("pk", "");
				i.putExtra("upload", "0");
				startActivity(i);
			}

		});
//
		TextView txtlupa= (TextView) findViewById(R.id.txtlupa);
		txtlupa.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent i = new Intent(Login.this, Lupa_pass.class);
				i.putExtra("pk", "");

				startActivity(i);
			}

		});
	}
	public void gagal(){
		new AlertDialog.Builder(this)
				.setTitle("Login Failed")
				.setMessage("Please check your username / password")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}})
				.show();
	}


	class ceklogin extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Login.this);
			pDialog.setMessage("Proses Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {

			try {
				String	username=txtusername.getText().toString().trim();
				String	password=txtpassword.getText().toString().trim();

				List<NameValuePair> myparams = new ArrayList<NameValuePair>();
				myparams.add(new BasicNameValuePair("username", username));
				myparams.add(new BasicNameValuePair("password", password));

				String url=ip+"customer/customer_login.php";
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
								status=myJSON.getString("status");
								id_reservasi=myJSON.getString("id_reservasi");
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
				session.createLoginSession(id_customer,nama_customer);
				final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Login.this);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putBoolean("Registered", true);
				editor.putString("id_customer", id_customer);
				editor.putString("nama_customer", nama_customer);
				editor.putString("id_reservasi", id_reservasi);
				editor.putString("status", status);
				editor.apply();

				Intent i = new Intent(getApplicationContext(),Menu_utama.class);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(i);
				finish();
			}
			else{gagal("Login");	}
		}
	}




	public void lengkapi(String item){
		new AlertDialog.Builder(this)
				.setTitle("Invalid")
				.setMessage("Please complete "+item)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
					}})
				.show();
	}



	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Login Failed")
				.setMessage("Please Check your username/password")
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
