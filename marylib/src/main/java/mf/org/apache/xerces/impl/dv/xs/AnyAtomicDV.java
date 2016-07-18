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

package mf.org.apache.xerces.impl.dv.xs;

import mf.org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import mf.org.apache.xerces.impl.dv.ValidationContext;

/**
 * Represent the schema type "anyAtomicType"
 *
 * @author Ankit Pasricha, IBM
 * @version $Id: AnyAtomicDV.java 446745 2006-09-15 21:43:58Z mrglavas $
 * @xerces.experimental
 */
class AnyAtomicDV extends TypeValidator {

    @Override
    public short getAllowedFacets() {
        return 0;
    }

    @Override
    public Object getActualValue(String content, ValidationContext context) throws InvalidDatatypeValueException {
        return content;
    }

} // class AnyAtomicDV
