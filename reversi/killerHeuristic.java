import reversi.*;
import java.util.*;

public class killerHeuristic implements ReversiAlgorithm
{
	// Constants
	public final static int DEPTH_LIMIT = 1; // Just an example value.
	// Variables
	boolean initialized;
	volatile boolean running; // Note: volatile for synchronization issues.
	GameController controller;
	GameState initialState;
	public Move selectedMove;
	int myIndex;
	int aiIndex;

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
		if (myIndex == 1)
			aiIndex = 0;
		else
			aiIndex = 1;
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
		searchToDepth();
	}

	void searchToDepth()
	{
		Move parentMove;
		Move childMove;

		Vector parentMoves;
		Vector childMoves;
		Vector<Node> storeParent = new Vector<Node>();
		Vector<Node> storeChild = new Vector<Node>();
		Vector<Node> storeRoot = new Vector<Node>();

		GameState nextState;
		GameState finalState;

		Node child;
		Node parent = new Node(initialState, null);
		Node best = null;
		Node maxChild = new Node();

		//boolean selected = false;
		double minParent = 50;
		double maxScore;
		//int minChilds;
		int childScore;
		int depth = 1;

		storeChild.addElement(parent);

		do {
			for (Node parentMy : storeChild) {
				nextState = parentMy.getState();
				parentMoves = nextState.getPossibleMoves(myIndex);

				for (int j = 0; j < parentMoves.size(); j++) {
					parentMove = (Move)parentMoves.elementAt(j);
					parent = new Node(nextState.getNewInstance(parentMove), parentMove);
					parentMy.addChild(parent);
					storeParent.addElement(parent);

					if (depth == 1)
						storeRoot.addElement(parent);
				}
			}
			storeChild.clear();
			//minChilds = 1;

			for (Node parentAi : storeParent) {
				maxChild.setScore(0);
				maxScore = 0;

				nextState = parentAi.getState();
				childMoves = nextState.getPossibleMoves(aiIndex);

				for (int j = 0; j < childMoves.size(); j++){
					childMove = (Move)childMoves.elementAt(j);
					finalState = nextState.getNewInstance(childMove);
					child = new Node(finalState, childMove);
					childScore = finalState.getMarkCount(aiIndex);

					child.setScore(childScore);
					parentAi.addChild(child);
					storeChild.addElement(child);

					if (childScore > maxScore) {
						maxChild = child;
						maxScore = childScore;
					}
				}
				if (maxScore < minParent) {
					//minChilds = 1;
					best = maxChild;
					minParent = maxScore;
				}
				//else if (maxScore == minParent)
				//	minChilds++;
			}
			storeParent.clear();
			
			for (Node root : storeRoot) {
				if (best.isParent(root)) {
					selectedMove = root.getMove();
					break;
				}
			}
		} while (depth++ < DEPTH_LIMIT); //&& !selected);
	}
}
