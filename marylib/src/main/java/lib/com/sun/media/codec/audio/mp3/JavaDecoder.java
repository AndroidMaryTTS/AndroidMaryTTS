package lib.com.sun.media.codec.audio.mp3;
//// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
//// Jad home page: http://www.kpdus.com/jad.html
//// Decompiler options: packimports(3) 
//// Source File Name:   JavaDecoder.java
//
//package july.com.sun.media.codec.audio.mp3;
//
//import codecLib.mp3.*;
//import com.sun.media.codec.audio.AudioCodec;
//import java.io.PrintStream;
//import javax.media.*;
//import javax.media.format.AudioFormat;
//
//public class JavaDecoder extends AudioCodec
//{
//
//    public JavaDecoder()
//    {
//        pendingDataSize = 0;
//        pendingData = new byte[32768];
//        decoder = null;
//        info = null;
//        expectingSameInputBuffer = false;
//        accumTS = 0L;
//        aFormat = null;
//        fsamp = new float[12][1152];
//        fsampOffset = new int[12];
//        MAXOUTFRAMESIZE = 27648;
//        MIMINFRAMESIZE = 13;
//        outFrameSize = 0;
//        inputFormats = (new Format[] {
//            new AudioFormat("mpeglayer3", 16000D, -1, -1, -1, 1), new AudioFormat("mpeglayer3", 22050D, -1, -1, -1, 1), new AudioFormat("mpeglayer3", 24000D, -1, -1, -1, 1), new AudioFormat("mpeglayer3", 32000D, -1, -1, -1, 1), new AudioFormat("mpeglayer3", 44100D, -1, -1, -1, 1), new AudioFormat("mpeglayer3", 48000D, -1, -1, -1, 1), new AudioFormat("mpegaudio", 16000D, -1, -1, -1, 1), new AudioFormat("mpegaudio", 22050D, -1, -1, -1, 1), new AudioFormat("mpegaudio", 24000D, -1, -1, -1, 1), new AudioFormat("mpegaudio", 32000D, -1, -1, -1, 1), 
//            new AudioFormat("mpegaudio", 44100D, -1, -1, -1, 1), new AudioFormat("mpegaudio", 48000D, -1, -1, -1, 1)
//        });
//    }
//
//    public String getName()
//    {
//        return "MPEG Layer 3 Decoder";
//    }
//
//    public Format[] getSupportedOutputFormats(Format input)
//    {
//        if(input == null)
//            return (new Format[] {
//                new AudioFormat("LINEAR")
//            });
//        if(input instanceof AudioFormat)
//        {
//            AudioFormat af = (AudioFormat)input;
//            outputFormats = (new Format[] {
//                new AudioFormat("LINEAR", af.getSampleRate(), af.getSampleSizeInBits(), af.getChannels(), 1, 1)
//            });
//        } else
//        {
//            outputFormats = new Format[0];
//        }
//        return outputFormats;
//    }
//
//    public synchronized void open()
//        throws ResourceUnavailableException
//    {
//        if(decoder != null)
//            close();
//        try
//        {
//            decoder = new Decoder();
//            pendingDataSize = 0;
//            expectingSameInputBuffer = false;
//            accumTS = 0L;
//            aFormat = (AudioFormat)outputFormat;
//            return;
//        }
//        catch(Throwable e)
//        {
//            System.out.println("mpa JavaDecoder: open " + e);
//        }
//        throw new ResourceUnavailableException("could not open " + getName());
//    }
//
//    public synchronized void close()
//    {
//        if(decoder != null)
//            decoder = null;
//        if(info != null)
//            info = null;
//    }
//
//    public synchronized void reset()
//    {
//        if(decoder != null)
//        {
//            close();
//            try
//            {
//                open();
//            }
//            catch(ResourceUnavailableException rue)
//            {
//                System.err.println("MP3 Decoder: " + rue);
//            }
//        }
//    }
//
//    public synchronized int process(Buffer in, Buffer out)
//    {
//        if(isEOM(in))
//        {
//            propagateEOM(out);
//            return 0;
//        }
//        Object inObject = in.getData();
//        Object outObject = out.getData();
//        if(outObject == null)
//        {
//            outObject = new byte[32768];
//            out.setData(outObject);
//        }
//        if(!(inObject instanceof byte[]) || !(outObject instanceof byte[]))
//            return 1;
//        byte inData[] = (byte[])inObject;
//        byte outData[] = (byte[])outObject;
//        int inLength = in.getLength();
//        int inOffset = in.getOffset();
//        int outDataSize = outData.length;
//        int outOffset = 0;
//        int pendingDataOffset = 0;
//        int byteCount = 0;
//        if(!expectingSameInputBuffer && pendingDataSize + inLength <= pendingData.length)
//        {
//            System.arraycopy(inData, inOffset, pendingData, pendingDataSize, inLength);
//            pendingDataSize += inLength;
//        }
//        if(decoder != null)
//            do
//            {
//                if(outDataSize - outOffset < MAXOUTFRAMESIZE || pendingDataSize < MIMINFRAMESIZE)
//                    break;
//                if(info == null)
//                {
//                    info = new FrameInfo();
//                    try
//                    {
//                        decoder.getNextFrameInfo(info, pendingData, pendingDataOffset, pendingDataSize);
//                        outFrameSize = info.getNumberOfSamples() * info.getNumberOfChannels() * 2;
//                    }
//                    catch(MPADException e)
//                    {
//                        info = null;
//                        break;
//                    }
//                }
//                try
//                {
//                    byteCount = decoder.decode(fsamp, fsampOffset, pendingData, pendingDataOffset, pendingDataSize);
//                }
//                catch(MPADException e)
//                {
//                    if(e.getState() == -10)
//                        return 1;
//                    try
//                    {
//                        decoder.getCurrFrameInfo(info);
//                    }
//                    catch(MPADException e2)
//                    {
//                        info = null;
//                        break;
//                    }
//                    if(e.getState() == -9)
//                    {
//                        byteCount = info.getHeaderOffset() + info.getFrameLength();
//                        pendingDataOffset += byteCount;
//                        pendingDataSize -= byteCount;
//                        continue;
//                    }
//                    info = null;
//                    break;
//                }
//                if(info.getNumberOfChannels() == 1)
//                    OutputConverter.convert(outData, outOffset, fsamp[0], fsampOffset[0], info.getNumberOfSamples());
//                else
//                    OutputConverter.convert(outData, outOffset, fsamp[0], fsampOffset[0], fsamp[1], fsampOffset[1], info.getNumberOfSamples());
//                outOffset += outFrameSize;
//                pendingDataOffset += byteCount;
//                pendingDataSize -= byteCount;
//            } while(true);
//        if(pendingDataOffset != 0)
//            System.arraycopy(pendingData, pendingDataOffset, pendingData, 0, pendingDataSize);
//        out.setLength(outOffset);
//        out.setFormat(outputFormat);
//        if(aFormat != null && accumTS != 0L && in.getTimeStamp() > 0L)
//            out.setTimeStamp(in.getTimeStamp() + aFormat.computeDuration(accumTS));
//        if(pendingDataSize > 1024)
//        {
//            expectingSameInputBuffer = true;
//            accumTS += out.getLength();
//            return 2;
//        } else
//        {
//            accumTS = 0L;
//            expectingSameInputBuffer = false;
//            return 0;
//        }
//    }
//
//    public static void main(String args[])
//    {
//        try
//        {
//            Codec c = new JavaDecoder();
//            Format inputs[] = c.getSupportedInputFormats();
//            Format outputs[] = c.getSupportedOutputFormats(null);
//            PlugInManager.addPlugIn("com.sun.media.codec.audio.mp3.JavaDecoder", inputs, outputs, 2);
//            PlugInManager.commit();
//            System.out.println("Registered succesfully");
//        }
//        catch(Throwable t)
//        {
//            System.err.println("Error: Could not register plugin. \nCheck if jmf.properties is in the CLASSPATH and is writable.");
//        }
//    }
//
//    private int pendingDataSize;
//    private static final int OUTSIZE = 32768;
//    private byte pendingData[];
//    private Decoder decoder;
//    private FrameInfo info;
//    private boolean expectingSameInputBuffer;
//    private long accumTS;
//    private AudioFormat aFormat;
//    float fsamp[][];
//    int fsampOffset[];
//    int MAXOUTFRAMESIZE;
//    int MIMINFRAMESIZE;
//    int outFrameSize;
//}
