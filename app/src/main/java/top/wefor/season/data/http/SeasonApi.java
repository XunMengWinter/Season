package top.wefor.season.data.http;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import top.wefor.season.data.model.BaseResult;
import top.wefor.season.data.model.DateCardEntity;

/**
 * Created on 2018/12/25.
 *
 * @author ice
 */
public interface SeasonApi {

    @GET("/calendar/{name}")
    Observable<BaseResult<List<DateCardEntity>>> getCalendar(@Path("name") String name);

}
