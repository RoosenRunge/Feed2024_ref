package com.example.feed2023;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

class ParseApplications {

        //#####################################################
        //1# variável que irá receber o xmlFileContents "arquivo baixado".
        private String xmlData;

        //1.1#Vamos usar uma Arraylist de "Application1" cada entry estará associada a um application1.
        private ArrayList<Applications> applications = new ArrayList<Applications>() ;

        //1.2# Precisamos criar um construtor para inicializar o objeto dessa classe com o conteudo baixado e inicializar o Arraylist.
        //Vamos já criar a instância ArrayList de application
    public ParseApplications(String xmlData) {
        this.xmlData = xmlData;
    }

        //1.3# vamos criar um getter para acessar o ArrayList
        public ArrayList<Applications> getApplications() {
        return applications;
    }

        //2# criação do metodo process() para extração das informações do conteúdo XML e criação do
        //Array de applications contendo os conteúdos dos Top10 apps para a RecyclerView
        public boolean process(){

        //2.1 criação de várias locais para uso internamente durante a execução do método
        boolean status = true;       //sinaliza se a operação foi bem sucedida
        Applications currentRecord = null;    //objeto criado pra cada entry
        boolean inEntry = false;     //sinaliza se um campo a ser analisado é valido. "entry"
        String textValue = "";        //Onde o texto dos campos será armazenado.

        //3 Vamos criar uma estrutura try/catch pois as operações com a classe XmlPullParserFactory podem lançar exceções.
        try {
            // A classe XmlPullParserFactory  permite a manipulação de conteúdos XML (analisador)
            //#############################################################################################
            // 3.3 Vamos criar uma instancia do tipo XmlPullParserFactory para a criação de um objeto XmlPullParse capaz de tratar o arquivo XML.
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            //3.4 configura o suporte do factory para espaços reservados a nomes
            factory.setNamespaceAware(true);

            //3.5  este objeto possui metodos que nos permite extrair o conteudo do campo de dados do XML
            XmlPullParser xpp = factory.newPullParser();

            //3.6 passando o conteudo para o analisador XML.
            xpp.setInput(new StringReader(this.xmlData));
            //#############################################################################################

            //3.7  vamos criar um marcador que recebe uma sinalização sempre o  parser encontra um novo campo.
            // END_DOCUMENT, TEXT, START_TAG, END_TAG
            int evenType = xpp.getEventType();

            //3.8 Famos fazer o laço para ler o documento até o final.
            while (evenType != XmlPullParser.END_DOCUMENT) {
                // 3.9 guarda o nome do campo ( "entry", "name", "artist"...)
                String tagName = xpp.getName();

                //3.10 vamos verificar se é um dos campos de interesse e fazer o tratamento
                switch (evenType) {
                    // Se é uma tag de inicio de campo...
                    case XmlPullParser.START_TAG:
                        Log.d("ParseApplication", "Starting tag for-> " + tagName);//mostra a tag encontrada(e.g. name, entry, artist..)

                        //3.11 Se a tag for de um campo entry... será o início da criação de um objeto application1
                        if (tagName.equalsIgnoreCase("entry")) {
                            inEntry = true; // entrada valida!
                            currentRecord = new Applications();//cria um objeto Application para armazenar dados
                        }
                        break;


                    //3.12  no caso do envento ser um campo de texto capturamos o conteúdo e armazenamos na variável textValue.
                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        Log.d("ParseApplication", "text found");
                        break;

                    //3.13 # caso tenha encontrado o final da tag manda pro LogCat
                    case XmlPullParser.END_TAG:
                        Log.d("ParseApplication", "Ending tag for-> " + tagName);
                        //8.2 Ao final de cada TAG, se estiver dentro de um campo "entry" ->inEntry = true
                        //verfica se é um dos campos de interesse e atribui o texto do campo (textValue) no objeto.
                        if (inEntry) {

                            if (tagName.equalsIgnoreCase("entry")) {
                                applications.add(currentRecord);
                                inEntry = false;
                            } else if (tagName.equalsIgnoreCase("name")) {
                                currentRecord.setName(textValue);
                            } else if (tagName.equalsIgnoreCase("artist")) {
                                currentRecord.setArtist(textValue);
                            } else if (tagName.equalsIgnoreCase("releaseDate")) {
                                currentRecord.setReleaseDate(textValue);
                            }
                        }
                        break;
                    default:
                }
                //3.14 le o próximo evento através do parse xpp.
                evenType = xpp.next();
            }
        }
        //3.2 Se ocorrer qualquer tipo de exceção
        catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
            //3.15. Vamos jogar o resultado no LogCat
            for(Applications app: applications) {
                Log.d("ParseApplications", "*******************");
                Log.d("ParseApplications", "Name: " + app.getName());
                Log.d("ParseApplications", "Artist: " + app.getArtist());
                Log.d("ParseApplications", "Release Date: " + app.getReleaseDate());
            }

            //3.1 retorno do método
        return status;
    }
        // 3.16 Vamos testar o ParseAplication criando uma instância na MainActivity
    }

