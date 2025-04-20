package org.katas;

import org.katas.model.TrainDetails;

public interface IApiCall {
    double getBasePrice(TrainDetails trainDetails);
}
