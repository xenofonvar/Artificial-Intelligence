import java.util.ArrayList;
import java.util.Collections;
//State class implements Comparable to compare states.
public class State implements Comparable<State>{

    private ArrayList<Integer> Left_side;
    private ArrayList<Integer> Right_side;
    //Helps us not to lose the Right side.
    private ArrayList<Integer> Right_side_original = new ArrayList<Integer>();
    //Helps us not to lose the Left side.
    private ArrayList<Integer> Left_side_original = new ArrayList<Integer>();
    //Here we store every combination of each left and right side of each state. (ex. if state has: [1,2,3] || []. Compinations of left side is [1,2],[1,3],[2,3]. )
    private ArrayList<Pair> Combinations;
    private ArrayList<State> children = new ArrayList<State>();
    private int score;
    //tm is the total score that every state has. ( Cost from root and heuristic. )
    private int tm;
    private State father;
    private State tempo= null;
    private int torch;
    private int SumTime;
    private int Sumtemp;
    //Arraylist which helps to store total score of each compination and then it is easy select the one with the lowest score.
    private ArrayList<Integer> total_time;
    private int Croot;
    //As Left/Right_side_original helps us not to lose the cost from the root.
    private int Croot_temp;

    //Costructor for the init state which has only left side.
    public State(ArrayList<Integer> times, int SumTime) {
        Left_side = new ArrayList<Integer>(times);
        Right_side = new ArrayList<Integer>();
        total_time = new ArrayList<Integer>();
        father = null;
        torch = 0;
        //the cost is only the max of the left side, as the cost from the root is zero.
        tm = Find_Max(Left_side, false);
        Croot = 0;
        this.SumTime = SumTime;
    }

    //Costructor of states.
    public State(ArrayList<Integer> Left_side, ArrayList<Integer> Right_side , int torch, int time, State father, int root, int SumTime) {
        this.Left_side = Left_side;
        this.Right_side = Right_side;
        this.torch = torch;
        this.tm = time;
        this.father = father;
        this.Croot = root;
        this.SumTime = SumTime;
    }

    //Getters and setters
    public ArrayList<Integer> getLeft_side() {
        return Left_side;
    }

    public ArrayList<Integer> getRight_side() {
        return Right_side;
    }

    public int getSumTime() { return SumTime; }

    public void setTm(int Tm) { this.tm = Tm; }
    //Method to move two persons to the right. Essentially, a pair from left to right.
    public boolean move_right_2(Pair k) {
        //Checks if left side is empty.
        if (this.Left_side.size() == 0) {
            return false;
        }
        //Adds to the cost from the root the maximum value of the pair.
        Croot += k.getPMax();
        SumTime -= k.getPMax();

        int person1 = k.getP1();
        int person2 = k.getP2();
        //Remove persons from left and add them to the Right side.
        this.Left_side.remove((Integer) person1);
        this.Right_side.add(person1);

        this.Left_side.remove((Integer) person2);
        this.Right_side.add(person2);
        //Torch is now 1 , because is on the right side.
        torch = 1;
        return true;
    }
    //In the same way as moving to the right. This method move a person to the Left, because only one person is sent back with the torch.
    public boolean move_left_1(int k) {
        //Checks if the Right side is empty.
        if (this.Right_side.size() == 0) {
            return false;
        }
        //Adds to the cost from the root the time of this one person.
        Croot += k;
        SumTime -= k;
        //Remove from the right and add it to the left Arraylist.
        this.Right_side.remove((Integer) k);
        this.Left_side.add(k);
        //Torch is now 0, as the last move is from the right to left.
        torch = 0;
        return true;
    }

