/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.configuration2.converter;

import java.math.BigInteger;

import org.apache.commons.configuration2.ConversionException;

/**
 * BigInteger converter.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 2.0
 */
class BigIntegerConverter extends NumberConverter<BigInteger>
{
    private static final TypeConverter instance = new BigIntegerConverter();

    public static TypeConverter getInstance()
    {
        return instance;
    }

    public BigInteger convert(Object value, Object... params) throws ConversionException
    {
        Number n = toNumber(value, BigInteger.class);
        if (n instanceof BigInteger)
        {
            return (BigInteger) n;
        }
        else
        {
            return BigInteger.valueOf(n.longValue());
        }
    }
}