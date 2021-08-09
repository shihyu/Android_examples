package com.eventyay.organizer.core.auth.login;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eventyay.organizer.BuildConfig;
import com.eventyay.organizer.common.Constants;
import com.eventyay.organizer.common.livedata.SingleEventLiveData;
import com.eventyay.organizer.data.Preferences;
import com.eventyay.organizer.data.auth.AuthService;
import com.eventyay.organizer.data.auth.model.Login;
import com.eventyay.organizer.data.auth.model.RequestToken;
import com.eventyay.organizer.data.encryption.EncryptionService;
import com.eventyay.organizer.data.network.HostSelectionInterceptor;
import com.eventyay.organizer.utils.ErrorUtils;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import retrofit2.HttpException;

import static com.eventyay.organizer.common.Constants.PREF_USER_EMAIL;

public class LoginViewModel extends ViewModel {

    private final AuthService loginModel;
    private final EncryptionService encryptionService;
    private final Login login = new Login();
    private final HostSelectionInterceptor interceptor;
    private final Preferences sharedPreferenceModel;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String PREF_USER_PASSWORD = "user_password";

    private final MutableLiveData<Boolean> progress = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Login> decryptedLogin = new MutableLiveData<>();
    private final SingleEventLiveData<Void> actionLogin = new SingleEventLiveData<>();
    private final SingleEventLiveData<Boolean> actionOpenResetPassword = new SingleEventLiveData<>();
    private final SingleEventLiveData<String> baseUrlLiveData = new SingleEventLiveData<>();

    @Inject
    public LoginViewModel(AuthService loginModel, HostSelectionInterceptor interceptor,
                          Preferences sharedPreferenceModel, EncryptionService encryptionService) {
        this.loginModel = loginModel;
        this.interceptor = interceptor;
        this.sharedPreferenceModel = sharedPreferenceModel;
        this.encryptionService = encryptionService;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getProgress() {
        return progress;
    }

    public LiveData<Void> getActionLogIn() {
        return actionLogin;
    }

    public LiveData<Boolean> getActionOpenResetPassword() {
        return actionOpenResetPassword;
    }

    public LiveData<String> getBaseUrl() {
        return baseUrlLiveData;
    }

    private void encryptUserCredentials() {
        String encryptedEmail = encryptionService.encrypt(login.getEmail());
        String encryptedPassword = encryptionService.encrypt(login.getPassword());
        sharedPreferenceModel.saveString(PREF_USER_EMAIL, encryptedEmail);
        sharedPreferenceModel.saveString(PREF_USER_PASSWORD, encryptedPassword);
    }

    //for logging into the app
    public void login() {
        compositeDisposable.add(loginModel.login(login)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> {
                    encryptUserCredentials();
                    actionLogin.call();
                },
                throwable -> {
                    if (throwable instanceof HttpException) {
                        int errorCode = ((HttpException) throwable).code();

                        if (errorCode == 401)
                            error.setValue("Please check the credentials you have entered");
                        else
                            error.setValue(ErrorUtils.getMessage(throwable).toString());
                    } else {
                        error.setValue(ErrorUtils.getMessage(throwable).toString());
                    }
                }));
    }

    public void setBaseUrl() {
        String baseUrl = sharedPreferenceModel.getString(Constants.SHARED_PREFS_BASE_URL,
            BuildConfig.DEFAULT_BASE_URL);
        baseUrlLiveData.setValue(baseUrl);
        interceptor.setInterceptor(baseUrl);
    }

    public LiveData<Login> getLogin(String email) {
        if (decryptedLogin.getValue() == null) {
            String savedEmail = encryptionService.decrypt(sharedPreferenceModel.getString(PREF_USER_EMAIL, null));
            if (savedEmail != null && savedEmail.equals(email)) {
                login.setPassword(encryptionService.decrypt(sharedPreferenceModel.getString(PREF_USER_PASSWORD, null)));
            }
            decryptedLogin.setValue(login);
        }
        return decryptedLogin;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }

    @VisibleForTesting
    public Login getLoginModel() {
        return login;
    }

    public void requestToken() {
        RequestToken requestToken = new RequestToken();
        requestToken.setEmail(login.getEmail());

        compositeDisposable.add(loginModel.requestToken(requestToken)
            .doOnSubscribe(disposable -> progress.setValue(true))
            .doFinally(() -> progress.setValue(false))
            .subscribe(() -> actionOpenResetPassword.setValue(true),
                throwable -> error.setValue(ErrorUtils.getMessage(throwable).toString())));
    }
}
