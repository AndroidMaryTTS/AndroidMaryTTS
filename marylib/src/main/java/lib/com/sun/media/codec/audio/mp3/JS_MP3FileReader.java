// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JS_MP3FileReader.java

package lib.com.sun.media.codec.audio.mp3;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;

import codecLib.mp3.Decoder;
import codecLib.mp3.FrameInfo;
import codecLib.mp3.MPADException;
import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.UnsupportedAudioFileException;
import lib.sound.sampled.spi.AudioFileReader;


// Referenced classes of package com.sun.media.codec.audio.mp3:
//            JS_MP3ConversionProvider

public class JS_MP3FileReader extends AudioFileReader {
    private static final int MIN_HEADER_SIZE = 1024;
    private static final int MAX_HEADER_SIZE = 16384;
    private static final String TFILETYPES_CLASS = "org.tritonus.share.sampled.AudioFileTypes";
    private static final String TGETFILETYPE_METHOD = "getType";
    private static final int WAVE_FORMAT_MPEG = 80;
    private static final int WAVE_FORMAT_MPEGLAYER3 = 85;
    public static boolean DEBUG;
    private static Class tAudioFileTypes = null;
    private static Method tGetType = null;
    private static boolean triedTritonus = false;
    static final lib.sound.sampled.AudioFileFormat.Type MPEG = getType("MPEG", "mpeg");
    static final lib.sound.sampled.AudioFileFormat.Type MP3 = getType("MP3", "mp3");

    static {
        DEBUG = false;
        if (DEBUG)
            System.out.println("JS_MP3FileReader: DEBUG enabled");
    }

    public JS_MP3FileReader() {
    }

    public static lib.sound.sampled.AudioFileFormat.Type getFileTypeByInfo(FrameInfo info) {
        lib.sound.sampled.AudioFileFormat.Type type = MPEG;
        if (info.getMpegId() == 1 && info.getLayerId() == 3)
            type = MP3;
        return type;
    }

    public static lib.sound.sampled.AudioFormat.Encoding getEncByInfo(FrameInfo info) {
        if (info.getMpegId() == 1)
            switch (info.getLayerId()) {
                case 1: // '\001'
                    return JS_MP3ConversionProvider.MPEG1L1;

                case 2: // '\002'
                    return JS_MP3ConversionProvider.MPEG1L2;

                case 3: // '\003'
                    return JS_MP3ConversionProvider.MPEG1L3;
            }
        else if (info.getMpegId() == 2)
            switch (info.getLayerId()) {
                case 1: // '\001'
                    return JS_MP3ConversionProvider.MPEG2L1;

                case 2: // '\002'
                    return JS_MP3ConversionProvider.MPEG2L2;

                case 3: // '\003'
                    return JS_MP3ConversionProvider.MPEG2L3;
            }
        throw new IllegalArgumentException("invalid frame: MPEG " + info.getMpegId() + ", layer " + info.getMpegId());
    }

    private static lib.sound.sampled.AudioFileFormat.Type getType(String name, String ext) {
        lib.sound.sampled.AudioFileFormat.Type ret = null;
        if (!triedTritonus || tGetType != null)
            try {
                boolean firstTime = !triedTritonus;
                if (!triedTritonus) {
                    triedTritonus = true;
                    tAudioFileTypes = Class.forName("org.tritonus.share.sampled.AudioFileTypes");
                    tGetType = tAudioFileTypes.getMethod("getType", String.class, String.class);
                }
                Object args[] = new Object[2];
                args[0] = name;
                args[1] = ext;
                ret = (lib.sound.sampled.AudioFileFormat.Type) tGetType.invoke(null, args);
                if (DEBUG && firstTime)
                    System.out.println("JS_MP3FileReader: Using Tritonus' AudioFileTypes class");
            } catch (Exception e) {
            }
        if (ret == null)
            ret = new AFFT(name, ext);
        return ret;
    }