    //This method finds the combinations of left side of a state.
    public ArrayList<Pair> Find_Comb(ArrayList<Integer> k) {

        ArrayList<Pair> Combinations = new ArrayList<Pair>();
        for (int i = 0; i < k.size() - 1; i++) {
            for (int j = 0; j < k.size(); j++) {
                if (j > i) {

                    Pair pair = new Pair(k.get(i), k.get(j));
                    //for each pair add it to Combinations ArrayList.
                    Combinations.add(pair);
                }
            }
        }
        return Combinations;
    }
    //Finds the minimum of the called side. Each side has the times of the persons that it has.
    public int Find_Min(ArrayList<Integer> side) {
        return Collections.min(side);
    }
    //Similarly, this method finds the maximum. The only remarkable difference is the if state.
    public int Find_Max(ArrayList<Integer> side, boolean pl) {
        int max = Collections.max(side);
        //This if state checks for a extreme case, which is if the minimun of the right side is bigger than the maximum of the left side. If this is true, return the minimum of the right side as cost.
        if(pl && Find_Min(Right_side) > max) {return Find_Min(Right_side);}
        return max;
    }


    //get the children for the state called.
    public ArrayList<State> getChildren() {
        State current = this;
        //stores the current elements, so we will not lose anything.
        start();
        total_time = new ArrayList<Integer>();
        //checks if torch is left or right.
        if (torch == 0) {
            //Find combinations for the left side.
            Combinations = Find_Comb(this.Left_side);
            //Move each combo and calculate total time.
            for (int i = 0; i < Combinations.size(); i++) {
                move_right_2(Combinations.get(i));
                tempo = new State(this.Left_side, this.Right_side, torch, tm, father, Croot, SumTime);
                //Evaluate the total time.
                evaluate();
                tempo.setTm(total_time.get(i));
                //add this children to children arraylist.
                children.add(tempo);
                //reset the elements, thus we are again in the same state as we started.
                reset();
                //set the father of this children.
                children.get(children.size()-1).setFather(current);
            }
        }else {
            //Same as the previous loop. The only difference is that this loop refers to right side.
            for (int i = 0; i < Right_side.size(); i++) {
                move_left_1(Right_side.get(i));
                tempo = new State(this.Left_side, this.Right_side, torch, tm, father, Croot, SumTime);
                evaluate();
                tempo.setTm(total_time.get(i));
                children.add(tempo);
                reset();
                children.get(children.size()-1).setFather(current);
            }
        }
        return children;
    }
    //sum the cost from the root and the heuristic for each children.
    private void evaluate() {
        total_time.add(Croot + heuristic());
    }
    //heuristic finds a cost for all persons together cross the bridge from the current state. It depends where the torch is it.
    private int heuristic () {
        //if the torch is on the left side. Heuristic is just the maximum value of time from the left side.
        if (torch == 0) {
            score = Find_Max(Left_side, false);
        } else {
            //On the other hand, if torch is from the right side, heuristic is the sum of minimum value from the right side (to return the torch back) and maximum from left.
            if(Left_side.size() == 0){
                return 0;
            }
            score = Find_Min(Right_side) + Find_Max(Left_side, true);
        }
        return score;
    }

    //stores the current elements, so we will not lose anything.
    public void start() {
        Left_side_original = new ArrayList<>(Left_side);
        Right_side_original = new ArrayList<>(Right_side);
        Croot_temp = Croot;
        Sumtemp = SumTime;
    }
    //reset the elements, thus we are again in the same state as we started.
    public void reset() {
        Left_side = new ArrayList<>(Left_side_original);
        Right_side = new ArrayList<>(Right_side_original);
        Croot = Croot_temp ;
        SumTime = Sumtemp;
    }

    //help us to compare each state.
    public int compareTo(State s){ return Integer.compare(this.tm, s.tm); }

    public State getFather () { return father; }

    public void setFather (State f){ this.father = f; }
    //a state is terminal if the provided time is not enough or if all individuals have crossed the bridge.
    public int isTerminal() {
        if(SumTime<0){
            return -1;
        }
        if(this.getLeft_side().isEmpty()){
            return 0;
        }
        return 1;
    }
}

