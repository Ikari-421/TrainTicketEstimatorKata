package org.katas.service;

import org.json.JSONObject;
import org.katas.IApiCall;
import org.katas.model.TrainDetails;
import org.katas.exceptions.ApiException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiCallService implements IApiCall {

    @Override
    public double getBasePrice(TrainDetails trainDetails) {

        double basePrice = -1;
        try {
            String urlString = String.format("https://sncftrenitaliadb.com/api/train/estimate/price?from=%s&to=%s&date=%s", trainDetails.details().from(), trainDetails.details().to(), trainDetails.details().when());
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            conn.disconnect();
            JSONObject obj = new JSONObject(content.toString());
            basePrice = obj.has("price") ? obj.getDouble("price") : -1;
        } catch (Exception e) {
        }

        if (basePrice == -1) {
            throw new ApiException();
        }

        return basePrice;
    }

}
