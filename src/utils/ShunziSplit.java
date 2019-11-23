package utils;

public class ShunziSplit {


    public static void split(int[][] shunzis, int[] cards, int m) {

        //合并顺子，让顺子尽量长
//        for (int i = 1; i < cards.length - 3; i++) {
//            if (cards[i] > 0) {
//                for (int j = 0; j < m; j++) {
//                    int[] shunzi = shunzis[j];
//                    if (shunzi[0] - shunzi[1] == i || shunzi[0] == i - 1) {
//                        cards[i] = cards[i] - 1;
//                        shunzi[1] = shunzi[1] + 1;
//                        if (shunzi[0] == i - 1) {
//                            shunzi[0] = shunzi[0] + 1;
//                        }
//                        break;
//                    }
//                }
//            }
//        }
        for (int i = 0; i < m; i++) {
            int[] shunzi = shunzis[i];
            for (int j = 0; j < cards.length - 3; j++) {
                if (cards[j] > 0&&(shunzi[0] - shunzi[1] == j || shunzi[0] == j - 1)){
                    cards[j] -= 1;
                    shunzi[1] = shunzi[1] + 1;
                    if (shunzi[0] == j - 1) {
                        shunzi[0] += 1;
                    }
                }
            }

        }
        //拆出连对
        int n = 0;
        for (int i = 0; i < cards.length - 5; i++) {
            if (cards[i] > 0 && cards[i + 1] > 0 && cards[i + 2] > 0) {
                for (int j = 0; j < m; j++) {
                    int[] shunzi = shunzis[j];
                    if (shunzi[0] >= i + 2 && i > shunzi[0] - shunzi[1]) {
                        int s1 = isBetter(shunzi, cards[i], cards[i + 1], cards[i + 2]);
                        if (s1 == 99) {
                            shunzi[1] = shunzi[1] - 3;
                            cards[i] = cards[i] + 1;
                            cards[i + 1] = cards[i + 1] + 1;
                            cards[i + 2] = cards[i + 2] + 1;
                            if (shunzi[0] == cards[i + 2]) {
                                shunzi[0] = shunzi[0] - 3;
                            }
                        }
                        if (s1 < 0) {
                            if (shunzi[1] > 8) {
                                shunzi[1] = shunzi[1] - 4;
                                cards[i] = cards[i] + 1;
                                cards[i + 1] = cards[i + 1] + 1;
                                cards[i + 2] = cards[i + 2] + 1;
                                if (shunzi[0] - 1 == cards[i + 2]) {
                                    shunzi[0] = shunzi[0] - 4;
                                    cards[i + 3] = cards[i + 3] + 1;
                                } else {
                                    cards[i - 1] = cards[i - 1] + 1;
                                }
                            } else {
                                for (int k = 0; k < shunzi[1]; k++) {
                                    cards[shunzi[0] - k] = cards[shunzi[0] - k] + 1;
                                }
                                shunzi[0] = 0;
                                shunzi[1] = 0;
                                n++;
                            }
//                            if (s1 == -999 && cards[i + 3] > 0 && shunzi[0] >= i + 3) {
//                                if (isBetter(shunzi, cards[i], cards[i + 1], cards[i + 2], cards[i + 3]) < 0) {
//
//                                }
//                            }
                        }
                    }
                }

            }

        }

        //拆出3长
        for (int i = 0; i < 4; i++) {
            int[] shunzi = shunzis[i];
            if (shunzi[1] > 5) {

            }
        }

    }


    private static int isBetter(int[] shunzi, int... x) {
        //修改前分数
        int a = Math.max(0, (shunzi[0] - 7) / 2);
        for (int i = 0; i < x.length; i++) {
            a += (x[i] - 7);
        }

        //修改后分数
        int b = Math.max(0, (x[x.length - 1] - 7) / 2);
        if (shunzi[1] - x.length > 4 && (shunzi[0] - x[x.length - 1] > 4 || x[0] - shunzi[0] - shunzi[1] + 1 > 4)) {
            if (shunzi[0] == x[x.length - 1] || shunzi[0] - shunzi[1] + 1 == x[0]) {
                return 99;
            } else {
                if (shunzi[0] - x[x.length - 1] > 4) {
                    b += Score.max01(shunzi);
                    b += Score.single(x[0] - 1 - 7);
                } else {
                    b += Score.max01(new int[]{x[0] - 1, shunzi[1] - 3});
                    b += Score.single(shunzi[0]);
                }
                return b - a;
            }
        } else {
            for (int i = 0; i < shunzi[1]; i++) {
                if (shunzi[0] - i > x[x.length - 1] && shunzi[0] - i < x[0]) {
                    b += (shunzi[0] - i - 7);
                }
            }
        }
        return b - a;
    }

    public static void prolong(int[][] shunzis, int[] cards){
        for (int i = 0; i < shunzis.length; i++) {
            int[] shunzi = shunzis[i];

            if (shunzi[0] == 0 || shunzi[0] - shunzi[1] > 5){
                continue;
            }
            if (shunzi[0] < 7 && cards[shunzi[0] + 1] == 2){
                cards[shunzi[0] + 1] -= 1;
                shunzi[0] += 1;
                shunzi[1] += 1;
            }
            if (shunzi[0] - shunzi[1] > 0 && cards[shunzi[0] - shunzi[1]] == 2){
                cards[shunzi[0] - shunzi[1]] -= 1;
                shunzi[1] += 1;
            }
        }
    }

    public static void merge(int[][] shunzis){
        for (int i = 0; i < shunzis.length - 1; i++) {
            int[] shunzi1 =shunzis[i];
            if (shunzi1[0] == 0){
                continue;
            }
            for (int j = i+1; j < shunzis.length; j++) {
                int[] shunzi2 =shunzis[j];
                if (shunzi2[0] == 0){
                    continue;
                }
                if (shunzi1[0] == shunzi2[0] - shunzi2[1]){
                    shunzi1[0] = shunzi2[0];
                    shunzi1[1] += shunzi2[1];
                    shunzi2[0] = 0;
                    shunzi2[1] = 0;
                } else if (shunzi2[0] == shunzi1[0] - shunzi1[1]){
                    shunzi1[1] += shunzi2[1];
                    shunzi2[0] = 0;
                    shunzi2[1] = 0;
                }
            }
        }
    }
}
