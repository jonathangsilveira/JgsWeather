package br.edu.example.jonathan.jgsweather.repository;

public class CityNotFoundException extends BusinessException {

    public CityNotFoundException(String message) {
        super(message);
    }

}
