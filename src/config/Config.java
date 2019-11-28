package config;

import constant.CardType;

import java.util.HashMap;
import java.util.Map;

public class Config {

    /**
     * 我是农民时
     */

    public static final double[] IF1 = {0,0.8,0.5};

    public static final String IF1_EXP = "队友的牌多小时,我才接";

    public static final double IF2 = 0.9;
    public static final String IF2_EXP = "我接队友的牌，我的牌小于多少,我才接";


    public static final int MIN_EXCEPT_1 = 10;

    public static final String MIN_EXCEPT_1_EXP = "队友在我下家，且只剩一张牌是，手牌存在小于几的牌时，才强行接地主的牌";



    public static final int MIN_EXCEPT_2 = 9;
    public static final String MIN_EXCEPT_2_EXP = "队友在我下家，且只剩一张牌是，手牌存在小于几的牌时，才强行炸队友的牌";

    public static final int MAX_ACCEPT_LOSS = 9;
    public static final String MAX_ACCEPT_LOSS_EXP = "结牌之后允许的最大变差范围";

    public static final int[] PN = {6,6,6,5,5,5,4,4,4,3,3,3,2,2,2,1,0,0,0,0};

    public static final int REF = 7;

    public static final int MIN_CARD_IGNORE = 12;
    public static final String MIN_CARD_IGNORE_EXP = "最小可以忽略的牌";

    public static final int GOOD_CARDS_VALUE = 10;
    public static final String GOOD_CARDS_VALUE_EXP = "几分算好牌";

    public static final int MAX_CARDS_SPAN = 10;
    public static final String MAX_CARDS_SPAN_EXP = "我接的牌比对方大几分";


    /**
     * 我是地主时
     */

    public static final double RECEIVE_AND_WIN = 0.88;
    public static final String RECEIVE_AND_WIN_EXP = "当接完牌之后,有多大概率稳赢,则接,一般在处理炸弹的时候用到";


    public static final double FOOL_ACTION = 0.05;
    public static final String FOOL_ACTION_EXP = "玩家犯傻概率，随意出牌";

    public static final double SMALL_CARD = 0.7;
    public static final String SMALL_CARD_EXP = "牌多小时算小牌";


    public static final Map<CardType,Double> SMALL_CARD_MAP = new HashMap<>();
    public static final Map<CardType,Double> SMALL_CARD_MAP_EXP = new HashMap<>();
    static {
        SMALL_CARD_MAP.put(CardType.FEIJI, 0.6);
        SMALL_CARD_MAP.put(CardType.FEIJIWITHTAIL, 0.65);
        SMALL_CARD_MAP.put(CardType.ZHADAN, 0.65);
        SMALL_CARD_MAP.put(CardType.ZHADANWITHTAIL, 0.65);
        SMALL_CARD_MAP.put(CardType.DAN, 0.85);
        SMALL_CARD_MAP.put(CardType.DUI, 0.80);
        SMALL_CARD_MAP.put(CardType.SANTIAOWITHTAIL, 0.80);
        SMALL_CARD_MAP.put(CardType.SANTIAO, 0.80);
        SMALL_CARD_MAP.put(CardType.LIANDUI, 0.60);
        SMALL_CARD_MAP.put(CardType.SHUNZI, 0.85);
        SMALL_CARD_MAP.put(CardType.HUOJIAN, 0d);

    }






}
