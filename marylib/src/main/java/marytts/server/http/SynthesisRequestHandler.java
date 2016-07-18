
package marytts.server.http;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import lib.sound.sampled.AudioFileFormat;
import lib.sound.sampled.AudioFormat;
import lib.sound.sampled.AudioInputStream;
import lib.sound.sampled.AudioSystem;
import marytts.datatypes.MaryDataType;
import marytts.modules.synthesis.Voice;
import marytts.server.Mary;
import marytts.server.Request;
import marytts.server.RequestHandler.StreamingOutputPiper;
import marytts.server.RequestHandler.StreamingOutputWriter;
import marytts.util.MaryRuntimeUtils;
import marytts.util.MaryUtils;
import marytts.util.data.audio.MaryAudioUtils;
import marytts.util.http.Address;

//import org.apache.log4j.Logger;

/**
 * Provides functionality to process synthesis http requests
 *
 * @author Oytun T&uumlrk
 */
public class SynthesisRequestHandler extends BaseHttpRequestHandler {

    private static int id = 0;
    private StreamingOutputWriter outputToStream;
    private StreamingOutputPiper streamToPipe;
    private PipedOutputStream pipedOutput;
    private PipedInputStream pipedInput;

    public SynthesisRequestHandler() {
        super();

        outputToStream = null;
        streamToPipe = null;
        pipedOutput = null;
        pipedInput = null;
    }

    private static synchronized int getId() {

        return id++;
    }

    @Override
    protected void handleClientRequest(String absPath,
                                       Map<String, String> queryItems, HttpResponse response,
                                       Address serverAddressAtClient) throws IOException {
        /*
         * response.setStatusCode(HttpStatus.SC_OK); TestProducingNHttpEntity
		 * entity = new TestProducingNHttpEntity();
		 * entity.setContentType("audio/x-mp3"); response.setEntity(entity); if
		 * (true) return;
		 */
        Log.d(Mary.LOG, "New synthesis request: " + absPath);
        if (queryItems != null) {
            for (String key : queryItems.keySet()) {
                Log.d(Mary.LOG, "    " + key + "=" + queryItems.get(key));
            }
        }
        process(serverAddressAtClient, queryItems, response);

    }

    public void process(Address serverAddressAtClient,
                        Map<String, String> queryItems, HttpResponse response) {
        if (queryItems == null
                || !(queryItems.containsKey("INPUT_TYPE")
                && queryItems.containsKey("OUTPUT_TYPE")
                && queryItems.containsKey("LOCALE")
                && queryItems.containsKey("INPUT_TEXT"))) {
            MaryHttpServerUtils
                    .errorMissingQueryParameter(response,
                            "'INPUT_TEXT' and 'INPUT_TYPE' and 'OUTPUT_TYPE' and 'LOCALE'");
            return;
        }

        Locale locale = MaryUtils.string2locale(queryItems.get("LOCALE"));
        if (locale == null) {
            MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                    "LOCALE", queryItems.get("LOCALE"), null);
            return;
        }

        String prosody = "";
        MaryDataType inputType = null;
        String inputText = "";

        String prosody_rate = (queryItems.get("prosody_rate") == null || queryItems
                .get("prosody_rate").isEmpty()) ? "" : queryItems.get(
                "prosody_rate").trim();

        String seperate_prosody = queryItems.get("seperate_prosody") == null ? ""
                : queryItems.get("seperate_prosody");
        seperate_prosody = seperate_prosody.replace("enter", "\"\n\"");


        String seperateParagraph = (queryItems.get("seperateParagraph") == null || queryItems
                .get("seperateParagraph").isEmpty()) ? "" : queryItems.get(
                "seperateParagraph").trim();

        if (!prosody_rate.isEmpty()) {
            prosody_rate = prosody_rate.contains("%") ? prosody_rate
                    : prosody_rate + "%";
            prosody += "rate=\"" + prosody_rate.trim() + "\"";
        }


        if (queryItems.get("INPUT_TYPE").equalsIgnoreCase("TEXT")) {

            inputType = MaryDataType.get("RAWMARYXML");

            String[] sentences = queryItems.get("INPUT_TEXT").split("\n");

            for (String line : sentences) {
                if (line.trim().isEmpty()) {

                    continue;

                }

                Reader reader = new StringReader(line.trim());
                DocumentPreprocessor dp = new DocumentPreprocessor(reader);
                Iterator<List<HasWord>> it = dp.iterator();

                while (it.hasNext()) {
                    String sentence = new String();
                    List<HasWord> sentenceAsList = it.next();
                    int wordCount = 0;

                    for (HasWord token : sentenceAsList) {

                        String word = token.word();
                        wordCount++;

                        if (wordCount == 1
                                || word.equalsIgnoreCase(".")
                                || word.equalsIgnoreCase("?")
                                || word.equalsIgnoreCase("!")
                                || word.equalsIgnoreCase(";")) {

                            sentence += word;
                        } else {
                            if (word.equalsIgnoreCase(",")) {
                                sentence += word;

                                if (!seperate_prosody.isEmpty()) {
                                    sentence += seperate_prosody;
                                }

                                if (seperateParagraph
                                        .equalsIgnoreCase("seperate")) {

                                    inputText += "<p><prosody "
                                            + prosody.trim() + ">" + sentence
                                            + "</prosody></p>";

                                    sentence = "";
                                    wordCount = 0;
                                }

                            } else {
                                sentence += " " + word;
                            }

                        }
                    }

                    inputText += "<p><prosody " + prosody.trim() + ">"
                            + sentence.trim() + "</prosody></p>";
                }

            }

            inputText = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><maryxml xmlns=\"http://mary.dfki.de/2002/MaryXML\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                    + " version=\"0.5\" xml:lang=\""
                    + queryItems.get("LOCALE")
                    + "\">" + inputText + "</maryxml>";
            System.out.println("***********************");
            System.out.println(inputText);

        } else {
            inputType = MaryDataType.get(queryItems.get("INPUT_TYPE"));
            inputText = queryItems.get("INPUT_TEXT");
        }


