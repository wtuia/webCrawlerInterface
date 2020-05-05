package bingimg.db;

import bingimg.bean.BingImg;
import common.ConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Objects;

public class DB {
	private static final Logger logger = LoggerFactory.getLogger(DB.class);
	public static void save(BingImg bingImg) {
		String sql = "INSERT INTO bingImg(url,title,titleUrl,savePath,recordTime) VALUES (?,?,?,?,?)";
		Connection conn = null;
		PreparedStatement ps =null;
		try {
			conn = Objects.requireNonNull(ConnectionManager.getConnection());
			ps = conn.prepareStatement(sql);
			ps.setString(1, bingImg.getImageUrl());
			ps.setString(2, bingImg.getTitle());
			ps.setString(3, bingImg.getTitleUrl());
			ps.setString(4, bingImg.getFullName());
			ps.setTimestamp(5, new Timestamp(bingImg.getRecordTime().getTime()));
			ps.executeUpdate();
		}catch (Exception e){
			logger.error("", e);
		}finally {
			ConnectionManager.closeConnectionAndPreparedStatement(conn, ps);
		}
	}
}
