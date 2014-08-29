package br.com.AD7.silasladislau.IO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import br.com.AD7.silasladislau.Extracao;
import android.os.AsyncTask;
import android.util.Log;

public class DocumentDownloadTask extends AsyncTask<String, Void, Document>{
	TarefaConcluidaListener tcl;
	private static final String TAG = "DocumentDownloadTask";
	
	@Override
	protected Document doInBackground(String... url) {
		
		try {
			System.setProperty("http.keepAlive", "false");
			// obtem o html do endereço
			Document doc = Jsoup.connect(url[0]).timeout(10000)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.get();
			
			Log.d(TAG, "Abrindo " + url[0]);

			return doc;
		} catch (Exception e) {			
			Log.e(TAG, "Erro a abrir: " + url[0] + " " + e);			
		}
		return null;
	}

	@Override
	protected void onPostExecute(Document html) {
		if (html != null) {
			tcl.quandoTarefaConcluida(html);
		} 
	}

}
