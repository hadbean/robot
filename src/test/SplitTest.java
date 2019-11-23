package test;

import model.CardArray;
import model.OutCard;
import utils.CardSplit;

import java.util.HashMap;
import java.util.Map;

public class SplitTest {

    public static void main(String[] args) {

        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < OutCard.POKE.length; i++) {
            map.put(OutCard.POKE[i],i);
        }
        String s = "3,3,3,4,5,5,6,7,7,7,8,9,9,10,10,J,Q,2,2,JOKER2";
        int[] cards = new int[15];
        for (String s1 : s.split(",")) {
            cards[map.get(s1)] ++;
        }

        CardArray rs = new CardSplit().split(cards);
        System.out.println(rs);

    }
}
