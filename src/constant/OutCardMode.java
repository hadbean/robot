package constant;

public enum OutCardMode {

    //最大模式
    ALLBIG(9,"allBig"),

    //小牌先行
    SMALLFIRST(1,"smallFirst"),

    //一手出完
    ONEHAND(10,"oneHand"),
    JIEANDWIN(8,"jieAndWin"),
    ZHAANDWIN(9,"zhaAndWin"),
    //forceOnlyOne
    FORCEONLYONE(2,"forceOnlyOne"),

    //长牌优先
    SMALLANDLONGFIRST(3,"smallAndLongFirst"),

    //长牌优先 letFriend
    LETFRIEND(4,"letFriend"),
    OTHER(0, "other"),

    ENEMYLASTONE(5,"enemyLastOne");


    private int index;
    private String name;
    OutCardMode(int index,String name) {
        this.index =index;
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
