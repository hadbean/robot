package strategy;

import config.Config;
import constant.CardType;
import model.CardArray;
import model.OutCard;

import java.util.ArrayList;
import java.util.List;

/**
 * 主动出牌车略
 */
public class OutCardStrategy implements Strategy {


    private int[] alearyOutCards;
    private int round;

    public OutCardStrategy(int round) {
        this.round = round;
        split.setRound(round);
    }

    public static OutCard oneHand(CardArray cards, int remainCardNum) {

        OutCard outCard = null;
        switch (remainCardNum) {
            case 1: {
                outCard = OutCard.dan(cards.dan[0]);
                break;
            }
            case 2: {
                if (cards.nEr == 2 || cards.nDuizi == 1 || cards.nHuojian == 1) {
                    if (cards.nDuizi == 1) {
                        outCard = OutCard.duizi(cards.duizi[0]);
                    } else if (cards.nEr == 2) {
                        outCard = OutCard.duizi(12);
                    } else {
                        outCard = OutCard.huojian();
                    }
                }
                break;
            }
            case 3: {
                if (cards.nSantiao == 1 || cards.nEr == 3) {
                    if (cards.nSantiao == 1) {
                        outCard = OutCard.santiao(cards.santiao[0]);
                    } else {
                        outCard = OutCard.santiao(12);
                    }
                }
                break;
            }
            case 4: {
                if (cards.nSantiao == 1 || cards.nEr > 2 || cards.nZhadan == 1) {
                    if (cards.nSantiao == 1) {
                        outCard = OutCard.santiaoWithTail(cards.santiao[0], new int[]{cards.dan[0]});
                    } else if (cards.nEr == 3) {
                        outCard = OutCard.santiaoWithTail(12, new int[]{cards.dan[0]});
                    } else {
                        outCard = OutCard.zhadan(cards.zhadan[0]);
                    }
                }
                break;
            }
            case 5: {
                if (cards.nDuizi == 1 && (cards.nSantiao == 1 || cards.nEr == 3)) {
                    if (cards.nEr == 3) {
                        outCard = OutCard.santiaoWithTail(12, new int[]{cards.duizi[0], cards.duizi[0]});
                    } else {
                        outCard = OutCard.santiaoWithTail(cards.santiao[0], new int[]{cards.duizi[0], cards.duizi[0]});
                    }
                } else if (cards.nShunzi == 1) {
                    outCard = OutCard.shunzi(cards.shunzi[0]);
                } else if (cards.nSantiao == 1 && cards.nEr == 2) {
                    outCard = OutCard.santiaoWithTail(cards.santiao[0], new int[]{12, 12});
                }
                break;
            }
            default:
                break;
        }

        if (remainCardNum > 5) {
//            throw new RuntimeException("一手牌判断逻辑错误<"+ remainCardNum +">:" + cards.toString() );
            //判断是否顺子
            if (cards.nShunzi == 1) {
                if (cards.shunzi[0][1] == remainCardNum) {
                    outCard = OutCard.shunzi(cards.shunzi[0]);
                }
            } else if (cards.nLiandui == 1) {
                if (cards.liandui[0][1] * 2 == remainCardNum) {
                    outCard = OutCard.liandui(cards.liandui[0]);
                }
            } else if (cards.nFeiji > 0) {
                boolean flag = false;
                if (remainCardNum - cards.feiji[0][1] * 3 == 0 || remainCardNum == cards.nFeiji * 4) {

                    flag = true;
                } else if (cards.nEr == 2 && (remainCardNum == cards.nFeiji * 3 + cards.nDuizi * 2 + 2)) {
                    flag = true;
                } else if (cards.nEr == 0 && remainCardNum == cards.nFeiji * 3 + cards.nDuizi * 2) {
                    flag = true;
                }
                if (flag) {
                    if (remainCardNum - cards.feiji[0][1] * 3 == 0) {
                        outCard = OutCard.feiji(cards.feiji[0]);
                    } else {
                        int[] tail = new int[cards.feiji[0][1]];
                        int k = 0;
                        for (int i : cards.cards) {
                            if (i > 0 && i < 3) {
                                if (i == 1) {
                                    tail[k] = i;
                                    k++;
                                } else {
                                    tail[k] = i;
                                    tail[k + 1] = i;
                                    k += 2;
                                }
                            }
                        }
                        outCard = OutCard.feijiWithTail(cards.feiji[0], tail);
                    }
                }
            } else if (cards.nZhadan == 1) {
                if (remainCardNum == 6) {
                    if (cards.nDan == 2) {
                        outCard = OutCard.zhadanWithTail(cards.zhadan[0], new int[]{cards.dan[0], cards.dan[1]});
                    } else if (cards.nHuojian == 1) {
                        outCard = OutCard.zhadan(cards.zhadan[0]);
                    } else {
                        if (cards.nEr == 2) {
                            outCard = OutCard.zhadanWithTail(cards.zhadan[0], new int[]{12, 12});
                        } else {
                            outCard = OutCard.zhadanWithTail(cards.zhadan[0], new int[]{cards.duizi[0], cards.duizi[0]});
                        }
                    }
                } else if (remainCardNum == 8 && (cards.nDuizi == 2 || (cards.nDuizi == 1 && cards.nEr == 2))) {
                    if (cards.nDuizi == 2) {
                        outCard = OutCard.zhadanWithTail(cards.zhadan[0], new int[]{cards.duizi[0], cards.duizi[0], cards.duizi[1], cards.duizi[1]});
                    } else {
                        outCard = OutCard.zhadanWithTail(cards.zhadan[0], new int[]{cards.duizi[0], cards.duizi[0], 12, 12});
                    }
                }
            } else if (cards.nZhadan == 2 && remainCardNum == 8) {
                return OutCard.zhadan(cards.zhadan[0]);
            }
        }

        return outCard;
    }


