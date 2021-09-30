package br.com.dw.comanda_facil_pro_plus.telas.mesa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.banco.DatabaseHelper;
import br.com.dw.comanda_facil_pro_plus.dao.Dao_Mesa;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;

public class TelaMesa extends AppCompatActivity {

    EditText m_descricao;
    CheckBox m_ativo;

    Mesa mesa = new Mesa();
    final Conexao conexao = new Conexao();
    Dao dao_mesa;
    int v =0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_mesa);

        try {
            conexao.conexao(getApplicationContext()).initialize();
            dao_mesa = conexao.getDao(Mesa.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        m_descricao = findViewById(R.id.m_descricao);
        m_ativo = findViewById(R.id.m_ativo);
    }

    public void salvar(View view){
        if(m_descricao.getText().length() >0){
            mesa.setDescricao(m_descricao.getText().toString().toUpperCase());
            mesa.setStatus(m_ativo.isChecked());
            Thread thread= new Thread(){
                @Override public void run() {
                    try {
                        dao_mesa.createOrUpdate(mesa);
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
                }else{
                    Toast.makeText(this, "Mesa salva com Sucesso !", Toast.LENGTH_SHORT).show();
                    finish();
                }
            } catch (InterruptedException e){
            }

        }else{
            Toast.makeText(this, "Preencha o nome da Mesa !", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche();
    }

    private void preenche() {
        if( v == 0) {
            final Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.containsKey("id")) {
                Thread thread= new Thread(){
                    @Override public void run() {
                        try {
                            mesa = (Mesa) dao_mesa.queryForId(bundle.getInt("id"));
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
                    }else{
                        if (!mesa.getId().equals("")) {
                            m_descricao.setText(mesa.getDescricao());
                            m_ativo.setChecked(mesa.isStatus());
                            v = 1;
                        }
                    }
                } catch (InterruptedException e){

                }

            }
        }
    }
}