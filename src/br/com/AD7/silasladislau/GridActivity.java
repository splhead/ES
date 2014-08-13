package br.com.AD7.silasladislau;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_trim);
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

		@Override
		public View getView(int posicao, View convertView, ViewGroup parent) {
			ViewHolder holder =null;
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.grid_item, null);
				holder = new ViewHolder();
				
				holder.ivCapa = (ImageView) findViewById(R.id.ivCapa);
				holder.tvTitulo = (TextView) findViewById(R.id.tvTitulo);
				holder.tvTrimestre = (TextView) findViewById(R.id.tvTrimestre);
				
				Trimestre trimestre_pos = trimestres.get(posicao);
				
				holder.ivCapa.setImageBitmap(
						BitmapFactory.decodeByteArray(trimestre_pos.getCapa()
								,0,trimestre_pos.getCapa().length));
				
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
