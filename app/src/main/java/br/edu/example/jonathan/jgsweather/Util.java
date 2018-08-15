package br.edu.example.jonathan.jgsweather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class Util {

    public static Clima converterJsonParaClima(String json) throws JSONException {
        final String PARAMETRO_NOME_CIDADE = "name";
        final String PARAMETRO_PRINCIPAL = "main";
        final String PARAMETRO_TEMPERATURA = "temp";
        final String PARAMETRO_TEMPERATURA_MINIMA = "temp_min";
        final String PARAMETRO_TEMPERATURA_MAXIMA = "temp_max";
        final String PARAMETRO_CLIMA = "weather";
        final String PARAMETRO_ID = "id";
        final String PARAMETRO_DESCRICAO = "description";
        JSONObject objetoJson = new JSONObject(json);
        JSONObject principal = objetoJson.getJSONObject(PARAMETRO_PRINCIPAL);
        JSONArray clima = objetoJson.getJSONArray(PARAMETRO_CLIMA);
        String nomeCidade = objetoJson.getString(PARAMETRO_NOME_CIDADE);
        double temperaturaAtual = principal.getDouble(PARAMETRO_TEMPERATURA);
        double temperaturaMinima = principal.getDouble(PARAMETRO_TEMPERATURA_MINIMA);
        double temperaturaMaxima = principal.getDouble(PARAMETRO_TEMPERATURA_MAXIMA);
        int climaID = clima.getJSONObject(0).getInt(PARAMETRO_ID);
        String descricao = clima.getJSONObject(0).getString(PARAMETRO_DESCRICAO);
        Clima climaAtual = new Clima();
        climaAtual.setCodigoClima(climaID);
        climaAtual.setCidade(nomeCidade);
        climaAtual.setStatus(descricao);
        climaAtual.setTemperaturaAtual(temperaturaAtual);
        climaAtual.setTemperaturaMinima(temperaturaMinima);
        climaAtual.setTemperaturaMaxima(temperaturaMaxima);
        return climaAtual;
    }

    public static List<Clima> converterJsonParaLista(String json) throws JSONException {
        final String PARAMETRO_CIDADE = "city";
        final String PARAMETRO_CIDADE_NOME = "name";
        final String PARAMETRO_REGISTROS = "list";
        final String PARAMETRO_TEMPERATURA = "main";
        final String PARAMETRO_TEMPERATURA_ATUAL = "temp";
        final String PARAMETRO_TEMPERATURA_MINIMA = "temp_min";
        final String PARAMETRO_TEMPERATUEA_MAXIMA = "temp_max";
        final String PARAMETRO_CLIMA = "weather";
        final String PARAMETRO_CLIMA_DESCRICAO = "description";
        final String PARAMETRO_CLIMA_ID = "id";
        List<Clima> climas = new ArrayList<>();
        JSONObject objetoJson = new JSONObject(json);
        JSONObject objetoCidade = objetoJson.getJSONObject(PARAMETRO_CIDADE);
        String nomeCidade = objetoCidade.getString(PARAMETRO_CIDADE_NOME);
        JSONArray registros = objetoJson.getJSONArray(PARAMETRO_REGISTROS);
        for (int i = 0; i < registros.length(); i++) {
            JSONObject registro = registros.getJSONObject(i);
            JSONObject objetoTemperatura = registro.getJSONObject(PARAMETRO_TEMPERATURA);
            JSONArray registrosClima = registro.getJSONArray(PARAMETRO_CLIMA);
            JSONObject registroClima = registrosClima.getJSONObject(0);
            double temperaturaAtual = objetoTemperatura.getDouble(PARAMETRO_TEMPERATURA_ATUAL);
            double temperaturaMinima = objetoTemperatura.getDouble(PARAMETRO_TEMPERATURA_MINIMA);
            double temperaturaMaxima = objetoTemperatura.getDouble(PARAMETRO_TEMPERATUEA_MAXIMA);
            int idClima = registroClima.getInt(PARAMETRO_CLIMA_ID);
            String descricao = registroClima.getString(PARAMETRO_CLIMA_DESCRICAO);
            Clima clima = new Clima();
            clima.setCidade(nomeCidade);
            clima.setTemperaturaAtual(temperaturaAtual);
            clima.setTemperaturaMinima(temperaturaMinima);
            clima.setTemperaturaMaxima(temperaturaMaxima);
            clima.setCodigoClima(idClima);
            clima.setStatus(descricao);
            climas.add(clima);
        }
        return climas;
    }

    public static int getIconeClimaPeloId(int idClima) {
        // Tirado do código do clima em :
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (idClima >= 200 && idClima <= 232) {
            return R.drawable.ic_storm;
        } else if (idClima >= 300 && idClima <= 321) {
            return R.drawable.ic_light_rain;
        } else if (idClima >= 500 && idClima <= 504) {
            return R.drawable.ic_rain;
        } else if (idClima == 511) {
            return R.drawable.ic_snow;
        } else if (idClima >= 520 && idClima <= 531) {
            return R.drawable.ic_rain;
        } else if (idClima >= 600 && idClima <= 622) {
            return R.drawable.ic_snow;
        } else if (idClima >= 701 && idClima <= 761) {
            return R.drawable.ic_fog;
        } else if (idClima == 761 || idClima == 781) {
            return R.drawable.ic_storm;
        } else if (idClima == 800) {
            return R.drawable.ic_clear;
        } else if (idClima == 801) {
            return R.drawable.ic_light_clouds;
        } else if (idClima >= 802 && idClima <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

}
