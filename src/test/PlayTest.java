package test;

import model.OutCard;
import model.Player;
import strategy.Strategy;
import utils.Room;

import java.util.HashMap;
import java.util.Map;

public class PlayTest {
    static Map<String,Integer> map = new HashMap<>();
    static {
        for (int i = 0; i < OutCard.POKE.length; i++) {
            map.put(OutCard.POKE[i], i);
        }
    }
    public static void main(String[] args) {

        /*
        地主:3,3,3,4,4,5,5,6,6,7,7,7,9,10,J,Q,Q,K,2,2,	PASS
主下:3,4,4,5,8,8,9,10,10,J,J,Q,A,A,A,2,JOKER1,	PASS
主上:5,6,6,7,8,8,9,9,10,J,Q,K,K,K,A,2,JOKER2,	PASS
         */
        String s0 = "3,3,3,4,6,6,7,7,8,8,9,10,10,10,J,Q,Q,A,A,2";
        String s1 = "3,5,5,6,7,9,9,J,J,K,K,K,2,2,2,JOKER1,JOKER2";
        String s2 = "4,4,4,5,5,6,7,8,8,9,10,J,Q,Q,K,A,A";

        String s = "地主:4,4,5,5,6,6,7,7,8,8,9,9,10,10,J,J,Q,Q,K,K,\tPASS\n" +
                "主下:3,3,4,5,6,7,8,9,10,10,J,J,K,A,2,2,JOKER2,\tPASS\n" +
                "主上:3,3,4,5,6,7,8,9,Q,Q,K,A,A,A,2,2,JOKER1,\tPASS";
        String[] ss = s.split("\n");
        if (ss.length == 3) {
            for (int i = 0; i < s.split("\n").length; i++) {
                String t = ss[i];
                t = t.substring(t.indexOf(":") + 1, t.indexOf(",\t"));
                ss[i] = t;
            }
            s0 = ss[0];
            s1 = ss[1];
            s2 = ss[2];
        }

        
        int[] c0 = strToCard(s0);
        int[] c1 = strToCard(s1);
        int[] c2 = strToCard(s2);

        Player p = new Player(c0,0,1);
        Player p1 = new Player(c1,1,1);
        Player p2 = new Player(c2,2,1);
        
        
        
        int[] remainingCardNum = remainingCardNum(c0,c1,c2);
        int[] alreadyOutCard = alearyCards(c0,c1,c2);

        Room r = new Room();
        r.init(alreadyOutCard,remainingCardNum,p,p1,p2);
        r.play(false);
//        OutCard out =


    }

    static int[] CARDS = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1};
    static int[] alearyCards(int[] c0,int[] c1,int[] c2){
        int[] cards = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1};
        for (int i = 0; i < CARDS.length; i++) {
            cards[i] -= (c1[i] + c2[i] + c0[i]);
        }
        return cards;
    }

    static int[] remainingCardNum(int[] c0,int[] c1,int[] c2){
        int[] cards = new int[]{0,0,0};
        for (int i : c0) {
            cards[0] +=i;
        }
        for (int i : c1) {
            cards[1] +=i;
        }
        for (int i : c2) {
            cards[2] +=i;
        }
        return cards;
    }

    static int[] strToCard(String ss){

        int[] cards = new int[15];
        for (String s : ss.split(",")) {
            cards[map.get(s)] ++;
        }
        return cards;
    }
}
