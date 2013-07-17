package br.com.AD7.silasladislau;


import java.io.IOException;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import br.com.AD7.silasladislau.DB.TrimestreDBAdapter;
import br.com.AD7.silasladislau.IO.DownloadService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Principal extends Activity {
	private TrimestreDBAdapter dba = new TrimestreDBAdapter(this);
	private String capa, tmp;
	private int ordem_trimestre, ano = 2012;
	private StringBuilder titulo = new StringBuilder();
		
	private Handler handler = new Handler() {
	    public void handleMessage(Message message) {
	      Object path = message.obj;
	      if (message.arg1 == RESULT_OK && path != null) {
	        Toast.makeText(Principal.this,
	            "Capas baixadas" + path.toString(), Toast.LENGTH_LONG)
	            .show();
	      } else {
	        Toast.makeText(Principal.this, "Falha ao baixar as capas.",
	            Toast.LENGTH_LONG).show();
	      }

	    };
	  };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		atualizaTrimestres(ano);
		// trimestreAtualNoSite(ano);
	}

	/**
	 * Busca informações (título, ordem, capa - imagem) de todos os trimestres
	 * de um determinado ano.
	 * 
	 * @param int ano
	 */
	public void atualizaTrimestres(int ano) {
		// obtem o html do endereço
		Document doc = this.getHtml(ano);
		// pegando do #conteudo por haver erro de sintaxe no html do #trimestre
		Elements trimestres = this.getElements(doc,
				"#trimestres p:matches([t|T]rimestre+)");
		Elements capas = this.getElements(doc, "#trimestres img");

		for (int i = 0; i < trimestres.size(); i++) {
			tmp = trimestres.get(i).text().replace('/', ' ');
			//Log.d("trimestre", tmp);
			StringTokenizer tokens = new StringTokenizer(tmp);
			// 1¤ Trimestre 2011 - A Bíblia e as emoções humanas
			// pega apenas o primeiro char de 4¤ e converte para int
			ordem_trimestre = Integer.parseInt(Character.toString(tokens
					.nextToken().charAt(0)));
			tokens.nextToken(); // pula a palavra Trimestre
			tokens.nextToken(); // pula o ano 2011
			tokens.nextToken(); // pula o "-"
			// junta todas as palavras que formam o título do trimestre.
			while (tokens.hasMoreTokens()) {
				titulo.append(tokens.nextToken() + " ");
			}
			titulo.deleteCharAt(titulo.length() - 1);
			// Log.d("trimestre", String.valueOf(ordem_trimestre));
			// Log.d("trimestre", titulo.toString());
			// obtem o endereço absoluto da imagem no site
			capa = capas.get(i).attr("abs:src");
			
			// baixa a imagem em outro processo
			Intent intent = new Intent(this, DownloadService.class);
			Messenger messenger = new Messenger(handler);
		    intent.putExtra("MESSENGER", messenger);
		    intent.setData(Uri.parse(capa));
		    intent.putExtra("urlpath", capa);
		    startService(intent);
			
			// pega o nome original da imagem da capa
			capa = capa.substring(capa.lastIndexOf("/") + 1);
			//Log.d("capa", capa);
			// GregorianCalendar gc=new GregorianCalendar();
			// gc.set(Integer.parseInt(ano), 0, 1);
			// SimpleDateFormat formatador = new
			// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// String ano_tmp = formatador.format(gc.getTime());

			Trimestre trimestre = new Trimestre(titulo.toString(),
					ordem_trimestre, ano, capa);

			dba.addTrimestre(trimestre);

			// limpa a StringBuilder para o proximo titulo
			titulo.delete(0, titulo.length());
			// limpa a variavel
			capa = null;
		}
	}	

	public Document getHtml(int ano) {		
		String url = "http://www.cpb.com.br/htdocs/periodicos/les" + ano
				+ ".html";
		try {
			// obtem o html do endereço
			Document doc = Jsoup.connect(url).get();
			Log.d("Elementos", "Abrindo " + url);
			// faz a selecao dos elementos desejados com base na query

			return doc;
		} catch (IOException e) {
			Toast.makeText(this, "Erro:" + e.getMessage(), Toast.LENGTH_SHORT).show();
			return null;
		}
	}

	public Elements getElements(Document doc, String query) {
		return doc.select(query);
	}

	/**
	 * Retorna o trimestre mais atual do site ex.: <strong>3</strong> trimestre
	 * 
	 * @param int ano
	 * @return int trimestre_atual
	 */
	public int trimestreAtualNoSite(int ano) {
		int trimestreAtual = 0;
		Document doc = this.getHtml(ano);
		Elements trimestres = this.getElements(doc,
				"#conteudo p:matches([t|T]rimestre+)");
		for (int i = 0; i < trimestres.size(); i++) {
			tmp = trimestres.get(i).text().replace('/', ' ');
			Log.d("trimestreAtual", tmp);
			StringTokenizer tokens = new StringTokenizer(tmp);
			// pega apenas o primeiro char de 4¤ e converte para int
			int t = Integer.parseInt(Character.toString(tokens.nextToken()
					.charAt(0)));
			trimestreAtual = (trimestreAtual < t) ? t : trimestreAtual;
		}
		Log.i(getClass().getName(),
				"Trimestre Atual: " + String.valueOf(trimestreAtual));
		return trimestreAtual;
	}	
}