package br.com.AD7.silasladislau.IO;

import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Util {
	public Util(){		
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
	
	public Bitmap baixaImagem(String url) {
		final DefaultHttpClient cliente = new DefaultHttpClient();
		final HttpGet requisicao = new HttpGet(url);
		
		try {
			
			HttpResponse resposta = cliente.execute(requisicao);
			
			final int codigoStatus = resposta.getStatusLine().getStatusCode();
			
			//deu erro
			if(codigoStatus != HttpStatus.SC_OK) {				
				Log.e("baixaImagem", "Erro " + codigoStatus + 
						" ao baixar a imagem de " + url);
				return null;
			}
			
			final HttpEntity entidade = resposta.getEntity();
			
			if (entidade != null) {
				InputStream is = null;
				try {
					is = entidade.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(is);
					return bitmap;
				} finally {
					if (is != null) {
						is.close();
					}
					entidade.consumeContent();
				}
			}
			
		} catch (Exception e) {
			requisicao.abort();
			Log.e("baixaImagem", "Erro: " + e.toString() +
					" ao baixar imagem de " + url);
		}
		return null;
	}
}