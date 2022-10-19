package com.example.crudapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private SQLiteDatabase bancoDados;
    public ListView listViewDados;
    public Button botao;
    public ArrayList<Integer> arrayIds;
    public Integer idSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //System.out.println("passando por aqui");
        //deleteDatabase("crudapp");

        listViewDados = (ListView) findViewById(R.id.listViewDados);
        botao = (Button) findViewById(R.id.buttonAlterar);

        botao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirTelaCadastro();
            }
        });

        listViewDados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                idSelecionado = arrayIds.get(i);
                abrirTelaAlterar();
            }
        });

        criarBancoDados();
        listarDados();
    }

    @Override
    protected void onResume(){
        super.onResume();
        listarDados();
    }

    public void criarBancoDados(){
        try {
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            bancoDados.execSQL("CREATE TABLE IF NOT EXISTS coisa(" +
                    " id INTEGER PRIMARY KEY AUTOINCREMENT" +
                    " , nome VARCHAR"+
                    " , obs VARCHAR"+
                    " , complexidade VARCHAR"+
                    " , equipamentosEspeciais VARCHAR"+
                    " , risco VARCHAR"+
                    " , urgencia VARCHAR)");
            bancoDados.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void listarDados(){
        try {
            arrayIds = new ArrayList<>();
            bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
            Cursor meuCursor = bancoDados.rawQuery("SELECT id, nome, obs, complexidade, equipamentosEspeciais, risco, urgencia FROM coisa", null);
            ArrayList<String> linhas = new ArrayList<String>();
            ArrayAdapter meuAdapter = new ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    linhas
            );
            /*
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent){
                    View view = super.getView(position, convertView, parent);

                    String comp = meuCursor.getString(3);
                    int colr = 0;
                    if(comp.equals("Baixa")){
                        //CB.toggle();
                        colr = Color.GREEN;
                    }
                    if(comp.equals("Media")){
                        //CM.toggle();
                        colr = Color.YELLOW;
                    }
                    if(comp.equals("Alta")){
                        //CA.toggle();
                        colr = Color.RED;
                    }

                    //((TextView)view.findViewById(android.R.id.text1)).setTextColor(position % 2 == 0 ? Color.BLUE : Color.RED); // here can be your logic
                    //((TextView)view.findViewById(android.R.id.text1)).setTextColor(colr); // here can be your logic
                    return view;
                };

            };*/
            listViewDados.setAdapter(meuAdapter);
            meuCursor.moveToFirst();
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(1) + " - " + meuCursor.getString(2));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }
            /*
            while(meuCursor!=null){
                linhas.add(meuCursor.getString(1) + " - " + meuCursor.getString(2));
                arrayIds.add(meuCursor.getInt(0));
                meuCursor.moveToNext();
            }
             */

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void abrirTelaCadastro(){
        Intent intent = new Intent(this,CadastroActivity.class);
        startActivity(intent);
    }

    public void confirmaExcluir() {
        AlertDialog.Builder msgBox = new AlertDialog.Builder(MainActivity.this);
        msgBox.setTitle("Excluir");
        msgBox.setIcon(android.R.drawable.ic_menu_delete);
        msgBox.setMessage("Você realmente deseja excluir esse registro?");
        msgBox.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                excluir();
                listarDados();
            }
        });
        msgBox.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        msgBox.show();
    }

    public void excluir(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Por favor confirme a exclusão");

        alert.setPositiveButton("EXCLUIR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                try{
                    bancoDados = openOrCreateDatabase("crudapp", MODE_PRIVATE, null);
                    String sql = "DELETE FROM coisa WHERE id =?";
                    SQLiteStatement stmt = bancoDados.compileStatement(sql);
                    stmt.bindLong(1, idSelecionado);
                    stmt.executeUpdateDelete();
                    listarDados();
                    bancoDados.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        alert.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        alert.show();

    }

    public void abrirTelaAlterar(){
        //Intent intent = new Intent(this, AlterarActivity.class);
        //intent.putExtra("id",idSelecionado);
        Intent intent = new Intent(this, AlterarActivity.class);
        //startActivity(intent);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Selecione uma ação:");
        // alert.setMessage("Message");

        alert.setPositiveButton("Detalhes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Your action here
                intent.putExtra("id",idSelecionado);
                startActivity(intent);
            }
        });

        alert.setNegativeButton("Excluir",
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    excluir();
                }
            });

        alert.show();
    }
}