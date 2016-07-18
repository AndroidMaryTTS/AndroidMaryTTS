// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JS_MP3DecoderStream.java

package lib.com.sun.media.codec.audio.mp3;

import java.io.IOException;

import codecLib.mp3.Decoder;
import codecLib.mp3.FrameInfo;
import codecLib.mp3.MPADException;
import codecLib.mp3.OutputConverter;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;


public class JS_MP3DecoderStream extends AudioInputStream {

    @SuppressWarnings("unused")
    private static final int OUTSIZE = 9216;
    public static boolean DEBUG;

    static {
        DEBUG = false;
        if (DEBUG)
            System.out.println("JS_MP3DecoderStream: DEBUG enabled");
    }

    private byte pendingData[];
    private int pendingDataSize;
    private int pendingDataOffset;
    private boolean streamEOF;
    private Decoder decoder;
    private AudioFormat sourceFormat;
    private float fsamp[][];
    private int fsampOffset[];
    private byte outData[];
    private int outFrameSize;
    private int outDataOffset;
    private boolean outputBigEndian;

    JS_MP3DecoderStream(AudioInputStream stream, AudioFormat format, long length) {
        super(stream, format, length);
        pendingData = new byte[8096];
        pendingDataSize = 0;
        pendingDataOffset = 0;
        streamEOF = false;
        decoder = null;
        sourceFormat = null;
        fsamp = new float[12][1152];
        fsampOffset = new int[12];
        outData = new byte[9216];
        outFrameSize = 0;
        outDataOffset = 0;
        outputBigEndian = false;
        sourceFormat = stream.getFormat();
        outputBigEndian = getFormat().isBigEndian();
        open();
    }

    public static void convertLE(byte out[], int outoff, float in[], int inoff, int number) {
        for (int i = 0; i < number; i++) {
            float xd = in[inoff++];
            int tmp = (int) xd;
            tmp = tmp < 32767 ? tmp > -32768 ? tmp : -32768 : 32767;
            out[outoff + i * 2] = (byte) tmp;
            out[outoff + i * 2 + 1] = (byte) (tmp >> 8);
        }

    }

    public static void convertLE(byte out[], int outoff, float in1[], int inoff1, float in2[], int inoff2, int number) {
        for (int i = 0; i < number; i++) {
            float xd = in1[inoff1++];
            int tmp = (int) xd;
            tmp = tmp < 32767 ? tmp > -32768 ? tmp : -32768 : 32767;
            out[outoff + i * 4] = (byte) tmp;
            out[outoff + i * 4 + 1] = (byte) (tmp >> 8);
            xd = in2[inoff2++];
            tmp = (int) xd;
            tmp = tmp < 32767 ? tmp > -32768 ? tmp : -32768 : 32767;
            out[outoff + i * 4 + 2] = (byte) tmp;
            out[outoff + i * 4 + 3] = (byte) (tmp >> 8);
        }

    }

    private synchronized void open() {
        if (decoder != null)
            close();
        decoder = new Decoder();
        pendingDataSize = 0;
        streamEOF = false;
        if (DEBUG)
            System.out.println("JS_MP3DecoderStream: input format:  " + sourceFormat);
        if (DEBUG)
            System.out.println("JS_MP3DecoderStream: output format: " + getFormat());
        outFrameSize = 0;
        outDataOffset = 0;
    }

    public synchronized void close() {
        if (decoder != null)
            decoder = null;
    }

    public synchronized void reset()
            throws IOException {
        super.reset();
        if (decoder != null)
            close();
        open();
    }

    public int read()
            throws IOException {
        throw new IOException("cannot read a single byte from stream");
    }

    public int read(byte b[])
            throws IOException {
        return read(b, 0, b.length);
    }

    public synchronized int read(byte b[], int off, int len)
            throws IOException {
        if (len % frameSize != 0)
            len -= len % getFormat().getFrameSize();
        int totalRead = 0;
        if (decoder == null)
            return -1;
        boolean endDecoding = false;
        do {
            if (totalRead >= len)
                break;
            int toCopyFromOutBuffer = len - totalRead;
            if (toCopyFromOutBuffer > outFrameSize - outDataOffset)
                toCopyFromOutBuffer = outFrameSize - outDataOffset;
            if (toCopyFromOutBuffer > 0) {
                System.arraycopy(outData, outDataOffset, b, off, toCopyFromOutBuffer);
                off += toCopyFromOutBuffer;
                outDataOffset += toCopyFromOutBuffer;
                totalRead += toCopyFromOutBuffer;
            } else if (endDecoding)
                break;
            if (!endDecoding && totalRead < len)
                endDecoding = decodeNextFrame();
        } while (true);
        if (totalRead > 0)
            return totalRead;
        if (endDecoding) {
            if (DEBUG)
                System.out.println("Close.   outTotalSize=" + outFrameSize + "  outOffset=" + outDataOffset + "  remaining pendingData=" + pendingDataSize);
            close();
            return -1;
        } else {
            return totalRead;
        }
    }

