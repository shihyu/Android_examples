package com.eventyay.organizer.core.event.copyright;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.common.ContextManager;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.copyright.Copyright;
import com.eventyay.organizer.data.copyright.CopyrightRepository;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.utils.ErrorUtils;
import com.eventyay.organizer.utils.StringUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

public class CreateCopyrightViewModel extends ViewModel {

    private final CopyrightRepository copyrightRepository;
    private final Copyright copyright = new Copyright();
    private static final int YEAR_LENGTH = 4;

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final SingleEventLiveData<Boolean> progress = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> error = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> success = new SingleEventLiveData<>();
    private final SingleEventLiveData<Void> dismiss = new SingleEventLiveData<>();

    @Inject
    public CreateCopyrightViewModel(CopyrightRepository copyrightRepository) {
        this.copyrightRepository = copyrightRepository;
    }

    public Copyright getCopyright() {
        return copyright;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<String> getSuccess() {
        return success;
    }

    public LiveData<Void> getDismiss() {
        return dismiss;
    }

    public LiveData<String> getError() {
        return error;
    }

    private void nullifyEmptyFields(Copyright copyright) {
        copyright.setHolderUrl(StringUtils.emptyToNull(copyright.getHolderUrl()));
        copyright.setLicence(StringUtils.emptyToNull(copyright.getLicence()));
        copyright.setLicenceUrl(StringUtils.emptyToNull(copyright.getLicenceUrl()));
        copyright.setYear(StringUtils.emptyToNull(copyright.getYear()));
        copyright.setLogoUrl(StringUtils.emptyToNull(copyright.getLogoUrl()));
    }

    private boolean verifyYear(Copyright copyright) {
        if (copyright.getYear() == null)
            return true;
        else if (copyright.getYear().length() == YEAR_LENGTH)
            return true;
        else {
            error.setValue("Please Enter a Valid Year");
            return false;
        }
    }

    public void createCopyright() {
        nullifyEmptyFields(copyright);

        if (!verifyYear(copyright))
            return;

        long eventId = ContextManager.getSelectedEvent().getId();
        Event event = new Event();
        event.setId(eventId);
        copyright.setEvent(event);

        compositeDisposable.add(
            copyrightRepository.createCopyright(copyright)
                .doOnSubscribe(disposable -> progress.setValue(true))
                .doFinally(() -> progress.setValue(false))
                .subscribe(createdTicket -> {
                    success.setValue("Copyright Created");
                    dismiss.call();
                }, throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
