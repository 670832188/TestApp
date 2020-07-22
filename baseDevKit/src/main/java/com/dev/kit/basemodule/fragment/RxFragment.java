package com.dev.kit.basemodule.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.View;

import com.trello.rxlifecycle4.LifecycleProvider;
import com.trello.rxlifecycle4.LifecycleTransformer;
import com.trello.rxlifecycle4.RxLifecycle;
import com.trello.rxlifecycle4.android.FragmentEvent;
import com.trello.rxlifecycle4.android.RxLifecycleAndroid;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;


/**
 * RxFragment
 * Created by cuiyan on 16/6/8.
 */
public class RxFragment extends Fragment implements LifecycleProvider<FragmentEvent> {

    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();

    @Override
    @CallSuper
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    @CallSuper
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    @CallSuper
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        super.onPause();
        // onStop时解除绑定
//        lifecycleSubject.onNext(FragmentEvent.PAUSE);
    }

    @Override
    @CallSuper
    public void onStop() {
        super.onStop();
        lifecycleSubject.onNext(FragmentEvent.STOP);
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        super.onDestroyView();
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
    }

    @Override
    @CallSuper
    public void onDestroy() {
        super.onDestroy();
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
    }

    @Override
    @CallSuper
    public void onDetach() {
        super.onDetach();
        lifecycleSubject.onNext(FragmentEvent.DETACH);
    }


    @NonNull
    @Override
    public Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.hide();
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @NonNull
    @Override
    public <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }
}
