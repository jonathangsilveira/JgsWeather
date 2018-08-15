package br.edu.example.jonathan.jgsweather;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String NEW_LINE = "\n";

    private RecyclerView climaView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        climaView = findViewById(R.id.activity_main_weather_list);
        climaView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        new ClimaAtualTarefaAssincrona(this).execute("Cairo", "Blumenau", "Gaspar");
    }

    private void acaoClickCidade(String cidade) {
        startActivity(ForecastActivity.newIntent(this, cidade));
    }

    private String buscarClimaAtual(String cidade) throws Exception {
        HttpURLConnection conexaoHttp = null;
        BufferedReader reader = null;
        try {
            Uri uri = Uri.parse("http://api.openweathermap.org/data/2.5/weather?").buildUpon()
                    .appendQueryParameter("q", cidade)
                    .appendQueryParameter("APPID", getString(R.string.open_wheather_app_id))
                    .build();
            URL url = new URL(uri.toString());
            conexaoHttp = (HttpURLConnection) url.openConnection();
            conexaoHttp.setRequestMethod("GET");
            conexaoHttp.connect();
            InputStream inputStream = conexaoHttp.getInputStream();
            StringBuilder json = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String linha;
            while ((linha = reader.readLine()) != null) {
                json.append(linha).append(NEW_LINE);
            }
            return json.toString();
        } finally {
            if (conexaoHttp != null) {
                conexaoHttp.disconnect();
            }
            if (reader != null) {
                reader.close();
            }
        }
    }

    private List<String> buscarClimaAtualCidades(List<String> cidades) throws Exception {
        List<String> jsons = new ArrayList<>();
        if (!cidades.isEmpty()) {
            for (String cidade : cidades) {
                String json = buscarClimaAtual(cidade);
                if (!TextUtils.isEmpty(json)) {
                    jsons.add(json);
                }
            }
        }
        return jsons;
    }

    private void mostrarMensagemErro(String mensagem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.error);
        builder.setMessage(mensagem);
        builder.setPositiveButton(R.string.ok, null);
        final AlertDialog dialogAlerta = builder.show();
        Button botaoOk = dialogAlerta.getButton(DialogInterface.BUTTON_POSITIVE);
        botaoOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlerta.dismiss();
            }
        });
    }

    private void setClimaAdapter(List<Clima> climas) {
        climaView.setAdapter(new ClimaAdapter(this, climas));
    }

    class ClimaViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewCidade;

        private TextView textViewClima;

        private TextView textViewMinimo;

        private TextView textViewMaxima;

        private ImageView imageViewIcone;

        ClimaViewHolder(View itemView) {
            super(itemView);
            textViewCidade = itemView.findViewById(R.id.item_list_weather_city);
            textViewClima = itemView.findViewById(R.id.item_list_weather_status);
            textViewMinimo = itemView.findViewById(R.id.item_list_weather_min);
            textViewMaxima = itemView.findViewById(R.id.item_list_weather_max);
            imageViewIcone = itemView.findViewById(R.id.item_list_weather_image);
        }

    }

    class ClimaAdapter extends RecyclerView.Adapter<ClimaViewHolder> {

        private Context contexto;

        private List<Clima> climaCidades;

        ClimaAdapter(Context contexto, List<Clima> climaCidades) {
            this.contexto = contexto;
            this.climaCidades = climaCidades;
        }

        @Override
        public ClimaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(contexto).inflate(R.layout.item_list_weather, null);
            return new ClimaViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ClimaViewHolder holder, int position) {
            final Clima clima = climaCidades.get(position);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    acaoClickCidade(clima.getCidade());
                }
            });
            holder.textViewCidade.setText(clima.getCidade());
            holder.textViewClima.setText(contexto.getString(R.string.label_weather, clima.getStatus()));
            DecimalFormat decimal = (DecimalFormat) NumberFormat.getInstance();
            decimal.setMinimumFractionDigits(2);
            decimal.setMaximumFractionDigits(2);
            holder.textViewMinimo.setText(contexto.getString(R.string.label_min,
                    decimal.format(clima.getTemperaturaMinima())));
            holder.textViewMaxima.setText(contexto.getString(R.string.label_max,
                    decimal.format(clima.getTemperaturaMaxima())));
            holder.imageViewIcone.setImageResource(Util.getIconeClimaPeloId(clima.getCodigoClima()));
        }

        @Override
        public int getItemCount() {
            return climaCidades.size();
        }

    }

    class ClimaAtualTarefaAssincrona extends AsyncTask<String, Void, List<String>> {

        private String mensagemErro;

        private AlertDialog dialogoProgresso;

        private Context contexto;

        ClimaAtualTarefaAssincrona(Context contexto) {
            this.contexto = contexto;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
            builder.setTitle(R.string.processing);
            builder.setMessage(R.string.searching_current_weather);
            builder.setCancelable(true);
            dialogoProgresso = builder.show();
        }

        @Override
        protected List<String> doInBackground(String... cidades) {
            List<String> climas = new ArrayList<>();
            try {
                climas.addAll(buscarClimaAtualCidades(Arrays.asList(cidades)));
            } catch (Exception e) {
                mensagemErro = e.getMessage();
            }
            return climas;
        }

        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);
            if (dialogoProgresso != null && dialogoProgresso.isShowing()) {
                dialogoProgresso.dismiss();
            }
            if (!TextUtils.isEmpty(mensagemErro)) {
                mostrarMensagemErro(mensagemErro);
            } else {
                try {
                    List<Clima> climas = new ArrayList<>();
                    for (String json : result) {
                        Clima clima = Util.converterJsonParaClima(json);
                        Log.d("MERDA", clima.getCidade());
                        climas.add(clima);
                    }
                    setClimaAdapter(climas);
                } catch (JSONException e) {
                    Log.d("MERDA", e.getMessage());
                }
            }
        }

    }

}
