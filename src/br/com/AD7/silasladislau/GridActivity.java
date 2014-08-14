package br.com.AD7.silasladislau;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import br.com.AD7.silasladislau.DB.TrimestreDBAdapter;

public class GridActivity extends Activity {
	// private List<Trimestre> trimestres = null;
	private TrimestreDBAdapter dba = new TrimestreDBAdapter(this);
	private final int ANO = 2014;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_trim);

		final List<Trimestre> t = dba.buscaTodosTrimestres();
		//startManagingCursor(c);
		

		final GridView gvCapas = (GridView) findViewById(R.id.gvCapas);
		
		gvCapas.setAdapter(new TrimestreGridAdapter(this, t));
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
			//ImageView imageView;
			
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.grid_item, null);
				holder = new ViewHolder();

				holder.ivCapa = (ImageView) convertView.findViewById(R.id.ivCapa);
				holder.tvTitulo = (TextView) convertView.findViewById(R.id.tvTitulo);
				holder.tvTrimestre = (TextView) convertView.findViewById(R.id.tvTrimestre);

				Trimestre trimestre_pos = trimestres.get(posicao);
				//Log.d(getClass().getName(), trimestre_pos.toString());

				holder.ivCapa.setImageBitmap(BitmapFactory.decodeByteArray(
						trimestre_pos.getCapa(), 0,
						trimestre_pos.getCapa().length));

				holder.tvTitulo.setText(trimestre_pos.getTitulo());

				holder.tvTrimestre.setText(trimestre_pos.getOrdemTrimestre()
						+ "º trimestre de " + trimestre_pos.getAno());

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}
	}
}
