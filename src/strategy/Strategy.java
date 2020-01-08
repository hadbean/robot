package strategy;

import config.Config;
import constant.CardType;
import model.CardArray;
import model.CardGraph;
import model.OutCard;
import model.Player;
import utils.CardSplit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public interface Strategy {
    CardSplit split = new CardSplit();

    int[] CARDS = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1};
    int[] EMPTY_CARDS = new int[15];

    public default int[] remainingCardsExceptMe(int[] self, int[] alearyOutCards) {
        int[] remain = new int[15];
        for (int i = 0; i < 15; i++) {
            remain[i] = CARDS[i] - self[i] - alearyOutCards[i];
        }
        return remain;
    }

    /**
     * 判断我出的牌是不是场上最大
     *
     * @param outCard            出的牌
     * @param type               牌类型
     * @param remainCards        除了我的都牌，场上剩余的牌
     * @param enemyRemainCardNum 敌人剩余手牌数量
     * @return 1:有比你大， 0 没有
     */
    default int canBiggerThanMe(int[] outCard, CardType type, int[] remainCards, int enemyRemainCardNum) {

        switch (type) {
            case DAN: {
                if (outCard[0] == 14) {
                    return 0;
                }
                for (int i = outCard[0] + 1; i < remainCards.length; i++) {
                    if (remainCards[i] > 0) {
                        return 1;
                    }
                }
                return 0;
            }
            case DUI: {
                if (outCard[0] == 12) {
                    return 0;
                }
                for (int i = outCard[0] + 1; i < remainCards.length - 2; i++) {
                    if (remainCards[i] > 1) {
                        return 1;
                    }
                }
                return 0;
            }
            case ZHADAN:
            case FEIJI:
            case FEIJIWITHTAIL:
            case ZHADANWITHTAIL:
            case HUOJIAN: {
                return 0;
            }
            case LIANDUI:
            case SHUNZI: {

                int p = type == CardType.LIANDUI ? 2 : 1;
                int a = p - 1;
                if (outCard[1] * p > enemyRemainCardNum || outCard[0] == 11) {
                    return 0;
                } else {
                    int begin = outCard[0] - outCard[1] + 1;
                    int length = outCard[1];
                    int n = 0;
                    for (int i = begin + 1; i < 12; i++) {
                        if (remainCards[i] > a) {
                            n++;
                        } else if (n >= length) {
                            return 1;
                        } else {
                            n = 0;
                        }
                    }
                }
                return 0;
            }
            case SANTIAOWITHTAIL:
            case SANTIAO: {
                int min = type == CardType.SANTIAO ? 3 : 4;
                if (enemyRemainCardNum < min) {
                    return 0;
                } else {
                    for (int i = outCard[0] + 1; i < remainCards.length; i++) {
                        if (remainCards[i] == 3) {
                            return 1;
                        }
                    }
                    return 0;
                }
            }
            default:
                break;

        }

        return 0;
    }

    /**
     * 出牌赢牌最优概率
     *
     * @param role
     * @param cards
     * @param remainCards
     * @param remainCardNum
     * @return
     */
    default OutCard allBig2(int role, int[] cards, int[] remainCards, int[] remainCardNum, boolean godView, int[][] playCards) {
        int n = 0;
        OutCard out = null;

        CardGraph cg = new CardGraph();
        List<OutCard> small = new ArrayList<>(3);
        int[] tmpCards = new int[cards.length];
        System.arraycopy(cards, 0, tmpCards, 0, cards.length);

        CardArray arr = split.split(tmpCards);
        boolean change = false;
        if (arr.nFeiji > 0) {
            change = true;
            for (int i = 0; i < arr.nFeiji; i++) {
                OutCard o = OutCard.feiji(arr.feiji[i]);
                removeCard(arr.cards, EMPTY_CARDS, o, true);
                if (findTail(arr.cards, o, 1, arr.feiji[i][1], false) || findTail(arr.cards, o, 2, arr.feiji[i][1], false)) {
                    o.setType(CardType.FEIJIWITHTAIL);
                    removeFrom(o.getTail(), arr.cards, true);
                }
                double bp = godView?biggestProbability(role,playCards,remainCardNum,o) : biggestProbability(role, remainCards, remainCardNum, o,remainCardNum[role] < 7);
                o.setBp(bp);
                if (bp < Config.SMALL_CARD_MAP.get(o.getType())) {
                    n++;
                    small.add(o);
                    if (n > 2) {
                        return null;
                    }
                } else if (out == null) {
                    out = o;
                }

            }
            arr.nFeiji = 0;
        }
        if (change) {
            arr = split.split(arr.cards);
            change = false;
        }
        if (arr.nShunzi > 0) {
            change = true;
            for (int i = 0; i < arr.nShunzi; i++) {
                OutCard o = OutCard.shunzi(arr.shunzi[i]);

                double bp = godView?biggestProbability(role,playCards,remainCardNum,o) : biggestProbability(role, remainCards, remainCardNum, o,remainCardNum[role] < 7);
                o.setBp(bp);
                if (bp < Config.SMALL_CARD_MAP.get(o.getType())) {
                    n++;
                    small.add(o);
                } else if (out == null) {
                    out = o;
                }
                removeCard(arr.cards, EMPTY_CARDS, o, true);
            }
            arr.nShunzi = 0;
        }
        if (n > 2) {
            return null;
        }
        if (arr.nSantiao > 0) {
            change = true;
            for (int i = 0; i < arr.nSantiao; i++) {
                OutCard o = OutCard.santiao(arr.santiao[i]);
                removeCard(arr.cards, EMPTY_CARDS, o, true);
                if (findTail(arr.cards, o, 1, 1, false) || findTail(arr.cards, o, 2, 1, false)) {
                    removeFrom(o.getTail(), arr.cards, true);
                    if (i < arr.nSantiao - 1) {
                        for (int t : o.getTail()) {
                            boolean flag = false;
                            for (int j = i + 1; j < arr.nSantiao; j++) {
                                if (t == arr.santiao[j]) {
                                    arr = split.split(arr.cards);
                                    flag = true;
                                    break;
                                }
                            }
                            if (flag) {
                                break;
                            }
                        }
                    }
                    o.setType(CardType.SANTIAOWITHTAIL);
                }
                double bp = godView?biggestProbability(role,playCards,remainCardNum,o) : biggestProbability(role, remainCards, remainCardNum, o,remainCardNum[role] < 7);
                o.setBp(bp);
                if (bp < Config.SMALL_CARD_MAP.get(o.getType())) {
                    n++;
                    small.add(o);
                    if (n > 2) {
                        return null;
                    }
                } else if (out == null) {
                    out = o;
                }

            }
            arr.nSantiao = 0;
        }
        if (change) {
            arr = split.split(arr.cards);
            change = false;
        }
        if (arr.nLiandui > 0) {
            change = true;
            for (int i = 0; i < arr.nLiandui; i++) {
                OutCard o = OutCard.liandui(arr.liandui[i]);
                double bp = godView?biggestProbability(role,playCards,remainCardNum,o) : biggestProbability(role, remainCards, remainCardNum, o,remainCardNum[role] < 7);
                o.setBp(bp);
                if (bp < Config.SMALL_CARD_MAP.get(o.getType())) {
                    n++;
                    small.add(o);

                } else if (out == null) {
                    out = o;
                }
                removeCard(arr.cards, EMPTY_CARDS, o, true);
            }
            arr.nLiandui = 0;
        }

        if (n > 2) {
            return null;
        }
        CardArray newCard = change ? split.split(arr.cards) : arr;

        int emermyCardNum = role == 0 ? Math.min(remainCardNum[1], remainCardNum[2]) : remainCardNum[0];
        if (newCard.nDan > 0) {
            for (int i = 0; i < newCard.nDan; i++) {
                OutCard o = OutCard.dan(newCard.dan[i]);
                double bp = godView?biggestProbability(role,playCards,remainCardNum,o) : biggestProbability(role, remainCards, remainCardNum, o,remainCardNum[role] < 7);
                o.setBp(bp);
                if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                    cg.bDan[cg.nbDan] = newCard.dan[i];
                    cg.nbDan++;
                    if (out == null) {
                        out = o;
                    }
                } else {
                    cg.sDan[cg.nsDan] = newCard.dan[i];
                    cg.nsDan++;
                    n++;
                    small.add(OutCard.dan(newCard.dan[i]));
                }
            }
        }
        if (n > 2) {
            return null;
        }
        if (newCard.nDuizi > 0) {
            for (int i = 0; i < newCard.nDuizi; i++) {
                OutCard o = OutCard.duizi(newCard.duizi[i]);
                double bp = godView?biggestProbability(role,playCards,remainCardNum,o) : biggestProbability(role, remainCards, remainCardNum, o,remainCardNum[role] < 7);
                o.setBp(bp);
                if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                    cg.bDuizi[cg.nbDuizi] = newCard.duizi[i];
                    cg.nbDuizi++;
                    if (emermyCardNum == 1 || out == null) {
                        out = o;
                    }
                } else {
                    cg.sDuizi[cg.nsDuizi] = newCard.duizi[i];
                    cg.nsDuizi++;
                    small.add(OutCard.duizi(newCard.duizi[i]));
                    n++;
                }
            }
        }
        if (arr.nEr > 1) {
            cg.nEr = arr.nEr;
            cg.hasBigDui2 = (godView?biggestProbability(role,playCards,remainCardNum,OutCard.duizi(12)) : biggestProbability(role, remainCards, remainCardNum, OutCard.duizi(12),remainCardNum[role] < 7)) > Config.SMALL_CARD;
            if (cg.nEr > 2) {
                cg.hasBigSan2 = (godView?biggestProbability(role,playCards,remainCardNum,OutCard.santiao(12)) : biggestProbability(role, remainCards, remainCardNum, OutCard.santiao(12),remainCardNum[role] < 7)) > Config.SMALL_CARD;
            }
        }
        if (n > 2) {
            return null;
        }
        if (n == 2) {

            for (OutCard o : small) {
                OutCard bg = findBiggestCardFromMe(role, (o.getType() == CardType.DAN || o.getType() == CardType.DUI) ? split.split(arr.cards) : split.split(cards), remainCards, remainCardNum, o, true);
                if (bg != null && (godView?biggestProbability(role,playCards,remainCardNum,bg) : biggestProbability(role, remainCards, remainCardNum, bg,remainCardNum[role] < 7)) > Config.SMALL_CARD_MAP.get(bg.getType())) {
                    return o;
                }
            }

//            if (cg.nsDan > 0 && cg.nbDan > 0) {
//                return OutCard.dan(cg.sDan[0]);
//            } else if (cg.nsDuizi > 0 && (cg.nbDuizi > 0 || cg.hasBigDui2)) {
//                return OutCard.duizi(cg.sDuizi[0]);
//            }

            return null;

        } else {//先出大牌
            if (out != null) {
                return out;
            }
            if (cg.nbDuizi > 0) {
                return OutCard.duizi(cg.bDuizi[0]);
            }
            if (cg.nbDan > 0) {
                return OutCard.dan(cg.bDan[0]);
            }

            if (cg.hasBigSan2) {
                OutCard o = OutCard.santiao(12);
                if (findTail(newCard.cards, o, 1, 1, false) || findTail(newCard.cards, o, 2, 1, false)) {
                    o.setType(CardType.SANTIAOWITHTAIL);
                }
                return o;
            } else if (cg.hasBigDui2) {
                return OutCard.duizi(12);
            } else if (arr.nZhadan > 0) {
                if (n == 1){
                    if (small.get(0).getLength() < emermyCardNum){
                        return small.get(0);
                    }
                }
                return OutCard.zhadan(arr.zhadan[0]);
            } else if (arr.nHuojian > 0) {
                return OutCard.huojian();
            }

        }
        return null;
    }

    default OutCard findBiggestCardFromMe(int role, CardArray rs, int[] remainCards, int[] remaingCardNum, OutCard out, boolean force) {

        CardType type = out.getType();
        switch (type) {
            case FEIJI:
            case FEIJIWITHTAIL: {
                if (rs.nFeiji > 1) {
                    if (rs.feiji[rs.nFeiji - 1][0] > out.getCards()[0] && rs.feiji[rs.nFeiji - 1][1] == out.getCards()[1]) {
                        OutCard o = OutCard.feiji(rs.feiji[rs.nFeiji - 1]);
                        if (!findTail(rs.cards, o, (out.getLength() / out.getCards()[1]) - 3, out.getCards()[1], false)) {
                            return null;
                        }
                        double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                        if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                            o.setType(type);
                            return o;
                        }
                    }
                    return null;
                }
                break;
            }
            case SANTIAO:
            case SANTIAOWITHTAIL: {
                if (rs.nSantiao > 1) {
                    if (rs.santiao[rs.nSantiao - 1] > out.getCards()[0]) {
                        OutCard o = OutCard.santiao(rs.santiao[rs.nSantiao - 1]);
                        if (!findTail(rs.cards, o, out.getLength() - 3, 1, false)) {
                            return null;
                        }
                        double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                        if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                            o.setType(type);
                            return o;
                        }
                    }
                    return null;
                }
                break;
            }
            case SHUNZI: {
                if (rs.nShunzi > 1) {
                    int[] shunzi = rs.shunzi[rs.nShunzi - 1];
                    if (shunzi[0] > out.getCards()[0] && shunzi[1] == out.getCards()[1]) {
                        OutCard o = OutCard.shunzi(shunzi);
                        double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                        if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                            return o;
                        }
                    }
                }
                break;
            }
            case LIANDUI: {
                if (rs.nLiandui > 1) {
                    int[] liandui = rs.liandui[rs.nLiandui - 1];
                    if (liandui[0] > out.getCards()[0] && liandui[1] == out.getCards()[1]) {
                        OutCard o = OutCard.liandui(liandui);
                        double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                        if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                            return o;
                        }
                    }
                }
                break;
            }
            case DAN: {
                if (rs.nDan > 1) {
                    int dan = rs.dan[rs.nDan - 1];
                    if (dan > out.getCards()[0]) {
                        OutCard o = OutCard.dan(dan);
                        double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                        if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                            return o;
                        }
                    }
                }
                if (rs.nEr > 1 && out.getCards()[0] < 12){
                    OutCard o = OutCard.dan(12);
                    double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                    if (bp > Config.SMALL_CARD_MAP.get(CardType.DAN)) {
                        return o;
                    }
                }
                break;
            }
            case DUI: {
                if (rs.nDuizi > 1) {
                    int dui = rs.duizi[rs.nDuizi - 1];
                    if (dui > out.getCards()[0]) {
                        OutCard o = OutCard.duizi(dui);
                        double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                        if (bp > Config.SMALL_CARD_MAP.get(o.getType())) {
                            return o;
                        }
                    }
                }
                if (rs.nEr > 1 && out.getCards()[0] < 12){
                    OutCard o = OutCard.duizi(12);
                    double bp = biggestProbability(role, remainCards, remaingCardNum, o);
                    if (bp > Config.SMALL_CARD_MAP.get(CardType.DUI)) {
                        return o;
                    }
                }
                break;
            }
            default:
                break;
        }
        if (force) {
            return findZhanDanOrHuoJian(rs.cards, out);
        }
        return null;
    }

    /**
     * @param cards
     * @param remainCardNum
     * @param outCard
     * @param force         是否一定要接，一定要接的话，会考虑炸弹和火箭，并且不管接了牌变坏的情况，否则不考虑
     * @return
     */
    default List<OutCard> findBiggerCards(int[] cards, int remainCardNum, OutCard outCard, boolean force, boolean ignoreZha) {
        List<OutCard> rs = new ArrayList<>();
        int outLength = outCard.getLength();

        int[] mainCard = outCard.getCards();
        CardType type = outCard.getType();
        if (type == CardType.HUOJIAN) {
            return rs;
        }

        if (remainCardNum < outLength) {

        } else {

            switch (type) {
                case DAN: {
                    for (int i = mainCard[0] + 1; i < cards.length; i++) {
                        if (cards[i] > 0) {
                            rs.add(OutCard.dan(i));
                        }
                    }
                    break;
                }
                case DUI: {
                    for (int i = mainCard[0] + 1; i < cards.length; i++) {
                        if (cards[i] > 1) {
                            rs.add(OutCard.duizi(i));
                        }
                    }
                    break;
                }
                case SHUNZI: {
                    if (mainCard[0] == 11) {
                        break;
                    }
                    int n = 0;
                    for (int i = mainCard[0] + 2 - mainCard[1]; i < 13 - mainCard[1] + n; i++) {
                        if (cards[i] > 0) {
                            n++;
                        } else {
                            n = 0;
                        }
                        if (n == mainCard[1]) {
                            rs.add(new OutCard(new int[]{i, mainCard[1]}, CardType.SHUNZI));
                            i = i + 1 - mainCard[1];
                            n = 0;
                        }
                    }
                    break;
                }
                case LIANDUI: {
                    if (mainCard[0] == 11) {
                        break;
                    }
                    int n = 0;
                    for (int i = mainCard[0] + 2 - mainCard[1]; i < 12 - mainCard[1] + n; i++) {
                        if (cards[i] > 1) {
                            n++;
                        } else {
                            n = 0;
                        }
                        if (n == mainCard[1]) {
                            rs.add(new OutCard(new int[]{i, mainCard[1]}, CardType.LIANDUI));
                            i = i + 2 - mainCard[1];
                            n = 0;
                        }
                    }
                    break;
                }

                case SANTIAO:
                case SANTIAOWITHTAIL: {
                    if (mainCard[0] == 12) {
                        break;
                    }

                    for (int i = mainCard[0] + 1; i < 13; i++) {
                        if (cards[i] > 2) {
                            rs.add(new OutCard(new int[]{i}, type));
                        }
                    }
                    break;
                }
                case FEIJI:
                case FEIJIWITHTAIL: {
                    if (mainCard[0] == 11) {
                        break;
                    }
                    if (mainCard[1] == 2) {
                        for (int i = mainCard[0] + 1; i < 11; i++) {
                            if (cards[i] > 2 && cards[i + 1] > 2) {
                                rs.add(new OutCard(new int[]{i + 1, 2}, type));
                            }
                        }
                    } else if (mainCard[1] == 3) {
                        for (int i = mainCard[0] + 1; i < 9; i++) {
                            if (cards[i] > 2 && cards[i + 1] > 2 && cards[i + 2] > 2) {
                                rs.add(new OutCard(new int[]{i + 2, 3}, type));
                            }
                        }
                    }
                    break;
                }
                case HUOJIAN: {
                    return rs;
                }
                case ZHADANWITHTAIL:
                case ZHADAN: {
                    for (int i = mainCard[0] + 1; i < 13; i++) {
                        if (cards[i] == 4) {
                            rs.add(new OutCard(new int[]{i}, type));
                        }
                    }
                    break;
                }
                default:
                    break;
            }
            if (force) {
                if (!ignoreZha) {
                    int i = 0;
                    if (type == CardType.ZHADAN) {
                        i = mainCard[0] + 1;
                    }
                    for (; i < 13; i++) {
                        if (cards[i] == 4) {
                            rs.add(OutCard.zhadan(i));
                        }
                    }
                }
                if (cards[13] == 1 && cards[14] == 1) {
                    rs.add(OutCard.huojian());
                }
            }
            //
            if (rs.size() == 0) {
                return rs;
            }

            // 带牌程序 主动带牌程序，不考虑自身 --------------------------------
            for (OutCard x : rs) {
                CardType t = x.getType();
                switch (t) {

                    case SANTIAOWITHTAIL: {

                        int n = 1;
                        cards[x.getCards()[0]] -= 3;
                        int k = outCard.getTail().length;
                        boolean flag = findTail(cards, x, k, n, force);
                        cards[x.getCards()[0]] += 3;

                        if (!flag) {
                            return Collections.EMPTY_LIST;
                        }


                        break;
                    }
                    case ZHADANWITHTAIL: {

                        int n = 2;
                        cards[x.getCards()[0]] -= 4;

                        int k = outCard.getTail().length / n;

                        boolean flag = findTail(cards, x, k, n, false);
                        cards[x.getCards()[0]] += 4;

                        if (!flag) {
                            return Collections.EMPTY_LIST;
                        }

                        break;
                    }
                    case FEIJIWITHTAIL: {

                        int n = x.getCards()[1];
                        for (int i = 0; i < n; i++) {
                            cards[x.getCards()[0] - i] -= 3;
                        }
                        int k = outCard.getTail().length / n;

                        boolean flag = findTail(cards, x, k, n, false);
                        for (int i = 0; i < n; i++) {
                            cards[x.getCards()[0] - i] += 3;
                        }
                        if (!flag) {
                            return Collections.EMPTY_LIST;
                        }


                        break;
                    }

                    default:
                        break;
                }
            }
        }

        return rs;
    }

    static OutCard findZhanDanOrHuoJian(int[] cards, OutCard outCard) {
        int i = 0;
        if (outCard.getType() == CardType.ZHADAN) {
            i = outCard.getCards()[0] + 1;
        }
        for (; i < cards.length - 2; i++) {
            if (cards[i] == 4) {
                return OutCard.zhadan(i);
            }
        }
        if (cards[13] == 1 && cards[14] == 1) {
            return OutCard.huojian();
        }
        return null;
    }

