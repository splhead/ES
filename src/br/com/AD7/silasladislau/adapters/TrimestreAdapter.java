package br.com.AD7.silasladislau.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.AD7.silasladislau.R;

public class TrimestreAdapter extends SimpleCursorAdapter {
	private Context ct;
	 
    private int layout;

	@SuppressWarnings("deprecation")
	public TrimestreAdapter(Context context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.ct = context;
		this.layout = layout;
	}

	
	@Override
	public void bindView(View v, Context context, Cursor c) {
		int nCapa = c.getColumnIndex(TrimestreDBAdapter.CAPA);			 
        byte[] tCapa = c.getBlob(nCapa);
        	        
        ImageView ivCapa = (ImageView) v.findViewById(R.id.ivCapa);
        if (ivCapa != null) {
        	ivCapa.setImageBitmap(
        			BitmapFactory.decodeByteArray(tCapa,0,tCapa.length));
        }
        
		int nTitulo = c.getColumnIndex(TrimestreDBAdapter.TITULO);			 
        String tTitulo = c.getString(nTitulo);
        	        
        TextView tvTitulo = (TextView) v.findViewById(R.id.tvTitulo);
        if (tvTitulo != null) {
            tvTitulo.setText(tTitulo);
        }
        
        int nTrimestre = c.getColumnIndex(TrimestreDBAdapter.ORDEM_TRIMESTRE);			 
        String tTrimestre = c.getString(nTrimestre);
        
        int nAno = c.getColumnIndex(TrimestreDBAdapter.ANO);			 
        String tAno = c.getString(nAno);
        
        TextView tvTrimestre = (TextView) v.findViewById(R.id.tvTrimestre);
        if (tvTrimestre != null) {
            tvTrimestre.setText(tTrimestre + "º trimestre de " + tAno);
        }
		//super.bindView(arg0, arg1, arg2);
	}

	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		 Cursor c = getCursor();
		 
	        final LayoutInflater inflater = LayoutInflater.from(context);
	        View v = inflater.inflate(layout, parent, false);
	 
	        int nCapa = c.getColumnIndex(TrimestreDBAdapter.CAPA);			 
	        byte[] tCapa = c.getBlob(nCapa);
	        	        
	        ImageView ivCapa = (ImageView) v.findViewById(R.id.ivCapa);
	        if (ivCapa != null) {
	        	ivCapa.setImageBitmap(
	        			BitmapFactory.decodeByteArray(tCapa,0,tCapa.length));
	        }
	        
			int nTitulo = c.getColumnIndex(TrimestreDBAdapter.TITULO);			 
	        String tTitulo = c.getString(nTitulo);
	        	        
	        TextView tvTitulo = (TextView) v.findViewById(R.id.tvTitulo);
	        if (tvTitulo != null) {
	            tvTitulo.setText(tTitulo);
	        }
	        
	        int nTrimestre = c.getColumnIndex(TrimestreDBAdapter.ORDEM_TRIMESTRE);			 
	        String tTrimestre = c.getString(nTrimestre);
	        
	        int nAno = c.getColumnIndex(TrimestreDBAdapter.ANO);			 
	        String tAno = c.getString(nAno);
	        
	        TextView tvTrimestre = (TextView) v.findViewById(R.id.tvTrimestre);
	        if (tvTrimestre != null) {
	            tvTrimestre.setText(tTrimestre + "º trimestre de " + tAno);
	        }
	 
	        return v;
		//return super.newView(context, cursor, parent);
	}
}
