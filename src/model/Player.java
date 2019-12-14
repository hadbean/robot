package model;

import config.Config;
import constant.CardType;
import strategy.OutCardStrategy;
import strategy.ReceiveStrategy;
import strategy.Strategy;
import utils.CardSplit;

import java.util.List;

public class Player {


    private String id;

    private int[] cards;
    // 0 地主，1 地主下 2 地主上
    private int role;
    private int type;
    private int minCardFriend;
    private int minCardEnemy;


    public Player(int[] cards, int role, int type) {
        this.cards = cards;
        this.role = role;
        this.type = type;
    }

    //主动出牌

    /**
     *
     * @param dipai  3张底牌
     * @return true 加倍
     */
    public boolean jiabei(int[] dipai){
        int score = callScore();
        if (score == 3){
            CardArray arr = new CardSplit().split(cards);
            if (arr.hands <= 3 && arr.maxCardNum() >=5){
                return true;
            }else {
                OutCardStrategy strategy = new OutCardStrategy(1);
                int[] remainingCards = strategy.remainingCardsExceptMe(cards, new int[15]);
                if (strategy.allBig2(role,cards,remainingCards,new int[]{20,17,17})!= null){
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * @param s 其他人叫的分
     * @return
     */
    public int jiaoDiZhu(int s) {

        if (s >= 3) {
            return 0;
        }
        OutCardStrategy strategy = new OutCardStrategy(0);

        int score = callScore();
        if (score > s) {
            return score;
        }

        int[] remainingCards = strategy.remainingCardsExceptMe(cards, new int[15]);

        for (int i = 0; i < 12; i++) {
            if (remainingCards[i] > 0) {
                Strategy.removeFrom(new int[]{i}, cards, false);
                score = callScore();
                if (score > s) {
                    if (Math.random() > Config.JDZ[remainingCards[i] - 1]) {
                        Strategy.removeFrom(new int[]{i}, cards, true);
                        return score;
                    }
                }
                Strategy.removeFrom(new int[]{i}, cards, true);
            }
        }

        return 0;
    }

    private int callScore() {

        CardSplit split = new CardSplit();
        split.setRound(30);
        CardArray arr = split.split(cards);

        int maxCard = arr.maxCardNum();
        if (cards[12] == 1) {
            maxCard += 1;
        }
        if (maxCard > 5) {
            return 3;
        }
        int score = arr.score();
        if (score  > 0) {
            return 3;
        }
        if (maxCard < 3) {
            if (arr.hands <= 4){

                return 1;
            }
            return 0;
        }

        if (arr.hands == 5) {
            return 1;
        } else if (arr.hands == 4) {
            return 2;
        } else if (arr.hands < 4) {
            return 3;
        }


        if (arr.hands - maxCard < 3) {
            return Math.max(0,3 - arr.hands + maxCard);
        }

        return 0;
    }

    /**
     * @param round            第几圈
     * @param remainingCardNum 每个玩家剩余手牌数量
     * @param alreadyOutCards  全局已经出的牌
     * @param outCard          出的牌，最近出的牌，不包含PASS
     * @return
     */
    public OutCard out(int round, int[] remainingCardNum, int[] alreadyOutCards, OutCard outCard) {

        CardArray rs = new CardSplit().split(cards);
        if (outCard != null && outCard.getRole() != role) {
            return receive(round, remainingCardNum, alreadyOutCards, outCard);
        }

        OutCardStrategy strategy = new OutCardStrategy(round);

        //判断是否一手牌出完
        OutCard out = OutCardStrategy.oneHand(rs, remainingCardNum[role]);
        int emeryCarNum = role == 0 ? Math.min(remainingCardNum[1], remainingCardNum[2]) : remainingCardNum[0];

        int[] remainingCards = strategy.remainingCardsExceptMe(cards, alreadyOutCards);

        if (out != null) {
            //在能一手出完的情况下，判断炸弹带牌是否要出
            if (out.getType() == CardType.ZHADANWITHTAIL) {
                if (strategy.canBiggerThanMe(out.getCards(), CardType.ZHADAN, remainingCards, emeryCarNum) == 1) {
                    return out;
                } else {
                    int l = out.getTail().length / 2;
                    if (role == 0) {
                        if (remainingCardNum[1] == l || remainingCardNum[0] == l) {
                            return out;
                        }
                    } else {
                        if (remainingCardNum[0] == l) {
                            return out;
                        }
                    }
                    return l == 1 ? OutCard.dan(out.getTail()[1]) : OutCard.duizi(out.getTail()[2]);
                }
            } else {
                return out;
            }
        }

        //判断是否只剩下一手小牌，那么先出大牌
        out = strategy.allBig2(role, cards, remainingCards, remainingCardNum);
        if (out != null) {
            out.setMode("allBig");
            return out;
        }
        if (emeryCarNum == 2) {
            int i = strategy.forceEnemySingle(rs, remainingCards, remainingCardNum, role);
            if (i == 1) {
                if (rs.nDan > 0) {
                    out = OutCard.dan(rs.dan[0]);
                } else if (rs.nDuizi > 0) {
                    out = OutCard.dan(rs.duizi[0]);
                }
            }
            if (out != null) {
                out.setMode("forceEnemySingle");
                return out;
            }
        }
        //判断是否让队友过牌,如果队友是我下家，且只剩一张牌，不用考虑自己牌型，直接让他过
        if (role == 1 && remainingCardNum[2] == 1) {
            out = strategy.letFriend(cards);
            if (out != null) {
                out.setMode("letFriend");
                return out;
            }
        }

        //如果队友只剩下两张牌，我优先出对子
        if ((role == 1 && remainingCardNum[2] == 2) || (role == 2 && remainingCardNum[1] == 2 && remainingCardNum[0] != 2)) {
            for (int i = 0; i < rs.nDuizi; i++) {
                if (rs.duizi[i] <= 7) {
                    return OutCard.duizi(rs.duizi[i]);
                }
            }
        }
        OutCard outSL = strategy.smallAndLongFirst(rs, role, remainingCards, remainingCardNum);
        if (outSL != null && outSL.getBp() < Config.SMALL_CARD_MAP.get(outSL.getType())) {
            outSL.setMode("smallAndLongFirst");
            return outSL;
        }

        if (emeryCarNum == 1) {
            out = strategy.enemyLastOne(rs, role, remainingCardNum);
            if (out != null) {
                out.setMode("enemyLastOne");
                return out;
            }
        }
        boolean fewHand = strategy.fewPoke(rs, remainingCardNum[role]);
        if (fewHand && outSL != null) {
            return outSL;
        }

        out = strategy.smallFirst(rs, role, remainingCards, remainingCardNum, fewHand);
        if (out == null && outSL == null) {
            throw new RuntimeException("没法主动出牌异常");
        }
        if (out == null) {
            return outSL;
        } else if (outSL == null) {
            return out;
        } else {
            return out.getBp() > outSL.getBp() ? outSL : out;
        }


    }


    public OutCard receive(int round, int[] remainingCardNum, int[] alreadyOutCards, OutCard outCard) {

        ReceiveStrategy strategy = new ReceiveStrategy(round);
        OutCard out = strategy.oneHand(cards, remainingCardNum[role], outCard);
        if (out != null) {
            out.setMode("oneHand");
            return out;
        }

        //找到所有适合的牌
        List<OutCard> outs = strategy.findBiggerCards(cards, remainingCardNum[role], outCard, round > 3, false);
        if (outs == null || outs.size() == 0) {
            return null;
        }

//        int emeryCardNum = role == 0 ? Math.min(remainingCardNum[1], remainingCardNum[2]) : remainingCardNum[0];
        if (role == 1 && outCard.getRole() == 2 && remainingCardNum[2] == 1){
            return strategy.letFriend(cards, outCard, Config.MIN_EXCEPT_1, outs);
        }
        int[] remainCards = strategy.remainingCardsExceptMe(cards, alreadyOutCards);

        out = strategy.zhaAndWin(role, cards, remainCards, remainingCardNum, outCard);
        if (out != null) {
            return out;
        }


        out = strategy.jieAndWin(role, cards, remainCards, outs, remainingCardNum);

        if (out != null) {
            out.setMode("jieAndWin");
            return out;
        }

        if (role == 2 && remainingCardNum[0] == 1 && outCard.getType() == CardType.DAN && outCard.getRole() == 1) {
            out = strategy.enemyLastOne(role, cards, outCard.getCards(), remainCards, remainingCardNum);
            if (out != null) {
                out.setMode("enemyLastOne");
                return out;
            }
        }
        //只剩两牌

        //队友只剩下一张牌，且在我下家
        // 则不用管自己的牌型，直接顶地主牌，如果是队友的牌，则手牌如果有小于9的牌，直接炸弹走起
        if (role == 1 && remainingCardNum[2] < 2) {
            out = strategy.letFriend(cards, outCard, Config.MIN_EXCEPT_1, outs);
            if (out != null) {
                out.setMode("letFriend");
                return out;
            }
        }
        return strategy.normal(cards, role, outCard, remainCards, remainingCardNum, outs);
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int[] getCards() {
        return cards;
    }

    public void setCards(int[] cards) {
        this.cards = cards;
    }
}
