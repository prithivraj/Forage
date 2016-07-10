package io.github.plastix.forage.ui.log;

import javax.inject.Inject;

import io.github.plastix.forage.R;
import io.github.plastix.forage.data.api.OkApiInteractor;
import io.github.plastix.forage.data.api.response.SubmitLogResponse;
import io.github.plastix.forage.data.local.DatabaseInteractor;
import io.github.plastix.forage.data.network.NetworkInteractor;
import io.github.plastix.forage.ui.base.rx.RxPresenter;
import retrofit2.adapter.rxjava.HttpException;
import timber.log.Timber;

public class LogPresenter extends RxPresenter<LogView> {

    private OkApiInteractor okApiInteractor;
    private NetworkInteractor networkInteractor;
    private DatabaseInteractor databaseInteractor;

    @Inject
    public LogPresenter(OkApiInteractor okApiInteractor,
                        NetworkInteractor networkInteractor,
                        DatabaseInteractor databaseInteractor) {
        this.okApiInteractor = okApiInteractor;
        this.networkInteractor = networkInteractor;
        this.databaseInteractor = databaseInteractor;
    }

    public void submitLog(final String cacheCode, final String comment, final String type) {
        networkInteractor.hasInternetConnectionCompletable()
                .subscribe(() -> addSubscription(
                        okApiInteractor.submitLog(cacheCode, type, comment)
                                .toObservable()
                                .compose(LogPresenter.this.<SubmitLogResponse>deliverFirst())
                                .toSingle()
                                .doOnSubscribe(() -> {
                                    if (isViewAttached()) {
                                        view.showSubmittingDialog();
                                    }
                                })
                                .subscribe(submitLogResponse -> {
                                            if (isViewAttached()) {
                                                if (submitLogResponse.isSuccessful) {
                                                    view.showSuccessfulSubmit();
                                                } else {
                                                    view.showErrorDialog(submitLogResponse.message);
                                                }
                                            }
                                        },
                                        throwable -> {
                                            if (isViewAttached()) {
                                                // Non-200 HTTP Code
                                                if (throwable instanceof HttpException) {
                                                    HttpException httpException = ((HttpException) throwable);
                                                    view.showErrorDialog(httpException.getMessage());
                                                } else {
                                                    view.showErrorDialog(R.string.log_submit_error_unknown);
                                                }
                                            }
                                        })
                ), throwable -> {
                    if (isViewAttached()) {
                        view.showErrorInternetDialog();
                    }
                });
    }

    public void getLogTypes(String cacheCode) {
        addSubscription(
                databaseInteractor.getGeocache(cacheCode)
                        .subscribe(cache -> {
                            if (cache.type.equals("Event")) {
                                if (isViewAttached()) {
                                    view.setLogTypes(R.array.log_types_event);
                                }
                            }

                        }, throwable -> Timber.e(throwable, "Error fetching geocache from database!"))
        );
    }

    @Override
    public void onDestroyed() {
        databaseInteractor.onDestroy();
    }
}
