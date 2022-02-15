//Pair class
public class Pair {
    //Define two persons.
    private int p1;
    private int p2;
    //Constructs a couple.
    Pair(int p1,int p2){
        this.p1=p1;
        this.p2=p2;
    }
    //Getters
    public int getP1() {
        return p1;
    }
    public int getP2() {
        return p2;
    }
    //Finds the maximum of two.
    public int getPMax(){
        if(p1>p2){
            return p1;
        }else{
            return p2;
        }
    }
}
