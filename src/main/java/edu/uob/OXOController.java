package edu.uob;
import edu.uob.OXOMoveException.*;

public class OXOController {
    OXOModel gameModel;

    public OXOController(OXOModel model) {
        gameModel = model;
    }

    public void handleIncomingCommand(String command) throws OXOMoveException {

        int row = command.charAt(0) - 'a';
        int length = command.length();
        //Is string length 2?
        if (length != 2 || length == 1){
            throw new InvalidIdentifierLengthException(length);
        }
        int col = Character.getNumericValue(command.charAt(1)) - 1;
        if (checkForWin() == true) {
            throw new CellAlreadyTakenException(row, col);
        }
        //Convert the command to lowercase
        command = command.toLowerCase();

        //Extract the row letter and column number from the command
        char rowLetter = command.charAt(0);

        char colDigit = command.charAt(1);
        //Is Row a letter?
        if (!Character.isLetter(rowLetter) || row > 26){
            throw new InvalidIdentifierCharacterException(RowOrColumn.ROW, command.charAt(0));
        }
        //Is Column a digit?
        if (!Character.isDigit(colDigit) || col > 26){
            throw new InvalidIdentifierCharacterException(RowOrColumn.COLUMN, command.charAt(1));
        }
        //Is it within the range?
        if (row >= gameModel.getNumberOfRows()) {
            throw new OutsideCellRangeException(RowOrColumn.ROW, row);
        }
        if (col >= gameModel.getNumberOfColumns()) {
            throw new OutsideCellRangeException(RowOrColumn.COLUMN, col);
        }
        //Get the current player
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());

        //check if the cell is claimed
        if (gameModel.getCellOwner(row, col) != null){
            throw new CellAlreadyTakenException(row, col);
        }


        //claim the cell for current player
        gameModel.setCellOwner(row, col, currentPlayer);
        if (checkForWin() == true) {
            return;
        }
        //next player
        int nextPlayerNumber = (gameModel.getCurrentPlayerNumber() + 1) % gameModel.getNumberOfPlayers();
        gameModel.setCurrentPlayerNumber(nextPlayerNumber);
    }

    public void addRow() {
        gameModel.addRow();
    }
    public void removeRow() {
        gameModel.removeRow();
    }
    public void addColumn() {
        gameModel.addColumn();
    }
    public void removeColumn() {
        gameModel.removeColumn();
    }
    public void increaseWinThreshold() {
        gameModel.setWinThreshold(gameModel.getWinThreshold() + 1);
    }
    public void decreaseWinThreshold() {
        //Decrease only if empty board! or if there is a winner
        if (isBoardEmpty()){
            gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
        }
        else if(gameModel.getWinner() != null){
            gameModel.setWinThreshold(gameModel.getWinThreshold() - 1);
        }
    }
    public boolean isBoardEmpty() {
        for (int row = 0; row < gameModel.getNumberOfRows(); row++) {
            for (int col = 0; col < gameModel.getNumberOfColumns(); col++) {
                if (gameModel.getCellOwner(row, col) != null) {
                    return false;
                }
            }
        }
        return true;
    }
    public void reset() {
        gameModel.clearCells(); // clear the board
        gameModel.setCurrentPlayerNumber(0); // reset the player turn to the first player
        gameModel.setWinner(null); // reset the winner
        gameModel.setDrawnReset();
    }
    public boolean checkForWin() {
        if (checkHorizontalWin() || checkVerticalWin() || checkDiagonalWin() || checkDraw()) {
            return true;
        } else {
            return false;
        }
    }
    private boolean checkHorizontalWin() {
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
        for (int row = 0; row < gameModel.getNumberOfRows(); row++) {
            int count = 0;
            for (int col = 0; col < gameModel.getNumberOfColumns(); col++) {
                if (gameModel.getCellOwner(row, col) == currentPlayer) {
                    count++;
                    if (count == gameModel.getWinThreshold()) {
                        gameModel.setWinner(currentPlayer);
                        return true; // We have a win!
                    }
                } else {
                    count = 0;
                }
            }
        }
        return false;
    }
    private boolean checkVerticalWin() {
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
        for (int col = 0; col < gameModel.getNumberOfColumns(); col++) {
            int count = 0;
            for (int row = 0; row < gameModel.getNumberOfRows(); row++) {
                if (gameModel.getCellOwner(row, col) == currentPlayer) {
                    count++;
                    if (count == gameModel.getWinThreshold()) {
                        gameModel.setWinner(currentPlayer);
                        return true;
                    }
                } else {
                    count = 0;
                }
            }
        }
        return false;
    }
    private boolean checkDiagonalWin() {
        return checkDiagonalWinTopLeftToBottomRight() || checkDiagonalWinTopRightToBottomLeft();
    }
    private boolean checkDiagonalWinTopLeftToBottomRight() {
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
        for (int i = 0; i <= gameModel.getNumberOfRows() - gameModel.getWinThreshold(); i++) {
            int count = 0;
            for (int j = 0; j <= gameModel.getNumberOfColumns() - gameModel.getWinThreshold(); j++) {
                count = 0;
                for (int k = 0; k < gameModel.getWinThreshold(); k++) {
                    if (gameModel.getCellOwner(i + k, j + k) == currentPlayer) {
                        count++;
                    } else {
                        break;
                    }
                }
                if (count == gameModel.getWinThreshold()) {
                    gameModel.setWinner(currentPlayer);
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkDiagonalWinTopRightToBottomLeft() {
        OXOPlayer currentPlayer = gameModel.getPlayerByNumber(gameModel.getCurrentPlayerNumber());
        for (int i = 0; i <= gameModel.getNumberOfRows() - gameModel.getWinThreshold(); i++) {
            int count = 0;
            for (int j = gameModel.getWinThreshold() - 1; j < gameModel.getNumberOfColumns(); j++) {
                count = 0;
                for (int k = 0; k < gameModel.getWinThreshold(); k++) {
                    if (gameModel.getCellOwner(i + k, j - k) == currentPlayer) {
                        count++;
                    } else {
                        break;
                    }
                }
                if (count == gameModel.getWinThreshold()) {
                    gameModel.setWinner(currentPlayer);
                    return true;
                }
            }
        }
        return false;
    }
    private boolean checkDraw() {
        for (int row = 0; row < gameModel.getNumberOfRows(); row++) {
            for (int col = 0; col < gameModel.getNumberOfColumns(); col++) {
                if (gameModel.getCellOwner(row, col) == null) {
                    return false; // There is still an unoccupied cell, the game is not a draw
                }
            }
        }
        // All cells are occupied, the game is a draw
        gameModel.setGameDrawn();
        return true;
    }
}
