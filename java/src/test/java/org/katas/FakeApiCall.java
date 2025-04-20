package org.katas;

import org.katas.model.TrainDetails;
import org.katas.service.IApiCall;

public class FakeApiCall implements IApiCall {

    @Override
    public double getBasePrice(TrainDetails trainDetails) {
        return 100.00;
    }
}
