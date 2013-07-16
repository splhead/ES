package br.com.AD7.silasladislau;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class LicaoDBAdapter extends DBAdapter {
	public static final String ROWID = "_id";
	public static final String DATA = "data";
	public static final String TITULO = "titulo";
	public static final String TRIMESTREID = "trimestreId";
	private static final String BD_TABELA = "licao";
	
	public LicaoDBAdapter (Context contexto) {
		super(contexto);
	}
	
	private long add(Licao licao) {
		ContentValues valores = new ContentValues();
		valores.put(DATA, licao.getData());
		valores.put(TITULO, licao.getTitulo());
		valores.put(TRIMESTREID, licao.getTrimestreId());
		Log.i(getClass().getName(), "Gravando: " + licao.toString());
		return bancoDados.insert(BD_TABELA, null, valores);
	}
	/**
	 * Grava a licao no banco
	 * @param licao
	 * @return id
	 */
	public long addLicao(Licao licao) {
		try {
			abrir();
			return add(licao);
		}
		finally {
			fechar();
		}
	}
}
