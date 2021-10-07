package com.example.rester;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class GridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.imageTitle = (TextView) row.findViewById(R.id.txtatas);
            holder.imagejarak = (TextView) row.findViewById(R.id.txtjarak);
            holder.imagekat = (TextView) row.findViewById(R.id.txtKategori);
            holder.imageharga = (TextView) row.findViewById(R.id.txtbawah);
            holder.imagestatus = (TextView) row.findViewById(R.id.txtstatus);
            holder.imagerating = (RatingBar) row.findViewById(R.id.ratingbar);
            holder.image = (ImageView) row.findViewById(R.id.image);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }


        ImageItem item = data.get(position);
        holder.imageTitle.setText(item.getTitle());
        holder.imagejarak.setText(item.getJarak().toString()+" Km");
         holder.imagestatus.setText(item.getStatus());
        holder.imageharga.setText(item.getHarga());
        holder.image.setImageBitmap(item.getImage());
        holder.imagerating.setRating(item.getRating());
        return row;
    }

    static class ViewHolder {
        TextView imageTitle;
        TextView imagekat;
        TextView imagejarak;
        TextView imageharga;
        TextView imagestatus;
        ImageView image;
        RatingBar imagerating;
    }
}