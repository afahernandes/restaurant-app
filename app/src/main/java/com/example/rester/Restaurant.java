package com.example.rester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Restaurant extends Activity {
	String ip="",myPosisi,myLati,myLongi;

	String id_restaurant;
	String id_restaurant0="";

	EditText txtnama_restaurant;
	EditText txtemail;
	EditText txttelepon;
	EditText txtalamat;
	EditText txtusername;
	EditText txtpassword;
	EditText txtketerangan;
	EditText txtstatus;
	EditText txtdeskripsi;
	EditText txtgambar;
	EditText txtmax_kuota_harian;

	Button btnProses;
	Button btnHapus;

	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_nama_restaurant = "nama_restaurant";
	private static final String TAG_email = "email";
	private static final String TAG_telepon = "telepon";
	private static final String TAG_alamat = "alamat";
	private static final String TAG_username = "username";
	private static final String TAG_password = "password";
	private static final String TAG_keterangan = "keterangan";
	private static final String TAG_status = "status";
	private static final String TAG_gambar = "gambar";
	private static final String TAG_max_kouta_harian = "max_kouta_harian";
	private static final String TAG_deskripsi = "deskripsi";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.restaurant);

		ip=jsonParser.getIP();
		Intent i = getIntent();

		txtnama_restaurant= (EditText) findViewById(R.id.txtnama_restaurant);
		txtemail= (EditText) findViewById(R.id.txtemail);
		txttelepon= (EditText) findViewById(R.id.txttelepon);
		txtalamat= (EditText) findViewById(R.id.txtalamat);
		txtusername= (EditText) findViewById(R.id.txtusername);
		txtpassword= (EditText) findViewById(R.id.txtpassword);
		txttelepon= (EditText) findViewById(R.id.txttelepon);
		txtketerangan = (EditText) findViewById(R.id.txtketerangan);
		txtstatus = (EditText) findViewById(R.id.txtstatus);
		txtstatus = (EditText) findViewById(R.id.txtgambar);
		txtstatus = (EditText) findViewById(R.id.txtmax_kuota_harian);
		txtstatus = (EditText) findViewById(R.id.txtDeskripsi);




		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_restaurant0 = i.getStringExtra("pk");
		id_restaurant=id_restaurant0;
		if(id_restaurant0.length()>0){
			new get().execute();
			btnProses.setText("Update Data");
			btnHapus.setVisibility(View.VISIBLE);
		}
		else{
			btnProses.setText("Add New Data");
			btnHapus.setVisibility(View.GONE);
		}

		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String lnama_restaurant= txtnama_restaurant.getText().toString();
				String lemail= txtemail.getText().toString();
				String ltelepon= txttelepon.getText().toString();
				String lalamat= txtalamat.getText().toString();
				String lusername= txtusername.getText().toString();
				String lpassword= txtpassword.getText().toString();
				String lketerangan= txtketerangan.getText().toString();
				String lmax_kouta_harian= txtmax_kuota_harian.getText().toString();
				String lgambar= txtgambar.getText().toString();
				String ldeskrpsi= txtdeskripsi.getText().toString();
				
				 if(lnama_restaurant.length()<1){lengkapi("nama_restaurant");}
				else if(ltelepon.length()<1){lengkapi("telepon");}
				else if(lemail.length()<1){lengkapi("email");}
				else{
					if(id_restaurant0.length()>0){
						new update().execute();
					}
					else{
						new save().execute();
					}
				}//else

			}});
		btnHapus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new del().execute();
			}});


	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Restaurant.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_restaurant", id_restaurant0));

				String url=ip+"restaurant/restaurant_detail.php";
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
								txtnama_restaurant.setText(myJSON.getString(TAG_nama_restaurant));
								txtemail.setText(myJSON.getString(TAG_email));
								txttelepon.setText(myJSON.getString(TAG_telepon));
								txtalamat.setText(myJSON.getString(TAG_alamat));
								txtusername.setText(myJSON.getString(TAG_username));
								txtpassword.setText(myJSON.getString(TAG_password));
								txtketerangan.setText(myJSON.getString(TAG_keterangan));
								txtstatus.setText(myJSON.getString(TAG_status));
								txtstatus.setText(myJSON.getString(TAG_max_kouta_harian));
								txtstatus.setText(myJSON.getString(TAG_gambar));
								txtstatus.setText(myJSON.getString(TAG_deskripsi));




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
			pDialog = new ProgressDialog(Restaurant.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lnama_restaurant= txtnama_restaurant.getText().toString();
			String lemail= txtemail.getText().toString();
			String ltelepon= txttelepon.getText().toString();
			String lalamat= txtalamat.getText().toString();
			String lusername= txtusername.getText().toString();
			String lpassword= txtpassword.getText().toString();
			String lketerangan= txtketerangan.getText().toString();
			String lstatus= txtstatus.getText().toString();
			String lmax_kouta_harian= txtmax_kuota_harian.getText().toString();
			String lgambar= txtgambar.getText().toString();
			String ldeskripsi= txtdeskripsi.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_restaurant", id_restaurant));
			params.add(new BasicNameValuePair("nama_restaurant", lnama_restaurant));
			params.add(new BasicNameValuePair("email", lemail));
			params.add(new BasicNameValuePair("telepon", ltelepon));
			params.add(new BasicNameValuePair("alamat", lalamat));
			params.add(new BasicNameValuePair("username", lusername));
			params.add(new BasicNameValuePair("password", lpassword));
			params.add(new BasicNameValuePair("keterangan", lketerangan));
			params.add(new BasicNameValuePair("status", lstatus));
			params.add(new BasicNameValuePair("max_kouta_harian", lmax_kouta_harian));
			params.add(new BasicNameValuePair("gambar", lgambar));
			params.add(new BasicNameValuePair("deskripsi", ldeskripsi));
			String url=ip+"restaurant/restaurant_add.php";
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

	class update extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Restaurant.this);
			pDialog.setMessage("Mengubah data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lnama_restaurant= txtnama_restaurant.getText().toString();
			String lemail= txtemail.getText().toString();
			String ltelepon= txttelepon.getText().toString();
			String lalamat= txtalamat.getText().toString();
			String lusername= txtusername.getText().toString();
			String lpassword= txtpassword.getText().toString();
			String lketerangan= txtketerangan.getText().toString();
			String lstatus= txtstatus.getText().toString();
			String lmax_kouta_harian= txtmax_kuota_harian.getText().toString();
			String lgambar= txtgambar.getText().toString();
			String ldeskripsi= txtdeskripsi.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_restaurant", id_restaurant));
			params.add(new BasicNameValuePair("nama_restaurant", lnama_restaurant));
			params.add(new BasicNameValuePair("email", lemail));
			params.add(new BasicNameValuePair("telepon", ltelepon));
			params.add(new BasicNameValuePair("alamat", lalamat));
			params.add(new BasicNameValuePair("username", lusername));
			params.add(new BasicNameValuePair("password", lpassword));
			params.add(new BasicNameValuePair("keterangan", lketerangan));
			params.add(new BasicNameValuePair("status", lstatus));
			params.add(new BasicNameValuePair("max_kouta_harian", lmax_kouta_harian));
			params.add(new BasicNameValuePair("gambar", lgambar));
			params.add(new BasicNameValuePair("status", lstatus));

			String url=ip+"restaurant/restaurant_update.php";
			Log.v("update",url);
			JSONObject json = jsonParser.makeHttpRequest(url,"POST", params);
			Log.d("add", json.toString());
			try {
				 sukses = json.getInt(TAG_SUKSES);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {sukses("Update");
			} else { gagal("Update");}
		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	class del extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Restaurant.this);
			pDialog.setMessage("Menghapus data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id_restaurant", id_restaurant0));

				String url=ip+"restaurant/restaurant_del.php";
				Log.v("delete",url);
				JSONObject json = jsonParser.makeHttpRequest(url, "GET", params);
				Log.d("delete", json.toString());
				sukses = json.getInt(TAG_SUKSES);

			}
			catch (JSONException e) {e.printStackTrace();}
			return null;
		}

		protected void onPostExecute(String file_url) {pDialog.dismiss();
			if (sukses == 1) {sukses("Hapus");
			} else { gagal("Hapus");}}
	}
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	public void lengkapi(String item){
		new AlertDialog.Builder(this)
				.setTitle("Lengkapi Data")
				.setMessage("Silakan lengkapi data "+item +" !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {

					}}).show();
	}

	public void sukses(String item){
		new AlertDialog.Builder(this)
				.setTitle("Sukses")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dlg, int sumthin) {
						finish();
					}}).show();
	}

	public void gagal(String item){
		new AlertDialog.Builder(this)
				.setTitle("Sukses")
				.setMessage("Sukses "+item +" Data !")
				.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
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
