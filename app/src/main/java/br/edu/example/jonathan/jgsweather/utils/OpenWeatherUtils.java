package br.edu.example.jonathan.jgsweather.utils;

import br.edu.example.jonathan.jgsweather.R;

public final class OpenWeatherUtils {

    public static int getWeatherIcon(long idClima) {
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
        } else if (idClima == 781) {
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
