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
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.AbsSavedState;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.CallSuper;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.core.content.ContextCompat;
import androidx.core.content.SharedPreferencesCompat;
import androidx.core.content.res.TypedArrayUtils;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Represents the basic Preference UI building
 * block displayed by a {@link PreferenceFragment} in the form of a
 * {@link RecyclerView}. This class provides data for the
 * {@link android.view.View} to be displayed
 * in the list and associates with a {@link SharedPreferences} to
 * store/retrieve the preference data.
 * <p>
 * When specifying a preference hierarchy in XML, each element can point to a
 * subclass of {@link Preference}, similar to the view hierarchy and layouts.
 * <p>
 * This class contains a {@code key} that will be used as the key into the
 * {@link SharedPreferences}. It is up to the subclass to decide how to store
 * the value.
 *
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For information about building a settings UI with Preferences,
 * read the <a href="{@docRoot}guide/topics/ui/settings.html">Settings</a>
 * guide.</p>
 * </div>
 *
 * @attr name android:icon
 * @attr name android:key
 * @attr name android:title
 * @attr name android:summary
 * @attr name android:order
 * @attr name android:fragment
 * @attr name android:layout
 * @attr name android:widgetLayout
 * @attr name android:enabled
 * @attr name android:selectable
 * @attr name android:dependency
 * @attr name android:persistent
 * @attr name android:defaultValue
 * @attr name android:shouldDisableView
 * @attr name android:singleLineTitle
 * @attr name android:iconSpaceReserved
 */
public class Preference implements Comparable<Preference>
{
    /**
     * Specify for {@link #setOrder(int)} if a specific order is not required.
     */
    public static final int DEFAULT_ORDER = Integer.MAX_VALUE;

    private Context mContext;

    @Nullable
    private PreferenceManager mPreferenceManager;

    /**
     * The data store that should be used by this Preference to store / retrieve data. If null then
     * {@link PreferenceManager#getPreferenceDataStore()} needs to be checked. If that one is null
     * too it means that we are using {@link android.content.SharedPreferences} to store the data.
     */
    @Nullable
    private PreferenceDataStore mPreferenceDataStore;

    /**
     * Set when added to hierarchy since we need a unique ID within that
     * hierarchy.
     */
    private long mId;

    /**
     * Set true temporarily to keep {@link #onAttachedToHierarchy(PreferenceManager)} from
     * overwriting mId
     */
    private boolean mHasId;

    private OnPreferenceChangeListener mOnChangeListener;
    private OnPreferenceClickListener mOnClickListener;

    private int mOrder = DEFAULT_ORDER;
    private int mViewId = 0;
    private CharSequence mTitle;
    private CharSequence mSummary;
    /**
     * mIconResId is overridden by mIcon, if mIcon is specified.
     */
    private int mIconResId;
    private Drawable mIcon;
    private String mKey;
    private Intent mIntent;
    private String mFragment;
    private Bundle mExtras;
    private boolean mEnabled = true;
    private boolean mSelectable = true;
    private boolean mRequiresKey;
    private boolean mPersistent = true;
    private String mDependencyKey;
    private Object mDefaultValue;
    private boolean mDependencyMet = true;
    private boolean mParentDependencyMet = true;
    private boolean mVisible = true;

    @IntDef({DividerVisibility.UNSPECIFIED, DividerVisibility.ENFORCED, DividerVisibility.FORBIDDEN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DividerVisibility
    {
        /**
         * Unspecified, display or not is depend on DividerDecoration
         */
        int UNSPECIFIED = 0;
        /**
         * Forced to show the divider
         */
        int ENFORCED = 1;
        /**
         * Forbid the display of divider
         */
        int FORBIDDEN = 2;
    }

    private @DividerVisibility
    int mDividerBelowVisibility = DividerVisibility.UNSPECIFIED;

    private boolean mHasSingleLineTitleAttr;
    private boolean mSingleLineTitle = true;
    private boolean mIconSpaceReserved;

    /**
     * @see #setShouldDisableView(boolean)
     */
    private boolean mShouldDisableView = true;

    private int mLayoutResId = R.layout.preference_material;
    private int mWidgetLayoutResId;

    private OnPreferenceChangeInternalListener mListener;

    private List<Preference> mDependents;
    private PreferenceGroup mParentGroup;

    private boolean mWasDetached;
    private boolean mBaseMethodCalled;

    private final View.OnClickListener mClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            performClick(v);
        }
    };

    /**
     * Interface definition for a callback to be invoked when the value of this
     * {@link Preference} has been changed by the user and is
     * about to be set and/or persisted.  This gives the client a chance
     * to prevent setting and/or persisting the value.
     */
    public interface OnPreferenceChangeListener
    {
        /**
         * Called when a Preference has been changed by the user. This is
         * called before the state of the Preference is about to be updated and
         * before the state is persisted.
         *
         * @param preference The changed Preference.
         * @param newValue   The new value of the Preference.
         * @return True to update the state of the Preference with the new value.
         */
        boolean onPreferenceChange(Preference preference, Object newValue);
    }

    /**
     * Interface definition for a callback to be invoked when a {@link Preference} is
     * clicked.
     */
    public interface OnPreferenceClickListener
    {
        /**
         * Called when a Preference has been clicked.
         *
         * @param preference The Preference that was clicked.
         * @return True if the click was handled.
         */
        boolean onPreferenceClick(Preference preference);
    }

    /**
     * Interface definition for a callback to be invoked when this
     * {@link Preference} is changed or, if this is a group, there is an
     * addition/removal of {@link Preference}(s). This is used internally.
     */
    interface OnPreferenceChangeInternalListener
    {
        /**
         * Called when this Preference has changed.
         *
         * @param preference This preference.
         */
        void onPreferenceChange(Preference preference);

        /**
         * Called when this group has added/removed {@link Preference}(s).
         *
         * @param preference This Preference.
         */
        void onPreferenceHierarchyChange(Preference preference);

        /**
         * Called when this preference has changed its visibility.
         *
         * @param preference This Preference.
         */
        void onPreferenceVisibilityChange(Preference preference);
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of Preference allows subclasses to use their own base style
     * when they are inflating. For example, a {@link CheckBoxPreference}
     * constructor calls this version of the super class constructor and
     * supplies {@code android.R.attr.checkBoxPreferenceStyle} for
     * <var>defStyleAttr</var>. This allows the theme's checkbox preference
     * style to modify all of the base preference attributes as well as the
     * {@link CheckBoxPreference} class's attributes.
     *
     * @param context      The Context this is associated with, through which it can
     *                     access the current theme, resources,
     *                     {@link android.content.SharedPreferences}, etc.
     * @param attrs        The attributes of the XML tag that is inflating the
     *                     preference.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @param defStyleRes  A resource identifier of a style resource that
     *                     supplies default values for the view, used only if
     *                     defStyleAttr is 0 or can not be found in the theme. Can be 0
     *                     to not look for defaults.
     * @see #Preference(Context, android.util.AttributeSet)
     */
    @SuppressLint("RestrictedApi")
    public Preference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        mContext = context;

        final TypedArray a = context.obtainStyledAttributes(
                                 attrs, R.styleable.Preference, defStyleAttr, defStyleRes);

        mIconResId = TypedArrayUtils.getResourceId(a, R.styleable.Preference_icon,
                     R.styleable.Preference_android_icon, 0);

        mKey = TypedArrayUtils.getString(a, R.styleable.Preference_key,
                                         R.styleable.Preference_android_key);

