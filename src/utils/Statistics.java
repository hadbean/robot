package utils;

public class Statistics {


    public int[] jdz = {0,0,0};
    public int[] jb = {0,0,0};

    public int n;
    public int score;
    public int hands;
    public int maxCard;

    public int dzs;


    @Override
    public String toString() {
        return "平均分:" + (score/n) +"\t平均手数:" + (hands/n)  +"\t平均大牌数目:" + (maxCard/n);
    }
}
