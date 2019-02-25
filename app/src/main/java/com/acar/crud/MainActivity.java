package com.acar.crud;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import model.Pessoa;

public class MainActivity extends AppCompatActivity {

    EditText editNome, editEmail, editTelefone, editEndereco;
    ListView lista, listaV_dados;
    Pessoa pessoaSelecionada;
    // Write a message to the database
   FirebaseDatabase database = FirebaseDatabase.getInstance();
   DatabaseReference myRef = database.getReference();

    private List<Pessoa> listPessoa = new ArrayList<>();
    private ArrayAdapter<Pessoa> arrayAdapterPessoa;

//Evento de pesquisa no banco de dados
    private void eventoDatabase() {
        myRef.child("Pessoa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listPessoa.clear();
                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Pessoa p = objSnapshot.getValue(Pessoa.class);
                    listPessoa.add(p);
                }
                arrayAdapterPessoa = new ArrayAdapter<Pessoa>(MainActivity.this,android.R.layout.simple_list_item_1,listPessoa);
                listaV_dados.setAdapter(arrayAdapterPessoa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*Inicializa o banco de dados*/
    private void inicializarFirebase() {
        FirebaseApp.initializeApp(MainActivity.this);
        myRef = FirebaseDatabase.getInstance().getReference();
        database.setPersistenceEnabled(true);
        database.getReference();
    }

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //validando os botoes
        setContentView(R.layout.activity_main);
        editNome = findViewById(R.id.editNome);
        editTelefone = findViewById(R.id.editTelefone);
        editEndereco = findViewById(R.id.editEndereco);
        editEmail = findViewById(R.id.editEmail);
        lista = findViewById(R.layout.fragment_item_list);
        listaV_dados = findViewById(R.id.listaV_dados);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

// modelo para consultar lista de pessoas
        listaV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Pessoa pessoaSelecionada = (Pessoa) parent.getItemAtPosition(position);
                editNome.setText(pessoaSelecionada.getNome());
                editTelefone.setText(pessoaSelecionada.getTelefone());
                editEndereco.setText(pessoaSelecionada.getEndereco());
                editEmail.setText(pessoaSelecionada.getEmail());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
   /* Menu interativo que salva e consulta dados!! */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btSalvar) {
            Pessoa p = new Pessoa();
            p.setId(UUID.randomUUID().toString());  //UUID é especifico para randomizar o id
            p.setNome(editNome.getText().toString());
            p.setTelefone(editEndereco.getText().toString());
            p.setEndereco(editEndereco.getText().toString());
            p.setEmail(editEmail.getText().toString());
            myRef.child("Pessoa").child(p.getId()).setValue(p);

            Toast.makeText(getApplicationContext(), "Salvo!", Toast.LENGTH_SHORT).show();
            limparCampos();
        }
        if (id == R.id.btExcluir) {
            myRef.child("Pessoa").removeEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    listPessoa.clear();
                    for (DataSnapshot objSnapshot : dataSnapshot.getChildren()) {
                        Pessoa p = objSnapshot.getValue(Pessoa.class);
                        listPessoa.remove(p);
                        Toast.makeText(getApplicationContext(), "Excluido com sucesso!!", Toast.LENGTH_SHORT).show();
                        limparCampos();
                    }
            }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else if (id == R.id.btEditar){
            Pessoa p = new Pessoa();
            p.setId(pessoaSelecionada.getId());
            p.setNome(editNome.getText().toString().trim());
            p.setTelefone(editTelefone.getText().toString().trim());
            p.setEndereco(editEndereco.getText().toString().trim());
            p.setEmail(editEmail.getText().toString().trim());
            myRef.child("Pessoa").child(p.getId()).setValue(p);
            limparCampos();

        }else if (id == R.id.btConsulta) {
            setContentView(R.layout.activity_consulta);
            Intent i = new Intent(MainActivity.this,Consulta.class);
            startActivity(i);
        }return true;
    }

    private void limparCampos() {
        editNome.setText("");
        editTelefone.setText("");
        editEndereco.setText("");
        editEmail.setText("");
    }
}
// if (id == R.id.btTelaConsulta) {

//setContentView(R.layout.fragment_item_list);

//}return true;

