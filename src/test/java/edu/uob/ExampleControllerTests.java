package edu.uob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import edu.uob.OXOMoveException.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;

class ExampleControllerTests {
  private OXOModel model;
  private OXOController controller;

  // Make a new "standard" (3x3) board before running each test case (i.e. this method runs before every `@Test` method)
  // In order to test boards of different sizes, winning thresholds or number of players, create a separate test file (without this method in it !)
  @BeforeEach
  void setup() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
  }

  // This next method is a utility function that can be used by any of the test methods to _safely_ send a command to the controller
  void sendCommandToController(String command) {
      // Try to send a command to the server - call will timeout if it takes too long (in case the server enters an infinite loop)
      // Note: this is ugly code and includes syntax that you haven't encountered yet
      String timeoutComment = "Controller took too long to respond (probably stuck in an infinite loop)";
      assertTimeoutPreemptively(Duration.ofMillis(1000), ()-> controller.handleIncomingCommand(command), timeoutComment);
  }

  // Test simple move taking and cell claiming functionality
  @Test
  void testBasicMoveTaking() throws OXOMoveException {
    // Find out which player is going to make the first move
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a move
    sendCommandToController("a1");
    // Check that A1 (cell [0,0] on the board) is now "owned" by the first player
    String failedTestComment = "Cell a1 wasn't claimed by the first player";
    assertEquals(firstMovingPlayer, controller.gameModel.getCellOwner(0, 0), failedTestComment);
  }

  // Test out basic win detection
  @Test
  void testBasicWin() throws OXOMoveException {
    // Find out which player is going to make the first move (they should be the eventual winner)
    OXOPlayer firstMovingPlayer = model.getPlayerByNumber(model.getCurrentPlayerNumber());
    // Make a bunch of moves for the two players
    sendCommandToController("a1"); // First player
    sendCommandToController("b1"); // Second player
    sendCommandToController("a2"); // First player
    sendCommandToController("b2"); // Second player
    sendCommandToController("a3"); // First player

    // a1, a2, a3 should be a win for the first player (since players alternate between moves)
    // Let's check to see whether the first moving player is indeed the winner
    String failedTestComment = "Winner was expected to be " + firstMovingPlayer.getPlayingLetter() + " but wasn't";
    assertEquals(firstMovingPlayer, model.getWinner(), failedTestComment);
  }

  // Example of how to test for the throwing of exceptions
  @Test
  void testInvalidIdentifierException() throws OXOMoveException {
    // Check that the controller throws a suitable exception when it gets an invalid command
    String failedTestComment = "Controller failed to throw an InvalidIdentifierLengthException for command `abc123`";
    // The next lins is a bit ugly, but it is the easiest way to test exceptions (soz)
    assertThrows(InvalidIdentifierLengthException.class, ()-> sendCommandToController("abc123"), failedTestComment);
  }

    @Test
    public void testMultiplePlayers() {
      // Create a model with 3 players
      model = new OXOModel(3, 3, 3);
      model.addPlayer(new OXOPlayer('X'));
      model.addPlayer(new OXOPlayer('O'));
      model.addPlayer(new OXOPlayer('Z'));
      controller = new OXOController(model);
      // Create a controller with the model
      OXOController controller = new OXOController(model);
      //check if there are 3 players
      assert(model.getNumberOfPlayers() == 3);
      // Make some moves
      sendCommandToController("a1"); // player 1
      sendCommandToController("a2"); // player 2
      sendCommandToController("a3"); // player 3
      sendCommandToController("b1"); // player 1
      sendCommandToController("b2"); // player 2
      sendCommandToController("b3"); // player 3
      sendCommandToController("c1"); // player 1
      // Check that the moves were made correctly
      assertEquals(model.getCellOwner(0, 0), model.getPlayerByNumber(0));
      assertEquals(model.getCellOwner(0, 1), model.getPlayerByNumber(1));
      assertEquals(model.getCellOwner(0, 2), model.getPlayerByNumber(2));
      assertEquals(model.getCellOwner(1, 0), model.getPlayerByNumber(0));
      assertEquals(model.getCellOwner(1, 1), model.getPlayerByNumber(1));
      assertEquals(model.getCellOwner(1, 2), model.getPlayerByNumber(2));
      assertEquals(model.getCellOwner(2, 0), model.getPlayerByNumber(0));
    }
  @Test
  void testInvalidException() {
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
    // Create a controller with the model
    OXOController controller = new OXOController(model);
    // InvalidIdentifierLengthException
    assertThrows(InvalidIdentifierLengthException.class, ()-> controller.handleIncomingCommand("aa1"));
    // InvalidIdentifierCharacterException
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("1a"));
    // OXOMoveException$OutsideCellRangeException
    assertThrows(OutsideCellRangeException.class, () -> { throw new OutsideCellRangeException(RowOrColumn.ROW, 5); } );
    //OutsideCellRangeException for row
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("g2"));
    //OutsideCellRangeException for column
    assertThrows(OutsideCellRangeException.class, ()-> controller.handleIncomingCommand("a9"));
    //CellAlreadyTakenException
    sendCommandToController("a1"); // player 1
    assertThrows(CellAlreadyTakenException.class, ()-> controller.handleIncomingCommand("a1"));
    //Non-english letter
    assertThrows(InvalidIdentifierCharacterException.class, ()-> controller.handleIncomingCommand("Ą1"));

  }
  @Test
  void checkForDraw() {
    // Create a model with 3 players
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
    // Create a controller with the model
    OXOController controller = new OXOController(model);

    // Make some moves
    //Increase winthreshold before the game starts to 4
    model.setWinThreshold(model.getWinThreshold() + 1);
    sendCommandToController("a1"); // player 1
    sendCommandToController("b1"); // player 2
    sendCommandToController("a2"); // player 1
    sendCommandToController("b2"); // player 2
    sendCommandToController("c1"); // player 1
    //Attempt to decrease winthreshold, but it remains 4, as it should
    controller.decreaseWinThreshold();
    sendCommandToController("a3"); // player 2
    sendCommandToController("c2"); // player 1
    sendCommandToController("c3"); // player 2
    sendCommandToController("b3"); // player 1
    assert (model.getWinThreshold() == 4);
    assert (model.isGameDrawn());
  }
    @Test
    void increaseWinthreshold(){
      // Create a model with 3 players
      model = new OXOModel(3, 3, 3);
      model.addPlayer(new OXOPlayer('X'));
      model.addPlayer(new OXOPlayer('O'));
      controller = new OXOController(model);
      // Create a controller with the model
      OXOController controller = new OXOController(model);

      // Make some moves
      //Increase winthreshold before the game starts to 4
      model.setWinThreshold(model.getWinThreshold() + 1);
      sendCommandToController("a1"); // player 1
      sendCommandToController("b1"); // player 2
      sendCommandToController("a2"); // player 1
      sendCommandToController("b2"); // player 2
      sendCommandToController("c1"); // player 1
      //Attempt to decrease winthreshold, but it remains 4, as it should
      controller.decreaseWinThreshold();
      sendCommandToController("a3"); // player 2
      sendCommandToController("c2"); // player 1
      sendCommandToController("c3"); // player 2
      sendCommandToController("b3"); // player 1
      assert(model.getWinThreshold() == 4);
      assert(model.isGameDrawn());
  }
  @Test
  void addColumnsRows(){
    // Create a model with 3 players
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
    // Create a controller with the model
    OXOController controller = new OXOController(model);

    // Make some moves
    sendCommandToController("b1"); // player 1
    sendCommandToController("a1"); // player 2
    //add one row and col
    model.addRow();
    model.addColumn();
    sendCommandToController("c1"); // player 1
    sendCommandToController("b2"); // player 2
    sendCommandToController("d1"); // player 1
    //check that game is not a draw
    assert(!model.isGameDrawn());
    //check that there is a winner
    assert(model.getWinner() != null);
    //reset game
    controller.reset();
    //check if the board is empty
    assert(controller.isBoardEmpty() == true);
    //make some moves
    sendCommandToController("a1"); // player 1
    sendCommandToController("a4"); // player 2
    sendCommandToController("d1"); // player
    //attempt to remove row and col that are occupied
    model.removeColumn();
    model.removeRow();
    //check that row and col have not been removed
    assert(model.getNumberOfColumns() == 4);
    assert(model.getNumberOfRows() == 4);
  }
  @Test
  void checkForEdgeExamples(){
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
    // Create a controller with the model
    OXOController controller = new OXOController(model);
    // 2x2
    model.removeRow();
    model.removeColumn();
    assert(model.getNumberOfColumns() == 2);
    assert(model.getNumberOfRows() == 2);
    // 1x1
    model.removeRow();
    model.removeColumn();
    assert(model.getNumberOfColumns() == 1);
    assert(model.getNumberOfRows() == 1);
    sendCommandToController("a1"); // player 1
    assert(model.isGameDrawn() == true);
  }
  @Test
  void checkDiagonalWinthreshold (){
    model = new OXOModel(3, 3, 3);
    model.addPlayer(new OXOPlayer('X'));
    model.addPlayer(new OXOPlayer('O'));
    controller = new OXOController(model);
    // Create a controller with the model
    OXOController controller = new OXOController(model);
    //create 5x5
    model.addRow();
    model.addColumn();
    model.addRow();
    model.addColumn();
    //increase to 4
    model.setWinThreshold(model.getWinThreshold() + 1);
    sendCommandToController("a1"); // player 1
    sendCommandToController("a2"); // player 2
    sendCommandToController("b2"); // player 1
    //increase to 5
    model.setWinThreshold(model.getWinThreshold() + 1);
    sendCommandToController("a3"); // player 2
    sendCommandToController("c3"); // player 1
    sendCommandToController("d1"); // player 2
    sendCommandToController("d4"); // player 1
    sendCommandToController("d5"); // player 2
    sendCommandToController("e5"); // player 1
    assert(model.getWinThreshold() == 5);
    assert(controller.checkForWin() == true);
  }
}