        if (inputType == null) {
            MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                    "INPUT_TYPE", queryItems.get("INPUT_TYPE"), null);
            return;
        }

        MaryDataType outputType = MaryDataType.get(queryItems
                .get("OUTPUT_TYPE"));
        if (outputType == null) {
            MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                    "OUTPUT_TYPE", queryItems.get("OUTPUT_TYPE"), null);
            return;
        }
        boolean isOutputText = true;
        boolean streamingAudio = false;
        AudioFileFormat.Type audioFileFormatType = null;
        if (outputType.name().contains("AUDIO")) {
            isOutputText = false;
            String audioTypeName = queryItems.get("AUDIO");
            if (audioTypeName == null) {
                MaryHttpServerUtils.errorMissingQueryParameter(response,
                        "'AUDIO' when OUTPUT_TYPE=AUDIO");
                return;
            }
            if (audioTypeName.endsWith("_STREAM")) {
                streamingAudio = true;
            }
            int lastUnderscore = audioTypeName.lastIndexOf('_');
            if (lastUnderscore != -1) {
                audioTypeName = audioTypeName.substring(0, lastUnderscore);
            }
            try {
                audioFileFormatType = MaryAudioUtils
                        .getAudioFileFormatType(audioTypeName);
            } catch (Exception ex) {
            }
            if (audioFileFormatType == null) {
                MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                        "AUDIO", queryItems.get("AUDIO"), null);
                return;
            } else if (audioFileFormatType.toString().equals("MP3")
                    && !MaryRuntimeUtils.canCreateMP3()) {
                MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                        "AUDIO", queryItems.get("AUDIO"),
                        "Conversion to MP3 not supported.");
                return;
            } else if (audioFileFormatType.toString().equals("Vorbis")
                    && !MaryRuntimeUtils.canCreateOgg()) {
                MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                        "AUDIO", queryItems.get("AUDIO"),
                        "Conversion to OGG Vorbis format not supported.");
                return;
            }
        }
        // optionally, there may be output type parameters
        // (e.g., the list of features to produce for the output type
        // TARGETFEATURES)
        String outputTypeParams = queryItems.get("OUTPUT_TYPE_PARAMS");


        Voice voice = null;
        String voiceName = queryItems.get("VOICE");
        if (voiceName != null) {
            if (voiceName.equals("male") || voiceName.equals("female")) {
                voice = Voice.getVoice(locale, new Voice.Gender(voiceName));
            } else {
                voice = Voice.getVoice(voiceName);
            }
            if (voice == null) {
                // a voice name was given but there is no such voice
                MaryHttpServerUtils.errorWrongQueryParameterValue(response,
                        "VOICE", queryItems.get("VOICE"), null);
                return;
            }
        }
        if (voice == null) { // no voice tag -- use locale default if it exists.
            voice = Voice.getDefaultVoice(locale);
            Log.d(Mary.LOG, "No voice requested -- using default " + voice);
        }

        String style = queryItems.get("STYLE");
        if (style == null)
            style = "";

        String effects = toRequestedAudioEffectsString(queryItems);
        if (effects.length() > 0)
            Log.d(Mary.LOG, "Audio effects requested: " + effects);
        else
            Log.d(Mary.LOG, "No audio effects requested");

        String logMsg = queryItems.get("LOG");
        if (logMsg != null) {
            Log.i(Mary.LOG, "Connection info: " + logMsg);
        }

        // Now, the parse is complete.

        // Construct audio file format -- even when output is not AUDIO,
        // in case we need to pass via audio to get our output type.
        if (audioFileFormatType == null) {
            audioFileFormatType = AudioFileFormat.Type.AU;
        }
        AudioFormat audioFormat;
        if (audioFileFormatType.toString().equals("MP3")) {
            audioFormat = MaryRuntimeUtils.getMP3AudioFormat();
        } else if (audioFileFormatType.toString().equals("Vorbis")) {
            audioFormat = MaryRuntimeUtils.getOggAudioFormat();
        } else if (voice != null) {
            audioFormat = voice.dbAudioFormat();
        } else {
            audioFormat = Voice.AF16000;
        }
        AudioFileFormat audioFileFormat = new AudioFileFormat(
                audioFileFormatType, audioFormat, AudioSystem.NOT_SPECIFIED);

        final Request maryRequest = new Request(inputType, outputType, locale,
                voice, effects, style, getId(), audioFileFormat,
                streamingAudio, outputTypeParams);

        // Process the request and send back the data
        boolean ok = true;
        try {
            maryRequest.setInputData(inputText);
            Log.i(Mary.LOG, "Read: " + inputText);
        } catch (Exception e) {
            String message = "Problem reading input";
            Log.w(Mary.LOG, message, e);
            MaryHttpServerUtils.errorInternalServerError(response, message, e);
            ok = false;
        }
        if (ok) {
            if (streamingAudio) {
                // Start two separate threads:
                // 1. one thread to process the request;
                new Thread("RH " + maryRequest.getId()) {
                    @Override
                    public void run() {
                        //Logger myLogger = MaryUtils.getLogger(this.getName());
                        try {
                            maryRequest.process();
                            Log.i(Mary.LOG, "Streaming request processed successfully.");
                        } catch (Throwable t) {

                        }
                    }
                }.start();

                // 2. one thread to take the audio data as it becomes available
                // and write it into the ProducingNHttpEntity.
                // The second one does not depend on the first one practically,
                // because the AppendableSequenceAudioInputStream returned by
                // maryRequest.getAudio() was already created in the constructor
                // of Request.
                AudioInputStream audio = maryRequest.getAudio();
                assert audio != null : "Streaming audio but no audio stream -- very strange indeed! :-(";
                AudioFileFormat.Type audioType = maryRequest
                        .getAudioFileFormat().getType();
                AudioStreamNHttpEntity entity = new AudioStreamNHttpEntity(
                        maryRequest);
                new Thread(entity, "HTTPWriter " + maryRequest.getId()).start();
                // entity knows its contentType, no need to set explicitly here.
                response.setEntity(entity);
                response.setStatusCode(HttpStatus.SC_OK);
                return;
            } else { // not streaming audio
                // Process input data to output data
                try {
                    maryRequest.process(); // this may take some time
                } catch (Throwable e) {
                    String message = "Processing failed.";

                    MaryHttpServerUtils.errorInternalServerError(response,
                            message, e);
                    ok = false;
                }
                if (ok) {
                    // Write output data to client
                    try {
                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                        maryRequest.writeOutputData(outputStream);
                        String contentType;
                        if (maryRequest.getOutputType().isXMLType()
                                || maryRequest.getOutputType().isTextType()) // text
                            // output
                            contentType = "text/plain; charset=UTF-8";
                        else
                            // audio output
                            contentType = MaryHttpServerUtils
                                    .getMimeType(maryRequest
                                            .getAudioFileFormat().getType());
                        MaryHttpServerUtils.toHttpResponse(
                                outputStream.toByteArray(), response,
                                contentType);
                    } catch (Exception e) {
                        String message = "Cannot write output";
                        Log.w(Mary.LOG, message, e);
                        MaryHttpServerUtils.errorInternalServerError(response,
                                message, e);
                        ok = false;
                    }
                }
            }
        }

        if (ok)
            Log.i(Mary.LOG, "Request handled successfully.");
        else
            Log.i(Mary.LOG, "Request couldn't be handled successfully.");
        if (MaryRuntimeUtils.lowMemoryCondition()) {
            Log.i(Mary.LOG, "Low memory condition detected (only "
                    + MaryUtils.availableMemory()
                    + " bytes left). Triggering garbage collection.");
            Runtime.getRuntime().gc();
            Log.i(Mary.LOG, "After garbage collection: "
                    + MaryUtils.availableMemory() + " bytes available.");
        }
    }

    protected String toRequestedAudioEffectsString(
            Map<String, String> keyValuePairs) {
        StringBuilder effects = new StringBuilder();
        StringTokenizer tt;
        Set<String> keys = keyValuePairs.keySet();
        String currentKey;
        String currentEffectName, currentEffectParams;
        for (Iterator<String> it = keys.iterator(); it.hasNext(); ) {
            currentKey = it.next();
            if (currentKey.startsWith("effect_")) {
                if (currentKey.endsWith("_selected")) {
                    if (keyValuePairs.get(currentKey).compareTo("on") == 0) {
                        if (effects.length() > 0)
                            effects.append("+");

                        tt = new StringTokenizer(currentKey, "_");
                        if (tt.hasMoreTokens())
                            tt.nextToken(); // Skip "effects_"
                        if (tt.hasMoreTokens()) // The next token is the effect
                        // name
                        {
                            currentEffectName = tt.nextToken();

                            currentEffectParams = keyValuePairs.get("effect_"
                                    + currentEffectName + "_parameters");
                            if (currentEffectParams != null
                                    && currentEffectParams.length() > 0)
                                effects.append(currentEffectName).append("(")
                                        .append(currentEffectParams)
                                        .append(")");
                            else
                                effects.append(currentEffectName);
                        }
                    }
                }
            }
        }

        return effects.toString();
    }

}
