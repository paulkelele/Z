package root;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws Exception {
         int ac = 12;
        int [][]   t =   {{12,45},{1,2,3,3}}  ;
        ArrayList<Integer> f = new ArrayList<>();
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                 f.add(t[i][j]);
            }
        }

    Agent a = new Agent("P1",0.3);
    HashMap<String,String> d = new HashMap<>();
    a.setStates_values( d);
    a.savePolicy();
    a.readPolicy();
        System.out.println(a.getStates_values());
    }


}
