package razorclaw.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import razorclaw.object.KeyPhrase;

public class DomainFeeder {

	private static final Logger LOG = Logger.getLogger(DomainFeeder.class
			.getName());

	private static final String DOMAIN_LIST = "evaluation/0512.txt";

	private static final String OUTPUT = "evaluation/0512.10";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<KeyPhrase> keyPhrases = new ArrayList<KeyPhrase>();
		ArrayList<String> domains = new ArrayList<String>();

		// load the domain list
		try {
			BufferedReader in = new BufferedReader(new FileReader(DOMAIN_LIST));
			for (String s = in.readLine(); s != null; s = in.readLine()) {
				domains.add(s);
			}

			LOG.info("Loaded domains: " + domains.size());
		} catch (FileNotFoundException e) {
			LOG.severe("File not found: " + DOMAIN_LIST);

			e.printStackTrace();
		} catch (IOException e) {
			LOG.severe("Read file failed");

			e.printStackTrace();
		}

		// access razorclaw
		ExecutorService executor = java.util.concurrent.Executors
				.newFixedThreadPool(10);
		List<Callable<KeyPhrase>> tasks = new ArrayList<Callable<KeyPhrase>>();

		for (String domain : domains) {
			KeyPhrase keyPhrase = new KeyPhrase();
			keyPhrases.add(keyPhrase);

			DomainFeederThread t = new DomainFeederThread(domain);
			tasks.add(t);
		}

		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(OUTPUT));

			List<Future<KeyPhrase>> results = executor.invokeAll(tasks, 10,
					TimeUnit.MINUTES);
			KeyPhrase k;
			for (Future<KeyPhrase> result : results) {
				if ((k = result.get()) != null) {
					out.write(k.getDomain() + "\t" + k.getKeyphrase() + "\n");
				}
			}
		} catch (InterruptedException e) {
			LOG.severe("Execute tasks failed");

			e.printStackTrace();
		} catch (IOException e) {
			LOG.severe("Write file failed");

			e.printStackTrace();
		} catch (ExecutionException e) {
			LOG.severe("Execute tasks failed");

			e.printStackTrace();
		}
	}
}
