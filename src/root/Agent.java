package root;

import java.io.*;
import java.util.HashMap;

public class Agent   {
    String name;
    private double exp_rate;
    private double lr;
    private double decay_gamma;

    private HashMap<String,String> states_values;

    public Agent() {
    }
    public Agent(String name, double exp_rate){
        this.name = name;
        this.exp_rate = exp_rate;
        this.lr = 0.2;
        this.decay_gamma = 0.9;
        this.states_values = new HashMap<>();
    }

    public HashMap<String, String> getStates_values() {
        return states_values;
    }

    public void setStates_values(HashMap<String, String> states_values) {
        this.states_values = states_values;
    }

    public void savePolicy() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("dd.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fileOutputStream);
        oos.writeObject(getStates_values());
    }

    public void readPolicy() throws IOException, ClassNotFoundException {
        try(FileInputStream fileInputStream = new FileInputStream("dd.ser");
        ObjectInputStream oos = new ObjectInputStream(fileInputStream);){
         setStates_values((HashMap<String, String>) oos.readObject());
        }

    }

}
