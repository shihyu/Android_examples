package com.eventyay.organizer.core.main;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.eventyay.organizer.R;
import com.eventyay.organizer.core.event.dashboard.EventDashboardFragment;
import com.eventyay.organizer.core.settings.SettingsFragment;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class FragmentNavigatorTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private FragmentManager fragmentManager;
    @Mock
    private FragmentTransaction fragmentTransaction;

    private FragmentNavigator fragmentNavigator;

    @Before
    public void setUp() {
        fragmentNavigator = new FragmentNavigator(fragmentManager, 0L);
    }

    @Test
    public void testFragmentLoading() {
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        when(fragmentTransaction.replace(any(Integer.TYPE), any())).thenReturn(fragmentTransaction);

        fragmentNavigator.loadFragment(R.id.nav_settings);

        verify(fragmentTransaction).replace(any(Integer.TYPE), isA(SettingsFragment.class));
        verify(fragmentTransaction).addToBackStack(null);
        verify(fragmentTransaction).commit();
        verifyNoMoreInteractions(fragmentTransaction);
    }

    @Test
    public void testOtherFragmentLoading() {
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        when(fragmentTransaction.replace(any(Integer.TYPE), any())).thenReturn(fragmentTransaction);

        fragmentNavigator.loadFragment(R.id.nav_settings);
        fragmentNavigator.loadFragment(R.id.nav_dashboard);

        InOrder inOrder = Mockito.inOrder(fragmentTransaction);
        inOrder.verify(fragmentTransaction).replace(any(Integer.TYPE), isA(SettingsFragment.class));
        inOrder.verify(fragmentTransaction).addToBackStack(null);
        inOrder.verify(fragmentTransaction).commit();

        inOrder.verify(fragmentTransaction).replace(any(Integer.TYPE), isA(EventDashboardFragment.class));
        inOrder.verify(fragmentTransaction).addToBackStack(null);
        inOrder.verify(fragmentTransaction).commit();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSameFragmentLoading() {
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        when(fragmentTransaction.replace(any(Integer.TYPE), any())).thenReturn(fragmentTransaction);

        fragmentNavigator.loadFragment(R.id.nav_settings);
        fragmentNavigator.loadFragment(R.id.nav_settings);
        fragmentNavigator.loadFragment(R.id.nav_settings);

        verify(fragmentTransaction).replace(any(Integer.TYPE), isA(SettingsFragment.class));
        verify(fragmentTransaction).addToBackStack(null);
        verify(fragmentTransaction).commit();
        verifyNoMoreInteractions(fragmentTransaction);
    }

    @Test
    public void testPopBackStack() {
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        when(fragmentTransaction.replace(any(Integer.TYPE), any())).thenReturn(fragmentTransaction);

        fragmentNavigator.loadFragment(R.id.nav_settings);

        verify(fragmentManager).popBackStack();
    }

    @Test
    public void testMyEventsActive() {
        assertTrue(fragmentNavigator.isMyEventsActive());

        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        when(fragmentTransaction.replace(any(Integer.TYPE), any())).thenReturn(fragmentTransaction);

        fragmentNavigator.loadFragment(R.id.nav_settings);
        assertFalse(fragmentNavigator.isMyEventsActive());
    }

    @Test
    public void testBack() {
        when(fragmentManager.beginTransaction()).thenReturn(fragmentTransaction);
        when(fragmentTransaction.replace(any(Integer.TYPE), any())).thenReturn(fragmentTransaction);

        fragmentNavigator.loadFragment(R.id.nav_settings);

        assertFalse(fragmentNavigator.isMyEventsActive());

        InOrder inOrder = Mockito.inOrder(fragmentManager);

        inOrder.verify(fragmentManager).popBackStack();

        when(fragmentManager.getBackStackEntryCount()).thenReturn(1);

        fragmentNavigator.back();

        assertTrue(fragmentNavigator.isMyEventsActive());

        inOrder.verify(fragmentManager).popBackStack();
    }
}
