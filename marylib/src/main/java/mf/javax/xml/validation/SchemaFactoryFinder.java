/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

/*
 * $Id: SchemaFactoryFinder.java,v 1.8 2010-11-01 04:36:13 joehw Exp $
 * %W% %E%
 */
package mf.javax.xml.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Implementation of {@link SchemaFactory#newInstance(String)}.
 *
 * @author <a href="Kohsuke.Kawaguchi@Sun.com">Kohsuke Kawaguchi</a>
 * @version $Revision: 1.8 $, $Date: 2010-11-01 04:36:13 $
 * @since 1.5
 */
class SchemaFactoryFinder {

    private static final Class SERVICE_CLASS = SchemaFactory.class;
    private static final String SERVICE_ID = "META-INF/services/" + SERVICE_CLASS.getName();
    /**
     * debug support code.
     */
    private static boolean debug = false;
    /**
     * <p> Take care of restrictions imposed by java security model </p>
     */
    private static SecuritySupport ss = new SecuritySupport();
    /**
     * <p>Cache properties for performance.</p>
     */
    private static Properties cacheProps = new Properties();
    /**
     * <p>First time requires initialization overhead.</p>
     */
    private static volatile boolean firstTime = true;

    static {
        // Use try/catch block to support applets
        try {
            debug = ss.getSystemProperty("jaxp.debug") != null;
        } catch (Exception _) {
            debug = false;
        }
    }

    /**
     * <p><code>ClassLoader</code> to use to find <code>SchemaFactory</code>.</p>
     */
    private final ClassLoader classLoader;

    /**
     * <p>Constructor that specifies <code>ClassLoader</code> to use
     * to find <code>SchemaFactory</code>.</p>
     *
     * @param loader to be used to load resource, {@link SchemaFactory}, and
     *               {@link SchemaFactoryLoader} implementations during
     *               the resolution process.
     *               If this parameter is null, the default system class loader
     *               will be used.
     */
    public SchemaFactoryFinder(ClassLoader loader) {
        this.classLoader = loader;
        if (debug) {
            debugDisplayClassLoader();
        }
    }

    /**
     * <p>Conditional debug printing.</p>
     *
     * @param msg to print
     */
    private static void debugPrintln(String msg) {
        if (debug) {
            System.err.println("JAXP: " + msg);
        }
    }

    /**
     * Try to construct using newTransformerFactoryNoServiceLoader
     * method if available.
     */
    private static Object newInstanceNoServiceLoader(
            Class<?> providerClass
    ) {
        // Retain maximum compatibility if no security manager.
        if (System.getSecurityManager() == null) {
            return null;
        }
        try {
            Method creationMethod =
                    providerClass.getDeclaredMethod(
                            "newXMLSchemaFactoryNoServiceLoader"
                    );
            return creationMethod.invoke(null, (Object[]) null);
        } catch (NoSuchMethodException exc) {
            return null;
        } catch (Exception exc) {
            return null;
        }
    }

    private static String which(Class clazz) {
        return which(clazz.getName(), clazz.getClassLoader());
    }

    /**
     * <p>Search the specified classloader for the given classname.</p>
     *
     * @param classname the fully qualified name of the class to search for
     * @param loader    the classloader to search
     * @return the source location of the resource, or null if it wasn't found
     */
    private static String which(String classname, ClassLoader loader) {

        String classnameAsResource = classname.replace('.', '/') + ".class";

        if (loader == null) loader = ClassLoader.getSystemClassLoader();

        //URL it = loader.getResource(classnameAsResource);
        URL it = ss.getResourceAsURL(loader, classnameAsResource);
        if (it != null) {
            return it.toString();
        } else {
            return null;
        }
    }

    private void debugDisplayClassLoader() {
        try {
            if (classLoader == ss.getContextClassLoader()) {
                debugPrintln("using thread context class loader (" + classLoader + ") for search");
                return;
            }
        } catch (Throwable _) {
            // getContextClassLoader() undefined in JDK1.1
        }

        if (classLoader == ClassLoader.getSystemClassLoader()) {
            debugPrintln("using system class loader (" + classLoader + ") for search");
            return;
        }

        debugPrintln("using class loader (" + classLoader + ") for search");
    }

    /**
     * <p>Creates a new {@link SchemaFactory} object for the specified
     * schema language.</p>
     *
     * @param schemaLanguage See {@link SchemaFactory Schema Language} table in <code>SchemaFactory</code>
     *                       for the list of available schema languages.
     * @return <code>null</code> if the callee fails to create one.
     * @throws NullPointerException If the <code>schemaLanguage</code> parameter is null.
     */
    public SchemaFactory newFactory(String schemaLanguage) {
        if (schemaLanguage == null) throw new NullPointerException();
        SchemaFactory f = _newFactory(schemaLanguage);
        if (f != null) {
            debugPrintln("factory '" + f.getClass().getName() + "' was found for " + schemaLanguage);
        } else {
            debugPrintln("unable to find a factory for " + schemaLanguage);
        }
        return f;
    }

    /**
     * <p>Lookup a <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.</p>
     *
     * @param schemaLanguage Schema language to lookup <code>SchemaFactory</code> for.
     * @return <code>SchemaFactory</code> for the given <code>schemaLanguage</code>.
     */
    private SchemaFactory _newFactory(String schemaLanguage) {
        SchemaFactory sf;

        String propertyName = SERVICE_CLASS.getName() + ":" + schemaLanguage;

        // system property look up
        try {
            debugPrintln("Looking up system property '" + propertyName + "'");
            String r = ss.getSystemProperty(propertyName);
            if (r != null) {
                debugPrintln("The value is '" + r + "'");
                sf = createInstance(r, true);
                if (sf != null) return sf;
            } else
                debugPrintln("The property is undefined.");
        } catch (Throwable t) {
            if (debug) {
                debugPrintln("failed to look up system property '" + propertyName + "'");
                t.printStackTrace();
            }
        }

        String javah = ss.getSystemProperty("java.home");
        String configFile = javah + File.separator +
                "lib" + File.separator + "jaxp.properties";

        String factoryClassName = null;

        // try to read from $java.home/lib/jaxp.properties
        try {
            if (firstTime) {
                synchronized (cacheProps) {
                    if (firstTime) {
                        File f = new File(configFile);
                        firstTime = false;
                        if (ss.doesFileExist(f)) {
                            debugPrintln("Read properties file " + f);
                            cacheProps.load(ss.getFileInputStream(f));
                        }
                    }
                }
            }
            factoryClassName = cacheProps.getProperty(propertyName);
            debugPrintln("found " + factoryClassName + " in $java.home/jaxp.properties");

            if (factoryClassName != null) {
                sf = createInstance(factoryClassName, true);
                if (sf != null) {
                    return sf;
                }
            }
        } catch (Exception ex) {
            if (debug) {
                ex.printStackTrace();
            }
        }

        /**
         // try to read from $java.home/lib/jaxp.properties
         try {
         String javah = ss.getSystemProperty( "java.home" );
         String configFile = javah + File.separator +
         "lib" + File.separator + "jaxp.properties";
         File f = new File( configFile );
         if( ss.doesFileExist(f)) {
         sf = loadFromProperty(
         propertyName,f.getAbsolutePath(), new FileInputStream(f));
         if(sf!=null)    return sf;
         } else {
         debugPrintln("Tried to read "+ f.getAbsolutePath()+", but it doesn't exist.");
         }
         } catch(Throwable e) {
         if( debug ) {
         debugPrintln("failed to read $java.home/lib/jaxp.properties");
         e.printStackTrace();
         }
         }
         */

        // try META-INF/services files
        Iterator sitr = createServiceFileIterator();
        while (sitr.hasNext()) {
            URL resource = (URL) sitr.next();
            debugPrintln("looking into " + resource);
            try {
                sf = loadFromService(schemaLanguage, resource.toExternalForm(),
                        ss.getURLInputStream(resource));
                if (sf != null) return sf;
            } catch (IOException e) {
                if (debug) {
                    debugPrintln("failed to read " + resource);
                    e.printStackTrace();
                }
            }
        }

        // platform default
        if (schemaLanguage.equals("http://www.w3.org/2001/XMLSchema")) {
            debugPrintln("attempting to use the platform default XML Schema validator");
            return createInstance("com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory", true);
        }

        debugPrintln("all things were tried, but none was found. bailing out.");
        return null;
    }

    /**
     * <p>Create class using appropriate ClassLoader.</p>
     *
     * @param className Name of class to create.
     * @return Created class or <code>null</code>.
     */
    private Class createClass(String className) {
        Class clazz;

        // use approprite ClassLoader
        try {
            if (classLoader != null) {
                clazz = classLoader.loadClass(className);
            } else {
                clazz = Class.forName(className);
            }
        } catch (Throwable t) {
            if (debug) t.printStackTrace();
            return null;
        }

        return clazz;
    }

    /**
     * <p>Creates an instance of the specified and returns it.</p>
     *
     * @param className fully qualified class name to be instanciated.
     * @return null
     * if it fails. Error messages will be printed by this method.
     */
    SchemaFactory createInstance(String className) {
        return createInstance(className, false);
    }

    SchemaFactory createInstance(String className, boolean useServicesMechanism) {
        SchemaFactory schemaFactory = null;

        debugPrintln("createInstance(" + className + ")");

        // get Class from className
        Class clazz = createClass(className);
        if (clazz == null) {
            debugPrintln("failed to getClass(" + className + ")");
            return null;
        }
        debugPrintln("loaded " + className + " from " + which(clazz));

        // instantiate Class as a SchemaFactory
        try {
            if (!useServicesMechanism) {
                schemaFactory = (SchemaFactory) newInstanceNoServiceLoader(clazz);
            }
            if (schemaFactory == null) {
                schemaFactory = (SchemaFactory) clazz.newInstance();
            }
        } catch (ClassCastException classCastException) {
            debugPrintln("could not instantiate " + clazz.getName());
            if (debug) {
                classCastException.printStackTrace();
            }
            return null;
        } catch (IllegalAccessException illegalAccessException) {
            debugPrintln("could not instantiate " + clazz.getName());
            if (debug) {
                illegalAccessException.printStackTrace();
            }
            return null;
        } catch (InstantiationException instantiationException) {
            debugPrintln("could not instantiate " + clazz.getName());
            if (debug) {
                instantiationException.printStackTrace();
            }
            return null;
        }

        return schemaFactory;
    }

    /**
     * Looks up a value in a property file
     * while producing all sorts of debug messages.
     *
     * @return null
     * if there was an error.
     */
    private SchemaFactory loadFromProperty(String keyName, String resourceName, InputStream in)
            throws IOException {
        debugPrintln("Reading " + resourceName);

        Properties props = new Properties();
        props.load(in);
        in.close();
        String factoryClassName = props.getProperty(keyName);
        if (factoryClassName != null) {
            debugPrintln("found " + keyName + " = " + factoryClassName);
            return createInstance(factoryClassName);
        } else {
            debugPrintln(keyName + " is not in the property file");
            return null;
        }
    }

    /**
     * <p>Look up a value in a property file.</p>
     * <p/>
     * <p>Set <code>debug</code> to <code>true</code> to trace property evaluation.</p>
     *
     * @param schemaLanguage Schema Language to support.
     * @param inputName      Name of <code>InputStream</code>.
     * @param in             <code>InputStream</code> of properties.
     * @return <code>SchemaFactory</code> as determined by <code>keyName</code> value or <code>null</code> if there was an error.
     * @throws IOException If IO error reading from <code>in</code>.
     */
    private SchemaFactory loadFromService(
            String schemaLanguage,
            String inputName,
            InputStream in)
            throws IOException {

        SchemaFactory schemaFactory = null;
        final Class[] stringClassArray = {"".getClass()};
        final Object[] schemaLanguageObjectArray = {schemaLanguage};
        final String isSchemaLanguageSupportedMethod = "isSchemaLanguageSupported";

        debugPrintln("Reading " + inputName);

        // read from InputStream until a match is found
        BufferedReader configFile = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while ((line = configFile.readLine()) != null) {
            // '#' is comment char
            int comment = line.indexOf("#");
            switch (comment) {
                case -1:
                    break; // no comment
                case 0:
                    line = "";
                    break; // entire line is a comment
                default:
                    line = line.substring(0, comment);
                    break; // trim comment
            }

            // trim whitespace
            line = line.trim();

            // any content left on line?
            if (line.length() == 0) {
                continue;
            }

            // line content is now the name of the class
            Class clazz = createClass(line);
            if (clazz == null) {
                continue;
            }

            // create an instance of the Class
            try {
                schemaFactory = (SchemaFactory) clazz.newInstance();
            } catch (ClassCastException classCastExcpetion) {
                schemaFactory = null;
                continue;
            } catch (InstantiationException instantiationException) {
                schemaFactory = null;
                continue;
            } catch (IllegalAccessException illegalAccessException) {
                schemaFactory = null;
                continue;
            }

            // does this Class support desired Schema?
            try {
                Method isSchemaLanguageSupported = clazz.getMethod(isSchemaLanguageSupportedMethod, stringClassArray);
                Boolean supported = (Boolean) isSchemaLanguageSupported.invoke(schemaFactory, schemaLanguageObjectArray);
                if (supported.booleanValue()) {
                    break;
                }
            } catch (NoSuchMethodException noSuchMethodException) {

            } catch (IllegalAccessException illegalAccessException) {

            } catch (InvocationTargetException invocationTargetException) {

            }
            schemaFactory = null;
        }

        // clean up
        configFile.close();

        // return new instance of SchemaFactory or null
        return schemaFactory;
    }

    /**
     * Returns an {@link Iterator} that enumerates all
     * the META-INF/services files that we care.
     */
    private Iterator createServiceFileIterator() {
        if (classLoader == null) {
            return new SingleIterator() {
                @Override
                protected Object value() {
                    ClassLoader classLoader = SchemaFactoryFinder.class.getClassLoader();
                    //return (ClassLoader.getSystemResource( SERVICE_ID ));
                    return ss.getResourceAsURL(classLoader, SERVICE_ID);
                }
            };
        } else {
            try {
                //final Enumeration e = classLoader.getResources(SERVICE_ID);
                final Enumeration e = ss.getResources(classLoader, SERVICE_ID);
                if (!e.hasMoreElements()) {
                    debugPrintln("no " + SERVICE_ID + " file was found");
                }

                // wrap it into an Iterator.
                return new Iterator() {
                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }

                    @Override
                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    @Override
                    public Object next() {
                        return e.nextElement();
                    }
                };
            } catch (IOException e) {
                debugPrintln("failed to enumerate resources " + SERVICE_ID);
                if (debug) e.printStackTrace();
                return new ArrayList().iterator();  // empty iterator
            }
        }
    }

    /**
     * Iterator that lazily computes one value and returns it.
     */
    private static abstract class SingleIterator implements Iterator {
        private boolean seen = false;

        @Override
        public final void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean hasNext() {
            return !seen;
        }

        @Override
        public final Object next() {
            if (seen) throw new NoSuchElementException();
            seen = true;
            return value();
        }

        protected abstract Object value();
    }
}
