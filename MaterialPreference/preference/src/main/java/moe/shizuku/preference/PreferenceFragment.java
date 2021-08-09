/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package moe.shizuku.preference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.XmlRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;
import static moe.shizuku.preference.Preference.DividerVisibility;

/**
 * Shows a hierarchy of {@link Preference} objects as
 * lists. These preferences will
 * automatically save to {@link android.content.SharedPreferences} as the user interacts with
 * them. To retrieve an instance of {@link android.content.SharedPreferences} that the
 * preference hierarchy in this fragment will use, call
 * {@link PreferenceManager#getDefaultSharedPreferences(android.content.Context)}
 * with a context in the same package as this fragment.
 * <p>
 * Furthermore, the preferences shown will follow the visual style of system
 * preferences. It is easy to create a hierarchy of preferences (that can be
 * shown on multiple screens) via XML. For these reasons, it is recommended to
 * use this fragment (as a superclass) to deal with preferences in applications.
 * <p>
 * A {@link PreferenceScreen} object should be at the top of the preference
 * hierarchy. Furthermore, subsequent {@link PreferenceScreen} in the hierarchy
 * denote a screen break--that is the preferences contained within subsequent
 * {@link PreferenceScreen} should be shown on another screen. The preference
 * framework handles this by calling {@link #onNavigateToScreen(PreferenceScreen)}.
 * <p>
 * The preference hierarchy can be formed in multiple ways:
 * <li> From an XML file specifying the hierarchy
 * <li> From different {@link android.app.Activity Activities} that each specify its own
 * preferences in an XML file via {@link android.app.Activity} meta-data
 * <li> From an object hierarchy rooted with {@link PreferenceScreen}
 * <p>
 * To inflate from XML, use the {@link #addPreferencesFromResource(int)}. The
 * root element should be a {@link PreferenceScreen}. Subsequent elements can point
 * to actual {@link Preference} subclasses. As mentioned above, subsequent
 * {@link PreferenceScreen} in the hierarchy will result in the screen break.
 * <p>
 * To specify an object hierarchy rooted with {@link PreferenceScreen}, use
 * {@link #setPreferenceScreen(PreferenceScreen)}.
 * <p>
 * As a convenience, this fragment implements a click listener for any
 * preference in the current hierarchy, see
 * {@link #onPreferenceTreeClick(Preference)}.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For information about using {@code PreferenceFragment},
 * read the <a href="{@docRoot}guide/topics/ui/settings.html">Settings</a>
 * guide.</p>
 * </div>
 *
 * <a name="SampleCode"></a>
 * <h3>Sample Code</h3>
 *
 * <p>The following sample code shows a simple preference fragment that is
 * populated from a resource.  The resource it loads is:</p>
 * <p>
 * {@sample frameworks/support/samples/SupportPreferenceDemos/res/xml/preferences.xml preferences}
 *
 * <p>The fragment implementation itself simply populates the preferences
 * when created.  Note that the preferences framework takes care of loading
 * the current values out of the app preferences and writing them when changed:</p>
 * <p>
 * {@sample frameworks/support/samples/SupportPreferenceDemos/src/com/example/android/supportpreference/FragmentSupportPreferencesCompat.java
 * support_fragment_compat}
 *
 * @see Preference
 * @see PreferenceScreen
 */
