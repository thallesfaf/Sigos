package br.edu.ifsul.myapp.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import br.edu.ifsul.myapp.R;
import br.edu.ifsul.myapp.model.Cliente;
import br.edu.ifsul.myapp.setup.AppSetup;

public class ClienteAdminActivity extends AppCompatActivity {

    private static final String TAG = "produtoAdminActivity";
    private static final int RC_BARCODE_CAPTURE = 1, RC_GALERIA_IMAGE_PICK = 2;
    private EditText etCodigoDeBarras, etNome, etSobrenome, etCpf;
    private Button btSalvar;
    //private Button btVerPedidos;
    private ImageView imvFoto;
    private Cliente cliente;
    private byte[] fotoCliente = null;
    private Uri arquivoUri;
    private FirebaseDatabase database;
    private boolean flagInsertOrUpdate = true;
    private ProgressDialog mProgressDialog;
    private ImageButton imbPesquisar;
    private ProgressBar pbFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_admin);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        database = FirebaseDatabase.getInstance();

        etCodigoDeBarras = findViewById(R.id.etCodigoDeBarras_clienteAdmin);
        etNome = findViewById(R.id.etNomeClienteTelaAdmin);
        etSobrenome = findViewById(R.id.etSobrenomeClienteTelaAdmin);
        etCpf = findViewById(R.id.etCPFClienteTelaAdmin);
        btSalvar = findViewById(R.id.btSalvarClienteTelaAdmin);
        //btVerPedidos = findViewById(R.id.bt_verPedidos);
        imvFoto = findViewById(R.id.imvFotoClienteTelaAdmin);
        imbPesquisar = findViewById(R.id.imbPesquisar_clienteAdmin);
        pbFoto = findViewById(R.id.pb_foto_cliente_admin);

        imvFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent,getString(R.string.titulo_janela_galeria_imagens)), RC_GALERIA_IMAGE_PICK);
            }
        });

        imbPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etCodigoDeBarras.getText().toString().isEmpty()){
                    buscarNoBanco(Long.valueOf(etCodigoDeBarras.getText().toString()));
                }
            }
        });

        cliente = new Cliente();

        btSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etCodigoDeBarras.getText().toString().isEmpty() &&
                        !etNome.getText().toString().isEmpty() &&
                        !etSobrenome.getText().toString().isEmpty() &&
                        !etCpf.getText().toString().isEmpty() ){
                    if(fotoCliente != null){
                        uploadFotoDoCliente();
                    }else{
                        salvarCliente();
                    }
                }else{
                    Snackbar.make(findViewById(R.id.container_activity_produtoadmin), R.string.snack_preencher_todos_campos, Snackbar.LENGTH_LONG).show();
                }

            }
        });

    }

    private void uploadFotoDoCliente(){

        Long codigoDeBarras = Long.valueOf(etCodigoDeBarras.getText().toString());
        cliente.setCodigoDeBarras(codigoDeBarras);
        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("images/clientes/" + cliente.getCodigoDeBarras() + ".jpeg");
        UploadTask uploadTask = mStorageRef.putBytes(fotoCliente);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(ClienteAdminActivity.this, getString(R.string.toast_foto_cliente_upload_fail), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG, "URL da foto no storage: " + taskSnapshot.getMetadata().getPath());
                cliente.setUrl_foto(taskSnapshot.getMetadata().getPath());
                salvarCliente();
            }
        });
    }

    private void buscarNoBanco(Long codigoDeBarras) {

        DatabaseReference myRef = database.getReference("clientes");
        Log.d(TAG, "Barcode = " + codigoDeBarras);
        Query query = myRef.orderByChild("codigoDeBarras").equalTo(codigoDeBarras).limitToFirst(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "dataSnapshot = " + dataSnapshot.getValue());
                if(dataSnapshot.getValue() != null){
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        cliente = ds.getValue(Cliente.class);
                        cliente.setKey(ds.getKey());
                    }
                    flagInsertOrUpdate = false;
                    carregarView();
                }else{
                    Toast.makeText(ClienteAdminActivity.this, getString(R.string.toast_produto_nao_cadastrado), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void carregarView() {
        etCodigoDeBarras.setText(cliente.getCodigoDeBarras().toString());
        etCodigoDeBarras.setEnabled(false);
        etNome.setText(cliente.getNome());
        etSobrenome.setText(cliente.getSobrenome());
        etCpf.setText(cliente.getCpf());
        if(cliente.getUrl_foto() != ""){
            pbFoto.setVisibility(ProgressBar.VISIBLE);
            if(AppSetup.cacheProdutos.get(cliente.getKey()) == null){
                StorageReference mStorageRef = FirebaseStorage.getInstance().getReference("images/clientes/" + cliente.getCodigoDeBarras() + ".jpeg");
                final long ONE_MEGABYTE = 1024 * 1024;
                mStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap fotoEmBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        imvFoto.setImageBitmap(fotoEmBitmap);
                        pbFoto.setVisibility(ProgressBar.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        pbFoto.setVisibility(ProgressBar.INVISIBLE);
                        Log.d(TAG, "Download da foto do produto falhou: " + "images/clientes" + cliente.getCodigoDeBarras() + ".jpeg");
                    }
                });
            }else{
                imvFoto.setImageBitmap(AppSetup.cacheProdutos.get(cliente.getKey()));
                pbFoto.setVisibility(ProgressBar.INVISIBLE);
            }

        }
    }


    private void salvarCliente() {

        Long codigoDeBarras = Long.valueOf(etCodigoDeBarras.getText().toString());
        cliente.setCodigoDeBarras(codigoDeBarras);
        cliente.setNome(etNome.getText().toString());
        cliente.setSobrenome(etSobrenome.getText().toString());
        cliente.setCpf(etCpf.getText().toString());
        cliente.setSituacao(true);
        Log.d(TAG, "Cliente a ser salvo: " + cliente);
        if(flagInsertOrUpdate){
 
            DatabaseReference myRef = database.getReference("clientes");
            Log.d(TAG, "Barcode isNoBanco= " + codigoDeBarras);
            Query query = myRef.orderByChild("codigoDeBarras").equalTo(codigoDeBarras).limitToFirst(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "dataSnapshot isNoBanco = " + dataSnapshot.getValue());
                    if(dataSnapshot.getValue() != null){
                        Toast.makeText(ClienteAdminActivity.this, R.string.toast_codigo_barras_ja_cadastrado, Toast.LENGTH_SHORT).show();
                    }else{
                        showWait();
                        DatabaseReference myRef = database.getReference("clientes");
                        cliente.setKey(myRef.push().getKey());
                        myRef.child(cliente.getKey()).setValue(cliente)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ClienteAdminActivity.this, getString(R.string.toast_cliente_salvo), Toast.LENGTH_SHORT).show();
                                        limparForm();
                                        dismissWait();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Snackbar.make(findViewById(R.id.container_activity_produtoadmin), R.string.snack_operacao_falhou, Snackbar.LENGTH_LONG).show();
                                        dismissWait();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            flagInsertOrUpdate = true;
            showWait();
            DatabaseReference myRef = database.getReference("clientes/" + cliente.getKey());
            myRef.setValue(cliente)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(ClienteAdminActivity.this, getString(R.string.toast_cliente_salvo), Toast.LENGTH_SHORT).show();
                            limparForm();
                            dismissWait();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(findViewById(R.id.container_activity_produtoadmin), R.string.snack_operacao_falhou, Snackbar.LENGTH_LONG).show();
                            dismissWait();
                        }
                    });
        }

    }

    private void limparForm() {
        cliente = new Cliente();
        etCodigoDeBarras.setEnabled(true);
        etCodigoDeBarras.setText(null);
        etNome.setText(null);
        etSobrenome.setText(null);
        etCpf.setText(null);
        imvFoto.setImageResource(R.drawable.img_cliente_icon_524x524);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_produto_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.menuitem_limparform_admin:
                limparForm();
                break;

            case android.R.id.home:
                finish();
                break;

        }
        return true;
    }
   
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        return outputStream.toByteArray();
    }

    public void  showWait(){

        mProgressDialog = new ProgressDialog(ClienteAdminActivity.this);
        mProgressDialog.setMessage(getString(R.string.message_processando));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    public void dismissWait(){
        mProgressDialog.dismiss();
    }
}
