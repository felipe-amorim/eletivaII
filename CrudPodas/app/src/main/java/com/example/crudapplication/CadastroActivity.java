package com.example.crudapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

public class CadastroActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText editTextNome, editTextObs;
    Button botao;
    SQLiteDatabase bancoDados;
    String[] riscos = { "Queda", "Rede elétrica", "Danificação de edificações", "Poda agendada", "Outros"};
    RadioButton CB, CM, CA;
    //CheckBox Queda, RedeEletrica, Edificacao, Agendada, Outros;
    CheckBox CBCaminhaoGuindaste, CBTrator, CBBritadeira, CBOutro;
    String risco = "";
    Switch urgente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editTextNome = (EditText) findViewById(R.id.editTextNome);
        editTextObs = (EditText) findViewById(R.id.editTextObs);
        botao = (Button) findViewById(R.id.buttonAlterar);
        CB = (RadioButton) findViewById(R.id.radioCB);
        CM = (RadioButton) findViewById(R.id.radioCM);
        CA = (RadioButton) findViewById(R.id.radioCA);
        CBCaminhaoGuindaste = (CheckBox) findViewById(R.id.CBCaminhaoGuindaste);
        CBTrator = (CheckBox) findViewById(R.id.CBTrator);
        CBBritadeira = (CheckBox) findViewById(R.id.CBBritadeira);
        CBOutro = (CheckBox) findViewById(R.id.CBOutro);
        CBOutro.setOnClickListener((view) -> {setCBOutro();});
        urgente = (Switch) findViewById(R.id.urgente);

        createSpinner();

        botao.setOnClickListener((view -> {cadastrar();}));
    }

    public void setCBOutro(){
        Toast.makeText(getApplicationContext(), "Descreva o equipamento no campo observações", Toast.LENGTH_LONG).show();
    }

    private void createSpinner(){

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, riscos);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
    }

    public void cadastrar(){
        if(!TextUtils.isEmpty(editTextNome.getText().toString())){
            try{
                String complexidade = "";
                if(CB.isChecked()){
                    complexidade = "Baixa";
                }
                if(CM.isChecked()){
                    complexidade = "Media";
                }
                if(CA.isChecked()){
                    complexidade = "Alta";
                }
                String equipamentos = "";
                if(CBCaminhaoGuindaste.isChecked()){
                    equipamentos = equipamentos + "CaminhaoGuindaste;";
                }
                if(CBTrator.isChecked()){
                    equipamentos = equipamentos + "Trator;";
                }
                if(CBBritadeira.isChecked()){
                    equipamentos = equipamentos + "Britadeira;";
                }
                if(CBOutro.isChecked()){
                    equipamentos = equipamentos + "Outro;";
                }

                String urg = "false";
                if(urgente.isChecked()){
                    urg = "true";
                }
                bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
                String sql = "INSERT INTO coisa (nome, obs, complexidade, equipamentosEspeciais, risco, urgencia) VALUES (?, ?, ?, ?, ?, ?)";
                //String sql = "INSERT INTO coisa (nome, obs) VALUES (?, ?)";
                SQLiteStatement stmt = bancoDados.compileStatement(sql);
                stmt.bindString(1,editTextNome.getText().toString());
                stmt.bindString(2,editTextObs.getText().toString());
                stmt.bindString(3,complexidade);
                stmt.bindString(4,equipamentos);
                stmt.bindString(5,risco);
                stmt.bindString(6,urg);
                stmt.executeInsert();
                bancoDados.close();
                finish();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        //Toast.makeText(getApplicationContext(),riscos[position] , Toast.LENGTH_LONG).show();
        risco = riscos[position];
        if(riscos[position].equals("Outros")){
            Toast.makeText(getApplicationContext(), "Descreva o risco no campo observações", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}