package me.sisko.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import me.sisko.forumsync.Main;

public class UUIDFetcher {
	public static String getUUID(String playername) {
		String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);

		StringBuilder result = new StringBuilder();

		try {
			readData(output, result);
		} catch (Exception e) {
			Main.getPlugin().getLogger().info("Could not find uuid for "  + playername + "!");
			return null;
		}

		String u = result.toString();

		String uuid = "";

		for (int i = 0; i <= 31; i++) {
			uuid = uuid + u.charAt(i);
		}

		return uuid;
	}

	private static void readData(String toRead, StringBuilder result) {
		int i = 7;

		while (i < 200) {
			if (String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\""))
				break;
			result.append(String.valueOf(toRead.charAt(i)));

			i++;
		}
	}

	private static String callURL(String URL) {
		StringBuilder sb = new StringBuilder();
		URLConnection urlConn = null;
		InputStreamReader in = null;
		try {
			URL url = new URL(URL);
			urlConn = url.openConnection();

			if (urlConn != null) {
				urlConn.setReadTimeout(60000);
			}
			if ((urlConn != null) && (urlConn.getInputStream() != null)) {
				in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
				BufferedReader bufferedReader = new BufferedReader(in);

				if (bufferedReader != null) {
					int cp;
					while ((cp = bufferedReader.read()) != -1) {
						sb.append((char) cp);
					}

					bufferedReader.close();
				}
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return sb.toString();
	}
}
