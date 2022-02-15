import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import static java.lang.Integer.parseInt;
//CrossFrame class. A simple GUI.
public class CrossFrame extends JFrame implements ActionListener {

    Container container = getContentPane();
    JLabel title = new JLabel("CROSS-BRIDGE GAME");
    JLabel userLabel = new JLabel("Number of person");
    JLabel NumperLabel = new JLabel("Person's times");
    JLabel ExLabel = new JLabel("(ex. 1,3,6,8,12)");
    JLabel providedtime = new JLabel("Time");
    JTextField userTextField = new JTextField();
    JTextArea result = new JTextArea();
    JTextField NumperField = new JTextField();
    JTextField providedfield = new JTextField();
    JButton RunButton = new JButton("RUN");
    JButton resetButton = new JButton("RESET");
    JScrollPane scr = new JScrollPane(result, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    ImageIcon img = new ImageIcon(getClass().getResource("img.png"));
    JLabel label = new JLabel(img);

    //Constructor
    CrossFrame() {
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();

    }
    //determines the size and position of the components within a container
    public void setLayoutManager() {
        container.setLayout(null);
    }
    //regulates the position and the size, but the final say is on the layout manager.
    public void setLocationAndSize() {
        result.setEditable(false);
        userLabel.setBounds(50, 150, 150, 30);
        label.setBounds(80, 50, 200, 80);
        title.setBounds(110, 10, 180, 30);
        NumperLabel.setBounds(50, 180, 150, 30);
        ExLabel.setBounds(50, 195, 150, 30);
        providedtime.setBounds(50, 230, 150, 30);
        userTextField.setBounds(180, 150, 150, 30);
        NumperField.setBounds(180, 190, 150, 30);
        scr.setBounds(30, 370, 300, 160);
        providedfield.setBounds(180, 230, 150, 30);
        RunButton.setBounds(50, 300, 100, 30);
        resetButton.setBounds(200, 300, 100, 30);
    }
    //It just adds the components to container.
    public void addComponentsToContainer() {
        container.add(userLabel);
        container.add(title);
        container.add(NumperLabel);
        container.add(ExLabel);
        container.add(providedtime);
        container.add(providedfield);
        container.add(userTextField);
        container.add(NumperField);
        container.add(RunButton);
        container.add(resetButton);
        container.add(scr);
        container.add(label);
    }
    //event handlers
    public void addActionEvent() {
        RunButton.addActionListener(this);
        resetButton.addActionListener(this);
    }


    @Override
    //define every action.
    public void actionPerformed(ActionEvent e) {
        //If Run button pushed.
        if (e.getSource() == RunButton) {
            String userText;
            String pwdText;
            String proText;
            //Gets the user input.
            userText = userTextField.getText();
            pwdText = NumperField.getText();
            proText = providedfield.getText();
            //user text is the Number of person. If user did not put anything.
            if (!userText.isEmpty()){
                //If number of person does not match with the person's times.
                if(pwdText.split(",").length==parseInt(userText)){
                    //if provided time is empty.
                    if(!proText.isEmpty()) {
                        //If RUN button pushed. User is unable to edit the input or push again run button.
                        RunButton.setEnabled(false);
                        providedfield.setEnabled(false);
                        NumperField.setEnabled(false);
                        userTextField.setEnabled(false);
                            //Persons deadline to cross the bridge.
                            int SumTime = parseInt(proText);
                            //It stores the person's times to a array of string.
                            String[] elements = pwdText.split(",");
                            ArrayList<Integer> times = new ArrayList<Integer>();
                            //add input times to the empty Arraylist. Eventually, we have stored input times to an Arraylist.
                            for (int i = 0; i < elements.length; i++) {
                                times.add(parseInt(elements[i]));
                            }
                            //Stores current time. It will help us to count how much it will take to find the optimal path.
                            long startTime = System.nanoTime();
                            //creates a initial state.
                            State grant = new State(times,SumTime);
                            //calls Astar.
                            Searcher.Astar(grant);
                            //Stores current time after Astar and substracts the current time with startTime. Hence, we have the total time.
                            long estimatedTime = System.nanoTime() - startTime;
                            //appends time converted to seconds.
                            result.append("-------------------------" + System.lineSeparator());
                            result.append("Astar running time: " + (double) estimatedTime / 1_000_000_000.0 + " sec" + System.lineSeparator());
                            //calls method to print the rest.
                            print(Searcher.getPath(), SumTime);
                    }else {
                        //Print error if provided time is empty.
                        JOptionPane.showMessageDialog(this, "Invalid time!");
                    }
                }else {
                    //Print error if person's times is empty or does not match with number of person.
                    JOptionPane.showMessageDialog(this, "Invalid person's times!");
                }
            }else {
                //Print error if number of person is empty.
                JOptionPane.showMessageDialog(this, "Invalid number of person!");
            }
        }
        //If reset button pushed, close the running window and rerun the program.
        if (e.getSource() == resetButton) {
            this.dispose();
            main(null);
        }

    }

    public static void main(String[] a) {
        CrossFrame frame = new CrossFrame();
        frame.setTitle("Cross-Bridge GUI");
        frame.setVisible(true);
        frame.setBounds(10, 10, 370, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //User is unable to maximize or change the size of window.
        frame.setResizable(false);
    }

    //Print function.
    public void print(ArrayList<State> Path, int rest) {
        if(Path.isEmpty()){
            result.append("There is no path for this example! Reset and try an other.\n");
            result.append("-------------------------");
            return;
        }else{
            //Subtract the hupothetical time that optimal path did from the provided time.
            rest = rest - Path.get(0).getSumTime();
            result.append("Hypothetical optimal path time: "+ rest + " sec.\n");
        }
        //A reverse Arraylist. Aids to reverse the path for the proper printing.
        ArrayList<ArrayList<Integer>> Rpath = new ArrayList<ArrayList<Integer>>();
        //here we will store the final state and use it to discover the entire path with method getFather().
        ArrayList<State> path = new ArrayList<State>(Path);
        //flag help us to print the initial state too.
        boolean flag = false;

        while(true) {
            //add the father of current state.
            path.add(path.get(0).getFather());
            //adds the Right side first and then the Left. Thus, when we will reverse it. It'll show the path correctly.
            Rpath.add(path.get(0).getRight_side());
            Rpath.add(path.get(0).getLeft_side());
            //remove the first element of the Array, so the array has only one element. The father of removed element.
            path.remove(0);
            //Break when we have added the initial state to Rpath.
            if(flag){ break; }
            //checks if the last father is the init state. If is, we want to do an other final loop to add the init state to Rpath, otherwise it will print the path without the init state.
            if (path.get(0).getRight_side().isEmpty()) { flag = true; }
        }
        //Reverse Rpath.
        Collections.reverse(Rpath);
        //Size of Rpath.
        int length = Rpath.size();
        //Because, Rpath counts Left and Right side separately. We divide the length by 2 and then subtract 1 because we do not want the states but the steps that have been done.
        length = (length/2)-1;
        //Just print Rpath and some other details.
        result.append("Optimal path took: " + length + " step(s).\n");
        result.append("-------------------------\n");
        for(int i=0; i<Rpath.size(); i+=2){
            result.append(String.valueOf(Rpath.get(i)));
            result.append("  ||  ");
            result.append(String.valueOf(Rpath.get(i+1)));
            result.append("\n");
        }

    }

}



