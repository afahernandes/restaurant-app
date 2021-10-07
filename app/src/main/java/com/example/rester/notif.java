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

public class notif extends Activity {
	String ip="",myPosisi,myLati,myLongi;

	String id_notif;
	String id_notif0="";

	EditText txtstatus;
	EditText txttanggal;
	EditText txtjam;
	EditText txtpesan;

	Button btnProses;
	Button btnHapus;

	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_status = "status";
	private static final String TAG_tanggal= "tanggal";
	private static final String TAG_jam = "jam";
	private static final String TAG_pesan = "pesan";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notif);

		ip=jsonParser.getIP();
		Intent i = getIntent();

		txtstatus= (EditText) findViewById(R.id.txtstatus);
		txttanggal= (EditText) findViewById(R.id.txttanggal);
		txtjam= (EditText) findViewById(R.id.txtJam);
		txtpesan= (EditText) findViewById(R.id.txtPesan);




		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_notif0 = i.getStringExtra("pk");
		id_notif=id_notif0;
		if(id_notif0.length()>0){
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
				String lnotif= txtstatus.getText().toString();
				String lemail= txttanggal.getText().toString();
				String ltelepon= txtjam.getText().toString();
				String lalamat= txtpesan.getText().toString();

				 if(lnotif.length()<1){lengkapi("notif");}
				else if(ltelepon.length()<1){lengkapi("telepon");}
				else if(lemail.length()<1){lengkapi("email");}
				else{
					if(id_notif0.length()>0){
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
			pDialog = new ProgressDialog(notif.this);
			pDialog.setMessage("Load data detail. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		protected String doInBackground(String... params) {
			int sukses;
			try {
				List<NameValuePair> params1 = new ArrayList<NameValuePair>();
				params1.add(new BasicNameValuePair("id_notif", id_notif0));

				String url=ip+"notif/notif_detail.php";
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
								txtstatus.setText(myJSON.getString(TAG_status));
								txttanggal.setText(myJSON.getString(TAG_tanggal));
								txtjam.setText(myJSON.getString(TAG_jam));
								txtpesan.setText(myJSON.getString(TAG_pesan));



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
			pDialog = new ProgressDialog(notif.this);
			pDialog.setMessage("Menyimpan data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		int sukses;

		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lstatus= txtstatus.getText().toString();
			String ltanggal= txttanggal.getText().toString();
			String ljam= txtjam.getText().toString();
			String lpesan= txtpesan.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_notif", id_notif));
			params.add(new BasicNameValuePair("status", lstatus));
			params.add(new BasicNameValuePair("tanggal", ltanggal));
			params.add(new BasicNameValuePair("jam", ljam));
			params.add(new BasicNameValuePair("pesan", lpesan));

			String url=ip+"notif/notif_add.php";
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
			pDialog = new ProgressDialog(notif.this);
			pDialog.setMessage("Mengubah data ...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@SuppressLint("WrongThread")
		protected String doInBackground(String... args) {
			String lnotif= txtstatus.getText().toString();
			String ltanggal= txttanggal.getText().toString();
			String ljam= txtjam.getText().toString();
			String lpesan= txtpesan.getText().toString();

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id_notif", id_notif));
			params.add(new BasicNameValuePair("notif", lnotif));
			params.add(new BasicNameValuePair("tanggal", ltanggal));
			params.add(new BasicNameValuePair("jam", ljam));
			params.add(new BasicNameValuePair("pesan", lpesan));

			String url=ip+"notif/notif_update.php";
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
			pDialog = new ProgressDialog(notif.this);
			pDialog.setMessage("Menghapus data...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		protected String doInBackground(String... args) {
			try {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("id_notif", id_notif0));

				String url=ip+"notif/notif_del.php";
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
