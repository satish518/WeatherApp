
package com.example.weatherapp.models.citywiseweather;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Rain {

    @SerializedName("1h")
    private Double mH;

    public Double getH() {
        return mH;
    }

    public void setH(Double h) {
        mH = h;
    }

}
