package br.com.AD7.silasladislau;

import java.io.IOException;
import java.util.StringTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import br.com.AD7.silasladislau.DB.TrimestreDBAdapter;
import br.com.AD7.silasladislau.IO.DownloadService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class Principal extends Activity {
	private TrimestreDBAdapter dba = new TrimestreDBAdapter(this);
	private String capa, tmp;
	private static final int ADULTO = 0, JOVEM = 1;
	private int ordem_trimestre, ano = 2013, tipo = JOVEM; // ano e tipo para
															// teste !!!!
															// remover!!!

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

		if (internetDisponivel(this)) {
			// atualizaTrimestres(tipo, ano);
			// trimestreAtualNoSite(ano);
			obtemLicao();
		} else {
			Toast.makeText(this, "Ops! Sem Internet!", Toast.LENGTH_SHORT)
					.show();
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

	/**
	 * Busca informações (título, ordem, capa - imagem) de todos os trimestres
	 * de um determinado ano.
	 * 
	 * @param int ano
	 */
	public void atualizaTrimestres(int tipo, int ano) {
		String tmp;
		// obtem o html do endereço
		Document html = this.obtemHtml(this.obtemURLTrimestre(tipo, ano));
		// pegando do #conteudo por haver erro de sintaxe no html do #trimestre
		Elements trimestres = this.buscaElementos(html,
				"#trimestres p:matches([t|T]rimestre+)");
		Elements capas = this.buscaElementos(html, "#trimestres img");

		for (int i = 0; i < trimestres.size(); i++) {
			tmp = trimestres.get(i).text().replace('/', ' ');
			// Log.d("trimestre", tmp);
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
			// Log.d("capa", capa);
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

	public void obtemLicao() {
		String url = "http://www.cpb.com.br/htdocs/periodicos/licoes/jovens/2013/lj432013.html";
		Document html = obtemHtml(url);
		Element h1s = buscaElemento(html, "div#conteudo div table tbody tr td h1");
		Log.d("obtemLicao", h1s.text());
		
		Element data_inicial_final = buscaElemento(html,
				"div#conteudo div table tbody tr td div p strong");
		
		Element ilustracao = buscaElemento(html, "div#conteudo p img");		
		Uri urlIlustração = Uri.parse(ilustracao.attr("abs:src"));
		String nomeArquivoIlustracao = urlIlustração.getLastPathSegment();
		Log.d("obtemLicao", urlIlustração.toString() + " " + nomeArquivoIlustracao);
		
		Elements sabado = buscaElementos(html, "div#conteudo blockquote p");
		String textoSabado = "";
		for(Element elt: sabado) {
			textoSabado += elt.html();
		}
		Log.d("obtemLicao", textoSabado);
		/*Element l = buscaElemento(html, "div#conteudo");
		Log.d("obtemLicao", l.html());
		String data = "<html><body>" + textoSabado + "</body></html>";
		WebView wV = (WebView) findViewById(R.id.webView1);
		wV.loadData(data, "text/html", "UTF-8");
		wV.loadUrl(url);*/
		
		
		
		
		Log.d("obtemLicao", data_inicial_final.text());
		
		
		

	}

	/**
	 * Obtem a url do trimestre conforme o tipo e ano.
	 * 
	 * @param tipo
	 *            - ex.: ADULTO ou JOVEM
	 * @param ano
	 *            - ex.: 2012
	 * @return String url
	 */
	private String obtemURLTrimestre(int tipo, int ano) {
		String url = "http://www.cpb.com.br/htdocs/periodicos/les";
		if (tipo == ADULTO) {
			url += ano + ".html";
		} else {
			url += "jovens" + ano + ".html";
		}
		Log.d("obtemURLTrimestre", url);
		return url;
	}

	public Document obtemHtml(String url) {
		try {
			// obtem o html do endereço
			Document doc = Jsoup.connect(url).get();
			Log.d("obtemHTML", "Abrindo " + url);
			// faz a selecao dos elementos desejados com base na query

			return doc;
		} catch (IOException e) {
			Toast.makeText(this, "Erro:" + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
			return null;
		}
	}

	public Elements buscaElementos(Document html, String busca) {
		return html.select(busca);
	}

	public Element buscaElemento(Document html, String busca) {
		return html.select(busca).first();
	}

	/**
	 * Retorna o trimestre mais atual do site ex.: <strong>3</strong> trimestre
	 * 
	 * @param int ano
	 * @return int trimestre_atual
	 */
	public int trimestreAtualNoSite(int tipo, int ano) {
		int trimestreAtual = 0;
		Document doc = this.obtemHtml(obtemURLTrimestre(tipo, ano));
		Elements trimestres = this.buscaElementos(doc,
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