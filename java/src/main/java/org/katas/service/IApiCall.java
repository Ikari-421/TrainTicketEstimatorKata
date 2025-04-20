package org.katas.service;

import org.katas.model.TrainDetails;

public interface IApiCall {
    double getBasePrice(TrainDetails trainDetails);
}
