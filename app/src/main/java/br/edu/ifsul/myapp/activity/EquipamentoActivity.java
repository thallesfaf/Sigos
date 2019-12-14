package br.edu.ifsul.myapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

import br.edu.ifsul.myapp.R;
import br.edu.ifsul.myapp.model.Equipamento;
import br.edu.ifsul.myapp.setup.AppSetup;

public class EquipamentoActivity extends AppCompatActivity {
    private static final String TAG = "equipamentoactivity";
    EditText edtCliente, edtTipo, edtMarca, edtModelo, edtNserie, edtDescricao;
    ListView listV_equipamentos;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    private ArrayAdapter<Equipamento> arrayAdapterEquipamento;

    Equipamento equipamentoSelecionado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipamento);
        edtCliente = (EditText)findViewById(R.id.editCliente);
        edtTipo = (EditText)findViewById(R.id.editTipo);
        edtMarca = (EditText)findViewById(R.id.editMarca);
        edtModelo = (EditText)findViewById(R.id.editModelo);
        edtNserie = (EditText)findViewById(R.id.editNserie);
        edtDescricao = (EditText)findViewById(R.id.editDesc);
        listV_equipamentos = (ListView)findViewById(R.id.listV_equipamentos);
        inicializarFirebase();
        eventoDatabase();

        listV_equipamentos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                equipamentoSelecionado =  AppSetup.equipamentos.get(i);
                Log.d(TAG, "objeto em onclick" + equipamentoSelecionado);
                edtCliente.setText(equipamentoSelecionado.getCliente().getNome());
                edtTipo.setText(equipamentoSelecionado.getTipo());
                edtMarca.setText(equipamentoSelecionado.getMarca());
                edtModelo.setText(equipamentoSelecionado.getModelo());
                edtNserie.setText(equipamentoSelecionado.getNserie());
                edtDescricao.setText(equipamentoSelecionado.getDescricao());
                confirmarSelecao(i);
            }
        });

        edtCliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EquipamentoActivity.this, ClientesActivity.class));
            }
        });
    }

    private void confirmarSelecao(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleção de equipamento");
        builder.setMessage("Deseja selecionar este equipamento?");
        builder.setPositiveButton(R.string.alertdialog_sim, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               AppSetup.equipamento = AppSetup.equipamentos.get(position);
                finish();
            }
        });
        builder.setNegativeButton(R.string.alertdialog_nao, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if((AppSetup.cliente != null)){
            edtCliente.setText(AppSetup.cliente.getNome());
        }
    }

    private void eventoDatabase() {
        databaseReference.child("Equipamento").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                AppSetup.equipamentos.clear();

                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Equipamento e = objSnapshot.getValue(Equipamento.class);
                    AppSetup.equipamentos.add(e);
                }

                Log.d(TAG, "listEquipamento = " +  AppSetup.equipamentos);

                arrayAdapterEquipamento = new ArrayAdapter<Equipamento>(EquipamentoActivity.this,android.R.layout.simple_list_item_1, AppSetup.equipamentos);
                listV_equipamentos.setAdapter(arrayAdapterEquipamento);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(EquipamentoActivity.this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menu_novo){
            Equipamento e = new Equipamento();
            e.setUid(UUID.randomUUID().toString());
            e.setCliente(AppSetup.cliente);
            e.setTipo(edtTipo.getText().toString());
            e.setMarca(edtMarca.getText().toString());
            e.setModelo(edtModelo.getText().toString());
            e.setNserie(edtNserie.getText().toString());
            e.setDescricao(edtDescricao.getText().toString());
            databaseReference.child("Equipamento").child(e.getUid()).setValue(e);
            limparCampos();
        }else if(id == R.id.menu_atualiza){
            Equipamento e = new Equipamento();
            e.setUid(equipamentoSelecionado.getUid());
            e.setCliente(AppSetup.cliente);
            e.setTipo(edtTipo.getText().toString().trim());
            e.setMarca(edtMarca.getText().toString().trim());
            e.setModelo(edtModelo.getText().toString().trim());
            e.setNserie(edtNserie.getText().toString().trim());
            e.setDescricao(edtDescricao.getText().toString().trim());
            databaseReference.child("Equipamento").child(e.getUid()).setValue(e);
            limparCampos();
        }else if(id == R.id.menu_deleta){
            Equipamento e = new Equipamento();
            e.setUid(equipamentoSelecionado.getUid());
            databaseReference.child("Equipamento").child(e.getUid()).removeValue();
            limparCampos();
        }

        return true;
    }
    private void limparCampos(){
        edtCliente.setText("");
        edtTipo.setText("");
        edtMarca.setText("");
        edtModelo.setText("");
        edtNserie.setText("");
        edtDescricao.setText("");
    }

}


