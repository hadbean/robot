package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Shuffle {

    public int[][] shuffle() {

        int[][] cards = new int[3][15];
        List<Integer> group = new ArrayList<>();
        group.add(13);
        group.add(14);
        for (int i = 0; i < 13; i++) {
            group.add(i);
            group.add(i);
            group.add(i);
            group.add(i);
        }
        Collections.shuffle(group);
        for (int i = 0; i < 51; i++) {
            cards[0][group.get(i)] = cards[0][group.get(i)] + 1;
            cards[1][group.get(i + 1)] = cards[1][group.get(i + 1)] + 1;
            cards[2][group.get(i + 2)] = cards[2][group.get(i + 2)] + 1;
            i = i + 2;
        }
        int load = new Random().nextInt(3);
        cards[load][group.get(51)] = cards[load][group.get(51)] + 1;
        cards[load][group.get(52)] = cards[load][group.get(52)] + 1;
        cards[load][group.get(53)] = cards[load][group.get(53)] + 1;
        return cards;
    }

    /**
     * 生成一手卡牌，让第一个数组玩家一定赢，不管是当地主还是农民
     * @param whoToWin
     * @return
     */
    public int[][] shuffleToWin(int whoToWin){



        return null;
    }

    /**
     * 生成一手卡牌，让第一个数组玩家一定赢，不管是当地主还是农民 result[0]一定输
     * @param whoToLose
     * @return
     */
    public int[][] shuffleToLose(int whoToLose){



        return null;
    }


}
