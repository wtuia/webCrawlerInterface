package common;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

class JDBCHelper {

    private static final Logger logger = LoggerFactory.getLogger(JDBCHelper.class);
    private static final Map<String, DataSource> DATA_SOURCE_MAP = new HashMap<>();

    static Connection getConnection() throws SQLException {
      DataSource ds = DATA_SOURCE_MAP.get("conn");
      if (ds == null) {
          synchronized (JDBCHelper.class) {
              ds = DATA_SOURCE_MAP.get("conn");
              if (ds == null) {
                  ds = initDataSource();
                  DATA_SOURCE_MAP.put("conn", ds);
              }
          }
      }
      return ds.getConnection();
  }

      private static  DataSource initDataSource() {
        ComboPooledDataSource cbPoolDS = new ComboPooledDataSource();
        try {
            cbPoolDS.setDriverClass("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            logger.error("数据库驱动加载失败",e);
        }
        cbPoolDS.setJdbcUrl("jdbc:mysql://139.155.83.148:3306/y4j" +
				"?useUnicode=true&characterEncoding=UTF-8&useFastDateParsing=false");
        cbPoolDS.setUser("root");
        cbPoolDS.setPassword("Root123.");
        cbPoolDS.setInitialPoolSize(1);
        cbPoolDS.setMinPoolSize(1);
        cbPoolDS.setMaxPoolSize(5);
        cbPoolDS.setMaxIdleTime(60);
        return cbPoolDS;
    }
}
