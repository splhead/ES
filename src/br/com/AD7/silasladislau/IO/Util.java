package br.com.AD7.silasladislau.IO;

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
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
		
		final HttpGet requisicao = new HttpGet(url);
		final HttpParams httpParameters = new BasicHttpParams();
        // Set the timeout in milliseconds until a connection is established.
        HttpConnectionParams.setConnectionTimeout(httpParameters, 7000);

        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        HttpConnectionParams.setSoTimeout(httpParameters, 10000);
        final DefaultHttpClient cliente = new DefaultHttpClient(httpParameters);
		
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
					final Bitmap bitmap = 
							BitmapFactory.decodeStream(new FlushedInputStream(is));
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
	
	class FlushedInputStream extends FilterInputStream {
		 
	    /**
	     * The constructor that takes in the InputStream reference.
	     *
	     * @param inputStream the input stream reference.
	     */
	    public FlushedInputStream(final InputStream inputStream) {
	        super(inputStream);
	    }
	 
	    /**
	     * Overriding the skip method to actually skip n bytes.
	     * This implementation makes sure that we actually skip 
	     * the n bytes no matter what.
	     * {@inheritDoc}
	     */
	    @Override
	    public long skip(final long n) throws IOException {
	        long totalBytesSkipped = 0L;
	        //If totalBytesSkipped is equal to the required number 
	        //of bytes to be skipped i.e. "n"
	        //then come out of the loop.
	        while (totalBytesSkipped < n) {
	            //Skipping the left out bytes.
	            long bytesSkipped = in.skip(n - totalBytesSkipped);
	            //If number of bytes skipped is zero then 
	            //we need to check if we have reached the EOF
	            if (bytesSkipped == 0L) {
	                //Reading the next byte to find out whether we have reached EOF.
	                int bytesRead = read();
	                //If bytes read count is less than zero (-1) we have reached EOF.
	                //Cant skip any more bytes.
	                if (bytesRead < 0) {
	                    break;  // we reached EOF
	                } else {
	                    //Since we read one byte we have actually 
	                    //skipped that byte hence bytesSkipped = 1
	                    bytesSkipped = 1; // we read one byte
	                }
	            }
	            //Adding the bytesSkipped to totalBytesSkipped
	            totalBytesSkipped += bytesSkipped;
	        }        
	        return totalBytesSkipped;
	    }
	}
}