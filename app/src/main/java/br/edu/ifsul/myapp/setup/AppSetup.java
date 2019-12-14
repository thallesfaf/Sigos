package br.edu.ifsul.myapp.setup;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.edu.ifsul.myapp.model.Cliente;
import br.edu.ifsul.myapp.model.Equipamento;
import br.edu.ifsul.myapp.model.User;

public class AppSetup {

    public static List<Cliente> clientes = new ArrayList<>();
    public static Cliente cliente = null;

    public static List<Equipamento> equipamentos = new ArrayList<>();
    public static Equipamento equipamento = null;

    public static User user = null;
    public static Map<String, Bitmap> cacheProdutos = new HashMap<>();
}