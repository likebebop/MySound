package com.example.likebebop.mysound;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func0;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public abstract class BaseViewModel {

    protected ArrayList<Runnable> initializerList = new ArrayList<>();
    protected ArrayList<Runnable> releaserList = new ArrayList<>();
    protected ArrayList<Subscription> subscriptions = new ArrayList<>();

    protected <T> Observable<T> publishSubject(final Func0<Observable<T>> observableCreator) {
        final PublishSubject<T> subject = PublishSubject.create();
        initializerList.add(() ->subscriptions.add(observableCreator.call().subscribe(subject)));
        return subject;
    }

    protected <T> BehaviorSubject<T> behaviorSubject(final Func0<Observable<T>> observableCreator) {
        final BehaviorSubject<T> subject = BehaviorSubject.create();
        initializerList.add(() ->subscriptions.add(observableCreator.call().subscribe(subject)));
        return subject;
    }

    protected <T> BehaviorSubject<T> behaviorSubject(final Func0<Observable<T>> observableCreator, T defaultValue) {
        final BehaviorSubject<T> subject = BehaviorSubject.create(defaultValue);
        initializerList.add(() -> subscriptions.add(observableCreator.call().subscribe(subject)));
        return subject;
    }

    protected <T> PublishSubject<T> publishSubject() {
        return PublishSubject.create();
    }

    protected <T> BehaviorSubject<T> behaviorSubject() {
        return BehaviorSubject.create();
    }

    protected <T> BehaviorSubject<T> behaviorSubject(T v) {
        return BehaviorSubject.create(v);
    }

    public void init() {
        for (Runnable runnable : initializerList) {
            runnable.run();
        }
        initializerList.clear();
    }

    public void release() {
        for (Runnable runnable : releaserList) {
            runnable.run();
        }
        for (Subscription s : subscriptions) {
            s.unsubscribe();
        }
        subscriptions.clear();
    }

}
