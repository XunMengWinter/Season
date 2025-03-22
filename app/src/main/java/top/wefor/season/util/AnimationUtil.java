package top.wefor.season.util;

import android.app.Activity;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;

import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created on 2018/12/23.
 *
 * @author ice
 */
public class AnimationUtil {

    public static void shake(View view) {
        shake(view, 600, -4, 2);
    }

    public static void shakeOk(View view) {
        shake(view, 40, -2, 1);
    }

    public static void shake(View view, long duration, int degrees, int cycle) {
        RotateAnimation animation = new RotateAnimation(0, degrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.77f);
        animation.setInterpolator(new CycleInterpolator(cycle));
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public static void blink(View view) {
        AlphaAnimation animation = new AlphaAnimation(1f, 0.6f);
        animation.setInterpolator(new CycleInterpolator(2));
        animation.setDuration(400);
        view.startAnimation(animation);
    }

    public static void scale(View view) {
        ScaleAnimation animation = new ScaleAnimation(1f, 1.01f, 1f, 1.01f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new CycleInterpolator(1));
        animation.setDuration(800);
        view.startAnimation(animation);
    }

    public static void clearAnim(View view) {
        view.animate().cancel();
        view.clearAnimation();
    }

    public static void removeActivityFromTransitionManager(Activity activity) {
        Class transitionManagerClass = TransitionManager.class;
        try {
            Field runningTransitionsField = transitionManagerClass.getDeclaredField("sRunningTransitions");
            runningTransitionsField.setAccessible(true);
            //noinspection unchecked
            ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>> runningTransitions
                    = (ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<Transition>>>>)
                    runningTransitionsField.get(transitionManagerClass);
            if (runningTransitions.get() == null || runningTransitions.get().get() == null) {
                return;
            }
            ArrayMap map = runningTransitions.get().get();
            View decorView = activity.getWindow().getDecorView();
            Logger.i("removeActivityFromTransitionManager " + map.remove(decorView));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(e.getMessage());
        }
    }

}
