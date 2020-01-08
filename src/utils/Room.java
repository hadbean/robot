package utils;

import constant.CardType;
import model.CardArray;
import model.OutCard;
import model.Player;
import strategy.Strategy;

import java.util.Random;

public class Room {

    static int[] CARDS = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1};

    private int[] alreadyOutCards;
    private int[] remainingCardNum;
    static Statistics stat = new Statistics();


    private Player[] players;

    public void init(int[] alreadyOutCards, int[] remainingCardNum, Player... ps) {
        this.alreadyOutCards = alreadyOutCards;
        this.remainingCardNum = remainingCardNum;
        this.players = ps;
    }

    public boolean initJDZ() {
        Random r = new Random();
        alreadyOutCards = new int[15];
        remainingCardNum = new int[]{20, 17, 17};
        players = new Player[3];
        Shuffle shuffle = new Shuffle();
        int[][] cardGroup = shuffle.shuffle();
        int[] dp = new int[3];
        int k = 0;
        for (int i = 0; i < 3; i++) {
            int[] cards = cardGroup[i];
            int n = 0;
            for (int j = 0; j < cards.length; j++) {
                n += cards[j];
            }
            if (n == 20){
                while (k < 3){
                    int idx = r.nextInt(15);
                    if (cards[idx] > 0){
                        dp[k] = idx;
                        cards[idx] --;
                        k ++;
                    }
                }
            }
        }
        players[0] = new Player(cardGroup[0], 0, 1);
        players[1] = new Player(cardGroup[1], 1, 1);
        players[2] = new Player(cardGroup[2], 2, 1);
        int round = r.nextInt(3);
        int s = 0;
        int who = 0;
        for (int i = 0; i < 3; i++) {
            int s2 = players[round % 3].jiaoDiZhu(s,dp);
            if (s2 > s){
                s = s2;
                who = round % 3;
            }
            if (s == 3){
                break;
            }
        }
        stat.n ++;
        if (s > 0) {

            System.out.println(who + "号抢得地主");
            CardSplit split = new CardSplit();
            split.setRound(40);
            CardArray rs = split.split(players[who].getCards());
            System.out.println(rs.score() +" \t" + rs.hands +" \t" + rs.maxCardNum());
            stat.score += rs.score();
            stat.hands += rs.hands;
            stat.maxCard += rs.maxCardNum();
            stat.dzs += s;
            Strategy.removeFrom(dp,players[who].getCards(),false);
            stat.jdz[who] ++;
            if (who != 0){
                Player p = players[who];
                p.setRole(0);
                players[0].setRole(who);
                players[who] = players[0];
                players[0] = p;
            }
            for (int i = 0; i < players.length; i++) {
                if (players[i].jiabei(dp)){
                    stat.jb[i] += 1;
                }
            }
            return true;
        }else {
            Strategy.removeFrom(dp,players[0].getCards(),false);
            return false;
        }
    }

    public boolean init() {
        alreadyOutCards = new int[15];
        remainingCardNum = new int[]{20, 17, 17};
        players = new Player[3];
        Shuffle set = new Shuffle();
        int[][] cardGroup = set.shuffle();
        for (int i = 0; i < 3; i++) {
            int[] cards = cardGroup[i];
            int n = 0;
            for (int j = 0; j < cards.length; j++) {
                n += cards[j];
            }
            if (n == 20) {
                players[0] = new Player(cards, 0, 1);
            } else {
                if (players[1] == null) {
                    players[1] = new Player(cards, 1, 1);
                } else {
                    players[2] = new Player(cards, 2, 1);
                }
            }
        }
        return true;
    }

    public int play(boolean debug) {
        int[] roles = new int[]{0,1,1};
        CardSplit split = new CardSplit();
        OutCard outCard = null;
        int round = 0;
        int[][] playerCards = new int[3][];
        playerCards[0] = players[0].getCards();
        playerCards[1] = players[1].getCards();
        playerCards[2] = players[2].getCards();
        String cardStr = cardDesc(players[0], null) + "\n" + cardDesc(players[1], null) + "\n" + cardDesc(players[2], null) + "\n";
        try {
            Player p = players[round % 3];
            CardArray rss = split.split(p.getCards());
            int score = rss.score();
            int hands = rss.hands;
            int maxNum = rss.maxCardNum();

            outCard = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard,playerCards,roles);
            if (outCard.getType() == CardType.ZHADAN){
                throw new RuntimeException("出牌混乱");
            }
            System.out.println(cardDesc(p, outCard));
            Strategy.removeCard(p.getCards(), alreadyOutCards, outCard, true);
            remainingCardNum[p.getRole()] -= outCard.getLength();
            round++;
            if (isOver(p.getCards())){
                return p.getRole();
            }


            while (true) {
                p = players[round % 3];
                if (!debug) {
                    OutCard tmp0 = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard,playerCards,roles);
//                    OutCard tmp0 = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard);
                    System.out.print((round / 3) +"准备出牌：" + cardDesc(p, tmp0));

                    split.setRound(round / 3);
                }

                CardArray rs = split.split(p.getCards());
                OutCard tmp = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard,playerCards,roles);
