package strategy;

import config.Config;
import constant.CardType;
import model.CardArray;
import model.OutCard;

import java.util.List;

public class ReceiveStrategy implements Strategy {

    //是否可以一手出完
    public int round;


    public ReceiveStrategy(int round) {
        this.round = round;
        split.setRound(round);
    }

    /**
     * @param cards
     * @param remainCardNum
     * @param outCard       出牌， 牌型为 int[2]{};为连的牌型，int[0]为牌型最大值，int[1]为长度，88899967 {6,8},456789 {6,5}, 66 {3,2}
     * @return
     */
    public OutCard oneHand(int[] cards, int remainCardNum, OutCard outCard) {

        int outCardLength = outCard.getLength();
        CardType type = outCard.getType();

        if (remainCardNum == 2 && cards[13] == 1 && cards[14] == 1) {
            return OutCard.huojian();
        }
        if (remainCardNum == 4) {
            for (int i = 0; i < 13; i++) {
                if (cards[i] == 4) {
                    if (type == CardType.ZHADAN && i > outCard.getCards()[0]) {
                        return null;
                    }
                    return OutCard.zhadan(i);
                }
            }
        }
        if (remainCardNum != outCardLength) {
            return null;
        }

        CardArray rs = split.split(cards);
        switch (type) {
            case HUOJIAN: {
                return null;
            }
            case ZHADAN:
            case ZHADANWITHTAIL: {
                if (rs.nZhadan > 0 && rs.zhadan[0] > outCard.getCards()[0]) {
                    if (outCardLength == 8) {
                        if (rs.nDuizi == 2 || (rs.nDuizi == 1 && rs.nEr == 2)) {
                            if (rs.nEr == 2) {
                                return OutCard.zhadanWithTail(rs.zhadan[0], new int[]{rs.duizi[0], rs.duizi[0], 12, 12});
                            } else {
                                return OutCard.zhadanWithTail(rs.zhadan[0], new int[]{rs.duizi[0], rs.duizi[0], rs.duizi[1], rs.duizi[1]});

                            }
                        }
                        return null;
                    } else if (outCardLength == 6) {
                        if (rs.nDuizi == 1 || rs.nEr == 2) {
                            if (rs.nEr == 2) {
                                return OutCard.zhadanWithTail(rs.zhadan[0], new int[]{12, 12});
                            } else {
                                return OutCard.zhadanWithTail(rs.zhadan[0], new int[]{rs.duizi[0], rs.duizi[0]});
                            }
                        } else {
                            return OutCard.zhadanWithTail(rs.zhadan[0], new int[]{rs.dan[0], rs.dan[1]});
                        }
                    }
                }
                return null;
            }
            case FEIJIWITHTAIL:
            case FEIJI: {
                if (rs.nFeiji > 0 && rs.feiji[0][0] > outCard.getCards()[0] && outCard.getCards()[1] == 2) {
                    if (outCardLength == 10) {
                        if (rs.nDuizi == 2 || (rs.nDuizi == 1 && rs.nEr == 2)) {
                            if (rs.nEr == 2) {
                                return OutCard.feijiWithTail(rs.feiji[0], new int[]{rs.duizi[0], rs.duizi[0], 12, 12});
                            } else {
                                return OutCard.feijiWithTail(rs.feiji[0], new int[]{rs.duizi[0], rs.duizi[0], rs.duizi[1], rs.duizi[1]});

                            }
                        }
                        return null;
                    } else if (outCardLength == 8) {
                        if (rs.nDuizi == 1 || rs.nEr == 2) {
                            if (rs.nEr == 2) {
                                return OutCard.feijiWithTail(rs.feiji[0], new int[]{12, 12});
                            } else {
                                return OutCard.feijiWithTail(rs.feiji[0], new int[]{rs.duizi[0], rs.duizi[0]});
                            }
                        } else {
                            return OutCard.feijiWithTail(rs.feiji[0], new int[]{rs.dan[0], rs.dan[1]});
                        }
                    } else {
                        return OutCard.feiji(rs.feiji[0]);

                    }
                }
                return null;
            }
            case SANTIAOWITHTAIL:
            case SANTIAO: {
                if (rs.nSantiao > 0 && rs.santiao[0] > outCard.getCards()[0]) {
                    if (outCardLength == 5) {
                        if (rs.nDuizi == 1 || rs.nEr == 2) {
                            if (rs.nEr == 2) {
                                return OutCard.santiaoWithTail(rs.santiao[0], new int[]{12, 12});
                            } else {
                                return OutCard.santiaoWithTail(rs.santiao[0], new int[]{rs.duizi[0], rs.duizi[0]});

                            }
                        }
                        return null;
                    } else if (outCardLength == 4) {

                        return OutCard.santiaoWithTail(rs.santiao[0], new int[]{rs.dan[0]});

                    } else {
                        return OutCard.santiao(rs.santiao[0]);
                    }
                }
                return null;
            }
            case SHUNZI: {
                if (rs.nShunzi > 0 && rs.shunzi[0][0] > outCard.getCards()[0] && rs.shunzi[0][1] == outCard.getCards()[1]) {
                    return OutCard.shunzi(rs.shunzi[0]);
                }
                return null;
            }
            case LIANDUI: {
                if (rs.nLiandui > 0 && rs.liandui[0][0] > outCard.getCards()[0]) {
                    return OutCard.liandui(rs.liandui[0]);
                }
                return null;
            }
            case DAN: {
                if (rs.dan[0] > outCard.getCards()[0]) {
                    return OutCard.dan(rs.dan[0]);
                }
                return null;
            }
            case DUI: {
                if (rs.nDuizi == 1 && rs.duizi[0] > outCard.getCards()[0]) {
                    return OutCard.duizi(rs.duizi[0]);
                } else if (rs.nEr == 2 && outCard.getCards()[0] < 12) {
                    return OutCard.duizi(12);
                }
                return null;
            }
            default:
                throw new RuntimeException("未识别类型:" + outCard);

        }
    }