        mTitle = TypedArrayUtils.getText(a, R.styleable.Preference_title,
                                         R.styleable.Preference_android_title);

        mSummary = TypedArrayUtils.getText(a, R.styleable.Preference_summary,
                                           R.styleable.Preference_android_summary);

        mOrder = TypedArrayUtils.getInt(a, R.styleable.Preference_order,
                                        R.styleable.Preference_android_order, DEFAULT_ORDER);

        mFragment = TypedArrayUtils.getString(a, R.styleable.Preference_fragment,
                                              R.styleable.Preference_android_fragment);

        mLayoutResId = TypedArrayUtils.getResourceId(a, R.styleable.Preference_layout,
                       R.styleable.Preference_android_layout, R.layout.preference_material);

        mWidgetLayoutResId = TypedArrayUtils.getResourceId(a, R.styleable.Preference_widgetLayout,
                             R.styleable.Preference_android_widgetLayout, 0);

        mEnabled = TypedArrayUtils.getBoolean(a, R.styleable.Preference_enabled,
                                              R.styleable.Preference_android_enabled, true);

        mSelectable = TypedArrayUtils.getBoolean(a, R.styleable.Preference_selectable,
                      R.styleable.Preference_android_selectable, true);

        mPersistent = TypedArrayUtils.getBoolean(a, R.styleable.Preference_persistent,
                      R.styleable.Preference_android_persistent, true);

        mDependencyKey = TypedArrayUtils.getString(a, R.styleable.Preference_dependency,
                         R.styleable.Preference_android_dependency);

        mDividerBelowVisibility = TypedArrayUtils.getInt(a, R.styleable.Preference_dividerBelowVisibility,
                                  R.styleable.Preference_dividerBelowVisibility, DividerVisibility.UNSPECIFIED);

        if (a.hasValue(R.styleable.Preference_defaultValue)) {
            mDefaultValue = onGetDefaultValue(a, R.styleable.Preference_defaultValue);
        } else if (a.hasValue(R.styleable.Preference_android_defaultValue)) {
            mDefaultValue = onGetDefaultValue(a, R.styleable.Preference_android_defaultValue);
        }

        mShouldDisableView =
            TypedArrayUtils.getBoolean(a, R.styleable.Preference_shouldDisableView,
                                       R.styleable.Preference_android_shouldDisableView, true);

        mHasSingleLineTitleAttr = a.hasValue(R.styleable.Preference_singleLineTitle);

        if (mHasSingleLineTitleAttr) {
            mSingleLineTitle = TypedArrayUtils.getBoolean(a, R.styleable.Preference_singleLineTitle,
                               R.styleable.Preference_android_singleLineTitle, true);
        }

        mIconSpaceReserved = TypedArrayUtils.getBoolean(a, R.styleable.Preference_iconSpaceReserved,
                             R.styleable.Preference_android_iconSpaceReserved, false);

        a.recycle();
    }

    /**
     * Perform inflation from XML and apply a class-specific base style. This
     * constructor of Preference allows subclasses to use their own base style
     * when they are inflating. For example, a {@link CheckBoxPreference}
     * constructor calls this version of the super class constructor and
     * supplies {@code android.R.attr.checkBoxPreferenceStyle} for
     * <var>defStyleAttr</var>. This allows the theme's checkbox preference
     * style to modify all of the base preference attributes as well as the
     * {@link CheckBoxPreference} class's attributes.
     *
     * @param context      The Context this is associated with, through which it can
     *                     access the current theme, resources,
     *                     {@link android.content.SharedPreferences}, etc.
     * @param attrs        The attributes of the XML tag that is inflating the
     *                     preference.
     * @param defStyleAttr An attribute in the current theme that contains a
     *                     reference to a style resource that supplies default values for
     *                     the view. Can be 0 to not look for defaults.
     * @see #Preference(Context, AttributeSet)
     */
    public Preference(Context context, AttributeSet attrs, int defStyleAttr)
    {
        this(context, attrs, defStyleAttr, R.style.Preference_Material);
    }

    /**
     * Constructor that is called when inflating a Preference from XML. This is
     * called when a Preference is being constructed from an XML file, supplying
     * attributes that were specified in the XML file. This version uses a
     * default style of 0, so the only attribute values applied are those in the
     * Context's Theme and the given AttributeSet.
     *
     * @param context The Context this is associated with, through which it can
     *                access the current theme, resources, {@link android.content.SharedPreferences},
     *                etc.
     * @param attrs   The attributes of the XML tag that is inflating the
     *                preference.
     * @see #Preference(Context, AttributeSet, int)
     */
    public Preference(Context context, AttributeSet attrs)
    {
        this(context, attrs, R.attr.preferenceStyle);
    }

    /**
     * Constructor to create a Preference.
     *
     * @param context The Context in which to store Preference values.
     */
    public Preference(Context context)
    {
        this(context, null);
    }

    /**
     * Called when a Preference is being inflated and the default value
     * attribute needs to be read. Since different Preference types have
     * different value types, the subclass should get and return the default
     * value which will be its value type.
     * <p>
     * For example, if the value type is String, the body of the method would
     * proxy to {@link TypedArray#getString(int)}.
     *
     * @param a     The set of attributes.
     * @param index The index of the default value attribute.
     * @return The default value of this preference type.
     */
    protected Object onGetDefaultValue(TypedArray a, int index)
    {
        return null;
    }

    /**
     * Sets an {@link Intent} to be used for
     * {@link Context#startActivity(Intent)} when this Preference is clicked.
     *
     * @param intent The intent associated with this Preference.
     */
    public void setIntent(Intent intent)
    {
        mIntent = intent;
    }

    /**
     * Return the {@link Intent} associated with this Preference.
     *
     * @return The {@link Intent} last set via {@link #setIntent(Intent)} or XML.
     */
    public Intent getIntent()
    {
        return mIntent;
    }

    /**
     * Sets the class name of a fragment to be shown when this Preference is clicked.
     *
     * @param fragment The class name of the fragment associated with this Preference.
     */
    public void setFragment(String fragment)
    {
        mFragment = fragment;
    }

    /**
     * Return the fragment class name associated with this Preference.
     *
     * @return The fragment class name last set via {@link #setFragment} or XML.
     */
    public String getFragment()
    {
        return mFragment;
    }

    /**
     * Sets a {@link PreferenceDataStore} to be used by this Preference instead of using
     * {@link android.content.SharedPreferences}.
     *
     * <p>The data store will remain assigned even if the Preference is moved around the preference
     * hierarchy. It will also override a data store propagated from the {@link PreferenceManager}
     * that owns this Preference.
     *
     * @param dataStore the {@link PreferenceDataStore} to be used by this Preference
     * @see PreferenceManager#setPreferenceDataStore(PreferenceDataStore)
     */
    public void setPreferenceDataStore(PreferenceDataStore dataStore)
    {
        mPreferenceDataStore = dataStore;
    }

    /**
     * Returns {@link PreferenceDataStore} used by this Preference. Returns {@code null} if
     * {@link android.content.SharedPreferences} is used instead.
     *
     * <p>By default preferences always use {@link android.content.SharedPreferences}. To make this
     * preference to use the {@link PreferenceDataStore} you need to assign your implementation
     * to the Preference itself via {@link #setPreferenceDataStore(PreferenceDataStore)} or to its
     * {@link PreferenceManager} via
     * {@link PreferenceManager#setPreferenceDataStore(PreferenceDataStore)}.
     *
     * @return the {@link PreferenceDataStore} used by this Preference or {@code null} if none
     */
    @Nullable
    public PreferenceDataStore getPreferenceDataStore()
    {
        if (mPreferenceDataStore != null) {
            return mPreferenceDataStore;
        } else if (mPreferenceManager != null) {
            return mPreferenceManager.getPreferenceDataStore();
        }

        return null;
    }