//                OutCard tmp = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard);
                if (!debug) {
                    System.out.println("\t实际出牌：" + (tmp == null ? "PASS" : tmp.toString()));
                }

                if (tmp != null) {
                    outCard = tmp;
                    remainingCardNum[p.getRole()] -= outCard.getLength();
                    Strategy.removeCard(p.getCards(), alreadyOutCards, outCard, true);
                    outCard.setRole(p.getRole());
                    for (int i : p.getCards()) {
                        if (i < 0){
                            throw new RuntimeException("出牌异常1");
                        }
                    }
                }
                if (round / 3 > 20) {

                }
                round++;

                if (isOver(p.getCards())) {
                    System.out.println(p.getRole() + "号玩家获胜");

                    for (int i = 0; i < alreadyOutCards.length; i++) {
                        if (alreadyOutCards[i] < 0 || alreadyOutCards[i] > 4 || alreadyOutCards[i] != (CARDS[i] - players[0].getCards()[i] - players[1].getCards()[i] - players[2].getCards()[i])) {
                            System.out.println("already :" + i + "\t" + alreadyOutCards[i] + "\t" + (CARDS[i] - players[0].getCards()[i] - players[1].getCards()[i] - players[2].getCards()[i]));

                            throw new RuntimeException("出牌规则不符合:" + OutCard.POKE[i]);
                        }

                    }
                    int n = 0;
                    for (int i = 0; i < remainingCardNum.length; i++) {
                        if (remainingCardNum[i] < 0) {
                            throw new RuntimeException("牌的数量统计错误:" + OutCard.POKE[i]);
                        }
                        if (remainingCardNum[i] == 0) {
                            n++;
                        }

                    }
                    if (n != 1) {
                        throw new RuntimeException("牌的数量统计错误");
                    }
                    if (p.getRole() == 0){
                        stat.maxCard += maxNum;
                        stat.hands += hands;
                        stat.score += score;
                        stat.n += 1;
                    }

                    return p.getRole();
                }
            }
        } catch (Exception e) {

            System.out.println(cardStr);
            throw e;
        }

    }

    public String cardDesc(Player p, OutCard out) {
        StringBuffer sb = new StringBuffer();
        if (p.getRole() == 0) {
            sb.append("地主:");
        } else if (p.getRole() == 1) {
            sb.append("主下:");
        } else if (p.getRole() == 2) {
            sb.append("主上:");
        }
        for (int i = 0; i < p.getCards().length; i++) {
            int l = p.getCards()[i];
            for (int j = 0; j < l; j++) {
                sb.append(OutCard.POKE[i]).append(",");
            }
        }
        if (out == null) {
            sb.append("\t").append("PASS");
        } else {
            sb.append("\t").append((out.getMode() == null ? "" : "<" + out.getMode() + ">") + out.getType() + ":" + out.toString() + " ");
        }
        return sb.toString();
    }

    public boolean isOver(int[] cards) {
        for (int n : cards) {
            if (n > 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        long begin = System.currentTimeMillis();
        int[] wins = new int[3];
        Room room = new Room();
        int[] call = new int[2];
        int i = 100000;
        while (i > 0) {
//            room.init();
            if (room.initJDZ()) {
                wins[room.play(true)] += 1;
                call[0] ++;
                i --;
            }else {
                call[1] ++;
            }
        }
        System.out.println("JDZ:" + call[0] +"," + call[1] +" p = " + (1.0*call[0]/(call[0] + call[1])));
        System.out.println("地主:" + wins[0] + "胜; 下家:" + wins[1] + "胜; 上家:" + wins[2] + "胜");
        System.out.println(System.currentTimeMillis() - begin);



//        System.out.println(stat.toString());
        System.out.println(stat.n + "\t" + stat.jdz[0]+ "\t" + stat.jdz[1]+ "\t" + stat.jdz[2]);
        System.out.println("加倍\t" + stat.jb[0]+ "\t" + stat.jb[1]+ "\t" + stat.jb[2]);
        System.out.println(stat.n + "\t" + (stat.score/100000)+ "\t" + (stat.hands/100000)+ "\t" + (stat.maxCard/100000)+ "\t" + (stat.dzs/100000));
    }
}
