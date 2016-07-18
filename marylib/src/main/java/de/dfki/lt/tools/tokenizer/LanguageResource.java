/*
 * JTok
 * A configurable tokenizer implemented in Java
 *
 * (C) 2003 - 2014  DFKI Language Technology Lab http://www.dfki.de/lt
 *   Author: Joerg Steffen, steffen@dfki.de
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU Lesser General Public
 *   License as published by the Free Software Foundation; either
 *   version 2.1 of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *   Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public
 *   License along with this library; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package de.dfki.lt.tools.tokenizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dfki.lt.tools.tokenizer.exceptions.InitializationException;
import de.dfki.lt.tools.tokenizer.exceptions.ProcessingException;
import de.dfki.lt.tools.tokenizer.regexp.RegExp;
import mf.javax.xml.parsers.DocumentBuilder;
import mf.javax.xml.parsers.DocumentBuilderFactory;
import mf.org.w3c.dom.Document;
import mf.org.w3c.dom.Element;
import mf.org.w3c.dom.Node;
import mf.org.w3c.dom.NodeList;

/**
 * Manages the language-specific information needed by the tokenizer to process a document of that
 * language.
 *
 * @author Joerg Steffen, DFKI
 */
public class LanguageResource {

    // name suffix of the resource file with the classes hierarchy
    private static final String CLASSES_HIERARCHY = "_class_hierarchy.xml";

    // name suffix of the config file with the macros
    private static final String MACRO_CFG = "_macros.cfg";


    // name of the language for which this class contains the resources
    private String language;

    // root element of the classes hierarchy
    private Element classesRoot;

    // name of the root element of the classes hierarchy
    private String classesRootName;

    // map from class names to a lists of class names that are ancestors of this class
    private Map<String, List<String>> ancestorsMap;

    // punctuation description
    private PunctDescription punctDescr;

    // clitics description
    private CliticsDescription clitDescr;

    // abbreviations description
    private AbbrevDescription abbrevDescr;

    // token classes description
    private TokenClassesDescription classesDescr;


