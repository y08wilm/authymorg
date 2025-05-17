<VirtualHost *:443>
	# The ServerName directive sets the request scheme, hostname and port that
	# the server uses to identify itself. This is used when creating
	# redirection URLs. In the context of virtual hosts, the ServerName
	# specifies what hostname must appear in the request's Host: header to
	# match this virtual host. For the default virtual host (this file) this
	# value is not decisive as it is used as a last resort host regardless.
	# However, you must set it for any further virtual host explicitly.
	ServerName matrix-client.nadeko.pw

	# Available loglevels: trace8, ..., trace1, debug, info, notice, warn,
	# error, crit, alert, emerg.
	# It is also possible to configure the loglevel for particular
	# modules, e.g.
	#LogLevel info ssl:warn

	ErrorLog ${APACHE_LOG_DIR}/error.log
	CustomLog ${APACHE_LOG_DIR}/access.log combined

	# For most configuration files from conf-available/, which are
	# enabled or disabled at a global level, it is possible to
	# include a line for only one particular virtual host. For example the
	# following line enables the CGI configuration for this host only
	# after it has been globally disabled with "a2disconf".
	#Include conf-available/serve-cgi-bin.conf
	SSLEngine on
	SSLProxyEngine On
	<IfModule mod_remoteip.c>
		RemoteIPProxyProtocol On
	</IfModule>
	ProxyRequests Off
	ProxyPreserveHost On
	AllowEncodedSlashes NoDecode
	<Location /_matrix/client/r0/auth/m.login.sso>
		ProxyPass !
		Redirect 302 /_matrix/client/r0/auth/m.login.sso https://nadeko.pw/_matrix/client/r0/auth/m.login.sso
	</Location>
	RewriteEngine On
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '173.245.48.0/20'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '103.21.244.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '103.22.200.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '103.31.4.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '141.101.64.0/18'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '108.162.192.0/18'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '190.93.240.0/20'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '188.114.96.0/20'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '197.234.240.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '198.41.128.0/17'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '162.158.0.0/15'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '104.16.0.0/13'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '104.24.0.0/14'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '172.64.0.0/13'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '131.0.72.0/22'"
	RewriteRule .* - [S=1]
	RewriteRule .* - [L,R=404]
	RewriteRule ^/_matrix/media/v1/thumbnail/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/thumbnail/$1 [QSA,P]
	RewriteRule ^/_matrix/media/v1/download/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/download/$1 [QSA,P]
	RewriteRule ^/_matrix/media/r0/thumbnail/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/thumbnail/$1 [QSA,P]
	RewriteRule ^/_matrix/media/r0/download/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/download/$1 [QSA,P]
	<Proxy *>
		Order deny,allow
		Allow from all
	</Proxy>
	ProxyPass /_matrix/client/v1/media http://127.0.0.1:4242/_matrix/client/v1/media nocanon
	ProxyPassReverse /_matrix/client/v1/media http://127.0.0.1:4242/_matrix/client/v1/media
	ProxyPass /_matrix/client/r0/sync http://127.0.0.1:4242/_matrix/client/r0/sync nocanon
	ProxyPassReverse /_matrix/client/r0/sync http://127.0.0.1:4242/_matrix/client/r0/sync
	ProxyPass / http://localhost:8008/ nocanon
	ProxyPassReverse / http://localhost:8008/
	SSLCertificateFile /etc/apache2/nadeko.pw.crt
	SSLCertificateKeyFile /etc/apache2/nadeko.pw.key
	SSLCertificateChainFile /etc/apache2/origin_ca_rsa_root.crt
</VirtualHost>
<VirtualHost *:443>
	# The ServerName directive sets the request scheme, hostname and port that
	# the server uses to identify itself. This is used when creating
	# redirection URLs. In the context of virtual hosts, the ServerName
	# specifies what hostname must appear in the request's Host: header to
	# match this virtual host. For the default virtual host (this file) this
	# value is not decisive as it is used as a last resort host regardless.
	# However, you must set it for any further virtual host explicitly.
	ServerName nadeko.pw

	# Available loglevels: trace8, ..., trace1, debug, info, notice, warn,
	# error, crit, alert, emerg.
	# It is also possible to configure the loglevel for particular
	# modules, e.g.
	#LogLevel info ssl:warn

	ErrorLog ${APACHE_LOG_DIR}/error.log
	CustomLog ${APACHE_LOG_DIR}/access.log combined

	# For most configuration files from conf-available/, which are
	# enabled or disabled at a global level, it is possible to
	# include a line for only one particular virtual host. For example the
	# following line enables the CGI configuration for this host only
	# after it has been globally disabled with "a2disconf".
	#Include conf-available/serve-cgi-bin.conf
	SSLEngine on
	SSLProxyEngine On
	<IfModule mod_remoteip.c>
		RemoteIPProxyProtocol On
	</IfModule>
	SSLCertificateFile /etc/apache2/nadeko.pw.crt
	SSLCertificateKeyFile /etc/apache2/nadeko.pw.key
	SSLCertificateChainFile /etc/apache2/origin_ca_rsa_root.crt
	ProxyRequests Off
	ProxyPreserveHost On
	AllowEncodedSlashes NoDecode
	RewriteEngine On
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '173.245.48.0/20'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '103.21.244.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '103.22.200.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '103.31.4.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '141.101.64.0/18'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '108.162.192.0/18'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '190.93.240.0/20'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '188.114.96.0/20'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '197.234.240.0/22'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '198.41.128.0/17'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '162.158.0.0/15'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '104.16.0.0/13'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '104.24.0.0/14'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '172.64.0.0/13'" [OR]
	RewriteCond expr "%{REMOTE_ADDR} -ipmatch '131.0.72.0/22'"
	RewriteRule .* - [S=1]
	RewriteRule .* - [L,R=404]
	<Proxy *>
		Order deny,allow
		Allow from all
	</Proxy>
	<Location /.well-known/matrix/server>
		ProxyPass !
		ErrorDocument 200 '{"m.server": "matrix-client.nadeko.pw:443"}'
		Redirect 200 /
		Header always set Content-Type application/json
		Header always set Access-Control-Allow-Origin *
	</Location>
	<Location /.well-known/matrix/client>
		ProxyPass !
		ErrorDocument 200 '{"m.homeserver": {"base_url": "https://matrix-client.nadeko.pw"}}'
		Redirect 200 /
		Header always set Content-Type application/json
		Header always set Access-Control-Allow-Origin *
	</Location>
	RewriteRule ^/_matrix/media/v1/thumbnail/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/thumbnail/$1 [QSA,P]
	RewriteRule ^/_matrix/media/v1/download/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/download/$1 [QSA,P]
	RewriteRule ^/_matrix/media/r0/thumbnail/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/thumbnail/$1 [QSA,P]
	RewriteRule ^/_matrix/media/r0/download/(.*) https://matrix-client.nadeko.pw/_matrix/client/v1/media/download/$1 [QSA,P]
	ProxyPass /_matrix/client/v1/media http://127.0.0.1:4242/_matrix/client/v1/media nocanon
	ProxyPassReverse /_matrix/client/v1/media http://127.0.0.1:4242/_matrix/client/v1/media
	ProxyPass /_matrix/client/r0/sync http://127.0.0.1:4242/_matrix/client/r0/sync nocanon
	ProxyPassReverse /_matrix/client/r0/sync http://127.0.0.1:4242/_matrix/client/r0/sync
	ProxyPass / http://localhost:8008/ nocanon
	ProxyPassReverse / http://localhost:8008/
</VirtualHost>