    //接完该牌后，能赢
    public OutCard jieAndWin(int role, int[] cards, int[] remainCards, List<OutCard> outCards, int[] remainingCardNum) {

        int ememyNum = role == 0 ? Math.min(remainingCardNum[1], remainingCardNum[2]) : remainingCardNum[0];
        OutCard best = null;
        for (OutCard x : outCards) {
            Strategy.removeCard(cards, EMPTY_CARDS, x, true);
            double bp = biggestProbability(role, remainCards, remainingCardNum, x);
            if (bp > Config.SMALL_CARD) {

                OutCard out = allBig2(role, cards, remainCards, remainingCardNum);
                if (out != null) {
                    CardArray arr = split.split(cards);
                    x.setScore(arr.score());
                    x.setHands(arr.hands);
                    if (best == null){
                        best = x;
                    }else {
                        if (x.getScore() >= best.getScore() && x.getHands() < best.getHands()){
                            best = x;
                        } else if (x.getType() == CardType.HUOJIAN && best.getCards()[0] > 12){
                            best = x;
                        }
                    }
                }else {
                    out = OutCardStrategy.oneHand(split.split(cards),remainingCardNum[role] - x.getLength());
                    if (out != null){
                        Strategy.removeCard(cards, EMPTY_CARDS, x, false);
                        return x;
                    }
                }
            } else if (ememyNum > 1 || x.getType() != CardType.DAN) {
                CardArray arr = split.split(cards);
                OutCard o = findBiggestCardFromMe(role, arr, remainCards, remainingCardNum, x,true);
                if (o != null) {
                    Strategy.removeCard(cards, EMPTY_CARDS, o, true);
                    OutCard out = allBig2(role, cards, remainCards, remainingCardNum);
                    if (out != null) {
                        x.setScore(arr.score());
                        x.setHands(arr.hands);
                        if (best == null){
                            best = x;
                        }else {
                            if (x.getScore() >= best.getScore() && x.getHands() < best.getHands()){
                                best = x;
                            }
                        }
                    }else {
                        out = OutCardStrategy.oneHand(split.split(cards),remainingCardNum[role] - x.getLength()  - o.getLength());
                        if (out != null){
                            Strategy.removeCard(cards, EMPTY_CARDS, x, false);
                            Strategy.removeCard(cards, EMPTY_CARDS, o, false);
                            return x;
                        }
                    }
                    Strategy.removeCard(cards, EMPTY_CARDS, o, false);
                }
            }

            Strategy.removeCard(cards, EMPTY_CARDS, x, false);
        }
        return best;
    }


    /**
     * * 我在地主上家，地主只剩下一张牌，队友出单，我尽量大牌接
     *
     * @param cards
     * @param outCard
     * @param remainCards
     * @return
     */

