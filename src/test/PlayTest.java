package test;

import model.OutCard;
import model.Player;
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

//        String pref = "0:JOKER1,2,2,A,K,Q,J,10,10,10,9,8,8,7,6,6,5,4,3,3,\tPASS\n" +
//                "1:JOKER2,A,A,K,Q,J,10,8,7,7,6,5,5,5,4,4,3,\tPASS\n" +
//                "2:2,2,2,2,A,K,K,Q,Q,9,9,9,8,7,6,4,3,\tPASS";


        /*
        地主:3,3,3,4,4,5,5,6,6,7,7,7,9,10,J,Q,Q,K,2,2,	PASS
主下:3,4,4,5,8,8,9,10,10,J,J,Q,A,A,A,2,JOKER1,	PASS
主上:5,6,6,7,8,8,9,9,10,J,Q,K,K,K,A,2,JOKER2,	PASS
         */
        String s0 = "3,3,3,4,6,6,7,7,8,8,9,10,10,10,J,Q,Q,A,A,2";
        String s1 = "3,5,5,6,7,9,9,J,J,K,K,K,2,2,2,JOKER1,JOKER2";
        String s2 = "4,4,4,5,5,6,7,8,8,9,10,J,Q,Q,K,A,A";

//        String s = "地主:6,7,JOKER1,\tDUI:4,4 \n" +
//                "主下:Q,2,2,\tDUI:9,9 \t实际出牌：9,9\n" +
//                "主上:9,9,2,\tDUI:A,A \t实际出牌：A,A";

        String s = "地主:4,4,5,6,6,7,7,8,8,8,9,9,10,K,K,A,2,2,JOKER1,JOKER2,\tDUI:4,4 \n" +
                "主下:3,3,3,4,4,6,8,9,9,10,10,J,J,J,J,K,K,\tDUI:9,9 \t实际出牌：9,9\n" +
                "主上:3,5,5,5,6,7,7,10,Q,Q,Q,Q,A,A,A,2,2,\tDUI:A,A \t实际出牌：A,A";
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
