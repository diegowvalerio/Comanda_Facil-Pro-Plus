package br.com.dw.comanda_facil_pro_plus.telas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.util.Installer;

public class Config extends AppCompatActivity {
    EditText conf;
    String configJ;
    static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE_PERMISSION=1;

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

        writeStoragePermissionGranted();
    }

    public void installservidor(View view) {
        if (!isAppInstalled()) {
            File path = new File(Environment.getExternalStorageDirectory(), "Comanda_Facil/servidor");
            path.mkdirs();
            AssetManager assetManager = getAssets();

            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open("mariadbapplication.apk");
                out = new FileOutputStream(path + "mariadbapplication.apk");
                byte[] buffer = new byte[1024];

                int read;
                while ((read = in.read(buffer)) != -1) {

                    out.write(buffer, 0, read);

                }

                in.close();
                in = null;

                out.flush();
                out.close();
                out = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Uri apkUri = FileProvider.getUriForFile(this, "br.com.dw.comanda_facil_pro_plus.fileprovider", new File(path + "mariadbapplication.apk"));
                    Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                    intent.setData(apkUri);
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(path + "mariadbapplication.apk")), "application/vnd.android.package-archive");
                    startActivity(intent);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "Servidor já está instalado !", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isAppInstalled(){
        List list = getListOfInstalledApps();
        for (int i = 0; i < list.size();i++){
            if (list.get(i).toString().contains("com.esminis.server.mariadb"))
                return true;
        }
        return false;
    }
    private List getListOfInstalledApps(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return getPackageManager().queryIntentActivities( mainIntent, 0);
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
}