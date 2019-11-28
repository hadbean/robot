package model;

import constant.CardType;

/**
 * 出牌类型定义
 */
public class OutCard {

    public static final String[] POKE = {"3","4","5","6","7","8","9","10","J","Q","K","A","2","JOKER1","JOKER2"};

    private int[] cards;
    private int[] tail;
    private int role;
    private CardType type;
    private String mode;
    private double bp;


    private int score = 0;


    public OutCard(int[] cards, CardType type) {
        this.cards = cards;
        this.type = type;
    }

    public void setTail(int[] tail) {
        this.tail = tail;
    }

    public int[] getCards() {
        return cards;
    }

    public void setCards(int[] cards) {
        this.cards = cards;
    }

    public int[] getTail() {
        return tail;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public double getBp() {
        return bp;
    }

    public void setBp(double bp) {
        this.bp = bp;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        switch (type) {
            case DAN: {
                return POKE[cards[0]];
            }
            case DUI: {
                return POKE[cards[0]] +"," + POKE[cards[0]];
            }
            case SANTIAO:
            case SANTIAOWITHTAIL: {
                String s = POKE[cards[0]] +"," + POKE[cards[0]] +"," + POKE[cards[0]] +",";
                if (type == CardType.SANTIAOWITHTAIL) {
                    for (int i = 0; i < tail.length; i++) {
                        s += POKE[tail[i]] +",";
                    }
                }

                return s;
            }

            case FEIJI:
            case FEIJIWITHTAIL: {
                String s = "";
                for (int i = cards[1] - 1; i >= 0; i--) {
                    s += POKE[cards[0] - i ] + ",";
                    s += POKE[cards[0] - i ] + ",";
                    s += POKE[cards[0] - i ] + ",";
                }
                if (type == CardType.FEIJIWITHTAIL) {
                    for (int i = 0; i < tail.length; i++) {
                        s += POKE[tail[i]] +",";
                    }
                }
                return s;

            }
            case LIANDUI: {
                String s = "";
                for (int i = cards[1] - 1; i >= 0; i--) {
                    s += POKE[cards[0] - i ] +",";
                    s += POKE[cards[0] - i ] +",";
                }
                return s;
            }
            case SHUNZI: {
                String s = "";
                for (int i = cards[1] - 1; i >= 0; i--) {
                    s += POKE[cards[0] - i] +",";
                }
                return s;
            }
            case ZHADAN:
            case ZHADANWITHTAIL: {
                String s = "";
                s += POKE[cards[0]] +",";
                s += POKE[cards[0]] +",";
                s += POKE[cards[0]] +",";
                s += POKE[cards[0]] +",";
                if (type == CardType.ZHADANWITHTAIL) {
                    for (int i = 0; i < tail.length; i++) {
                        s += POKE[tail[i]] +",";
                    }
                }
                return s;
            }
            case HUOJIAN:{
                return POKE[13] +"," + POKE[14];
            }

            default:
                throw new RuntimeException("未知出牌类型" + type);
        }

    }

    public static OutCard dan(int cards){
        return new OutCard(new int[]{cards},CardType.DAN);
    }
    public static OutCard duizi(int cards){
        return new OutCard(new int[]{cards,2},CardType.DUI);
    }
    public static OutCard shunzi(int[] cards){
        return new OutCard(cards,CardType.SHUNZI);
    }
    public static OutCard liandui(int[] cards){
        return new OutCard(cards,CardType.LIANDUI);
    }
    public static OutCard santiao(int cards){
        return new OutCard(new int[]{cards},CardType.SANTIAO);
    }
    public static OutCard santiaoWithTail(int cards, int[] tail){
        OutCard out = new OutCard(new int[]{cards},CardType.SANTIAOWITHTAIL);
        out.setTail(tail);
        return out;
    }
    public static OutCard feiji(int[] cards){
        return new OutCard(cards,CardType.FEIJI);
    }
    public static OutCard feijiWithTail(int[] cards, int[] tail){
        OutCard out = new OutCard(cards,CardType.FEIJIWITHTAIL);
        out.setTail(tail);
        return out;
    }
    public static OutCard zhadan(int cards){
        return new OutCard(new int[]{cards},CardType.ZHADAN);
    }
    public static OutCard zhadanWithTail(int cards, int[] tail){
        OutCard out = new OutCard(new int[]{cards},CardType.ZHADANWITHTAIL);
        out.setTail(tail);
        return out;
    }
    public static OutCard huojian(){
        return new OutCard(new int[]{13,14},CardType.HUOJIAN);
    }

    public int getLength() {

        switch (type){
            case ZHADANWITHTAIL:{
                return 4 + tail.length;
            }
            case ZHADAN:{
                return 4;
            }
            case DUI:
            case HUOJIAN:{
                return 2;
            }
            case FEIJI:{
                return cards[1] * 3;
            }
            case FEIJIWITHTAIL:{
                return cards[1] * 3 + tail.length;
            }
            case SANTIAO:{
                return 3;
            }
            case SANTIAOWITHTAIL:{
                return 3 + tail.length;
            }
            case LIANDUI:{
                return cards[1] * 2;
            }
            case SHUNZI:{
                return cards[1];
            }
            case DAN:{
                return 1;
            }
            default: throw new RuntimeException("未知牌型" + type);
        }
    }

    public static void main(String[] args) {
        System.out.println(OutCard.feijiWithTail(new int[]{5,2},new int[]{0,0,1,1}));
        System.out.println(OutCard.shunzi(new int[]{5,5}));
        System.out.println(OutCard.dan(1));
        System.out.println(OutCard.duizi(1));
        System.out.println(OutCard.liandui(new int[]{5,5}));
        System.out.println(OutCard.huojian());
    }


}
