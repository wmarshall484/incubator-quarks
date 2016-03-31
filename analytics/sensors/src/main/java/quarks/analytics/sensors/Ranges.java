/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/
package quarks.analytics.sensors;

import java.math.BigDecimal;
import java.math.BigInteger;

import quarks.analytics.sensors.Range.BoundType;

/**
 * Convenience functions and utility operations on {@link Range}.
 */
public final class Ranges {

    /** 
     * Create a Range (lowerEndpoint..upperEndpoint) (both exclusive/OPEN)
     * <p>
     * Same as {@code new Range<T>(BoundType.OPEN, lowerEndpoint, upperEndpoint, BoundType.OPEN)}
     */
    public static <T extends Comparable<?>> Range<T> open(T lowerEndpoint, T upperEndpoint) { 
        return new Range<T>(BoundType.OPEN, lowerEndpoint, upperEndpoint, BoundType.OPEN);
    }

    /** 
     * Create a Range [lowerEndpoint..upperEndpoint] (both inclusive/CLOSED)
     * <p>
     * Same as {@code new Range<T>(BoundType.CLOSED, lowerEndpoint, upperEndpoint, BoundType.CLOSED)}
     */
    public static <T extends Comparable<?>> Range<T> closed(T lowerEndpoint, T upperEndpoint) {
        return new Range<T>(BoundType.CLOSED, lowerEndpoint, upperEndpoint, BoundType.CLOSED); 
    }

    /** 
     * Create a Range (lowerEndpoint..upperEndpoint] (exclusive/OPEN,inclusive/CLOSED)
     */
    public static <T extends Comparable<?>> Range<T> openClosed(T lowerEndpoint, T upperEndpoint) {
        return new Range<T>(BoundType.OPEN, lowerEndpoint, upperEndpoint, BoundType.CLOSED);
    }

    /** 
     * Create a Range [lowerEndpoint..upperEndpoint) (inclusive/CLOSED,exclusive/OPEN)
     */
    public static <T extends Comparable<?>> Range<T> closedOpen(T lowerEndpoint, T upperEndpoint) {
        return new Range<T>(BoundType.CLOSED, lowerEndpoint, upperEndpoint, BoundType.OPEN);
    }

    /** 
     * Create a Range (lowerEndpoint..upperEndpoint) (both exclusive/OPEN)
     */
    public static <T extends Comparable<?>> Range<T> greaterThan(T v) {
        return new Range<T>(BoundType.OPEN, v, null, BoundType.OPEN);
    }

    /**
     * Create a Range [lowerEndpoint..*) (inclusive/CLOSED)
     */
    public static <T extends Comparable<?>> Range<T> atLeast(T v) {
        return new Range<T>(BoundType.CLOSED, v, null, BoundType.OPEN);
    }

    /** 
     * Create a Range (*..upperEndpoint) (exclusive/OPEN)
     */
    public static <T extends Comparable<?>> Range<T> lessThan(T v) {
        return new Range<T>(BoundType.OPEN, null, v, BoundType.OPEN);
    }

    /** 
     * Create a Range (*..upperEndpoint] (inclusive/CLOSED)
     */
    public static <T extends Comparable<?>> Range<T> atMost(T v) {
        return new Range<T>(BoundType.OPEN, null, v, BoundType.CLOSED);
    }

    /** 
     * Create a Range [v..v] (both inclusive/CLOSED)
     */
    public static  <T extends Comparable<?>> Range<T> singleton(T v) {
        return new Range<T>(BoundType.CLOSED, v, v, BoundType.CLOSED);
    }
    
    /**
     * Create a Range from a Range<Integer>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Integer> valueOfInteger(String str) {
        return Range.valueOf(str, v -> Integer.valueOf(v));
    }
    
    /**
     * Create a Range from a Range<Short>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Short> valueOfShort(String str) {
        return Range.valueOf(str, v -> Short.valueOf(v));
    }
    
    /**
     * Create a Range from a Range<Byte>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Byte> valueOfByte(String str) {
        return Range.valueOf(str, v -> Byte.valueOf(v));
    }
    
    /**
     * Create a Range from a Range<Long>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Long> valueOfLong(String str) {
        return Range.valueOf(str, v -> Long.valueOf(v));
    }
    
    /**
     * Create a Range from a Range<Float>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Float> valueOfFloat(String str) {
        return Range.valueOf(str, v -> Float.valueOf(v));
    }
    
    /**
     * Create a Range from a Range<Double>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Double> valueOfDouble(String str) {
        return Range.valueOf(str, v -> Double.valueOf(v));
    }
    
    /**
     * Create a Range from a Range<BigInteger>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<BigInteger> valueOfBigInteger(String str) {
        return Range.valueOf(str, v -> new BigInteger(v));
    }
    
    /**
     * Create a Range from a Range<BigDecimal>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<BigDecimal> valueOfBigDecimal(String str) {
        return Range.valueOf(str, v -> new BigDecimal(v));
    }
    
    /**
     * Create a Range from a Range<String>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if str includes a String
     * endpoint value containing "..".
     */
    public static Range<String> valueOfString(String str) {
        return Range.valueOf(str, v -> new String(v));
    }
    
    /**
     * Create a Range from a Range<Character>.toString() value. 
     * @param str the String
     * @return the Range
     * @throws IllegalArgumentException if unable to parse or convert
     *         the endpoint strings to the type
     */
    public static Range<Character> valueOfCharacter(String str) {
        return Range.valueOf(str, v -> Character.valueOf(v.charAt(0)));
    }

}
