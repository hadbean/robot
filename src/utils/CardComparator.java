package utils;

import constant.CardType;

public class CardComparator {

    /**
     * @return -1 小； 0 相等； 1 大于； 3 不能比较
     */
    public static int compareTo(int[] x, int[] y, CardType type) {

        if (x[1] != y[1]) {
            return 3;
        }
        return x[1] < y[1] ? -1 : x[1] == y[1] ? 0 : 1;
    }

}


