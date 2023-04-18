package edu.uob;
import java.util.ArrayList;
public class OXOModel {
    //outer arraylist represents rows, inner columns
    private ArrayList<ArrayList<OXOPlayer>> cells;
    private ArrayList<OXOPlayer> players;
    private int currentPlayerNumber;
    private OXOPlayer winner;
    private boolean gameDrawn;
    private int winThreshold;

    public OXOModel(int numberOfRows, int numberOfColumns, int winThresh) {
        winThreshold = winThresh;
        cells = new ArrayList<ArrayList<OXOPlayer>>();
        // Initialize arraylist
        for (int i = 0; i < numberOfRows; i++) {
            ArrayList<OXOPlayer> row = new ArrayList<OXOPlayer>();
            for (int j = 0; j < numberOfColumns; j++) {
                row.add(null);
            }
            cells.add(row);
        }
        players = new ArrayList<OXOPlayer>();
    }


    public int getNumberOfPlayers() {
        return players.size();
    }

    public void addPlayer(OXOPlayer player) {
        players.add(player);
    }

    public OXOPlayer getPlayerByNumber(int number) {
        if (number < 0 || number >= players.size()) {
            throw new IndexOutOfBoundsException("Invalid player index");
        }
        return players.get(number);
    }

    public OXOPlayer getWinner() {
        return winner;
    }

    public void setWinner(OXOPlayer player) {
        winner = player;
    }

    public int getCurrentPlayerNumber() {
        return currentPlayerNumber;
    }

    public void setCurrentPlayerNumber(int playerNumber) {
        currentPlayerNumber = playerNumber;
    }

    public int getNumberOfRows() {
        return cells.size();
    }

    public int getNumberOfColumns() {
        return cells.get(0).size();
    }

    public OXOPlayer getCellOwner(int rowNumber, int colNumber) {
        return cells.get(rowNumber).get(colNumber);
    }

    public void setCellOwner(int rowNumber, int colNumber, OXOPlayer player) {
        cells.get(rowNumber).set(colNumber,player);
    }

    public void setWinThreshold(int winThresh) {
        if (winThresh < 3){
            winThreshold = 3;
        } else {
            winThreshold = winThresh;
        }
    }

    public int getWinThreshold() {
        return winThreshold;
    }

    public void setGameDrawn() {
        gameDrawn = true;
    }
    public void setDrawnReset (){
        gameDrawn = false;
    }

    public boolean isGameDrawn() {
        return gameDrawn;
    }

    public void addRow() {
        //int numCols = getNumberOfColumns();
        ArrayList<OXOPlayer> row = new ArrayList<OXOPlayer>();
        if (getNumberOfRows() < 9){
        for (int i = 0; i < getNumberOfColumns(); i++) {
            row.add(null);
        }
        gameDrawn = false;
        cells.add(cells.size(), row);
    }
    }

    public void addColumn(){
        if (getNumberOfColumns() < 9){
        for (int j = 0; j < getNumberOfRows(); j++){
            cells.get(j).add(null);
        }
        gameDrawn = false;
    }
    }
    public void removeRow() {
        for (int i = 0; i < getNumberOfColumns(); i++) {
            if (getCellOwner(getNumberOfRows()-1, i) != null) {
                return;
            }
        }
        if (getNumberOfRows() > 1) {
            cells.remove(cells.size() - 1);
            winner = null;
            gameDrawn = false;
            if (currentPlayerNumber > 0) {
                currentPlayerNumber = (currentPlayerNumber - 1) % players.size();
            }
        }
    }
    public void removeColumn() {
        for (int i = 0; i < getNumberOfRows(); i++){
            if (getCellOwner(i, getNumberOfColumns() -1) != null){
                return;
            }
        }
        if (getNumberOfColumns() > 1) {
            // Remove last column from each row
            for (int i = 0; i < getNumberOfRows(); i++) {
                cells.get(i).remove(cells.get(i).size() - 1);
            }
            winner = null;
            gameDrawn = false;
            if (currentPlayerNumber > 0) {
                currentPlayerNumber = (currentPlayerNumber - 1) % players.size();
            }
        }
    }
    public void clearCells(){
        for (int row = 0; row < getNumberOfRows(); row++){
            for (int col = 0; col < getNumberOfColumns(); col++){
                cells.get(row).set(col, null);
            }
        }
    }
}


