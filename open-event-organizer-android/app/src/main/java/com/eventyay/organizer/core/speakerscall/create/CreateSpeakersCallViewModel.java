package com.eventyay.organizer.core.speakerscall.create;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.speakerscall.SpeakersCall;
import com.eventyay.organizer.data.speakerscall.SpeakersCallRepository;
import com.eventyay.organizer.utils.DateUtils;
import com.eventyay.organizer.utils.ErrorUtils;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeParseException;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateSpeakersCallViewModel extends ViewModel {

    private final SpeakersCall speakersCall = new SpeakersCall();

    private final SpeakersCallRepository speakersCallRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final MutableLiveData<SpeakersCall> speakersCallLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> success = new MutableLiveData<>();

    @Inject
    public CreateSpeakersCallViewModel(SpeakersCallRepository speakersCallRepository) {
        this.speakersCallRepository = speakersCallRepository;
    }

    public void createSpeakersCall(long eventId) {
        if (!verify())
            return;

        Event event = new Event();

        event.setId(eventId);
        speakersCallLive.getValue().setEvent(event);

        compositeDisposable.add(speakersCallRepository.createSpeakersCall(speakersCallLive.getValue())
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(var -> success.setValue("Speakers Call Created Successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public void updateSpeakersCall(long eventId) {
        if (!verify())
            return;

        Event event = new Event();

        event.setId(eventId);
        speakersCallLive.getValue().setEvent(event);

        compositeDisposable.add(speakersCallRepository.updateSpeakersCall(speakersCallLive.getValue())
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(var -> success.setValue("Speakers Call Updated Successfully"),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }

    public SpeakersCall loadSpeakersCall(long id, boolean reload) {
        compositeDisposable.add(speakersCallRepository.getSpeakersCall(id, reload)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(speakersCallLive::setValue, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
        return speakersCallLive.getValue();
    }

    public void initialize() {
        LocalDateTime current = LocalDateTime.now();

        String isoDate = DateUtils.formatDateToIso(current);
        speakersCall.setStartsAt(isoDate);
        speakersCall.setEndsAt(isoDate);
        speakersCallLive.setValue(speakersCall);
    }

    private boolean verify() {
        try {
            ZonedDateTime start = DateUtils.getDate(speakersCallLive.getValue().getStartsAt());
            ZonedDateTime end = DateUtils.getDate(speakersCallLive.getValue().getEndsAt());

            if (!end.isAfter(start)) {
                error.setValue("End time should be after start time");
                return false;
            }
            return true;
        } catch (DateTimeParseException pe) {
            error.setValue("Please enter date in correct format");
            return false;
        }
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<SpeakersCall> getSpeakersCall() {
        return speakersCallLive;
    }

    @VisibleForTesting
    protected void setSpeakersCall(SpeakersCall speakersCall) {
        speakersCallLive.setValue(speakersCall);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
