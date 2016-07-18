/**
 *
 */
package marytts.config;

import com.marytts.android.link.MaryLink;

import java.io.IOException;

import marytts.exceptions.MaryConfigurationException;

/**
 * @author marc
 */
public class MainConfig extends MaryConfig {

    public MainConfig() throws MaryConfigurationException, IOException {


        super(MaryLink.getContext().getAssets().open("marytts/config/marybase.config"));
    }

    @Override
    public boolean isMainConfig() {
        return true;
    }
}
