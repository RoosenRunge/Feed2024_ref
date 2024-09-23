package com.example.feed2023;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class MyFirstAdapter extends RecyclerView.Adapter{

   //1 - construtor para receber o ArrayList de Applications
   private ArrayList<Applications> aplications;
   public MyFirstAdapter(ArrayList<Applications> aplications) {
      this.aplications = aplications;
   }
   @NonNull
   @Override

   public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
   //3 - Infla a View definida no Layout e item do adapter e referência através da variável v
      //cria uma instância de ViewHolder e passando a view para a instância
      View v = LayoutInflater.from(parent.getContext()).inflate(
              R.layout.adapter_layout, parent, false);


      return new MyFirstViewHolder(v);
   }

   @Override
   public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
      //4 - aqui chamamos o proprio método bind do ViewHolder para informar o ViewHolder e a posição.
      ((MyFirstViewHolder) holder).bind(aplications.get(position));

   }

   @Override
   public int getItemCount() {
      //5 retorna o comprimento do array applications com os Top 10 apps.
      return aplications.size();
   }

   //2 --Criação da classe interna ViewHolder
   class MyFirstViewHolder extends RecyclerView.ViewHolder{
      ////2.1declara as variáveis correspondentes do layout do adapter
      private TextView name;
      private TextView artist;
      private TextView release;
      //2.2construtor da classe recebe a View da classe externa Adapter
      public MyFirstViewHolder(@NonNull View itemView) {
         super(itemView);

         //2.3 associação com os itens do layout do adapter
         name = itemView.findViewById(R.id.nameXML);
         artist = itemView.findViewById(R.id.textXML);
         release = itemView.findViewById(R.id.releaseXML);
      }
      //2.4 metodo bind faz a conexão entre a instância applications contento o conteúdo e a instância ViewHolder para exibição do conteudo no XML
      public void bind(final Applications applications) {
         name.setText("app Name: "+applications.getName());
         artist.setText("Artist: "+applications.getArtist());
         release.setText("Release: "+applications.getReleaseDate());

      }



   }
}

//6 -Vamos retornar a MainActivity para instanciar o Adapter e criar a RecyclerView