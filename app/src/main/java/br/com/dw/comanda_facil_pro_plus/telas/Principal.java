package br.com.dw.comanda_facil_pro_plus.telas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.adapters.Adp_Comanda;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.banco.DatabaseHelper;
import br.com.dw.comanda_facil_pro_plus.dao.Dao_Comanda;
import br.com.dw.comanda_facil_pro_plus.dao.Dao_Mesa;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;
import br.com.dw.comanda_facil_pro_plus.telas.comanda.Comandas_Mesa;
import br.com.dw.comanda_facil_pro_plus.telas.mesa.Mesas;
import br.com.dw.comanda_facil_pro_plus.telas.produto.Produtos;
import br.com.dw.comanda_facil_pro_plus.telas.relatorio.Relatorios;

public class Principal extends AppCompatActivity implements AdapterView.OnItemClickListener {
    GridView gridView;
    List<Mesa> mesas = new ArrayList<>();
    List<Comanda> comadas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        gridView = findViewById(R.id.gridview);
        gridView.setOnItemClickListener(this);
    }

    public void tela_produtos(View view)  {
            Intent intent = new Intent(this, Produtos.class);
            startActivity(intent);
    }

    public void tela_relatorios(View view)  {
            Intent intent = new Intent(this, Relatorios.class);
            startActivity(intent);
    }

    public void tela_mesas(View view)  {
            Intent intent = new Intent(this, Mesas.class);
            startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenchelista();
    }

    private void preenchelista() {
        //ABERTO //PARCIAL //ATENDIDO //FECHADO //FECHADO_PARCIAL
        try {
            final Conexao conexao = new Conexao();
            conexao.conexao(getApplicationContext()).initialize();
            final Dao dao_mesa = conexao.getDao(Mesa.class);
            final Dao dao_comanda = conexao.getDao(Comanda.class);

            Thread thread= new Thread(){
                @Override public void run() {
                    try {
                        mesas = dao_mesa.queryBuilder().where().eq("status",true).query();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
            long delayMillis = 5000;
            try {
                thread.join(delayMillis);
                if (thread.isAlive()) {
                    Toast.makeText(this, "Execidido tempo de conexção com servidor !", Toast.LENGTH_SHORT).show();
                }
            } catch (InterruptedException e){

            }

            final Object[] status = {"ABERTO","PARCIAL","ATENDIDO","FECHADO_PARCIAL"};
            for(final Mesa m:mesas){
                int a = 0,p = 0,at = 0,fp =0;
                comadas.clear();
                Thread thread2 = new Thread(){
                    @Override public void run() {
                        try {
                            comadas = dao_comanda.queryBuilder().where().eq("mesa",m.getId()).and().in("status",status).query();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread2.start();
                try {
                    thread2.join(delayMillis);
                    if (thread2.isAlive()) {
                        Toast.makeText(this, "Execidido tempo de conexção com servidor !", Toast.LENGTH_SHORT).show();
                    }else{
                        m.setTotalcomandas(comadas.size());
                        for(Comanda c:comadas){
                            if(c.getStatus().equals("ABERTO")){
                                a++;
                            }else if(c.getStatus().equals("PARCIAL")){
                                p++;
                            }else if(c.getStatus().equals("ATENDIDO")){
                                at++;
                            }else if(c.getStatus().equals("FECHADO_PARCIAL")){
                                fp++;
                            }
                        }
                        m.setTotal_aberto(a);
                        m.setTotal_parcial(p);
                        m.setTotal_atendido(at);
                        m.setTotal_fechado_parcial(fp);
                    }
                } catch (InterruptedException e){

                }
            }
            gridView.setAdapter(new Adp_Comanda(this,mesas));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Mesa mesa = (Mesa) parent.getItemAtPosition(position);
            Intent intent = new Intent(this, Comandas_Mesa.class);
            intent.putExtra("id", mesa.getId());
            startActivity(intent);
            //Toast.makeText(this, "Selecionado: "+mesa.getDescricao(), Toast.LENGTH_SHORT).show();

    }
}