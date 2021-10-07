package com.example.rester;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Detail_restaurant extends Activity {
	String ip="",myPosisi,myLati,myLongi;
	String gambar="";
	String id_restaurant;
	String id_restaurant0="";

	TextView txtnama_restaurant;
	TextView txtemail;
	TextView txttelepon;
	TextView txtalamat;
	TextView txtdeskripsi;
	TextView txtjarak;
	TextView txtstatus;
	TextView txtkuota;
	String jarak;
	Button btnProses;
	Button btnHapus;


	RatingBar srating;
	int sukses;

	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();

	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";

	private static final String TAG_nama_restaurant = "nama_restaurant";
	private static final String TAG_email = "email";
	private static final String TAG_telepon = "telepon";
	private static final String TAG_alamat = "alamat";
	private static final String TAG_gambar = "gambar";
	private static final String TAG_deskripsi = "deskripsi";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_restoran);

		ip=jsonParser.getIP();
		Intent i = getIntent();
		jarak = i.getStringExtra("jarak");
		myLati = i.getStringExtra("myLati");
		myLongi = i.getStringExtra("myLongi");

		txtnama_restaurant= (TextView) findViewById(R.id.txtnama_restaurant);
		txtemail= (TextView) findViewById(R.id.txtemail);
		txttelepon= (TextView) findViewById(R.id.txttelepon);
		txtalamat= (TextView) findViewById(R.id.txtalamat);
		txtdeskripsi= (TextView) findViewById(R.id.txtdeskripsi);
		txtjarak= (TextView) findViewById(R.id.txtjarak);
		txtstatus= (TextView) findViewById(R.id.txtstatus);
		txtkuota= (TextView) findViewById(R.id.txtkuota);
		srating= (RatingBar) findViewById(R.id.ratingbar);

		txtjarak.setText(jarak+" Km");


		btnProses= (Button) findViewById(R.id.btnproses);
		btnHapus = (Button) findViewById(R.id.btnhapus);

		id_restaurant0 = i.getStringExtra("pk");
		id_restaurant=id_restaurant0;
			new get().execute();
			btnProses.setText("Booking");
			btnHapus.setText("Back");
			btnHapus.setVisibility(View.VISIBLE);


		btnProses.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String lstatus=txtstatus.getText().toString();
				String lkuota=txtkuota.getText().toString();

				if(lstatus.equalsIgnoreCase("Tutup")){
					gagal("Restaurant not open");
				}else if(lkuota.equalsIgnoreCase("0")){
					gagal("Kuota Not Avaliable");
				}else {


					Intent i = new Intent(Detail_restaurant.this, Detail_Reservasi.class);
					i.putExtra("pk", id_restaurant);
					i.putExtra("myLati", myLati);
					i.putExtra("myLongi", myLongi);
					startActivity(i);
				}
			}});
		btnHapus.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			finish();//	new del().execute();
			}});


	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	class get extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Detail_restaurant.this);
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
								gambar=myJSON.getString("gambar");
								txtdeskripsi.setText(myJSON.getString(TAG_deskripsi));
								txtstatus.setText(myJSON.getString("status"));
								txtkuota.setText(myJSON.getString("kuota"));
								srating.setRating(myJSON.getLong("rating"));



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
		protected void onPostExecute(String file_url) {pDialog.dismiss();
			String arUrlFoto=ip+"ypathfile/"+gambar;
			new DownloadImageTask((ImageView) findViewById(R.id.myGambar)).execute(arUrlFoto);

		}
	}
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	

	//fungsi Download image task
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			}
			catch (Exception e) {Log.e("Error", e.getMessage());e.printStackTrace();}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result); }
	}




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
				.setTitle("Failed")
				.setMessage(item)
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
