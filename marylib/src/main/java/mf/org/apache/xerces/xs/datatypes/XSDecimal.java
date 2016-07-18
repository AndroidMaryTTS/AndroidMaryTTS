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
package mf.org.apache.xerces.xs.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>Interface to expose the value of 'decimal' and related datatypes.</p>
 *
 * @author Naela Nissar, IBM
 * @version $Id: XSDecimal.java 447250 2006-09-18 05:27:17Z mrglavas $
 */
public interface XSDecimal {

    /**
     * @return the <code>BigDecimal</code> representation of this object
     */
    BigDecimal getBigDecimal();

    /**
     * @return the <code>BigInteger</code> representation of this object
     * @throws NumberFormatException if the value cannot be represented as a <code>BigInteger</code>
     */
    BigInteger getBigInteger() throws NumberFormatException;

    /**
     * @return the long value representation of this object
     * @throws NumberFormatException if the value cannot be represented as a <code>long</code>
     */
    long getLong() throws NumberFormatException;

    /**
     * @return the int value representation of this object
     * @throws NumberFormatException if the value cannot be represented as a <code>int</code>
     */
    int getInt() throws NumberFormatException;

    /**
     * @return the short value representation of this object
     * @throws NumberFormatException if the value cannot be represented as a <code>short</code>
     */
    short getShort() throws NumberFormatException;

    /**
     * @return the byte value representation of this object
     * @throws NumberFormatException if the value cannot be represented as a <code>byte</code>
     */
    byte getByte() throws NumberFormatException;
}
