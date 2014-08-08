package br.com.AD7.silasladislau.IO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class UtilImagem {
	public UtilImagem() {

	}

	public Bitmap getFile(String urlString) {
		/*
		 * try { URL imageUrl = new URL(url); URLConnection ucon =
		 * imageUrl.openConnection(); Log.d("UtilImagem", "url: " + url);
		 * InputStream in = ucon.getInputStream(); return leBytes(in); } catch
		 * (Exception e) { Log.d("UtilImagem", "Erro: " + e.toString()); }
		 * return null;
		 */
		try {
			URL url = new URL(urlString);
			HttpURLConnection conexao = (HttpURLConnection) url
					.openConnection();
			conexao.setRequestMethod("GET");
			conexao.setDoInput(true);
			conexao.connect();
			InputStream is = conexao.getInputStream();
			Bitmap imagem = BitmapFactory.decodeStream(is);
			return imagem;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
