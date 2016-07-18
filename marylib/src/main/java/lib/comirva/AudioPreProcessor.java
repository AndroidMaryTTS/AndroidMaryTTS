package lib.comirva;

import java.io.IOException;

import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;


/**
 * <b>AudioPreProcessor</b>
 * <p/>
 * <p>Description: </p>
 * This class simplifies the access to audio streams. It frees you from taking
 * care of the encoding of the input stream. The samples of the audio stream get
 * converted and are stored as double values. The sample rate of the
 * output stream can be specifed. The output stream has only one channel. If the
 * input stream has more than one channel, it will get reduced. If the input
 * stream is encoded the <i>Java Sound API</i> will be used for decoding the
 * stream. Also some of the audio conversion operations use the API. So
 * be sure to have correctly configured your <i>Java Audio System</i>.<br>
 * <br>
 * Internally the class has to ensure, that the input buffer is never null and
 * always large enough. It also has to ensure that the number of bits per sample
 * is either 8, 16, 24 or 32. Furthermore the frame size is given in bytes and
 * is always the number of bits per sample divided by eight. If the sample size
 * of the input stream is unknown (-1), the sample size internally used will be
 * 16.
 *
 * @author Klaus Seyerlehner
 * @version 1.6
 */

public class AudioPreProcessor {
    /**
     * default sample rate of the output stream
     */
    public static final float DEFAULT_SAMPLE_RATE = 11025.0f;

    //information about the streams
    protected AudioInputStream in;
    protected float sampleRate;
    protected int frameSize;
    protected boolean isBigEndian;

    //implementation fields
    private byte[] inputBuffer = new byte[1024];
    private double normalise;
    private double scale;
    private double dB_max;

    /**
     * An AudioPreProcessor encapsulates an AudioInputStream object permitting an
     * easy access to the audio data.
     *
     * @param in         AudioInputStream the audio input stream to encapsulate, must not
     *                   be a null value
     * @param sampleRate int the sampleRate of the output stream, must be greater
     *                   than zero and less or equal the sample rate of the
     *                   input stream, such that a conversion is possible;
     *                   not wohle-numbered values get rounded
     * @throws IllegalArgumentException raised if mehtod contract is violated
     */
    public AudioPreProcessor(AudioInputStream in, float sampleRate) throws IllegalArgumentException {
        AudioFormat source, converted;

        //check for not null
        if (in == null)
            throw new IllegalArgumentException("the input stream must not be a null value");

        //check sample rate
        source = in.getFormat();
///////// sampleRate = sampleRate
        this.sampleRate = sampleRate;
        if (sampleRate < 0 || sampleRate > source.getSampleRate())
            throw new IllegalArgumentException("the sample rate to convert to must be greater null and less or equal the sample rate of the input stream");

        //convert input stream to pcm
        this.in = convertToPCM(in);

        try {
            //first try to use a optimized version
            this.in = new ReducedAudioInputStream(this.in, sampleRate);
//      System.out.println("Optimize successfully");
        } catch (IllegalArgumentException iae) {
            //convert sample rate (by using the java sound api)
            this.in = convertSampleRate(this.in, sampleRate);

            //reduce number of channels
            this.in = convertChannels(this.in, 1);
//      System.out.println("failed to optimize");
        }

        //get information about the stream
        converted = this.in.getFormat();

        //check conversion
        if (converted.getSampleSizeInBits() != 8 && converted.getSampleSizeInBits() != 16 && converted.getSampleSizeInBits() != 24 && converted.getSampleSizeInBits() != 32)
            throw new IllegalArgumentException("the sample size of the input stream must be 8, 16, 24 or 32 bit");
        if (converted.getFrameSize() != (converted.getSampleSizeInBits() / 8) || converted.getSampleRate() != sampleRate)
            throw new IllegalArgumentException("the conversion is not supported");

        //set fields
        this.sampleRate = sampleRate;
        this.frameSize = converted.getFrameSize();
        this.isBigEndian = converted.isBigEndian();

        //set dB_max
        dB_max = 6.0d * converted.getSampleSizeInBits();

        //bits - 1 because positive and negatve values are possible
        this.normalise = (double) converted.getChannels() * (1 << (converted.getSampleSizeInBits() - 1));

        //compute rescale factor to rescale and normalise at once (default is 96dB = 2^16)
        this.scale = (Math.pow(10, dB_max / 20)) / normalise;
//    System.out.println("normalise " + this.normalise + " scale " + this.scale);
    }


