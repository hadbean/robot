package model;

import config.Config;
import utils.CardSplit;

public class CardArray {

    public static int[] P = {6, 6, 6, 5, 5, 5, 4, 4, 4, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2};
    public int[] cards;
    public int round;
    private int usage;


    public int[] huojian = new int[1];
    public int nHuojian;
    public int[] zhadan = new int[9];
    public int nZhadan;

    public int[] er = new int[1];
    public int nEr;

    public int[][] feiji = new int[6][2];
    public int nFeiji;

    public int[][] shunzi = new int[8][2];
    public int nShunzi;

    public int[][] liandui = new int[6][2];
    public int nLiandui;

    public int[] santiao = new int[10];
    public int nSantiao;


    public int[] dan = new int[15];
    public int nDan;

    public int[] duizi = new int[12];
    public int nDuizi;


    public int hands = 0;

    public int score() {
        int s = 0;
        int N = 0;

        for (int i = 0; i < nDan; i++) {
            s += dan[i] - Config.REF;
            if (dan[i] < Config.MIN_CARD_IGNORE) {
                N++;
            }
        }
        for (int i = 0; i < nDuizi; i++) {
            int k = duizi[i] - Config.REF;
            if (k > 0) {
                k = 3 * k / 2;
            }
            if (duizi[i] < Config.MIN_CARD_IGNORE) {
                N++;
            }
            s += k;
        }

        if (nSantiao > 0) {
            for (int i = 0; i < nSantiao; i++) {
                int k = Math.max(0, santiao[i] - Config.REF);
                if (nDan > 0 || nDuizi > 0) {
                    k = 3 * k / 2;
                } else {
                    k = 2 * k;
                    N++;
                }
                s += k;
            }

        }
        for (int i = 0; i < nShunzi; i++) {
            s += Math.max(0, (shunzi[i][1] - Config.REF) / 2);
            N++;
        }


        if (nFeiji > 0) {
            for (int i = 0; i < nFeiji; i++) {
                s += Math.max(0, (feiji[i][0] - Config.REF) / 2);
                if (nDan > 1 || nDuizi > 1) {
                    if (nDan > 1) {
                        if (dan[0] < Config.REF) {
                            s -= dan[0] - Config.REF;
                        }
                        if (dan[1] < Config.REF) {
                            s -= dan[1] - Config.REF;
                        }
                    }
                    N--;
                } else {
                    N++;
                }
            }
        }

        for (int i = 0; i < nZhadan; i++) {
            s += 9;
        }
        if (huojian[0] == 1) {
            s += 12;
        }
        s += nEr * 5;
        hands = N;

        return round < P.length ? s - P[round] * N : s;
    }

    public int maxCardNum() {
        int maxCard = nHuojian * 2 + nZhadan + nEr;
        if (nZhadan > 0 && cards[12] == 4){
            maxCard += 4;
        }

        if (nHuojian == 0) {
            maxCard += cards[13];
            maxCard += cards[14];
        }
        if (cards[12] == 1) {
            maxCard += 1;
        }
        maxCard += cards[11] / 2;
        return maxCard;
    }

    public int smallCardNum(int[][] playerCards,int role) {
        int maxCard = nHuojian * 2 + nZhadan + nEr;
        if (nZhadan > 0 && cards[12] == 4){
            maxCard += 4;
        }

        if (nHuojian == 0) {
            maxCard += cards[13];
            maxCard += cards[14];
        }
        if (cards[12] == 1) {
            maxCard += 1;
        }
        maxCard += cards[11] / 2;
        return maxCard;
    }


    public void addUsage(int n) {
        this.usage += n;
    }

    public int getUsage() {
        return usage;
    }

    public void setCards(int[] cards) {
        this.cards = cards;
    }

    public static void main(String[] args) {

        int[] cards = {0, 0, 1, 2, 3, 2, 1, 1, 1, 1, 1, 1, 1, 0, 1};
        CardArray arr = new CardSplit().split(cards);
        System.out.println(arr.maxCardNum());

    }

    @Override
    public String toString() {
        StringBuffer bf = new StringBuffer();
        bf.append(" 评分: <" + score() + ">");
        if (huojian[0] == 1) {
            bf.append("\t火箭: 16, 17\t;");
        }
        bf.append("炸弹: ");

        for (int i = 0; i < nZhadan; i++) {
            bf.append(zhadan[i] + 3);
        }
        bf.append("\t;2: ");
        if (nEr > 1) {
            bf.append(nEr + "; ");
        }
        bf.append("\t;飞机: ");
        for (int i = 0; i < nFeiji; i++) {
            if (feiji[i][1] > 0) {
                bf.append((feiji[i][0] - feiji[i][1] + 4) + "~" + (feiji[i][0] + 3));
            }
        }
        bf.append("\t;顺子: ");
        for (int i = 0; i < nShunzi; i++) {
            if (shunzi[i][1] > 0) {
                bf.append((shunzi[i][0] + 4 - shunzi[i][1]) + "~" + (shunzi[i][0] + 3) + "; ");
            }
        }
        bf.append("\t;连对: ");
        for (int i = 0; i < nLiandui; i++) {
            if (liandui[i][1] > 0) {
                bf.append((liandui[i][0] + 4 - liandui[i][1]) + "~" + (liandui[i][0] + 3) + "; ");
            }
        }
        bf.append("\t;三条: ");
        for (int i = 0; i < nSantiao; i++) {
            bf.append(santiao[i] + 3 + ", ");
        }
        bf.append("\t;对子: ");
        for (int i = 0; i < nDuizi; i++) {
            bf.append(duizi[i] + 3 + ", ");
        }
        bf.append("\t;单: ");
        for (int i = 0; i < nDan; i++) {
            bf.append(dan[i] + 3 + ", ");
        }

        return bf.toString();
    }
}
