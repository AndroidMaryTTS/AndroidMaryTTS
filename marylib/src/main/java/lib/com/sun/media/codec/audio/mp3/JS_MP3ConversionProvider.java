// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JS_MP3ConversionProvider.java

package lib.com.sun.media.codec.audio.mp3;

import java.lang.reflect.Method;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.spi.FormatConversionProvider;


// Referenced classes of package com.sun.media.codec.audio.mp3:
//            JS_MP3DecoderStream

public class JS_MP3ConversionProvider extends FormatConversionProvider {
    static final lib.sound.sampled.AudioFormat.Encoding MPEG1L1;
    static final lib.sound.sampled.AudioFormat.Encoding MPEG1L2;
    static final lib.sound.sampled.AudioFormat.Encoding MPEG1L3;
    static final lib.sound.sampled.AudioFormat.Encoding MPEG2L1;
    static final lib.sound.sampled.AudioFormat.Encoding MPEG2L2;
    static final lib.sound.sampled.AudioFormat.Encoding MPEG2L3;
    static final lib.sound.sampled.AudioFormat.Encoding MP3;
    static final lib.sound.sampled.AudioFormat.Encoding SIGNED;
    private static final lib.sound.sampled.AudioFormat.Encoding sourceEncodings[];
    private static final lib.sound.sampled.AudioFormat.Encoding targetEncodings[];
    private static final int MAX_CHANNELS = 2;
    private static final int SAMPLE_SIZE_IN_BITS[] = {
            16
    };
    private static final float SAMPLE_RATES[] = {
            16000F, 22050F, 24000F, 32000F, 44100F, 48000F
    };
    private static final String TENCODINGS_CLASS = "org.tritonus.share.sampled.Encodings";
    private static final String TGETENCODING_METHOD = "getEncoding";
    public static boolean DEBUG;
    private static AudioFormat targetFormats[];
    private static Class tEncodings = null;
    private static Method tGetEncoding = null;
    private static boolean triedTritonus = false;

    static {
        DEBUG = false;
        MPEG1L1 = getEncoding("MPEG1L1");
        MPEG1L2 = getEncoding("MPEG1L2");
        MPEG1L3 = getEncoding("MPEG1L3");
        MPEG2L1 = getEncoding("MPEG2L1");
        MPEG2L2 = getEncoding("MPEG2L2");
        MPEG2L3 = getEncoding("MPEG2L3");
        MP3 = getEncoding("MP3");
        SIGNED = lib.sound.sampled.AudioFormat.Encoding.PCM_SIGNED;
        sourceEncodings = (new lib.sound.sampled.AudioFormat.Encoding[]{
                MPEG1L1, MPEG1L2, MPEG1L3, MP3, MPEG2L1, MPEG2L2, MPEG2L3
        });
        targetEncodings = (new lib.sound.sampled.AudioFormat.Encoding[]{
                SIGNED
        });
        targetFormats = null;
        if (DEBUG)
            System.out.println("JS_MP3ConversionProvider: DEBUG enabled");
        if (targetFormats == null) {
            targetFormats = new AudioFormat[SAMPLE_RATES.length * SAMPLE_SIZE_IN_BITS.length * 2 * 2];
            int index = 0;
            for (int sr = 0; sr < SAMPLE_RATES.length; sr++) {
                for (int b = 0; b < SAMPLE_SIZE_IN_BITS.length; b++) {
                    for (int c = 0; c < 2; c++) {
                        int bits = SAMPLE_SIZE_IN_BITS[b];
                        targetFormats[index++] = new AudioFormat(SAMPLE_RATES[sr], bits, c + 1, bits > 8, false);
                        targetFormats[index++] = new AudioFormat(SAMPLE_RATES[sr], bits, c + 1, bits > 8, true);
                    }

                }

            }

        }
    }

    public JS_MP3ConversionProvider() {
    }

    private static boolean encodingEquals(lib.sound.sampled.AudioFormat.Encoding enc1, lib.sound.sampled.AudioFormat.Encoding enc2) {
        return enc1 == enc2 || enc1 != null && enc2 != null && enc1.toString().equals(enc2.toString());
    }

    private static boolean floatEquals(float f1, float f2) {
        return Math.abs(f1 - f2) < 1E-009F;
    }

    private static boolean audioFormatMatches(AudioFormat f1, AudioFormat f2) {
        return encodingEquals(f1.getEncoding(), f2.getEncoding()) && (floatEquals(f1.getSampleRate(), f2.getSampleRate()) || floatEquals(f1.getSampleRate(), -1F) || floatEquals(f2.getSampleRate(), -1F)) && (floatEquals(f1.getFrameRate(), f2.getFrameRate()) || floatEquals(f1.getFrameRate(), -1F) || floatEquals(f2.getFrameRate(), -1F)) && (f1.getChannels() == f2.getChannels() || f1.getChannels() == -1 || f2.getChannels() == -1) && (f1.getSampleSizeInBits() == f2.getSampleSizeInBits() || f1.getSampleSizeInBits() == -1 || f2.getSampleSizeInBits() == -1) && (f1.getFrameSize() == f2.getFrameSize() || f1.getFrameSize() == -1 || f2.getFrameSize() == -1) && (f1.getChannels() <= 8 || f2.getChannels() <= 8 || f1.isBigEndian() == f2.isBigEndian());
    }

