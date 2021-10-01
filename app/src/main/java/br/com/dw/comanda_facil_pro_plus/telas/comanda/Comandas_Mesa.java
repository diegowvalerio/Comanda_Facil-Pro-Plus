package br.com.dw.comanda_facil_pro_plus.telas.comanda;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.adapters.Adp_ComandasMesas;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;


public class Comandas_Mesa extends AppCompatActivity implements AdapterView.OnItemClickListener,AdapterView.OnItemLongClickListener {
    final Conexao conexao = new Conexao();
    Dao dao_comanda;
    Dao dao_mesa;

    Mesa mesa;
    TextView mesaselecionada;
    List<Comanda> comandas = new ArrayList<>();
    int idmesa;
    ListView listView;
    Adp_ComandasMesas adp_comandasMesas;
    int n = 0;
    private AlertDialog alerta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda);

        try {
            conexao.conexao(getApplicationContext()).initialize();
            dao_comanda = conexao.getDao(Comanda.class);
            dao_mesa = conexao.getDao(Mesa.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mesaselecionada = findViewById(R.id.mesaselecionada);
        listView = findViewById(R.id.listvew_pedidos);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche();
    }

    public void comanda_pedido(View view){
        Intent intent = new Intent(this, Comanda_Pedido.class);
        intent.putExtra("id", idmesa);
        startActivity(intent);
    }

    private void preenche() {
        //busca comandas em aberto,parcial ,atendido da mesa selecionada.

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("id")) {
            idmesa = bundle.getInt("id");

            Thread thread= new Thread(){
                @Override public void run() {
                    try {
                        mesa = (Mesa) dao_mesa.queryForId(idmesa);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            long delayMillis = 5000;
            try {
                thread.join(delayMillis);
                if (thread.isAlive()) {}
            } catch (InterruptedException e){}

            mesaselecionada.setText(mesa.getDescricao());
            final Object[] status = {"ABERTO","PARCIAL","ATENDIDO"};

            Thread thread2= new Thread(){
                @Override public void run() {
                    try {
                        comandas = dao_comanda.queryBuilder().where().eq("mesa",idmesa).and().in("status",status).query();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread2.start();
            try {
                thread2.join(delayMillis);
                if (thread2.isAlive()) {}
            } catch (InterruptedException e){}

            adp_comandasMesas = new Adp_ComandasMesas(this,comandas);
            listView.setAdapter(adp_comandasMesas);

        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Comanda comanda = (Comanda) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, Comanda_Pedido.class);
        intent.putExtra("idcomanda",comanda.getId());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Comanda comanda = (Comanda) parent.getItemAtPosition(position);
        if(comanda.getValor_total() > 0){
            Toast.makeText(this, "Para excluir a Comanda, remova todos os itens ! ", Toast.LENGTH_SHORT).show();
        }else{
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ArrayList<String> itens = new ArrayList<>();
            itens.add("Sim");
            itens.add("Não");
            ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_alerta, itens);
            builder.setTitle("Confirma Exclusão da Comanda ?");
            builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if(arg1 == 0){
                        try {
                            dao_comanda.delete(comanda);
                            Toast.makeText(Comandas_Mesa.this, "Comanda Excluído com Sucesso !", Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Toast.makeText(Comandas_Mesa.this, "Erro ao Excluir Comanda !", Toast.LENGTH_SHORT).show();
                        }
                        alerta.dismiss();
                        preenche();
                    }else if(arg1 ==1){
                        alerta.dismiss();
                    }
                }
            });
            alerta = builder.create();
            alerta.show();
        }
        return true;
    }
}