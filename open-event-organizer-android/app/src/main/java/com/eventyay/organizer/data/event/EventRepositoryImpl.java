package com.eventyay.organizer.data.event;

import androidx.annotation.NonNull;

import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.data.Repository;
import com.eventyay.organizer.data.auth.AuthHolder;
import com.eventyay.organizer.data.image.ImageData;
import com.eventyay.organizer.data.image.ImageUploadApi;
import com.eventyay.organizer.data.image.ImageUrl;
import com.eventyay.organizer.data.ticket.Ticket;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Singleton
public class EventRepositoryImpl implements EventRepository {

    private final Repository repository;
    private final EventApi eventApi;
    private final AuthHolder authHolder;
    private final ImageUploadApi imageUploadApi;

    @Inject
    public EventRepositoryImpl(Repository repository, EventApi eventApi, AuthHolder authHolder, ImageUploadApi imageUploadApi) {
        this.repository = repository;
        this.eventApi = eventApi;
        this.authHolder = authHolder;
        this.imageUploadApi = imageUploadApi;
    }

    private void saveEvent(Event event) {
        event.setComplete(true);

        repository
            .save(Event.class, event)
            .subscribe();

        List<Ticket> tickets = event.getTickets();
        if (tickets != null) {
            for (Ticket ticket : tickets)
                ticket.setEvent(event);

            repository
                .saveList(Ticket.class, tickets)
                .subscribe();
        }
    }

    @Override
    public Observable<Event> getEvent(long eventId, boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            repository
                .getItems(Event.class, Event_Table.id.eq(eventId))
                .filter(Event::isComplete)
                .take(1)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventApi
                .getEvent(eventId)
                .doOnNext(this::saveEvent));

        return repository.observableOf(Event.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Event> getEvents(boolean reload) {
        Observable<Event> diskObservable = Observable.defer(() ->
            repository.getAllItems(Event.class)
        );

        Observable<Event> networkObservable = Observable.defer(() ->
            eventApi.getEvents(authHolder.getIdentity())
                .doOnNext(events -> repository.syncSave(Event.class, events, Event::getId, Event_Table.id).subscribe())
                .flatMapIterable(events -> events));

        return repository.observableOf(Event.class)
            .reload(reload)
            .withDiskObservable(diskObservable)
            .withNetworkObservable(networkObservable)
            .build();
    }

    @NonNull
    @Override
    public Observable<Event> updateEvent(Event event) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        event.setTickets(null);
        return eventApi.patchEvent(event.getId(), event)
            .doOnNext(updatedEvent -> repository
                .update(Event.class, updatedEvent)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Event> createEvent(Event event) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventApi.postEvent(event)
            .doOnNext(created -> repository
                .save(Event.class, created)
                .subscribe())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EventStatistics> getEventStatistics(long id) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return eventApi
            .getEventStatistics(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    @Override
    public Observable<ImageUrl> uploadEventImage(ImageData imageData) {
        if (!repository.isConnected()) {
            return Observable.error(new Throwable(Constants.NO_NETWORK));
        }

        return imageUploadApi
            .postOriginalImage(imageData)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
    }
}
