
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
    public String realizarJugada(int idJugador, int casilla) throws Exception {
        if (idJugador != turnoActual) throw new Exception("No es tu turno.");
        if (casilla < 1 || casilla > 9) throw new Exception("Casilla inválida (1-9).");
        
        int fila = (casilla - 1) / 3;
        int col = (casilla - 1) % 3;
        
        if (tablero[fila][col] == 'X' || tablero[fila][col] == 'O') throw new Exception("Casilla ocupada.");
        
        tablero[fila][col] = (idJugador == idJugadorX) ? 'X' : 'O';
        cambiarTurno();
        return dibujarTablero();
    }
}
