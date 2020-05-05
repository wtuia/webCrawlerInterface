package common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionManager {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private static int CONN_NUM;


    public static Connection getConnection() throws NullPointerException{
        try {
            CONN_NUM ++;
            logger.info("当前连接数:{}", CONN_NUM);
            return JDBCHelper.getConnection();
        } catch (SQLException e) {
            logger.error("获取数据库连接池出错",e);
        }
        throw new NullPointerException("获取连接为空");
    }


    public static void closeConnection(Connection connection) {
        if(connection != null) {
            try {
                CONN_NUM--;
                connection.close();
            } catch (SQLException e) {
                logger.error("关闭数据连接出错",e);
            }
        }
    }

    public static void closePreparedStatement(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                logger.error("PreparedStatement关闭异常",e);
            }
        }
    }

    public static void rollback(Connection connection) {
        if (connection != null) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                logger.info("数据库回滚出错",e);
            }
        }
    }

    public static void setAutoCommit(Connection connection, boolean flag) {
        if (connection != null ) {
            try {
                connection.setAutoCommit(flag);
            } catch (SQLException e) {
                logger.error("设置自动提交为{}异常", flag, e);
            }
        }
    }

    public static void  closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                logger.error("ResultSet关闭异常",e);
            }
        }
    }

    public static void closeConnectionAndPreparedStatement(Connection connection,
                                                           PreparedStatement ps) {
        if(ps != null) {
            closePreparedStatement(ps);
        }
        if (connection != null) {
            closeConnection(connection);
        }
    }

    public static void release(Connection connection, PreparedStatement ps, ResultSet rs) {
        if( rs != null) {
            closeResultSet(rs);
        }
        if(ps != null) {
            closePreparedStatement(ps);
        }
        if (connection != null) {
            closeConnection(connection);
        }
    }
}
