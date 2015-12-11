package cz.cuni.mff.vojta.mobile.utils;

import cz.cuni.mff.vojta.mobile.models.PractiseModel;

/**
 * Created by vojta on 6. 11. 2015.
 */
public interface IModelListener<Model> {

    public void onPrepared(Model p);
}
