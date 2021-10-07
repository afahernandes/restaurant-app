package com.example.rester;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DetailMenu extends ListActivity {
String ip="",myPosisi,myLati,myLongi;


	EditText txtCari;

	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	JSONArray myJSON = null;
	
	ArrayList<HashMap<String, String>> arrayList;
	private static final String TAG_SUKSES = "sukses";
	private static final String TAG_record = "record";
	
	private static final String TAG_id_user = "id_user";
	private static final String TAG_atas = "atas";
	private static final String TAG_bawah = "bawah";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_listadd);
		arrayList = new ArrayList<HashMap<String, String>>();
		ip=jParser.getIP();

		Intent i = getIntent();

        try{
            arrayList.clear();
        }
        catch(Exception ee){}
		new load().execute();


		txtCari=(EditText)findViewById(R.id.txtCari);

		ListView lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			String pk = ((TextView) view.findViewById(R.id.kode_k)).getText().toString();
			Intent i = new Intent(getApplicationContext(), User.class);
			i.putExtra("pk", pk);
			startActivityForResult(i, 100);
		}});

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), User.class);
                i.putExtra("pk", "");
                startActivityForResult(i, 100);
			}
		});

        Button btnCari  = findViewById(R.id.btnCari);
        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cari= txtCari.getText().toString();

                if(cari.length()<1){lengkapi("Item dicari");}
                else{
                    try{
                        arrayList.clear();
                    }
                    catch(Exception ee){}
                    new load2().execute();
                }//else

            }
        });
}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 100) {// jika result code 100
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}
	}

	class load extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(DetailMenu.this);
			pDialog.setMessage("Load data. Please Wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			JSONObject json = jParser.makeHttpRequest(ip+"user/user_show.php", "GET", params);
			Log.d("show: ", json.toString());
			try {
				int sukses = json.getInt(TAG_SUKSES);
				if (sukses == 1) {
					myJSON = json.getJSONArray(TAG_record);
					for (int i = 0; i < myJSON.length(); i++) {
						JSONObject c = myJSON.getJSONObject(i);
						String id_user= c.getString(TAG_id_user);
						String atas = c.getString(TAG_atas);
						String bawah = c.getString(TAG_bawah);
						
						HashMap<String, String> map = new HashMap<String, String>();
							map.put(TAG_id_user, id_user);
							map.put(TAG_atas, atas);
							map.put(TAG_bawah, bawah);
						
						arrayList.add(map);
					}
				} else {
//					Intent i = new Intent(getApplicationContext(),User.class);
//					i.putExtra("pk", "");
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(i);
				}
			} 
			catch (JSONException e) {e.printStackTrace();}
			return null;
		}

		protected void onPostExecute(String file_url) {
			pDialog.dismiss();
			runOnUiThread(new Runnable() {
				public void run() {
					ListAdapter adapter = new SimpleAdapter(DetailMenu.this, arrayList,R.layout.desain_list, new String[] { TAG_id_user,TAG_atas, TAG_bawah,},new int[] { R.id.kode_k, R.id.txtNamalkp ,R.id.txtDeskripsilkp});
					setListAdapter(adapter);
				}
			});}
	}

    class load2 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DetailMenu.this);
            pDialog.setMessage("Load data. Please Wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
        protected String doInBackground(String... args) {
            @SuppressLint("WrongThread")
            String item=txtCari.getText().toString();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("item", item));

            JSONObject json = jParser.makeHttpRequest(ip+"user/user_show.php", "GET", params);
            Log.d("show: ", json.toString());
            try {
                int sukses = json.getInt(TAG_SUKSES);
                if (sukses == 1) {
                    myJSON = json.getJSONArray(TAG_record);
                    for (int i = 0; i < myJSON.length(); i++) {
                        JSONObject c = myJSON.getJSONObject(i);
                        String id_user= c.getString(TAG_id_user);
                        String atas = c.getString(TAG_atas);
                        String bawah = c.getString(TAG_bawah);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put(TAG_id_user, id_user);
                        map.put(TAG_atas, atas);
                        map.put(TAG_bawah, bawah);

                        arrayList.add(map);
                    }
                } else {
//					Intent i = new Intent(getApplicationContext(),User.class);
//					i.putExtra("pk", "");
//					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//					startActivity(i);
                }
            }
            catch (JSONException e) {e.printStackTrace();}
            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    ListAdapter adapter = new SimpleAdapter(DetailMenu.this, arrayList,R.layout.desain_list, new String[] { TAG_id_user,TAG_atas, TAG_bawah,},new int[] { R.id.kode_k, R.id.txtNamalkp ,R.id.txtDeskripsilkp});
                    setListAdapter(adapter);
                }
            });}
    }


	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:         
        	Intent i = new Intent(getApplicationContext(), User.class);
			i.putExtra("pk", "");
			startActivityForResult(i, 100);
            return true;
        }
        return false;
    }

public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		finish();
		return true;
		}
		return super.onKeyDown(keyCode, event);
		}



    public void lengkapi(String item){
        new AlertDialog.Builder(this)
                .setTitle("Lengkapi Data")
                .setMessage("Silakan lengkapi data "+item +" !")
                .setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                        //finish();
                    }}).show();
    }

}
