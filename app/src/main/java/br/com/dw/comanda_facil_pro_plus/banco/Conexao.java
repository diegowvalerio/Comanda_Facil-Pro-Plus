package br.com.dw.comanda_facil_pro_plus.banco;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.db.DatabaseTypeUtils;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda_Item;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;
import br.com.dw.comanda_facil_pro_plus.entidades.Produto;

public class Conexao {
    public static JdbcPooledConnectionSource cSource = new JdbcPooledConnectionSource();
    private static Conexao instance = null;
    String configJ;
    String teste;
    public void criarbanco(){
        Thread thread= new Thread(){
            @Override public void run() {
                try {
                    Class.forName("org.mariadb.jdbc.Driver");
                    Connection conn = DriverManager.getConnection("jdbc:mariadb://"+configJ, "root", "") ;
                    Statement stmt = conn.createStatement() ;
                    String query = "CREATE DATABASE IF NOT EXISTS DB;" ;
                    ResultSet rs = stmt.executeQuery(query) ;
                    conn.close();
                    teste = "Conexão com servidor efetuada !";
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                    teste = "Erro conexão com o servidor";
                }
            }
        };
        thread.start();
        long delayMillis = 5000;
        try {
            thread.join(delayMillis);
            if (thread.isAlive()) {
               Log.e("ERRO","Não foi possivél criar banco");
            }
        } catch (InterruptedException e){

        }
    }

    public JdbcPooledConnectionSource conexao(Context context) throws SQLException {
        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = lt.edit();
        configJ = lt.getString("config","vazio");
        if(!configJ.equals("vazio")) {
            criarbanco();
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                cSource.setDatabaseType(new MariaDBType());
                cSource.setUrl("jdbc:mariadb://" + configJ + "/DB?user=root&password=");
                cSource.setCheckConnectionsEveryMillis(5000);
                cSource.setMaxConnectionsFree(5);
                cSource.initialize();
            } catch (ClassNotFoundException cnfe) {
                cnfe.printStackTrace();
            }

        }
        return cSource;
    }

    public void criatabelas(){
        new Task().execute();
    }

    public synchronized <D extends Dao<T, ?>, T> D getDao(Class<T> cls) throws SQLException {
        Dao<T, ?> dao = DaoManager.createDao(cSource, cls);
        D daoImpl = (D) dao;

        return daoImpl;
    }

    public synchronized static Conexao getInstance() throws SQLException {
        if (instance == null) instance = new Conexao();
        return instance;
    }

    class Task extends AsyncTask<String, Void, Void> {

        private Exception exception;

        protected Void doInBackground(String... urls) {
            try {
                ;
                //criar as tabelas do banco
                TableUtils.createTableIfNotExists(cSource, Produto.class);
                TableUtils.createTableIfNotExists(cSource, Mesa.class);
                TableUtils.createTableIfNotExists(cSource, Comanda.class);
                TableUtils.createTableIfNotExists(cSource, Comanda_Item.class);
            } catch (Exception e) {
                this.exception = e;
                e.printStackTrace();
                return null;
            }
            return null;
        }

    }

    public String getTeste() {
        return teste;
    }

    public void setTeste(String teste) {
        this.teste = teste;
    }
}