    public OutCard enemyLastOne(int role, int[] cards, OutCard outCard, int[] remainCards, int[] remainingCardNum, List<OutCard> outs) {

        if (role == 2) {
            if (outCard.getRole() == 0 || biggestProbability(2, remainCards, remainingCardNum, outCard) < Config.SMALL_CARD_MAP.get(CardType.DAN)) {

                CardArray rs = split.split(cards);
                if (rs.nDan > 0) {
                    for (int j = 0; j < rs.nDan; j++) {
                        if (rs.dan[j] > outCard.getCards()[0] && canBiggerThanMe(new int[]{rs.dan[j]}, CardType.DAN, remainCards, 1) == 0) {
                            return OutCard.dan(rs.dan[j]);
                        }
                    }
                }
                if (rs.nEr > 1) {
                    if (canBiggerThanMe(new int[]{12}, CardType.DAN, remainCards, 1) == 0) {
                        return OutCard.dan(12);
                    }
                }
                if (rs.nZhadan > 0 || rs.nHuojian > 0) {
                    OutCard tmp = allBig2(role, cards, remainCards, remainingCardNum);
                    if (tmp != null) {
                        return rs.nZhadan > 0 ? OutCard.zhadan(rs.zhadan[0]) : OutCard.huojian();
                    }
                }
                if (rs.nDan > 0 && rs.dan[rs.nDan - 1] > outCard.getCards()[0]) {
                    if (rs.dan[rs.nDan - 1] < 11) {
                        if (rs.nDuizi > 0 && rs.duizi[rs.nDuizi - 1] == 11) {
                            return OutCard.dan(11);
                        }
                    }
                    return OutCard.dan(rs.dan[rs.nDan - 1]);
                }
            }
        } else if (role == 1) {

        } else if (role == 0) {
            for (int i = outs.size() - 1; i >= 0; i--) {
                OutCard o = outs.get(i);
                if (o.getType() == CardType.DAN) {
                    return o;
                }
            }
        }
        return null;
    }

    /**
     * 队友在我下手，且只有一张牌，我接牌
     *
     * @param cards
     * @return
     */
    public OutCard letFriend(int[] cards, OutCard outCard, int minAccept, List<OutCard> biggerCards) {
        //如果是队友的牌，我如果有炸弹或者飞机，且手牌有小于10的，则强势加分

        OutCard biggest = Strategy.findZhanDanOrHuoJian(cards, outCard);
        if (biggest != null) {
            Strategy.removeCard(cards, EMPTY_CARDS, biggest, true);
            for (int i = 0; i < minAccept; i++) {
                if (cards[i] > 0) {
                    Strategy.removeCard(cards, EMPTY_CARDS, biggest, false);
                    return biggest;
                }
            }
            Strategy.removeCard(cards, EMPTY_CARDS, biggest, false);
        }
        if (outCard.getRole() == 2) {
            return null;
        } else {
            return biggerCards.get(biggerCards.size() - 1);

        }
    }


