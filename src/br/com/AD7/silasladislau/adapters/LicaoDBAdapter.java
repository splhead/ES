package br.com.AD7.silasladislau.adapters;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import br.com.AD7.silasladislau.models.Licao;

public class LicaoDBAdapter extends DBAdapter {
	public static final String ROWID = "_id";
	public static final String DATA = "data";
	public static final String NUMERO = "numero";
	public static final String TITULO = "titulo";
	public static final String TRIMESTREID = "trimestreId";
	public static final String CAPA = "capa";
	private static final String BD_TABELA = "licao";

	public LicaoDBAdapter(Context contexto) {
		super(contexto);
	}

	private long add(Licao licao) {
		// verifica se o registro já exite no banco
		if (this.licao(licao.getTrimestreId(), licao.getNumero()) != null) {
			Log.w(getClass().getName(),
					"A lição já existe e não será gravada.");
			return -1;
		}
		//Log.d("adapter", licao.toString());
		ContentValues valores = new ContentValues();
		valores.put(DATA, licao.getData());
		valores.put(TITULO, licao.getTitulo());
		valores.put(NUMERO, licao.getNumero());
		valores.put(TRIMESTREID, licao.getTrimestreId());
		valores.put(CAPA, licao.getCapa());
		Log.i(getClass().getName(), "Gravando: " + licao.toString());
		try {
			return bancoDados.insert(BD_TABELA, null, valores);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Grava a licao no banco
	 * 
	 * @param licao
	 * @return id
	 */
	public long addLicao(Licao licao) {
		try {
			abrir();
			return add(licao);
		} finally {
			fechar();
		}
	}

	private Licao licao(long trimestreID, int numero) {
		Cursor c = bancoDados.query(true, BD_TABELA, 
				new String[] {ROWID, DATA, NUMERO, TITULO, TRIMESTREID, CAPA}
				, TRIMESTREID + "=" + trimestreID + " AND " 
				+ NUMERO + "=" + numero
				, null, null, null, null, null);
		try {			
			if (c.getCount() > 0) {
				c.moveToFirst();
				Licao licao = new Licao(c.getLong(0), c.getString(1), c.getInt(2)
						, c.getString(3), c.getLong(4), c.getBlob(5));
				Log.d(getClass().getName(), licao.toString());
				return licao;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return null;
	}
	
	/**
	 * Busca a lição com base em seu número!
	 * @param número
	 */
	public Licao buscaLicao(long trimestreId, int numero) {
		try {
			abrir();
			return licao(trimestreId, numero);
		} finally {
			fechar();
		}
	}
	
	private List<Licao> licoes(long trimestreID) {
		List<Licao> licoesList = new ArrayList<Licao>();
		Cursor c = bancoDados.query(BD_TABELA, 
				new String[] {ROWID, DATA, NUMERO, TITULO, TRIMESTREID, CAPA}
				, TRIMESTREID + "=" + trimestreID, null, null, null, null);
		try {			
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					Licao licao = new Licao(c.getLong(0), c.getString(1),
							c.getInt(2), c.getString(3), c.getLong(4),
							c.getBlob(5));
					
					licoesList.add(licao);
					Log.d(getClass().getName(), licao.toString());
					
				} while (c.moveToNext());				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			c.close();
		}
		return licoesList;
	}
	
	/**
	 * Busca todos as lições
	 * 
	 * @return List<Licao>
	 */
	public List<Licao> buscaTodasLicoes(long trimestreID) {
		try {
			abrir();
			return licoes(trimestreID);
		} finally {
			fechar();
		}
	}
}
