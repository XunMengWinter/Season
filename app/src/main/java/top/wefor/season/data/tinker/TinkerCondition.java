package top.wefor.season.data.tinker;

/**
 * Tinker条件类。
 * 供Tinker补丁条件下发使用。
 * <p>
 * Created on 2018/10/24.
 *
 * @author ice
 */
public class TinkerCondition {

    public static void setMacAddress(String macAddress) {
        setCondition("mac_address", macAddress);
    }

    private static void setCondition(String key, String value) {
//        try {
//            TinkerPatch.with().setPatchCondition(key, value);
//            Logger.i(key + ": " + value);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Logger.e("tinker error: " + e.getMessage());
//        }
    }
}