    //兜底牌
    public OutCard normal(int[] cards, int role, OutCard outcard, int[] remainingCards, int[] remainCardNums, List<OutCard> biggerCards) {

        CardArray arr = split.split(cards);
        OutCard best = findBestOutCard(role,cards,arr,outcard,remainCardNums, biggerCards, role == 2);
        if (best == null){
            return null;
        }
        //农民接牌
        if (role != 0) {
            int s = arr.score();
            if (outcard.getRole() == 0){
                if (best.getScore() > s) {
                    return best;
                }
            }

            if (outcard.getRole() == 2) {
                if (best.getScore() < s) {
                    return null;
                }
            }

            if (remainCardNums[0] > 7) {
                if (s - best.getScore() > Config.MAX_ACCEPT_LOSS) {
                    return null;
                }
            }

            if (s > Config.GOOD_CARDS_VALUE) {
                return best;
            }

            if (outcard.getRole() != 0) {
                if (remainCardNums[outcard.getRole()] <=3){
                    return null;
                }
                double b = biggestProbability(role, remainingCards, remainCardNums, best, false);
                if (outcard.getType() == CardType.DAN || outcard.getType() == CardType.DUI) {
                    if (best.getCards()[0] > 11) {
                        return null;
                    }
                }
                if (b > Config.SMALL_CARD_MAP.get(best.getType())) {
                    return null;
                }
                double p = Config.IF1[outcard.getRole()];
                double bp = biggestProbability(role, remainingCards, remainCardNums, outcard, false);
                if (outcard.getRole() == 2) {
                    if (bp <= p) {
                        return best;
                    } else {
                        return null;
                    }
                } else if (outcard.getRole() == 1) {
                    if (bp >= p) {
                        return null;
                    } else {
                        return best;
                    }
                }
            } else {

                if (best.getType() == CardType.DAN && best.getCards()[0] == 14) {
                    if (remainingCards[13] == 1 && remainCardNums[0] > 9) {
                        return null;
                    }
                }

                if (best.getType() == outcard.getType() && best.getType() != CardType.ZHADAN && best.getType() != CardType.HUOJIAN && (best.getCards()[0] < 12 || (best.getCards()[0] - outcard.getCards()[0] < Config.MAX_CARDS_SPAN))) {
                    return best;
                }
                if (best.getType() == CardType.SHUNZI || best.getType() == CardType.LIANDUI || best.getType() == CardType.FEIJI || best.getType() == CardType.FEIJIWITHTAIL) {
                    return best;
                }

                if (best.getType() == CardType.ZHADAN || best.getType() == CardType.HUOJIAN) {
                    if (outcard.getCards()[0] < 12 || remainCardNums[0] > 2) {
                        return null;
                    }
                }

                double bp = biggestProbability(role, remainingCards, remainCardNums, outcard);
                if (remainCardNums[0] < 3 || bp > Config.SMALL_CARD_MAP.get(outcard.getType())) {
                    return best;
                }

                //如果地址出大牌了，手牌很少，则接
//                double bpSelf = biggestProbability(role, remainingCards, remainCardNums, best);
//                if (bpSelf - bp < Config.)

                if (remainCardNums[0] < 3 || bp == 1) {
                    System.out.println("瞎出牌");
                    return best;
                }
                return null;
            }
        } else {
            //地主接牌
            int s = arr.score();
            int bs = best.getScore();

            if (bs >= s) {
                return best;
            }
            if (bs > Config.GOOD_CARDS_VALUE) {
                return best;
            }
            //如果手数变差非常多，则不接
            if (remainCardNums[outcard.getRole()] > 1){
                if (s -  bs >  12){
                    return null;
                }
            }

            //地主接牌规则,接地主下级时，尽可能接牌，因为该位置不会故意压牌，大部分情况下，不会由他来压牌，更多是为了跑牌
            if (outcard.getRole() == 1) {
                if (best.getType() == outcard.getType() && best.getType() != CardType.ZHADAN && best.getType() != CardType.HUOJIAN && (best.getCards()[0] < 12 || (best.getCards()[0] - outcard.getCards()[0] < Config.MAX_CARDS_SPAN))) {
                    return best;
                }
            } else {
                if (best.getType() == outcard.getType() && best.getType() != CardType.ZHADAN && best.getType() != CardType.HUOJIAN && (best.getCards()[0] < 12)) {
                    return best;
                } else if ((remainingCards[13] == 0 && remainingCards[14] == 0) || cards[13] == 1  || cards[14] == 1) {
                    if (best.getType() == CardType.DAN) {
                        return best;
                    } else if (best.getType() == CardType.DUI && arr.nDan > cards[best.getCards()[0]]) {
                        return null;
                    }
                }
            }

        }
//        if (Strategy.randomFloat() < Config.FOOL_ACTION) {
//            return best;
//        }

        return null;
    }

    private OutCard shouldSplit(int role, int[] cards, CardArray origin, OutCard out, OutCard o, int[] remainCardNum) {

        int c = o.getCards()[0];
        switch (o.getType()) {
            case DAN: {
                if (o.getCards()[0] < 12) {
                    if (cards[c] == 3) {
                        return null;
                    } else if (cards[c] == 2) {
                        if (o.getHands() >= origin.hands) {
                            return null;
                        }
                    } else if (cards[c] == 1) {
                        if (role != 0 && out.getRole() != 0) {
                            if (o.getHands() >= origin.hands) {
                                return null;
                            }
                        } else if (role == 0) {
                            if (remainCardNum[1] > 7 || remainCardNum[2] > 7) {
                                return null;
                            }
                        }
                    }
                } else if (o.getCards()[0] > 12) {
                    if (role == 0) {
                        if (origin.nHuojian > 0) {
                            if (origin.nDan > 4 && (remainCardNum[1] < 7 || remainCardNum[2] < 7)) {
                                return o;
                            }else {
                                return null;
                            }
                        }
                    } else {
                        if (origin.nHuojian > 0){
                            if (remainCardNum[0] > 7 || origin.hands < 4 || remainCardNum[3 - role] < 7){
                                return null;
                            }
                        }
                    }
                }else {

                }
                break;
            }
            case DUI: {
                //拆3条
                if (cards[o.getCards()[0]] == 1) {
                    if (o.getCards()[0] < 12) {
                        if (role != 0 && out.getRole() != 0) {
                            return null;
                        }else if (role == 0){
                            if (o.getHands() < origin.hands){
                                return o;
                            }
                            if (remainCardNum[out.getRole()] > 7 && remainCardNum[3 - out.getRole()] > 1){
                                return null;
                            }
                        }else if (role == 2){

                        }
                        if (o.getHands() > out.getHands()) {
                            return null;
                        }
                        //是否接对2
                    }
                } else if (cards[o.getCards()[0]] == 2) {
                    if (o.getHands() >= out.getHands()) {
                        return null;
                    }
                }
                break;
            }
            case SANTIAO:
            case SANTIAOWITHTAIL: {
                if (cards[o.getCards()[0]] == 1) {
                    if (o.getHands() >= origin.hands) {
                        return null;
                    }
                }
            }

            default:
                break;
        }
        return o;
    }


