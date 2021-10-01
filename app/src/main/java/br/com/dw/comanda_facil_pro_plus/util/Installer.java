package br.com.dw.comanda_facil_pro_plus.util;

import java.io.File;
        import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.content.FileProvider;

import br.com.dw.comanda_facil_pro_plus.BuildConfig;

public class Installer {

    private static Context context = null;
    private static File file = null;
    public static final String PACKAGE_NAME = "com.esminis.server.mariadb";
    private static BroadcastReceiver receiver = null;


    public Installer(Context context, File file){
        Installer.context = context;
        Installer.file = file;
    }

    public boolean install(){
        if (!isAppInstalled()){
            registerReceiver();
            installApk();
            return true;
        }
        return false;
    }

    private boolean installApk(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, "br.com.dw.comanda_facil_pro_plus.fileprovider", file);
            Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean isAppInstalled(){
        List list = getListOfInstalledApps();
        for (int i = 0; i < list.size();i++){
            if (list.get(i).toString().contains(PACKAGE_NAME))
                return true;
        }
        return false;
    }

    private List getListOfInstalledApps(){
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        return context.getPackageManager().queryIntentActivities( mainIntent, 0);
    }

    private void registerReceiver(){
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        context.registerReceiver(receiver, filter);
    }

    public static void unregisterReceiver(){
        context.unregisterReceiver(receiver);
    }

    public static void onApkInstalled(){
        unregisterReceiver();
    }
}