    private static lib.sound.sampled.AudioFormat.Encoding getEncoding(String name) {
        lib.sound.sampled.AudioFormat.Encoding ret = null;
        if (!triedTritonus || tGetEncoding != null)
            try {
                boolean firstTime = !triedTritonus;
                if (!triedTritonus) {
                    triedTritonus = true;
                    tEncodings = Class.forName("org.tritonus.share.sampled.Encodings");
                    tGetEncoding = tEncodings.getMethod("getEncoding", String.class);
                }
                Object args[] = new Object[1];
                args[0] = name;
                ret = (lib.sound.sampled.AudioFormat.Encoding) tGetEncoding.invoke(null, args);
                if (DEBUG && firstTime)
                    System.out.println("JS_MP3ConversionProvider: Using Tritonus' Encodings class");
            } catch (Exception e) {
            }
        if (ret == null)
            ret = new AFE(name);
        return ret;
    }

    public lib.sound.sampled.AudioFormat.Encoding[] getSourceEncodings() {
        return sourceEncodings;
    }

    public lib.sound.sampled.AudioFormat.Encoding[] getTargetEncodings() {
        return targetEncodings;
    }

    public lib.sound.sampled.AudioFormat.Encoding[] getTargetEncodings(AudioFormat sourceFormat) {
        if (getTargetFormats(sourceFormat, true) == null)
            return new lib.sound.sampled.AudioFormat.Encoding[0];
        else
            return getTargetEncodings();
    }

    public AudioFormat[] getTargetFormats(lib.sound.sampled.AudioFormat.Encoding targetEncoding, AudioFormat sourceFormat) {
        if (!encodingEquals(targetEncoding, SIGNED))
            return new AudioFormat[0];
        else
            return getTargetFormats(sourceFormat, false);
    }

    public AudioInputStream getAudioInputStream(lib.sound.sampled.AudioFormat.Encoding targetEncoding, AudioInputStream sourceStream) {
        AudioFormat targetFormat = new AudioFormat(targetEncoding, -1F, -1, -1, -1, -1F, true);
        return getAudioInputStream(targetFormat, sourceStream);
    }

    public AudioInputStream getAudioInputStream(AudioFormat targetFormat, AudioInputStream sourceStream) {
        AudioFormat sourceFormat = sourceStream.getFormat();
        if (sourceFormat.getSampleRate() > 0.0F && sourceFormat.getChannels() > 0) {
            AudioFormat targetFormats[] = getTargetFormats(sourceFormat, false);
            for (int i = 0; i < targetFormats.length; i++)
                if (audioFormatMatches(targetFormat, targetFormats[i]))
                    return new JS_MP3DecoderStream(sourceStream, targetFormats[i], -1L);

        }
        throw new IllegalArgumentException("conversion not supported");
    }

    private AudioFormat[] getTargetFormats(AudioFormat sourceFormat, boolean testOnly) {
        int count = 0;
        for (int e = 0; e < sourceEncodings.length; e++)
            count += encodingEquals(sourceFormat.getEncoding(), sourceEncodings[e]) ? 1 : 0;

        float sampleRate = sourceFormat.getSampleRate();
        boolean allSampleRates = floatEquals(sampleRate, -1F);
        if (count > 0)
            if (allSampleRates) {
                count *= SAMPLE_RATES.length;
            } else {
                boolean srMatches = false;
                int sr = 0;
                do {
                    if (sr >= SAMPLE_RATES.length)
                        break;
                    if (floatEquals(SAMPLE_RATES[sr], sampleRate)) {
                        srMatches = true;
                        break;
                    }
                    sr++;
                } while (true);
                if (!srMatches)
                    count = 0;
            }
        int channels = sourceFormat.getChannels();
        boolean allChannels = channels == -1;
        if (count > 0)
            if (allChannels)
                count *= 2;
            else if (channels < 1 || channels > 2)
                count = 0;
        int bits = sourceFormat.getSampleSizeInBits();
        boolean allBits = bits == -1;
        if (count > 0)
            if (allBits) {
                count *= SAMPLE_SIZE_IN_BITS.length;
            } else {
                boolean bitsSupported = false;
                int b = 0;
                do {
                    if (b >= SAMPLE_SIZE_IN_BITS.length)
                        break;
                    if (bits == SAMPLE_SIZE_IN_BITS[b]) {
                        bitsSupported = true;
                        break;
                    }
                    b++;
                } while (true);
                if (!bitsSupported)
                    count = 0;
            }
        if (testOnly)
            if (count == 0)
                return null;
            else
                return new AudioFormat[0];
        count *= 2;
        AudioFormat ret[] = new AudioFormat[count];
        if (count == 0)
            return ret;
        if (count == 2 && !allSampleRates && !allChannels && !allBits) {
            ret[0] = new AudioFormat(sampleRate, bits, channels, bits > 8, false);
            ret[1] = new AudioFormat(sampleRate, bits, channels, bits > 8, true);
            return ret;
        }
        int index = 0;
        for (int f = 0; f < targetFormats.length; f++) {
            AudioFormat format = targetFormats[f];
            if ((allSampleRates || floatEquals(sampleRate, format.getSampleRate())) && (allChannels || channels == format.getChannels()) && (allBits || bits == format.getSampleSizeInBits()))
                ret[index++] = format;
        }

        if (DEBUG && index != count)
            System.out.println("JS_MP3ConversionProvider: inconsistency ERROR: did not fill all audio formats!");
        return ret;
    }

    private static class AFE extends lib.sound.sampled.AudioFormat.Encoding {

        public AFE(String name) {
            super(name);
        }
    }
}