    /**
     * An AudioPreProcessor encapsulates an AudioInputStream object permitting an
     * easy access to the audio data. The default constructor uses the default
     * sample rate for the output stream.
     *
     * @param in AudioInputStream the audio input stream to encapsulate, must not
     *           be a null value
     * @throws IllegalArgumentException raised if mehtod contract is violated
     */
    public AudioPreProcessor(AudioInputStream in) throws IllegalArgumentException {
        this(in, DEFAULT_SAMPLE_RATE);
    }

    /**
     * Converts an AudioInputStream with arbitrary encoding into an
     * AudioInputStream with signed pulse code modulation (PCM_SIGNED). If the
     * input stream is already a PCM_SIGNED encoded stream this method has no
     * effect. The sample size (in bits) of the input stream is unknown (-1) a
     * default sample size of 16 bits will be used for the target format.The
     * conversion is done using the Java Sound API. Therefore only conversion
     * supported by your audio system can be performed.
     *
     * @param in AudioInputStream the original audio stream, must not be a null
     *           value
     * @return AudioInputStream a pcm signed encoded audio stream
     * @throws IllegalArgumentException raised if mehtod contract is violated or
     *                                  if the conversion is not supported by your
     *                                  audio system
     */
    @SuppressWarnings("unused")
    static public AudioInputStream convertToPCM(AudioInputStream in) throws IllegalArgumentException {
        AudioFormat targetFormat = null;
        AudioInputStream pcm = in;
        AudioFormat sourceFormat = in.getFormat();
        int sampleSizeInBits;

        //check for not null
        if (in == null)
            throw new IllegalArgumentException("the inputstream must not be null values");

        //set appropriate sample size
        sampleSizeInBits = sourceFormat.getSampleSizeInBits();
        if (sampleSizeInBits == -1)
            sampleSizeInBits = 16;
        if (sampleSizeInBits != 8 && sampleSizeInBits != 16 && sampleSizeInBits != 24 && sampleSizeInBits != 32)
            sampleSizeInBits = 16;

        //get source format
        sourceFormat = in.getFormat();

        //convert to pcm
        if (sourceFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {

//    	System.out.println("not encoded11");
            targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    sourceFormat.getSampleRate(),
                    sampleSizeInBits,
                    sourceFormat.getChannels(),
                    sourceFormat.getChannels() * (sampleSizeInBits / 8),
                    sourceFormat.getSampleRate(),
                    false);

            //convert to pcm

            pcm = AudioSystem.getAudioInputStream(targetFormat, pcm);
//        else
//          throw new IllegalArgumentException("conversion to PCM_SIGNED not supported for this input stream");
        }
        return pcm;
    }

    /**
     * Converts the sample rate of an AudioInputStream by returning a new stream
     * with the given sample rate. It meight be usefull to convert the audio
     * stream to PCM_SIGNED first, since your audio system meight not support
     * the direct conversion with other encodings.
     *
     * @param in         AudioInputStream the original audio stream, must not be a null
     *                   value
     * @param sampleRate float the sample rate to convert, must be greater null
     *                   and less or equal the sample rate of the input
     *                   stream
     * @return AudioInputStream an audio stream with the given sample rate
     * @throws IllegalArgumentException raised if mehtod contract is violated or
     *                                  if the conversion is not supported by your
     *                                  audio system
     */
    public static AudioInputStream convertSampleRate(AudioInputStream in, float sampleRate) throws IllegalArgumentException {
        AudioInputStream converted;
        AudioFormat sourceFormat, targetFormat;

        //check for not null
        if (in == null)
            throw new IllegalArgumentException("the inputstream must not be null values");

        //check sample rate
        if (sampleRate < 0 || sampleRate > in.getFormat().getSampleRate())
            throw new IllegalArgumentException("the sample rate to convert to must be greater null and less or equal the sample rate of the input stream");

        converted = in;

        sourceFormat = in.getFormat();
        targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,
                sourceFormat.getSampleSizeInBits(),
                sourceFormat.getChannels(),
                sourceFormat.getFrameSize(),
                sampleRate,
                false);

