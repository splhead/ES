package br.com.AD7.silasladislau.adapters;

import java.util.ArrayList;
import java.util.List;

import br.com.AD7.silasladislau.models.Trimestre;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class TrimestreDBAdapter extends DBAdapter {
	// campos do banco
	public static final String ROWID = "_id";
	public static final String TITULO = "titulo";
	public static final String ORDEM_TRIMESTRE = "ordem_trimestre";
	public static final String ANO = "ano";
	public static final String TIPO = "tipo"; //JOVEM OU ADULTO
	public static final String CAPA = "capa";
	private static final String BD_TABELA = "trimestre";

	public TrimestreDBAdapter(Context contexto) {
		super(contexto);
	}

	private long add(Trimestre trimestre) {
		// verifica se o registro já exite no banco
		if (this.trimestre(trimestre.getOrdemTrimestre(), trimestre.getAno(), trimestre.getTipo()) != null) {
			Log.w(getClass().getName(),
					"O trimestre já existe e não será gravado.");
			return trimestre.get_id();
		}

		ContentValues valores = new ContentValues();
		valores.put(TITULO, trimestre.getTitulo());
		valores.put(ORDEM_TRIMESTRE, trimestre.getOrdemTrimestre());
		valores.put(ANO, trimestre.getAno());
		valores.put(TIPO, trimestre.getTipo());
		valores.put(CAPA, trimestre.getCapa());
		Log.i(getClass().getName(), "Gravando: " + trimestre.toString());

		return bancoDados.insert(BD_TABELA, null, valores);
	}

	/**
	 * Grava trimestre no banco se não existir
	 * 
	 * @param trimestre
	 * @return id ou null em caso não grave no bd
	 */
	public long addTrimestre(Trimestre trimestre) {
		try {
			abrir();
			return add(trimestre);
		} finally {
			fechar();
		}
	}

	private Trimestre trimestre(int ordemTrimestre, int ano, int tipo) {
		Cursor c = bancoDados.query(true, BD_TABELA, new String[] { ROWID,
				TITULO, ORDEM_TRIMESTRE, ANO, TIPO, CAPA }, ORDEM_TRIMESTRE + "="
				+ ordemTrimestre + " AND ano =" + ano + " AND tipo=" + tipo
				, null, null, null, null,
				null);
		try {
			if (c.getCount() > 0) {
				c.moveToFirst();
				Trimestre trimestre = new Trimestre(c.getLong(0),
						c.getString(1), c.getInt(2), c.getInt(3), c.getInt(4), c.getBlob(5));

				Log.d(getClass().getName(), trimestre.toString());
				return trimestre;
			}
		} catch (SQLException sqle) {
			Log.e(getClass().getName(), sqle.toString());
		} finally {
			// fecha o cursor
			c.close();
		}
		return null;
	}

	/**
	 * Busca o trimeste pela ordem do trimestre ex.: 1 (primeiro) e ano ex.:
	 * 2010.
	 * 
	 * @param int ordem_trimestre
	 * @param int ano
	 * @return Trimestre
	 */
	public Trimestre buscaTrimestre(int ordemTrimestre, int ano, int tipo) {
		try {
			abrir();
			return trimestre(ordemTrimestre, ano, tipo);
		} finally {
			fechar();
		}
	}

	private Cursor trimestres(int ano, int tipo) {
		try {
			Cursor c = bancoDados.query(true, BD_TABELA, new String[] { ROWID,
					TITULO, ORDEM_TRIMESTRE, ANO, TIPO, CAPA },
					"ano =" + ano + " AND tipo=" + tipo , null,
					null, null, null, null);
			return c;
		} catch (SQLException e) {
			Log.e(getClass().getName(), e.toString());
		}
		return null;
	}

	/**
	 * Busca todos os trimestres do BD
	 * 
	 * @param ano
	 * @return Cursor
	 */
	public Cursor buscaTrimestres(int ano, int tipo) {
		try {
			abrir();
			return trimestres(ano, tipo);
		} finally {
			fechar();
		}
	}

	private List<Trimestre> todosTrimestres(int tipo) {
		List<Trimestre> trimestreList = new ArrayList<Trimestre>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + BD_TABELA 
				+ " WHERE tipo=" + tipo +" ORDER BY "
				+ ANO + " DESC";

		Cursor c = null;
		try {
			c = bancoDados.rawQuery(selectQuery, null);

			// looping through all rows and adding to list
			if (c.getCount() > 0) {
				c.moveToFirst();
				do {
					Trimestre trimestre = new Trimestre();
					trimestre.set_id(c.getLong(c.getColumnIndex(ROWID)));
//					Log.d(getClass().getName(),
//							String.valueOf(trimestre.get_id()));
					trimestre.setTitulo(c.getString(c.getColumnIndex(TITULO)));
//					Log.d(getClass().getName(), trimestre.getTitulo());
					trimestre.setOrdemTrimestre(c.getInt(c
							.getColumnIndex(ORDEM_TRIMESTRE)));
//					Log.d(getClass().getName(),
//							String.valueOf(trimestre.getOrdemTrimestre()));
					trimestre.setAno(c.getInt(c.getColumnIndex(ANO)));
//					Log.d(getClass().getName(),
//							String.valueOf(trimestre.getAno()));
					trimestre.setTipo(c.getInt(c.getColumnIndex(TIPO)));
//					Log.d(getClass().getName(),
//					String.valueOf(trimestre.getTipo()));
					trimestre.setCapa(c.getBlob(c.getColumnIndex(CAPA)));
//					Log.d(getClass().getName(),
//							String.valueOf(trimestre.getCapa()));
					// add na lista
					trimestreList.add(trimestre);
				} while (c.moveToNext());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			c.close();
		}

		// return trimestre list
		return trimestreList;
	}

	/**
	 * Busca todos os trimestres do BD
	 * 
	 * @return List<Trimestre>
	 */
	public List<Trimestre> buscaTodosTrimestres(int tipo) {
		try {
			abrir();
			return todosTrimestres(tipo);
		} finally {
			fechar();
		}
	}

	private int trimestreAtualBD(int ano) {
		String sql = "SELECT MAX(" + ORDEM_TRIMESTRE + ") AS ultimo_trimestre"
				+ "FROM trimestre WHERE ano=" + ano;
		Cursor c = bancoDados.rawQuery(sql, null);
		try {
			if (c.getCount() > 0) {
				c.moveToFirst();
				return c.getInt(0);
			}
		} catch (SQLException sqle) {
			Log.e(getClass().getName(), sqle.toString());
		} finally {
			c.close();
		}
		return 0;
	}

	/**
	 * Busca a ordem do trimestre mais atual de um determinado ano no banco de
	 * dados ex.: no banco existe o 1 primeiro, 2 segundo e 3 terceiro
	 * trimestres cadastrado neste caso retornaria um inteiro 3, ou seja, o
	 * terceiro trimestre é o mais atual.
	 * 
	 * @param int ano
	 * @return int ordem_trimestre
	 */
	public int buscarTrimestreAtualBD(int ano) {
		try {
			abrir();
			return trimestreAtualBD(ano);
		} finally {
			fechar();
		}
	}
}
