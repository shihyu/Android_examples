package com.eventyay.organizer.data.order;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.RateLimiter;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.order.model.OrderReceiptRequest;

import org.threeten.bp.Duration;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class OrderRepositoryImpl implements OrderRepository {

    private final OrderApi orderApi;
    private final Repository repository;
    private final RateLimiter<String> rateLimiter = new RateLimiter<>(Duration.ofMinutes(10));

    @Inject
    public OrderRepositoryImpl(OrderApi orderApi, Repository repository) {
        this.orderApi = orderApi;
        this.repository = repository;
    }

    @Override
    public Observable<Order> getOrders(long eventId, boolean reload) {
        Observable<Order> diskObservable = Observable.defer(() ->
            repository.getItems(Order.class, Order_Table.event_id.eq(eventId))
        );

        Observable<Order> networkObservable = Observable.defer(() ->
            orderApi.getOrders(eventId)
                .doOnNext(orders -> repository.syncSave(Order.class, orders, Order::getId, Order_Table.id).subscribe())
                .flatMapIterable(orders -> orders));

        return repository.observableOf(Order.class)
            .reload(reload)
            .withRateLimiterConfig("Orders", rateLimiter)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Order> getOrder(String orderIdentifier, boolean reload) {
        Observable<Order> diskObservable = Observable.defer(() ->
            repository
                .getItems(Order.class, Order_Table.identifier.eq(orderIdentifier)).take(1)
        );

        Observable<Order> networkObservable = Observable.defer(() ->
            orderApi.getOrder(orderIdentifier)
                .doOnNext(order -> repository
                    .save(Order.class, order)
                    .subscribe()));

        return repository
            .observableOf(Order.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Observable<Order> createOrder(Order order) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return orderApi
            .postOrder(order)
            .doOnNext(created -> {
                created.setEvent(order.getEvent());
                repository
                    .save(Order.class, created)
                    .subscribe();
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<OrderStatistics> getOrderStatisticsForEvent(long eventId, boolean reload) {
        Observable<OrderStatistics> diskObservable = Observable.defer(() ->
            repository
                .getItems(OrderStatistics.class, OrderStatistics_Table.event_id.eq(eventId)).take(1)
        );

        Observable<OrderStatistics> networkObservable = Observable.defer(() ->
            orderApi.getOrderStatisticsForEvent(eventId)
                .doOnNext(orderStatistics -> repository
                    .save(OrderStatistics.class, orderStatistics)
                    .subscribe()));

        return repository
            .observableOf(OrderStatistics.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @Override
    public Completable sendReceipt(OrderReceiptRequest orderReceiptRequest) {
        if (!repository.isConnected())
            return Completable.error(new Throwable(Constants.NO_NETWORK));

        return orderApi
            .sendReceiptEmail(orderReceiptRequest)
            .flatMapCompletable(
                var -> Completable.complete())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
