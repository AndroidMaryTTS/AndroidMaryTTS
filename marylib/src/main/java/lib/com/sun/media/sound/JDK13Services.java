/*
 * %W% %E%
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package lib.com.sun.media.sound;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lib.sound.midi.Receiver;
import lib.sound.midi.Sequencer;
import lib.sound.midi.Synthesizer;
import lib.sound.midi.Transmitter;
import lib.sound.sampled.Clip;
import lib.sound.sampled.Port;
import lib.sound.sampled.SourceDataLine;
import lib.sound.sampled.TargetDataLine;


/**
 * JDK13Services uses the Service class in JDK 1.3
 * to discover a list of service providers installed
 * in the system.
 * <p/>
 * This class is public because it is called from july.sound.midi.MidiSystem
 * and july.sound.sampled.AudioSystem. The alternative would be to make
 * JSSecurityManager public, which is considered worse.
 *
 * @author Matthias Pfisterer
 * @version %I% %E%
 */
public class JDK13Services {

    /**
     * The default for the length of the period to hold the cache.
     * This value is given in milliseconds. It is equivalent to
     * 1 minute.
     */
    private static final long DEFAULT_CACHING_PERIOD = 60000;

    /**
     * Filename of the properties file for default provider properties.
     * This file is searched in the subdirectory "lib" of the JRE directory
     * (this behaviour is hardcoded).
     */
    private static final String PROPERTIES_FILENAME = "sound.properties";

    /**
     * Cache for the providers.
     * Class objects of the provider type (MixerProvider, MidiDeviceProvider
     * ...) are used as keys. The values are instances of ProviderCache.
     */
    private static Map providersCacheMap = new HashMap();


    /**
     * The length of the period to hold the cache.
     * This value is given in milliseconds.
     */
    private static long cachingPeriod = DEFAULT_CACHING_PERIOD;

    /**
     * Properties loaded from the properties file for default provider
     * properties.
     */
    private static Properties properties;


    /**
     * Private, no-args constructor to ensure against instantiation.
     */
    private JDK13Services() {
    }


    /**
     * Set the period provider lists are cached.
     * This method is only intended for testing.
     */
    public static void setCachingPeriod(int seconds) {
        cachingPeriod = seconds * 1000L;
    }


    /**
     * Obtains a List containing installed instances of the
     * providers for the requested service.
     * The List of providers is cached for the period of time given by
     * {@link #cachingPeriod cachingPeriod}. During this period, the same
     * List instance is returned for the same type of provider. After this
     * period, a new instance is constructed and returned. The returned
     * List is immutable.
     *
     * @param serviceClass The type of providers requested. This should be one
     *                     of AudioFileReader.class, AudioFileWriter.class,
     *                     FormatConversionProvider.class, MixerProvider.class,
     *                     MidiDeviceProvider.class, MidiFileReader.class, MidiFileWriter.class or
     *                     SoundbankReader.class.
     * @return A List of providers of the requested type. This List is
     * immutable.
     */
    public static synchronized List getProviders(Class serviceClass) {
        ProviderCache cache = (ProviderCache) providersCacheMap.get(serviceClass);
        if (cache == null) {
            cache = new ProviderCache();
            providersCacheMap.put(serviceClass, cache);
        }
        if (cache.providers == null ||
                System.currentTimeMillis() > cache.lastUpdate + cachingPeriod) {
            cache.providers = Collections.unmodifiableList(JSSecurityManager.getProviders(serviceClass));
            cache.lastUpdate = System.currentTimeMillis();
        }
        return cache.providers;
    }


    /**
     * Obtain the provider class name part of a default provider property.
     *
     * @param typeClass The type of the default provider property. This
     *                  should be one of Receiver.class, Transmitter.class, Sequencer.class,
     *                  Synthesizer.class, SourceDataLine.class, TargetDataLine.class,
     *                  Clip.class or Port.class.
     * @return The value of the provider class name part of the property
     * (the part before the hash sign), if available. If the property is
     * not set or the value has no provider class name part, null is returned.
     */
    public static synchronized String getDefaultProviderClassName(Class typeClass) {
        String value = null;
        String defaultProviderSpec = getDefaultProvider(typeClass);
        if (defaultProviderSpec != null) {
            int hashpos = defaultProviderSpec.indexOf('#');
            if (hashpos == 0) {
                // instance name only; leave value as null
            } else if (hashpos > 0) {
                value = defaultProviderSpec.substring(0, hashpos);
            } else {
                value = defaultProviderSpec;
            }
        }
        return value;
    }


    /**
     * Obtain the instance name part of a default provider property.
     *
     * @param typeClass The type of the default provider property. This
     *                  should be one of Receiver.class, Transmitter.class, Sequencer.class,
     *                  Synthesizer.class, SourceDataLine.class, TargetDataLine.class,
     *                  Clip.class or Port.class.
     * @return The value of the instance name part of the property (the
     * part after the hash sign), if available. If the property is not set
     * or the value has no instance name part, null is returned.
     */
    public static synchronized String getDefaultInstanceName(Class typeClass) {
        String value = null;
        String defaultProviderSpec = getDefaultProvider(typeClass);
        if (defaultProviderSpec != null) {
            int hashpos = defaultProviderSpec.indexOf('#');
            if (hashpos >= 0 && hashpos < defaultProviderSpec.length() - 1) {
                value = defaultProviderSpec.substring(hashpos + 1);
            }
        }
        return value;
    }


    /**
     * Obtain the value of a default provider property.
     *
     * @param typeClass The type of the default provider property. This
     *                  should be one of Receiver.class, Transmitter.class, Sequencer.class,
     *                  Synthesizer.class, SourceDataLine.class, TargetDataLine.class,
     *                  Clip.class or Port.class.
     * @return The complete value of the property, if available.
     * If the property is not set, null is returned.
     */
    private static synchronized String getDefaultProvider(Class typeClass) {
        if (!SourceDataLine.class.equals(typeClass)
                && !TargetDataLine.class.equals(typeClass)
                && !Clip.class.equals(typeClass)
                && !Port.class.equals(typeClass)
                && !Receiver.class.equals(typeClass)
                && !Transmitter.class.equals(typeClass)
                && !Synthesizer.class.equals(typeClass)
                && !Sequencer.class.equals(typeClass)) {
            return null;
        }
        String value;
        String propertyName = typeClass.getName();
        value = JSSecurityManager.getProperty(propertyName);
        if (value == null) {
            value = getProperties().getProperty(propertyName);
        }
        if ("".equals(value)) {
            value = null;
        }
        return value;
    }


    /**
     * Obtain a properties bundle containing property values from the
     * properties file. If the properties file could not be loaded,
     * the properties bundle is empty.
     */
    private static synchronized Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            JSSecurityManager.loadProperties(properties, PROPERTIES_FILENAME);
        }
        return properties;
    }

    // INNER CLASSES

    private static class ProviderCache {
        // System time of the last update in milliseconds.
        public long lastUpdate;

        // The providers.
        public List providers;
    }
}