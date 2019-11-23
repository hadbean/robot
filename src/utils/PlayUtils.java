package utils;

import constant.CardType;
import model.CardArray;

public class PlayUtils {

    /**
     *
     * @param cards 手牌
     * @param remainCardNum 剩余手牌数量 0 地主手牌数量 1 地主 下家手牌数量 2 地主上家手牌数量
     * @param posion 当前角色位置 0 地主 1 地主下家 2 地主上家
     * @param alearyOutCards 已经出的牌
     * @param extraCards 额外牌
     * @param outCards 出牌
     * @param cardType 牌类型
     * @param receiving 是否接牌
     */
    public static String outHand(int[] cards,int[] remainCardNum,int posion, int[] alearyOutCards, int[] extraCards, String[] outCards, CardType cardType, boolean receiving){


        if (!receiving){
            CardSplit split = new CardSplit();
            switch (posion){
                case 0: {
                    CardArray rs = split.split(cards);

                    break;
                }
                case 1: break;
                case 2: break;
                default: break;
            }

        }else {
            return null;
        }

        return null;

    }
}
