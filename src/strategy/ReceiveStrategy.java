package strategy;

import config.Config;
import constant.CardType;
import model.CardArray;
import model.OutCard;

import java.util.List;

public class ReceiveStrategy implements Strategy {

    //是否可以一手出完


    public ReceiveStrategy(int round) {
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
                if (rs.nShunzi > 0 && rs.shunzi[0][0] > outCard.getCards()[0]) {
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

    public OutCard jieAndWin(int role, int[] cards, int[] remainCards, List<OutCard> outCards, int[] remainingCardNum) {

        for (OutCard x : outCards) {
            Strategy.removeCard(cards, EMPTY_CARDS, x, true);
            double bp = biggestProbability(role, remainCards, remainingCardNum, x);
            if (bp > Config.SMALL_CARD) {
                OutCard out = allBig2(role, cards, remainCards, remainingCardNum);
                if (out != null) {
                    Strategy.removeCard(cards, EMPTY_CARDS, x, false);
                    return x;
                }
            } else {
                CardArray arr = split.split(cards);
                OutCard o = findBiggestCardFromMe(role, arr, remainCards, remainingCardNum, x);
                if (o != null) {
                    Strategy.removeCard(cards, EMPTY_CARDS, o, true);
                    OutCard out = allBig2(role, cards, remainCards, remainingCardNum);
                    if (out != null) {
                        Strategy.removeCard(cards, EMPTY_CARDS, o, false);
                        Strategy.removeCard(cards, EMPTY_CARDS, x, false);
                        return x;
                    }
                    Strategy.removeCard(cards, EMPTY_CARDS, o, false);
                }
            }

            Strategy.removeCard(cards, EMPTY_CARDS, x, false);
        }
        return null;
    }


    /**
     * * 我在地主上家，地主只剩下一张牌，队友出单，我尽量大牌接
     *
     * @param cards
     * @param outCard
     * @param remainCards
     * @return
     */

    public OutCard enemyLastOne(int role, int[] cards, int[] outCard, int[] remainCards) {


        int i = canBiggerThanMe(outCard, CardType.DAN, remainCards, 1);
        if (i == 1) {
            CardArray rs = split.split(cards);
            if (rs.nDan > 0) {
                for (int j = 0; j < rs.nDan; j++) {
                    if (rs.dan[j] > outCard[0] && canBiggerThanMe(new int[]{rs.dan[j]}, CardType.DAN, remainCards, 1) == 0) {
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
                OutCard tmp = allBig(role, rs, remainCards, 1);
                if (tmp != null) {
                    return rs.nZhadan > 0 ? OutCard.zhadan(rs.zhadan[0]) : OutCard.huojian();
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
            Strategy.removeCard(cards,EMPTY_CARDS,biggest,true);
            for (int i = 0; i < minAccept; i++) {
                if (cards[i] > 0) {
                    Strategy.removeCard(cards,EMPTY_CARDS,biggest,false);
                    return biggest;
                }
            }
            Strategy.removeCard(cards,EMPTY_CARDS,biggest,false);
        }
        if (outCard.getRole() == 2) {
            return null;
        } else {
            return biggerCards.get(biggerCards.size() - 1);

        }
    }


    //兜底牌
    public OutCard normal(int[] cards, int role, OutCard outcard, int[] remainingCards, int[] remainCardNums, List<OutCard> biggerCards) {
        OutCard best = findBestOutCard(cards, biggerCards);

        if (role != 0) {
            int s = split.split(cards).score();
            if (best.getScore() > s) {
                return best;
            }
            if (s > Config.GOOD_CARDS_VALUE) {
                return best;
            }
            if (biggestProbability(role,remainingCards,remainCardNums,best,false) > Config.SMALL_CARD_MAP.get(best.getType())){
                return null;
            }

            if (outcard.getRole() != 0) {
                double p = Config.IF1[outcard.getRole()];
                double bp = biggestProbability(role, remainingCards, remainCardNums, outcard);
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
//                double bpSelf = biggestProbability(role, remainingCards, remainCardNums, best);
//                if (bpSelf - bp < Config.)

                if (remainCardNums[0] < 3 || bp == 1) {
                    System.out.println("瞎出牌");
                    return best;
                }
                return null;
            }
        } else {
            int s = split.split(cards).score();

            if (best.getScore() > s) {
                return best;
            }
            if (s > Config.GOOD_CARDS_VALUE) {
                return best;
            }

            //地主接牌规则,接地主下级时，尽可能接牌，因为该位置不会故意压牌，大部分情况下，不会由他来压牌，更多是为了跑牌
            if (outcard.getRole() == 1) {
                if (best.getType() == outcard.getType() && best.getType() != CardType.ZHADAN && best.getType() != CardType.HUOJIAN && (best.getCards()[0] < 12 || (best.getCards()[0] - outcard.getCards()[0] < Config.MAX_CARDS_SPAN))) {
                    return best;
                }
            }

        }
        if (Strategy.randomFloat() < Config.FOOL_ACTION) {
            return best;
        }

        return null;
    }


    private OutCard findBestOutCard(int[] cards, List<OutCard> outs) {

        OutCard out = null;
        int score = Integer.MIN_VALUE;
        for (OutCard o : outs) {
            Strategy.removeCard(cards, EMPTY_CARDS, o, true);
            CardArray rs = split.split(cards);
            int s = rs.score();
            if (s > score) {
                out = o;
                score = s;
            }
            Strategy.removeCard(cards, EMPTY_CARDS, o, false);
        }
        out.setScore(score);
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
}