    //当我有大牌时，地主还剩2张牌，出单
    public int forceEnemySingle(CardArray cards, int[] remainCards, int[] remainCardNum, int posion) {
        int max = 0;
        if (cards.nSantiao > 0 || cards.nShunzi > 0 || cards.nLiandui > 0 || cards.nFeiji > 0) {
            return 0;
        }
        if (cards.nZhadan > 0) {
            return 1;
        }
        if (cards.nDuizi == 0) {
            return 1;
        }
        if (cards.nDan > 0) {
            max = cards.dan[cards.nDan - 1];
        }
        if (cards.nEr > 0 && max < 12) {
            max = 12;
        }
        if (cards.duizi[cards.nDuizi - 1] > max) {
            max = cards.duizi[cards.nDuizi - 1];
        }
        if (max == 0) {
            return 0;
        }

        int n = canBiggerThanMe(new int[]{max}, CardType.DAN, remainCards, 2);
        if (n == 1) {
            return 0;
        }

        return 1;
    }

    //让队友过牌  我下手是队友，只有一张牌，且我有<10的牌
    public OutCard letFriend(int[] cards) {
        for (int i = 0; i < 7; i++) {
            if (cards[i] > 0) {
                return OutCard.dan(i);
            }
        }
        return null;
    }

    //有些小的顺子、连对、飞机	先出这些
    public OutCard smallAndLongFirst(CardArray cards, int role, int[] remainingCards, int[] remainCardNum) {
        double mp = 0;
        OutCard out = null;
        List<OutCard> outs = new ArrayList<>(3);
        if (cards.nFeiji > 0) {
            OutCard o1 = OutCard.feiji(cards.feiji[0]);
            Strategy.removeCard(cards.cards, EMPTY_CARDS, o1, true);
            if (findTail(cards.cards, o1, 1, cards.feiji[0][1], false) || findTail(cards.cards, o1, 2, cards.feiji[0][1], false)) {
                Strategy.removeCard(cards.cards, EMPTY_CARDS, o1, false);
//                Strategy.removeFrom(o1.getTail(),cards.cards,true);
                o1.setType(CardType.FEIJIWITHTAIL);
            } else {
                Strategy.removeCard(cards.cards, EMPTY_CARDS, o1, false);
            }
//            int mbp = 0;
//            if (cards.nDan >= cards.feiji[0][1]) {
//                int n = cards.feiji[0][1];
//                int[] tail = new int[n];
//                for (int i = 0; i < n; i++) {
//                    tail[i] = cards.dan[i];
//                    mbp += cards.dan[i] - Config.REF;
//                }
//
//                o1 = OutCard.feijiWithTail(cards.feiji[0], tail);
//
//            }
//            if (cards.nDuizi >= cards.feiji[0][1]) {
//                int mbp2 = 0;
//                int n = cards.feiji[0][1];
//                int[] tail = new int[2 * n];
//                for (int i = 0; i < n; i++) {
//
//                    tail[2 * i] = cards.duizi[i];
//                    tail[2 * i + 1] = cards.duizi[i];
//                    mbp2 += cards.duizi[i] > Config.REF ? (cards.duizi[i] - Config.REF) * 3 / 2 : (cards.duizi[i] - Config.REF);
//                }
//                if ((o1 != null && mbp2 < mbp) || o1 == null) {
//                    o1 = OutCard.feijiWithTail(cards.feiji[0], tail);
//                }
//            }
//            if (o1 == null && (cards.nDuizi * 2 + cards.nDan >= cards.feiji[0][1])) {
//                if (cards.feiji[0][1] == 2) {
//                    int[] tail = new int[2];
//                    if (cards.nDan == 1) {
//                        if (cards.dan[0] > cards.duizi[0]) {
//                            tail[0] = cards.duizi[0];
//                            tail[1] = cards.duizi[0];
//                        } else {
//                            tail[0] = cards.dan[0];
//                            tail[1] = cards.duizi[0];
//                        }
//                    } else {
//                        tail[0] = cards.duizi[0];
//                        tail[1] = cards.duizi[0];
//                    }
//                    o1 = OutCard.feijiWithTail(cards.feiji[0], tail);
//                } else if (cards.feiji[0][1] == 3) {
//                    int[] tail = new int[3];
//                    if (cards.nDan == 1) {
//                        if (cards.nDuizi == 1 || cards.dan[0] < cards.duizi[0]) {
//                            tail[0] = cards.duizi[0];
//                            tail[1] = cards.duizi[0];
//                            tail[2] = cards.dan[0];
//                        } else if (cards.nDuizi > 1) {
//                            if (cards.dan[0] < cards.duizi[1]) {
//                                tail[0] = cards.duizi[0];
//                                tail[1] = cards.duizi[0];
//                                tail[2] = cards.dan[0];
//                            } else {
//                                tail[0] = cards.duizi[0];
//                                tail[1] = cards.duizi[0];
//                                tail[2] = cards.duizi[1];
//                            }
//                        }
//                    } else if (cards.nDan == 0) {
//                        tail[0] = cards.duizi[0];
//                        tail[1] = cards.duizi[0];
//                        tail[2] = cards.duizi[1];
//                    } else if (cards.nDan == 2) {
//                        if (cards.dan[1] < cards.duizi[0]) {
//                            tail[0] = cards.dan[0];
//                            tail[1] = cards.dan[1];
//                            tail[2] = cards.duizi[0];
//                        } else {
//                            tail[0] = cards.dan[0];
//                            tail[1] = cards.duizi[0];
//                            tail[2] = cards.duizi[0];
//                        }
//                    }
//                    o1 = OutCard.feijiWithTail(cards.feiji[0], tail);
//                }
//            } else if (o1 == null) {
//                o1 = OutCard.feiji(cards.feiji[0]);
//            }
            outs.add(o1);
        }
        if (cards.nLiandui > 0) {
            outs.add(OutCard.liandui(cards.liandui[0]));
        }
        if (cards.nShunzi > 0) {
            outs.add(OutCard.shunzi(cards.shunzi[0]));
        }
        if (outs.isEmpty()) {
            return null;
        }
        double minBp = 1.1;
        for (OutCard o : outs) {
            double bp = biggestProbability(role, remainingCards, remainCardNum, o);
            if (bp < minBp) {
                minBp = bp;
                out = o;
            }
        }

        if (out != null) {
            out.setBp(minBp);
        }

        return out;
    }

