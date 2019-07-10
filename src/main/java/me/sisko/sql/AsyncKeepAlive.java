package me.sisko.sql;

import java.sql.Connection;

public class AsyncKeepAlive implements Runnable {
	private Connection conn;

	public AsyncKeepAlive(Connection conn) {
		this.conn = conn;
	}

	public void run() {
		try {
			conn.createStatement().executeQuery("SELECT 1;");
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}
}
