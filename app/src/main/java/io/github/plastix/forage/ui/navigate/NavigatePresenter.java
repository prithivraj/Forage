package io.github.plastix.forage.ui.navigate;

import javax.inject.Inject;

import io.github.plastix.forage.ui.base.Presenter;
import io.github.plastix.forage.util.LocationUtils;

public class NavigatePresenter extends Presenter<NavigateView> {

    @Inject
    public NavigatePresenter() {
    }

    public void navigate(String latitude, String longitude) {
        if (!isViewAttached()) {
            return;
        }

        boolean error = false;

        if (!LocationUtils.isValidLatitude(latitude)) {
            view.errorParsingLatitude();
            error = true;
        }

        if (!LocationUtils.isValidLongitude(longitude)) {
            view.errorParsingLongitude();
            error = true;
        }

        if (!error) {
            double lat = Double.valueOf(latitude);
            double lon = Double.valueOf(longitude);

            view.openCompassScreen(lat, lon);
        }

    }

    @Override
    public void onDestroyed() {
        // Nothing
    }
}
