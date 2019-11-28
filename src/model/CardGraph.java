package model;

import constant.CardType;
import utils.CardComparator;

import java.util.List;

public class CardGraph {

    public int[][] sShunzi= new int[4][2];
    public int nsShunzi;

    public int[][] bShunzi= new int[4][2];
    public int nbShunzi;


    public int[] bSaotiao= new int[6];
    public int nbSaotiao;

    public int[] sSaotiao= new int[6];
    public int nsSaotiao;



    public int[][] bLiandui = new int[3][2];
    public int nbLiandui;

    public int[][] sLiandui = new int[3][2];
    public int nsLiandui;

    public int[] sDan= new int[15];
    public int nsDan;

    public int[] bDan= new int[15];
    public int nbDan;

    public int[] sDuizi = new int[10];
    public int nsDuizi;

    public int[] bDuizi = new int[10];
    public int nbDuizi;

    public int extra;

    public int big;
    public int small;
    public int nEr;
    public int nFeiji;

    public boolean hasBigDan2;
    public boolean hasBigDui2;
    public boolean hasBigSan2;
    public int nHuojian;
    public int nZhadan;


    public List<OutCard> allBig(CardArray rs){

        int remainDan = Math.max(0,nsDan - nbDan);
        int remainDui = Math.max(0,nsDuizi - nbDuizi);
        int remainSan = Math.max(0,nsSaotiao - nbSaotiao);

        int n = remainDan + remainDui;
        if (rs.nSantiao > 0){
            if (remainSan >= rs.nSantiao){
                n -= remainSan;
            }else if (remainDui >= rs.nSantiao || remainDui * 2 == rs.nSantiao){
                n -= remainSan;
            }
        }

        return null;
    }

}
