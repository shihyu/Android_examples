package com.eventyay.organizer.core.presenter;

import com.eventyay.organizer.common.rx.Logger;
import com.eventyay.organizer.core.session.list.SessionsPresenter;
import com.eventyay.organizer.core.session.list.SessionsView;
import com.eventyay.organizer.data.db.DatabaseChangeListener;
import com.eventyay.organizer.data.session.Session;
import com.eventyay.organizer.data.session.SessionRepository;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
@SuppressWarnings("PMD.TooManyMethods")
public class SessionsPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Mock
    private SessionsView sessionsView;
    @Mock
    private SessionRepository sessionRepository;
    @Mock
    private DatabaseChangeListener<Session> databaseChangeListener;

    private SessionsPresenter sessionsPresenter;

    private static final long ID = 10L;

    private static final String SESSION_DELETION_SUCCESS = "Sessions Deleted";

    private static List<Session> sessions = new ArrayList<Session>(Arrays.asList(
        Session.builder().id(2L).title("a").build(),
        Session.builder().id(3L).title("b").build(),
        Session.builder().id(4L).title("c").build()
    ));

    private static final Session SESSION = Session.builder().id(7L).title("a").build();

    @Before
    public void setUp() {
        sessionsPresenter = new SessionsPresenter(sessionRepository, databaseChangeListener);
        sessionsPresenter.attach(ID, sessionsView);

        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
    }

    @After
    public void tearDown() {
        RxJavaPlugins.reset();
        RxAndroidPlugins.reset();
    }

    @Test
    public void shouldLoadSessionListAutomatically() {
        when(sessionRepository.getSessions(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(sessions));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        sessionsPresenter.start();

        verify(sessionRepository).getSessions(ID, false);
    }

    @Test
    public void shouldShowSessionListAutomatically() {
        when(sessionRepository.getSessions(ID, false)).thenReturn(Observable.fromIterable(sessions));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        sessionsPresenter.start();

        verify(sessionsView).showResults(sessions);
    }

    @Test
    public void shouldActivateChangeListenerOnStart() {
        when(sessionRepository.getSessions(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(sessions));
        when(databaseChangeListener.getNotifier()).thenReturn(PublishSubject.create());

        sessionsPresenter.start();

        verify(databaseChangeListener).startListening();
    }

    @Test
    public void shouldDisableChangeListenerOnDetach() {
        sessionsPresenter.detach();

        verify(databaseChangeListener).stopListening();
    }

    @Test
    public void shouldShowEmptyViewOnNoSessionList() {
        when(sessionRepository.getSessions(anyLong(), anyBoolean())).thenReturn(Observable.fromIterable(new ArrayList<>()));

        sessionsPresenter.loadSessions(true);

        verify(sessionsView).showEmptyView(true);
    }

    @Test
    public void shouldShowSessionListOnSwipeRefreshSuccess() {
        when(sessionRepository.getSessions(ID, true)).thenReturn(Observable.fromIterable(sessions));

        sessionsPresenter.loadSessions(true);

        verify(sessionsView).showResults(any());
    }

    @Test
    public void shouldShowErrorMessageOnSwipeRefreshError() {
        when(sessionRepository.getSessions(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        sessionsPresenter.loadSessions(true);

        verify(sessionsView).showError(Logger.TEST_ERROR.getMessage());
    }

    @Test
    public void testProgressbarOnSwipeRefreshSuccess() {
        when(sessionRepository.getSessions(ID, true)).thenReturn(Observable.fromIterable(sessions));

        sessionsPresenter.loadSessions(true);

        InOrder inOrder = Mockito.inOrder(sessionsView);

        inOrder.verify(sessionsView).showProgress(true);
        inOrder.verify(sessionsView).onRefreshComplete(true);
        inOrder.verify(sessionsView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshError() {
        when(sessionRepository.getSessions(ID, true)).thenReturn(Observable.error(Logger.TEST_ERROR));

        sessionsPresenter.loadSessions(true);

        InOrder inOrder = Mockito.inOrder(sessionsView);

        inOrder.verify(sessionsView).showProgress(true);
        inOrder.verify(sessionsView).onRefreshComplete(false);
        inOrder.verify(sessionsView).showProgress(false);
    }

    @Test
    public void testProgressbarOnSwipeRefreshNoItem() {
        List<Session> emptyList = new ArrayList<>();
        when(sessionRepository.getSessions(ID, true)).thenReturn(Observable.fromIterable(emptyList));

        sessionsPresenter.loadSessions(true);

        InOrder inOrder = Mockito.inOrder(sessionsView);

        inOrder.verify(sessionsView).showProgress(true);
        inOrder.verify(sessionsView).onRefreshComplete(true);
        inOrder.verify(sessionsView).showProgress(false);
    }

    @Test
    public void shouldDeleteSessionWithIdSuccessfully() {
        when(sessionRepository.deleteSession(SESSION.getId())).thenReturn(Completable.complete());

        sessionsPresenter.isSessionSelected(SESSION.getId());
        sessionsPresenter.getSelectedSessions().get(SESSION.getId()).set(true);
        sessionsPresenter.deleteSession(SESSION.getId());

        assertFalse(sessionsPresenter.isSessionSelected(SESSION.getId()).get());
    }

    @Test
    public void shouldDeleteSessionsSuccessfully() {
        for (Session session : sessionsPresenter.getSessions()) {
            sessionsPresenter.getSelectedSessions().get(session.getId()).set(true);
        }

        for (Long sessionId : sessionsPresenter.getSelectedSessions().keySet()) {
            when(sessionRepository.deleteSession(sessionId)).thenReturn(Completable.complete());
        }

        sessionsPresenter.deleteSelectedSessions();

        InOrder inOrder = Mockito.inOrder(sessionsView);

        inOrder.verify(sessionsView).showProgress(true);
        inOrder.verify(sessionsView).showMessage(SESSION_DELETION_SUCCESS);
        inOrder.verify(sessionsView).showProgress(false);

        assertEquals(sessionsPresenter.getSelectedSessions().size(), 0);
    }

    @Test
    public void shouldNotDeleteUnselectedSessions() {
        for (Long sessionId : sessionsPresenter.getSelectedSessions().keySet()) {
            when(sessionRepository.deleteSession(sessionId)).thenReturn(Completable.error(Logger.TEST_ERROR));
        }

        sessionsPresenter.deleteSelectedSessions();

        verify(sessionRepository, Mockito.never()).deleteSession(anyLong());
    }

    @Test
    public void shouldResetToolbarToDefaultState() {
        sessionsPresenter.resetToolbarToDefaultState();

        verify(sessionsView).exitContextualMenuMode();
        verify(sessionsView).changeToolbarMode(false, false);
    }
}