        if (sourceFormat.getSampleRate() != targetFormat.getSampleRate()) {
            if (AudioSystem.isConversionSupported(targetFormat, sourceFormat))
                converted = AudioSystem.getAudioInputStream(targetFormat, in);
            else
                throw new IllegalArgumentException("Conversion to specified sample rate not supported.");
        }
        return converted;
    }

    /**
     * Converts the number of channels of an AudioInputStream by returning a new
     * stream with the given number of channels. It meight be usefull to convert
     * the audio stream to PCM_SIGNED first, since your audio system meight not
     * support the direct conversion with other encodings.
     *
     * @param in       AudioInputStream the original audio stream, must not be a null
     *                 value
     * @param channels int the number of channels to convert to, must be greater
     *                 null
     * @return AudioInputStream an audio stream with the given number of channels
     * @throws IllegalArgumentException raised if mehtod contract is violated or
     *                                  if the conversion is not supported by your
     *                                  audio system
     */
    public static AudioInputStream convertChannels(AudioInputStream in, int channels) throws IllegalArgumentException {
        AudioInputStream converted;
        AudioFormat sourceFormat, targetFormat;

        //check for not null
        if (in == null)
            throw new IllegalArgumentException("the inputstream must not be null values");

        //check number of channels
        if (channels < 1)
            throw new IllegalArgumentException("the number of channels must be greater than one");

        converted = in;

        //convert channels
        sourceFormat = in.getFormat();
        targetFormat = new AudioFormat(sourceFormat.getSampleRate(),
                sourceFormat.getSampleSizeInBits(),
                channels,
                true,
                sourceFormat.isBigEndian());

        if (sourceFormat.getChannels() != targetFormat.getChannels()) {
            if (AudioSystem.isConversionSupported(targetFormat, sourceFormat))
                converted = AudioSystem.getAudioInputStream(targetFormat, converted);
            else
                throw new IllegalArgumentException("Conversion to specified number of channels not supported.");
        }

        return converted;
    }

    /**
     * Converts the number bits used to represent one sample within an
     * AudioInputStream by returning a new a new stream using the given number of
     * bits to represent one sample. It meight be usefull to convert the audio
     * stream to PCM_SIGNED first, since your audio system meight not support the
     * direct conversion with other encodings. Another effect of the conversion to
     * PCM_SIGNED is, that if the number of bits per sample is unknown (-1) the
     * correct number of bits for the PCM_SIGNED stream will be used.
     *
     * @param in            AudioInputStream the original audio stream, must not be a null
     *                      value
     * @param bitsPerSample int the number of bits per samples to convert to, must
     *                      be either 8, 16, 24 or 32
     * @return AudioInputStream an audio stream with the given number of bits per
     * sample
     * @throws IllegalArgumentException raised if mehtod contract is violated or
     *                                  if the conversion is not supported by your
     *                                  audio system
     */
    public static AudioInputStream convertBitsPerSample(AudioInputStream in, int bitsPerSample) throws IllegalArgumentException {
        AudioInputStream converted;
        AudioFormat sourceFormat, targetFormat;

        //check for not null
        if (in == null)
            throw new IllegalArgumentException("the inputstream must not be null values");

        //check number of bits per sample
        if (bitsPerSample != 8 && bitsPerSample != 16 && bitsPerSample != 24 && bitsPerSample != 32)
            throw new IllegalArgumentException("number of bits must be 8, 16, 24 or 32");

        converted = in;

        sourceFormat = in.getFormat();
        targetFormat = new AudioFormat(sourceFormat.getSampleRate(), bitsPerSample,
                sourceFormat.getChannels(), true,
                sourceFormat.isBigEndian());

        if (targetFormat.getSampleSizeInBits() != sourceFormat.getSampleSizeInBits()) {
            if (AudioSystem.isConversionSupported(targetFormat, sourceFormat))
                converted = AudioSystem.getAudioInputStream(targetFormat, converted);
            else
                throw new IllegalArgumentException("conversion to specified sample size (bits) not supported");
        }

        return converted;
    }

    /**
     * Converts the byte order of an AudioInputStream by returning a new a new
     * stream using the given byte order (big/little Endian). It meight be usefull
     * to convert the audio stream to PCM_SIGNED first, since your audio system
     * meight not support the direct conversion with other encodings.
     *
     * @param in          AudioInputStream the original audio stream, must not be a null
     *                    value
     * @param isBigEndian boolean true if the byte order in the resulting stream
     *                    should be big-Endian, false otherwise
     *                    (little-Endian)
     * @return AudioInputStream an audio stream with the given byte order
     * @throws IllegalArgumentException raised if mehtod contract is violated or
     *                                  if the conversion is not supported by your
     *                                  audio system
     */
    public static AudioInputStream convertByteOrder(AudioInputStream in, boolean isBigEndian) throws IllegalArgumentException {
        AudioInputStream converted;
        AudioFormat sourceFormat, targetFormat;

        //check for not null
        if (in == null)
            throw new IllegalArgumentException("the inputstream must not be null values");

        converted = in;

        sourceFormat = in.getFormat();
        targetFormat = new AudioFormat(sourceFormat.getSampleRate(), sourceFormat.getSampleSizeInBits(), sourceFormat.getChannels(), true, isBigEndian);
        if (targetFormat.isBigEndian() != sourceFormat.isBigEndian()) {
            if (AudioSystem.isConversionSupported(targetFormat, sourceFormat))
                converted = AudioSystem.getAudioInputStream(targetFormat, converted);
            else
                throw new IllegalArgumentException("conversion of byte order not supported");
        }

        return converted;
    }

    /**
     * Returns the sample rate of the output stream.
     *
     * @return the sample rate of the output data
     */
    public float getSampleRate() {
        return sampleRate;
    }

    /**
     * Skips a specified number of samples. Internally this is done by reading
     * the specified number of bytes. This is because the skipping of encoded
     * streams is only possible on frame basis.
     *
     * @param len number of samples to skip, must be a positve value
     * @return the number of samples actually skipped within the input stream
     * @throws IllegalArgumentException raised if mehtod contract is violated
     * @throws IOException              if there are any problems regarding the inputstream
     * @see #fastSkip(int)
     */
    public int skip(int len) throws IllegalArgumentException, IOException {
        int bytesRead = 0;
        int bytesToRead = 0;
        int total = 0;

        //check len
        if (len < 0)
            throw new IllegalArgumentException("len must be a positve value");

        //skip by reading blocks of the size of the buffer
        int blockSize = inputBuffer.length;

        //compute byte to read
        bytesToRead = len * frameSize;

        //read new data from inputstream
        while (total < bytesToRead) {
            //adjust blockSize for the last block to skip
            if (blockSize > bytesToRead - total)
                blockSize = bytesToRead - total;

            //read one block of data
            bytesRead = read(blockSize);

            //compute total bytes read
            total += bytesRead;

            //check if we read the correct number of bytes
            if (blockSize != bytesRead)
                return total / frameSize;
        }

        //return number of samples read
        return total / frameSize;
    }

    /**
     * This method only skips on frame basis. Therefore you cannot skip a precise
     * number of bytes. You should at least skip one frame, or no bytes will be
     * skippd. The real number of skipped bytes will be returned.
     *
     * @param len number of samples to skip, must be a positve value
     * @return the number of samples actually skipped within the input stream
     * @throws IllegalArgumentException raised if mehtod contract is violated
     * @throws IOException              if there are any problems regarding the inputstream
     */
    public int fastSkip(int len) throws IllegalArgumentException, IOException {
        if (len < 0)
            throw new IllegalArgumentException("the number of frames to skip must be positiv");

        if (len == 0)
            return 0;

        return ((int) in.skip(len * frameSize)) / frameSize;
    }

    /**
     * Returns a specified number of samples.
     *
     * @param len a number of samples to return, must be a positive value
     * @return an array of samples, the size of the array is the number of samples
     * read
     * @throws IllegalArgumentException raised if mehtod contract is violated
     * @throws IOException              if there are any problems regarding the inputstream
     */
    public double[] get(int len) throws IllegalArgumentException, IOException {
        int bytesRead = 0;
        int bytesToRead = 0;
        double[] outputData;

        //check len
        if (len < 0)
            throw new IllegalArgumentException("len must be a positive value");

        //compute byte to read
        bytesToRead = len * frameSize;

        //read new data from inputstream
        bytesRead = read(bytesToRead);

        //allocate array for data
        outputData = new double[bytesRead / frameSize];

        //now calculate double values
        convertToDouble(inputBuffer, bytesRead, outputData, 0);

        return outputData;
    }

    /**
     * Appends a specified number of samples to an array.
     *
     * @param buffer the buffer for the samples to be written in, must not be a
     *               null value and suffizent large to hold the number of samples
     *               specified by len
     * @param start  start index at which the new samples are filled into the
     *               array, must be greater equal zero; starting form this index
     *               there must be enough space to hold the number of samples
     *               specified by len
     * @param len    number of samples to be written into the buffer, must be greater
     *               than or equal to zero.
     * @return number of samples actually read form the input stream
     * @throws IllegalArgumentException raised if mehtod contract is violated
     * @throws IOException              if there are any problems regarding the inputstream
     */
    public int append(double[] buffer, int start, int len) throws IllegalArgumentException, IOException {
        int bytesRead = 0;
        int bytesToRead = 0;

        //check start and len
        if (start < 0 || len < 0)
            throw new IllegalArgumentException("start and len must be positiv values");

        //check the buffer size
        if (buffer == null || buffer.length - start < len)
            throw new IllegalArgumentException("Specified buffer is too samll to hold all samples.");

        //compute byte to read
        bytesToRead = len * frameSize;

        //read new data from inputstream
        bytesRead = read(bytesToRead);

        //now append new data
        convertToDouble(inputBuffer, bytesRead, buffer, start);

        return bytesRead / frameSize;
    }

    /**
     * This method is internally used to convert the bytes of the input stream to
     * double values.
     * <p/>
     * Some scaling of the amplitude is done, such that the values exactly fit
     * the MatLab implementation. However, it seems that the matlab implementation
     * is wrong, and simply amplifies the signal approximately by 92%. One could
     * achieve a small speed up by removing the scaling operation.
     * <p/>
     * It is assumed that bitsPerSample / 8 == frameSize. No checks for correct
     * parameter values are performed, since this method is of internal use only.
     * All calling methods within this class must ensure the preconditions.
     *
     * @param in    byte[] input date, bytes of the audio input stream
     * @param len   int number of valid bytes in the input buffer
     * @param out   double[] output buffer for the db-SPL values
     * @param start int start index within the output buffer
     */
    protected void convertToDouble(byte[] in, int len, double[] out, int start) {
//	  protected AudioInputStream in;
//	  protected float sampleRate;
//	  protected int frameSize;
//	  protected boolean isBigEndian;
//
//	  //implementation fields
//	  private byte[] inputBuffer = new byte[1024];
//	  private double normalise;
//	  private double scale;
//	  private double dB_max;
//	System.out.println("FS " + frameSize + " SR " + sampleRate + " normalise " + normalise + " scale " + scale + " dBmax " + dB_max);
        int db = 0;

        if (frameSize == 1) {
            //8-bit signed PCM
            for (int i = 0; i < len; i += frameSize)
                out[start++] = ((double) in[i]) * scale;
        } else {
            //more than one byte per sample
            if (isBigEndian) {
                //the bytes of one sample value are in big-Endian order
                //first byte will be converted to int with respect to the sign
                db = (int) in[0];
                for (int i = 1, j = frameSize; i < len; i++) {
                    if (i == j)//finished one sample?
                    {
                        //convert to double value (and rescale for compatibility to the matlab implementation)
                        out[start++] = ((double) db) * scale;
                        //first byte will be converted to int with respect to the sign
                        db = (int) in[i];
                        //set the end of the next sample value
                        j += frameSize;
                    } else {
                        //combine the bytes of the sample (unsigned bytes)
                        db = db << 8 | ((int) in[i] & 0xff);
                    }
                }
                //convert to double value (and rescale for compatibility to the matlab implementation)
                out[start++] = ((double) db) * scale;
            } else {
                //the bytes of one sample value are in little-Endian order
                for (int i = 0; i < len; i += frameSize) {
                    //first byte will be converted to int with respect to the sign
                    db = (int) in[i + frameSize - 1];

                    //combine the bytes of the sample (unsigned bytes) in reversed order
                    for (int b = frameSize - 2; b >= 0; b--)
                        db = db << 8 | ((int) in[i + b] & 0xff);

                    //convert to double value (and rescale for compatibility to the matlab implementation)
                    out[start++] = ((double) db) * scale;
                }
            }
        }
    }

    /**
     * Reads exactly the specified number of bytes, if the end of the stream has
     * not been reached. If the end of the stream has allready been reached, the
     * number of bytes read will be returend.
     *
     * @param bytesToRead int specifies how many bytes should be read
     * @return int actual number of bytes read; only differs from bytesToRead at
     * the end of the stream
     * @throws IOException raised if an io operation fails
     */
    private int read(int bytesToRead) throws
            IOException {
        int nBytesRead = 0;
        int nBytesTotalRead = 0;

        //check buffer size and adjust it
        if (inputBuffer.length < bytesToRead)
            inputBuffer = new byte[bytesToRead];

        while (nBytesRead != -1 && bytesToRead > 0) {
            nBytesRead = in.read(inputBuffer, nBytesTotalRead, bytesToRead);
            if (nBytesRead != -1) {
                bytesToRead -= nBytesRead;
                nBytesTotalRead += nBytesRead;
            }
        }

        return nBytesTotalRead;
    }
}
