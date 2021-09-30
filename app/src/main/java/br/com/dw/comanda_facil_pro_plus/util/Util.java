package br.com.dw.comanda_facil_pro_plus.util;

import java.io.IOException;


public class Util {

    public static  boolean isOnline() throws InterruptedException, IOException {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }
}