    private OutCard findBestOutCard(int role,int[] cards,CardArray origin,OutCard outCard,int[] remainCardNum, List<OutCard> outs, boolean biggerFirst) {

        OutCard out = null;
        origin.score();
        int score = Integer.MIN_VALUE;
        int hands = origin.hands;
        for (OutCard o : outs) {
            Strategy.removeCard(cards, EMPTY_CARDS, o, true);
            CardArray rs = split.split(cards);
            int s = rs.score();
            int h = rs.hands;
            o.setHands(h);
            o.setScore(s);
            if ((s > score && shouldSplit(role,cards,origin,outCard,o,remainCardNum) != null) || hands - h > 1) {
                out = o;
                score = s;
                hands = h;
                out.setScore(score);
                out.setHands(hands);
            } else if (biggerFirst) {
                if (o.getType() == CardType.DAN) {
                    if (out != null && out.getType() == CardType.DAN && out.getCards()[0] < 8 && o.getCards()[0] > 7) {
                        if (out.getScore() - s == o.getCards()[0] - out.getCards()[0]) {
                            out = o;
                            out.setScore(s);
                            out.setHands(hands);
                        }
                    }
                } else if (o.getType() == CardType.DUI) {
                    if (out != null && out.getType() == CardType.DUI && out.getCards()[0] < 4 && o.getCards()[0] > 3) {
                        int span = o.getCards()[0] - out.getCards()[0];
                        if (o.getCards()[0] > Config.REF) {
                            span = (o.getCards()[0] - Config.REF) * 3 / 2 - out.getCards()[0];
                        }
                        if (out.getScore() - s == span) {
                            out = o;
                            out.setScore(s);
                            out.setHands(hands);
                        }
                    }
                }
            }
            Strategy.removeCard(cards, EMPTY_CARDS, o, false);
        }

        return out;

    }


    public static void main(String[] args) {

        long begin = System.currentTimeMillis();
        ReceiveStrategy strategy = new ReceiveStrategy(0);
        int[] cards = {2, 2, 1, 1, 1, 1, 2, 3, 3, 1, 3, 3, 0, 0, 0};
        int remainCardnum = 0;
        for (int n : cards) {
            remainCardnum += n;
        }

        OutCard outCard = OutCard.feijiWithTail(new int[]{5, 2}, new int[]{0, 0, 1, 1});

//        List<OutCard> outs = strategy.findBiggerCards(cards, remainCardnum, outCard, true);
//        if (outs != null && outs.size() > 0) {
//            outs.forEach(System.out::println);
//        } else {
//            System.out.println(" 未找到 !!! ");
//        }

        System.out.println(remainCardnum);
        int m = 11;
        double p = strategy.biggestProbability(0, cards, new int[]{3, remainCardnum - m, m}, outCard);

        System.out.println(p);
        System.out.println(System.currentTimeMillis() - begin);

    }

    public OutCard zhaAndWin(int role, int[] cards, int[] remainCards, int[] remainCardNum, OutCard outCard) {
        OutCard o = Strategy.findZhanDanOrHuoJian(cards, outCard);
        if (o != null) {
            Strategy.removeCard(cards, EMPTY_CARDS, o, true);
            OutCard oneHand = OutCardStrategy.oneHand(split.split(cards), o.getType() == CardType.ZHADAN ? remainCardNum[role] - 4 : remainCardNum[role] - 2);
            if (oneHand != null) {
                if (o.getType() == CardType.HUOJIAN || Strategy.randomFloat() > 0.3 || biggestProbability(role, remainCards, remainCardNum, o) > Config.SMALL_CARD) {
                    Strategy.removeCard(cards, EMPTY_CARDS, o, false);
                    return o;
                }

            }
            Strategy.removeCard(cards, EMPTY_CARDS, o, false);
        }

        return null;
    }
}
