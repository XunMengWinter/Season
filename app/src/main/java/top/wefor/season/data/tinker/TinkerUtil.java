package top.wefor.season.data.tinker;

import top.wefor.season.util.PrefHelper;

/**
 * 该类用于安全获取Tinker相关的消息。
 * <p>
 * Created on 2018/10/24.
 *
 * @author ice
 */
public class TinkerUtil {

    /**
     * 获取TinkerId
     */
    public static String getTinkerId() {
        return PrefHelper.get().getTinkerId();
    }

    /**
     * 获取Tinker版本号
     */
    public static int getTinkerVersion() {
//        try {
//            return TinkerPatch.with().getPatchVersion();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return -1;
    }

    /**
     * 拉取补丁
     */
    public static void fetchPatch() {
//        try {
//            TinkerPatch.with().fetchPatchUpdate(true);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
