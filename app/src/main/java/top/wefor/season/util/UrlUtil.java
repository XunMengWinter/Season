package top.wefor.season.util;

/**
 * Created on 2018/12/23.
 *
 * @author ice
 */
public class UrlUtil {

    public static boolean isInvalidUrl(String url) {
        if (url == null)
            return false;
        return url.startsWith("http://") || url.startsWith("https://");
    }

}
