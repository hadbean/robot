package utils;

import model.CardArray;

public class CardSplit {

    private int round;

    public void setRound(int round){
        this.round = round;
    }
    public CardArray split(int[] cards, boolean lessDan) {
        return split(cards,1 , lessDan, false);
    }

    public CardArray split(int[] cards) {
        return split(cards,1 , false, false);
    }

    /**
     * @param handCards
     * @param type      玩家类型 0 地主 1 地主下家 2 地主上家
     * @return
     */
    public CardArray split(int[] handCards,int type, boolean lessDan, boolean cheat) {


        CardArray rs = new CardArray();
        rs.round = round;
        rs.cards = handCards;
        int[] cards = new int[handCards.length];
        System.arraycopy(handCards, 0, cards, 0, cards.length);
        int length = cards.length;
        //找火箭
        if (cards[13] == 1 && cards[14] == 1) {
            rs.huojian[0] = 1;
            cards[13] = 0;
            cards[14] = 0;
            rs.addUsage(2);
            rs.nHuojian++;
        }
        //找炸弹
        for (int i = 0; i < length - 2; i++) {
            if (cards[i] == 4) {
                rs.zhadan[rs.nZhadan] = i;
                cards[i] = 0;
                rs.addUsage(4);
                rs.nZhadan++;
            }
        }
        //找 2,如果2 大于1张
        if (cards[12] > 1) {
            rs.er[0] = cards[12];
            rs.nEr = cards[12];
            cards[12] = 0;
        }
        //找飞机;为了方便处理，只处理长度 <= 3的飞机
        int n = 1;
        int index = -1;
        int m = 0;
        for (int i = 0; i < length - 4; i++) {
            if (cards[i] == 3 && cards[i + 1] == 3) {
                n++;
                index = i + 1;
            } else if (n > 1) {
                rs.feiji[m][0] = index;
                rs.feiji[m][1] = n;
                rs.nFeiji++;
                m++;
                for (int j = 0; j < n; j++) {
                    cards[index - j] -= 3;
                }
                n = 1;
                index = -1;
                rs.addUsage(n * 3);
            }
        }


        //拆顺子，把最大的顺子分别拆出来
        n = 0;
        m = 0;
        for (int i = 0; i < length - 2; i++) {
            if (cards[i] > 0 && i < 12) {
                n++;
            } else if (n > 4) {
                rs.shunzi[m][0] = i - 1;
                rs.shunzi[m][1] = n;
                for (int j = 1; j <= n; j++) {
                    cards[i - j] -= 1;
                }
                rs.nShunzi++;
                i = i - n - 1;
                n = 0;
                m++;
            } else {
                n = 0;
            }

        }
        if (rs.nShunzi > 0) {
            splitShunzi(rs, cards, m);
        }
//        ShunziSplit.split(rs.shunzi, cards, m);
        //找连对
        n = 1;
        m = 0;
        index = 0;
        for (int i = 0; i < length - 4; i++) {
            if (cards[i] == 2 && cards[i + 1] == 2) {
                n++;
                index = i + 1;
            } else if (n > 2) {
                rs.liandui[m][0] = index;
                rs.liandui[m][1] = n;
                for (int j = 0; j < n; j++) {
                    cards[index - j] -= 2;
                }
                rs.nLiandui++;
                n = 1;
                m++;
            } else {
                n = 1;
            }
        }

        //拆连对的三条
        for (int i = 0; i < m; i++) {
            int[] liandui = rs.liandui[m];
            if (liandui.length > 3) {
                if (cards[liandui[0]] == 1) {
                    cards[liandui[0]] += 2;
                    liandui[0] -= 1;
                    liandui[1] -= 1;
                } else if (cards[liandui[0] - liandui[1] + 1] == 1) {
                    cards[liandui[0] - liandui[1] + 1] += 2;
                    liandui[1] -= 1;
                }
            }
        }

        //找三张
        for (int i = 0; i < length - 3; i++) {
            if (cards[i] == 3) {
                rs.santiao[rs.nSantiao] = i;
                cards[i] -= 3;
                rs.nSantiao++;
            }
        }
        //延长顺子
        if (rs.nShunzi > 0) {
            if (!lessDan) {
                ShunziSplit.prolong(rs.shunzi, cards);
            }
            //合并顺子
            if (rs.nShunzi > 1) {
                ShunziSplit.merge(rs);
            }
        }
        //找出对子和单
        for (int i = 0; i < length; i++) {
            if (cards[i] > 0) {
                if (cards[i] == 1) {
                    rs.dan[rs.nDan] = i;
                    rs.nDan++;
                    cards[i] -=1;
                } else {
                    rs.duizi[rs.nDuizi] = i;
                    rs.nDuizi++;
                    cards[i] -=2;
                }
            }
        }
        //分裂飞机,4 变成 2,好带牌
        for (int i = 0; i < rs.nFeiji; i++) {
            if (rs.feiji[i][1] == 4){
                rs.feiji[rs.nFeiji][0] = rs.feiji[i][0];
                rs.feiji[rs.nFeiji][1] = 2;
                rs.feiji[i][0] = rs.feiji[i][0] - 2;
                rs.feiji[i][1] = rs.feiji[i][1] - 2;
                rs.nFeiji++;
                break;
            }
        }
        for (int i : cards) {
            if (i != 0){
                throw new RuntimeException("分牌异常");
            }
        }

        return rs;
    }

