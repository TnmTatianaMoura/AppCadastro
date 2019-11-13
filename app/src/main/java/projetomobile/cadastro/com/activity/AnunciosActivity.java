package projetomobile.cadastro.com.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import projetomobile.cadastro.com.R;
import projetomobile.cadastro.com.TelaLogin.LoginActivity;
import projetomobile.cadastro.com.activity.MeusAnunciosActivity;
import projetomobile.cadastro.com.adapter.AdapterAnuncios;
import projetomobile.cadastro.com.helper.ConfiguracaoFirebase;
import projetomobile.cadastro.com.model.Anuncio;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnunciosActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerAnunciosPublicos;
    private Button buttonRegiao, buttonCategoria;
    private AdapterAnuncios adapterAnuncios;
    private List<Anuncio> listaAnuncios =new ArrayList<>();
    private DatabaseReference anunciosPublicosRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();

        //Configurações Iniciais
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        anunciosPublicosRef = ConfiguracaoFirebase.getFirebase()
                .child("anuncios");


        //Configurar a recyclerView
        recyclerAnunciosPublicos.setLayoutManager(new LinearLayoutManager(this));
        recyclerAnunciosPublicos.setHasFixedSize(true);
        adapterAnuncios = new AdapterAnuncios(listaAnuncios,this);

        recyclerAnunciosPublicos.setAdapter(adapterAnuncios);

        recuperarAnunciosPublicos();

        //autenticacao.signOut();
    }

      //Metodo para recuperar dados dos anuncios

        public void recuperarAnunciosPublicos(){
        listaAnuncios.clear();
        anunciosPublicosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot estados: dataSnapshot.getChildren()){
                    for(DataSnapshot categorias: estados.getChildren()){
                        for(DataSnapshot anuncios: categorias.getChildren()){

                            Anuncio anuncio = anuncios.getValue(Anuncio.class);
                            listaAnuncios.add(anuncio);

                            Collections.reverse(listaAnuncios);
                            adapterAnuncios.notifyDataSetChanged();

                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(autenticacao.getCurrentUser() == null){//usuario deslogado

            menu.setGroupVisible(R.id.group_deslogado,true);
        }else {//usuario logado

            menu.setGroupVisible(R.id.group_logado,true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId() ){
            case R.id.menu_cadastrar :
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
            case R.id.menu_sair :
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;
            case R.id.menu_anuncios :
                startActivity(new Intent(getApplicationContext(), MeusAnunciosActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void inicializarComponentes(){

        recyclerAnunciosPublicos = findViewById(R.id.recyclerAnunciosPublicos);
    }
}

