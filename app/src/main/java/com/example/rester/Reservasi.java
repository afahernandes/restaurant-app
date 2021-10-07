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

public class Reservasi extends Activity {
	String ip="",myPosisi,myLati,myLongi;

	String id_reservasi;
	String id_reservasi0="";

	EditText txtid_restaurant;
	EditText txtid_customer;
	EditText txttanggal;
	EditText txtjam;
	EditText txtjumlah;
	EditText txtjam_datang;
	EditText txtnomor_meja;
	EditText txtstatus;

	Button btnProses;
	Button btnHapus;

	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_id_restaurant = "id_restaurant";
	private static final String TAG_id_customer = "id_customer";
	private static final String TAG_tanggal = "tanggal";
	private static final String TAG_jam = "jam";
	private static final String TAG_jumlah = "jumlah";
	private static final String TAG_jam_datang = "jam_datang";
	private static final String TAG_nomor_meja = "nomor_meja";
	private static final String TAG_status = "status";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reservasi);

		ip=jsonParser.getIP();
		Intent i = getIntent();

	//	txtid_restaurant= (EditText) findViewById(R.id.txtid_restaurant);
		txtid_customer= (EditText) findViewById(R.id.txttanggal);
		txttanggal= (EditText) findViewById(R.id.txttanggal);
		txtjam= (EditText) findViewById(R.id.txtjumlah);
		txtjumlah= (EditText) findViewById(R.id.txtjumlah);
		txtjam_datang= (EditText) findViewById(R.id.txtjam_datang);
		txttanggal= (EditText) findViewById(R.id.txttanggal);
		txtnomor_meja = (EditText) findViewById(R.id.txttanggal);
		txtstatus = (EditText) findViewById(R.id.txtstatus);




		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_reservasi0 = i.getStringExtra("pk");
		id_reservasi=id_reservasi0;
		if(id_reservasi0.length()>0){
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
				String lid_restaurant= txtid_restaurant.getText().toString();
				String lid_customer= txtid_customer.getText().toString();
				String ltanggal= txttanggal.getText().toString();
				String ljam= txtjam.getText().toString();
				String ljumlah= txtjumlah.getText().toString();
				String ljam_datang= txtjam_datang.getText().toString();
				String lnomor_meja= txtnomor_meja.getText().toString();
				
				 if(lid_restaurant.length()<1){lengkapi("id_restaurant");}
				else if(ltanggal.length()<1){lengkapi("tanggal");}
				else if(lid_customer.length()<1){lengkapi("id_customer");}
				else{
					if(id_reservasi0.length()>0){
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
			pDialog = new ProgressDialog(Reservasi.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_reservasi", id_reservasi0));

				String url=ip+"reservasi/reservasi_detail.php";
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
								txtid_restaurant.setText(myJSON.getString(TAG_id_restaurant));
								txtid_customer.setText(myJSON.getString(TAG_id_customer));
								txttanggal.setText(myJSON.getString(TAG_tanggal));
								txtjam.setText(myJSON.getString(TAG_jam));
								txtjumlah.setText(myJSON.getString(TAG_jumlah));
								txtjam_datang.setText(myJSON.getString(TAG_jam_datang));
								txtnomor_meja.setText(myJSON.getString(TAG_nomor_meja));
								txtstatus.setText(myJSON.getString(TAG_status));




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
			pDialog = new ProgressDialog(Reservasi.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lid_restaurant= txtid_restaurant.getText().toString();
			String lid_customer= txtid_customer.getText().toString();
			String ltanggal= txttanggal.getText().toString();
			String ljam= txtjam.getText().toString();
			String ljumlah= txtjumlah.getText().toString();
			String ljam_datang= txtjam_datang.getText().toString();
			String lnomor_meja= txtnomor_meja.getText().toString();
			String lstatus= txtstatus.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_restaurant", lid_restaurant));
			params.add(new BasicNameValuePair("id_customer", lid_customer));
			params.add(new BasicNameValuePair("tanggal", ltanggal));
			params.add(new BasicNameValuePair("jam", ljam));
			params.add(new BasicNameValuePair("jumlah", ljumlah));
			params.add(new BasicNameValuePair("jam_datang", ljam_datang));
			params.add(new BasicNameValuePair("nomor_meja", lnomor_meja));
			params.add(new BasicNameValuePair("status", lstatus));
			String url=ip+"reservasi/reservasi_add.php";
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
			pDialog = new ProgressDialog(Reservasi.this);
			pDialog.setMessage("Mengubah data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lid_restaurant= txtid_restaurant.getText().toString();
			String lid_customer= txtid_customer.getText().toString();
			String ltanggal= txttanggal.getText().toString();
			String ljam= txtjam.getText().toString();
			String ljumlah= txtjumlah.getText().toString();
			String ljam_datang= txtjam_datang.getText().toString();
			String lnomor_meja= txtnomor_meja.getText().toString();
			String lstatus= txtstatus.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_reservasi", id_reservasi));
			params.add(new BasicNameValuePair("id_restaurant", lid_restaurant));
			params.add(new BasicNameValuePair("id_customer", lid_customer));
			params.add(new BasicNameValuePair("tanggal", ltanggal));
			params.add(new BasicNameValuePair("jam", ljam));
			params.add(new BasicNameValuePair("jumlah", ljumlah));
			params.add(new BasicNameValuePair("jam_datang", ljam_datang));
			params.add(new BasicNameValuePair("nomor_meja", lnomor_meja));
			params.add(new BasicNameValuePair("status", lstatus));

			String url=ip+"reservasi/reservasi_update.php";
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
			pDialog = new ProgressDialog(Reservasi.this);
			pDialog.setMessage("Menghapus data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id_reservasi", id_reservasi0));

				String url=ip+"reservasi/reservasi_del.php";
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
