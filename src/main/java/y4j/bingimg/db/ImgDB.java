package y4j.bingimg.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import y4j.bingimg.bean.BingImg;
import y4j.bingimg.job.TypeEnum;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ImgDB {
	private static final Logger logger = LoggerFactory.getLogger(ImgDB.class);
	
	@Autowired
	private DataSource dataSource;
	
	public void save(BingImg bingImg) {
		delNow(bingImg.getRecordTime());
		String sql = "INSERT INTO bingImg(title,titleUrl,savePath,hdUrl,uhdUrl,phoneUrl,recordTime) VALUES (?,?,?,?,?,?,?)";
		try (Connection conn = Objects.requireNonNull(dataSource.getConnection());
		     PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, bingImg.getTitle());
			ps.setString(2, bingImg.getTitleUrl());
			ps.setString(3, bingImg.getFullName());
			ps.setString(4, bingImg.getImageUrl().get(TypeEnum.HD.getType()));
			ps.setString(5, bingImg.getImageUrl().get(TypeEnum.UHD.getType()));
			ps.setString(6, bingImg.getImageUrl().get(TypeEnum.PHONE.getType()));
			ps.setDate(7, bingImg.getRecordTime());
			ps.executeUpdate();
		}catch (Exception e){
			logger.error("", e);
		}
	}
	
	public void delNow(Date date) {
		String sql = "delete from bingImg where recordTime = ?";
		try (Connection conn = Objects.requireNonNull(dataSource.getConnection());
		     PreparedStatement ps = conn.prepareStatement(sql)){
		    ps.setDate(1, date);
		    ps.execute();
		}catch (Exception e) {
		    logger.error("",e);
		}
	}
	
	public List<BingImg> get() {
		String sql = "select title,titleUrl,recordTime from bingImg limit 10";
		List<BingImg> bingImgs = new ArrayList<>();
		try (Connection conn = Objects.requireNonNull(dataSource.getConnection());
		     PreparedStatement ps = conn.prepareStatement(sql);
		     ResultSet rs = ps.executeQuery()){
				while (rs.next()) {
					bingImgs.add(new BingImg());
				}
		}catch (Exception e) {
			logger.error("",e);
		}
		return bingImgs;
	}
}
