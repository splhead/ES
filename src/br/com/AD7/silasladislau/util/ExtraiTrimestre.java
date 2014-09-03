package br.com.AD7.silasladislau.util;

import java.util.StringTokenizer;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.util.Log;
import br.com.AD7.silasladislau.interfaces.TarefaConcluidaListener;
import br.com.AD7.silasladislau.models.Extracao;
import br.com.AD7.silasladislau.models.Trimestre;

public class ExtraiTrimestre extends Extracao implements TarefaConcluidaListener {
	private Trimestre trimestre;
	//private String url;
	
	public ExtraiTrimestre() {
	//	new DocumentDownloadTask().execute(params);
	}

	public Trimestre getTrimestre() {
		return trimestre;
	}

	@Override
	public void iniciaExtracao(Document html) {
		if (html != null) {
			// pegando do #conteudo por haver erro de sintaxe no html do
			// #trimestre
			StringBuilder titulo = new StringBuilder();
			Elements trimestres = buscaElementos(html,
					"#trimestres p:matches([t|T]rimestre+)");
			Elements capas = buscaElementos(html, "#trimestres img");

			for (int i = 0; i < trimestres.size(); i++) {
									
				String tmp = trimestres.get(i).text().replace('/', ' ');
					Log.d("trimestre", trimestres.get(i).text());
				StringTokenizer tokens = new StringTokenizer(tmp);
				// 1¤ Trimestre de 2011 A Bíblia e as emoções humanas
				// pega apenas o primeiro char de 4¤ e converte para int
				trimestre.setOrdemTrimestre(Integer.parseInt(Character
						.toString(tokens.nextToken().charAt(0))));
				tokens.nextToken(); // pula a palavra Trimestre
				tokens.nextToken(); // pula o "de"
				trimestre.setAno(Integer.parseInt(tokens.nextToken())); // pula o "2011"
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
				String urlCapa = capas.get(i).attr("abs:src");

				// baixa a imagem em outro processo
				// byte[] imagemFile = getFile(capa);
				//ImageView image = (ImageView) findViewById(R.id.imageView);
				// image.setImageBitmap(getFile(capa));
				// Bitmap bitmap =
				// BitmapFactory.decodeByteArray(imagemFile,0,imagemFile.length);
				//Bitmap bitmap = null;
				/*try {
					Bitmap bitmap = new baixaImagemTask().execute(urlCapa).get();//new Util().baixaImagem(capa);
					//image.setImageBitmap(bitmap);
					if (bitmap == null) {
						Log.d("capa", "vazio");
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// comprime a imagem para gravar no banco
					bitmap.compress(CompressFormat.PNG, 100, baos);
					
					Trimestre trimestre = new Trimestre(titulo.toString(),
							ordem_trimestre, ano, tipo, baos.toByteArray());
					
					

					trimestreID = dbaTrimestre.addTrimestre(trimestre);

				} catch (Exception e) {
					e.printStackTrace();
				}*/
				

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

				
				trimestre.setTitulo(titulo.toString());
				Log.d("trim", trimestre.toString());
				// limpa a StringBuilder para o proximo titulo
				titulo.delete(0, titulo.length());
				// limpa a variavel
				urlCapa = null;
				
				//pega o link das lições do trimestre
				/*int nLicao = 1;
				Elements linkLicoes = buscaElementos(html,
						"div#conteudo a[href~="+ ordem_trimestre + ano +"]");
				for (Element link : linkLicoes) {
					Log.d("oT link licao", link.attr("abs:href") + " - " 
							+ String.valueOf(nLicao) + " " + link.text());
					nLicao++;
					new LicaoTask().execute(link.attr("abs:href"));
				}*/
			} //fim for trimestres
		}
	}

	@Override
	public void quandoTarefaConcluida(Document html) {
		this.iniciaExtracao(html);		
	}

}
