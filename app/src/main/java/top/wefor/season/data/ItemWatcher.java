package top.wefor.season.data;

import java.lang.ref.WeakReference;

import top.wefor.season.data.model.DateCardEntity;

/**
 * Created on 2018/12/26.
 *
 * @author ice
 */
public class ItemWatcher {

    private static ItemWatcher sItemWatcher = new ItemWatcher();

    private WeakReference<DateCardEntity> mEntityWeakReference;

    private ItemWatcher() {
    }

    public static ItemWatcher get(){
        return sItemWatcher;
    }

    public void watch(DateCardEntity dateCardEntity) {
        mEntityWeakReference = new WeakReference<>(dateCardEntity);
    }

    public void set(DateCardEntity dateCardEntity) {
        if (mEntityWeakReference != null) {
            DateCardEntity oldEntity = mEntityWeakReference.get();
            if (oldEntity != null) {
                oldEntity.title = dateCardEntity.title;
                oldEntity.subtitle = dateCardEntity.subtitle;
                oldEntity.desc = dateCardEntity.desc;
            }
        }
    }

}
