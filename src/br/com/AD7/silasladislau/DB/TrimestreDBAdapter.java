package br.com.AD7.silasladislau.DB;

import br.com.AD7.silasladislau.Trimestre;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;


public class TrimestreDBAdapter extends DBAdapter{
	// campos do banco
	public static final String ROWID = "_id";
	public static final String TITULO = "titulo";
	public static final String ORDEM_TRIMESTRE = "ordem_trimestre";
	public static final String ANO = "ano";
	public static final String CAPA = "capa";
	private static final String BD_TABELA = "trimestre";
	
	public TrimestreDBAdapter(Context contexto)
	{
		super(contexto);
	}	
	
	private long add(Trimestre trimestre)
	{
		//verifica se o registro já exite no banco
		if(this.trimestre(trimestre.getOrdemTrimestre(), trimestre.getAno()) != null) {
			Log.w(getClass().getName(), "O trimestre já existe e não será gravado.");
			return -1;
		}
		
		ContentValues valores = new ContentValues();
		valores.put(TITULO, trimestre.getTitulo());
		valores.put(ORDEM_TRIMESTRE, trimestre.getOrdemTrimestre());
		valores.put(ANO, trimestre.getAno());
		valores.put(CAPA, trimestre.getCapa());
		Log.i(getClass().getName(), "Gravando: " + trimestre.toString());
		
		return bancoDados.insert(BD_TABELA, null, valores);
	}
	/**
	 * Grava trimestre no banco se não existir
	 * @param trimestre
	 * @return id ou -1 em caso não grave no bd
	 */
	public long addTrimestre(Trimestre trimestre) {
		try {
			abrir();
			return add(trimestre);
		}
		finally {
			fechar();
		}
	}
	
	private Trimestre trimestre(int ordemTrimestre, int ano){
		Cursor c = bancoDados.query(true, BD_TABELA, new String[]{ROWID,TITULO,ORDEM_TRIMESTRE,ANO,CAPA},
				ORDEM_TRIMESTRE + "=" + ordemTrimestre + " AND ano =" + ano, null, null, null, null, null);
		try {
			if(c.getCount() > 0){
				c.moveToFirst();
				Trimestre trimestre = new Trimestre(c.getLong(0),c.getString(1),
						c.getInt(2),c.getInt(3),c.getBlob(4));
				
				Log.d(getClass().getName(), trimestre.toString());
				return trimestre;
			}
		} catch (SQLException sqle){
			Log.e(getClass().getName(), sqle.toString());
		}
		finally {			
			//fecha o cursor
			c.close();
		}		
		return null;
	}
	/**
	 * Busca o trimeste pela ordem do trimestre ex.: 1 (primeiro) e ano ex.: 2010.
	 * @param int ordem_trimestre
	 * @param int ano
	 * @return Trimestre
	 */
	public Trimestre buscaTrimestre(int ordemTrimestre, int ano) {
		try {
			abrir();
			return trimestre(ordemTrimestre, ano);
		}
		finally {
			fechar();
		}
	}
	
	private Cursor trimestres(int ano){
		try {
			Cursor c = bancoDados.query(true, BD_TABELA, new String[]{ROWID,TITULO,ORDEM_TRIMESTRE,ANO,CAPA},
					"ano =" + ano, null, null, null, null, null);
			return c;
		} catch (SQLException e) {
			Log.e(getClass().getName(), e.toString());
		}
		return null;
	}
	/**
	 * Busca todos os trimestres do BD
	 * @param ano
	 * @return Cursor 
	 */
	public Cursor buscaTrimestres(int ano) {
		try {
			abrir();
			return trimestres(ano);
		}
		finally {
			fechar();
		}
	}
	
	private int trimestreAtualBD(int ano){
		String sql = "SELECT MAX(" + ORDEM_TRIMESTRE + ") AS ultimo_trimestre" +
				"FROM trimestre WHERE ano=" + ano;
		Cursor c = bancoDados.rawQuery(sql, null);
		try {
			if(c.getCount() > 0){
				c.moveToFirst();
				return c.getInt(0);
			}
		} catch (SQLException sqle){
			Log.e(getClass().getName(), sqle.toString());
		}
		finally {			
			c.close();
		}
		return 0;
	}
	/**
	 * Busca a ordem do trimestre mais atual de um determinado ano no banco de dados
	 * ex.: no banco existe o 1 primeiro, 2 segundo e 3 terceiro trimestres cadastrado
	 * neste caso retornaria um inteiro 3, ou seja, o terceiro trimestre é o mais atual.
	 * @param int ano
	 * @return int ordem_trimestre
	 */
	public int buscarTrimestreAtualBD(int ano) {
		try {
			abrir();
			return trimestreAtualBD(ano);
		}
		finally {
			fechar();
		}
	}
}
