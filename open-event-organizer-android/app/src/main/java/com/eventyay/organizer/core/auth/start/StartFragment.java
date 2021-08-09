package com.eventyay.organizer.core.auth.start;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.eventyay.organizer.R;
import com.eventyay.organizer.common.mvp.view.BaseFragment;
import com.eventyay.organizer.core.auth.SharedViewModel;
import com.eventyay.organizer.core.auth.login.LoginFragment;
import com.eventyay.organizer.core.auth.signup.SignUpFragment;
import com.eventyay.organizer.core.main.MainActivity;
import com.eventyay.organizer.data.auth.model.EmailRequest;
import com.eventyay.organizer.databinding.StartFragmentBinding;
import com.eventyay.organizer.ui.ViewUtils;
import com.eventyay.organizer.utils.ValidateUtils;

import java.util.ArrayList;
import java.util.Set;

import javax.inject.Inject;

import br.com.ilhasoft.support.validation.Validator;

import static com.eventyay.organizer.ui.ViewUtils.showView;
import static com.eventyay.organizer.utils.ValidateUtils.validate;
import static com.eventyay.organizer.utils.ValidateUtils.validateUrl;

public class StartFragment extends BaseFragment implements StartView {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private StartViewModel startFragmentViewModel;
    private StartFragmentBinding binding;
    private Validator validator;
    private SharedViewModel sharedViewModel;


    public static StartFragment newInstance() {
        return new StartFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.start_fragment, container, false);
        startFragmentViewModel = ViewModelProviders.of(this, viewModelFactory).get(StartViewModel.class);
        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        validator = new Validator(binding);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        startFragmentViewModel.getLoginStatus().observe(this, (loginStatus) -> handleIntent());
        startFragmentViewModel.getProgress().observe(this, this::showProgress);
        startFragmentViewModel.getError().observe(this, this::showError);
        startFragmentViewModel.getEmailList().observe(this, this::attachEmails);
        startFragmentViewModel.getEmailRequestModel().observe(this, emailRequest -> binding.setEmailRequest(emailRequest));
        startFragmentViewModel.getIsEmailRegistered().observe(this, this::toNextAuthFragment);
        sharedViewModel.getEmail().observe(this, email -> {
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setEmail(email);
            startFragmentViewModel.getEmailRequestModel().setValue(emailRequest);
        });
        startFragmentViewModel.getWarningDialog().observe(this, showMessage -> {
            new AlertDialog.Builder(requireActivity())
                .setTitle("Message")
                .setMessage(R.string.dialog_message)
                .setPositiveButton(R.string.ok, ((dialog, which) -> {
                    startFragmentViewModel.setWarningPreference();
                    dialog.dismiss();
                })).show();

        });

        binding.url.toggleUrl.setVisibility(View.VISIBLE);

        validate(binding.url.baseUrlLayout, ValidateUtils::validateUrl, getResources().getString(R.string.url_validation_error));

        binding.url.toggleUrl.setOnClickListener(view -> {

            if (binding.url.baseUrlLayout.getVisibility() == View.VISIBLE) {
                binding.url.toggleUrl.setText(getString(R.string.use_another_url));
                binding.url.baseUrlLayout.setVisibility(View.GONE);
            } else {
                binding.url.toggleUrl.setText(getString(R.string.use_default_url));
                binding.url.baseUrlLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.btnStart.setOnClickListener(view -> {
            if (!validator.validate())
                return;

            String url = binding.url.baseUrl.getText().toString().trim();

            boolean isBaseUrlLayoutVisible = binding.url.baseUrlLayout.getVisibility() == View.VISIBLE;

            if (isBaseUrlLayoutVisible && !validateUrl(url)) {
                return;
            }

            ViewUtils.hideKeyboard(view);
            startFragmentViewModel.setBaseUrl(url, !isBaseUrlLayoutVisible);
            startFragmentViewModel.checkIsEmailRegistered(startFragmentViewModel.getEmailRequestModel().getValue());
        });

        validate(binding.emailLayout, ValidateUtils::validateEmail, getResources().getString(R.string.email_validation_error));
    }

    @Override
    public void handleIntent() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void toNextAuthFragment(boolean isEmailRegistered) {

        sharedViewModel.setEmail(binding.getEmailRequest().getEmail());

        Fragment fragment;
        if (isEmailRegistered) {
            fragment = new LoginFragment();
        } else {
            fragment = new SignUpFragment();
        }
        setupTransitionAnimation(fragment);
        getFragmentManager().beginTransaction()
            .addSharedElement(binding.emailLayout, ViewCompat.getTransitionName(binding.emailLayout))
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit();
    }

    private void setupTransitionAnimation(Fragment fragment) {
        Fade exitFade = new Fade();
        exitFade.setDuration(100);
        setExitTransition(exitFade);
        fragment.setReturnTransition(exitFade);

        ChangeBounds changeBoundsTransition = new ChangeBounds();
        fragment.setSharedElementEnterTransition(changeBoundsTransition);

        Fade enterFade = new Fade();
        enterFade.setStartDelay(300);
        enterFade.setDuration(300);
        fragment.setEnterTransition(enterFade);
        setReenterTransition(enterFade);
    }

    @Override
    protected int getTitle() {
        return R.string.get_started;
    }

    @Override
    public void onStop() {
        super.onStop();
        binding.emailDropdown.setAdapter(null);
    }

    @Override
    public void showError(String error) {
        ViewUtils.hideKeyboard(binding.getRoot());
        ViewUtils.showSnackbar(binding.getRoot(), error);
    }

    @Override
    public void showProgress(boolean show) {
        showView(binding.progressBar, show);
    }

    @Override
    public void attachEmails(Set<String> emails) {
        binding.emailDropdown.setAdapter(
            new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<>(emails))
        );
    }
}
