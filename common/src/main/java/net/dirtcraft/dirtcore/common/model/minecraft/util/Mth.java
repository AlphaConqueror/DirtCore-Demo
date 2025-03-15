/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.util;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

public class Mth {

    public static final float PI = (float) Math.PI;
    public static final float HALF_PI = ((float) Math.PI / 2F);
    public static final float TWO_PI = ((float) Math.PI * 2F);
    public static final float DEG_TO_RAD = ((float) Math.PI / 180F);
    public static final float RAD_TO_DEG = (180F / (float) Math.PI);
    public static final float EPSILON = 1.0E-5F;
    public static final float SQRT_OF_TWO = sqrt(2.0F);
    private static final long UUID_VERSION = 61440L;
    private static final long UUID_VERSION_TYPE_4 = 16384L;
    private static final long UUID_VARIANT = -4611686018427387904L;
    private static final long UUID_VARIANT_2 = Long.MIN_VALUE;
    private static final float SIN_SCALE = 10430.378F;
    private static final float[] SIN = make(new float[65536], (p_14077_) -> {
        for (int i = 0; i < p_14077_.length; ++i) {
            p_14077_[i] = (float) Math.sin((double) i * Math.PI * 2.0D / 65536.0D);
        }

    });
    /**
     * Though it looks like an array, this is really more like a mapping. Key (index of this
     * array) is the upper 5 bits
     * of the result of multiplying a 32-bit unsigned integer by the B(2, 5) De Bruijn sequence
     * 0x077CB531. Value (value
     * stored in the array) is the unique index (from the right) of the leftmo
     */
    private static final int[] MULTIPLY_DE_BRUIJN_BIT_POSITION =
            new int[] {0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23,
                    21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10, 9};
    private static final double ONE_SIXTH = 0.16666666666666666D;
    private static final int FRAC_EXP = 8;
    private static final int LUT_SIZE = 257;
    private static final double FRAC_BIAS = Double.longBitsToDouble(4805340802404319232L);
    private static final double[] ASIN_TAB = new double[257];
    private static final double[] COS_TAB = new double[257];

    static {
        for (int i = 0; i < 257; ++i) {
            final double d0 = (double) i / 256.0D;
            final double d1 = Math.asin(d0);
            COS_TAB[i] = Math.cos(d1);
            ASIN_TAB[i] = d1;
        }

    }

    private static <T> T make(final T pObject, final Consumer<T> pConsumer) {
        pConsumer.accept(pObject);
        return pObject;
    }

    /**
     * sin looked up in a table
     */
    public static float sin(final float pValue) {
        return SIN[(int) (pValue * 10430.378F) & '\uffff'];
    }

