package sg.jackiez.worker.module.ok.model;

public class Ticker {

    public double buy;
    public double high;
    public double last;
    public double low;
    public double sell;
    public double vol;

    @Override
    public String toString() {
        return "Ticker{" +
                "buy=" + buy +
                ", high=" + high +
                ", last=" + last +
                ", low=" + low +
                ", sell=" + sell +
                ", vol=" + vol +
                '}';
    }
}
