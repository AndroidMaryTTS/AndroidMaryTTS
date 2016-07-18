/**
 * Copyright 2011 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * <p/>
 * This file is part of MARY TTS.
 * <p/>
 * MARY TTS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package marytts.config;

import android.util.Log;

import com.marytts.android.link.MaryLink;

import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import marytts.exceptions.MaryConfigurationException;
import marytts.language.en.EnglishConfig;
import marytts.modules.phonemiser.AllophoneSet;
import marytts.server.Mary;
import marytts.util.io.PropertiesAccessor;
import marytts.util.io.PropertiesTrimTrailingWhitespace;
import marytts.voice.CmuSltHsmm.Config;


/**
 * @author marc
 */
public abstract class MaryConfig {


    private static final ArrayList<MaryConfig> configLoader = new ArrayList<MaryConfig>();
    public static ArrayList<String> configNames = new ArrayList<String>();

    static {
        try {
            configLoader.add(new MainConfig());
            configLoader.add(new EnglishConfig());
            configLoader.add(new Config());
        } catch (Exception e) {
            Log.d(Mary.LOG, "MaryConfig : " + e.toString());
        }
    }

    private Properties props;

    protected MaryConfig(InputStream propertyStream) throws MaryConfigurationException {
        props = new PropertiesTrimTrailingWhitespace();
        try {
            props.load(propertyStream);
        } catch (Exception e) {
            Log.d(Mary.LOG, "cannot load properties" + e.toString());
            throw new MaryConfigurationException("cannot load properties", e);
        }
    }

    public static void checkConsistency() throws MaryConfigurationException {
        // Check that we have a main config
        if (getMainConfig() == null) {
            Log.d(Mary.LOG, "No main config");
            throw new MaryConfigurationException("No main config");
        }

        // Check that for each voice, we have a matching language config
        for (VoiceConfig vc : getVoiceConfigs()) {
            if (getLanguageConfig(vc.getLocale()) == null) {
                Log.d(Mary.LOG, "Voice '" + vc.getName() + "' has locale '" + vc.getLocale() + "', but there is no corresponding language config.");
                throw new MaryConfigurationException("Voice '" + vc.getName() + "' has locale '" + vc.getLocale() + "', but there is no corresponding language config.");
            }
        }
    }

    public static int countConfigs() {

        int num = 0;
        for (@SuppressWarnings("unused") MaryConfig mc : configLoader) {
            num++;
        }
        return num;
    }

    public static int countLanguageConfigs() {
        int num = 0;
        for (MaryConfig mc : configLoader) {
            if (mc.isLanguageConfig()) {
                num++;
            }
        }
        return num;
    }

    public static int countVoiceConfigs() {
        int num = 0;
        for (MaryConfig mc : configLoader) {
            if (mc.isVoiceConfig()) {
                num++;
            }
        }
        return num;
    }

    public static MaryConfig getMainConfig() {

        for (MaryConfig mc : configLoader) {
            if (mc.isMainConfig()) {
                return mc;
            }
        }
        return null;
    }

    public static Iterable<LanguageConfig> getLanguageConfigs() {
        Set<LanguageConfig> lcs = new HashSet<LanguageConfig>();
        for (MaryConfig mc : configLoader) {
            if (mc.isLanguageConfig()) {
                LanguageConfig lc = (LanguageConfig) mc;
                lcs.add(lc);
            }
        }
        return lcs;
    }

    public static Iterable<VoiceConfig> getVoiceConfigs() {
        Set<VoiceConfig> vcs = new HashSet<VoiceConfig>();
        for (MaryConfig mc : configLoader) {
            if (mc.isVoiceConfig()) {
                VoiceConfig lc = (VoiceConfig) mc;
                vcs.add(lc);
            }
        }
        return vcs;
    }

    public static LanguageConfig getLanguageConfig(Locale locale) {
        for (MaryConfig mc : configLoader) {
            if (mc.isLanguageConfig()) {
                LanguageConfig lc = (LanguageConfig) mc;
                if (lc.getLocales().contains(locale)) {
                    return lc;
                }
            }
        }
        return null;
    }

    /**
     * Get the voice config for the given voice name, or null if there is no such voice config.
     *
     * @param voiceName
     * @return
     */
    public static VoiceConfig getVoiceConfig(String voiceName) {
        for (MaryConfig mc : configLoader) {
            if (mc.isVoiceConfig()) {
                VoiceConfig vc = (VoiceConfig) mc;
                if (vc.getName().equals(voiceName)) {
                    return vc;
                }
            }
        }
        return null;
    }


    //////////// Non-static / base class methods //////////////

    public static Iterable<MaryConfig> getConfigs() {
        return configLoader;
    }

    /**
     * Get the allophone set for the given locale, or null if it cannot be retrieved.
     *
     * @param locale
     * @return the allophone set for the given locale, or null of the locale is not supported.
     * @throws MaryConfigurationException if the locale is supported in principle but no allophone set can be retrieved.
     */
    public static AllophoneSet getAllophoneSet(Locale locale) throws MaryConfigurationException {
        LanguageConfig lc = getLanguageConfig(locale);
        if (lc == null) {
            return null;
        }
        return lc.getAllophoneSetFor(locale);
    }

    public boolean isMainConfig() {
        return false;
    }

    public boolean isLanguageConfig() {
        return false;
    }

    public boolean isVoiceConfig() {
        return false;
    }

    public Properties getProperties() {
        return props;
    }

    /**
     * Convenience access to this config's properties.
     *
     * @param systemPropertiesOverride whether to use system properties in priority if they exist.
     *                                 If true, any property requested from this properties accessor will first be looked up
     *                                 in the system properties, and only if it is not defined there, it will be looked up
     *                                 in this config's properties.
     * @return
     */
    public PropertiesAccessor getPropertiesAccessor(boolean systemPropertiesOverride) {
        //Map<String, String> maryBaseMap = new HashMap<String, String>();
        //maryBaseMap.put("MARY_BASE", MaryProperties.maryBase());
        return new PropertiesAccessor(props, systemPropertiesOverride, MaryLink.getProperties());
    }

    /**
     * Get the given property.  If it is not defined, the defaultValue is returned.
     *
     * @param property     name of the property to retrieve
     * @param defaultValue value to return if the property is not defined.
     * @return
     */
    public String getProperty(String property, String defaultValue) {
        return props.getProperty(property, defaultValue);
    }

    /**
     * For the given property name, return the value of that property as a list of items (interpreting the property value as a space-separated list).
     *
     * @param propertyName
     * @return the list of items, or an empty list if the property is not defined or contains no items
     */
//    public List<String> getList(String propertyName) {
//        String val = props.getProperty(propertyName).trim();
//        Log.i("test", "setupFeatureProcessors getList :: prop="+propertyName +" val="+val);
//        if (val == null || val.isEmpty())
//            return new ArrayList<String>();
//
//        String[] arrayy=StringUtils.split(val);
//        Log.i("test", "setupFeatureProcessors getList array="+arrayy.length);
//        return Arrays.asList(arrayy);
//
//    }
    public List<String> getList(String propertyName) {
        String val = MaryLink.getProperties().get(propertyName).trim();
        if (val == null || val.isEmpty()) return new ArrayList<String>();
        return Arrays.asList(StringUtils.split(val));

    }
}


