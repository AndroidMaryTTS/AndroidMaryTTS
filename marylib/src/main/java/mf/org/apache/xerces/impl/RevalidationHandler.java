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

package mf.org.apache.xerces.impl;

import mf.org.apache.xerces.xni.Augmentations;
import mf.org.apache.xerces.xni.parser.XMLDocumentFilter;

/**
 * DOM Revalidation handler adds additional functionality to XMLDocumentHandler
 *
 * @author Elena Litani, IBM
 * @version $Id: RevalidationHandler.java 446761 2006-09-15 21:59:29Z mrglavas $
 * @xerces.internal
 */
public interface RevalidationHandler extends XMLDocumentFilter {

    /**
     * Character content.
     *
     * @param data The character data.
     * @param augs Augmentations
     * @return True if data is whitespace only
     */
    boolean characterData(String data, Augmentations augs);


} // interface DOMRevalidationHandler