    /**
     * Return the extras Bundle object associated with this preference, creating
     * a new Bundle if there currently isn't one.  You can use this to get and
     * set individual extra key/value pairs.
     */
    public Bundle getExtras()
    {
        if (mExtras == null) {
            mExtras = new Bundle();
        }

        return mExtras;
    }

    /**
     * Return the extras Bundle object associated with this preference,
     * returning null if there is not currently one.
     */
    public Bundle peekExtras()
    {
        return mExtras;
    }

    /**
     * Sets the layout resource that is inflated as the {@link View} to be shown
     * for this Preference. In most cases, the default layout is sufficient for
     * custom Preference objects and only the widget layout needs to be changed.
     * <p>
     * This layout should contain a {@link ViewGroup} with ID
     * {@link android.R.id#widget_frame} to be the parent of the specific widget
     * for this Preference. It should similarly contain
     * {@link android.R.id#title} and {@link android.R.id#summary}.
     * <p>
     * It is an error to change the layout after adding the preference to a {@link PreferenceGroup}
     *
     * @param layoutResId The layout resource ID to be inflated and returned as
     *                    a {@link View}.
     * @see #setWidgetLayoutResource(int)
     */
    public void setLayoutResource(int layoutResId)
    {
        mLayoutResId = layoutResId;
    }

    /**
     * Gets the layout resource that will be shown as the {@link View} for this Preference.
     *
     * @return The layout resource ID.
     */
    public final int getLayoutResource()
    {
        return mLayoutResId;
    }

    /**
     * Sets the layout for the controllable widget portion of this Preference. This
     * is inflated into the main layout. For example, a {@link CheckBoxPreference}
     * would specify a custom layout (consisting of just the CheckBox) here,
     * instead of creating its own main layout.
     * <p>
     * It is an error to change the layout after adding the preference to a {@link PreferenceGroup}
     *
     * @param widgetLayoutResId The layout resource ID to be inflated into the
     *                          main layout.
     * @see #setLayoutResource(int)
     */
    public void setWidgetLayoutResource(int widgetLayoutResId)
    {
        mWidgetLayoutResId = widgetLayoutResId;
    }

    /**
     * Gets the layout resource for the controllable widget portion of this Preference.
     *
     * @return The layout resource ID.
     */
    public final int getWidgetLayoutResource()
    {
        return mWidgetLayoutResId;
    }

    /**
     * Binds the created View to the data for this Preference.
     * <p>
     * This is a good place to grab references to custom Views in the layout and
     * set properties on them.
     * <p>
     * Make sure to call through to the superclass's implementation.
     *
     * @param holder The ViewHolder that provides references to the views to fill in. These views
     *               will be recycled, so you should not hold a reference to them after this method
     *               returns.
     */
    public void onBindViewHolder(PreferenceViewHolder holder)
    {
        holder.itemView.setOnClickListener(mClickListener);
        holder.itemView.setId(mViewId);

        final TextView titleView = (TextView) holder.findViewById(android.R.id.title);

        if (titleView != null) {
            final CharSequence title = getTitle();

            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);

                if (mHasSingleLineTitleAttr) {
                    titleView.setSingleLine(mSingleLineTitle);
                }
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        final TextView summaryView = (TextView) holder.findViewById(android.R.id.summary);

        if (summaryView != null) {
            final CharSequence summary = getSummary();

            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }

        final ImageView imageView = (ImageView) holder.findViewById(android.R.id.icon);

        if (imageView != null) {
            if (mIconResId != 0 || mIcon != null) {
                if (mIcon == null) {
                    mIcon = ContextCompat.getDrawable(getContext(), mIconResId);
                }

                if (mIcon != null) {
                    imageView.setImageDrawable(mIcon);
                }
            }

            if (mIcon != null) {
                imageView.setVisibility(View.VISIBLE);
            } else {
                imageView.setVisibility(mIconSpaceReserved ? View.INVISIBLE : View.GONE);
            }
        }

        View imageFrame = holder.findViewById(R.id.icon_frame);

        if (imageFrame != null) {
            if (mIcon != null) {
                imageFrame.setVisibility(View.VISIBLE);
            } else {
                imageFrame.setVisibility(mIconSpaceReserved ? View.INVISIBLE : View.GONE);
            }
        }

        if (mShouldDisableView) {
            setEnabledStateOnViews(holder.itemView, isEnabled());
        } else {
            setEnabledStateOnViews(holder.itemView, true);
        }

        final boolean selectable = isSelectable();
        holder.itemView.setFocusable(selectable);
        holder.itemView.setClickable(selectable);

        /*holder.setDividerAllowedAbove(mDividerAboveVisibility);
        holder.setDividerAllowedBelow(mDividerBelowVisibility);*/
    }

    /**
     * Called when ViewHolder is recycled.
     *
     * @param holder The ViewHolder that provides references to the views to fill in. These views
     *               will be recycled, so you should not hold a reference to them after this method
     *               returns.
     */
    public void onViewRecycled(PreferenceViewHolder holder)
    {

    }

