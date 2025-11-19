
public class PartidaGato {
    private final char[][] tablero = {{'1','2','3'}, {'4','5','6'}, {'7','8','9'}};
    public final int idJugadorX;
    public final int idJugadorO;
    private int turnoActual;

    public PartidaGato(int idX, int idO) {
        this.idJugadorX = idX;
        this.idJugadorO = idO;
        this.turnoActual = idX;
    }
}
