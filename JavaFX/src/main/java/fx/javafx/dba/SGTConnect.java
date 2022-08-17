package fx.javafx.dba;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SGTConnect {

    public static Connection sgtConnection(){
        Connection conn = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String polaczenie = "jdbc:sqlserver://localhost:1433;databaseName=Sklep;user=sa;password=";
            conn = DriverManager.getConnection(polaczenie);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(BDConnect.class.getName()).log(Level.SEVERE,null,e);
        }

        return conn;
    }

}
