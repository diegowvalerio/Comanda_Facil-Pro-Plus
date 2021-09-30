package br.com.dw.comanda_facil_pro_plus.telas.mesa;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.j256.ormlite.dao.Dao;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.adapters.Adp_Comanda;
import br.com.dw.comanda_facil_pro_plus.adapters.Adp_mesas;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.banco.DatabaseHelper;
import br.com.dw.comanda_facil_pro_plus.dao.Dao_Comanda;
import br.com.dw.comanda_facil_pro_plus.dao.Dao_Mesa;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;


public class Mesas extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView listView;
    private Adp_mesas adp_mesas;
    private List<Mesa> mesas = new ArrayList<>();
    private List<Mesa> mesasfiltradas = new ArrayList<>();
    private List<Comanda> comandas = new ArrayList<>();
    private EditText filtro;
    private AlertDialog alerta;
    CheckBox filtro_ativo;
    final Activity activity = this;
    int v =0;

    final Conexao conexao = new Conexao();
    Dao dao_mesa;
    Dao dao_comanda;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesas);


        try {
            conexao.conexao(getApplicationContext()).initialize();
            dao_mesa = conexao.getDao(Mesa.class);
            dao_comanda = conexao.getDao(Comanda.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filtro = findViewById(R.id.m_filtro);
        filtro_ativo = findViewById(R.id.filtro_ativo);
        filtro_ativo.setChecked(true);
        filtro_ativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pesquisar_ativos();
                adp_mesas = new Adp_mesas(Mesas.this, mesasfiltradas);
                listView.setAdapter(adp_mesas);
            }
        });
        listView = findViewById(R.id.listview_mesas);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
    }

    public void tela_mesa(View view) throws IOException, InterruptedException {
        Intent intent = new Intent(this, TelaMesa.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Mesa mesa = (Mesa) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, TelaMesa.class);
        intent.putExtra("id",mesa.getId());
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        filtro.setText("");
        preenchelista();
    }

    private void preenchelista() {
        Thread thread= new Thread(){
            @Override public void run() {
                try {
                    mesas = dao_mesa.queryForAll();
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

        if( v == 0) {
            Pesquisar_ativos();
            adp_mesas = new Adp_mesas(this,mesasfiltradas);
            listView.setAdapter(adp_mesas);
            listView.setTextFilterEnabled(true);
            filtro.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Pesquisar();
                    adp_mesas = new Adp_mesas(Mesas.this, mesasfiltradas);
                    listView.setAdapter(adp_mesas);
                }
                @Override
                public void afterTextChanged(Editable s) {

                }
            });

        }
    }

    private void Pesquisar() {
        mesasfiltradas.clear();
        for(int i = 0;i < mesas.size();i++){
            Mesa data =mesas.get(i);
            if(!filtro.getText().equals("")){
                String pq = filtro.getText().toString().toLowerCase();
                String condicao = data.getDescricao().toLowerCase();
                String condicao2 = data.getId().toString();

                if((condicao.contains(pq) || condicao2.contains(pq) ) && (filtro_ativo.isChecked() == data.isStatus())){
                    mesasfiltradas.add(data);
                }
            }else{
                mesasfiltradas.addAll(mesas);
            }
        }
    }

    private void Pesquisar_ativos(){
        mesasfiltradas.clear();
        for(int i = 0;i < mesas.size();i++){
            Mesa data =mesas.get(i);
            if(!filtro.getText().equals("")){
                Pesquisar();
            }else if(filtro_ativo.isChecked() == data.isStatus()){
                mesasfiltradas.add(data);
            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Mesa p = (Mesa) parent.getItemAtPosition(position);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayList<String> itens = new ArrayList<>();
        itens.add("Sim");
        itens.add("Não");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_alerta, itens);
        builder.setTitle("Confirma Exclusão da Mesa ?");
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(arg1 == 0){
                    try {
                        comandas.clear();
                        new Thread( new Runnable() { @Override public void run() {
                            try {
                                comandas = dao_comanda.queryBuilder().where().eq("mesa",p.getId()).query();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        } } ).start();

                        if(comandas.size() >0){
                            Toast.makeText(activity, "Mesa já utilizada em comandas, não é possível excluir !", Toast.LENGTH_SHORT).show();
                        }else {
                            dao_mesa.delete(p);
                            Toast.makeText(Mesas.this, "Mesa Excluído com Sucesso !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(Mesas.this, "Erro ao Excluir Mesa !", Toast.LENGTH_SHORT).show();
                    }
                    alerta.dismiss();
                    preenchelista();
                }else if(arg1 ==1){
                    alerta.dismiss();
                }
            }
        });
        alerta = builder.create();
        alerta.show();
        return true;
    }
}