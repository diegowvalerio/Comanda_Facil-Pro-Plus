package br.com.dw.comanda_facil_pro_plus.telas.produto;

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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.adapters.Adp_produtos;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda_Item;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;
import br.com.dw.comanda_facil_pro_plus.entidades.Produto;

public class Produtos extends AppCompatActivity  implements  AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView listView;
    private List<Comanda_Item> comanda_items = new ArrayList<>();
    private Produto produto = new Produto();
    private Adp_produtos adp_produtos;
    private List<Produto> produtos = new ArrayList<>();
    private List<Produto> produtos_filtrados = new ArrayList<>();
    private EditText filtro;
    private AlertDialog alerta;
    ImageButton btn_leitura;
    CheckBox filtro_ativo;
    final Activity activity = this;
    int v =0;

    final Conexao conexao = new Conexao();
    Dao dao_produto;
    Dao dao_comanda_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produtos);

        try {
            conexao.conexao(getApplicationContext()).initialize();
            dao_comanda_item = conexao.getDao(Mesa.class);
            dao_produto = conexao.getDao(Produto.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        filtro = findViewById(R.id.p_filtro);
        filtro_ativo = findViewById(R.id.filtro_ativo);
        filtro_ativo.setChecked(true);
        filtro_ativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pesquisar_ativos();
                adp_produtos = new Adp_produtos(Produtos.this, produtos_filtrados);
                listView.setAdapter(adp_produtos);
            }
        });

        listView = findViewById(R.id.listview_produtos);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);
        btn_leitura = findViewById(R.id.btn_leitura2);
        btn_leitura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Leitor de Código de Barras");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //filtro.setText("");
        preenchelista();
    }

    public void preenchelista(){
        Thread thread= new Thread(){
            @Override public void run() {
                try {
                    produtos = dao_produto.queryForAll();
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
            adp_produtos = new Adp_produtos(this, produtos_filtrados);
            listView.setAdapter(adp_produtos);
            listView.setTextFilterEnabled(true);
            filtro.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Pesquisar();
                    adp_produtos = new Adp_produtos(Produtos.this, produtos_filtrados);
                    listView.setAdapter(adp_produtos);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }
    }

    private void Pesquisar_ativos(){
        produtos_filtrados.clear();
        for(int i = 0;i < produtos.size();i++){
            Produto data =produtos.get(i);
            if(!filtro.getText().equals("")){
                Pesquisar();
            }else if(filtro_ativo.isChecked() == data.isStatus()){
                produtos_filtrados.add(data);
            }
        }
    }

    private void Pesquisar() {
        produtos_filtrados.clear();
        for(int i = 0;i < produtos.size();i++){
            Produto data =produtos.get(i);
            if(!filtro.getText().equals("")){
                String pq = filtro.getText().toString().toLowerCase();
                String condicao = data.getDescricao().toLowerCase();
                String condicao2 = data.getId().toString();
                String condicao3 = data.getEan();
                String condicao4 = Double.toString(data.getValor());
                if((condicao.contains(pq) || condicao2.contains(pq) || condicao3.contains(pq) || condicao4.contains(pq)) && (filtro_ativo.isChecked() == data.isStatus())){
                    produtos_filtrados.add(data);
                }
            }else{
                produtos_filtrados.addAll(produtos);
            }
        }
    }

    public void tela_produto(View view)  {
        Intent intent = new Intent(this, TelaProduto.class);
        startActivity(intent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Produto produto = (Produto) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, TelaProduto.class);
        intent.putExtra("id", produto.getId());
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        final Produto p = (Produto) parent.getItemAtPosition(position);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ArrayList<String> itens = new ArrayList<>();
        itens.add("Sim");
        itens.add("Não");
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.item_alerta, itens);
        builder.setTitle("Confirma Exclusão do Produto ?");
        builder.setSingleChoiceItems(adapter, 0, new DialogInterface.OnClickListener() {
            @RequiresApi (api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if(arg1 == 0){
                    try {
                        comanda_items.clear();
                        Thread thread= new Thread(){
                            @Override public void run() {
                                try {
                                    comanda_items = dao_comanda_item.queryBuilder().where().eq("produto",p.getId()).query();
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        long delayMillis = 5000;
                        try {thread.join(delayMillis);
                            if (thread.isAlive()) {}
                        } catch (InterruptedException e){}

                        if(comanda_items.size() >0){
                            Toast.makeText(activity, "Produto já utilizado em comandas, não é possível excluir !", Toast.LENGTH_SHORT).show();
                        }else {
                            dao_produto.delete(p);
                            Toast.makeText(Produtos.this, "Produto Excluído com Sucesso !", Toast.LENGTH_SHORT).show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Toast.makeText(Produtos.this, "Erro ao Excluir Produto!", Toast.LENGTH_SHORT).show();
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

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            if (result.getContents() != null) {
                filtro.setText(result.getContents());
                Pesquisar();
                v = 1;
            } else {
                Toast.makeText(activity, "Leitor Cancelado", Toast.LENGTH_SHORT).show();
            }

        }
    }
}