    /**
     * Makes sure the view (and any children) get the enabled state changed.
     */
    private void setEnabledStateOnViews(View v, boolean enabled)
    {
        v.setEnabled(enabled);

        if (v instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) v;

            for (int i = vg.getChildCount() - 1; i >= 0; i--) {
                setEnabledStateOnViews(vg.getChildAt(i), enabled);
            }
        }
    }

    /**
     * Sets the order of this Preference with respect to other
     * Preference objects on the same level. If this is not specified, the
     * default behavior is to sort alphabetically. The
     * {@link PreferenceGroup#setOrderingAsAdded(boolean)} can be used to order
     * Preference objects based on the order they appear in the XML.
     *
     * @param order The order for this Preference. A lower value will be shown
     *              first. Use {@link #DEFAULT_ORDER} to sort alphabetically or
     *              allow ordering from XML.
     * @see PreferenceGroup#setOrderingAsAdded(boolean)
     * @see #DEFAULT_ORDER
     */
    public void setOrder(int order)
    {
        if (order != mOrder) {
            mOrder = order;

            // Reorder the list
            notifyHierarchyChanged();
        }
    }

    /**
     * Gets the order of this Preference with respect to other Preference objects
     * on the same level.
     *
     * @return The order of this Preference.
     * @see #setOrder(int)
     */
    public int getOrder()
    {
        return mOrder;
    }

    /**
     * Set the ID that will be assigned to the overall View representing this
     * preference, once bound.
     *
     * @see View#setId(int)
     */
    public void setViewId(int viewId)
    {
        mViewId = viewId;
    }

    /**
     * Sets the title for this Preference with a CharSequence.
     * This title will be placed into the ID
     * {@link android.R.id#title} within the View bound by
     * {@link #onBindViewHolder(PreferenceViewHolder)}.
     *
     * @param title The title for this Preference.
     */
    public void setTitle(CharSequence title)
    {
        if (title == null && mTitle != null || title != null && !title.equals(mTitle)) {
            mTitle = title;
            notifyChanged();
        }
    }

    /**
     * Sets the title for this Preference with a resource ID.
     *
     * @param titleResId The title as a resource ID.
     * @see #setTitle(CharSequence)
     */
    public void setTitle(int titleResId)
    {
        setTitle(mContext.getString(titleResId));
    }

    /**
     * Returns the title of this Preference.
     *
     * @return The title.
     * @see #setTitle(CharSequence)
     */
    public CharSequence getTitle()
    {
        return mTitle;
    }

    /**
     * Sets the icon for this Preference with a Drawable.
     * This icon will be placed into the ID
     * {@link android.R.id#icon} within the View created by
     * {@link #onBindViewHolder(PreferenceViewHolder)}.
     *
     * @param icon The optional icon for this Preference.
     */
    public void setIcon(Drawable icon)
    {
        if ((icon == null && mIcon != null) || (icon != null && mIcon != icon)) {
            mIcon = icon;
            mIconResId = 0;
            notifyChanged();
        }
    }

    /**
     * Sets the icon for this Preference with a resource ID.
     *
     * @param iconResId The icon as a resource ID.
     * @see #setIcon(Drawable)
     */
    public void setIcon(int iconResId)
    {
        setIcon(ContextCompat.getDrawable(mContext, iconResId));
        mIconResId = iconResId;
    }

    /**
     * Returns the icon of this Preference.
     *
     * @return The icon.
     * @see #setIcon(Drawable)
     */
    public Drawable getIcon()
    {
        if (mIcon == null && mIconResId != 0) {
            mIcon = ContextCompat.getDrawable(mContext, mIconResId);
        }

        return mIcon;
    }

    /**
     * Returns the summary of this Preference.
     *
     * @return The summary.
     * @see #setSummary(CharSequence)
     */
    public CharSequence getSummary()
    {
        return mSummary;
    }

    /**
     * Sets the summary for this Preference with a CharSequence.
     *
     * @param summary The summary for the preference.
     */
    public void setSummary(CharSequence summary)
    {
        if (summary == null && mSummary != null || summary != null && !summary.equals(mSummary)) {
            mSummary = summary;
            notifyChanged();
        }
    }

    /**
     * Sets the summary for this Preference with a resource ID.
     *
     * @param summaryResId The summary as a resource.
     * @see #setSummary(CharSequence)
     */
    public void setSummary(int summaryResId)
    {
        setSummary(mContext.getString(summaryResId));
    }

    /**
     * Sets whether this Preference is enabled. If disabled, it will
     * not handle clicks.
     *
     * @param enabled Set true to enable it.
     */
    public void setEnabled(boolean enabled)
    {
        if (mEnabled != enabled) {
            mEnabled = enabled;

            // Enabled state can change dependent preferences' states, so notify
            notifyDependencyChange(shouldDisableDependents());

            notifyChanged();
        }
    }

    /**
     * Checks whether this Preference should be enabled in the list.
     *
     * @return True if this Preference is enabled, false otherwise.
     */
    public boolean isEnabled()
    {
        return mEnabled && mDependencyMet && mParentDependencyMet;
    }

    /**
     * Sets whether this Preference is selectable.
     *
     * @param selectable Set true to make it selectable.
     */
    public void setSelectable(boolean selectable)
    {
        if (mSelectable != selectable) {
            mSelectable = selectable;
            notifyChanged();
        }
    }

    /**
     * Checks whether this Preference should be selectable in the list.
     *
     * @return True if it is selectable, false otherwise.
     */
    public boolean isSelectable()
    {
        return mSelectable;
    }

    /**
     * Sets whether this Preference should disable its view when it gets
     * disabled.
     * <p>
     * For example, set this and {@link #setEnabled(boolean)} to false for
     * preferences that are only displaying information and 1) should not be
     * clickable 2) should not have the view set to the disabled state.
     *
     * @param shouldDisableView Set true if this preference should disable its view
     *                          when the preference is disabled.
     */
    public void setShouldDisableView(boolean shouldDisableView)
    {
        mShouldDisableView = shouldDisableView;
        notifyChanged();
    }

    /**
     * Checks whether this Preference should disable its view when it's action is disabled.
     *
     * @return True if it should disable the view.
     * @see #setShouldDisableView(boolean)
     */
    public boolean getShouldDisableView()
    {
        return mShouldDisableView;
    }

    /**
     * Sets whether this preference should be visible in the list. If false, it is excluded from
     * the adapter, but can still be retrieved using
     * {@link PreferenceFragment#findPreference(CharSequence)}.
     *
     * @param visible Set false if this preference should be hidden from the list.
     */
    public final void setVisible(boolean visible)
    {
        if (mVisible != visible) {
            mVisible = visible;

            if (mListener != null) {
                mListener.onPreferenceVisibilityChange(this);
            }
        }
    }

    /**
     * Checks whether this preference should be visible to the user in the list.
     *
     * @return True if this preference should be displayed.
     * @see #setVisible(boolean)
     */
    public final boolean isVisible()
    {
        return mVisible;
    }

    /**
     * Returns a unique ID for this Preference.  This ID should be unique across all
     * Preference objects in a hierarchy.
     *
     * @return A unique ID for this Preference.
     */
    long getId()
    {
        return mId;
    }

    /**
     * Processes a click on the preference. This includes saving the value to
     * the {@link android.content.SharedPreferences}. However, the overridden method should
     * call {@link #callChangeListener(Object)} to make sure the client wants to
     * update the preference's state with the new value.
     */
    protected void onClick()
    {
    }

    /**
     * Sets the key for this Preference, which is used as a key to the {@link SharedPreferences} or
     * {@link PreferenceDataStore}. This should be unique for the package.
     *
     * @param key The key for the preference.
     */
    public void setKey(String key)
    {
        mKey = key;

        if (mRequiresKey && !hasKey()) {
            requireKey();
        }
    }

    /**
     * Gets the key for this Preference, which is also the key used for storing values into
     * {@link SharedPreferences} or {@link PreferenceDataStore}.
     *
     * @return The key.
     */
    public String getKey()
    {
        return mKey;
    }

    /**
     * Checks whether the key is present, and if it isn't throws an exception. This should be called
     * by subclasses that persist their preferences.
     *
     * @throws IllegalStateException If there is no key assigned.
     */
    void requireKey()
    {
        if (TextUtils.isEmpty(mKey)) {
            throw new IllegalStateException("Preference does not have a key assigned.");
        }

        mRequiresKey = true;
    }

    /**
     * Checks whether this Preference has a valid key.
     *
     * @return True if the key exists and is not a blank string, false otherwise.
     */
    public boolean hasKey()
    {
        return !TextUtils.isEmpty(mKey);
    }

    /**
     * Checks whether this Preference is persistent. If it is, it stores its value(s) into
     * the persistent {@link SharedPreferences} storage by default or into
     * {@link PreferenceDataStore} if assigned.
     *
     * @return {@code true} if persistent
     */
    public boolean isPersistent()
    {
        return mPersistent;
    }

    /**
     * Checks whether, at the given time this method is called, this Preference should store/restore
     * its value(s) into the {@link SharedPreferences} or into {@link PreferenceDataStore} if
     * assigned. This, at minimum, checks whether this Preference is persistent and it currently has
     * a key. Before you save/restore from the storage, check this first.
     *
     * @return {@code true} if it should persist the value
     */
    protected boolean shouldPersist()
    {
        return mPreferenceManager != null && isPersistent() && hasKey();
    }

    /**
     * Sets whether this Preference is persistent. When persistent, it stores its value(s) into
     * the persistent {@link SharedPreferences} storage by default or into
     * {@link PreferenceDataStore} if assigned.
     *
     * @param persistent set {@code true} if it should store its value(s) into the storage.
     */
    public void setPersistent(boolean persistent)
    {
        mPersistent = persistent;
    }

    /**
     * Sets whether to constrain the title of this Preference to a single line instead of
     * letting it wrap onto multiple lines.
     *
     * @param singleLineTitle set {@code true} if the title should be constrained to one line
     * @attr ref R.styleable#Preference_android_singleLineTitle
     */
    public void setSingleLineTitle(boolean singleLineTitle)
    {
        mHasSingleLineTitleAttr = true;
        mSingleLineTitle = singleLineTitle;
    }

    /**
     * Gets whether the title of this preference is constrained to a single line.
     *
     * @return {@code true} if the title of this preference is constrained to a single line
     * @attr ref R.styleable#Preference_android_singleLineTitle
     * @see #setSingleLineTitle(boolean)
     */
    public boolean isSingleLineTitle()
    {
        return mSingleLineTitle;
    }

    /**
     * Sets whether to reserve the space of this Preference icon view when no icon is provided. If
     * set to true, the preference will be offset as if it would have the icon and thus aligned with
     * other preferences having icons.
     *
     * @param iconSpaceReserved set {@code true} if the space for the icon view should be reserved
     * @attr ref R.styleable#Preference_android_iconSpaceReserved
     */
    public void setIconSpaceReserved(boolean iconSpaceReserved)
    {
        mIconSpaceReserved = iconSpaceReserved;
        notifyChanged();
    }

    /**
     * Returns whether the space of this preference icon view is reserved.
     *
     * @return {@code true} if the space of this preference icon view is reserved
     * @attr ref R.styleable#Preference_android_iconSpaceReserved
     * @see #setIconSpaceReserved(boolean)
     */
    public boolean isIconSpaceReserved()
    {
        return mIconSpaceReserved;
    }

    /**
     * Call this method after the user changes the preference, but before the
     * internal state is set. This allows the client to ignore the user value.
     *
     * @param newValue The new value of this Preference.
     * @return True if the user value should be set as the preference
     * value (and persisted).
     */
    public boolean callChangeListener(Object newValue)
    {
        return mOnChangeListener == null || mOnChangeListener.onPreferenceChange(this, newValue);
    }

    /**
     * Sets the callback to be invoked when this Preference is changed by the
     * user (but before the internal state has been updated).
     *
     * @param onPreferenceChangeListener The callback to be invoked.
     */
    public void setOnPreferenceChangeListener(
        OnPreferenceChangeListener onPreferenceChangeListener)
    {
        mOnChangeListener = onPreferenceChangeListener;
    }

    /**
     * Returns the callback to be invoked when this Preference is changed by the
     * user (but before the internal state has been updated).
     *
     * @return The callback to be invoked.
     */
    public OnPreferenceChangeListener getOnPreferenceChangeListener()
    {
        return mOnChangeListener;
    }

    /**
     * Sets the callback to be invoked when this Preference is clicked.
     *
     * @param onPreferenceClickListener The callback to be invoked.
     */
    public void setOnPreferenceClickListener(OnPreferenceClickListener onPreferenceClickListener)
    {
        mOnClickListener = onPreferenceClickListener;
    }

    /**
     * Returns the callback to be invoked when this Preference is clicked.
     *
     * @return The callback to be invoked.
     */
    public OnPreferenceClickListener getOnPreferenceClickListener()
    {
        return mOnClickListener;
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    protected void performClick(View view)
    {
        performClick();
    }

    /**
     * Called when a click should be performed.
     *
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    public void performClick()
    {

        if (!isEnabled()) {
            return;
        }

        onClick();

        if (mOnClickListener != null && mOnClickListener.onPreferenceClick(this)) {
            return;
        }

        PreferenceManager preferenceManager = getPreferenceManager();

        if (preferenceManager != null) {
            PreferenceManager.OnPreferenceTreeClickListener listener = preferenceManager
                    .getOnPreferenceTreeClickListener();

            if (listener != null && listener.onPreferenceTreeClick(this)) {
                return;
            }
        }

        if (mIntent != null) {
            Context context = getContext();

            if (mIntent.resolveActivity(context.getPackageManager()) != null) {
                // we still need try here because of some strange condition
                try {
                    context.startActivity(mIntent);
                } catch (Exception e) {
                    Log.w("Preference", "can't start intent " + mIntent, e);
                }
            } else {
                Log.w("Preference", "can't start intent " + mIntent + " because no matching activity found");
            }
        }
    }

    /**
     * Returns the {@link android.content.Context} of this Preference.
     * Each Preference in a Preference hierarchy can be
     * from different Context (for example, if multiple activities provide preferences into a single
     * {@link PreferenceFragment}). This Context will be used to save the Preference values.
     *
     * @return The Context of this Preference.
     */
    public Context getContext()
    {
        return mContext;
    }

    /**
     * Returns the {@link android.content.SharedPreferences} where this Preference can read its
     * value(s). Usually, it's easier to use one of the helper read methods:
     * {@link #getPersistedBoolean(boolean)}, {@link #getPersistedFloat(float)},
     * {@link #getPersistedInt(int)}, {@link #getPersistedLong(long)},
     * {@link #getPersistedString(String)}.
     *
     * @return the {@link SharedPreferences} where this Preference reads its value(s). If this
     * preference is not attached to a Preference hierarchy or if a
     * {@link PreferenceDataStore} has been set, this method returns {@code null}.
     * @see #setPreferenceDataStore(PreferenceDataStore)
     */
    public SharedPreferences getSharedPreferences()
    {
        if (mPreferenceManager == null || getPreferenceDataStore() != null) {
            return null;
        }

        return mPreferenceManager.getSharedPreferences();
    }

    /**
     * Compares Preference objects based on order (if set), otherwise alphabetically on the titles.
     *
     * @param another The Preference to compare to this one.
     * @return 0 if the same; less than 0 if this Preference sorts ahead of <var>another</var>;
     * greater than 0 if this Preference sorts after <var>another</var>.
     */
    @Override
    public int compareTo(@NonNull Preference another)
    {
        if (mOrder != another.mOrder) {
            // Do order comparison
            return mOrder - another.mOrder;
        } else if (mTitle == another.mTitle) {
            // If titles are null or share same object comparison
            return 0;
        } else if (mTitle == null) {
            return 1;
        } else if (another.mTitle == null) {
            return -1;
        } else {
            // Do name comparison
            return mTitle.toString().compareToIgnoreCase(another.mTitle.toString());
        }
    }

    /**
     * Sets the internal change listener.
     *
     * @param listener The listener.
     * @see #notifyChanged()
     */
    final void setOnPreferenceChangeInternalListener(OnPreferenceChangeInternalListener listener)
    {
        mListener = listener;
    }

    /**
     * Should be called when the data of this {@link Preference} has changed.
     */
    protected void notifyChanged()
    {
        if (mListener != null) {
            mListener.onPreferenceChange(this);
        }
    }

    /**
     * Should be called when a Preference has been
     * added/removed from this group, or the ordering should be
     * re-evaluated.
     */
    protected void notifyHierarchyChanged()
    {
        if (mListener != null) {
            mListener.onPreferenceHierarchyChange(this);
        }
    }

    /**
     * Gets the {@link PreferenceManager} that manages this Preference object's tree.
     *
     * @return The {@link PreferenceManager}.
     */
    public PreferenceManager getPreferenceManager()
    {
        return mPreferenceManager;
    }

    /**
     * Called when this Preference has been attached to a Preference hierarchy.
     * Make sure to call the super implementation.
     *
     * @param preferenceManager The PreferenceManager of the hierarchy.
     */
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager)
    {
        mPreferenceManager = preferenceManager;

        if (!mHasId) {
            mId = preferenceManager.getNextId();
        }

        dispatchSetInitialValue();
    }

    /**
     * Called from {@link PreferenceGroup} to pass in an ID for reuse
     *
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    protected void onAttachedToHierarchy(PreferenceManager preferenceManager, long id)
    {
        mId = id;
        mHasId = true;

        try {
            onAttachedToHierarchy(preferenceManager);
        }

        finally {
            mHasId = false;
        }
    }

    /**
     * Assigns a {@link PreferenceGroup} as the parent of this Preference. Set null to remove
     * the current parent.
     *
     * @param parentGroup Parent preference group of this Preference or null if none.
     */
    void assignParent(@Nullable PreferenceGroup parentGroup)
    {
        mParentGroup = parentGroup;
    }

    /**
     * Called when the Preference hierarchy has been attached to the
     * list of preferences. This can also be called when this
     * Preference has been attached to a group that was already attached
     * to the list of preferences.
     */
    public void onAttached()
    {
        // At this point, the hierarchy that this preference is in is connected
        // with all other preferences.
        registerDependency();
    }

    /**
     * Called when the Preference hierarchy has been detached from the
     * list of preferences. This can also be called when this
     * Preference has been removed from a group that was attached
     * to the list of preferences.
     */
    public void onDetached()
    {
        unregisterDependency();
        mWasDetached = true;
    }

    /**
     * Returns true if {@link #onDetached()} was called. Used for handling the case when a
     * preference was removed, modified, and re-added to a {@link PreferenceGroup}
     *
     * @hide
     */
    public final boolean wasDetached()
    {
        return mWasDetached;
    }

    /**
     * Clears the {@link #wasDetached()} status
     *
     * @hide
     */
    public final void clearWasDetached()
    {
        mWasDetached = false;
    }

    private void registerDependency()
    {

        if (TextUtils.isEmpty(mDependencyKey)) {
            return;
        }

        Preference preference = findPreferenceInHierarchy(mDependencyKey);

        if (preference != null) {
            preference.registerDependent(this);
        } else {
            throw new IllegalStateException("Dependency \"" + mDependencyKey
                                            + "\" not found for preference \"" + mKey + "\" (title: \"" + mTitle + "\"");
        }
    }

    private void unregisterDependency()
    {
        if (mDependencyKey != null) {
            final Preference oldDependency = findPreferenceInHierarchy(mDependencyKey);

            if (oldDependency != null) {
                oldDependency.unregisterDependent(this);
            }
        }
    }

    /**
     * Finds a Preference in this hierarchy (the whole thing,
     * even above/below your {@link PreferenceScreen} screen break) with the given
     * key.
     * <p>
     * This only functions after we have been attached to a hierarchy.
     *
     * @param key The key of the Preference to find.
     * @return The Preference that uses the given key.
     */
    protected Preference findPreferenceInHierarchy(String key)
    {
        if (TextUtils.isEmpty(key) || mPreferenceManager == null) {
            return null;
        }

        return mPreferenceManager.findPreference(key);
    }

    /**
     * Adds a dependent Preference on this Preference so we can notify it.
     * Usually, the dependent Preference registers itself (it's good for it to
     * know it depends on something), so please use
     * {@link Preference#setDependency(String)} on the dependent Preference.
     *
     * @param dependent The dependent Preference that will be enabled/disabled
     *                  according to the state of this Preference.
     */
    private void registerDependent(Preference dependent)
    {
        if (mDependents == null) {
            mDependents = new ArrayList<Preference>();
        }

        mDependents.add(dependent);

        dependent.onDependencyChanged(this, shouldDisableDependents());
    }

    /**
     * Removes a dependent Preference on this Preference.
     *
     * @param dependent The dependent Preference that will be enabled/disabled
     *                  according to the state of this Preference.
     */
    private void unregisterDependent(Preference dependent)
    {
        if (mDependents != null) {
            mDependents.remove(dependent);
        }
    }

    /**
     * Notifies any listening dependents of a change that affects the
     * dependency.
     *
     * @param disableDependents Whether this Preference should disable
     *                          its dependents.
     */
    public void notifyDependencyChange(boolean disableDependents)
    {
        final List<Preference> dependents = mDependents;

        if (dependents == null) {
            return;
        }

        final int dependentsCount = dependents.size();

        for (int i = 0; i < dependentsCount; i++) {
            dependents.get(i).onDependencyChanged(this, disableDependents);
        }
    }

    /**
     * Called when the dependency changes.
     *
     * @param dependency       The Preference that this Preference depends on.
     * @param disableDependent Set true to disable this Preference.
     */
    public void onDependencyChanged(Preference dependency, boolean disableDependent)
    {
        if (mDependencyMet == disableDependent) {
            mDependencyMet = !disableDependent;

            // Enabled state can change dependent preferences' states, so notify
            notifyDependencyChange(shouldDisableDependents());

            notifyChanged();
        }
    }

    /**
     * Called when the implicit parent dependency changes.
     *
     * @param parent       The Preference that this Preference depends on.
     * @param disableChild Set true to disable this Preference.
     */
    public void onParentChanged(Preference parent, boolean disableChild)
    {
        if (mParentDependencyMet == disableChild) {
            mParentDependencyMet = !disableChild;

            // Enabled state can change dependent preferences' states, so notify
            notifyDependencyChange(shouldDisableDependents());

            notifyChanged();
        }
    }

    /**
     * Checks whether this preference's dependents should currently be
     * disabled.
     *
     * @return True if the dependents should be disabled, otherwise false.
     */
    public boolean shouldDisableDependents()
    {
        return !isEnabled();
    }

    /**
     * Sets the key of a Preference that this Preference will depend on. If that
     * Preference is not set or is off, this Preference will be disabled.
     *
     * @param dependencyKey The key of the Preference that this depends on.
     */
    public void setDependency(String dependencyKey)
    {
        // Unregister the old dependency, if we had one
        unregisterDependency();

        // Register the new
        mDependencyKey = dependencyKey;
        registerDependency();
    }

    /**
     * Returns the key of the dependency on this Preference.
     *
     * @return The key of the dependency.
     * @see #setDependency(String)
     */
    public String getDependency()
    {
        return mDependencyKey;
    }

    /**
     * Returns the {@link PreferenceGroup} which is this Preference assigned to or null if this
     * preference is not assigned to any group or is a root Preference.
     *
     * @return The parent PreferenceGroup or null if not attached to any.
     */
    @Nullable
    public PreferenceGroup getParent()
    {
        return mParentGroup;
    }

    /**
     * Called when this Preference is being removed from the hierarchy. You
     * should remove any references to this Preference that you know about. Make
     * sure to call through to the superclass implementation.
     */
    protected void onPrepareForRemoval()
    {
        unregisterDependency();
    }

    /**
     * Sets the default value for this Preference, which will be set either if
     * persistence is off or persistence is on and the preference is not found
     * in the persistent storage.
     *
     * @param defaultValue The default value.
     */
    public void setDefaultValue(Object defaultValue)
    {
        mDefaultValue = defaultValue;
    }

    private void dispatchSetInitialValue()
    {
        if (getPreferenceDataStore() != null) {
            onSetInitialValue(mDefaultValue);
            return;
        }

        // By now, we know if we are persistent.
        final boolean shouldPersist = shouldPersist();

        if (!shouldPersist || !getSharedPreferences().contains(mKey)) {
            if (mDefaultValue != null) {
                onSetInitialValue(mDefaultValue);
            }
        } else {
            onSetInitialValue(null);
        }
    }

    /**
     * Implement this to set the initial value of the preference.
     *
     * <p>If you are persisting values to {@link SharedPreferences} or a {@link PreferenceDataStore}
     * you should restore the saved value for the preference.
     *
     * <p>If you are not persisting values, or there is no value saved for the preference, you
     * should set the value of the preference to <var>defaultValue</var>.
     *
     * @param defaultValue The default value for the preference if set, otherwise {@code null}.
     */
    protected void onSetInitialValue(@Nullable Object defaultValue) {}

    public @DividerVisibility
    int getDividerBelowVisibility()
    {
        return mDividerBelowVisibility;
    }

    public void setDividerBelowVisibility(@DividerVisibility int dividerBelowVisibility)
    {
        mDividerBelowVisibility = dividerBelowVisibility;
    }

    private void tryCommit(@NonNull SharedPreferences.Editor editor)
    {
        if (mPreferenceManager.shouldCommit()) {
            SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
        }
    }

    /**
     * Attempts to persist a {@link String} if this Preference is persistent.
     *
     * <p>The returned value doesn't reflect whether the given value was persisted, since we may not
     * necessarily commit if there will be a batch commit later.
     *
     * @param value The value to persist.
     * @return {@code true} if the Preference is persistent, {@code false} otherwise
     * @see #getPersistedString(String)
     */
    protected boolean persistString(String value)
    {
        if (!shouldPersist()) {
            return false;
        }

        // Shouldn't store null
        if (TextUtils.equals(value, getPersistedString(null))) {
            // It's already there, so the same as persisting
            return true;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            dataStore.putString(mKey, value);
        } else {
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putString(mKey, value);
            tryCommit(editor);
        }

        return true;
    }

    /**
     * Attempts to get a persisted set of Strings if this Preference is persistent.
     *
     * @param defaultReturnValue The default value to return if either the
     *                           Preference is not persistent or the Preference is not in the
     *                           shared preferences.
     * @return the value from the storage or the default return value
     * @see #persistString(String)
     */
    protected String getPersistedString(String defaultReturnValue)
    {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            return dataStore.getString(mKey, defaultReturnValue);
        }

        return mPreferenceManager.getSharedPreferences().getString(mKey, defaultReturnValue);
    }

    /**
     * Attempts to persist a set of Strings if this Preference is persistent.
     *
     * <p>The returned value doesn't reflect whether the given value was persisted, since we may not
     * necessarily commit if there will be a batch commit later.
     *
     * @param values the values to persist
     * @return {@code true} if the Preference is persistent, {@code false} otherwise
     * @see #getPersistedStringSet(Set)
     */
    public boolean persistStringSet(Set<String> values)
    {
        if (!shouldPersist()) {
            return false;
        }

        // Shouldn't store null
        if (values.equals(getPersistedStringSet(null))) {
            // It's already there, so the same as persisting
            return true;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            dataStore.putStringSet(mKey, values);
        } else {
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putStringSet(mKey, values);
            tryCommit(editor);
        }

        return true;
    }

    /**
     * Attempts to get a persisted set of Strings if this Preference is persistent.
     *
     * @param defaultReturnValue the default value to return if either this Preference is not
     *                           persistent or this Preference is not present
     * @return the value from the storage or the default return value
     * @see #persistStringSet(Set)
     */
    public Set<String> getPersistedStringSet(Set<String> defaultReturnValue)
    {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            return dataStore.getStringSet(mKey, defaultReturnValue);
        }

        return mPreferenceManager.getSharedPreferences().getStringSet(mKey, defaultReturnValue);
    }

    /**
     * Attempts to persist an {@link Integer} if this Preference is persistent.
     *
     * <p>The returned value doesn't reflect whether the given value was persisted, since we may not
     * necessarily commit if there will be a batch commit later.
     *
     * @param value The value to persist.
     * @return {@code true} if the Preference is persistent, {@code false} otherwise
     * @see #persistString(String)
     * @see #getPersistedInt(int)
     */
    protected boolean persistInt(int value)
    {
        if (!shouldPersist()) {
            return false;
        }

        if (value == getPersistedInt(~value)) {
            // It's already there, so the same as persisting
            return true;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            dataStore.putInt(mKey, value);
        } else {
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putInt(mKey, value);
            tryCommit(editor);
        }

        return true;
    }

    /**
     * Attempts to get a persisted {@link Integer} if this Preference is persistent.
     *
     * @param defaultReturnValue The default value to return if either this
     *                           Preference is not persistent or this Preference is not in the
     *                           SharedPreferences.
     * @return the value from the storage or the default return value
     * @see #getPersistedString(String)
     * @see #persistInt(int)
     */
    protected int getPersistedInt(int defaultReturnValue)
    {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            return dataStore.getInt(mKey, defaultReturnValue);
        }

        return mPreferenceManager.getSharedPreferences().getInt(mKey, defaultReturnValue);
    }

    /**
     * Attempts to persist a {@link Float} if this Preference is persistent.
     *
     * <p>The returned value doesn't reflect whether the given value was persisted, since we may not
     * necessarily commit if there will be a batch commit later.
     *
     * @param value The value to persist.
     * @return {@code true} if the Preference is persistent, {@code false} otherwise
     * @see #persistString(String)
     * @see #getPersistedFloat(float)
     */
    protected boolean persistFloat(float value)
    {
        if (!shouldPersist()) {
            return false;
        }

        if (value == getPersistedFloat(Float.NaN)) {
            // It's already there, so the same as persisting
            return true;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            dataStore.putFloat(mKey, value);
        } else {
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putFloat(mKey, value);
            tryCommit(editor);
        }

        return true;
    }

    /**
     * Attempts to get a persisted {@link Float} if this Preference is persistent.
     *
     * @param defaultReturnValue The default value to return if either this
     *                           Preference is not persistent or this Preference is not in the
     *                           SharedPreferences.
     * @return the value from the storage or the default return value
     * @see #getPersistedString(String)
     * @see #persistFloat(float)
     */
    protected float getPersistedFloat(float defaultReturnValue)
    {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            return dataStore.getFloat(mKey, defaultReturnValue);
        }

        return mPreferenceManager.getSharedPreferences().getFloat(mKey, defaultReturnValue);
    }

    /**
     * Attempts to persist a {@link Long} if this Preference is persistent.
     *
     * <p>The returned value doesn't reflect whether the given value was persisted, since we may not
     * necessarily commit if there will be a batch commit later.
     *
     * @param value The value to persist.
     * @return {@code true} if the Preference is persistent, {@code false} otherwise
     * @see #persistString(String)
     * @see #getPersistedLong(long)
     */
    protected boolean persistLong(long value)
    {
        if (!shouldPersist()) {
            return false;
        }

        if (value == getPersistedLong(~value)) {
            // It's already there, so the same as persisting
            return true;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            dataStore.putLong(mKey, value);
        } else {
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putLong(mKey, value);
            tryCommit(editor);
        }

        return true;
    }

    /**
     * Attempts to get a persisted {@link Long} if this Preference is persistent.
     *
     * @param defaultReturnValue The default value to return if either this
     *                           Preference is not persistent or this Preference is not in the
     *                           SharedPreferences.
     * @return the value from the storage or the default return value
     * @see #getPersistedString(String)
     * @see #persistLong(long)
     */
    protected long getPersistedLong(long defaultReturnValue)
    {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            return dataStore.getLong(mKey, defaultReturnValue);
        }

        return mPreferenceManager.getSharedPreferences().getLong(mKey, defaultReturnValue);
    }

    /**
     * Attempts to persist a {@link Boolean} if this Preference is persistent.
     *
     * <p>The returned value doesn't reflect whether the given value was persisted, since we may not
     * necessarily commit if there will be a batch commit later.
     *
     * @param value The value to persist.
     * @return {@code true} if the Preference is persistent, {@code false} otherwise
     * @see #persistString(String)
     * @see #getPersistedBoolean(boolean)
     */
    protected boolean persistBoolean(boolean value)
    {
        if (!shouldPersist()) {
            return false;
        }

        if (value == getPersistedBoolean(!value)) {
            // It's already there, so the same as persisting
            return true;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            dataStore.putBoolean(mKey, value);
        } else {
            SharedPreferences.Editor editor = mPreferenceManager.getEditor();
            editor.putBoolean(mKey, value);
            tryCommit(editor);
        }

        return true;
    }

    /**
     * Attempts to get a persisted {@link Boolean} if this Preference is persistent.
     *
     * @param defaultReturnValue The default value to return if either this
     *                           Preference is not persistent or this Preference is not in the
     *                           SharedPreferences.
     * @return the value from the storage or the default return value
     * @see #getPersistedString(String)
     * @see #persistBoolean(boolean)
     */
    protected boolean getPersistedBoolean(boolean defaultReturnValue)
    {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        PreferenceDataStore dataStore = getPreferenceDataStore();

        if (dataStore != null) {
            return dataStore.getBoolean(mKey, defaultReturnValue);
        }

        return mPreferenceManager.getSharedPreferences().getBoolean(mKey, defaultReturnValue);
    }

    @Override
    public String toString()
    {
        return getFilterableStringBuilder().toString();
    }

    /**
     * Returns the text that will be used to filter this Preference depending on
     * user input.
     * <p>
     * If overriding and calling through to the superclass, make sure to prepend
     * your additions with a space.
     *
     * @return Text as a {@link StringBuilder} that will be used to filter this
     * preference. By default, this is the title and summary
     * (concatenated with a space).
     */
    StringBuilder getFilterableStringBuilder()
    {
        StringBuilder sb = new StringBuilder();
        CharSequence title = getTitle();

        if (!TextUtils.isEmpty(title)) {
            sb.append(title).append(' ');
        }

        CharSequence summary = getSummary();

        if (!TextUtils.isEmpty(summary)) {
            sb.append(summary).append(' ');
        }

        if (sb.length() > 0) {
            // Drop the last space
            sb.setLength(sb.length() - 1);
        }

        return sb;
    }

    /**
     * Store this Preference hierarchy's frozen state into the given container.
     *
     * @param container The Bundle in which to save the instance of this Preference.
     * @see #restoreHierarchyState
     * @see #onSaveInstanceState
     */
    public void saveHierarchyState(Bundle container)
    {
        dispatchSaveInstanceState(container);
    }

    /**
     * Called by {@link #saveHierarchyState} to store the instance for this Preference and its
     * children. May be overridden to modify how the save happens for children. For example, some
     * Preference objects may want to not store an instance for their children.
     *
     * @param container The Bundle in which to save the instance of this Preference.
     * @see #saveHierarchyState
     * @see #onSaveInstanceState
     */
    void dispatchSaveInstanceState(Bundle container)
    {
        if (hasKey()) {
            mBaseMethodCalled = false;
            Parcelable state = onSaveInstanceState();

            if (!mBaseMethodCalled) {
                throw new IllegalStateException(
                    "Derived class did not call super.onSaveInstanceState()");
            }

            if (state != null) {
                container.putParcelable(mKey, state);
            }
        }
    }

    /**
     * Hook allowing a Preference to generate a representation of its internal
     * state that can later be used to create a new instance with that same
     * state. This state should only contain information that is not persistent
     * or can be reconstructed later.
     *
     * @return A Parcelable object containing the current dynamic state of
     * this Preference, or null if there is nothing interesting to save.
     * The default implementation returns null.
     * @see #onRestoreInstanceState
     * @see #saveHierarchyState
     */
    protected Parcelable onSaveInstanceState()
    {
        mBaseMethodCalled = true;
        return BaseSavedState.EMPTY_STATE;
    }

    /**
     * Restore this Preference hierarchy's previously saved state from the given container.
     *
     * @param container The Bundle that holds the previously saved state.
     * @see #saveHierarchyState
     * @see #onRestoreInstanceState
     */
    public void restoreHierarchyState(Bundle container)
    {
        dispatchRestoreInstanceState(container);
    }

    /**
     * Called by {@link #restoreHierarchyState} to retrieve the saved state for this
     * Preference and its children. May be overridden to modify how restoring
     * happens to the children of a Preference. For example, some Preference objects may
     * not want to save state for their children.
     *
     * @param container The Bundle that holds the previously saved state.
     * @see #restoreHierarchyState
     * @see #onRestoreInstanceState
     */
    void dispatchRestoreInstanceState(Bundle container)
    {
        if (hasKey()) {
            Parcelable state = container.getParcelable(mKey);

            if (state != null) {
                mBaseMethodCalled = false;
                onRestoreInstanceState(state);

                if (!mBaseMethodCalled) {
                    throw new IllegalStateException(
                        "Derived class did not call super.onRestoreInstanceState()");
                }
            }
        }
    }

    /**
     * Hook allowing a Preference to re-apply a representation of its internal
     * state that had previously been generated by {@link #onSaveInstanceState}.
     * This function will never be called with a null state.
     *
     * @param state The saved state that had previously been returned by
     *              {@link #onSaveInstanceState}.
     * @see #onSaveInstanceState
     * @see #restoreHierarchyState
     */
    protected void onRestoreInstanceState(Parcelable state)
    {
        mBaseMethodCalled = true;

        if (state != BaseSavedState.EMPTY_STATE && state != null) {
            throw new IllegalArgumentException("Wrong state class -- expecting Preference State");
        }
    }

    /**
     * Initializes an {@link android.view.accessibility.AccessibilityNodeInfo} with information
     * about the View for this Preference.
     */
    @CallSuper
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat info)
    {
    }

    /**
     * A base class for managing the instance state of a {@link Preference}.
     */
    public static class BaseSavedState extends AbsSavedState
    {
        public BaseSavedState(Parcel source)
        {
            super(source);
        }

        public BaseSavedState(Parcelable superState)
        {
            super(superState);
        }

        public static final Parcelable.Creator<BaseSavedState> CREATOR =
            new Parcelable.Creator<BaseSavedState>()
        {
            @Override
            public BaseSavedState createFromParcel(Parcel in) {
                return new BaseSavedState(in);
            }

            @Override
            public BaseSavedState[] newArray(int size) {
                return new BaseSavedState[size];
            }
        };
    }

}
