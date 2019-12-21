package test;

import utils.CardSet;

public class Test1 {

    public static void main(String[] args) {

        String paizu = "0 : 1 0 1 2 2 2 1 1 2 3 2 1 2 0 0 \n" +
                "1 : 0 2 1 2 2 2 1 1 2 1 0 1 0 1 1 \n" +
                "2 : 3 2 2 0 0 0 2 2 0 0 2 2 2 0 0";

        String[] fenpai = paizu.replaceAll(" ","").split("\n");
        int[][] cardsGroup = new int[3][15];
        for (int i = 0; i < fenpai.length; i++) {
            String[] s = fenpai[i].split(":");
            int n = Integer.valueOf(s[0].trim());
            String[] cards = s[1].split("");
            for (int j = 0; j < cards.length; j++) {
                cardsGroup[n][j] = Integer.valueOf(cards[j].trim());
            }
        }
        CardSet.split(cardsGroup);
    }
}
