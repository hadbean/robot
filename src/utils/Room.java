package utils;

import constant.CardType;
import model.CardArray;
import model.OutCard;
import model.Player;
import strategy.Strategy;
import test.CardSet;

public class Room {

    static int[] CARDS = new int[]{4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 1, 1};

    private int[] alreadyOutCards;
    private int[] remainingCardNum;


    private Player[] players;

    public void init(int[] alreadyOutCards, int[] remainingCardNum, Player... ps) {
        this.alreadyOutCards = alreadyOutCards;
        this.remainingCardNum = remainingCardNum;
        this.players = ps;
    }

    public void init() {
        alreadyOutCards = new int[15];
        remainingCardNum = new int[]{20, 17, 17};
        players = new Player[3];
        CardSet set = new CardSet();
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

    }

    public int play(boolean debug) {
        CardSplit split = new CardSplit();
        OutCard outCard = null;
        int round = 0;
        String cardStr = cardDesc(players[0], null) + "\n" + cardDesc(players[1], null) + "\n" + cardDesc(players[2], null) + "\n";
        try {
            Player p = players[round % 3];
            outCard = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard);
            if (outCard.getType() == CardType.ZHADAN){
                throw new RuntimeException("出牌混乱");
            }
            System.out.println(cardDesc(p, outCard));
            Strategy.removeCard(p.getCards(), alreadyOutCards, outCard, true);
            remainingCardNum[p.getRole()] -= outCard.getLength();
            round++;


            while (true) {
                p = players[round % 3];
                if (!debug) {
                    OutCard tmp0 = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard);
                    System.out.print((round / 3) +"准备出牌：" + cardDesc(p, tmp0));

                    split.setRound(round / 3);
                    CardArray rs = split.split(p.getCards());
                }


                OutCard tmp = p.out(round / 3, remainingCardNum, alreadyOutCards, outCard);
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
        int i = 1000000;
        while (i > 0) {
            room.init();
            wins[room.play(true)] += 1;

            System.out.println("\n\n");
            i--;
            System.out.println(i);
        }
        System.out.println("地主：" + wins[0] + "胜; 下家：" + wins[1] + "胜; 上家：" + wins[2] + "胜");
        System.out.println(System.currentTimeMillis() - begin);

    }
}
