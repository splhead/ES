package br.com.AD7.silasladislau.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.AD7.silasladislau.R;
import br.com.AD7.silasladislau.adapters.TrimestreDBAdapter;
import br.com.AD7.silasladislau.models.Trimestre;

public class GridActivity extends ActionBarActivity {
	// private List<Trimestre> trimestres = null;
	private TrimestreDBAdapter dba = new TrimestreDBAdapter(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_trim);
		
		//pega o tipo passado pela atividade principal
		Intent intent = getIntent();
		int tipo = intent.getIntExtra("tipo", 1); //padrao JOVEM
		//Log.d(getClass().getName(), String.valueOf(tipo));

		final List<Trimestre> t = dba.buscaTodosTrimestres(tipo);
		//startManagingCursor(c);
		/*Iterator<Trimestre> i = t.iterator();
		while (i.hasNext()) {
			Trimestre trimestre = (Trimestre) i.next();
			Log.d("grid", trimestre.toString());
		}*/
		

		final GridView gvCapas = (GridView) findViewById(R.id.gvCapas);
		
		gvCapas.setAdapter(new TrimestreGridAdapter(this, t));
		
		/**
         * On Click event for Single Gridview Item
         * */
        /*gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
 
                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullImageActivity.class);
                // passing array index
                i.putExtra("id", position);
                startActivity(i);
            }
        });*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.principal, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.principal, menu); return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class TrimestreGridAdapter extends BaseAdapter {
		private Context mContext;
		private List<Trimestre> trimestres;

		public TrimestreGridAdapter(Context c, List<Trimestre> t) {
			mContext = c;
			trimestres = t;
		}

		@Override
		public int getCount() {
			return trimestres.size();
		}

		@Override
		public Object getItem(int posicao) {
			return trimestres.get(posicao);
		}

		@Override
		public long getItemId(int posicao) {
			return trimestres.indexOf(getItem(posicao));
		}

		private class ViewHolder {
			ImageView ivCapa;
			TextView tvTitulo, tvTrimestre;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int posicao, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.grid_item, null);
				holder = new ViewHolder();
				
			} else {
				holder = (ViewHolder) convertView.getTag();				
			}			

			holder.ivCapa = (ImageView) convertView.findViewById(R.id.ivCapa);
			holder.tvTitulo = (TextView) convertView.findViewById(R.id.tvTitulo);
			holder.tvTrimestre = (TextView) convertView.findViewById(R.id.tvTrimestre);

			Trimestre trimestre_pos = trimestres.get(posicao);
			
			holder.ivCapa.setImageBitmap(BitmapFactory.decodeByteArray(
					trimestre_pos.getCapa(), 0,
					trimestre_pos.getCapa().length));

			holder.tvTitulo.setText(trimestre_pos.getTitulo());

			holder.tvTrimestre.setText(trimestre_pos.getOrdemTrimestre()
					+ "º trimestre de " + trimestre_pos.getAno());

			convertView.setTag(holder);
			return convertView;
		}
	}
}
