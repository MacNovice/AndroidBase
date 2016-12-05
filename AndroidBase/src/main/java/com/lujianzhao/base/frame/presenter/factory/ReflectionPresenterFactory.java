package com.lujianzhao.base.frame.presenter.factory;

import android.support.annotation.Nullable;

import com.lujianzhao.base.frame.presenter.BasePresenter;

/**
 * This class represents a {@link PresenterFactory} that creates a presenter using {@link Class#newInstance()} method.
 *
 * @param <P> the type of the presenter.
 */
public class ReflectionPresenterFactory<P extends BasePresenter> implements PresenterFactory<P> {

    private Class<P> mPresenterClass;

    /**
     * This method returns a {@link ReflectionPresenterFactory} instance if a given view class has
     * a {@link RequiresPresenter} annotation, or null otherwise.
     *
     * @param viewClass a class of the view
     * @param <P>       a type of the presenter
     * @return a {@link ReflectionPresenterFactory} instance that is supposed to create a presenter from {@link RequiresPresenter} annotation.
     */
    @Nullable
    public static <P extends BasePresenter> ReflectionPresenterFactory<P> fromViewClass(Class<?> viewClass) {
        RequiresPresenter annotation = viewClass.getAnnotation(RequiresPresenter.class);
        //noinspection unchecked
        Class<P> presenterClass = annotation == null ? null : (Class<P>)annotation.value();
        return presenterClass == null ? null : new ReflectionPresenterFactory<>(presenterClass);
    }

    public ReflectionPresenterFactory(Class<P> presenterClass) {
        this.mPresenterClass = presenterClass;
    }

    @Override
    public P createPresenter() {
        try {
            return mPresenterClass.newInstance();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
