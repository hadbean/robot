package utils;

import model.CardArray;
import model.OutCard;

public class CardSet {

    private static final int[] CARDS = new int[]{4,4,4,4,4,4,4,4,4,4,4,4,4,1,1};
    /**
     * 该洗牌，int[0] 为必赢牌，int[1],int[2]为必输牌, int[3]为3张底牌
     * 能赢的
     * @param realPlayerNum
     * @return
     */
    public int[][] shuffle(int realPlayerNum) {

        if (realPlayerNum != 1 && realPlayerNum != 2) {
            return null;
        }
        /**
         *
         */
        int[] allCards = new int[15];
        System.arraycopy(CARDS,0,allCards,0,15);
        int[][] cards = new int[4][15];
//        cards[]


        return null;
    }





    public static void main(String[] args) {
//        CardSet set = new CardSet();
//        int[][] cards = set.shuffle();
//        split(cards);


    }

    public static void split(int[][] cards) {
        for (int i = 0; i < cards.length; i++) {
            StringBuffer bf = new StringBuffer();
            System.out.print(i + " : ");
            for (int j = 0; j < cards[i].length; j++) {
//                System.out.print(cards[i][j] + " ");
                for (int k = 0; k < cards[i][j]; k++) {
                    System.out.print(OutCard.POKE[j] + " ");
                }

            }
            System.out.println();
        }

        CardSplit split = new CardSplit();
        CardArray[] arrays = new CardArray[3];
        for (int i = 0; i < 3; i++) {
            arrays[i] = split.split(cards[i], 1, false, false);
        }

        for (int i = 0; i < arrays.length; i++) {
            System.out.println(i + ": " + arrays[i].toString());
        }

    }
}
