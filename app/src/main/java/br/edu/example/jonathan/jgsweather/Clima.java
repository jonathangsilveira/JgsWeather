package br.edu.example.jonathan.jgsweather;

public class Clima {

    private long codigo;

    private String cidade;

    private String status;

    private double temperaturaMinima;

    private double temperaturaMaxima;

    private double temperaturaAtual;

    private int codigoClima;

    public long getCodigo() {
        return codigo;
    }

    public void setCodigo(long codigo) {
        this.codigo = codigo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTemperaturaMinima() {
        return temperaturaMinima;
    }

    public void setTemperaturaMinima(double temperaturaMinima) {
        this.temperaturaMinima = temperaturaMinima;
    }

    public double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(double temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    public double getTemperaturaAtual() {
        return temperaturaAtual;
    }

    public void setTemperaturaAtual(double temperaturaAtual) {
        this.temperaturaAtual = temperaturaAtual;
    }

    public int getCodigoClima() {
        return codigoClima;
    }

    public void setCodigoClima(int codigoClima) {
        this.codigoClima = codigoClima;
    }
}
