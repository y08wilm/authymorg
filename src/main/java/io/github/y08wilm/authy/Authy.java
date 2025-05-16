package io.github.y08wilm.authy;

import io.github.y08wilm.authy.bukkit.ConfigurationManager;
import io.github.y08wilm.authy.data.types.Auth;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;

public class Authy implements HttpHandler {

	public HashMap<String, Auth> ipUaToAuthorization = new HashMap<>();

	public io.undertow.server.handlers.proxy.SimpleProxyClientProvider proxyClientProvider;

	public String url;

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
		this.url = this.getConfig().getString("url", "http://localhost:8008");
		this.listen_port = this.getConfig().getInt("listen_port", 4242);
		this.getConfig().set("url", this.url);
		this.getConfig().set("listen_port", this.listen_port);
		proxyClientProvider = new io.undertow.server.handlers.proxy.SimpleProxyClientProvider(
				new URL(url).toURI());
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
				Auth auth = new Auth(access_token, System.currentTimeMillis()
						+ (60_000L * 5));
				ipUaToAuthorization.put(
						ip
								+ (user_agent.indexOf("/") != -1 ? user_agent
										.substring(0, user_agent.indexOf("/"))
										: user_agent), auth);
				System.out.println("access token " + access_token
						+ " verified for " + ip + " with ua " + user_agent);
			} else {
				if (ipUaToAuthorization.containsKey(ip
						+ (user_agent.indexOf("/") != -1 ? user_agent
								.substring(0, user_agent.indexOf("/"))
								: user_agent))) {
					Auth auth = ipUaToAuthorization.get(ip
							+ (user_agent.indexOf("/") != -1 ? user_agent
									.substring(0, user_agent.indexOf("/"))
									: user_agent));
					if (auth.getExpire_at() < System.currentTimeMillis()) {
						System.out.println("ERROR: access token "
								+ auth.getAccess_token() + " is expired for "
								+ ip + " with ua " + user_agent);
					} else {
						exchange.getRequestHeaders().add(
								HttpString.tryFromString("authorization"),
								"Bearer " + auth.getAccess_token());
					}
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
