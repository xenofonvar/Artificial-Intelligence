import java.util.ArrayList;
import java.util.Collections;
//Searcher class
public class Searcher{
    //Path stores the last state. Then, we will use it in CrossFrame class to find the path in print method.
    private static ArrayList<State> path = new ArrayList<State>();
    //Getter of path
    public static ArrayList<State> getPath(){ return path;}
    //Astar method.
    public static void Astar(State init) {
        //clear the path, so after reset button, path does not have any previous final state.
        path.clear();
        //Creates a new empty Arraylist.
        ArrayList<State> states = new ArrayList<State>();
        //add to the empty Arraylist the init state.
        states.add(init);
        //This loop removes the state which has the minimum cost and get his children.
        while(states.size() > 0)
        {
            //Remove the children which has the minimum cost. ( Cost is a sum of the cost from the root and the heuristic. )
            State currentState = states.remove(0);
            //Checks if current state is the state that we are looking for.
            if(currentState.isTerminal()==0)
            {
                //If is final state. Add it to path.
                path.add(currentState);
                break;
            }else if(currentState.isTerminal()<0){
                //breaks with a empty path.
                break;
            }
            //Call getChildren and add all children to states Array.
            states.addAll(currentState.getChildren());
            //Sort children
            Collections.sort(states);
        }
    }
}