    private void check(){

    }

    private void splitShunzi(CardArray rs, int[] cards, final int m) {
        int[][] shunzis = rs.shunzi;

        boolean flag = false;
        //顺子把长的顺子分裂
        for (int i = 0; i < rs.nShunzi; i++) {
            int[] shunzi = shunzis[i];
            if (shunzi[1] > 5) {
                // 优先分裂长的对子
                // 01234567  34
                int n = 0;
                int max = 0;
                int idx = 0;
                for (int j = shunzi[0] - shunzi[1] + 2; j < shunzi[0]; j++) {
                    if (cards[j] > 0) {
                        n++;
                    } else {
                        if (n > max) {
                            max = n;
                            idx = j - 1;
                        }
                        n=0;
                    }
                }
                if (max == 0) {
                    break;
                }
                //判断分裂后是否两条顺子都大于5
                if ((idx - shunzi[0] + shunzi[1]) > 4 && (shunzi[0] - idx + max) > 4) {
                    for (int j = 0; j < max; j++) {
                        cards[idx - j] -= 1;
                    }
                    shunzis[rs.nShunzi][0] = shunzi[0];
                    shunzis[rs.nShunzi][1] = shunzi[0] - idx + max;
                    shunzi[1] = shunzi[1] - shunzi[0] + idx;
                    shunzi[0] = idx;
                    rs.nShunzi++;
                    flag = true;
                }
            }
        }

        //拆出连对,拆完后出手数变少了，出手出变少了拆，否则不拆
        for (int i = 0; i < m; i++) {
            int[] shunzi = shunzis[i];
            if (shunzi[1] > 7) {
                int max = shunzi[0];
                int min = max - shunzi[1] + 1;
                //判断变得更好，如果减少了出手数量，则判断为更好
                if (cards[min] > 0 && cards[min + 1] > 0 & cards[min + 2] > 0) {
                    shunzi[1] = shunzi[1] - 3;
                    cards[min] += 1;
                    cards[min + 1] += 1;
                    cards[min + 2] += 1;
                    flag = true;
                } else if (cards[max] > 0 && cards[max - 1] > 0 & cards[max - 2] > 0) {
                    shunzi[0] -= 3;
                    shunzi[1] -= 3;
                    cards[max] += 1;
                    cards[max - 1] += 1;
                    cards[max - 2] += 1;
                    flag = true;
                }
            }
//            else if (shunzi[i] == 5){
//                int max = shunzi[0];
//                int min = max - shunzi[1] + 1;
//                for (int j = min; j < max-2; j++) {
//                    if (cards[j] == 1 && cards[j + 1] == 1 && cards[j + 2] == 1){
//
//
//                    }
//                }
//            }

        }

        // 拆3张
        for (int i = 0; i < m; i++) {
            int[] shunzi = shunzis[i];
            if (shunzi[1] > 5) {
                int max = shunzi[0];
                int min = max - shunzi[1] + 1;
                if (cards[min] == 2) {
                    shunzi[1] -= 1;
                    cards[min] += 1;
                    flag = true;
                } else if (cards[max] == 2) {
                    shunzi[0] -= 1;
                    shunzi[1] -= 1;
                    cards[max] += 1;
                    flag = true;
                }
            }
        }

        //判断是否要把顺子拆了
        for (int i = 0; i < m; i++) {
            int n = 0;
            int[] shunzi = shunzis[i];
            int max = shunzi[0];
            if (max < 5) {
                continue;
            }
            for (int j = 0; j < shunzi[1]; j++) {
                if (cards[max - j] > 0) {
                    n++;
                }
            }
            if (n > shunzi[1] / 2) {
                for (int j = 0; j < shunzi[1]; j++) {
                    cards[max - j] += 1;
                }
                shunzi[0] = 0;
                shunzi[1] = 0;
                rs.nShunzi--;
                flag = true;
            }

        }

        //拆两头对子
        for (int i = 0; i < m; i++) {
            int[] shunzi = shunzis[i];
            if (shunzi[1] == 0) {
                continue;
            }
            if (shunzi[1] > 5 &&(cards[shunzi[0]] == 1 || cards[shunzi[0] + 1 - shunzi[1]] == 1)) {
                if (cards[shunzi[0]] == 1){
                    cards[shunzi[0]] += 1;
                    shunzi[0] -=1;
                }else {
                    cards[shunzi[0] + 1 - shunzi[1]] += 1;
                }
                shunzi[1] -=1;
                flag = true;
            }
        }

        if (flag){
            int[][] newSHunzi = new int[rs.nShunzi*2][2];
            int k = 0;
            for (int i = 0; i < rs.shunzi.length; i++) {
                if (rs.shunzi[i][0] > 0){
                    newSHunzi[k] = rs.shunzi[i];
                    k++;
                }
            }
            rs.shunzi = newSHunzi;
            splitShunzi(rs,cards,rs.nShunzi);
        }

    }


    public static void main(String[] args) {
        CardSplit split = new CardSplit();
    }
}
