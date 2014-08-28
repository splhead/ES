package br.com.AD7.silasladislau.DB;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {
	private Context contexto;
	private DBHelper dbHelper;	
	protected SQLiteDatabase bancoDados;
	
	public DBAdapter(){
	}
	
	public DBAdapter(Context contexto) {
		this.setContexto(contexto);
	}
	
	public void setContexto(Context contexto){
		this.contexto = contexto;
	}
	
	public DBAdapter abrir() throws SQLException
	{
		dbHelper = new DBHelper(contexto);
		bancoDados  = dbHelper.getWritableDatabase();
		Log.i(getClass().getName(), "Conectando com banco de dados");
		return this;
	}
	
	public void fechar() throws SQLException
	{
		Log.i(getClass().getName(), "Fechando conexão com banco de dados");
		dbHelper.close();
	}
}