    private boolean decodeNextFrame()
            throws IOException {
        boolean ERROR = false;
        boolean needRead = false;
        boolean skipFrame = false;
        FrameInfo info = new FrameInfo();
        do {
            if (!streamEOF && (needRead || pendingDataSize < 2024)) {
                if (pendingDataOffset > 0 && pendingDataSize > 0)
                    System.arraycopy(pendingData, pendingDataOffset, pendingData, 0, pendingDataSize);
                pendingDataOffset = 0;
                do {
                    if (pendingDataSize + 20 >= pendingData.length || streamEOF)
                        break;
                    int read = super.read(pendingData, pendingDataSize, pendingData.length - pendingDataSize);
                    if (read < 0) {
                        streamEOF = true;
                    } else {
                        pendingDataSize += read;
                        if (pendingDataSize < pendingData.length)
                            Thread.yield();
                    }
                } while (true);
            }
            if (skipFrame) {
                skipFrame = false;
                try {
                    decoder.getCurrFrameInfo(info);
                } catch (MPADException e2) {
                    try {
                        decoder.getNextFrameInfo(info, pendingData, pendingDataOffset, pendingDataSize);
                    } catch (MPADException e3) {
                        if (streamEOF) {
                            pendingDataSize = 0;
                            return true;
                        }
                        skipFrame = true;
                        needRead = true;
                        continue;
                    }
                }
                int byteCount = info.getHeaderOffset() + info.getFrameLength();
                if (byteCount > pendingDataSize)
                    byteCount = pendingDataSize;
                pendingDataOffset += byteCount;
                pendingDataSize -= byteCount;
                if (pendingDataSize < 0)
                    pendingDataSize = 0;
            }
            if (streamEOF && pendingDataSize <= 13)
                return true;
            try {
                boolean useDecodeLast = false;
                if (streamEOF && pendingDataSize > 0) {
                    decoder.getNextFrameInfo(info, pendingData, pendingDataOffset, pendingDataSize);
                    useDecodeLast = info.getFrameLength() + info.getHeaderOffset() >= pendingDataSize;
                }
                int byteCount;
                if (useDecodeLast) {
                    if (DEBUG)
                        System.out.println("Using DecodeLast");
                    byteCount = decoder.decodeLast(fsamp, fsampOffset, pendingData, pendingDataOffset, pendingDataSize);
                } else {
                    byteCount = decoder.decode(fsamp, fsampOffset, pendingData, pendingDataOffset, pendingDataSize);
                }
                decoder.getCurrFrameInfo(info);
                outFrameSize = info.getNumberOfSamples() * info.getNumberOfChannels() * 2;
                if (info.getNumberOfChannels() == 1) {
                    if (outputBigEndian)
                        OutputConverter.convert(outData, 0, fsamp[0], fsampOffset[0], info.getNumberOfSamples());
                    else
                        convertLE(outData, 0, fsamp[0], fsampOffset[0], info.getNumberOfSamples());
                } else if (outputBigEndian)
                    OutputConverter.convert(outData, 0, fsamp[0], fsampOffset[0], fsamp[1], fsampOffset[1], info.getNumberOfSamples());
                else
                    convertLE(outData, 0, fsamp[0], fsampOffset[0], fsamp[1], fsampOffset[1], info.getNumberOfSamples());
                outDataOffset = 0;
                pendingDataOffset += byteCount;
                pendingDataSize -= byteCount;
                if (pendingDataSize < 0)
                    pendingDataSize = 0;
                needRead = false;
                continue;
            } catch (MPADException e) {
                if (e.getState() == -10) {
                    if (DEBUG)
                        System.out.println("JS_MP3DecoderStream: frame not supported.");
                    skipFrame = true;
                    continue;
                }
                if (e.getState() == -3) {
                    if (!streamEOF) {
                        if (pendingDataOffset > 0 || pendingDataSize < 13) {
                            if (DEBUG)
                                System.out.println("JS_MP3DecoderStream: Low buffer -> need to read from input.");
                            needRead = true;
                        } else {
                            if (DEBUG)
                                System.out.println("JS_MP3DecoderStream: Low buffer  but enough input data available! -> skip this frame.");
                            skipFrame = true;
                        }
                        continue;
                    }
                    if (DEBUG)
                        System.out.println("JS_MP3DecoderStream: Low buffer but EOF -> finish");
                    pendingDataSize = 0;
                    break;
                }
                if (e.getState() == -9) {
                    if (DEBUG)
                        System.out.println("JS_MP3DecoderStream: NO_PREV-> skip this frame");
                    skipFrame = true;
                } else if (e.getState() == -7) {
                    if (DEBUG)
                        System.out.println("JS_MP3DecoderStream: no header found -> skip this frame");
                    skipFrame = true;
                } else {
                    if (DEBUG)
                        System.out.println("JS_MP3DecoderStream: Unknown state: " + e.getState() + ". Skipping frame.");
                    skipFrame = true;
                }
            } catch (Exception e) {
                if (DEBUG) {
                    System.out.println("Unexpected Exception! Advancing to next frame.");
                    e.printStackTrace();
                }
                skipFrame = true;
            }
        } while (needRead || skipFrame);
        return streamEOF && pendingDataSize <= 13 || ERROR;
    }
}
