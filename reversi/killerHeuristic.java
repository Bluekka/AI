import reversi.*;
import java.util.Vector;
import java.util.Iterator;

public class killerHeuristic implements ReversiAlgorithm
{
    // Constants
    private final static int DEPTH_LIMIT = 2; // Just an example value.
    // Variables
    boolean initialized;
    volatile boolean running; // Note: volatile for synchronization issues.
    GameController controller;
    GameState initialState;
    int myIndex;
	int aiIndex;
    Move selectedMove;

    public killerHeuristic() {} //the constructor
      
    public void requestMove(GameController requester)
    {
        running = false;
        requester.doMove(selectedMove);
    }

    public void init(GameController game, GameState state, int playerIndex, int turnLength)
    {
        initialState = state;
        myIndex = playerIndex;
        controller = game;
        initialized = true;
	}

    public String getName() { return "killerHeuristic"; }

    public void cleanup() {}
    public void run()
    {
        //implementation of the actual algorithm
        while(!initialized);
        initialized = false;
        running = true;
        selectedMove = null;

        int currentDepth = 1;

        while (running && currentDepth < DEPTH_LIMIT)
        {
			Move newMove = searchToDepth(currentDepth++);
  
            // Check that there's a new move available.
            if (newMove != null)
                selectedMove = newMove;
        }
      
        if (running) // Make a move if there is still time left.
        {
            controller.doMove(selectedMove);
        }
    }
     
    Move searchToDepth(int depth)
    {	
		if (myIndex == 1)
			aiIndex = 0;
		else
			aiIndex = 1;

        Move parentMove;
		Move childMove;
        Vector parentMoves = initialState.getPossibleMoves(myIndex);
		Vector childMoves;
		
		Move optimalMove = null;
		GameState nextState;
		GameState finalState;
		Node lapsi;
		Node juuri;
		int disc;
		int maxChild = 0;
		int maxAi = 0;
		
		for (int i = 0; i < parentMoves.size(); i++) {
			parentMove = (Move)parentMoves.elementAt(i);
			nextState = initialState.getNewInstance(parentMove);
			
			juuri = new Node(nextState, parentMove);
			childMoves = nextState.getPossibleMoves(aiIndex);

			for (int j = 0; j < childMoves.size(); j++) {
				childMove = (Move)childMoves.elementAt(j);
				finalState = nextState.getNewInstance(childMove);
				
				disc = finalState.getMarkCount(aiIndex);
				lapsi = new Node(finalState, childMove);
				lapsi.setScore(disc);
				juuri.addChild(lapsi);
				
				if (disc > maxChild)
					maxChild = disc;
			}
			
			if (maxAi == 0)
				maxChild = maxAi;
			if (maxChild <= maxAi)
				optimalMove = (Move)parentMoves.elementAt(i);

		}
		/*Vector kidit = juuri.getChildren();
		Iterator e = kidit.iterator();*/

        return optimalMove;
    }
}
 