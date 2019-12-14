package br.edu.ifsul.myapp.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import br.edu.ifsul.myapp.R;
import br.edu.ifsul.myapp.model.Os;
import br.edu.ifsul.myapp.setup.AppSetup;

public class OsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "osactivity";
    EditText edtEquipamento, edtDescricao, edtDefeito, edtLaudotecnico, edtPrevisao, edtValor, edtStatus;
    ListView listV_dados;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private List<Os> listOs = new ArrayList<Os>();
    private ArrayAdapter<Os> arrayAdapterOs;

    Os osSelecionada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (AppSetup.user.getFuncao().equals("admin")) {
            navigationView.getMenu().findItem(R.id.admin_option).setVisible(true);
        }

        AppSetup.equipamento = null;

        edtEquipamento = (EditText)findViewById(R.id.editEquipamento);
        edtDescricao = (EditText)findViewById(R.id.editDescricao);
        edtLaudotecnico = (EditText)findViewById(R.id.editLaudotecnico);
        edtDefeito = (EditText)findViewById(R.id.editDefeito);
        edtPrevisao = (EditText)findViewById(R.id.editPrevisao);
        edtValor = (EditText)findViewById(R.id.editValor);
        edtStatus = (EditText)findViewById(R.id.editStatus);
        listV_dados = (ListView)findViewById(R.id.listV_dados);
        inicializarFirebase();
        //firebaseDatabase.setPersistenceEnabled(true);
        eventoDatabase();

        listV_dados.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                osSelecionada = listOs.get(i);
                Log.d(TAG, "objeto em onclick" + osSelecionada);
                edtEquipamento.setText(osSelecionada.getEquipamento().getDescricao());
                edtDescricao.setText(osSelecionada.getDescricao());
                edtLaudotecnico.setText(osSelecionada.getLaudotecnico());
                edtDefeito.setText(osSelecionada.getDefeito());
                edtPrevisao.setText(osSelecionada.getPrevisao());
                edtValor.setText(osSelecionada.getValor());
                edtStatus.setText(osSelecionada.getStatus());
            }
        });

        edtEquipamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(OsActivity.this, EquipamentoActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if((AppSetup.equipamento != null)){
            edtEquipamento.setText(AppSetup.equipamento.getDescricao());
        }
    }

    private void eventoDatabase() {
        databaseReference.child("Os").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listOs.clear();

                for (DataSnapshot objSnapshot:dataSnapshot.getChildren()){
                    Os o = objSnapshot.getValue(Os.class);
                    listOs.add(o);
                    //limparVariavel();
                }

                Log.d(TAG, "listOS = " + listOs);

                arrayAdapterOs = new ArrayAdapter<Os>(OsActivity.this,android.R.layout.simple_list_item_1,listOs);
                listV_dados.setAdapter(arrayAdapterOs);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void inicializarFirebase() {
        FirebaseApp.initializeApp(OsActivity.this);
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
            Os o = new Os();
            o.setUid(UUID.randomUUID().toString());
            o.setEquipamento(AppSetup.equipamento);
            o.setDescricao(edtDescricao.getText().toString());
            o.setDefeito(edtDefeito.getText().toString());
            o.setLaudotecnico(edtLaudotecnico.getText().toString());
            o.setPrevisao(edtPrevisao.getText().toString());
            o.setValor(edtValor.getText().toString());
            o.setStatus(edtStatus.getText().toString());
            databaseReference.child("Os").child(o.getUid()).setValue(o);
            limparCampos();
        }else if(id == R.id.menu_atualiza){
            Os o = new Os();
            o.setUid(osSelecionada.getUid());
            o.setEquipamento(AppSetup.equipamento);
            o.setDescricao(edtDescricao.getText().toString().trim());
            o.setDefeito(edtDefeito.getText().toString().trim());
            o.setLaudotecnico(edtLaudotecnico.getText().toString().trim());
            o.setPrevisao(edtPrevisao.getText().toString().trim());
            o.setValor(edtValor.getText().toString().trim());
            o.setStatus(edtStatus.getText().toString().trim());
            databaseReference.child("Os").child(o.getUid()).setValue(o);
            limparCampos();
        }else if(id == R.id.menu_deleta){
            Os o = new Os();
            o.setUid(osSelecionada.getUid());
            databaseReference.child("Os").child(o.getUid()).removeValue();
            limparCampos();
        }

        return true;
    }
    private void limparCampos(){
        edtEquipamento.setText("");
        edtDescricao.setText("");
        edtDefeito.setText("");
        edtPrevisao.setText("");
        edtValor.setText("");
        edtLaudotecnico.setText("");
        edtStatus.setText("");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {

            case R.id.nav_clientes: {
                startActivity(new Intent(OsActivity.this, ClientesActivity.class));
                break;
            }
            case R.id.nav_cliente_administracao: {
                startActivity(new Intent(OsActivity.this, ClienteAdminActivity.class));
                break;
            }
            case R.id.equipamentos: {
                startActivity(new Intent(OsActivity.this, EquipamentoActivity.class));
                break;
            }
            case R.id.os: {
                startActivity(new Intent(OsActivity.this, OsActivity.class));
                break;
            }
            case R.id.nav_sobre: {
                startActivity(new Intent(OsActivity.this, SobreActivity.class));
                break;
            }
            case R.id.nav_sair: {
                    finish();
                }
                break;
            }
        return true;
    }

    //private void limparVariavel() {
    //    if ((AppSetup.equipamento != null)) {
    //        AppSetup.equipamento = null;
    //    }
    //}
}
