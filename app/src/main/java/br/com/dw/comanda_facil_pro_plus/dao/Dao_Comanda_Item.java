package br.com.dw.comanda_facil_pro_plus.dao;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import br.com.dw.comanda_facil_pro_plus.entidades.Comanda_Item;

public class Dao_Comanda_Item extends BaseDaoImpl<Comanda_Item,Integer> {

    public Dao_Comanda_Item(ConnectionSource connectionSource) throws SQLException {
        super(Comanda_Item.class);
        setConnectionSource(connectionSource);
        initialize();
    }
}
