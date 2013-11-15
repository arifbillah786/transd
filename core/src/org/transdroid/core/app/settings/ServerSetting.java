/* 
 * Copyright 2010-2013 Eric Kok et al.
 * 
 * Transdroid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Transdroid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Transdroid.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.transdroid.core.app.settings;

import org.transdroid.core.gui.lists.SimpleListItem;
import org.transdroid.core.gui.log.Log;
import org.transdroid.daemon.Daemon;
import org.transdroid.daemon.DaemonSettings;
import org.transdroid.daemon.IDaemonAdapter;
import org.transdroid.daemon.OS;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Represents a user-configured remote server.
 * @author Eric Kok
 */
public class ServerSetting implements SimpleListItem {

	private static final String DEFAULT_NAME = "Default";

	private final int key;
	private final String name;
	private final Daemon type;
	private final String address;
	private final String localAddress;
	private final int localPort;
	private final String localNetwork;
	private final int port;
	private final String folder;
	private final boolean useAuthentication;
	private final String username;
	private final String password;
	private final String extraPass;
	private final OS os;
	private final String downloadDir;
	private final String ftpUrl;
	private final String ftpPassword;
	private final int timeout;
	private final boolean alarmOnFinishedDownload;
	private final boolean alarmOnNewTorrent;
	private final boolean ssl;
	private final boolean sslTrustAll;
	private final String sslTrustKey;
	private final boolean isAutoGenerated;

	/**
	 * Creates a daemon settings instance, providing full connection details
	 * @param name A name used to identify this server to the user
	 * @param type The server daemon type
	 * @param address The server domain name or IP address
	 * @param localAddress The server domain or IP address when connected to the server's local network
	 * @param localPort The port on which the server is running in the server's local network
	 * @param localNetwork The server's local network SSID
	 * @param port The port on which the server daemon is running
	 * @param sslTrustKey The specific key that will be accepted.
	 * @param folder The server folder (like a virtual sub-folder or an SCGI mount point)
	 * @param useAuthentication Whether to use basic authentication
	 * @param username The user name to provide during authentication
	 * @param password The password to provide during authentication
	 * @param extraPass The Deluge web interface password
	 * @param downloadDir The default download directory (which may also be used as base directory for file paths)
	 * @param ftpUrl The partial URL to connect to when requesting FTP-style transfers
	 * @param timeout The number of seconds to wait before timing out a connection attempt
	 * @param isAutoGenerated Whether this setting was generated rather than manually inputed by the user
	 */
	public ServerSetting(int key, String name, Daemon type, String address, String localAddress, int localPort,
			String localNetwork, int port, boolean ssl, boolean sslTrustAll, String sslTrustKey, String folder,
			boolean useAuthentication, String username, String password, String extraPass, OS os, String downloadDir,
			String ftpUrl, String ftpPassword, int timeout, boolean alarmOnFinishedDownload, boolean alarmOnNewTorrent,
			boolean isAutoGenerated) {
		this.key = key;
		this.name = name;
		this.type = type;
		this.address = address;
		this.localAddress = localAddress;
		this.localPort = localPort;
		this.localNetwork = localNetwork;
		this.port = port;
		this.ssl = ssl;
		this.sslTrustAll = sslTrustAll;
		this.sslTrustKey = sslTrustKey;
		this.folder = folder;
		this.useAuthentication = useAuthentication;
		this.username = username;
		this.password = password;
		this.extraPass = extraPass;
		this.os = os;
		this.downloadDir = downloadDir;
		this.ftpUrl = ftpUrl;
		this.ftpPassword = ftpPassword;
		this.timeout = timeout;
		this.alarmOnFinishedDownload = alarmOnFinishedDownload;
		this.alarmOnNewTorrent = alarmOnNewTorrent;
		this.isAutoGenerated = isAutoGenerated;
	}

	@Override
	public String getName() {
		if (!TextUtils.isEmpty(name))
			return name;
		if (!TextUtils.isEmpty(address)) {
			String host = Uri.parse(address).getHost();
			return host == null ? DEFAULT_NAME : host;
		}
		return DEFAULT_NAME;
	}

	public Daemon getType() {
		return type;
	}

	public String getAddress() {
		return address;
	}

	public String getLocalAddress() {
		return localAddress;
	}

	public int getLocalPort() {
		return localPort;
	}

	public String getLocalNetwork() {
		return localNetwork;
	}

	public int getPort() {
		return port;
	}

	public boolean getSsl() {
		return ssl;
	}

	public boolean getSslTrustAll() {
		return sslTrustAll;
	}