//带牌设置

    /**
     * @param cards
     * @param out
     * @param k     带单还是带双
     * @param n     带几对
     * @return 是否有找到合适的带牌
     */
    default boolean findTail(int[] cards, OutCard out, int k, int n, boolean force) {


        CardArray arr = split.split(cards, k == 2);
        if (k == 1) {
            int[] tail = new int[n];
            int idx = 0;
            // 从单牌中获取
            if (arr.nDan > 0) {
                for (int i = 0; i < arr.nDan; i++) {
                    if (arr.dan[i] < 12) {
                        tail[idx] = arr.dan[i];
                        idx++;
                        if (idx == n) {
                            break;
                        }
                    }
                }
            }
            // 从长度大于5的顺子获取
            if (idx < n && arr.nShunzi > 0) {
                for (int i = 0; i < arr.nShunzi; i++) {
                    int[] sz = arr.shunzi[i];
                    if (arr.shunzi[i][1] > 5) {
                        for (int j = sz[0] - sz[1] + 1; j < sz[0] - 4; j++) {
                            if (idx < n) {
                                tail[idx] = j;
                                idx++;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            if (force){
                if (arr.nDuizi > 0 || arr.nEr > 0){
                    if (arr.nDuizi > 0){
                        tail[0] = arr.duizi[0];
                    }else {
                        tail[0] = 12;
                    }
                    idx++;
                }
            }
            if (idx < n) {
                return false;
            }
            out.setTail(tail);
            //带双
        } else if (k == 2) {
            int[] tail = new int[2 * n];
            int idx = 0;
            //找对子
            if (arr.nDuizi > 0) {
                for (int i = 0; i < arr.nDuizi; i++) {
                    if (idx < n) {
                        tail[2 * i] = arr.duizi[i];
                        tail[2 * i + 1] = arr.duizi[i];
                        idx++;
                    } else {
                        break;
                    }
                }
            }
            if (force) {
                if (idx < n && arr.nSantiao >= n - idx) {
                    for (int i = 0; i < n - idx; i++) {
                        tail[2 * idx] = arr.santiao[i];
                        tail[2 * idx + 1] = arr.santiao[i];
                        idx++;
                    }
                }
            }
            if (idx < n) {
                return false;
            }
            out.setTail(tail);
        }

        return true;
    }

    default int[] findAllDuiziOrDan(int[] cards, CardType type) {
        int m = type == CardType.DAN ? 0 : 1;
        int[] r = new int[15];
        int n = 0;
        for (int i = 0; i < 13; i++) {
            if (cards[i] > m) {
                r[n] = i;
                n++;
            }
        }
        return r;
    }


    public static void removeCard(int[] cards, int[] alreadyCards, OutCard outCard, boolean remove) {
        if (outCard == null) {
            return;
        }

        CardType type = outCard.getType();
        int[] v = outCard.getCards();
        int a = remove ? 1 : -1;
        switch (type) {
            case DAN: {
                cards[v[0]] -= a;
                alreadyCards[v[0]] += a;
                break;
            }
            case DUI: {
                cards[v[0]] -= a * 2;
                alreadyCards[v[0]] += a * 2;
                break;
            }
            case SHUNZI: {
                for (int i = 0; i < v[1]; i++) {
                    cards[v[0] - i] -= a;
                    alreadyCards[v[0] - i] += a;
                }
                break;
            }
            case LIANDUI: {
                for (int i = 0; i < v[1]; i++) {
                    cards[v[0] - i] -= a * 2;
                    alreadyCards[v[0] - i] += a * 2;
                }
                break;
            }
            case SANTIAO:
            case SANTIAOWITHTAIL: {
                cards[v[0]] -= a * 3;
                alreadyCards[v[0]] += a * 3;
                if (outCard.getTail() != null) {
                    for (int i : outCard.getTail()) {
                        cards[i] -= a;
                        alreadyCards[i] += a;
                    }
                }
                break;
            }
            case HUOJIAN: {
                cards[13] -= a;
                cards[14] -= a;
                alreadyCards[13] += a;
                alreadyCards[14] += a;
                break;
            }
            case FEIJI:
            case FEIJIWITHTAIL: {
                for (int i = 0; i < v[1]; i++) {
                    cards[v[0] - i] -= a * 3;
                    alreadyCards[v[0] - i] += a * 3;
                }
                if (type == CardType.FEIJIWITHTAIL) {
                    if (outCard.getTail() != null) {
                        for (int i : outCard.getTail()) {
                            cards[i] -= a;
                            alreadyCards[i] += a;
                        }
                    }
                }
                break;
            }
            case ZHADAN:
            case ZHADANWITHTAIL: {
                cards[v[0]] -= a * 4;
                alreadyCards[v[0]] += a * 4;
                if (outCard.getTail() != null) {
                    for (int i : outCard.getTail()) {
                        cards[i] -= a;
                        alreadyCards[i] += a;
                    }
                }
                break;
            }
            default:
                throw new RuntimeException(outCard.toString());
        }

    }

    default double biggestProbability(int role, int[] remainingCards, int[] remainingCardNum, OutCard outCard) {
        return biggestProbability(role, remainingCards, remainingCardNum, outCard, true);
    }

    default double biggestProbability(int role, int[][] playerCards, int[] remainingCardNum, OutCard outCard) {
        if (role == 0){
            for (int i = 1; i < 3; i++) {
                if (findBiggerCards(playerCards[i], remainingCardNum[i], outCard, true, true).size() > 0){
                    return 0;
                }
            }
        } else {
            if (findBiggerCards(playerCards[0], remainingCardNum[0], outCard, true, true).size() > 0){
                return 0;
            }
        }
        return 1;
    }

    default double biggestProbability(int role, int[] remainingCards, int[] remainingCardNum, OutCard outCard, boolean force) {

        double p = 0.0;
        int max = 0;
        if (role == 0) {
            //地主时
            int num1 = remainingCardNum[1];
            int num2 = remainingCardNum[2];
            max = Math.max(num1, num2);
            p = max * 1.0 / (num1 + num2);

        } else {
            max = remainingCardNum[0];
            int num2 = remainingCardNum[outCard.getRole()];
            p = max * 1.0 / (max + num2);
        }

        List<OutCard> bigger = findBiggerCards(remainingCards, max, outCard, true, true);
        if (bigger == null || bigger.size() == 0) {
            return 1;
        }
        double prop = 1;
        for (OutCard card : bigger) {
            double tp = 1;
            boolean isLian = false;
            int k = 1;

            switch (card.getType()) {
                case DUI: {
                    k = 2;
                    break;
                }
                case SANTIAOWITHTAIL:
                case SANTIAO: {
                    k = 3;
                    break;
                }
                case ZHADANWITHTAIL:
                case ZHADAN: {
                    k = 4;
                    break;
                }
                case DAN: {
                    break;
                }
                case HUOJIAN: {
                    k = 2;
                    break;
                }
                case SHUNZI: {
                    k = 1;
                    isLian = true;
                    break;
                }
                case LIANDUI: {
                    k = 2;
                    isLian = true;
                    break;
                }
                case FEIJIWITHTAIL:
                case FEIJI: {
                    k = 3;
                    isLian = true;
                    break;
                }
                default:
                    break;
            }
            if (card.getType() == CardType.HUOJIAN) {
                tp = p * p;
            } else {
                if (isLian) {
                    for (int i = 0; i < card.getCards()[1]; i++) {
                        tp *= binomial(role, k, remainingCards[card.getCards()[0] - i], p);
                    }
                } else {
                    tp = binomial(role, k, remainingCards[card.getCards()[0]], p);
                }
            }

            prop *= (1.0 - tp);
        }
//        return 1.0 - prop;
        return prop;
    }

    /**
     * @param role             我的身份
     * @param remainingCards   剩余的牌
     * @param remainingCardNum 剩余的牌的数量
     * @param outCard          要接的牌
     * @return 概率
     */

    double[][] binomial_matrix = {{0.0, 0.0, 0.0, 0.0, 0.0}, {1.0, 1.0, 0.0, 0.0, 0.0}, {1.0, 2.0, 1.0, 0.0, 0.0}, {1.0, 3.0, 3.0, 1.0, 0.0}, {1.0, 4.0, 6.0, 4.0, 1.0}};


    /**
     * 计算牌出现的概率
     *
     * @param role 当前角色未知
     * @param k    最少出现几张
     * @param n    还剩几张
     * @param p    单次出现概率
     * @return
     */
    private double binomial(int role, int k, int n, double p) {
        if (k < 0 || n < 0 || k > n) {
            return 0;
        }
        double prop = 0.0;
        for (int i = k; i <= n; i++) {
            prop += binomial_matrix[n][i] * Math.pow(p, i) * Math.pow(1 - p, n - i);
        }
        return prop;
    }


    Random R = new Random();

    public static double randomFloat() {

        return R.nextDouble();
    }

    public static void removeFrom(int[] toDel, int[] from, boolean remove) {
        int a = remove ? -1 : 1;
        for (int i = 0; i < toDel.length; i++) {
            from[toDel[i]] += a;
        }

    }

    /**
     *
     * @param round
     * @param role
     * @param o
     * @param enemyCards
     * @param alreadyCards
     * @param remainCardNum
     * @return 0 表示危险  1 表示很好 2  表示一般
     */
    default int letEnemyWin(int round,int role, OutCard o, int[][] enemyCards, int[] alreadyCards, int[] remainCardNum) {

        if (role == 0) {
            //地主时，判断改出牌是否会让敌人赢牌
            for (int i = 1; i < 3; i++) {
                Player p = new Player(enemyCards[i], i, 1);
                Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, true);
                remainCardNum[0] -= o.getLength();
                OutCard tmpOut = p.out(round, remainCardNum, alreadyCards, o);
                Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, false);
                remainCardNum[0] += o.getLength();
                if (tmpOut != null && tmpOut.getMode().getIndex() > 7) {
                    o.setDangerLevel(tmpOut.getMode().getIndex());
                    return 0;
                }
            }
        } else if (role == 1){
            Player p = new Player(enemyCards[2], 2, 1);
            Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, true);
            remainCardNum[1] -= o.getLength();
            OutCard tmpOut = p.out(round, remainCardNum, alreadyCards, o);
            if (tmpOut != null && tmpOut.getMode().getIndex() > 7) {
                o.setFitLevel(tmpOut.getMode().getIndex());
                Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, false);
                remainCardNum[1] += o.getLength();
                return 1;
            } else if (tmpOut == null){
                p = new Player(enemyCards[0], 0, 1);
                if (tmpOut != null && tmpOut.getMode().getIndex() > 7) {
                    o.setDangerLevel(tmpOut.getMode().getIndex());
                    Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, false);
                    remainCardNum[1] += o.getLength();
                    return 0;
                }
            }
            Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, false);
            remainCardNum[1] += o.getLength();
        }else {
            Player p = new Player(enemyCards[0], 0, 1);
            Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, true);
            remainCardNum[2] -= o.getLength();
            OutCard tmpOut = p.out(round, remainCardNum, alreadyCards, o);
            if (tmpOut != null && tmpOut.getMode().getIndex() > 7) {
                o.setDangerLevel(tmpOut.getMode().getIndex());
                Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, false);
                remainCardNum[2] += o.getLength();
                return 0;
            } else {
                p = new Player(enemyCards[1], 1, 1);
                tmpOut = p.out(round, remainCardNum, alreadyCards, o);
                Strategy.removeCard(EMPTY_CARDS, alreadyCards, o, false);
                remainCardNum[2] += o.getLength();
                if (tmpOut != null && tmpOut.getMode().getIndex() > 7) {
                    o.setFitLevel(tmpOut.getMode().getIndex());
                    return 1;
                }
            }


        }
        return 2;
    }

    public static void main(String[] args) {
        int[] cards = {0, 3, 2, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0};
        int[] remainsCards = {2, 0, 0, 0, 0, 0, 2, 1, 2, 3, 0, 1, 1, 1, 1};
        int[] remainingCardNum = {8, 7, 11};
        OutCard o = OutCard.dan(12);
        o.setRole(2);
        OutCardStrategy strategy = new OutCardStrategy(6);

        OutCard o2 = OutCard.santiaoWithTail(1, new int[]{2, 2});
        OutCard o3 = OutCard.shunzi(new int[]{11, 5});
        double bp = strategy.biggestProbability(2, remainsCards, remainingCardNum, o);
        System.out.println(bp);
        System.out.println(strategy.biggestProbability(2, remainsCards, remainingCardNum, o2, false));
        System.out.println(strategy.biggestProbability(2, remainsCards, remainingCardNum, o3, false));
        Player p = new Player(cards, 2, 1);
        strategy.remainingCardsExceptMe(cards, remainsCards);
        OutCard out = p.out(6, remainingCardNum, strategy.remainingCardsExceptMe(cards, remainsCards), o);
        System.out.println(out);
        System.out.println(out.getMode());
    }
}
