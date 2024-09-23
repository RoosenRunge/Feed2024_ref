package com.example.feed2023;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.feed2023.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

//    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    // #1.0 - Primeiramente vamos utilizar o próprio TextView criado pelo android para exibir o conteúdo baixado.
    // #Vamos criar uma variável TextView para receber o conteúdo baixado
    private TextView xmlTexView;

    //1.1 - Vamos criar uma variável String que receberá o conteúdo do site.
    private String mFileContents;

    // Parte 3# Parte 3# Declarando a variável RecyclerView
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

/*        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });*/

        //1.2 - associando a variável à view no XML.
        xmlTexView = (TextView) findViewById(R.id.textViewInicial);

        //1.5 - Vamos criar agora uma instância da nossa AsyncTask e executá-la
        ParallelProcessing parallelProcessing = new ParallelProcessing();
        parallelProcessing.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml");

        //    Recycler block
        //3.1 Associando a a variável à RecyclerView no XML
        mRecyclerView = findViewById(R.id.my_recycler);
        //3.2 é interessante caso os itens de layout tenham tamanho fixo. Otimizar a implementação
        mRecyclerView.setHasFixedSize(true);
        //3.3 Associação de um gerenciador de Layout para a RecyclerView.
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    // 1.3 - Vamos criar o método para realizar o download do feed.
    private String downloadXMLFile(String theUrl){
        // precisamos usar um try pois as operações de abertura de conexão e leitura do stream de dados podem lançar exeções de IO.
        try {
           //cria um instância da classe URL com a url que será utilizada na conexão.
            URL myUrl = new URL(theUrl);

            //abre a conexão
            HttpURLConnection myconnection = (HttpURLConnection) myUrl.openConnection();

            //verifica se foi bem sucedida a conexão; se sim código 200
            int response = myconnection.getResponseCode();
            Log.d("DownloadData", "The response code is " + response);

            // cria a variável "data" que irá receber o stream de bytes
            InputStream data = myconnection.getInputStream();

            //usa a classe InputStreamReader para converter bytes em chars
            InputStreamReader caracteres = new InputStreamReader(data);

            //variável para contagem de número de caracteres lidos
            int charRead;

            //criação de um array de char para leitura de 500 em 500 caracteres
            char[] inputBuffer = new char[500];

            //Criação de uma instância StringBuilder para formar a String final de interesse
            StringBuilder tempBuffer = new StringBuilder();

            //laço de leitura e formação da string
            while (true) {
                //lê os caracteres com tamanho máximo de inputBuffer(500) e informa o número lido
                charRead = caracteres.read(inputBuffer);

                //se -1 não há caracteres lidos e sai do laço.
                if (charRead <= 0) {
                    break;
                }

                //Enquanto charRead>0, vai "appendando" as leituras de 500 em 500 caracteres do inputBuffer

                tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
            }
            //Retorna o conteúdo final do tempBuffer, convertido para String
            return tempBuffer.toString();
        }catch(IOException e)
            {
                Log.d("DownloadData", "IO Expection reading data: " + e.getMessage());
            }catch (SecurityException e){
            Log.e("DownloadData","Security exception. Needs permission?"+e.getMessage());
        }

        return null;
    }

    //1.4 - Vamos criar a classe DownloadData que é uma AsynkTask,
// que através de uma thread paralela fará a  chamada do método DownloadXMLFile para baixarmos o conteúdo do Feed

    private class ParallelProcessing extends AsyncTask<String, Void, String> {
               @Override
        protected String doInBackground(String... params) {
            //vamos efocar o método downloadXMLFile passando a url como parâmetro
            mFileContents = downloadXMLFile(params[0]);
            if (mFileContents == null) {
                //O logcat nos permite gerar logs de verificação durante o processamento do app, funciona como um monitor
                Log.d("DownloadData", "Erro downloading");
            }
            Log.d("DownloadData", "Baixou =>"+ mFileContents);
            return mFileContents;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //1.6 - Inserindo o conteúdo baixado na caixa de texto.
            // Obs.Deve ser comentado na parte 2, quando é ativada a RecyclerView

             //xmlTexView.setText(mFileContents);

            //Parte 2 parse application
            //criando uma instância da classe parseApplication e disparando a mineração e vamos conferir no logcat

            ParseApplications parseApplications = new ParseApplications(mFileContents);
            parseApplications.process();

            // Recycler block
            //3.4 criação de uma instância do nosso adapter e passando o resultado da mineração do parse Application
            //através do método getApplication que já retorna um array de application1
            MyFirstAdapter mAdapter = new MyFirstAdapter(parseApplications.getApplications());
            //3.5 linkando nossa RecyclerView com o adapter
            mRecyclerView.setAdapter(mAdapter);

        }

        return super.onOptionsItemSelected(item);
    }

/*    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/



}