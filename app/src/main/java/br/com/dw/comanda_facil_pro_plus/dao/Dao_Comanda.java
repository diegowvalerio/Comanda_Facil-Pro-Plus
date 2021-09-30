package br.com.dw.comanda_facil_pro_plus.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;

public class Dao_Comanda extends BaseDaoImpl<Comanda,Integer> {

    public Dao_Comanda(ConnectionSource connectionSource) throws SQLException {
        super(Comanda.class);
        setConnectionSource(connectionSource);
        initialize();
    }
}
