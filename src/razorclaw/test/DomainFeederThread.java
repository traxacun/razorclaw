package razorclaw.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.logging.Logger;

import net.arnx.jsonic.JSON;

import razorclaw.object.KeyPhrase;

public class DomainFeederThread implements Callable<KeyPhrase> {
	private static final Logger LOG = Logger.getLogger(DomainFeederThread.class
			.getName());

	private String _domain;

	private static final String RAZORCLAW_URL = "http://localhost:8888/razorclaw?domain=";

	public DomainFeederThread(String domain) {
		setDomain(domain);
	}

	@Override
	public KeyPhrase call() throws Exception {
		URLConnection conn;
		String s, content = "";
		try {
			conn = (new URL(RAZORCLAW_URL + getDomain())).openConnection();
			conn.setConnectTimeout(0);
			conn.setReadTimeout(0);
			conn.setRequestProperty("UserAgent", "razorclaw");

			BufferedReader in = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((s = in.readLine()) != null) {
				content += s;
			}
			// parse the response
			return JSON.decode(content, KeyPhrase.class);
		} catch (MalformedURLException e) {
			e.printStackTrace();

			return null;
		} catch (IOException e) {
			e.printStackTrace();

			return null;
		}
	}

	public void setDomain(String _domain) {
		this._domain = _domain;
	}

	public String getDomain() {
		return _domain;
	}
}
