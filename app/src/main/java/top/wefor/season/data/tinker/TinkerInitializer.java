//package top.wefor.season.data.tinker;
//
//import com.orhanobut.logger.Logger;
//import com.tencent.tinker.entry.ApplicationLike;
//import com.tencent.tinker.lib.service.PatchResult;
//import com.tinkerpatch.sdk.TinkerPatch;
//import com.tinkerpatch.sdk.loader.TinkerPatchApplicationLike;
//import com.tinkerpatch.sdk.server.callback.RollbackCallBack;
//import com.tinkerpatch.sdk.tinker.callback.ResultCallBack;
//
//import top.wefor.season.App;
//import top.wefor.season.BuildConfig;
//import top.wefor.season.util.ChannelHelper;
//import top.wefor.season.util.CommonUtil;
//import top.wefor.season.util.PrefHelper;
//
///**
// * Created on 2018/10/22.
// * <p>
// * ice
// */
//public class TinkerInitializer {
//
//    public static void init() {
//        if (!BuildConfig.TINKER_ENABLE)
//            return;
//
//        // 我们可以从这里获得Tinker加载过程的信息
//        ApplicationLike tinkerApplicationLike = TinkerPatchApplicationLike.getTinkerPatchApplicationLike();
//        if (tinkerApplicationLike == null) {
//            Logger.e("tinker error");
//            return;
//        }
//
//        // 初始化TinkerPatch SDK, 更多配置可参照API章节中的,初始化SDK
//        TinkerPatch.init(tinkerApplicationLike)
//                .setAppChannel(ChannelHelper.get().getChannel());
//
//        /* 设置当前设备的一些信息(用于额外的条件筛选) */
//        TinkerCondition.setMacAddress(CommonUtil.getMacAddress(App.get()));
//
//        /* 设置如何拉取补丁 */
//        TinkerPatch.with()
//                .reflectPatchLibrary()
//                //设置收到后台回退要求时,锁屏清除补丁,默认是等主进程重启时自动清除
//                .setPatchRollbackOnScreenOff(true)
//                //设置补丁合成成功后,锁屏重启程序,默认是等应用自然重启
//                .setPatchRestartOnSrceenOff(true)
//                // 每隔3个小时(通过setFetchPatchIntervalByHours设置)去访问后台时候有更新,通过handler实现轮训的效果
//                .setFetchPatchIntervalByHours(3)
//                .fetchPatchUpdateAndPollWithInterval()
//                //屏蔽部分渠道的补丁功能
//                .addIgnoreAppChannel("googleplay");
//
//        /* 设置监听器 */
//        TinkerPatch.with()
//                .setPatchResultCallback(new ResultCallBack() {
//                    @Override
//                    public void onPatchResult(PatchResult patchResult) {
//                        if (patchResult == null)
//                            return;
//                        Logger.i(patchResult.toString());
//                        if (patchResult.isSuccess) {
//                            //补丁拉取成功
//                            PrefHelper.get().setLastPatchTime(System.currentTimeMillis());
//                            App.get().setNeedExitApp(true);
//                        }
//                    }
//                })
//                .setPatchRollBackCallback(new RollbackCallBack() {
//                    @Override
//                    public void onPatchRollback() {
//                        //回滚成功
//                        PrefHelper.get().setLastPatchTime(System.currentTimeMillis());
//                        App.get().setNeedExitApp(true);
//                    }
//                });
//
//        if (TinkerUtil.getTinkerVersion() == 0) {
//            //说明这是这是基准包，那么写入TinkerId。
//            PrefHelper.get().setTinkerId(BuildConfig.TINKER_ID);
//        }
//
//        //这时主动拉取一次更新
//        TinkerPatch.with().fetchPatchUpdate(true);
//        Logger.i("tinker init success");
//    }
//
//}
