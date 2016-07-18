/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mf.org.apache.xerces.jaxp.validation;

import mf.org.apache.xerces.xni.grammars.XMLGrammarPool;

/**
 * <p>A container for grammar pools which only contain schema grammars.</p>
 *
 * @author Michael Glavassevich, IBM
 * @version $Id: XSGrammarPoolContainer.java 447235 2006-09-18 05:01:44Z mrglavas $
 */
public interface XSGrammarPoolContainer {

    /**
     * <p>Returns the grammar pool contained inside the container.</p>
     *
     * @return the grammar pool contained inside the container
     */
    XMLGrammarPool getGrammarPool();

    /**
     * <p>Returns whether the schema components contained in this object
     * can be considered to be a fully composed schema and should be
     * used to the exclusion of other schema components which may be
     * present elsewhere.</p>
     *
     * @return whether the schema components contained in this object
     * can be considered to be a fully composed schema
     */
    boolean isFullyComposed();

    /**
     * Returns the initial value of a feature for validators created
     * using this grammar pool container or null if the validators
     * should use the default value.
     */
    Boolean getFeature(String featureId);

}
