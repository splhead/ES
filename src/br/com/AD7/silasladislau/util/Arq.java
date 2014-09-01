package br.com.AD7.silasladislau.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.util.ByteArrayBuffer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.util.Log;

public class Arq {
	Context c;
	boolean existeArmazenamentoExterno = false;
	boolean escreveArmazenamentoExterno = false;
	private BroadcastReceiver mExternalStorageReceiver;
	private File caminho;
	private URL url;
	// baixa um arquivo
	public String download(String endereco)
    {
    	try
    	{
    		url = new URL(endereco);
    		
    		//if(usarArmazenamentoExterno())
    		//{
    		//	caminho = new File(Environment.getExternalStorageDirectory().toString() + "/data/" + c.getPackageName()+ "/imagens/" +
    		//			endereco.substring(endereco.lastIndexOf('/')+1));
    		//}
    		//else
    		//{
    			caminho = new File(c.getFilesDir() + "/imagens/" + endereco.substring(endereco.lastIndexOf('/')+1));
    		//}
    		    		   		
    		long inicio = System.currentTimeMillis();
    		Log.d("Download","começando o download");
    		Log.d("Download","url: " + endereco);
    		URLConnection ucon = url.openConnection();
    		
    		InputStream is = ucon.getInputStream();
    		BufferedInputStream bis = new BufferedInputStream(is);
    		
    		ByteArrayBuffer baf = new ByteArrayBuffer(50);
    		int atual = 0;
    		while ((atual = bis.read()) != -1)
    		{
    			baf.append((byte) atual);
    		}
    		
    		FileOutputStream fos = new FileOutputStream(caminho);
    		fos.write(baf.toByteArray());
    		fos.close();
    		Log.d("DownloadIndice","Download concluído em: " +
    				((System.currentTimeMillis() - inicio) / 1000) + " segundo(s)");
    		
    		return null;
    	}//fim try
    	catch (IOException ioe)
    	{
    		Log.d("DownloadIndice", "Erro " + ioe );    		
    		System.exit(1);
    	}//fim catch 
    	return null;
    }//fim do método download
	
	void atualizarEstadoArmazenamentoExterno() {
	    String status = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(status)) {
	        existeArmazenamentoExterno = 
	         escreveArmazenamentoExterno = true;
	    } else if 
	     (Environment.MEDIA_MOUNTED_READ_ONLY.equals
	     (status)) {
	        existeArmazenamentoExterno = true;
	        escreveArmazenamentoExterno = false;
	    } else {
	        existeArmazenamentoExterno = 
	         escreveArmazenamentoExterno = false;
	    }	    
	}	
	
	@SuppressWarnings("unused")
	private boolean usarArmazenamentoExterno()
	{
		return existeArmazenamentoExterno && escreveArmazenamentoExterno;
	}
	
	void comecarAssistirArmazenamentoExterno() {
	    mExternalStorageReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            Log.i("test", "Storage: " + intent.getData());
	            atualizarEstadoArmazenamentoExterno();
	        }			
	    };
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
	    filter.addAction(Intent.ACTION_MEDIA_REMOVED);
	    c.registerReceiver(mExternalStorageReceiver, filter);
	    atualizarEstadoArmazenamentoExterno();
	}

	void pararAssistirArmazenamentoExterno() {
	    c.unregisterReceiver(mExternalStorageReceiver);
	}
}