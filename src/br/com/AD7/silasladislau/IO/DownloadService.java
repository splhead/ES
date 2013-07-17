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

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class DownloadService extends IntentService {

	private int result = Activity.RESULT_CANCELED;

	public DownloadService() {
		super("DownloadService");		
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Uri data = intent.getData();
		String urlPath = intent.getStringExtra("urlpath");
		final DefaultHttpClient client = new DefaultHttpClient();
		final HttpGet getRequest = new HttpGet(urlPath);
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
		String nomeArquivo = data.getLastPathSegment();
		
		FileOutputStream fos = null;
		try {
			HttpResponse response = client.execute(getRequest);
			// check 200 OK for success
			final int statusCode = response.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				Log.w("Download", "Erro " + statusCode
						+ " enquanto baixa a imagem " + urlPath);
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream in = null;
				try {
					// getting contents from the stream
					in = entity.getContent();
					byte[] bytes = leBytes(in);
					fos = openFileOutput(nomeArquivo, MODE_PRIVATE);
					fos.write(bytes);
				} catch (Exception e) {
					// TODO: handle exception
				} finally {
					entity.consumeContent();
					if(fos != null){
						fos.close();
					}
				}				
				// Sucessful finished
				result = Activity.RESULT_OK;
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
			msg.obj = new File(nomeArquivo).getAbsolutePath();
			try {
				messenger.send(msg);
			} catch (android.os.RemoteException e1) {
				Log.w(getClass().getName(), "Falha ao enviar a mensagem", e1);
			}

		}
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
