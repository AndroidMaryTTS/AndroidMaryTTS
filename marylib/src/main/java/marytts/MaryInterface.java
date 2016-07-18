package marytts;


import java.util.Locale;
import java.util.Set;

import lib.sound.sampled.AudioInputStream;
import marytts.exceptions.SynthesisException;
import mf.org.w3c.dom.Document;

public interface MaryInterface {

    /**
     * Get the current input type, either the default ("TEXT") or the value most recently set through {@link #setInputType(String)}.
     *
     * @return the currently set input type.
     */
    String getInputType();

    /**
     * Set the input type for processing to the new input type.
     *
     * @param newInputType a string representation of a MaryDataType.
     * @throws IllegalArgumentException if newInputType is not a valid and known input data type.
     */
    void setInputType(String newInputType) throws IllegalArgumentException;

    /**
     * Get the current output type, either the default ("AUDIO") or the value most recently set through {@link #setInputType(String)}.
     *
     * @return the currently set input type.
     */
    String getOutputType();

    /**
     * Set the output type for processing to the new output type.
     *
     * @param newOutputType a string representation of a MaryDataType.
     * @throws IllegalArgumentException if newOutputType is not a valid and known output data type.
     */
    void setOutputType(String newOutputType) throws IllegalArgumentException;

    /**
     * Get the current locale used for processing. Either the default (US English) or the value most recently set through {@link #setLocale(Locale)} or indirectly through {@link #setVoice(String)}.
     *
     * @return the locale
     */
    Locale getLocale();

    /**
     * Set the locale for processing. Set the voice to the default voice for this locale.
     *
     * @param newLocale a supported locale.
     * @throws IllegalArgumentException if newLocale is not among the {@link #getAvailableLocales()}.
     */
    void setLocale(Locale newLocale) throws IllegalArgumentException;

    /**
     * The name of the current voice, if any.
     *
     * @return the voice name, or null if no voice is currently set.
     */
    String getVoice();

    /**
     * Set the voice to be used for processing. If the current locale differs from the voice's locale, the locale is updated accordingly.
     *
     * @param voiceName the name of a valid voice.
     * @throws IllegalArgumentException if voiceName is not among the {@link #getAvailableVoices()}.
     */
    void setVoice(String voiceName) throws IllegalArgumentException;

    /**
     * Get the currently set audio effects. For advanced use only.
     *
     * @return
     */
    String getAudioEffects();

    /**
     * Set the audio effects. For advanced use only.
     *
     * @param audioEffects
     */
    void setAudioEffects(String audioEffects);

    /**
     * Get the currently speaking style. For advanced use only.
     *
     * @return
     */
    String getStyle();

    void setStyle(String newStyle);

    /**
     * Get the currently set output type parameters. For advanced use only.
     *
     * @return
     */
    String getOutputTypeParams();

    /**
     * Set the output type parameters. For advanced use only.
     *
     * @param params
     */
    void setOutputTypeParams(String params);

    /**
     * Whether to stream audio. For advanced use only.
     *
     * @return
     */
    boolean isStreamingAudio();

    void setStreamingAudio(boolean newIsStreaming);

    /**
     * Partial processing command, converting an input text format such as TEXT into an output text format such as TARGETFEATURES.
     *
     * @param text
     * @return
     * @throws SynthesisException
     */
    String generateText(String text) throws SynthesisException;

    /**
     * Partial processing command, converting an input XML format such as SSML into an output text format such as TARGETFEATURES.
     *
     * @param doc
     * @return
     * @throws SynthesisException
     */
    String generateText(Document doc) throws SynthesisException;

    /**
     * Partial processing command, converting an input text format such as TEXT into an XML format such as ALLOPHONES.
     *
     * @param text
     * @return
     * @throws SynthesisException
     */
    Document generateXML(String text) throws SynthesisException;

    /**
     * Partial processing command, converting one XML format such as RAWMARYXML into another XML format such as TOKENS.
     *
     * @param doc
     * @return
     * @throws SynthesisException
     */
    Document generateXML(Document doc) throws SynthesisException;

    /**
     * Synthesis from a text format to audio. This is the method you want to call for text-to-speech conversion.
     *
     * @param text
     * @return
     * @throws SynthesisException
     */
    AudioInputStream generateAudio(String text)
            throws SynthesisException;

    /**
     * Synthesis from an XML format, such as SSML, to audio.
     *
     * @param doc
     * @return
     * @throws SynthesisException
     */
    AudioInputStream generateAudio(Document doc)
            throws SynthesisException;

    /**
     * List the names of all the voices that can be used in {@link #setVoice(String)}.
     *
     * @return the set of voices, or an empty set if no voices are available.
     */
    Set<String> getAvailableVoices();

    /**
     * List the names of all the voices for the given locale that can be used in {@link #setVoice(String)}.
     *
     * @param locale
     * @return the set of voices, or an empty set if no voices are available for the given locale.
     */
    Set<String> getAvailableVoices(Locale locale);

    /**
     * List the locales that can be used in {@link #setLocale(Locale)}.
     *
     * @return the set of locales, or the empty set if no locales are available.
     */
    Set<Locale> getAvailableLocales();

    /**
     * List the names of the input types that can be used in {@link #setInputType(String)}.
     *
     * @return
     */
    Set<String> getAvailableInputTypes();

    /**
     * List the names of the input types that can be used in {@link #setInputType(String)}.
     *
     * @return
     */
    Set<String> getAvailableOutputTypes();

    /**
     * Check whether the given data type is a text type. For input types (i.e. types contained in {@link #getAvailableInputTypes()})
     * that are text types, the synthesis methods {@link #generateText(String)}, {@link #generateXML(String)} and {@link #generateAudio(String)}
     * can be used; for output types that are text types, the synthesis methods {@link #generateText(String)} and {@link #generateText(Document)}
     * can be used.
     *
     * @param dataType an input or output data type.
     * @return
     */
    boolean isTextType(String dataType);

    /**
     * Check whether the given data type is an XML type. For input types (i.e. types contained in {@link #getAvailableInputTypes()})
     * that are XML types, the synthesis methods {@link #generateText(Document)}, {@link #generateXML(Document)} and {@link #generateAudio(Document)}
     * can be used; for output types that are XML types, the synthesis methods {@link #generateXML(String)} and {@link #generateXML(Document)}
     * can be used.
     *
     * @param dataType an input or output data type.
     * @return
     */
    boolean isXMLType(String dataType);

    /**
     * Check whether the given data type is an audio type. There are no input audio types; for output audio types, the methods {@link #generateAudio(String)}
     * and {@link #generateAudio(Document)} can be used.
     *
     * @param dataType an input or output data type
     * @return
     */
    boolean isAudioType(String dataType);

}