public abstract class PreferenceFragment extends Fragment implements
    PreferenceManager.OnPreferenceTreeClickListener,
    PreferenceManager.OnDisplayPreferenceDialogListener,
    PreferenceManager.OnNavigateToScreenListener,
    DialogPreference.TargetFragment
{

    /**
     * Fragment argument used to specify the tag of the desired root
     * {@link PreferenceScreen} object.
     */
    public static final String ARG_PREFERENCE_ROOT =
        "moe.shizuku.preference.PreferenceFragment.PREFERENCE_ROOT";

    private static final String PREFERENCES_TAG = "android:preferences";

    private static final String DIALOG_FRAGMENT_TAG =
        "moe.shizuku.preference.PreferenceFragment.DIALOG";

    private PreferenceManager mPreferenceManager;
    private RecyclerView mList;
    private boolean mHavePrefs;
    private boolean mInitDone;

    private Context mStyledContext;

    private int mLayoutResId = R.layout.preference_list_fragment;

    private DividerDecoration mDividerDecoration;

    private static final int MSG_BIND_PREFERENCES = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

            case MSG_BIND_PREFERENCES:
                bindPreferences();
                break;
            }
        }
    };

    final private Runnable mRequestFocus = new Runnable()
    {
        @Override
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    private Runnable mSelectPreferenceRunnable;

    /**
     * Interface that PreferenceFragment's containing activity should
     * implement to be able to process preference items that wish to
     * switch to a specified fragment.
     */
    public interface OnPreferenceStartFragmentCallback
    {
        /**
         * Called when the user has clicked on a Preference that has
         * a fragment class name associated with it.  The implementation
         * should instantiate and switch to an instance of the given
         * fragment.
         *
         * @param caller The fragment requesting navigation.
         * @param pref   The preference requesting the fragment.
         * @return true if the fragment creation has been handled
         */
        boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref);
    }

    /**
     * Interface that PreferenceFragment's containing activity should
     * implement to be able to process preference items that wish to
     * switch to a new screen of preferences.
     */
    public interface OnPreferenceStartScreenCallback
    {
        /**
         * Called when the user has clicked on a PreferenceScreen item in order to navigate to a new
         * screen of preferences.
         *
         * @param caller The fragment requesting navigation.
         * @param pref   The preference screen to navigate to.
         * @return true if the screen navigation has been handled
         */
        boolean onPreferenceStartScreen(PreferenceFragment caller, PreferenceScreen pref);
    }

    public interface OnPreferenceDisplayDialogCallback
    {

        /**
         * @param caller The fragment containing the preference requesting the dialog.
         * @param pref   The preference requesting the dialog.
         * @return true if the dialog creation has been handled.
         */
        boolean onPreferenceDisplayDialog(PreferenceFragment caller, Preference pref);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, tv, true);
        final int theme = tv.resourceId;

        if (theme <= 0) {
            throw new IllegalStateException("Must specify preferenceTheme in theme");
        }

        mStyledContext = new ContextThemeWrapper(getActivity(), theme);

        mPreferenceManager = new PreferenceManager(mStyledContext);
        mPreferenceManager.setFragment(this);
        mPreferenceManager.setOnNavigateToScreenListener(this);
        final Bundle args = getArguments();
        final String rootKey;

        if (args != null) {
            rootKey = getArguments().getString(ARG_PREFERENCE_ROOT);
        } else {
            rootKey = null;
        }

        onCreatePreferences(savedInstanceState, rootKey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        mPreferenceManager.dispatchActivityResult(requestCode, resultCode, data);
    }


    /**
     * Called during {@link #onCreate(Bundle)} to supply the preferences for this fragment.
     * Subclasses are expected to call {@link #setPreferenceScreen(PreferenceScreen)} either
     * directly or via helper methods such as {@link #addPreferencesFromResource(int)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     * @param rootKey            If non-null, this preference fragment should be rooted at the
     *                           {@link PreferenceScreen} with this key.
     */
    public abstract void onCreatePreferences(Bundle savedInstanceState, String rootKey);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        TypedArray a = mStyledContext.obtainStyledAttributes(null,
                       R.styleable.PreferenceFragment,
                       R.attr.preferenceFragmentStyle,
                       0);

        mLayoutResId = a.getResourceId(R.styleable.PreferenceFragment_android_layout,
                                       mLayoutResId);

        final Drawable divider = a.getDrawable(
                                     R.styleable.PreferenceFragment_android_divider);
        final int dividerHeight = a.getDimensionPixelSize(
                                      R.styleable.PreferenceFragment_android_dividerHeight, -1);
        final boolean allowDividerAfterLastItem = a.getBoolean(
                    R.styleable.PreferenceFragment_allowDividerAfterLastItem, false);

        a.recycle();

        // Need to theme the inflater to pick up the preferenceFragmentListStyle
        final TypedValue tv = new TypedValue();
        getActivity().getTheme().resolveAttribute(R.attr.preferenceTheme, tv, true);
        final int theme = tv.resourceId;

        final Context themedContext = new ContextThemeWrapper(inflater.getContext(), theme);
        final LayoutInflater themedInflater = inflater.cloneInContext(themedContext);

        final View view = themedInflater.inflate(mLayoutResId, container, false);

        final View rawListContainer = view.findViewById(R.id.list_container);

        if (!(rawListContainer instanceof ViewGroup)) {
            throw new RuntimeException("Content has view with id attribute "
                                       + "'R.id.list_container' that is not a ViewGroup class");
        }

        final ViewGroup listContainer = (ViewGroup) rawListContainer;

        final RecyclerView listView = onCreateRecyclerView(themedInflater, listContainer,
                                      savedInstanceState);

        if (listView == null) {
            throw new RuntimeException("Could not create RecyclerView");
        }

        mList = listView;

        mDividerDecoration = onCreateItemDecoration();

        if (mDividerDecoration != null) {
            mList.addItemDecoration(mDividerDecoration);
        }

        setDivider(divider);

        if (dividerHeight != -1) {
            setDividerHeight(dividerHeight);
        }

        mDividerDecoration.setAllowDividerAfterLastItem(allowDividerAfterLastItem);

        listContainer.addView(mList);
        mHandler.post(mRequestFocus);

        return view;
    }

    /**
     * Called when create ItemDecoration of the preference list.
     *
     * @return ItemDecoration or null for no divider
     */
    @Nullable
    public DividerDecoration onCreateItemDecoration()
    {
        return new DefaultDividerDecoration();
    }

    /**
     * Sets the drawable that will be drawn between each item in the list.
     * <p>
     * <strong>Note:</strong> If the drawable does not have an intrinsic
     * height, you should also call {@link #setDividerHeight(int)}.
     *
     * @param divider the drawable to use
     * @attr ref R.styleable#PreferenceFragmentCompat_android_divider
     */
    public void setDivider(Drawable divider)
    {
        mDividerDecoration.setDivider(divider);
    }

    /**
     * Sets the height of the divider that will be drawn between each item in the list. Calling
     * this will override the intrinsic height as set by {@link #setDivider(Drawable)}
     *
     * @param height The new height of the divider in pixels.
     * @attr ref R.styleable#PreferenceFragmentCompat_android_dividerHeight
     */
    public void setDividerHeight(int height)
    {
        mDividerDecoration.setDividerHeight(height);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        if (mHavePrefs) {
            bindPreferences();

            if (mSelectPreferenceRunnable != null) {
                mSelectPreferenceRunnable.run();
                mSelectPreferenceRunnable = null;
            }
        }

        mInitDone = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            Bundle container = savedInstanceState.getBundle(PREFERENCES_TAG);

            if (container != null) {
                final PreferenceScreen preferenceScreen = getPreferenceScreen();

                if (preferenceScreen != null) {
                    preferenceScreen.restoreHierarchyState(container);
                }
            }
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mPreferenceManager.setOnPreferenceTreeClickListener(this);
        mPreferenceManager.setOnDisplayPreferenceDialogListener(this);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        mPreferenceManager.dispatchActivityStop();
        mPreferenceManager.setOnPreferenceTreeClickListener(null);
        mPreferenceManager.setOnDisplayPreferenceDialogListener(null);
    }

    @Override
    public void onDestroyView()
    {
        mHandler.removeCallbacks(mRequestFocus);
        mHandler.removeMessages(MSG_BIND_PREFERENCES);

        if (mHavePrefs) {
            unbindPreferences();
        }

        mList = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mPreferenceManager.dispatchActivityDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        final PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen != null) {
            Bundle container = new Bundle();
            preferenceScreen.saveHierarchyState(container);
            outState.putBundle(PREFERENCES_TAG, container);
        }
    }

    /**
     * Returns the {@link PreferenceManager} used by this fragment.
     *
     * @return The {@link PreferenceManager}.
     */
    public PreferenceManager getPreferenceManager()
    {
        return mPreferenceManager;
    }

    /**
     * Sets the root of the preference hierarchy that this fragment is showing.
     *
     * @param preferenceScreen The root {@link PreferenceScreen} of the preference hierarchy.
     */
    public void setPreferenceScreen(PreferenceScreen preferenceScreen)
    {
        if (mPreferenceManager.setPreferences(preferenceScreen) && preferenceScreen != null) {
            onUnbindPreferences();
            mHavePrefs = true;

            if (mInitDone) {
                postBindPreferences();
            }
        }
    }

    /**
     * Gets the root of the preference hierarchy that this fragment is showing.
     *
     * @return The {@link PreferenceScreen} that is the root of the preference
     * hierarchy.
     */
    public PreferenceScreen getPreferenceScreen()
    {
        return mPreferenceManager.getPreferenceScreen();
    }

    /**
     * Inflates the given XML resource and adds the preference hierarchy to the current
     * preference hierarchy.
     *
     * @param preferencesResId The XML resource ID to inflate.
     */
    public void addPreferencesFromResource(@XmlRes int preferencesResId)
    {
        requirePreferenceManager();

        setPreferenceScreen(mPreferenceManager.inflateFromResource(mStyledContext,
                            preferencesResId, getPreferenceScreen()));
    }

    /**
     * Inflates the given XML resource and replaces the current preference hierarchy (if any) with
     * the preference hierarchy rooted at {@code key}.
     *
     * @param preferencesResId The XML resource ID to inflate.
     * @param key              The preference key of the {@link PreferenceScreen} to use as the root of the
     *                         preference hierarchy, or null to use the root {@link PreferenceScreen}.
     */
    public void setPreferencesFromResource(@XmlRes int preferencesResId, @Nullable String key)
    {
        requirePreferenceManager();

        final PreferenceScreen xmlRoot = mPreferenceManager.inflateFromResource(mStyledContext,
                                         preferencesResId, null);

        final Preference root;

        if (key != null) {
            root = xmlRoot.findPreference(key);

            if (!(root instanceof PreferenceScreen)) {
                throw new IllegalArgumentException("Preference object with key " + key
                                                   + " is not a PreferenceScreen");
            }
        } else {
            root = xmlRoot;
        }

        setPreferenceScreen((PreferenceScreen) root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPreferenceTreeClick(Preference preference)
    {
        if (preference.getFragment() != null) {
            boolean handled = false;

            if (getCallbackFragment() instanceof OnPreferenceStartFragmentCallback) {
                handled = ((OnPreferenceStartFragmentCallback) getCallbackFragment())
                          .onPreferenceStartFragment(this, preference);
            }

            if (!handled && getActivity() instanceof OnPreferenceStartFragmentCallback) {
                handled = ((OnPreferenceStartFragmentCallback) getActivity())
                          .onPreferenceStartFragment(this, preference);
            }

            return handled;
        }

        return false;
    }

    /**
     * Called by {@link PreferenceScreen#onClick()} in order to navigate to a
     * new screen of preferences. Calls
     * {@link PreferenceFragment.OnPreferenceStartScreenCallback#onPreferenceStartScreen}
     * if the target fragment or containing activity implements
     * {@link PreferenceFragment.OnPreferenceStartScreenCallback}.
     *
     * @param preferenceScreen The {@link PreferenceScreen} to navigate to.
     */
    @Override
    public void onNavigateToScreen(PreferenceScreen preferenceScreen)
    {
        boolean handled = false;

        if (getCallbackFragment() instanceof OnPreferenceStartScreenCallback) {
            handled = ((OnPreferenceStartScreenCallback) getCallbackFragment())
                      .onPreferenceStartScreen(this, preferenceScreen);
        }

        if (!handled && getActivity() instanceof OnPreferenceStartScreenCallback) {
            ((OnPreferenceStartScreenCallback) getActivity())
            .onPreferenceStartScreen(this, preferenceScreen);
        }
    }

    /**
     * Finds a {@link Preference} based on its key.
     *
     * @param key The key of the preference to retrieve.
     * @return The {@link Preference} with the key, or null.
     * @see PreferenceGroup#findPreference(CharSequence)
     */
    @Override
    public Preference findPreference(CharSequence key)
    {
        if (mPreferenceManager == null) {
            return null;
        }

        return mPreferenceManager.findPreference(key);
    }

    private void requirePreferenceManager()
    {
        if (mPreferenceManager == null) {
            throw new RuntimeException("This should be called after super.onCreate.");
        }
    }

    private void postBindPreferences()
    {
        if (mHandler.hasMessages(MSG_BIND_PREFERENCES)) {
            return;
        }

        mHandler.obtainMessage(MSG_BIND_PREFERENCES).sendToTarget();
    }

    private void bindPreferences()
    {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen != null) {
            getListView().setAdapter(onCreateAdapter(preferenceScreen));
            preferenceScreen.onAttached();
        }

        onBindPreferences();
    }

    private void unbindPreferences()
    {
        final PreferenceScreen preferenceScreen = getPreferenceScreen();

        if (preferenceScreen != null) {
            preferenceScreen.onDetached();
        }

        onUnbindPreferences();
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    protected void onBindPreferences()
    {
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    protected void onUnbindPreferences()
    {
    }

    public final RecyclerView getListView()
    {
        return mList;
    }

    /**
     * Creates the {@link RecyclerView} used to display the preferences.
     * Subclasses may override this to return a customized
     * {@link RecyclerView}.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate the
     *                           {@link RecyclerView}.
     * @param parent             The parent {@link android.view.View} that the RecyclerView will be attached to.
     *                           This method should not add the view itself, but this can be used to generate
     *                           the LayoutParams of the view.
     * @param savedInstanceState If non-null, this view is being re-constructed from a previous
     *                           saved state as given here
     * @return A new RecyclerView object to be placed into the view hierarchy
     */
    public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
            Bundle savedInstanceState)
    {
        RecyclerView recyclerView = (RecyclerView) inflater
                                    .inflate(R.layout.preference_recyclerview, parent, false);

        recyclerView.setLayoutManager(onCreateLayoutManager());
        /*recyclerView.setAccessibilityDelegateCompat(
                new PreferenceRecyclerViewAccessibilityDelegate(recyclerView));*/

        return recyclerView;
    }

    /**
     * Called from {@link #onCreateRecyclerView} to create the
     * {@link RecyclerView.LayoutManager} for the created
     * {@link RecyclerView}.
     *
     * @return A new {@link RecyclerView.LayoutManager} instance.
     */
    public RecyclerView.LayoutManager onCreateLayoutManager()
    {
        return new LinearLayoutManager(getActivity());
    }

    /**
     * Creates the root adapter.
     *
     * @param preferenceScreen Preference screen object to create the adapter for.
     * @return An adapter that contains the preferences contained in this {@link PreferenceScreen}.
     */
    protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen)
    {
        return new PreferenceGroupAdapter(preferenceScreen);
    }

    /**
     * Called when a preference in the tree requests to display a dialog. Subclasses should
     * override this method to display custom dialogs or to handle dialogs for custom preference
     * classes.
     *
     * @param preference The Preference object requesting the dialog.
     */
    @Override
    public void onDisplayPreferenceDialog(Preference preference)
    {

        boolean handled = false;

        if (getCallbackFragment() instanceof OnPreferenceDisplayDialogCallback) {
            handled = ((OnPreferenceDisplayDialogCallback) getCallbackFragment())
                      .onPreferenceDisplayDialog(this, preference);
        }

        if (!handled && getActivity() instanceof OnPreferenceDisplayDialogCallback) {
            handled = ((OnPreferenceDisplayDialogCallback) getActivity())
                      .onPreferenceDisplayDialog(this, preference);
        }

        if (handled) {
            return;
        }

        // check if dialog is already showing
        if (getFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        DialogFragment f = null;

        if (preference instanceof DialogPreference) {
            f = ((DialogPreference) preference).onCreateDialogFragment(preference.getKey());
        }

        f.setTargetFragment(this, 0);
        f.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    /**
     * Basically a wrapper for getParentFragment which is v17+. Used by the leanback preference lib.
     *
     * @return Fragment to possibly use as a callback
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    public Fragment getCallbackFragment()
    {
        return null;
    }

    public void scrollToPreference(final String key)
    {
        scrollToPreferenceInternal(null, key);
    }

    public void scrollToPreference(final Preference preference)
    {
        scrollToPreferenceInternal(preference, null);
    }

    private void scrollToPreferenceInternal(final Preference preference, final String key)
    {
        final Runnable r = new Runnable() {
            @Override
            public void run() {
                final RecyclerView.Adapter adapter = mList.getAdapter();

                if (!(adapter instanceof
                      PreferenceGroup.PreferencePositionCallback)) {
                    if (adapter != null) {
                        throw new IllegalStateException("Adapter must implement "
                                                        + "PreferencePositionCallback");
                    } else {
                        // Adapter was set to null, so don't scroll I guess?
                        return;
                    }
                }

                final int position;

                if (preference != null) {
                    position = ((PreferenceGroup.PreferencePositionCallback) adapter)
                               .getPreferenceAdapterPosition(preference);
                } else {
                    position = ((PreferenceGroup.PreferencePositionCallback) adapter)
                               .getPreferenceAdapterPosition(key);
                }

                if (position != RecyclerView.NO_POSITION) {
                    mList.scrollToPosition(position);
                } else {
                    // Item not found, wait for an update and try again
                    adapter.registerAdapterDataObserver(
                        new ScrollToPreferenceObserver(adapter, mList, preference, key));
                }
            }
        };

        if (mList == null) {
            mSelectPreferenceRunnable = r;
        } else {
            r.run();
        }
    }

    private static class ScrollToPreferenceObserver extends RecyclerView.AdapterDataObserver
    {
        private final RecyclerView.Adapter mAdapter;
        private final RecyclerView mList;
        private final Preference mPreference;
        private final String mKey;

        public ScrollToPreferenceObserver(RecyclerView.Adapter adapter, RecyclerView list,
                                          Preference preference, String key)
        {
            mAdapter = adapter;
            mList = list;
            mPreference = preference;
            mKey = key;
        }

        private void scrollToPreference()
        {
            mAdapter.unregisterAdapterDataObserver(this);
            final int position;

            if (mPreference != null) {
                position = ((PreferenceGroup.PreferencePositionCallback) mAdapter)
                           .getPreferenceAdapterPosition(mPreference);
            } else {
                position = ((PreferenceGroup.PreferencePositionCallback) mAdapter)
                           .getPreferenceAdapterPosition(mKey);
            }

            if (position != RecyclerView.NO_POSITION) {
                mList.scrollToPosition(position);
            }
        }

        @Override
        public void onChanged()
        {
            scrollToPreference();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount)
        {
            scrollToPreference();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload)
        {
            scrollToPreference();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            scrollToPreference();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            scrollToPreference();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount)
        {
            scrollToPreference();
        }
    }

    public abstract class DividerDecoration extends RecyclerView.ItemDecoration
    {

        private Drawable mDivider;
        private int mDividerHeight;
        private boolean mAllowDividerAfterLastItem = false;

        public final boolean shouldDrawDividerBelow(View view, RecyclerView parent)
        {
            if (!(parent.getAdapter() instanceof PreferenceGroupAdapter)) {
                return false;
            }

            PreferenceGroupAdapter adapter = (PreferenceGroupAdapter) parent.getAdapter();
            int index = parent.getChildAdapterPosition(view);

            if (index == RecyclerView.NO_POSITION) {
                return false;
            }

            Preference preference = adapter.getItem(index);

            switch (preference.getDividerBelowVisibility()) {
            case DividerVisibility.ENFORCED:
                return true;

            case DividerVisibility.FORBIDDEN:
                return false;

            case DividerVisibility.UNSPECIFIED:
            default:
                if (index == adapter.getItemCount() - 1) {
                    return isAllowDividerAfterLastItem();
                }

                return shouldDrawDividerBelow(view, parent, adapter, index, preference);
            }
        }

        /**
         * Called when Preference dose not specific whether to show the divider ot not.
         *
         * @param view       Preference item view
         * @param parent     RecyclerView
         * @param adapter    PreferenceGroupAdapter
         * @param index      index, never be last
         * @param preference Preference, never be last
         * @return whether to show the divider ot not
         */
        public abstract boolean shouldDrawDividerBelow(View view, RecyclerView parent,
                PreferenceGroupAdapter adapter,
                int index, Preference preference);

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
        {
            if (mDivider == null) {
                return;
            }

            final int childCount = parent.getChildCount();
            final int width = parent.getWidth();

            for (int childViewIndex = 0; childViewIndex < childCount; childViewIndex++) {
                final View view = parent.getChildAt(childViewIndex);

                if (shouldDrawDividerBelow(view, parent)) {
                    int top = (int) view.getY() + view.getHeight();
                    mDivider.setBounds(0, top, width, top + mDividerHeight);
                    mDivider.draw(c);
                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state)
        {
            if (shouldDrawDividerBelow(view, parent)) {
                outRect.bottom = mDividerHeight;
            }
        }

        public Drawable getDivider()
        {
            return mDivider;
        }

        public void setDivider(Drawable divider)
        {
            if (divider != null) {
                mDividerHeight = divider.getIntrinsicHeight();
            } else {
                mDividerHeight = 0;
            }

            mDivider = divider;
            mList.invalidateItemDecorations();
        }

        public int getDividerHeight()
        {
            return mDividerHeight;
        }

        public void setDividerHeight(int dividerHeight)
        {
            mDividerHeight = dividerHeight;
            mList.invalidateItemDecorations();
        }

        public boolean isAllowDividerAfterLastItem()
        {
            return mAllowDividerAfterLastItem;
        }

        public void setAllowDividerAfterLastItem(boolean allowDividerAfterLastItem)
        {
            mAllowDividerAfterLastItem = allowDividerAfterLastItem;
        }
    }

    /**
     * Default DividerDecoration, divider will show between items
     */
    public class DefaultDividerDecoration extends DividerDecoration
    {

        @Override
        public boolean shouldDrawDividerBelow(View view, RecyclerView parent, PreferenceGroupAdapter adapter,
                                              int index, Preference preference)
        {
            Preference nextPreference = adapter.getItem(index + 1);
            return !(preference instanceof PreferenceCategory)
                   && !(nextPreference instanceof PreferenceCategory);
        }
    }

    /**
     * An other DividerDecoration, divider show between category (uncheckable) items
     */
    public class CategoryDivideDividerDecoration extends DefaultDividerDecoration
    {

        private static final int PADDING_DP = 8;
        private int mPadding;

        public CategoryDivideDividerDecoration()
        {
            this(Math.round(PADDING_DP * getActivity().getResources().getDisplayMetrics().density));
        }

        public CategoryDivideDividerDecoration(int padding)
        {
            this.mPadding = padding;
        }

        @Override
        public boolean shouldDrawDividerBelow(View view, RecyclerView parent, PreferenceGroupAdapter adapter,
                                              int index, Preference preference)
        {
            Preference nextPreference = adapter.getItem(index + 1);
            return !(preference instanceof PreferenceCategory)
                   && nextPreference instanceof PreferenceCategory;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state)
        {
            if (shouldDrawDividerBelow(view, parent)) {
                PreferenceGroupAdapter adapter = (PreferenceGroupAdapter) parent.getAdapter();
                int index = parent.getChildAdapterPosition(view);

                if (index < adapter.getItemCount() - 1) {
                    Preference preference = adapter.getItem(index + 1);

                    if (preference instanceof PreferenceCategory) {
                        outRect.bottom = mPadding + getDividerHeight();
                        return;
                    }
                }

                outRect.bottom = mPadding * 2 + getDividerHeight();
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state)
        {
            if (getDivider() == null) {
                return;
            }

            final int childCount = parent.getChildCount();
            final int width = parent.getWidth();

            for (int childViewIndex = 0; childViewIndex < childCount; childViewIndex++) {
                final View view = parent.getChildAt(childViewIndex);

                if (shouldDrawDividerBelow(view, parent)) {
                    int top = (int) view.getY() + view.getHeight() + mPadding;
                    getDivider().setBounds(0, top, width, top + getDividerHeight());
                    getDivider().draw(c);
                }
            }
        }
    }
}
