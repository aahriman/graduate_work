package cz.cuni.mff.vojta.mobile.models;

import android.database.Cursor;

/**
 * Created by vojta on 1. 10. 2015.
 */
public class StatisticsModel {
    public static final String POSITION_IDENTIFIER = "position";
    public static final String SCORE_IDENTIFIER = "score";
    public static final String NICKNAME_IDENTIFIER = "nickname";

    public static final StatisticsModel SINGLETON = new StatisticsModel();

    public Cursor getStatisticsTable(){
        return null;
    }
}
