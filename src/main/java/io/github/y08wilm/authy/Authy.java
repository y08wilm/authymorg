package io.github.y08wilm.authy;

import io.github.y08wilm.authy.bukkit.ConfigurationManager;
import io.undertow.Undertow;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.ConduitFactory;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.bukkit.configuration.file.FileConfiguration;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public class Authy implements HttpHandler {

	public HashMap<String, String> ipUaToAuthorization = new HashMap<>();

	public io.undertow.server.handlers.proxy.SimpleProxyClientProvider proxyClientProvider;

	public int listen_port;

	public ConfigurationManager root;

	public FileConfiguration config;

	public Authy() throws MalformedURLException, URISyntaxException {
		this.root = new ConfigurationManager();
		this.root.setup(new File("config.yml"));
		this.config = this.root.getData();
		Thread thread = new Thread(() -> {
			while (true) {
				this.root.saveData();
				try {
					Thread.sleep(20000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
		this.listen_port = this.getConfig().getInt("listen_port", 4242);
		this.getConfig().set("listen_port", this.listen_port);
		proxyClientProvider = new io.undertow.server.handlers.proxy.SimpleProxyClientProvider(
				new URL("http://localhost:8008").toURI());
		Undertow server = Undertow.builder()
				.addHttpListener(this.listen_port, "localhost")
				.setHandler(this).build();
		server.start();
	}

	public FileConfiguration getConfig() {
		return this.config;
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		// TODO Auto-generated method stub
		String user_agent = null;
		for (HeaderValues header : exchange.getRequestHeaders()) {
			if (header.getHeaderName().toString().toLowerCase()
					.equals("user-agent")) {
				user_agent = header.element();
			}
		}
		String ip = null;
		System.out.println(exchange.getRequestMethod() + " "
				+ exchange.getRequestPath());
		for (HeaderValues header : exchange.getRequestHeaders()) {
			if (header.getHeaderName().toString().toLowerCase()
					.equals("x-forwarded-for")) {
				ip = header.element();
			}
		}
		if (ip == null) {
			ip = exchange.getSourceAddress().getAddress().getHostAddress();
		}
		if (ip.contains(", ")) {
			ip = ip.substring(0, ip.indexOf(", "));
		}
		if ((exchange.getRequestPath().equals("/_matrix/client/r0/sync")
				|| exchange.getRequestPath().equals("/_matrix/client/v1/sync")
				|| exchange.getRequestPath().startsWith(
						"/_matrix/client/v1/media/thumbnail/")
				|| exchange.getRequestPath().startsWith(
						"/_matrix/client/v1/media/download/")
				|| exchange.getRequestPath().startsWith(
						"/_matrix/media/v1/thumbnail/")
				|| exchange.getRequestPath().startsWith(
						"/_matrix/media/v1/download/")
				|| exchange.getRequestPath().startsWith(
						"/_matrix/media/r0/thumbnail/") || exchange
				.getRequestPath().startsWith("/_matrix/media/r0/download/"))) {
			String access_token = null;
			for (HeaderValues header : exchange.getRequestHeaders()) {
				if (header.getHeaderName().toString().toLowerCase()
						.equals("authorization")) {
					access_token = header.element();
				}
			}
			if (access_token != null) {
				access_token = access_token.replace("Bearer ", "");
				ipUaToAuthorization.put(
						ip
								+ (user_agent.indexOf("/") != -1 ? user_agent
										.substring(0, user_agent.indexOf("/"))
										: user_agent), access_token);
				System.out.println("access token " + access_token
						+ " verified for " + ip + " with ua " + user_agent);
			} else {
				if (ipUaToAuthorization.containsKey(ip
						+ (user_agent.indexOf("/") != -1 ? user_agent
								.substring(0, user_agent.indexOf("/"))
								: user_agent))) {
					exchange.getRequestHeaders()
							.add(HttpString.tryFromString("authorization"),
									"Bearer "
											+ ipUaToAuthorization.get(ip
													+ (user_agent.indexOf("/") != -1 ? user_agent
															.substring(
																	0,
																	user_agent
																			.indexOf("/"))
															: user_agent)));
				} else {
					System.out
							.println("ERROR: access token could not be verified for "
									+ ip + " with ua " + user_agent);
				}
			}
		}
		io.undertow.Handlers.proxyHandler(proxyClientProvider).handleRequest(
				exchange);
	}
}
