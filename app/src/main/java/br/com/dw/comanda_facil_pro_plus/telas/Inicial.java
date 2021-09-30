package br.com.dw.comanda_facil_pro_plus.telas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.sql.SQLException;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;

public class Inicial extends AppCompatActivity {
    static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION=1;
    static final int REQUEST_CODE_CAMERA_STORAGE_PERMISSION = 1;
    String configJ;
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicial);
        //chama permissão
        writeStoragePermissionGranted();
        cameraPermissionGranted();
        //fim permissões

        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = lt.edit();
        configJ = lt.getString("config","vazio");

        if(configJ.equals("vazio") || configJ.equals("") ){
            Toast.makeText(this, "Preencha a configuração com o IP do servidor", Toast.LENGTH_SHORT).show();
        }else{
            //criar tabelas do banco
            Conexao conexao = new Conexao();
            try {
                conexao.conexao(getApplicationContext()).initialize();
                conexao.criatabelas();
                Toast.makeText(this, ""+conexao.getTeste().toString(), Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void tela_principal(View view) throws IOException, InterruptedException {
        SharedPreferences lt = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = lt.edit();
        configJ = lt.getString("config","vazio");
        if(configJ.equals("vazio") || configJ.equals("") ){
            Toast.makeText(this, "Preencha a configuração com o IP do servidor", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, Principal.class);
            startActivity(intent);
        }
    }

    public void tela_config(View view) throws IOException, InterruptedException {
            Intent intent = new Intent(this, Config.class);
            startActivity(intent);
    }


    //chama permissões
    public void writeStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //startPeriodicRequest();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION);
            }
        } else {
            return;
        }
    }

    public void cameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //startPeriodicRequest();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA_STORAGE_PERMISSION);
            }
        } else {
            return;
        }
    }
}