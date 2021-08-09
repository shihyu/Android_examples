package com.eventyay.organizer.core.attendee.history;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.data.attendee.Attendee;
import com.eventyay.organizer.data.attendee.CheckInDetail;
import com.eventyay.organizer.databinding.CheckInHistoryFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;

import java.util.List;

import javax.inject.Inject;

public class CheckInHistoryFragment extends BaseFragment implements CheckInHistoryView {

    private static final String ATTENDEE_KEY = "attendee_id";
    private long attendeeId;
    private Context context;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CheckInHistoryViewModel checkInHistoryViewModel;

    private CheckInHistoryFragmentBinding binding;
    private SwipeRefreshLayout refreshLayout;
    private CheckInHistoryAdapter checkInHistoryAdapter;

    public static CheckInHistoryFragment newInstance(long attendeeId) {
        CheckInHistoryFragment fragment = new CheckInHistoryFragment();
        Bundle args = new Bundle();
        args.putLong(ATTENDEE_KEY, attendeeId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getContext();
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            attendeeId = getArguments().getLong(ATTENDEE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.check_in_history_fragment, container, false);
        checkInHistoryViewModel = ViewModelProviders.of(this, viewModelFactory).get(CheckInHistoryViewModel.class);

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        setupRecyclerView();
        setupRefreshListener();

        checkInHistoryViewModel.loadAttendee(attendeeId, false);
        checkInHistoryViewModel.getProgress().observe(this, this::showProgress);
        checkInHistoryViewModel.getError().observe(this, this::showError);
        checkInHistoryViewModel.getAttendee().observe(this, this::showAttendee);
        checkInHistoryViewModel.getCheckInHistory().observe(this, this::showResults);
    }

    @Override
    protected int getTitle() {
        return R.string.activity_log;
    }

    @Override
    public void onStop() {
        super.onStop();
        refreshLayout.setOnRefreshListener(null);
    }

    private void setupRecyclerView() {
        checkInHistoryAdapter = new CheckInHistoryAdapter();

        RecyclerView recyclerView = binding.checkInHistoryRecyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(checkInHistoryAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void setupRefreshListener() {
        refreshLayout = binding.swipeContainer;
        refreshLayout.setOnRefreshListener(() -> {
            refreshLayout.setRefreshing(false);
            checkInHistoryViewModel.loadAttendee(attendeeId, true);
        });
    }

    @Override
    public void showError(String error) {
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        ViewUtils.showView(binding.progressBar, show);
    }

    @Override
    public void onRefreshComplete(boolean success) {
        // Nothing to do
    }

    @Override
    public void showResults(List<CheckInDetail> checkInDetails) {
        if (checkInDetails.isEmpty()) {
            showEmptyView(true);
            return;
        }

        showEmptyView(false);
        checkInHistoryAdapter.setCheckInHistory(checkInDetails);
    }

    public void showAttendee(Attendee attendee) {
        binding.setAttendee(attendee);
    }

    @Override
    public void showEmptyView(boolean show) {
        ViewUtils.showView(binding.emptyView, show);
    }
}