	public String getSslTrustKey() {
		return sslTrustKey;
	}

	public String getFolder() {
		return folder;
	}

	public boolean shouldUseAuthentication() {
		return useAuthentication;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getExtraPassword() {
		return extraPass;
	}

	public OS getOS() {
		return os;
	}

	public String getDownloadDir() {
		return downloadDir;
	}

	public String getFtpUrl() {
		return ftpUrl;
	}

	public String getFtpPassword() {
		return ftpPassword;
	}

	public int getTimeoutInMilliseconds() {
		return timeout * 1000;
	}

	public boolean shouldAlarmOnFinishedDownload() {
		return alarmOnFinishedDownload;
	}

	public boolean shouldAlarmOnNewTorrent() {
		return alarmOnNewTorrent;
	}

	public boolean isAutoGenerated() {
		return isAutoGenerated;
	}

	public int getOrder() {
		return this.key;
	}

	/**
	 * Returns a string that the user can use to identify the server by internal settings (rather than the name).
	 * @return A human-readable identifier in the form [https://]username@address:port/folder
	 */
	public String getHumanReadableIdentifier() {
		if (isAutoGenerated) {
			// Hide the 'implementation details'; just give the username and server
			return (this.shouldUseAuthentication() && !TextUtils.isEmpty(this.getUsername()) ? this.getUsername() + "@"
					: "") + getAddress();
		}
		return (this.ssl ? "https://" : "http://")
				+ (this.shouldUseAuthentication() && !TextUtils.isEmpty(this.getUsername()) ? this.getUsername() + "@"
						: "") + getAddress() + ":" + getPort()
				+ (Daemon.supportsCustomFolder(getType()) && getFolder() != null ? getFolder() : "");
	}

	/**
	 * Returns a string that acts as a unique identifier for this server, non-depending on the internal storage
	 * order/index. THis may be used to store additional details about this server elsewhere. It may change if the user
	 * changes server settings, but not with name or notification settings.
	 * @return A unique identifying string, based primarily on the configured address, port number, SSL settings and
	 *         user name; returns null if the server is not yet fully identifiable (during configuration, for example)
	 */
	public String getUniqueIdentifier() {
		if (getType() == null || getAddress() == null || getAddress().equals(""))
			return null;
		return getType().toString() + "|" + getHumanReadableIdentifier();
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof ServerSetting) {
			// Directly compare order numbers/unique keys
			return ((ServerSetting) o).getOrder() == this.key;
		} else if (o instanceof DaemonSettings) {
			// Old-style DaemonSettings objects can be equal if they were constructed from a ServerSettings object:
			// idString should reflect the local key/order
			return ((DaemonSettings) o).getIdString().equals(Integer.toString(this.key));
		}
		// Other objects are never equal to this
		return false;
	}

	/**
	 * Returns the appropriate daemon adapter to which tasks can be executed, in accordance with this server's settings
	 * @param connectedToNetwork The name of the (wifi) network we are currently connected to, or null if this could not
	 *            be determined
	 * @param context
	 * @return An IDaemonAdapter instance of the specific torrent client daemon type
	 */
	public IDaemonAdapter createServerAdapter(String connectedToNetwork, Context context) {
		return type.createAdapter(convertToDaemonSettings(connectedToNetwork, context));
	}

	/**
	 * Converts local server settings into an old-style {@link DaemonSettings} object.
	 * @param connectedToNetwork The name of the (wifi) network we are currently connected to, or null if this could not
	 *            be determined
	 * @param caller
	 * @return A {@link DaemonSettings} object to execute server commands against
	 */
	private DaemonSettings convertToDaemonSettings(String connectedToNetwork, Context caller) {
		// The local integer key is converted to the idString string.
		// The host name address used is dependent on the network that we are currently connected to (to allow a
		// distinct connection IP or host name when connected to a local network).
		if (localNetwork != null)
			Log.d(caller, "Creating adapter for " + name + " of type " + type.name() + ": connected to "
					+ connectedToNetwork + " and configured local network is " + localNetwork);
		return new DaemonSettings(name, type,
				connectedToNetwork != null && connectedToNetwork.equals(localNetwork) ? localAddress : address,
				connectedToNetwork != null && connectedToNetwork.equals(localNetwork) ? localPort : port, ssl,
				sslTrustAll, sslTrustKey, folder, useAuthentication, username, password, extraPass, os, downloadDir,
				ftpUrl, ftpPassword, timeout, alarmOnFinishedDownload, alarmOnNewTorrent, Integer.toString(key),
				isAutoGenerated);
	}
}
