package fx.javafx.ListRow;

public  class ListRow {
    private String nrTowaru;
    private int ilTowaru;
    private String miejsceTowaru;
    private String nazwaTowaru;

    public ListRow(String nrTowaru, int ilTowaru, String miejsceTowaru, String nazwaTowaru) {
        this.nrTowaru = nrTowaru;
        this.ilTowaru = ilTowaru;
        this.miejsceTowaru = miejsceTowaru;
        this.nazwaTowaru = nazwaTowaru;
        
    }


    public String getNrTowaru() {
        return nrTowaru;
    }

    public void setNrTowaru(String nrTowaru) {
        this.nrTowaru = nrTowaru;
    }

    public int getIlTowaru() {
        return ilTowaru;
    }

    public void setIlTowaru(int ilTowaru) {
        this.ilTowaru = ilTowaru;
    }

    public String getMiejsceTowaru() {
        return miejsceTowaru;
    }

    public void setMiejsceTowaru(String miejsceTowaru) {
        this.miejsceTowaru = miejsceTowaru;
    }

    public String getNazwaTowaru() {
        return nazwaTowaru;
    }

    public void setNazwaTowaru(String nazwaTowaru) {
        this.nazwaTowaru = nazwaTowaru;
    }
}