    /**
     * cos looked up in the sin table with the appropriate offset
     */
    public static float cos(final float pValue) {
        return SIN[(int) (pValue * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static float sqrt(final float pValue) {
        return (float) Math.sqrt((double) pValue);
    }

    /**
     * {@return the greatest integer less than or equal to the float argument}
     */
    public static int floor(final float pValue) {
        final int i = (int) pValue;
        return pValue < (float) i ? i - 1 : i;
    }

    /**
     * {@return the greatest integer less than or equal to the double argument}
     */
    public static int floor(final double pValue) {
        final int i = (int) pValue;
        return pValue < (double) i ? i - 1 : i;
    }

    /**
     * Long version of floor()
     */
    public static long lfloor(final double pValue) {
        final long i = (long) pValue;
        return pValue < (double) i ? i - 1L : i;
    }

    public static float abs(final float pValue) {
        return Math.abs(pValue);
    }

    /**
     * {@return the unsigned value of an int}
     */
    public static int abs(final int pValue) {
        return Math.abs(pValue);
    }

    public static int ceil(final float pValue) {
        final int i = (int) pValue;
        return pValue > (float) i ? i + 1 : i;
    }

    public static int ceil(final double pValue) {
        final int i = (int) pValue;
        return pValue > (double) i ? i + 1 : i;
    }

    /**
     * {@return the given value if between the lower and the upper bound. If the value is less
     * than the lower bound,
     * returns the lower bound} If the value is greater than the upper bound, returns the upper
     * bound.
     *
     * @param pValue The value that is clamped.
     * @param pMin   The lower bound for the clamp.
     * @param pMax   The upper bound for the clamp.
     */
    public static int clamp(final int pValue, final int pMin, final int pMax) {
        return Math.min(Math.max(pValue, pMin), pMax);
    }

    /**
     * {@return the given value if between the lower and the upper bound. If the value is less
     * than the lower bound,
     * returns the lower bound} If the value is greater than the upper bound, returns the upper
     * bound.
     *
     * @param pValue The value that is clamped.
     * @param pMin   The lower bound for the clamp.
     * @param pMax   The upper bound for the clamp.
     */
    public static float clamp(final float pValue, final float pMin, final float pMax) {
        return pValue < pMin ? pMin : Math.min(pValue, pMax);
    }

    /**
     * {@return the given value if between the lower and the upper bound. If the value is less
     * than the lower bound,
     * returns the lower bound} If the value is greater than the upper bound, returns the upper
     * bound.
     *
     * @param pValue The value that is clamped.
     * @param pMin   The lower bound for the clamp.
     * @param pMax   The upper bound for the clamp.
     */
    public static double clamp(final double pValue, final double pMin, final double pMax) {
        return pValue < pMin ? pMin : Math.min(pValue, pMax);
    }

    /**
     * Method for linear interpolation of doubles.
     *
     * @param pStart Start value for the lerp.
     * @param pEnd   End value for the lerp.
     * @param pDelta A value between 0 and 1 that indicates the percentage of the lerp. (0 will
     *               give the start value and
     *               1 will give the end value) If the value is not between 0 and 1, it is clamped.
     */
    public static double clampedLerp(final double pStart, final double pEnd, final double pDelta) {
        if (pDelta < 0.0D) {
            return pStart;
        } else {
            return pDelta > 1.0D ? pEnd : lerp(pDelta, pStart, pEnd);
        }
    }

    /**
     * Method for linear interpolation of floats.
     *
     * @param pStart Start value for the lerp.
     * @param pEnd   End value for the lerp.
     * @param pDelta A value between 0 and 1 that indicates the percentage of the lerp. (0 will
     *               give the start value and
     *               1 will give the end value) If the value is not between 0 and 1, it is clamped.
     */
    public static float clampedLerp(final float pStart, final float pEnd, final float pDelta) {
        if (pDelta < 0.0F) {
            return pStart;
        } else {
            return pDelta > 1.0F ? pEnd : lerp(pDelta, pStart, pEnd);
        }
    }

    /**
     * {@return the maximum of the absolute value of two numbers}
     */
    public static double absMax(double pX, double pY) {
        if (pX < 0.0D) {
            pX = -pX;
        }

        if (pY < 0.0D) {
            pY = -pY;
        }

        return Math.max(pX, pY);
    }

    public static int floorDiv(final int pDividend, final int pDivisor) {
        return Math.floorDiv(pDividend, pDivisor);
    }

    public static boolean equal(final float pX, final float pY) {
        return Math.abs(pY - pX) < 1.0E-5F;
    }

    public static boolean equal(final double pX, final double pY) {
        return Math.abs(pY - pX) < (double) 1.0E-5F;
    }

    public static int positiveModulo(final int pX, final int pY) {
        return Math.floorMod(pX, pY);
    }

    public static float positiveModulo(final float pNumerator, final float pDenominator) {
        return (pNumerator % pDenominator + pDenominator) % pDenominator;
    }

    public static double positiveModulo(final double pNumerator, final double pDenominator) {
        return (pNumerator % pDenominator + pDenominator) % pDenominator;
    }

    public static boolean isMultipleOf(final int pNumber, final int pMultiple) {
        return pNumber % pMultiple == 0;
    }

    /**
     * Adjust the angle so that its value is in the range [-180;180)
     */
    public static int wrapDegrees(final int pAngle) {
        int i = pAngle % 360;
        if (i >= 180) {
            i -= 360;
        }

        if (i < -180) {
            i += 360;
        }

        return i;
    }

    /**
     * The angle is reduced to an angle between -180 and +180 by mod, and a 360 check.
     */
    public static float wrapDegrees(final float pValue) {
        float f = pValue % 360.0F;
        if (f >= 180.0F) {
            f -= 360.0F;
        }

        if (f < -180.0F) {
            f += 360.0F;
        }

        return f;
    }

    /**
     * The angle is reduced to an angle between -180 and +180 by mod, and a 360 check.
     */
    public static double wrapDegrees(final double pValue) {
        double d0 = pValue % 360.0D;
        if (d0 >= 180.0D) {
            d0 -= 360.0D;
        }

        if (d0 < -180.0D) {
            d0 += 360.0D;
        }

        return d0;
    }

    /**
     * {@return the difference between two angles in degrees}
     */
    public static float degreesDifference(final float pStart, final float pEnd) {
        return wrapDegrees(pEnd - pStart);
    }

    /**
     * {@return the absolute of the difference between two angles in degrees}
     */
    public static float degreesDifferenceAbs(final float pStart, final float pEnd) {
        return abs(degreesDifference(pStart, pEnd));
    }

    /**
     * Takes a rotation and compares it to another rotation.
     * If the difference is greater than a given maximum, clamps the original rotation between to
     * have at most the given
     * difference to the actual rotation.
     * This is used to match the body rotation of entities to their head rotation.
     *
     * @return The new value for the rotation that was adjusted
     */
    public static float rotateIfNecessary(final float pRotationToAdjust,
            final float pActualRotation, final float pMaxDifference) {
        final float f = degreesDifference(pRotationToAdjust, pActualRotation);
        final float f1 = clamp(f, -pMaxDifference, pMaxDifference);
        return pActualRotation - f1;
    }

    /**
     * Changes value by stepSize towards the limit and returns the result.
     * If value is smaller than limit, the result will never be bigger than limit.
     * If value is bigger than limit, the result will never be smaller than limit.
     */
    public static float approach(final float pValue, final float pLimit, float pStepSize) {
        pStepSize = abs(pStepSize);
        return pValue < pLimit ? clamp(pValue + pStepSize, pValue, pLimit)
                : clamp(pValue - pStepSize, pLimit, pValue);
    }

    /**
     * Changes the angle by stepSize towards the limit in the direction where the distance is
     * smaller.
     * {@see #approach(float, float, float)}
     */
    public static float approachDegrees(final float pAngle, final float pLimit,
            final float pStepSize) {
        final float f = degreesDifference(pAngle, pLimit);
        return approach(pAngle, pAngle + f, pStepSize);
    }

    /**
     * {@return the input value rounded up to the next highest power of two}
     */
    public static int smallestEncompassingPowerOfTwo(final int pValue) {
        int i = pValue - 1;
        i |= i >> 1;
        i |= i >> 2;
        i |= i >> 4;
        i |= i >> 8;
        i |= i >> 16;
        return i + 1;
    }

    /**
     * Is the given value a power of two?  (1, 2, 4, 8, 16, ...)
     */
    public static boolean isPowerOfTwo(final int pValue) {
        return pValue != 0 && (pValue & pValue - 1) == 0;
    }

    /**
     * Uses a B(2, 5) De Bruijn sequence and a lookup table to efficiently calculate the
     * log-base-two of the given value.
     * Optimized for cases where the input value is a power-of-two. If the input value is not a
     * power-of-two, then
     * subtract 1 from the return value.
     */
    public static int ceillog2(int pValue) {
        pValue = isPowerOfTwo(pValue) ? pValue : smallestEncompassingPowerOfTwo(pValue);
        return MULTIPLY_DE_BRUIJN_BIT_POSITION[(int) ((long) pValue * 125613361L >> 27) & 31];
    }

    /**
     * Efficiently calculates the floor of the base-2 log of an integer value.  This is
     * effectively the index of the
     * highest bit that is set.  For example, if the number in binary is 0...100101, this will
     * return 5.
     */
    public static int log2(final int pValue) {
        return ceillog2(pValue) - (isPowerOfTwo(pValue) ? 0 : 1);
    }

    public static float frac(final float pNumber) {
        return pNumber - (float) floor(pNumber);
    }

    /**
     * Gets the decimal portion of the given double. For instance, {@code frac(5.5)} returns
     * {@code .5}.
     */
    public static double frac(final double pNumber) {
        return pNumber - (double) lfloor(pNumber);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static long getSeed(final int pX, final int pY, final int pZ) {
        long i = (long) (pX * 3129871) ^ (long) pZ * 116129781L ^ (long) pY;
        i = i * i * 42317861L + i * 11L;
        return i >> 16;
    }

    public static double inverseLerp(final double pDelta, final double pStart, final double pEnd) {
        return (pDelta - pStart) / (pEnd - pStart);
    }

    public static float inverseLerp(final float pDelta, final float pStart, final float pEnd) {
        return (pDelta - pStart) / (pEnd - pStart);
    }

    public static double atan2(double pY, double pX) {
        final double d0 = pX * pX + pY * pY;
        if (Double.isNaN(d0)) {
            return Double.NaN;
        } else {
            final boolean flag = pY < 0.0D;
            if (flag) {
                pY = -pY;
            }

            final boolean flag1 = pX < 0.0D;
            if (flag1) {
                pX = -pX;
            }

            final boolean flag2 = pY > pX;
            if (flag2) {
                final double d1 = pX;
                pX = pY;
                pY = d1;
            }

            final double d9 = fastInvSqrt(d0);
            pX *= d9;
            pY *= d9;
            final double d2 = FRAC_BIAS + pY;
            final int i = (int) Double.doubleToRawLongBits(d2);
            final double d3 = ASIN_TAB[i];
            final double d4 = COS_TAB[i];
            final double d5 = d2 - FRAC_BIAS;
            final double d6 = pY * d4 - pX * d5;
            final double d7 = (6.0D + d6 * d6) * d6 * 0.16666666666666666D;
            double d8 = d3 + d7;
            if (flag2) {
                d8 = (Math.PI / 2D) - d8;
            }

            if (flag1) {
                d8 = Math.PI - d8;
            }

            if (flag) {
                d8 = -d8;
            }

            return d8;
        }
    }

    /**
     * Computes 1/sqrt(n) using
     * <a href="https://en.wikipedia.org/wiki/Fast_inverse_square_root">the fast inverse square
     * root</a> with a constant of 0x5FE6EB50C7B537AA.
     */
    @Deprecated
    public static double fastInvSqrt(double pNumber) {
        final double d0 = 0.5D * pNumber;
        long i = Double.doubleToRawLongBits(pNumber);
        i = 6910469410427058090L - (i >> 1);
        pNumber = Double.longBitsToDouble(i);
        return pNumber * (1.5D - d0 * pNumber * pNumber);
    }

    public static float fastInvCubeRoot(final float pNumber) {
        int i = Float.floatToIntBits(pNumber);
        i = 1419967116 - i / 3;
        float f = Float.intBitsToFloat(i);
        f = 0.6666667F * f + 1.0F / (3.0F * f * f * pNumber);
        return 0.6666667F * f + 1.0F / (3.0F * f * f * pNumber);
    }

    public static int murmurHash3Mixer(int pInput) {
        pInput ^= pInput >>> 16;
        pInput *= -2048144789;
        pInput ^= pInput >>> 13;
        pInput *= -1028477387;
        return pInput ^ pInput >>> 16;
    }

    public static int binarySearch(int pMin, final int pMax,
            final IntPredicate pIsTargetBeforeOrAt) {
        int i = pMax - pMin;

        while (i > 0) {
            final int j = i / 2;
            final int k = pMin + j;
            if (pIsTargetBeforeOrAt.test(k)) {
                i = j;
            } else {
                pMin = k + 1;
                i -= j + 1;
            }
        }

        return pMin;
    }

    public static int lerpInt(final float pDelta, final int pStart, final int pEnd) {
        return pStart + floor(pDelta * (float) (pEnd - pStart));
    }

    /**
     * Method for linear interpolation of floats
     *
     * @param pDelta A value usually between 0 and 1 that indicates the percentage of the lerp.
     *               (0 will give the start
     *               value and 1 will give the end value)
     * @param pStart Start value for the lerp
     * @param pEnd   End value for the lerp
     */
    public static float lerp(final float pDelta, final float pStart, final float pEnd) {
        return pStart + pDelta * (pEnd - pStart);
    }

    /**
     * Method for linear interpolation of doubles
     *
     * @param pDelta A value usually between 0 and 1 that indicates the percentage of the lerp.
     *               (0 will give the start
     *               value and 1 will give the end value)
     * @param pStart Start value for the lerp
     * @param pEnd   End value for the lerp
     */
    public static double lerp(final double pDelta, final double pStart, final double pEnd) {
        return pStart + pDelta * (pEnd - pStart);
    }

    public static double lerp2(final double pDelta1, final double pDelta2, final double pStart1,
            final double pEnd1, final double pStart2, final double pEnd2) {
        return lerp(pDelta2, lerp(pDelta1, pStart1, pEnd1), lerp(pDelta1, pStart2, pEnd2));
    }

    public static double lerp3(final double pDelta1, final double pDelta2, final double pDelta3,
            final double pStart1, final double pEnd1, final double pStart2, final double pEnd2,
            final double pStart3, final double pEnd3, final double pStart4, final double pEnd4) {
        return lerp(pDelta3, lerp2(pDelta1, pDelta2, pStart1, pEnd1, pStart2, pEnd2),
                lerp2(pDelta1, pDelta2, pStart3, pEnd3, pStart4, pEnd4));
    }

    public static float catmullrom(final float pDelta, final float pControlPoint1,
            final float pControlPoint2, final float pControlPoint3, final float pControlPoint4) {
        return 0.5F * (2.0F * pControlPoint2 + (pControlPoint3 - pControlPoint1) * pDelta +
                (2.0F * pControlPoint1 - 5.0F * pControlPoint2 + 4.0F * pControlPoint3
                        - pControlPoint4) * pDelta * pDelta
                + (3.0F * pControlPoint2 - pControlPoint1 - 3.0F * pControlPoint3 + pControlPoint4)
                * pDelta * pDelta * pDelta);
    }

    public static double smoothstep(final double pInput) {
        return pInput * pInput * pInput * (pInput * (pInput * 6.0D - 15.0D) + 10.0D);
    }

    public static double smoothstepDerivative(final double pInput) {
        return 30.0D * pInput * pInput * (pInput - 1.0D) * (pInput - 1.0D);
    }

    public static int sign(final double pX) {
        if (pX == 0.0D) {
            return 0;
        } else {
            return pX > 0.0D ? 1 : -1;
        }
    }

    /**
     * Linearly interpolates an angle between the start between the start and end values given as
     * degrees.
     *
     * @param pDelta A value between 0 and 1 that indicates the percentage of the lerp. (0 will
     *               give the start value and
     *               1 will give the end value)
     */
    public static float rotLerp(final float pDelta, final float pStart, final float pEnd) {
        return pStart + pDelta * wrapDegrees(pEnd - pStart);
    }

    public static float triangleWave(final float pInput, final float pPeriod) {
        return (Math.abs(pInput % pPeriod - pPeriod * 0.5F) - pPeriod * 0.25F) / (pPeriod * 0.25F);
    }

    public static float square(final float pValue) {
        return pValue * pValue;
    }

    public static double square(final double pValue) {
        return pValue * pValue;
    }

    public static int square(final int pValue) {
        return pValue * pValue;
    }

    public static long square(final long pValue) {
        return pValue * pValue;
    }

    public static double clampedMap(final double pInput, final double pInputMin,
            final double pInputMax, final double pOuputMin, final double pOutputMax) {
        return clampedLerp(pOuputMin, pOutputMax, inverseLerp(pInput, pInputMin, pInputMax));
    }

    public static float clampedMap(final float pInput, final float pInputMin, final float pInputMax,
            final float pOutputMin, final float pOutputMax) {
        return clampedLerp(pOutputMin, pOutputMax, inverseLerp(pInput, pInputMin, pInputMax));
    }

    public static double map(final double pInput, final double pInputMin, final double pInputMax,
            final double pOutputMin, final double pOutputMax) {
        return lerp(inverseLerp(pInput, pInputMin, pInputMax), pOutputMin, pOutputMax);
    }

    public static float map(final float pInput, final float pInputMin, final float pInputMax,
            final float pOutputMin, final float pOutputMax) {
        return lerp(inverseLerp(pInput, pInputMin, pInputMax), pOutputMin, pOutputMax);
    }

    /**
     * Rounds the given value up to a multiple of factor.
     *
     * @return The smallest integer multiple of factor that is greater than or equal to the value
     */
    public static int roundToward(final int pValue, final int pFactor) {
        return positiveCeilDiv(pValue, pFactor) * pFactor;
    }

    /**
     * Returns the smallest (closest to negative infinity) int value that is greater than or
     * equal to the algebraic
     * quotient.
     *
     * @see java.lang.Math#floorDiv(int, int)
     */
    public static int positiveCeilDiv(final int pX, final int pY) {
        return -Math.floorDiv(-pX, pY);
    }

    public static double lengthSquared(final double pXDistance, final double pYDistance) {
        return pXDistance * pXDistance + pYDistance * pYDistance;
    }

    public static double length(final double pXDistance, final double pYDistance) {
        return Math.sqrt(lengthSquared(pXDistance, pYDistance));
    }

    public static double lengthSquared(final double pXDistance, final double pYDistance,
            final double pZDistance) {
        return pXDistance * pXDistance + pYDistance * pYDistance + pZDistance * pZDistance;
    }

    public static double length(final double pXDistance, final double pYDistance,
            final double pZDistance) {
        return Math.sqrt(lengthSquared(pXDistance, pYDistance, pZDistance));
    }

    /**
     * Gets the value closest to zero that is not closer to zero than the given value and is a
     * multiple of the factor.
     */
    public static int quantize(final double pValue, final int pFactor) {
        return floor(pValue / (double) pFactor) * pFactor;
    }
}