    /**
     * Creates a new instance of {@link LanguageResource} for the given language using the resource
     * description files in the given resource directory.
     *
     * @param lang        the name of the language for which this class contains the resources
     * @param resourceDir the name of the resource directory
     * @throws InitializationException if an error occurs
     */
    public LanguageResource(String lang, String resourceDir) {

        // init stuff
        this.setAncestorsMap(new HashMap<String, List<String>>());
        this.setPunctDescr(null);
        this.setClitDescr(null);
        this.setAbbrevDescr(null);
        this.setClassseDescr(null);
        this.language = lang;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            // create builder for parsing xml
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(
                    FileTools.openResourceFileAsStream(
                            resourceDir + "/" + lang + CLASSES_HIERARCHY));


            // set hierarchy root
            this.setClassesRoot(doc.getDocumentElement());
            // map class names to dom elements
            this.mapSingleClass(this.getClassesRoot());
            this.mapClasses(this.getClassesRoot().getChildNodes());

            // load macros
            Map<String, String> macrosMap = new HashMap<>();
            Description.loadMacros(resourceDir + "/" + lang + MACRO_CFG, macrosMap);

            // load punctuation description
            this.setPunctDescr(new PunctDescription(resourceDir, lang, macrosMap));

            // load clitics description
            this.setClitDescr(new CliticsDescription(resourceDir, lang, macrosMap));

            // load abbreviation description
            this.setAbbrevDescr(new AbbrevDescription(resourceDir, lang, macrosMap));

            // load token classes description document
            this.setClassseDescr(new TokenClassesDescription(resourceDir, lang, macrosMap));
        } catch (Throwable ex) {
            System.out.println(ex.getMessage());
        }
//    } catch (SAXException spe) {
//    	System.out.println(spe);
//      throw new InitializationException(spe.getLocalizedMessage(), spe);
//    } catch (ParserConfigurationException pce) {
//    	System.out.println(pce);
//      throw new InitializationException(pce.getLocalizedMessage(), pce);
//    } catch (IOException ioe) {
//    	System.out.println(ioe);
//      throw new InitializationException(ioe.getLocalizedMessage(), ioe);
//    }
    }


    /**
     * @return the language
     */
    public String getLanguage() {

        return this.language;
    }


    /**
     * @return the classes root element
     */
    Element getClassesRoot() {

        return this.classesRoot;
    }


    /**
     * @param classesRoot the classes root element to set
     */
    void setClassesRoot(Element classesRoot) {

        this.classesRoot = classesRoot;
        this.classesRootName = classesRoot.getTagName();
    }


    /**
     * @return the ancestor map
     */
    Map<String, List<String>> getAncestorsMap() {

        return this.ancestorsMap;
    }


    /**
     * @param ancestorMap the ancestor map to set
     */
    void setAncestorsMap(Map<String, List<String>> ancestorMap) {

        this.ancestorsMap = ancestorMap;
    }


    /**
     * @return the punctuation description
     */
    PunctDescription getPunctDescr() {

        return this.punctDescr;
    }


    /**
     * @param punctDescr the punctuation description to set
     */
    void setPunctDescr(PunctDescription punctDescr) {

        this.punctDescr = punctDescr;
    }


    /**
     * @return the clitics description
     */
    CliticsDescription getClitDescr() {

        return this.clitDescr;
    }


    /**
     * @param clitDescr the clitics description to set
     */
    void setClitDescr(CliticsDescription clitDescr) {

        this.clitDescr = clitDescr;
    }


    /**
     * @return the abbreviations description
     */
    AbbrevDescription getAbbrevDescr() {

        return this.abbrevDescr;
    }


    /**
     * @param abbrevDescr the abbreviations description to set
     */
    void setAbbrevDescr(AbbrevDescription abbrevDescr) {

        this.abbrevDescr = abbrevDescr;
    }


    /**
     * @return the token classes description
     */
    TokenClassesDescription getClassesDescr() {

        return this.classesDescr;
    }


    /**
     * @param classesDescr the token classes description to set
     */
    void setClassseDescr(TokenClassesDescription classesDescr) {

        this.classesDescr = classesDescr;
    }


    /**
     * Iterates recursively over a list of class elements and adds each elements ancestors to
     * ancestors map using the name of the element as key.
     *
     * @param elementList node list of class elements
     */
    private void mapClasses(NodeList elementList) {

        // iterate over elements
        for (int i = 0, iMax = elementList.getLength(); i < iMax; i++) {
            Object oneObj = elementList.item(i);
            if (!(oneObj instanceof Element)) {
                continue;
            }
            Element oneEle = (Element) oneObj;
            this.mapSingleClass(oneEle);
            // add children of element to maps
            if (oneEle.getChildNodes().getLength() > 0) {
                this.mapClasses(oneEle.getChildNodes());
            }
        }
    }


    /**
     * Creates mappings for the given class in the ancestor maps.
     *
     * @param ele a class element
     */
    private void mapSingleClass(Element ele) {

        String key = ele.getTagName();
        // collect ancestors of element
        List<String> ancestors = new ArrayList<>();
        Node directAncestor = ele.getParentNode();
        while ((null != directAncestor)
                && (directAncestor instanceof Element)
                && (directAncestor != this.classesRoot)) {
            ancestors.add(((Element) directAncestor).getTagName());
            directAncestor = directAncestor.getParentNode();
        }
        // add list to ancestors map
        this.getAncestorsMap().put(key, ancestors);
    }


    /**
     * Checks if the first given class is ancestor in the class hierarchy of the second given class
     * or equals the second given class.
     *
     * @param class1 the first class name
     * @param class2 the second class name
     * @return a flag indicating the ancestor relation
     * @throws ProcessingException if the second class name is not a defined class
     */
    boolean isAncestor(String class1, String class2) {

        if (class1.equals(this.classesRootName) || class1.equals(class2)) {
            return true;
        }

        List<String> ancestors = this.getAncestorsMap().get(class2);
        if (null == ancestors) {
            // if there is explicit entry in the classes hierarchy, it is assumed
            // that the token is a direct child of the root
            return class1.equals(this.classesRootName);
        }
        return ancestors.contains(class1);
    }


    /**
     * @return the matcher for all punctuation from the punctuation description
     */
    RegExp getAllPunctMatcher() {

        return this.getPunctDescr().getRulesMap().get(PunctDescription.ALL_RULE);
    }


    /**
     * @return the matcher for internal punctuation from the punctuation description
     */
    RegExp getInternalMatcher() {

        return this.getPunctDescr().getRulesMap().get(PunctDescription.INTERNAL_RULE);
    }


    /**
     * @return the matcher for sentence internal punctuation from the punctuation description
     */
    RegExp getInternalTuMatcher() {

        return this.getPunctDescr().getRulesMap().get(PunctDescription.INTERNAL_TU_RULE);
    }


    /**
     * @return the matcher for proclitics from the clitics description
     */
    RegExp getProcliticsMatcher() {

        return this.getClitDescr().getRulesMap().get(CliticsDescription.PROCLITIC_RULE);
    }


    /**
     * @return the matcher for enclitics from the clitics description
     */
    RegExp getEncliticsMatcher() {

        return this.getClitDescr().getRulesMap().get(CliticsDescription.ENCLITIC_RULE);
    }


    /**
     * @return the map with the abbreviation lists
     */
    Map<String, Set<String>> getAbbrevLists() {

        return this.getAbbrevDescr().getClassMembersMap();
    }


    /**
     * @return the matcher for the all abbreviations from the abbreviations description
     */
    RegExp getAllAbbrevMatcher() {

        return this.getAbbrevDescr().getRulesMap().get(AbbrevDescription.ALL_RULE);
    }


    /**
     * @return the set of the most common terms that only start with a capital letter when they are at
     * the beginning of a sentence
     */
    Set<String> getNonCapTerms() {

        return this.getAbbrevDescr().getNonCapTerms();
    }


    /**
     * @return the matcher for all token classes from the token classes description
     */
    RegExp getAllClassesMatcher() {

        return this.getClassesDescr().getRulesMap().get(TokenClassesDescription.ALL_RULE);
    }
}
