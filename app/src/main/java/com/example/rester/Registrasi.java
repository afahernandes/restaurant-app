package com.example.rester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Registrasi extends Activity {
	String ip="",myPosisi,myLati,myLongi;

	String id_customer;
	String id_customer0="";

	EditText txtnama_customer;
	EditText txtemail;
	EditText txttelepon;
	EditText txtalamat;
	EditText txtusername;
	EditText txtpassword;

	Button btnProses;
	Button btnHapus;

	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_nama_customer = "nama_customer";
	private static final String TAG_email = "email";
	private static final String TAG_telepon = "telepon";
	private static final String TAG_alamat = "alamat";
	private static final String TAG_username = "username";
	private static final String TAG_password = "password";
	private static final String TAG_keterangan = "keterangan";
	private static final String TAG_status = "status";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registrasi);

		ip=jsonParser.getIP();
		Intent i = getIntent();

		txtnama_customer= (EditText) findViewById(R.id.txtnama_customer);
		txtemail= (EditText) findViewById(R.id.txtemail);



		txttelepon= (EditText) findViewById(R.id.txttelepon);
		txtalamat= (EditText) findViewById(R.id.txtalamat);
		txtusername= (EditText) findViewById(R.id.txtusername);
		txtpassword= (EditText) findViewById(R.id.txtpassword);
	



		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

	
			btnProses.setText("Create Account");
			btnHapus.setVisibility(View.GONE);
		

		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String lnama_customer= txtnama_customer.getText().toString();
				String lemail= txtemail.getText().toString();
				String ltelepon= txttelepon.getText().toString();
				String lalamat= txtalamat.getText().toString();
				String lusername= txtusername.getText().toString();
				String lpassword= txtpassword.getText().toString();
				String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


				if (lemail.matches(emailPattern)){
					{
						if (lnama_customer.length() < 1) {
							lengkapi("Customer Name");
						} else if (ltelepon.length() < 1) {
							lengkapi("Phone Number");
						} else if (lemail.length() < 1) {
							lengkapi("Email");
						} else {

							new save().execute();

						}//else
					}
				 }else{
					lengkapi2("Customer Name");
				}
			}});
//		btnHapus.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View arg0) {
//				new del().execute();
//			}});


	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Registrasi.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_customer", id_customer0));

				String url=ip+"customer/customer_detail.php";
				Log.v("detail",url);
				JSONObject json = jsonParser.makeHttpRequest(url, "GET", params1);
				Log.d("detail", json.toString());
				sukses = json.getInt(TAG_SUKSES);
				if (sukses == 1) {
					JSONArray myObj = json.getJSONArray(TAG_record); // JSON Array
					final JSONObject myJSON = myObj.getJSONObject(0);
					runOnUiThread(new Runnable() {
						public void run() {
							try {
								txtnama_customer.setText(myJSON.getString(TAG_nama_customer));
								txtemail.setText(myJSON.getString(TAG_email));
								txttelepon.setText(myJSON.getString(TAG_telepon));
								txtalamat.setText(myJSON.getString(TAG_alamat));
								txtusername.setText(myJSON.getString(TAG_username));
								txtpassword.setText(myJSON.getString(TAG_password));
							



							}
							catch (JSONException e) {e.printStackTrace();}
						}});
				}
				else{
					// jika id tidak ditemukan
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		protected void onPostExecute(String file_url) {pDialog.dismiss();}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	class save extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Registrasi.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lnama_customer= txtnama_customer.getText().toString();
			String lemail= txtemail.getText().toString();
			String ltelepon= txttelepon.getText().toString();
			String lalamat= txtalamat.getText().toString();
			String lusername= txtusername.getText().toString();
			String lpassword= txtpassword.getText().toString();
		
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("nama_customer", lnama_customer));
			params.add(new BasicNameValuePair("email", lemail));
			params.add(new BasicNameValuePair("telepon", ltelepon));
			params.add(new BasicNameValuePair("alamat", lalamat));
			params.add(new BasicNameValuePair("username", lusername));
			params.add(new BasicNameValuePair("password", lpassword));
			
			String url=ip+"customer/customer_add.php";
			Log.v("add",url);
			JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
			Log.d("add", json.toString());
			try {
				 sukses= json.getInt(TAG_SUKSES);

			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {sukses("Simpan");
			} else { gagal("Simpan");}
		}
	}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public void lengkapi(String item){
		new AlertDialog.Builder(this)
				.setTitle("Invalid")
				.setMessage("Please input "+item +" !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}

	public void lengkapi2(String item){
		new AlertDialog.Builder(this)
				.setTitle("Failed")
				.setMessage("Email Not Valid !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}
	public void sukses(String item){
		new AlertDialog.Builder(this)
				.setTitle("Success")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
						finish();
					}}).show();
	}

	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Failed")
				.setMessage("Failed "+item +" Data !")
				.setNeutralButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}


	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
