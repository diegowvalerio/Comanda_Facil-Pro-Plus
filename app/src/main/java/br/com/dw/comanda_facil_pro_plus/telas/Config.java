package br.com.dw.comanda_facil_pro_plus.telas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;

public class Config extends AppCompatActivity {
    EditText conf;
    String configJ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        conf = findViewById(R.id.ed_conexao);

        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = lt.edit();
        configJ = lt.getString("config","vazio");

        if(configJ.equals("vazio")){
            Toast.makeText(this, "Preencha a configuração com o IP do servidor", Toast.LENGTH_SHORT).show();
            editor.putString("config","");
            editor.commit();
        }else{
            conf.setText(configJ);
        }

    }

    public void gravaconfig(View view){
        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = lt.edit();
        editor.putString("config", conf.getText().toString());
        editor.commit();
        testar();
        finish();
    }

    public void testar(){
        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = lt.edit();
        configJ = lt.getString("config","vazio");

        if(configJ.equals("vazio")){
            Toast.makeText(this, "Preencha a configuração com o IP do servidor", Toast.LENGTH_SHORT).show();
        }else{
            //criar tabelas do banco
            Conexao conexao = new Conexao();
            try {
                conexao.conexao(getApplicationContext()).initialize();
                Toast.makeText(this, ""+conexao.getTeste().toString(), Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}