    private static int isWAVE_MP3(byte data[], int len) {
        if (len < 40)
            return -1;
        int offset = 8;
        int waveMagic = readBE32(data, offset);
        offset += 4;
        if (waveMagic != 0x57415645) {
            if (DEBUG)
                System.out.println("JS_MP3FileReader: RIFF, but not WAVE");
            return -1;
        }
        boolean found = false;
        int chunklen = 0;
        do {
            if (offset + 8 >= len)
                break;
            int fmt = readBE32(data, offset);
            offset += 4;
            chunklen = readLE32(data, offset);
            offset += 4;
            if (chunklen % 2 > 0)
                chunklen++;
            if (fmt == 0x666d7420) {
                found = true;
                break;
            }
            offset += chunklen;
        } while (true);
        if (!found) {
            if (DEBUG)
                System.out.println("JS_MP3FileReader: FMF_ chunk not found --> corrupt wave file");
            return -1;
        }
        int chunkStartOffset = offset;
        int wav_type = readLE16(data, offset);
        offset += 2;
        if (wav_type != 80 && wav_type != 85) {
            if (DEBUG)
                System.out.println("JS_MP3FileReader: WAVE, but not MP3 encoding");
            return -1;
        }
        offset += chunklen - (offset - chunkStartOffset);
        found = false;
        do {
            if (offset + 8 >= len)
                break;
            int datahdr = readBE32(data, offset);
            offset += 4;
            chunklen = readLE32(data, offset);
            offset += 4;
            if (chunklen % 2 > 0)
                chunklen++;
            if (datahdr == 0x64617461) {
                found = true;
                break;
            }
            offset += chunklen;
        } while (true);
        if (!found) {
            if (DEBUG)
                System.out.println("JS_MP3FileReader: data chunk not found in WAVE file.");
            return -1;
        }
        if (DEBUG)
            System.out.println("JS_MP3FileReader: Correctly parsed WAVE file. MP3 data starts at offset " + offset);
        return offset;
    }

    private static int readLE32(byte data[], int offset) {
        return data[offset] & 0xff | (data[offset + 1] & 0xff) << 8 | (data[offset + 2] & 0xff) << 16 | (data[offset + 3] & 0xff) << 24;
    }

    private static int readBE32(byte data[], int offset) {
        return data[offset + 3] & 0xff | (data[offset + 2] & 0xff) << 8 | (data[offset + 1] & 0xff) << 16 | (data[offset] & 0xff) << 24;
    }

    private static int readLE16(byte data[], int offset) {
        return data[offset] & 0xff | (data[offset + 1] & 0xff) << 8;
    }

    public AudioFileFormat getAudioFileFormat(InputStream stream)
            throws UnsupportedAudioFileException, IOException {
        return getAudioFileFormat(stream, true, 1024);
    }

    public AudioFileFormat getAudioFileFormat(URL url)
            throws UnsupportedAudioFileException, IOException {
        AudioFileFormat fileFormat = null;
        InputStream urlStream;
        fileFormat = null;
        urlStream = url.openStream();
        fileFormat = getAudioFileFormat(urlStream, false, 16384);
        urlStream.close();
//        break MISSING_BLOCK_LABEL_33;
//        Exception exception;
//        exception;
        urlStream.close();
//        throw exception;
        return fileFormat;
    }

    public AudioFileFormat getAudioFileFormat(File file)
            throws UnsupportedAudioFileException, IOException {
        AudioFileFormat fileFormat = null;
        FileInputStream fis;
        fileFormat = null;
        fis = new FileInputStream(file);
        fileFormat = getAudioFileFormat(fis, false, 16384);
        fis.close();
//        break MISSING_BLOCK_LABEL_37;
//        Exception exception;
//        exception;
//        fis.close();
//        throw exception;
        return fileFormat;
    }

    public AudioInputStream getAudioInputStream(InputStream stream)
            throws UnsupportedAudioFileException, IOException {
        return getAudioInputStream(stream, 1024);
    }

    public AudioInputStream getAudioInputStream(URL url)
            throws UnsupportedAudioFileException, IOException {
        InputStream urlStream = null;
        AudioInputStream result;
        urlStream = url.openStream();
        result = null;
        BufferedInputStream bis = new BufferedInputStream(urlStream, 16384);
        result = getAudioInputStream(bis, 16384);
        if (result == null)
            urlStream.close();
//        break MISSING_BLOCK_LABEL_54;
//        Exception exception;
//        exception;
        if (result == null)
            urlStream.close();
//        throw exception;
        return result;
    }

    public AudioInputStream getAudioInputStream(File file)
            throws UnsupportedAudioFileException, IOException {
        FileInputStream fis;
        AudioInputStream result;
        fis = new FileInputStream(file);
        result = null;
        BufferedInputStream bis = new BufferedInputStream(fis, 16384);
        result = getAudioInputStream(bis, 16384);
        if (result == null)
            fis.close();
//        break MISSING_BLOCK_LABEL_58;
//        Exception exception;
//        exception;
        if (result == null)
            fis.close();
//        throw exception;
        return result;
    }

