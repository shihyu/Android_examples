package com.eventyay.organizer.core.event.list;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.eventyay.organizer.core.presenter.TestUtil;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.event.Event;
import com.eventyay.organizer.data.event.EventRepository;
import com.eventyay.organizer.utils.DateUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.threeten.bp.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventsViewModelTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Mock
    private EventRepository eventRepository;
    @Mock
    private Preferences sharedPreferenceModel;

    @Mock
    Observer<List<Event>> events;
    @Mock
    Observer<String> error;
    @Mock
    Observer<Boolean> progress;
    @Mock
    Observer<Boolean> success;

    private EventsViewModel eventsViewModel;

    private static final String DATE = DateUtils.formatDateToIso(LocalDateTime.now());

    private static final List<Event> EVENT_LIST = Arrays.asList(
        Event.builder().id(12L).startsAt(DATE).endsAt(DATE).state("draft").build(),
        Event.builder().id(13L).startsAt(DATE).endsAt(DATE).state("draft").build(),
        Event.builder().id(14L).startsAt(DATE).endsAt(DATE).state("published").build()
    );

    @Before
    public void setUp() {
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        eventsViewModel = new EventsViewModel(eventRepository, sharedPreferenceModel);
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
    }

    @Test
    public void shouldLoadEventsSuccessfully() {
        when(eventRepository.getEvents(false))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(events, eventRepository, progress, success);

        eventsViewModel.getProgress().observeForever(progress);
        eventsViewModel.getSuccess().observeForever(success);
        eventsViewModel.getError().observeForever(error);

        events.onChanged(new ArrayList<Event>());

        eventsViewModel.loadUserEvents(false);

        inOrder.verify(events).onChanged(new ArrayList<>());
        inOrder.verify(eventRepository).getEvents(false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldRefreshEventsSuccessfully() {
        when(eventRepository.getEvents(true))
            .thenReturn(Observable.fromIterable(EVENT_LIST));

        InOrder inOrder = Mockito.inOrder(events, eventRepository, progress, success, progress);

        eventsViewModel.getProgress().observeForever(progress);
        eventsViewModel.getSuccess().observeForever(success);
        eventsViewModel.getError().observeForever(error);

        events.onChanged(new ArrayList<Event>());

        eventsViewModel.loadUserEvents(true);

        inOrder.verify(eventRepository).getEvents(true);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(success).onChanged(true);
        inOrder.verify(progress).onChanged(false);
    }

    @Test
    public void shouldShowEventError() {
        String errorString = "Test Error";
        when(eventRepository.getEvents(false))
            .thenReturn(TestUtil.ERROR_OBSERVABLE);

        InOrder inOrder = Mockito.inOrder(eventRepository, progress, error);

        eventsViewModel.getProgress().observeForever(progress);
        eventsViewModel.getError().observeForever(error);

        events.onChanged(new ArrayList<Event>());

        eventsViewModel.loadUserEvents(false);

        inOrder.verify(eventRepository).getEvents(false);
        inOrder.verify(progress).onChanged(true);
        inOrder.verify(error).onChanged(errorString);
        inOrder.verify(progress).onChanged(false);
    }
}
