package br.com.AD7.silasladislau.activity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutionException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;
import br.com.AD7.silasladislau.R;
import br.com.AD7.silasladislau.adapters.LicaoDBAdapter;
import br.com.AD7.silasladislau.adapters.TrimestreDBAdapter;
import br.com.AD7.silasladislau.models.Licao;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import br.com.AD7.silasladislau.models.Trimestre;
import br.com.AD7.silasladislau.util.Util;

@SuppressLint("HandlerLeak")
public class Principal extends ActionBarActivity {
	private String capa, tmp;
	private TrimestreDBAdapter dbaTrimestre = new TrimestreDBAdapter(this);
	private LicaoDBAdapter dbaLicao = new LicaoDBAdapter(this);
	private long trimestreID;
	private static final int ADULTO = 0, JOVEM = 1;
	private static final boolean DEVELOPER_MODE = true;
	private int ordem_trimestre, ano = 2014, tipo = JOVEM; // ano e tipo para
															// teste !!!!
															// remover!!!
	private Object path;
	private StringBuilder titulo = new StringBuilder();

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			path = message.obj;
			if (message.arg1 == RESULT_OK && path != null) {
				Toast.makeText(Principal.this,
						"Arquivo baixado " + path.toString(), Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(Principal.this,
						"Ops! Falha ao baixar arquivo(s).", Toast.LENGTH_LONG)
						.show();
			}

		};
	};

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (DEVELOPER_MODE) {
	         StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
	                 .detectDiskReads()
	                 .detectDiskWrites()
	                 .detectNetwork()   // or .detectAll() for all detectable problems
	                 .penaltyLog()
	                 .build());
	         StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
	                 .detectLeakedSqlLiteObjects()
	                 .detectLeakedClosableObjects()
	                 .penaltyLog()
	                 .penaltyDeath()
	                 .build());
	     }
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//setContentView(R.layout.activity_grid_trim);
		new TrimestreTask().execute(tipo, ano);
		/*obtemTrimestres(tipo, ano);
		
		Intent intent = new Intent(getApplicationContext(), GridActivity.class);
		intent.putExtra("tipo", tipo);
		startActivity(intent);*/
		

		// if (new Util().internetDisponivel(this)) {
		
		/*
		 * ImageView image = (ImageView) findViewById(R.id.imageView); TextView
		 * titulo = (TextView) findViewById(R.id.titulo);
		 * 
		 * Trimestre trim = dba.buscaTrimestre(3, 2014);
		 * 
		 * 
		 * if(trim.getCapa() != null) { titulo.setText(trim.getTitulo());
		 * Log.d("image", "capa não vazia");
		 * image.setImageBitmap(BitmapFactory.decodeByteArray
		 * (trim.getCapa(),0,trim.getCapa().length)); } //
		 * trimestreAtualNoSite(ano); //obtemLicao(); /* } else {
		 * Toast.makeText(this, "Ops! Sem Internet!", Toast.LENGTH_SHORT)
		 * .show(); }
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.principal, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 * menu; this adds items to the action bar if it is present.
	 * getMenuInflater().inflate(R.menu.principal, menu); return true; }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_refresh:
			// atualizaTrimestres(tipo, ano);
			new TrimestreTask().execute(new Integer [] {tipo, ano});
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Busca informações (título, ordem, capa - imagem) de todos os trimestres
	 * de um determinado ano.
	 * 
	 * @param int ano
	 */
	
	private class baixaImagemTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... urls) {

			return new Util().baixaImagem(urls[0]);

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			ImageView image = (ImageView) findViewById(R.id.imageView);
			image.setImageBitmap(result);
		}
		
		

	}

	private class TrimestreTask extends AsyncTask<Integer, Void, Document> {

		@Override
		protected Document doInBackground(Integer... params) {
			String url = obtemURLTrimestre(params[0], params[1]);
			return obtemHtml(url);

		}

		@Override
		protected void onPostExecute(Document html) {
			//html = new docTask().execute(tipo,ano).get();
			if (html != null) {
				// pegando do #conteudo por haver erro de sintaxe no html do
				// #trimestre
				Elements trimestres = buscaElementos(html,
						"#trimestres p:matches([t|T]rimestre+)");
				Elements capas = buscaElementos(html, "#trimestres img");

				for (int i = 0; i < trimestres.size(); i++) {
										
					tmp = trimestres.get(i).text().replace('/', ' ');
//						Log.d("trimestre", trimestres.get(i).text());
					StringTokenizer tokens = new StringTokenizer(tmp);
					// 1¤ Trimestre de 2011 A Bíblia e as emoções humanas
					// pega apenas o primeiro char de 4¤ e converte para int
					ordem_trimestre = Integer.parseInt(Character
							.toString(tokens.nextToken().charAt(0)));
					tokens.nextToken(); // pula a palavra Trimestre
					tokens.nextToken(); // pula o "de"
					ano = Integer.parseInt(tokens.nextToken()); // pula o "2011"
					// junta todas as palavras que formam o título do trimestre.
					
					//correção do problema
					//<p>3º trimestre de 2014</p>
					//<p>Ensinos de Jesus</p>
					if (tokens.hasMoreTokens()) {
						while (tokens.hasMoreTokens()) {
							titulo.append(tokens.nextToken() + " ");
						}
					} else if (trimestres.get(i).nextElementSibling().hasText()) {
						titulo.append(trimestres.get(i).nextElementSibling().text());
					}
								
					
					// Log.d("trimestre", String.valueOf(ordem_trimestre));
					//Log.d("trimestre", titulo.toString());
					// obtem o endereço absoluto da imagem no site
					capa = capas.get(i).attr("abs:src");

					// baixa a imagem em outro processo
					// byte[] imagemFile = getFile(capa);
					//ImageView image = (ImageView) findViewById(R.id.imageView);
					// image.setImageBitmap(getFile(capa));
					// Bitmap bitmap =
					// BitmapFactory.decodeByteArray(imagemFile,0,imagemFile.length);
					//Bitmap bitmap = null;
					try {
						Bitmap bitmap = new baixaImagemTask().execute(capa).get();//new Util().baixaImagem(capa);
						//image.setImageBitmap(bitmap);
						/*if (bitmap == null) {
							Log.d("capa", "vazio");
						}*/
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						// comprime a imagem para gravar no banco
						bitmap.compress(CompressFormat.PNG, 100, baos);
						
						Trimestre trimestre = new Trimestre(titulo.toString(),
								ordem_trimestre, ano, tipo, baos.toByteArray());
						
						

						trimestreID = dbaTrimestre.addTrimestre(trimestre);

					} catch (Exception e) {
						e.printStackTrace();
					}
					

					/*
					 * Intent intent = new Intent(this, DownloadService.class);
					 * Messenger messenger = new Messenger(handler);
					 * intent.putExtra("MESSENGER", messenger);
					 * intent.setData(Uri.parse(capa));
					 * intent.putExtra("urlpath", capa); startService(intent);
					 */

					// pega o nome original da imagem da capa
					// capa = capa.substring(capa.lastIndexOf("/") + 1);
					// Log.d("capa", capa);
					// GregorianCalendar gc=new GregorianCalendar();
					// gc.set(Integer.parseInt(ano), 0, 1);
					// SimpleDateFormat formatador = new
					// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// String ano_tmp = formatador.format(gc.getTime());

					

					// limpa a StringBuilder para o proximo titulo
					titulo.delete(0, titulo.length());
					// limpa a variavel
					capa = null;
					
					//pega o link das lições do trimestre
					int nLicao = 1;
					Elements linkLicoes = buscaElementos(html,
							"div#conteudo a[href~="+ ordem_trimestre + ano +"]");
					for (Element link : linkLicoes) {
						Log.d("oT link licao", link.attr("abs:href") + " - " 
								+ String.valueOf(nLicao) + " " + link.text());
						nLicao++;
						new LicaoTask().execute(link.attr("abs:href"));
					}
				}
			}
		}
		
	}
	
	
	/*public void obtemTrimestres(int tipo, int ano) {
		Document html;
		try {
			//html = new docTask().execute(tipo,ano).get();
			if (html != null) {
				// pegando do #conteudo por haver erro de sintaxe no html do
				// #trimestre
				Elements trimestres = buscaElementos(html,
						"#trimestres p:matches([t|T]rimestre+)");
				Elements capas = buscaElementos(html, "#trimestres img");

				for (int i = 0; i < trimestres.size(); i++) {
										
					tmp = trimestres.get(i).text().replace('/', ' ');
//					Log.d("trimestre", trimestres.get(i).text());
					StringTokenizer tokens = new StringTokenizer(tmp);
					// 1¤ Trimestre de 2011 A Bíblia e as emoções humanas
					// pega apenas o primeiro char de 4¤ e converte para int
					ordem_trimestre = Integer.parseInt(Character
							.toString(tokens.nextToken().charAt(0)));
					tokens.nextToken(); // pula a palavra Trimestre
					tokens.nextToken(); // pula o "de"
					ano = Integer.parseInt(tokens.nextToken()); // pula o "2011"
					// junta todas as palavras que formam o título do trimestre.
					
					//correção do problema
					//<p>3º trimestre de 2014</p>
					//<p>Ensinos de Jesus</p>
					if (tokens.hasMoreTokens()) {
						while (tokens.hasMoreTokens()) {
							titulo.append(tokens.nextToken() + " ");
						}
					} else if (trimestres.get(i).nextElementSibling().hasText()) {
						titulo.append(trimestres.get(i).nextElementSibling().text());
					}
								
					
					// Log.d("trimestre", String.valueOf(ordem_trimestre));
					//Log.d("trimestre", titulo.toString());
					// obtem o endereço absoluto da imagem no site
					capa = capas.get(i).attr("abs:src");

					// baixa a imagem em outro processo
					// byte[] imagemFile = getFile(capa);
					//ImageView image = (ImageView) findViewById(R.id.imageView);
					// image.setImageBitmap(getFile(capa));
					// Bitmap bitmap =
					// BitmapFactory.decodeByteArray(imagemFile,0,imagemFile.length);
					//Bitmap bitmap = null;
					try {
						Bitmap bitmap = new baixaImagemTask().execute(capa).get();//new Util().baixaImagem(capa);
						//image.setImageBitmap(bitmap);
						if (bitmap == null) {
							Log.d("capa", "vazio");
						}
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						// comprime a imagem para gravar no banco
						bitmap.compress(CompressFormat.PNG, 100, baos);
						
						Trimestre trimestre = new Trimestre(titulo.toString(),
								ordem_trimestre, ano, tipo, baos.toByteArray());
						
						TrimestreDBAdapter dba = new TrimestreDBAdapter(this);

						trimestreID = dba.addTrimestre(trimestre);

					} catch (Exception e) {
						e.printStackTrace();
					}
					

					
					 * Intent intent = new Intent(this, DownloadService.class);
					 * Messenger messenger = new Messenger(handler);
					 * intent.putExtra("MESSENGER", messenger);
					 * intent.setData(Uri.parse(capa));
					 * intent.putExtra("urlpath", capa); startService(intent);
					 

					// pega o nome original da imagem da capa
					// capa = capa.substring(capa.lastIndexOf("/") + 1);
					// Log.d("capa", capa);
					// GregorianCalendar gc=new GregorianCalendar();
					// gc.set(Integer.parseInt(ano), 0, 1);
					// SimpleDateFormat formatador = new
					// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					// String ano_tmp = formatador.format(gc.getTime());

					

					// limpa a StringBuilder para o proximo titulo
					titulo.delete(0, titulo.length());
					// limpa a variavel
					capa = null;
					
					//pega o link das lições do trimestre
					int nLicao = 1;
					Elements linkLicoes = buscaElementos(html,
							"div#conteudo a[href~="+ ordem_trimestre + ano +"]");
					for (Element link : linkLicoes) {
						Log.d("oT link licao", link.attr("abs:href") + " - " 
								+ String.valueOf(nLicao) + " " + link.text());
						nLicao++;
						new LicaoTask().execute(link.attr("abs:href"));
					}
				}
			}
		} catch (InterruptedException e1) {			
			e1.printStackTrace();
		} catch (ExecutionException e1) {			
			e1.printStackTrace();
		}
		
	}*/
	public class LicaoTask extends AsyncTask<String, Void, Document> {

		@Override
		protected Document doInBackground(String... urls) {
			
			return obtemHtml(urls[0]);
		}

		@Override
		protected void onPostExecute(Document html) {
			Element titulo = buscaElemento(html,
					"div#conteudo td:matches((?i)lição\\ \\d)");
			String sTitulo = titulo.text();
			Log.d("obtemLicao", "título: " + sTitulo);
			StringTokenizer tokens = new StringTokenizer(sTitulo);
			tokens.nextToken();//pula Lição
			int iNumero = Integer.parseInt(tokens.nextToken());
			Log.d("obtemLicao", "número: " + iNumero);

			Element data_inicial_final = buscaElemento(html,
					"div#conteudo td p:matches(\\d*\\ a\\ \\d*");
			String sData = data_inicial_final.text();
			Log.d("obtemLicao", "data: " + sData);

			Element ilustracao = buscaElemento(html,
					"div#conteudo p[align=center] img");
			/*Uri urlIlustração = Uri.parse(ilustracao.attr("abs:src"));
			String nomeArquivoIlustracao = urlIlustração.getLastPathSegment();
			Log.d("obtemLicao", urlIlustração.toString() + " "
					+ nomeArquivoIlustracao);*/
			String sCapa = ilustracao.attr("abs:src");
			
			Bitmap bitmap;
			try {
				bitmap = new baixaImagemTask().execute(sCapa).get();
				if (bitmap == null) {
					Log.d("capa", "vazio");
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// comprime a imagem para gravar no banco
				bitmap.compress(CompressFormat.PNG, 100, baos);
				byte[] capa = baos.toByteArray();
				
				Licao licao = new Licao(sData, iNumero, sTitulo, trimestreID, capa);
				
				
				
				dbaLicao.addLicao(licao);
				/*
				 * Element raiz = buscaElemento(html, "div#conteudo");
				 * getDia(html, raiz, "Sábado"); getDia(html, raiz, "Domingo");
				 * getDia(html, raiz, "Segunda"); getDia(html, raiz, "Terça");
				 * getDia(html, raiz, "Quarta"); getDia(html, raiz, "Quinta");
				 * getDia(html, raiz, "Sexta");
				 */
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//new Util().baixaImagem(capa);//image.setImageBitmap(bitmap);
								
		}
		
	}

	/*private void obtemLicao(String url) {
		//String url = "http://cpbmais.cpb.com.br/htdocs/periodicos/licoes/jovens/2014/lj632014.html";
		Document html = obtemHtml(url);
		// File in = new File(this.getFilesDir() + "/lj532014.html");
		// File in = new File(this.getFilesDir() + "/licao.html");
		// Document html;
		try {
			// html = Jsoup.parse(in, "UTF-8");
			
			Element titulo = buscaElemento(html,
					"div#conteudo td:matches((?i)lição\\ \\d)");
			String sTitulo = titulo.text();
			Log.d("obtemLicao", "título: " + sTitulo);
			StringTokenizer tokens = new StringTokenizer(sTitulo);
			tokens.nextToken();//pula Lição
			int iNumero = Integer.parseInt(tokens.nextToken());
			Log.d("obtemLicao", "número: " + iNumero);

			Element data_inicial_final = buscaElemento(html,
					"div#conteudo td p:matches(\\d*\\ a\\ \\d*");
			String sData = data_inicial_final.text();
			Log.d("obtemLicao", "data: " + sData);

			Element ilustracao = buscaElemento(html,
					"div#conteudo p[align=center] img");
			Uri urlIlustração = Uri.parse(ilustracao.attr("abs:src"));
			String nomeArquivoIlustracao = urlIlustração.getLastPathSegment();
			Log.d("obtemLicao", urlIlustração.toString() + " "
					+ nomeArquivoIlustracao);
			String sCapa = ilustracao.attr("abs:src");
			
			Bitmap bitmap = new baixaImagemTask().execute(sCapa).get();//new Util().baixaImagem(capa);
			//image.setImageBitmap(bitmap);
			if (bitmap == null) {
				Log.d("capa", "vazio");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// comprime a imagem para gravar no banco
			bitmap.compress(CompressFormat.PNG, 100, baos);
			byte[] capa = baos.toByteArray();
			
			Licao licao = new Licao(sData, iNumero, sTitulo, trimestreID, capa);
			
			LicaoDBAdapter dbaLicao = new LicaoDBAdapter(this);
			
			dbaLicao.addLicao(licao);		
			

			Element raiz = buscaElemento(html, "div#conteudo");
			getDia(html, raiz, "Sábado");
			getDia(html, raiz, "Domingo");
			getDia(html, raiz, "Segunda");
			getDia(html, raiz, "Terça");
			getDia(html, raiz, "Quarta");
			getDia(html, raiz, "Quinta");
			getDia(html, raiz, "Sexta");

		} catch (Exception e) {
			e.printStackTrace();
			this.finish();
			System.exit(1);
		}
	}*/

	/*
	 * pega o texto do dia
	 */
	private void getDia(Document html, Element raiz, String nomeDia) {
		Element dia = buscaElemento(html, "td:contains(" + nomeDia + ")");
		Element prox = proximo(dia, raiz);
		int contaHR = 0;

		while (contaHR < 2) {
			if (prox.tagName().equals("hr")) {
				contaHR += 1;
			}
			Log.d("gD", nomeDia + ": " + prox.tagName() + ' ' + prox.text());
			// passa para o próximo do mesmo nível
			prox = prox.nextElementSibling();
		}
	}

	/*
	 * Sobe o nível até a raiz para buscar os próximos elementos
	 */
	private Element proximo(Element irmao, Element raiz) {
		Element proximo = irmao;
		Log.d("oL", "pProximo: " + proximo.tagName() + " " + proximo.id());
		while (!proximo.parent().id().equals(raiz.id())) {
			proximo = proximo.parent();
			Log.d("oL", "proximo: " + proximo.tagName() + " " + proximo.id());
		}
		Log.d("oL", "uProximo: " + proximo.tagName());
		return proximo;
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
	public String obtemURLTrimestre(int tipo, int ano) {
		String url = "http://cpbmais.cpb.com.br/htdocs/periodicos/les";
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
			System.setProperty("http.keepAlive", "false");
			// obtem o html do endereço
			Document doc = Jsoup.connect(url).timeout(10000)
					.userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
					.get();
			Log.d("obtemHTML", "Abrindo " + url);

			return doc;
		} catch (Exception e) {
			// Toast.makeText(this, "Erro:" + e.getMessage(),
			// Toast.LENGTH_SHORT)
			// .show();
			Log.e("obtemHTML", "Erro a abrir: " + url);
			return obtemHtml(url);
			
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
				"#trimestres p:matches([t|T]rimestre+)");
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