import reversi.*;
import java.util.*;

public class killerHeuristic implements ReversiAlgorithm
{
    // Constants
    private final static int DEPTH_LIMIT = 2; // Just an example value.
    // Variables
    boolean initialized;
    volatile boolean running; // Note: volatile for synchronization issues.
    GameController controller;
    GameState initialState;
    Move selectedMove;		
	int myIndex;
	int aiIndex;
	int turn = 1;

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
		aiIndex = myIndex;
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
            if (newMove != null) {
                selectedMove = newMove;
				break;
			}
        }
      
        if (running) // Make a move if there is still time left.
            controller.doMove(selectedMove);
    }
     
    Move searchToDepth(int depth)
    {
        Move parentMy;
		Move parentAi;
		Move childMy;
		Move childAi;
		Move optimalMove = null;
        
		Vector parentMoves;
		Vector childMoves;
		Vector<Node> storeParent = new Vector<Node>();
		Vector<Node> storeChild = new Vector<Node>();

		GameState nextState;
		GameState finalState;
		
		Node child;
		Node parent;
		Node first = new Node(initialState, null);
		
		storeChild.addElement(first);
		
		for (int z = 0; z < DEPTH_LIMIT; z++) {
			for (int i = 0; i < storeChild.size(); i++) {
				parent = storeChild.elementAt(i);
				initialState = parent.getState();
				parentMoves = initialState.getPossibleMoves(myIndex);
			
				for (int j = 0; j < storeChild.size(); j++) {
					parentMy = (Move)parentMoves.elementAt(j);
					nextState = initialState.getNewInstance(parentMy);
					parent = new Node(nextState, parentMy);
					
					storeParent.addElement(parent);
				}
			}
			storeChild.clear();

			for (int i = 0; i < storeParent.size(); i++) {
				parent = storeParent.elementAt(i);
				nextState = parent.getState();
				childMoves = nextState.getPossibleMoves(aiIndex);
				
				for (int j = 0; j < childMoves.size(); j++){
					childAi = (Move)childMoves.elementAt(j);
					finalState = nextState.getNewInstance(childAi);
					child = new Node(finalState, childAi);
					parent.addChild(child);
					storeChild.addElement(child);
				}
			}
			storeParent.clear();
		}
        return optimalMove;
    }
	
	double evaluate(Node node, int player)
    {
        int score;
        //checks which player's move it is
        GameState state = node.getState();
        Move move = node.getMove();
        int x = move.getX();
        int y = move.getY();
		int minimize;
        int maximize = state.getMarkCount(player);
        if (player == 1)
            minimize = state.getMarkCount(0);
        else
            minimize = state.getMarkCount(1);

        int [][] scores = new int[][] {
            {100, -20, 10,  5,  5, 10, -20, 100},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {10,   -2, -1, -1, -1, -1,  -2,  10},
            {5,    -2, -1, -1, -1, -1,  -2,   5},
            {5,    -2, -1, -1, -1, -1,  -2,   5},
            {10,   -2, -1, -1, -1, -1,  -2,  10},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {100, -20, 10,  5,  5, 10, -20, 100}};

        //positional player's endgame evaluation
        //maximizes scores on each move
        score = maximize-minimize;

        if (player == 1)
            score += scores[x][y];
        else
            //scores reduced if opponent gets the square
            score -= scores[x][y];
        
        return score;
    } 
}
 