    public OutCard fewPoke(int role, CardType cards, int[] remainCardNum) {

        return null;
    }

    //牌比较少的时候	优先出单张以外的牌
    public Boolean fewPoke(CardArray cards, int remainCardNum) {

        if (cards.nDan == remainCardNum) {
            return false;
        }
        cards.score();
        if (cards.hands < 4) {
            return true;
        }
        return false;
    }

    /**
     * @param cards
     * @param role  1 地主下家 2 地主上家
     * @return 0 出其它 1 出单
     */
    public OutCard enemyLastOne(CardArray cards, int role, int[] remainCardNum) {

        if (role == 1) {
            if (remainCardNum[2] < 4 && cards.nDuizi > 0) {
                return OutCard.duizi(cards.duizi[0]);
            }
            if (cards.nDuizi > 0 && cards.duizi[0] > 1 && cards.nDan > 2) {

                return OutCard.dan(cards.dan[cards.nDan - 2]);
            }
            return null;
        } else if (role == 2) {
            if (cards.nDuizi > 0) {
                return OutCard.duizi(cards.duizi[0]);
            } else if (cards.nSantiao > 0){
                return null;
            }else {
                return OutCard.dan(cards.dan[cards.nDan - 1]);
            }
        }

        return null;
    }

    //以小牌优先
    public OutCard smallFirst(CardArray cards, int role, int[] remainingCards, int[] remainCardNum, boolean fewHand) {
        List<OutCard> outs = new ArrayList<>(3);

        if (cards.nSantiao > 0) {
            OutCard o = null;
            if (cards.nDan > 0 && cards.nDuizi > 0) {
                o = OutCard.santiaoWithTail(cards.santiao[0], cards.dan[0] > cards.duizi[0] ? new int[]{cards.duizi[0], cards.duizi[0]} : new int[]{cards.dan[0]});
            } else if (cards.nDan > 0) {
                o = OutCard.santiaoWithTail(cards.santiao[0], new int[]{cards.dan[0]});
            } else if (cards.nDuizi > 0) {
                o = OutCard.santiaoWithTail(cards.santiao[0], new int[]{cards.duizi[0], cards.duizi[0]});
            } else {
                o = OutCard.santiao(cards.santiao[0]);
            }
            if (o != null) {
                o.setBp(biggestProbability(role, remainingCards, remainCardNum, o, false));
                Strategy.removeCard(cards.cards, EMPTY_CARDS, o, true);
                o.setScore(split.split(cards.cards).score());
                Strategy.removeCard(cards.cards, EMPTY_CARDS, o, false);
                outs.add(o);
            }
            if (o != null  && fewHand){
                return o;
            }
        }


        if (cards.nDan > 0) {
            OutCard o = OutCard.dan(cards.dan[0]);
            o.setBp(biggestProbability(role, remainingCards, remainCardNum, o, false));
            Strategy.removeCard(cards.cards, EMPTY_CARDS, o, true);
            o.setScore(split.split(cards.cards).score());
            Strategy.removeCard(cards.cards, EMPTY_CARDS, o, false);
            outs.add(OutCard.dan(cards.dan[0]));
        }
        if (cards.nDuizi > 0) {
            outs.add(OutCard.duizi(cards.duizi[0]));
        }

        OutCard out = null;

        if (!outs.isEmpty()) {
            if (outs.size() == 1) {
                return outs.get(0);
            }
            double minS = Integer.MAX_VALUE;
            double maxS = Integer.MIN_VALUE;
            double fm = 0;
            double minBp = Integer.MAX_VALUE;
            double maxBp = Integer.MIN_VALUE;
            double minC = Integer.MAX_VALUE;
            double maxC = Integer.MIN_VALUE;
            for (OutCard o : outs) {
                o.setBp(biggestProbability(role, remainingCards, remainCardNum, o, false));
                if (o.getBp() < Config.SMALL_CARD_MAP.get(o.getType())){
                    //是否有手牌可以接回来
                    OutCard bg = findBiggestCardFromMe(role, cards, remainingCards, remainCardNum, o,cards.hands < 4);
                    if (bg != null && biggestProbability(role, remainingCards, remainCardNum, bg, true) > Config.SMALL_CARD_MAP.get(bg.getType())) {
                        o.setRecall(true);
                    }
                }
                Strategy.removeCard(cards.cards, EMPTY_CARDS, o, true);
                o.setScore(split.split(cards.cards).score());
                Strategy.removeCard(cards.cards, EMPTY_CARDS, o, false);
                if (o.getCards()[0] > maxC) {
                    maxC = o.getCards()[0];
                }
                if (o.getBp() > maxBp) {
                    maxBp = o.getBp();
                }
                if (o.getScore() > maxS) {
                    maxS = o.getScore();
                }
                if (o.getCards()[0] < minC) {
                    minC = o.getCards()[0];
                }
                if (o.getBp() < minBp) {
                    minBp = o.getBp();
                }
                if (o.getScore() < maxS) {
                    minS = o.getScore();
                }
            }
            int max = 0;

            for (OutCard o : outs) {
                int k = 0;
                if (o.getScore() == maxS) {
                    k += 2;
                } else if (o.getScore() > minS) {
                    k++;
                }
                if (o.getBp() == minBp) {
                    k += 2;
                } else if (o.getBp() < maxBp) {
                    k++;
                }
                if (o.getCards()[0] == minC) {
                    k += 2;
                } else if (o.getCards()[0] < minC) {
                    k++;
                }
                if (out != null && !out.isRecall() && o.isRecall()){
                    out = o;
                }else {
                    if (k > max) {
                        max = k;
                        if (fewHand && out != null && out.getType() == CardType.DAN && o.getType() == CardType.DAN) {
                            continue;
                        }
                        out = o;
                    } else if (k == max) {
                        if (Strategy.randomFloat() > 0.5) {
                            out = o;
                        }
                    }
                }
            }

        }
        return out;
    }

}
