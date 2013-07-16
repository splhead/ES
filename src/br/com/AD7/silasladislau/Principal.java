package br.com.AD7.silasladislau;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import android.app.Activity;
import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Principal extends Activity {
	private TrimestreDBAdapter dba = new TrimestreDBAdapter(this);
	private String capa, tmp;
	private int ordem_trimestre, ano = 2013;
	private StringBuilder titulo = new StringBuilder();

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
		Document doc = this.getDoc(ano);
		// pegando do #conteudo por haver erro de sintaxe no html do #trimestre
		Elements trimestres = this.getElements(doc,
				"#trimestres p:matches([t|T]rimestre+)");
		Elements capas = this.getElements(doc, "#trimestres img");

		for (int i = 0; i < trimestres.size(); i++) {
			tmp = trimestres.get(i).text().replace('/', ' ');
			Log.d("trimestre", tmp);
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
			// carregaImagens(capa);
			new BaixaCapas().execute(capa);
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

	/*
	 * public void carregaImagens(String endereco) { url = null; try { url = new
	 * URL(endereco); } catch (MalformedURLException e) { e.printStackTrace(); }
	 * new BaixaCapas().execute(url); }
	 */

	public Document getDoc(int ano) {
		String url = "http://www.cpb.com.br/htdocs/periodicos/lesjovens" + ano
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
		Document doc = this.getDoc(ano);
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

	class BaixaCapas extends AsyncTask<String, Integer, String> {
		Context context = getApplicationContext();
		private final static String TAG = "BaixaCapas";
		private File diretorio = new File(context.getFilesDir() + "/imagens/");

		@Override
		protected String doInBackground(String... urls) {
			criaDiretorioImagens(diretorio);
			// initilize the default HTTP client object
			final DefaultHttpClient client = new DefaultHttpClient();

			// forming a HttpGet request
			final HttpGet getRequest = new HttpGet(urls[0]);

			try {
				String tmp = urls[0].toString();
				String nomeArquivo = tmp.substring(tmp.lastIndexOf('/') + 1);
				File imagem = new File(diretorio, nomeArquivo);
				if (!imagemJaExiste(imagem)) {
					Log.i(TAG, "doInBackground: " + urls[0]);
					// Cria a url
					URL url = new URL(urls[0]);
					//InputStream in = url.openStream();
					try {
						HttpResponse response = client.execute(getRequest);
						// check 200 OK for success
						final int statusCode = response.getStatusLine()
								.getStatusCode();

						if (statusCode != HttpStatus.SC_OK) {
							Log.w("doInBackground", "Erro " + statusCode
									+ " enquanto baixa a imagem " + url);
							return null;

						}

						final HttpEntity entity = response.getEntity();
						if (entity != null) {
							InputStream in = null;
							try {
								// getting contents from the stream
								in = entity.getContent();
								byte[] bytes = leBytes(in);

								FileOutputStream fos = new FileOutputStream(imagem);
								fos.write(bytes);
								fos.close();
								Log.i(TAG, "imagem retornada com: " + bytes.length
										+ " bytes");
								// conexao.disconnect();
								return "Imagem retornada com: " + bytes.length + " bytes";
								// decoding stream data back into image Bitmap
								// that android understands
								/*final Bitmap bitmap = BitmapFactory
										.decodeStream(inputStream);

								return bitmap;*/
							} finally {								
								entity.consumeContent();
							}
						}
					} catch (Exception e) {
						Log.e(getClass().getName(), e.getMessage(), e);
					}
					// HttpURLConnection conexao = (HttpURLConnection) url
					// .openConnection();
					// Configura a requisição para GET
					/*
					 * conexao.setRequestProperty("Request-Method", "GET");
					 * conexao.setDoInput(true); conexao.setDoOutput(false);
					 * conexao.connect(); InputStream in =
					 * conexao.getInputStream();
					 */
					/*byte[] bytes = leBytes(in);

					FileOutputStream fos = new FileOutputStream(imagem);
					fos.write(bytes);
					fos.close();
					Log.i(TAG, "imagem retornada com: " + bytes.length
							+ " bytes");
					// conexao.disconnect();
					return "Imagem retornada com: " + bytes.length + " bytes";*/
				}
			} catch (MalformedURLException e) {
				Log.e(getClass().getName(), e.getMessage(), e);
			} 
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				Toast.makeText(Principal.this, result, Toast.LENGTH_LONG)
						.show();
			} else {
				Toast.makeText(Principal.this, "A imagem já existe!",
						Toast.LENGTH_LONG).show();
			}

		}

	}

	// /// metodos utilitários
	/*
	 * // baixa um arquivo protected Bitmap download(String url) throws
	 * IOException { byte[] bytes = this.baixaImagem(url); if (bytes != null) {
	 * Log.i(getClass().getName(), "Criando Bitmap com BitmapFactory " + bytes);
	 * Bitmap imagem = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	 * return imagem; } return null; }// fim do método download
	 */
	private boolean imagemJaExiste(File imagem) {
		if (imagem.exists() && imagem.isFile()) {
			Log.w(getClass().getName(), "A imagem já existe no diretório.");
			return true;
		}
		return false;
	}

	private boolean diretorioImagensExiste(File diretorio) {
		return diretorio.exists();
	}

	private void criaDiretorioImagens(File diretorio) {
		boolean success = false;
		if (!diretorioImagensExiste(diretorio)) {
			success = diretorio.mkdirs();
			if (!success) {
				Log.d("Arquivo", "Erro ao criar o diretorio imagens");
			} else {
				Log.d("Arquivo", "Criado o diretorio de imagens");
			}
		}
	}

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