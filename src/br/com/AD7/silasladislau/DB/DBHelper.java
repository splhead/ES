package br.com.AD7.silasladislau.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private static final String NOME_BANCO_DADOS = "escola_sabatina";

	private static final int VERSAO_BANCO_DADOS = 1;

	// Declaracao do SQL de criacao do banco de dados
	private static final String[] SQL_BANCO_DADOS = {
			"CREATE TABLE IF NOT EXISTS trimestre ("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "titulo TEXT NOT NULL, ordem_trimestre INTEGER NOT NULL,"
					+ "ano INTEGER NOT NULL," + "capa BLOB NOT NULL" +
					// "PRIMARY KEY(ordem_trimestre,ano)" +
					");",
			"CREATE TABLE IF NOT EXISTS licao("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "data_inicial NUMERIC NOT NULL,"
					+ "titulo TEXT NOT NULL,"
					+ "capa BLOB NOT NULL,"
					+ "trimestre_id INTEGER NOT NULL,"
					+ "FOREIGN KEY(trimestre_id) REFERENCES trimestre(_id)"
					+ ");",
			"CREATE TABLE IF NOT EXISTS dia("
					+ "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
					+ "dia NUMERIC NOT NULL," + "titulo TEXT NOT NULL,"
					+ "texto TEXT NOT NULL,"
					+ "licao_id INTEGER NOT NULL,"
					+ "FOREIGN KEY(licao_id) REFERENCES licao(_id)" + ");", };

	public DBHelper(Context contexto) {
		super(contexto, NOME_BANCO_DADOS, null, VERSAO_BANCO_DADOS);
	}

	// Método chamado durante a criação do banco de dados
	@Override
	public void onCreate(SQLiteDatabase bancodados) {
		for (int i = 0; i < SQL_BANCO_DADOS.length; i++) {
			try {

				bancodados.beginTransaction();
				bancodados.execSQL(SQL_BANCO_DADOS[i]);
				bancodados.setTransactionSuccessful();

			} finally {
				bancodados.endTransaction();
			}
		}
	}

	// Método chamado durante a atualização do bando de dados, se tiver
	// aumentado o valor
	// da versão do banco de dados
	@Override
	public void onUpgrade(SQLiteDatabase bancodados, int versao_antiga,
			int nova_versao) {
		Log.w(DBHelper.class.getName(),
				"Atualizando o banco de dados da versão " + versao_antiga
						+ " para " + nova_versao
						+ ", que apagará todos os dados da versão antiga");
		bancodados.execSQL("DROP TABLE IF EXISTS trimestre");
		bancodados.execSQL("DROP TABLE IF EXISTS licao");
		bancodados.execSQL("DROP TABLE IF EXISTS dia");
		onCreate(bancodados);
	}

}
