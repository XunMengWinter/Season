package top.wefor.season.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created on 2018/12/5.
 *
 * @author ice
 */
public class BaseResult<T> {

    @SerializedName("error_code")
    public int errorCode;

    @SerializedName("msg")
    public String msg;

    @SerializedName("date_offset")
    public int dateOffset = 0;

    @SerializedName("data")
    public T data;

}
