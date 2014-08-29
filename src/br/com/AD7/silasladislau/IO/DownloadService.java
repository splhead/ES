package br.com.AD7.silasladislau.IO;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class DownloadService extends IntentService {
	private byte[] bytes;
	private int result = Activity.RESULT_CANCELED;

	public DownloadService() {
		super("DownloadService");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Uri dados = intent.getData();
		String urlPath = intent.getStringExtra("urlpath");
		
		final HttpGet requisicao = new HttpGet(urlPath);
		final HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, 7000);

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
        final DefaultHttpClient cliente = new DefaultHttpClient(httpParameters);
		// String fileName = data.getLastPathSegment();
		/*
		 * File output = new File(Environment.getExternalStorageDirectory(),
		 * fileName); File diretorio = new File("imagens/");
		 * criaDiretorioImagens(diretorio); String nomeArquivo =
		 * data.getLastPathSegment();
		 * //urlPath.substring(urlPath.lastIndexOf('/') + 1); File arquivo = new
		 * File(diretorio, nomeArquivo); Log.d("Download", nomeArquivo + " ");
		 * 
		 * if (arquivo.exists()) { arquivo.delete(); }
		 */
		String nomeArquivo = dados.getLastPathSegment();
		
		//FileOutputStream fos = null;
		try {			
			if (internetDisponivel(getBaseContext())) {
				HttpResponse resposta = cliente.execute(requisicao);
				// check 200 OK for success
				final int codigoStatus = resposta.getStatusLine()
						.getStatusCode();
				if (codigoStatus != HttpStatus.SC_OK) {
					Log.w("Download", "Erro " + codigoStatus + " ao baixar "
							+ urlPath);
				}
				final HttpEntity entidade = resposta.getEntity();
				if (entidade != null) {
					InputStream in = null;
					try {
						// getting contents from the stream
						in = entidade.getContent();
						bytes = leBytes(in);
						//fos = openFileOutput(nomeArquivo, MODE_PRIVATE);
						//fos.write(bytes);
					} finally {
						entidade.consumeContent();
						/*if(fos != null){
							fos.close();
						}*/
					}
					// Sucessful finished
					result = Activity.RESULT_OK;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		//faz a comunicação com a Activity Principal passando dois parâmetros o result e caminho do arquivo baixado
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Messenger messenger = (Messenger) extras.get("MESSENGER");
			Message msg = Message.obtain();
			msg.arg1 = result;
			//msg.obj = bytes;
			msg.obj = new File(nomeArquivo).getAbsolutePath();
			try {
				messenger.send(msg);
			} catch (android.os.RemoteException e1) {
				Log.w(getClass().getName(), "Falha ao enviar a mensagem", e1);
			}

		}
	}
	
	/**
	 * Verifica se há uma conexão disponível.
	 * 
	 * @param con
	 *            - Contexto
	 * @return True se estiver conectado ou False se não.
	 */
	public Boolean internetDisponivel(Context con) {

		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) con
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo wifiInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobileInfo = connectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (wifiInfo.isConnected() || mobileInfo.isConnected()) {
				Log.d("TestaInternet", "Está conectado.");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("TestaInternet", "Não está conectado.");
		return false;
	}

	/*
	 * private boolean imagemJaExiste(File imagem) { if (imagem.exists() &&
	 * imagem.isFile()) { Log.w(getClass().getName(),
	 * "A imagem já existe no diretório."); return true; } return false; }
	 */

	/*
	 * private boolean diretorioImagensExiste(File diretorio) { return
	 * diretorio.exists(); }
	 */

	/*
	 * private void criaDiretorioImagens(File diretorio) { boolean success =
	 * false; if (!diretorioImagensExiste(diretorio)) { success =
	 * diretorio.mkdirs(); if (!success) { Log.d("Arquivo",
	 * "Erro ao criar o diretorio imagens"); } else { Log.d("Arquivo",
	 * "Criado o diretorio de imagens"); } } }
	 */

	private byte[] leBytes(InputStream in) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				bos.write(buffer, 0, len);
			}
			return bos.toByteArray();
		} finally {
			bos.close();
			in.close();
		}
	}
}
