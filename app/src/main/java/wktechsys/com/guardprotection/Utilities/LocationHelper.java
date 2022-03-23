
package wktechsys.com.guardprotection.Utilities;

import androidx.annotation.Nullable;

public class LocationHelper {

    @Nullable
    private static String current_location;

    @Nullable
    public String getCurrentLocation() {
        return current_location;
    }

    public void setCurrentLocation(final String current_location) {
        this.current_location = current_location;
    }
}