package br.com.dw.comanda_facil_pro_plus.telas.comanda;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Date;

import br.com.dw.comanda_facil_pro_plus.R;
import br.com.dw.comanda_facil_pro_plus.banco.Conexao;
import br.com.dw.comanda_facil_pro_plus.banco.DatabaseHelper;
import br.com.dw.comanda_facil_pro_plus.dao.Dao_Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda;
import br.com.dw.comanda_facil_pro_plus.entidades.Comanda_Item;
import br.com.dw.comanda_facil_pro_plus.entidades.Mesa;
import br.com.dw.comanda_facil_pro_plus.entidades.Produto;

public class Comanda_Pagamento extends AppCompatActivity {

    private TextInputEditText vltotal,vldesconto,vltroco,vlpago,vlrecebido;
    Comanda comanda = new Comanda();
    final Conexao conexao = new Conexao();
    Dao dao_comanda;
    double recebido = 0.00,troco = 0.00,pago = 0.00,desconto = 0.00;
    DecimalFormat df = new DecimalFormat("#,###.00");
    final Activity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comanda__pagamento);

        try {
            conexao.conexao(getApplicationContext()).initialize();
            dao_comanda = conexao.getDao(Comanda.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        vltotal = findViewById(R.id.ed_total);
        vldesconto = findViewById(R.id.ed_desconto);
        vlpago = findViewById(R.id.ed_pago);
        vltroco = findViewById(R.id.ed_troco);
        vlrecebido = findViewById(R.id.ed_recebido);

        vltotal.setEnabled(false);
        vltroco.setEnabled(false);
        vltroco.setText("0.00");
        vlrecebido.setEnabled(false);
        vlrecebido.setText("0.00");
        vlpago.setText("0.00");
        vldesconto.setText("0.00");
        //editavel somente vldesconto e vltroco
        vldesconto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!vlpago.getText().toString().trim().equals("")) {
                    pago = Double.valueOf(vlpago.getText().toString());
                }else{
                    pago = 00;
                }
                if(!vldesconto.getText().toString().trim().equals("")){
                    desconto = Double.valueOf(vldesconto.getText().toString());
                }
                if(pago+desconto > comanda.getValor_total()){
                    if(desconto > 0){
                        troco = pago - (comanda.getValor_total()-desconto);
                    }else{
                        troco = pago - comanda.getValor_total();
                    }
                }else{
                    troco = 0.00;
                }

                recebido= (comanda.getValor_total()-desconto);


                df.format(troco);
                df.format(desconto);
                df.format(pago);
                df.format(recebido);
                comanda.setValor_troco(troco);
                comanda.setValor_desconto(desconto);
                comanda.setValor_pago(pago);
                comanda.setValor_recebido(recebido);

                vltroco.setText(df.format(troco));
                vlrecebido.setText(df.format(recebido));
            }
        });

        vlpago.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!vlpago.getText().toString().trim().equals("")) {
                    pago = Double.valueOf(vlpago.getText().toString());
                }else{
                    pago = 00;
                }
                if(!vldesconto.getText().toString().trim().equals("")){
                    desconto = Double.valueOf(vldesconto.getText().toString());
                }else{
                    desconto = 0;
                }

                if(pago+desconto > comanda.getValor_total()){
                    if(desconto > 0){
                        troco = pago - (comanda.getValor_total()-desconto);
                    }else{
                        troco = pago - comanda.getValor_total();
                    }
                }else{
                    troco = 0.00;
                }

                recebido= (comanda.getValor_total()-desconto);

                df.format(troco);
                df.format(desconto);
                df.format(pago);
                df.format(recebido);
                comanda.setValor_troco(troco);
                comanda.setValor_desconto(desconto);
                comanda.setValor_pago(pago);
                comanda.setValor_recebido(recebido);

                vltroco.setText(df.format(troco));
                vlrecebido.setText(df.format(recebido));
            }
        });

        vlrecebido.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(df.format(comanda.getValor_total()).equals(df.format(comanda.getValor_pago()+comanda.getValor_desconto()-comanda.getValor_troco()))){
                    vlrecebido.setTextColor(Color.BLUE);
                }else{
                    vlrecebido.setTextColor(Color.RED);
                }
            }
        });

        vltroco.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                vltroco.setTextColor(Color.LTGRAY);
                if(!vltroco.getText().toString().trim().equals("") && !vltroco.getText().toString().trim().equals(",00")) {
                    String v = vltroco.getText().toString().replace(",",".");
                    if(Double.valueOf(v) > 0){
                        vltroco.setTextColor(Color.GREEN);
                    }else{
                        vltroco.setTextColor(Color.LTGRAY);
                    }
                }

            }
        });
    }

    public void finalizar(View view){
        if(comanda.getValor_recebido() > 0){
            if(df.format(comanda.getValor_total()).equals(df.format(comanda.getValor_pago()+comanda.getValor_desconto()-comanda.getValor_troco()))){
                AlertDialog.Builder mensagem = new AlertDialog.Builder(activity);
                mensagem.setTitle("Comanda Fácil");
                mensagem.setMessage("Confirma o recebimento ?");
                mensagem.setPositiveButton("Confimar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        comanda.setStatus("FECHADO");
                        comanda.setData_recebimento(new Date());
                        Thread thread= new Thread(){
                            @Override public void run() {
                                try {
                                    dao_comanda.createOrUpdate(comanda);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        long delayMillis = 5000;
                        try {
                            thread.join(delayMillis);
                            if (thread.isAlive()) {}else{
                                finish();
                                Toast.makeText(activity, "Recebimento efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException e){}
                    }
                });
                mensagem.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                mensagem.show();
            }else{
                Toast.makeText(this, "Valores informados não batem com o total da comanda !", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "Preencha corretamente os campos", Toast.LENGTH_SHORT).show();
        }

    }

    public void cancelar(View view){
        finish();
    }

    private void preenche(){
        final Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey("idcomanda")) {
            Thread thread= new Thread(){
                @Override public void run() {
                    try {
                        comanda = (Comanda) dao_comanda.queryForId(bundle.getInt("idcomanda"));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            long delayMillis = 5000;
            try {
                thread.join(delayMillis);
                if (thread.isAlive()) {}else{
                    vltotal.setText(df.format(comanda.getValor_total()));
                }
            } catch (InterruptedException e){}

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        preenche();
    }
}