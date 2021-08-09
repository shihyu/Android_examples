package com.eventyay.organizer.common.mvp.presenter;

import androidx.annotation.CallSuper;
import androidx.annotation.VisibleForTesting;

import io.reactivex.disposables.CompositeDisposable;

public abstract class AbstractBasePresenter<V> implements Presenter<V> {
    private V view;
    private CompositeDisposable compositeDisposable;

    private int attachCount;

    @Override
    @CallSuper
    public void attach(V view) {
        this.view = view;
        this.compositeDisposable = new CompositeDisposable();
        attachCount++;
    }

    @Override
    @CallSuper
    public void detach() {
        compositeDisposable.dispose();
    }

    protected V getView() {
        return view;
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public CompositeDisposable getDisposable() {
        return compositeDisposable;
    }

    protected boolean isRotated() {
        return attachCount > 1;
    }
}
