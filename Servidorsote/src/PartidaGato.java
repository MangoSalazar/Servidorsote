
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
    private void cambiarTurno() {
        turnoActual = (turnoActual == idJugadorX) ? idJugadorO : idJugadorX;
    }
    public boolean hayGanador() {
        for (int i = 0; i < 3; i++) {
            if (check(tablero[i][0], tablero[i][1], tablero[i][2])) return true;
            if (check(tablero[0][i], tablero[1][i], tablero[2][i])) return true;
        }
        if (check(tablero[0][0], tablero[1][1], tablero[2][2])) return true;
        if (check(tablero[0][2], tablero[1][1], tablero[2][0])) return true;
        return false;
    }
    public boolean esEmpate() {
        for (char[] fila : tablero) for (char c : fila) 
            if (c != 'X' && c != 'O') return false;
        return true;
    }

    private boolean check(char a, char b, char c) {
        return a == b && b == c;
    }
    public String dibujarTablero() {
        return String.format("\n %c | %c | %c \n---+---+---\n %c | %c | %c \n---+---+---\n %c | %c | %c \n",
            tablero[0][0], tablero[0][1], tablero[0][2],
            tablero[1][0], tablero[1][1], tablero[1][2],
            tablero[2][0], tablero[2][1], tablero[2][2]);
    }
    
    public int getOponente(int idYo) {
        return (idYo == idJugadorX) ? idJugadorO : idJugadorX;
    }
}
