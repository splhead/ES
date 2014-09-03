package br.com.AD7.silasladislau.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.AD7.silasladislau.R;
import br.com.AD7.silasladislau.util.Util;
import android.support.v7.app.ActionBarActivity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends ActionBarActivity {
	public static final String PREFS_NAME = "LoginPrefs";
	final String EMAIL = "silas_ladislau@yahoo.com.br";
	final String SENHA = "spl#e@d";
	String recaptchaChallengeField = "";
	String recaptchaResponseField = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		new CaptchaTask().execute("");
		final Button button = (Button) findViewById(R.id.bEntrar);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				final EditText etCaptcha = (EditText) findViewById(R.id.etCaptcha);
				recaptchaResponseField = etCaptcha.getText().toString();
				Log.d("cliquei", recaptchaResponseField);

				new LoginTask().execute("");

			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	class CaptchaTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... arg0) {
			recaptcha();
			return null;
		}
	}
	
	class LoginTask extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... arg0) {
			login2();
			return null;
		}
	}

	public void login() throws ClientProtocolException, IOException {
		String linha = "";
		String retorno = "";
		DefaultHttpClient httpclient = new DefaultHttpClient();

		HttpGet httpget = new HttpGet(
				"http://cpbmais.cpb.com.br/login/index.php");

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		Log.d("login", "Login form get: " + response.getStatusLine());
		if (entity != null) {
			entity.consumeContent();
		}
		Log.d("login", "Initial set of cookies:");
		List<Cookie> cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				Log.d("login", "- " + cookies.get(i).toString());
			}
		}

		HttpPost httpost = new HttpPost(
				"http://cpbmais.cpb.com.br/login/includes/autenticate.php");

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", EMAIL));
		nvps.add(new BasicNameValuePair("senha", SENHA));
		nvps.add(new BasicNameValuePair("recaptcha_challenge_field",
				recaptchaChallengeField));
		nvps.add(new BasicNameValuePair("recaptcha_response_field",
				recaptchaResponseField));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		response = httpclient.execute(httpost);
		entity = response.getEntity();

		System.out.println("Login form get: " + response.getStatusLine());
		if (entity != null) {
			// Pega o retorno
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					entity.getContent()));

			// Lê o buffer e coloca na variável
			while ((linha = rd.readLine()) != null) {
				retorno += linha;
			}
			Log.d("logado", retorno);
			entity.consumeContent();
		}

		System.out.println("Post logon cookies:");
		cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			Log.d("logon", "None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				Log.d("logon", "- " + cookies.get(i).toString());
			}
		}

		// When HttpClient instance is no longer needed,
		// shut down the connection manager to ensure
		// immediate deallocation of all system resources
		httpclient.getConnectionManager().shutdown();
	}

	private void recaptcha() {
		Connection.Response res;
		try {
			// abre o captch
			res = Jsoup
					.connect(
							"http://www.google.com/recaptcha/api/noscript?k=6LfGLPkSAAAAAIkbhvitAGElU7VC_LkL2Nog0Pq7")
					.userAgent(
							"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					// .data("email", EMAIL, "senha", SENHA)
					// .method(Method.POST)
					.execute();
			Document doc = res.parse();

			Element imagem = doc.select("img").first();
			Element desafio = doc.select("input#recaptcha_challenge_field")
					.first();
			recaptchaChallengeField = desafio.attr("value");
			// String sessionId = res.cookie("PHPSESSID");+ sessionId

			//Log.d("login2", doc.html() + " ");
			Log.d("login2", imagem.attr("abs:src") + " "
					+ recaptchaChallengeField);
			new baixaImagemTask().execute(imagem.attr("abs:src"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void login2() {
		Connection.Response res;
		try {
			//para pegar o cookie
			res = Jsoup
					.connect(
							"http://cpbmais.cpb.com.br/login/index.php")
					.userAgent(
							"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					// .data("email", EMAIL, "senha", SENHA)
					// .method(Method.POST)
					.execute();
			 String sessionId = res.cookie("PHPSESSID");
			 Log.d("cookie", sessionId);
			// abre o captch
			res = Jsoup
					.connect(
							"http://cpbmais.cpb.com.br/login/includes/autenticate.php")
					.userAgent(
							"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:32.0) Gecko/20100101 Firefox/32.0")
					.data("email", EMAIL, "senha", SENHA,
							"recaptcha_challenge_field",
							recaptchaChallengeField,
							"recaptcha_response_field", recaptchaResponseField)
					.method(Method.POST)
					.cookie("PHPSESSID", sessionId)
					.execute();
			Document doc = res.parse();

			

			Log.d("login2", doc.html() + " ");
			
			res = Jsoup.connect("http://cpbmais.cpb.com.br/htdocs/periodicos/lesjovens2014.php")
					.cookie("PHPSESSID", sessionId)
					.execute();
			doc = res.parse();
			Log.d("logado-request", doc.html() + " ");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class baixaImagemTask extends AsyncTask<String, Void, Bitmap> {
		@Override
		protected Bitmap doInBackground(String... urls) {

			return new Util().baixaImagem(urls[0]);

		}

		@Override
		protected void onPostExecute(Bitmap result) {
			ImageView image = (ImageView) findViewById(R.id.ivCaptcha);
			image.setImageBitmap(result);
		}

	}
}
