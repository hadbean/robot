package utils;

public class Score {

    public static int REF = 7;

    public static int max01(int[] shunzi){
        return Math.max(0, (shunzi[0] - REF)/2);
    }

    public static int single(int ...x){
        int s = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 0){
                continue;
            }
            s+=i-REF;
        }
        return s;
    }

    public static int max01WithP(int a,int b,int ...x){
        int s = 0;
        for (int i = 0; i < x.length; i++) {
            if (x[i] == 0){
                continue;
            }
            if (i > REF){
                s = s + (i - REF)*a/b;
            }else{
                s+=i-7;
            }

        }
        return s;
    }

    public static int zhadan(){
        return 9;
    }
    public static int huojian(){
        return 12;
    }
}
