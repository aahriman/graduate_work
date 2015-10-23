package cz.cuni.mff.vojta.mobile.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cz.cuni.mff.vojta.mobile.R;
import cz.cuni.mff.vojta.mobile.models.StatisticsModel;

/**
 * Created by vojta on 1. 10. 2015.
 */
public class StatisticsAdapter extends CursorAdapter {

    public StatisticsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context ctx, Cursor c, ViewGroup root) {
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View view = inflater.inflate(R.layout.statistics, root, false);
        bindView(view, ctx, c);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int position = cursor.getColumnIndex(StatisticsModel.POSITION_IDENTIFIER);
        int score = cursor.getColumnIndex(StatisticsModel.SCORE_IDENTIFIER);
        int nickname = cursor.getColumnIndex(StatisticsModel.NICKNAME_IDENTIFIER);

        ((TextView)view.findViewById(R.id.position)).setText(position);
        ((TextView)view.findViewById(R.id.score)).setText(score);
        ((TextView)view.findViewById(R.id.nickname)).setText(nickname);
    }
}