    private AudioInputStream getAudioInputStream(InputStream stream, int headerSize)
            throws UnsupportedAudioFileException, IOException {
        AudioFileFormat fileFormat = getAudioFileFormat(stream, true, headerSize);
        AudioFormat format = fileFormat.getFormat();
        return new AudioInputStream(stream, format, fileFormat.getFrameLength());
    }

    private AFF getAudioFileFormat(InputStream stream, boolean doReset, int headerSize)
            throws UnsupportedAudioFileException, IOException {
        if (doReset)
            stream.mark(headerSize);
        boolean isMP3 = true;
        boolean hasID3 = false;
        boolean isWAVE = false;
        int dataOffset = 0;
        lib.sound.sampled.AudioFileFormat.Type fileType = null;
        FrameInfo info = new FrameInfo();
        Decoder decoder = new Decoder();
        byte data[] = new byte[headerSize - 1];
        int read = stream.read(data, 0, data.length);
        int maxSyncSearch = 1000;
        int magic = readLE32(data, 0);
        switch (magic) {
            case 1297239878:
                if (DEBUG)
                    System.out.println("JS_MP3FileReader: Detected AIFF file");
                isMP3 = false;
                break;

            case 1179011410:
                if (DEBUG)
                    System.out.println("JS_MP3FileReader: Detected RIFF file");
                isWAVE = true;
                dataOffset = isWAVE_MP3(data, read);
                isMP3 = dataOffset >= 0;
                maxSyncSearch = 100;
                fileType = lib.sound.sampled.AudioFileFormat.Type.WAVE;
                break;

            case 1684960046:
                if (DEBUG)
                    System.out.println("JS_MP3FileReader: Detected AU file");
                isMP3 = false;
                break;
        }
        if ((magic & 0xffffff) == 0x334449) {
            if (DEBUG)
                System.out.println("JS_MP3FileReader: Detected ID3v2 header");
            hasID3 = true;
            maxSyncSearch = read;
        }
        if (isMP3)
            try {
                decoder.getNextFrameInfo(info, data, dataOffset, maxSyncSearch);
                if (!isWAVE && info.getHeaderOffset() > 0) {
                    int nextOffset = dataOffset + info.getHeaderOffset() + info.getFrameLength();
                    int maxLength = nextOffset + 100 <= read ? 100 : read - nextOffset;
                    if (maxLength > 0) {
                        FrameInfo nextInfo = new FrameInfo();
                        if (DEBUG)
                            System.out.println("JS_MP3FileReader: trying to find consecutive frame from offset " + nextOffset + " on. maxLength=" + maxLength);
                        decoder.getNextFrameInfo(nextInfo, data, nextOffset, maxLength);
                        if (nextInfo.getHeaderOffset() != 0) {
                            isMP3 = false;
                            if (DEBUG)
                                System.out.println("JS_MP3FileReader: unable to find next consecutive frame. Probably not an mp3 file.");
                        }
                    }
                }
            } catch (MPADException e) {
                isMP3 = false;
                if (DEBUG)
                    System.out.println("JS_MP3FileReader: getNextFrameInfo threw Exception. state=" + e.getState());
            }
        if (!isMP3) {
            if (doReset)
                stream.reset();
            throw new UnsupportedAudioFileException("not an MP3 file");
        }
        AFF fileFormat = new AFF(fileType, info);
        if (doReset) {
            stream.reset();
            if (info.getHeaderOffset() + dataOffset > 0)
                stream.skip(info.getHeaderOffset() + dataOffset);
        }
        return fileFormat;
    }

    private static class AFF extends AudioFileFormat {

        private FrameInfo frameInfo;

        public AFF(lib.sound.sampled.AudioFileFormat.Type afft, FrameInfo info) {
            super(afft == null ? JS_MP3FileReader.getFileTypeByInfo(info) : afft, new AudioFormat(JS_MP3FileReader.getEncByInfo(info), info.getSamplingRate(), -1, info.getNumberOfChannels(), -1, -1F, false), -1);
            frameInfo = info;
        }
    }

    private static class AFFT extends lib.sound.sampled.AudioFileFormat.Type {

        public AFFT(String name, String ext) {
            super(name, ext);
        }
    }


}
