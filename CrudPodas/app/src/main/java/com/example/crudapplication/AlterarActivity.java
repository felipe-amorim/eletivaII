package com.example.crudapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
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

import java.util.Arrays;
import java.util.Collections;

public class AlterarActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SQLiteDatabase bancoDados;
    public Button buttonAlterar;
    public EditText editTextNome, editTextObs;
    Button botao;
    String[] riscos = { "Queda", "Rede elétrica", "Danificação de edificações", "Poda agendada", "Outros"};
    RadioButton CB, CM, CA;
    //CheckBox Queda, RedeEletrica, Edificacao, Agendada, Outros;
    CheckBox CBCaminhaoGuindaste, CBTrator, CBBritadeira, CBOutro;
    String risco = "";
    Spinner riscoSpinner;
    Switch urgente;;
    public Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar);

        buttonAlterar = (Button) findViewById(R.id.buttonAlterar);
        editTextNome = (EditText) findViewById(R.id.editTextNome);
        editTextObs = (EditText) findViewById(R.id.editTextObs);
        CB = (RadioButton) findViewById(R.id.radioCB);
        CM = (RadioButton) findViewById(R.id.radioCM);
        CA = (RadioButton) findViewById(R.id.radioCA);
        CBCaminhaoGuindaste = (CheckBox) findViewById(R.id.CBCaminhaoGuindaste);
        CBTrator = (CheckBox) findViewById(R.id.CBTrator);
        CBBritadeira = (CheckBox) findViewById(R.id.CBBritadeira);
        CBOutro = (CheckBox) findViewById(R.id.CBOutro);
        CBOutro.setOnClickListener((view) -> {setCBOutro();});
        urgente = (Switch) findViewById(R.id.urgente);
        riscoSpinner = (Spinner) findViewById(R.id.spinner);

        Intent intent = getIntent();
        id = intent.getIntExtra("id",0);

        createSpinner();
        carregarDados();

        buttonAlterar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alterar();
            }
        });

    }

    private void createSpinner(){

        Spinner spin = (Spinner) findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item, riscos);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
    }

    public void setCBOutro(){
        Toast.makeText(getApplicationContext(), "Descreva o equipamento no campo observações", Toast.LENGTH_LONG).show();
    }

    public void carregarDados(){
        try {
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            Cursor cursor = bancoDados.rawQuery("SELECT id, nome, obs, complexidade, equipamentosEspeciais, risco, urgencia FROM coisa WHERE id = " + id.toString(), null);
            cursor.moveToFirst();
            editTextNome.setText(cursor.getString(1));
            editTextObs.setText(cursor.getString(2));
            String comp = cursor.getString(3);
            if(comp.equals("Baixa")){
                CB.toggle();
            }
            if(comp.equals("Media")){
                CM.toggle();
            }
            if(comp.equals("Alta")){
                CA.toggle();
            }
            String[] equip = cursor.getString(4).split(";");
            for (String e: equip) {
                if(e.equals("CaminhaoGuindaste")){
                    CBCaminhaoGuindaste.toggle();
                }
                if(e.equals("Trator")){
                    CBTrator.toggle();
                }
                if(e.equals("Britadeira")){
                    CBBritadeira.toggle();
                }
                if(e.equals("Outro")){
                    CBOutro.toggle();
                }
            }
            riscoSpinner.setSelection(Arrays.asList(riscos).indexOf(cursor.getString(5)));
            if(cursor.getString(6).equals("true")){
                urgente.toggle();
            }


        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void alterar(){
        String valueNome = editTextNome.getText().toString();
        String valueObs = editTextObs.getText().toString();
        String valueComplexidade = "";
        if(CB.isChecked()){
            valueComplexidade = "Baixa";
        }
        if(CM.isChecked()){
            valueComplexidade = "Media";
        }
        if(CA.isChecked()){
            valueComplexidade = "Alta";
        }
        String valueEquipamentos = "";
        if(CBCaminhaoGuindaste.isChecked()){
            valueEquipamentos = valueEquipamentos + "CaminhaoGuindaste;";
        }
        if(CBTrator.isChecked()){
            valueEquipamentos = valueEquipamentos + "Trator;";
        }
        if(CBBritadeira.isChecked()){
            valueEquipamentos = valueEquipamentos + "Britadeira;";
        }
        if(CBOutro.isChecked()){
            valueEquipamentos = valueEquipamentos + "Outro;";
        }
        String valueUrgente = "false";
        if(urgente.isChecked()){
            valueUrgente = "true";
        }
        try{
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            //, obs, complexidade, equipamentosEspeciais, risco, urgencia
            String sql = "UPDATE coisa SET nome=?, obs=?, complexidade=?, equipamentosEspeciais=?, risco=?, urgencia=? WHERE id=?";
            SQLiteStatement stmt = bancoDados.compileStatement(sql);
            stmt.bindString(1,valueNome);
            stmt.bindString(2,valueObs);
            stmt.bindString(3,valueComplexidade);
            stmt.bindString(4,valueEquipamentos);
            stmt.bindString(5,risco);
            stmt.bindString(6,valueUrgente);
            stmt.bindLong(7,id);
            stmt.executeUpdateDelete();
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
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