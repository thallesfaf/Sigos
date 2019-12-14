package br.edu.ifsul.myapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.edu.ifsul.myapp.R;
import br.edu.ifsul.myapp.model.Cliente;

public class ClientesAdapter extends ArrayAdapter<Cliente> {

    private Context context;
    private List<Cliente> clientes;

    public ClientesAdapter(@NonNull Context context, @NonNull List<Cliente> clientes) {
        super(context, 0, clientes);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Cliente cliente = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cliente_adapter, parent, false);
        }


        TextView tvNome = convertView.findViewById(R.id.tvNomeClienteAdapter);
        TextView tvDetalhes = convertView.findViewById(R.id.tvDetalhesDoClienteAdapater);
        ImageView imvFoto = convertView.findViewById(R.id.imvFotoDoClienteAdapter);

        tvNome.setText(cliente.getNome() + " " + cliente.getSobrenome());
        tvDetalhes.setText("Código: " + cliente.getCodigoDeBarras()
                           + "\nCPF.: " + cliente.getCpf());
        if(cliente.getUrl_foto().equals("")){
            imvFoto.setImageResource(R.drawable.img_cliente_icon_524x524);
        }else{

        }


        return convertView;
    }
}
