/*******************************************************************************
 * Copyright 2013 Sebastian Niemann <niemann@sra.uni-hannover.de> and contributors.
 * 
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 *******************************************************************************/

package arma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.ejml.alg.dense.linsol.svd.SolvePseudoInverseSvd;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.CholeskyDecomposition;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.EigenDecomposition;
import org.ejml.factory.LUDecomposition;
import org.ejml.factory.LinearSolverFactory;
import org.ejml.factory.QRDecomposition;
import org.ejml.factory.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.MatrixFeatures;
import org.ejml.ops.NormOps;

/**
 * Provides interfaces to non-member functions that are similar to the Armadillo C++ Algebra Library (Armadillo) by
 * Conrad Sanderson et al., based on DenseMatrix64F from Peter Abeles' Efficient Java Matrix Library (EJML) Version 0.23
 * from 21.06.2013.
 * <p>
 * If not stated otherwise (marked as non-canonical), the provided interfaces should be identical to Armadillo (e.g.
 * same ordering of arguments, accepted values, ...). However, this project is based on EJML to provide a pure Java
 * solution, which is why numeric results may slightly differ from the Armadillo C++ Algebra Library.
 * 
 * @author Sebastian Niemann <niemann@sra.uni-hannover.de>
 * 
 * @see <a href="http://arma.sourceforge.net/">Armadillo C++ Algebra Library</a>
 * @see <a href="http://efficient-java-matrix-library.googlecode.com">Efficient Java Matrix Library</a>
 */
public class Arma {
  /**
   * Returns a zero column vector with {@code numberOfElements} elements.
   * 
   * @param numberOfElements The number of elements
   * @return The vector
   */
  public static Mat zeros(int numberOfElements) {
    return zeros(numberOfElements, 1);
  }

  /**
   * Creates a zero matrix with {@code numberOfRows} rows and {@code numberOfColumns} columns.
   * 
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @return The matrix
   */
  public static Mat zeros(int numberOfRows, int numberOfColumns) {
    return new Mat(numberOfRows, numberOfColumns, Fill.ZEROS);
  }

  /**
   * Returns a column vector of ones with {@code numberOfElements} elements.
   * 
   * @param numberOfElements The number of elements
   * @return The vector
   */
  public static Mat ones(int numberOfElements) {
    return ones(numberOfElements, 1);
  }

  /**
   * Creates a matrix of ones with {@code numberOfRows} rows and {@code numberOfColumns} columns.
   * 
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @return The matrix
   */
  public static Mat ones(int numberOfRows, int numberOfColumns) {
    return new Mat(numberOfRows, numberOfColumns, Fill.ONES);
  }

  /**
   * Creates a identity of ones with {@code numberOfRows} rows and {@code numberOfColumns} columns.
   * 
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @return The matrix
   */
  public static Mat eye(int numberOfRows, int numberOfColumns) {
    return new Mat(numberOfRows, numberOfColumns, Fill.EYE);
  }

  /**
   * Creates a column vector with {@code numberOfRows} rows and {@code numberOfColumns} columns and values drawn from
   * the standard uniform distribution on the left-closed and right-open interval [0,1).
   * <p>
   * <b>Non-canonical:</b> Drawn from [0,1) instead of the closed interval [0,1].
   * 
   * @param numberOfElements The number of elements
   * @param rng The pseudorandom generator
   * @return The vector
   */
  public static Mat randu(int numberOfElements, Random rng) {
    return randu(numberOfElements, 1, rng);
  }

  /**
   * Creates a matrix with {@code numberOfRows} rows and {@code numberOfColumns} columns and values drawn from the
   * standard uniform distribution on the left-closed and right-open interval [0,1).
   * <p>
   * <b>Non-canonical:</b> Drawn from [0,1) instead of the closed interval [0,1].
   * 
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @param rng The pseudorandom generator
   * @return The matrix
   */
  public static Mat randu(int numberOfRows, int numberOfColumns, Random rng) {
    return new Mat(numberOfRows, numberOfColumns, Fill.RANDU, rng);
  }

  /**
   * Creates a column vector with {@code numberOfRows} rows and {@code numberOfColumns} columns and values drawn from
   * the standard normal distribution with mean 0.0 and standard deviation 1.0.
   * 
   * @param numberOfElements The number of elements
   * @param rng The pseudorandom generator
   * @return The vector
   */
  public static Mat randn(int numberOfElements, Random rng) {
    return randn(numberOfElements, 1, rng);
  }

  /**
   * Creates a matrix with {@code numberOfRows} rows and {@code numberOfColumns} columns and values drawn from the
   * standard normal distribution with mean 0.0 and standard deviation 1.0.
   * 
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @param rng The pseudorandom generator
   * @return The matrix
   */
  public static Mat randn(int numberOfRows, int numberOfColumns, Random rng) {
    return new Mat(numberOfRows, numberOfColumns, Fill.RANDN, rng);
  }

  /**
   * Creates a matrix by copying the matrix {@code numberOfCopiesPerRow} times per row and
   * {@code numberOfCopiesPerColumn} per column.
   * 
   * @param matrix The matrix
   * @param numberOfCopiesPerRow The number of copies per row
   * @param numberOfCopiesPerColumn The number of copies per column
   * @return The matrix
   */
  public static Mat repmat(AbstractMat matrix, int numberOfCopiesPerRow, int numberOfCopiesPerColumn) {
    AbstractMat.isInvalidPositionDetection(numberOfCopiesPerRow);
    AbstractMat.isInvalidPositionDetection(numberOfCopiesPerColumn);

    // This method uses System.arraycopy instead of iterating over each element
    if (!(matrix instanceof Mat)) {
      matrix = new Mat(matrix);
    }

    Mat result = new Mat(matrix.n_rows * numberOfCopiesPerRow, matrix.n_cols * numberOfCopiesPerColumn);

    int srcColumnPointer = 0;
    for (int j = 0; j < matrix.n_cols; j++) {
      for (int copyJ = 0; copyJ < numberOfCopiesPerColumn; copyJ++) {
        int destColumnPointer = (j + copyJ * matrix.n_cols) * result.n_rows;
        for (int copyI = 0; copyI < numberOfCopiesPerRow; copyI++) {
          System.arraycopy(matrix._matrix, srcColumnPointer, result._matrix, destColumnPointer, matrix.n_rows);
          destColumnPointer += matrix.n_rows;
        }
      }
      srcColumnPointer += matrix.n_rows;
    }

    return result;
  }

  /**
   * Creates a toeplitz matrix with {@code The} as the first column of the matrix.
   * 
   * @param vector The vector
   * @return The matrix
   */
  public static Mat toeplitz(AbstractMat vector) {
    vector.isNonVectorDetection();

    if (vector.n_elem > 0) {
      Mat result = new Mat(vector.n_elem, vector.n_elem);

      vector.iteratorReset();
      result.diag(Op.EQUAL, vector._matrix[vector.iteratorNext()]);
      for (int n = 1; n < vector.n_elem; n++) {
        double element = vector._matrix[vector.iteratorNext()];
        result.diag(n, Op.EQUAL, element);
        result.diag(-n, Op.EQUAL, element);
      }

      return result;
    } else {
      return new Mat();
    }
  }

  /**
   * Creates a toeplitz matrix with {@code vector1} as the first column and {@code vector2} the first row of the matrix.
   * 
   * @param vector1 The first vector
   * @param vector2 The second vector
   * @return The matrix
   */
  public static Mat toeplitz(AbstractMat vector1, AbstractMat vector2) {
    vector1.isNonVectorDetection();
    vector2.isNonVectorDetection();

    if (vector1.n_elem > 0 && vector2.n_elem > 0) {
      Mat result = new Mat(vector1.n_elem, vector2.n_elem);

      vector1.iteratorReset();
      result.diag(Op.EQUAL, vector1._matrix[vector1.iteratorNext()]);
      for (int n = 1; n < vector1.n_elem; n++) {
        result.diag(-n, Op.EQUAL, vector1._matrix[vector1.iteratorNext()]);
      }

      vector2.iteratorReset();
      vector2.iteratorNext(); // Skip this one
      for (int n = 1; n < vector2.n_elem; n++) {
        result.diag(n, Op.EQUAL, vector2._matrix[vector2.iteratorNext()]);
      }

      return result;
    } else {
      return new Mat();
    }
  }

  /**
   * Creates a circulant toeplitz matrix with {@code The} as the first column of the matrix.
   * 
   * @param vector The vector
   * @return The matrix
   */
  public static Mat circ_toeplitz(AbstractMat vector) {
    vector.isNonVectorDetection();

    if (vector.n_elem > 0) {
      Mat result = new Mat(vector.n_elem, vector.n_elem);

      vector.iteratorReset();
      result.diag(Op.EQUAL, vector._matrix[vector.iteratorNext()]);
      for (int n = 1; n < vector.n_elem; n++) {
        double element = vector._matrix[vector.iteratorNext()];
        result.diag(vector.n_elem - n, Op.EQUAL, element);
        result.diag(-n, Op.EQUAL, element);
      }

      return result;
    } else {
      return new Mat();
    }
  }

  /**
   * Creates a column vector with 100 elements linear increasing from value {@code a} to {@code b}.
   * 
   * @param startValue The start value
   * @param endValue The end value
   * @return The matrix
   */
  public static Mat linspace(double startValue, double endValue) {
    return linspace(startValue, endValue, 100);
  }

  /**
   * Creates a column vector with {@code numberOfElements} elements linear increasing from value {@code a} to {@code b}.
   * 
   * @param startValue The start value
   * @param endValue The end value
   * @param numberOfElements The number of elements
   * @return The matrix
   */
  public static Mat linspace(double startValue, double endValue, int numberOfElements) {
    AbstractMat.isInvalidPositionDetection(numberOfElements);

    Mat result = new Mat(numberOfElements, 1);

    if (result.n_elem > 1) {
      double stepLength = (endValue - startValue) / (result.n_elem - 1);
      for (int n = 0; n < result.n_elem; n++) {
        // Increasing a value step by step by stepLength might be faster, but also reduces precision
        result._matrix[n] = startValue + stepLength * n;
      }
    } else {
      result._matrix[0] = endValue;
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed sine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat sin(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.sin(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed inverse sine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat asin(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.asin(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed hyperbolic sine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat sinh(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.sinh(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed inverse hyperbolic sine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat asinh(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      double element = matrix._matrix[matrix.iteratorNext()];

      if (Double.isInfinite(element)) {
        result._matrix[n] = element;
      } else {
        result._matrix[n] = Math.log(element + Math.sqrt(Math.pow(element, 2) + 1));
      }
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed cosine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat cos(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.cos(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed inverse cosine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix.
   */
  public static Mat acos(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.acos(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed hyperbolic cosine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat cosh(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.cosh(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed inverse hyperbolic cosine of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat acosh(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      double element = matrix._matrix[matrix.iteratorNext()];

      if (element < 1) {
        result._matrix[n] = Double.NaN;
      } else if (Double.isInfinite(element)) {
        result._matrix[n] = Double.NaN;
      } else {
        result._matrix[n] = Math.log(element + Math.sqrt(Math.pow(element, 2) - 1));
      }
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed tangent of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat tan(AbstractMat matrix) {
    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.tan(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed inverse tangent of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat atan(AbstractMat matrix) {
    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.atan(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed hyperbolic tangent of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat tanh(AbstractMat matrix) {
    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.tanh(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed inverse hyperbolic tangent of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat atanh(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      double element = matrix._matrix[matrix.iteratorNext()];
      result._matrix[n] = 0.5 * Math.log((1 + element) / (1 - element));
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed absolute values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat abs(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.abs(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise determined positive distances between each value and next larger one in
   * magnitude of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat eps(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.ulp(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-e exponential values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat exp(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.exp(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-2 exponential values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat exp2(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.pow(2, matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-10 exponential values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat exp10(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.pow(10, matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-e exponential values of the matrix.
   * <p>
   * Values that would result in infinity are truncated to the largest representable number.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat trunc_exp(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      double exp = Math.exp(matrix._matrix[matrix.iteratorNext()]);
      if (!Double.isInfinite(exp)) {
        result._matrix[n] = exp;
      } else {
        result._matrix[n] = Double.MAX_VALUE;
      }
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-e logarithmic values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat log(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.log(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-2 logarithmic values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat log2(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.log(matrix._matrix[matrix.iteratorNext()]) / Math.log(2);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-10 logarithmic values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat log10(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.log10(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed base-e logarithmic values of the matrix.
   * <p>
   * Values that would result in infinity are truncated to the base-e logarithmic of the largest representable number
   * and values that would result in a complex number are truncated to the base-e logarithmic of the smallest
   * representable number.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat trunc_log(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      double value = matrix._matrix[matrix.iteratorNext()];

      if (value <= 0) {
        value = Double.MIN_NORMAL;
      } else if (Double.isInfinite(value)) {
        value = Double.MAX_VALUE;
      }

      result._matrix[n] = Math.log(value);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed raise to power of {@code power} of the matrix.
   * 
   * @param matrix The matrix
   * @param power The power
   * @return The matrix
   */
  public static Mat pow(AbstractMat matrix, double power) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.pow(matrix._matrix[matrix.iteratorNext()], power);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise computed square root of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat sqrt(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.sqrt(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise squared values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat square(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.pow(matrix._matrix[matrix.iteratorNext()], 2);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise floored values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat floor(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.floor(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise rounded up values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat ceil(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.ceil(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise rounded values of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat round(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      double element = matrix._matrix[matrix.iteratorNext()];

      if (Double.isInfinite(element)) {
        result._matrix[n] = element;
      } else {
        result._matrix[n] = Math.round(element);
      }
    }

    return result;
  }

  /**
   * Creates a matrix with element-wise determined sign values of the matrix.
   * <p>
   * The sign value is -1 for negative, 0 for zero and 1 for non-negative values.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat sign(AbstractMat matrix) {
    matrix.isEmptyDetection();

    Mat result = new Mat(matrix.n_rows, matrix.n_cols);

    matrix.iteratorReset();
    for (int n = 0; n < matrix.n_elem; n++) {
      result._matrix[n] = Math.signum(matrix._matrix[matrix.iteratorNext()]);
    }

    return result;
  }

  /**
   * Returns the sum of all elements of the matrix.
   * 
   * @param matrix The matrix
   * @return The sum
   */
  public static double accu(AbstractMat matrix) {
    matrix.isEmptyDetection();

    matrix.iteratorReset();
    double sum = 0;
    while (matrix.iteratorHasNext()) {
      sum += matrix._matrix[matrix.iteratorNext()];
    }

    return sum;
  }

  /**
   * Returns the dot product between two vectors.
   * 
   * @param vector1 The first vector
   * @param vector2 The second vector
   * @return The dot product
   */
  public static double dot(AbstractMat vector1, AbstractMat vector2) {
    vector1.isNonVectorDetection();
    vector2.isNonVectorDetection();
    vector1.isEmptyDetection();
    AbstractMat.isNonEqualNumberOfElementsDetection(vector1.n_elem, vector2.n_elem);

    double dotProduct = 0;
    if (vector1.equals(vector2)) {
      vector1.iteratorReset();
      dotProduct = 0;
      while (vector1.iteratorHasNext()) {
        dotProduct += Math.pow(vector1._matrix[vector1.iteratorNext()], 2);
      }
    } else {
      vector1.iteratorReset();
      vector2.iteratorReset();
      dotProduct = 0;
      while (vector1.iteratorHasNext()) {
        dotProduct += vector1._matrix[vector1.iteratorNext()] * vector2._matrix[vector2.iteratorNext()];
      }
    }

    return dotProduct;
  }

  /**
   * Returns the normalised dot product between two vectors.
   * 
   * @param vector1 The first vector
   * @param vector2 The second vector
   * @return The dot product
   */
  public static double norm_dot(AbstractMat vector1, AbstractMat vector2) {
    return dot(vector1, vector2) / Math.sqrt(dot(vector1, vector1) * dot(vector2, vector2));
  }

  /**
   * Returns the determinant of the matrix.
   * 
   * @param matrix The matrix
   * @return The determinant
   */
  public static double det(AbstractMat matrix) {
    matrix.isNotSquareDetection();
    matrix.isEmptyDetection();
    matrix.isIllConditionedDectetion();

    return CommonOps.det(AbstractMat.convertMatToEJMLMat(matrix));
  }

  /**
   * Sets {@code value} to the absolute value of the base-e logarithmic determinant and {@sign} to the sign of the
   * determinant of the matrix.
   * <p>
   * Note: The value must be of the mutable type double[] and the sign of the mutable type int[].
   * 
   * @param value The value
   * @param sign The sign
   * @param matrix The matrix
   */
  public static void log_det(double[] value, int[] sign, AbstractMat matrix) {
    double determinant = det(matrix);

    value[0] = Math.log(Math.abs(determinant));
    sign[0] = (int) Math.signum(determinant);
  }

  /**
   * Computes the {@code p}-norm of the matrix.
   * <p>
   * If {@code matrix} is not a vector, an induced norm is computed.
   * 
   * @param matrix The matrix
   * @param p The type
   * @return The norm
   * 
   * @throws IllegalArgumentException For vectors, p must be strict greater than 0, but was {@code p}.
   */
  public static double norm(AbstractMat matrix, int p) throws IllegalArgumentException {
    matrix.isEmptyDetection();
    matrix.isIllConditionedDectetion();

    if (matrix.is_vec()) {
      if (p < 0) {
        throw new IllegalArgumentException("For vectors, p must be strict greater than 0, but was " + p);
      }
    } else {
      if (p != 1 && p != 2) {
        throw new IllegalArgumentException("For non-vector matrices, p must be one of 1 or 2, but was " + p);
      }
    }

    return NormOps.normP(AbstractMat.convertMatToEJMLMat(matrix), p);
  }

  /**
   * Computes the {@code p}-norm of the matrix.
   * <p>
   * If {@code matrix} is not a vector, an induced norm is computed.
   * <p>
   * {@code -inf} stands for the minimum norm, {@code inf} for the maximum norm and {@code fro} for the Frobenius norm.
   * 
   * @param matrix The matrix
   * @param p The type
   * @return The norm
   * 
   * @throws IllegalArgumentException For vectors, p must be one of '-inf', 'inf' or 'fro', but was {@code p}.
   * @throws IllegalArgumentException For non-vector matrices, p must be one of 'inf' or 'fro', but was {@code p}.
   */
  public static double norm(AbstractMat matrix, String p) throws IllegalArgumentException {
    matrix.isEmptyDetection();
    matrix.isIllConditionedDectetion();

    if (matrix.is_vec()) {
      switch (p) {
        case "-inf":
          return CommonOps.elementMinAbs(AbstractMat.convertMatToEJMLMat(matrix));
        case "inf":
          return CommonOps.elementMaxAbs(AbstractMat.convertMatToEJMLMat(matrix));
        case "fro":
          return NormOps.normF(AbstractMat.convertMatToEJMLMat(matrix));
        default:
          throw new IllegalArgumentException("For vectors, p must be one of '-inf', 'inf' or 'fro', but was " + p);
      }
    } else {
      switch (p) {
        case "inf":
          return NormOps.inducedPInf(AbstractMat.convertMatToEJMLMat(matrix));
        case "fro":
          return NormOps.normF(AbstractMat.convertMatToEJMLMat(matrix));
        default:
          throw new IllegalArgumentException("For non-vector matrices, p must be one of 'inf' or 'fro', but was " + p);
      }
    }
  }

  /**
   * Returns the rank of the matrix.
   * <p>
   * All values smaller than {@code max(size(A))*eps(norm(A))} are treated as zero.
   * 
   * @param matrix The matrix
   * @return The rank
   */
  public static double rank(AbstractMat matrix) {
    return rank(matrix, Math.max(matrix.n_rows, matrix.n_cols) * Math.ulp(norm(matrix, 2)));
  }

  /**
   * Returns the rank of the matrix.
   * <p>
   * All values smaller than {@code tolerance} are treated as zero.
   * 
   * @param matrix The matrix
   * @param tolerance The tolerance
   * @return The rank
   */
  public static double rank(AbstractMat matrix, double tolerance) {
    matrix.isEmptyDetection();
    matrix.isIllConditionedDectetion();

    return MatrixFeatures.rank(AbstractMat.convertMatToEJMLMat(matrix), tolerance);
  }

  /**
   * Returns the trace of the matrix.
   * 
   * @param matrix The matrix
   * @return The trace
   */
  public static double trace(AbstractMat matrix) {
    matrix.isEmptyDetection();
    matrix.isNotSquareDetection();

    DiagMat diagonal = matrix.diagInternal(0);
    diagonal.iteratorReset();
    double trace = matrix._matrix[diagonal.iteratorNext()];
    while (diagonal.iteratorHasNext()) {
      trace += matrix._matrix[diagonal.iteratorNext()];
    }

    return trace;
  }

  /**
   * Converts a (1,1)-matrix into a scalar.
   * 
   * @param matrix The matrix
   * @return The scalar
   * 
   * @throws IllegalArgumentException The matrices must be a (1,1)-matrix, but was a ({@link Mat#n_rows matrix.n_rows},
   *           {@link Mat#n_cols matrix.n_cols})-matrix.
   */
  public static double as_scalar(AbstractMat matrix) throws IllegalArgumentException {
    if (matrix.n_rows != 1 || matrix.n_cols != 1) {
      throw new IllegalArgumentException("The matrices must be a (1,1)-matrix, but was a (" + matrix.n_rows + ", " + matrix.n_cols + ")-matrix.");
    }

    return matrix._matrix[matrix.getElementIndex(0)];
  }

  /**
   * Returns the smallest value of the vector.
   * 
   * @param vector The vector
   * @return The minimum
   */
  public static double min(AbstractMat vector) {
    vector.isEmptyDetection();
    vector.isNonVectorDetection();

    vector.iteratorReset();
    double minimum = vector._matrix[vector.iteratorNext()];
    while (vector.iteratorHasNext()) {
      minimum = Math.min(minimum, vector._matrix[vector.iteratorNext()]);
    }

    return minimum;
  }

  /**
   * Returns the smallest value for each column of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   * 
   * @throws UnsupportedOperationException The matrix must have at least one element.
   */
  public static Mat minMat(AbstractMat matrix) {
    return minMat(matrix, 0);
  }

  /**
   * Returns the smallest value for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the matrix.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat minMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;

    if (dimension == 0) {
      result = new Mat(1, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = min(matrix.colInternal(j));
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = min(matrix.rowInternal(i));
      }
    }

    return result;
  }

  /**
   * Returns the largest value of the vector.
   * 
   * @param vector The vector
   * @return The maximum
   */
  public static double max(AbstractMat vector) {
    vector.isEmptyDetection();
    vector.isNonVectorDetection();

    vector.iteratorReset();
    double maximum = vector._matrix[vector.iteratorNext()];
    while (vector.iteratorHasNext()) {
      maximum = Math.max(maximum, vector._matrix[vector.iteratorNext()]);
    }

    return maximum;
  }

  /**
   * Returns the largest value for each column of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat maxMat(AbstractMat matrix) {
    return maxMat(matrix, 0);
  }

  /**
   * Returns the largest value for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the matrix.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat maxMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;

    if (dimension == 0) {
      result = new Mat(1, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = max(matrix.colInternal(j));
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = max(matrix.rowInternal(i));
      }
    }

    return result;
  }

  /**
   * Returns the mean of the vector.
   * 
   * @param vector The vector
   * @return The mean
   */
  public static double mean(AbstractMat vector) {
    vector.isEmptyDetection();
    vector.isNonVectorDetection();

    return accu(vector) / vector.n_elem;
  }

  /**
   * Returns the mean for each column of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat meanMat(AbstractMat matrix) {
    return meanMat(matrix, 0);
  }

  /**
   * Returns the mean for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the matrix.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat meanMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result = sumMat(matrix, dimension);

    int count;
    if (dimension == 0) {
      count = matrix.n_rows;
    } else {
      count = matrix.n_cols;
    }

    for (int n = 0; n < result.n_elem; n++) {
      result._matrix[n] /= count;
    }

    return result;
  }

  /**
   * Returns the median of the vector.
   * 
   * @param vector The vector
   * @return The median
   */
  public static double median(AbstractMat vector) {
    vector.isEmptyDetection();
    vector.isNonVectorDetection();

    Mat sort = sort(vector);

    double median;
    if (vector.n_elem % 2 == 0) {
      int middle = vector.n_elem / 2;
      median = (sort._matrix[sort.getElementIndex(middle - 1)] + sort._matrix[sort.getElementIndex(middle)]) / 2;
    } else {
      median = sort._matrix[sort.getElementIndex(vector.n_elem / 2)];
    }

    return median;
  }

  /**
   * Returns the median for each column of the matrix.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat medianMat(AbstractMat matrix) {
    return medianMat(matrix, 0);
  }

  /**
   * Returns the median for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the matrix.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat medianMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(1, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = median(matrix.colInternal(j));
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = median(matrix.rowInternal(i));
      }
    }

    return result;
  }

  /**
   * Returns the standard deviation of the vector with normalisation by {@code count} - 1.
   * 
   * @param vector The vector
   * @return The standard deviation
   */
  public static double stddev(AbstractMat vector) {
    return stddev(vector, 0);
  }

  /**
   * Returns the standard deviation of the vector.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param vector The vector
   * @param normType The normalisation
   * @return The standard deviation
   */
  public static double stddev(AbstractMat vector, int normType) {
    return Math.sqrt(var(vector, normType));
  }

  /**
   * Returns the standard deviation for each column of the matrix with normalisation by {@code count} - 1.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat stddevMat(AbstractMat matrix) {
    return stddevMat(matrix, 0, 0);
  }

  /**
   * Returns the standard deviation for each column of the matrix.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param matrix The matrix
   * @param normType The normalisation
   * @return The matrix
   */
  public static Mat stddevMat(AbstractMat matrix, int normType) {
    return stddevMat(matrix, normType, 0);
  }

  /**
   * Returns the standard deviation for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the
   * matrix.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param matrix The matrix
   * @param normType The normalisation
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat stddevMat(AbstractMat matrix, int normType, int dimension) {
    Mat result = varMat(matrix, normType, dimension);

    for (int n = 0; n < result.n_elem; n++) {
      result._matrix[n] = Math.sqrt(result._matrix[n]);
    }

    return result;
  }

  /**
   * Returns the variance of the vector with normalisation by {@code count} - 1.
   * 
   * @param vector The vector
   * @return The variance
   */
  public static double var(AbstractMat vector) {
    return var(vector, 0);
  }

  /**
   * Returns the variance of the vector.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param vector The vector
   * @param normType The normalisation
   * @return The variance
   */
  public static double var(AbstractMat vector, int normType) {
    vector.isEmptyDetection();
    vector.isNonVectorDetection();
    AbstractMat.isNonBinaryParameterDetection(normType);

    double variance = sum(square(vector.minus(mean(vector))));
    if (normType == 0) {
      variance /= (vector.n_elem - 1);
    } else {
      variance /= vector.n_elem;
    }

    return variance;
  }

  /**
   * Returns the variance for each column of the matrix with normalisation by {@code count} - 1.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat varMat(AbstractMat matrix) {
    return varMat(matrix, 0, 0);
  }

  /**
   * Returns the variance for each column of the matrix.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param matrix The matrix
   * @param normType The normalisation
   * @return The matrix
   */
  public static Mat varMat(AbstractMat matrix, int normType) {
    return varMat(matrix, normType, 0);
  }

  /**
   * Returns the variance for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the matrix.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param matrix The matrix
   * @param normType The normalisation
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat varMat(AbstractMat matrix, int normType, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(1, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = var(matrix.colInternal(j), normType);
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = var(matrix.rowInternal(i), normType);
      }
    }

    return result;
  }

  /**
   * Returns true if any element of the vector is non-zero.
   * 
   * @param vector The vector
   * @return Whether any element is non-zero
   */
  public static boolean any(AbstractMat vector) {
    vector.isNonVectorDetection();

    vector.iteratorReset();
    while (vector.iteratorHasNext()) {
      if (vector._matrix[vector.iteratorNext()] != 0) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns true if any element of the matrix is non-zero.
   * 
   * @param matrix The matrix
   * @return Whether any element is non-zero
   */
  public static Mat anyMat(AbstractMat matrix) {
    return anyMat(matrix, 0);
  }

  /**
   * Returns for each column ({@code dimension} = 0) or row ({@code dimension} = 1) true if any element is non-zero.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat anyMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(matrix.n_cols, 1);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = any(matrix.colInternal(j)) ? 1 : 0;
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = any(matrix.rowInternal(i)) ? 1 : 0;
      }
    }

    return result;
  }

  /**
   * Returns true if all element of the vector are non-zero.
   * 
   * @param vector The vector
   * @return Whether any element is non-zero
   */
  public static boolean all(AbstractMat vector) {
    vector.isNonVectorDetection();

    if (vector.n_elem < 1) {
      return false;
    }

    vector.iteratorReset();
    while (vector.iteratorHasNext()) {
      if (vector._matrix[vector.iteratorNext()] == 0) {
        return false;
      }
    }

    return true;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat allMat(AbstractMat matrix) {
    return allMat(matrix, 0);
  }

  /**
   * Returns for each column ({@code dimension} = 0) or row ({@code dimension} = 1) true if all element are non-zero.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat allMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(matrix.n_cols, 1);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = all(matrix.colInternal(j)) ? 1 : 0;
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = all(matrix.rowInternal(i)) ? 1 : 0;
      }
    }

    return result;
  }

  /**
   * Returns the product of the vector.
   * 
   * @param vector The vector
   * @return The product
   */
  public static double prod(AbstractMat vector) {
    vector.isEmptyDetection();
    vector.isNonVectorDetection();

    vector.iteratorReset();
    double product = 1;
    while (vector.iteratorHasNext()) {
      product *= vector._matrix[vector.iteratorNext()];
    }

    return product;
  }

  /**
   * Returns the product of all elements for each column of the matrix as a column vector.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat prodMat(AbstractMat matrix) {
    return prodMat(matrix, 0);
  }

  /**
   * Returns the product of all elements for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the
   * matrix as a column vector.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat prodMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(1, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = prod(matrix.colInternal(j));
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = prod(matrix.rowInternal(i));
      }
    }

    return result;
  }

  /**
   * Returns the sum of all elements for each column of the matrix as a column vector.
   * 
   * @param vector The vector
   * @return The sum
   */
  public static double sum(AbstractMat vector) {
    return accu(vector);
  }

  /**
   * Returns the sum of all elements for each column of the matrix as a column vector.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat sumMat(AbstractMat matrix) {
    return sumMat(matrix, 0);
  }

  /**
   * Returns the sum of all elements for each column ({@code dimension} = 0) or row ({@code dimension} = 1) of the
   * matrix as a column vector.
   * 
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat sumMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(1, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result._matrix[j] = sum(matrix.colInternal(j));
      }
    } else {
      result = new Mat(matrix.n_rows, 1);
      for (int i = 0; i < matrix.n_rows; i++) {
        result._matrix[i] = sum(matrix.rowInternal(i));
      }
    }

    return result;
  }

  /**
   * Concatenation of the matrices {@code matrix1} and {@code matrix2} along their columns.
   * <p>
   * Matrix {@code matrix1} will be placed above and {@code matrix2} bellow.
   * 
   * @param matrix1 First matrix
   * @param matrix2 Second matrix
   * @return The matrix
   */
  public static Mat join_cols(AbstractMat matrix1, AbstractMat matrix2) {
    AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_cols, matrix2.n_cols);

    Mat result = new Mat(matrix1.n_rows + matrix2.n_rows, matrix1.n_cols);
    result.submat(0, matrix1.n_rows - 1, 0, result.n_cols - 1, Op.EQUAL, matrix1);
    result.submat(matrix1.n_rows, result.n_rows - 1, 0, result.n_cols - 1, Op.EQUAL, matrix2);

    return result;
  }

  /**
   * Concatenation of the matrices {@code matrix1} and {@code matrix2} along their columns.
   * <p>
   * Matrix {@code matrix1} will be placed above and {@code matrix2} bellow.
   * 
   * @param matrix1 First matrix
   * @param matrix2 Second matrix
   * @return The matrix
   */
  public static Mat join_vert(AbstractMat matrix1, AbstractMat matrix2) {
    return join_cols(matrix1, matrix2);
  }

  /**
   * Concatenation of the matrices {@code matrix1} and {@code matrix2} along their rows.
   * <p>
   * Matrix {@code matrix1} will be placed left and {@code matrix2} right.
   * 
   * @param matrix1 First matrix
   * @param matrix2 Second matrix
   * @return The matrix
   */
  public static Mat join_rows(AbstractMat matrix1, AbstractMat matrix2) {
    AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_rows, matrix2.n_rows);

    Mat result = new Mat(matrix1.n_rows, matrix1.n_cols + matrix2.n_cols);
    result.submat(0, result.n_rows - 1, 0, matrix1.n_cols - 1, Op.EQUAL, matrix1);
    result.submat(0, result.n_rows - 1, matrix1.n_cols, result.n_cols - 1, Op.EQUAL, matrix2);

    return result;
  }

  /**
   * Concatenation of the matrices {@code matrix1} and {@code matrix2} along their rows.
   * <p>
   * Matrix {@code matrix1} will be placed left and {@code matrix2} right.
   * 
   * @param matrix1 First matrix
   * @param matrix2 Second matrix
   * @return The matrix
   */
  public static Mat join_horiz(AbstractMat matrix1, AbstractMat matrix2) {
    return join_rows(matrix1, matrix2);
  }

  /**
   * Sorts the vector in ascending order.
   * 
   * @param vector The vector
   * @return The vector
   */
  public static Mat sort(AbstractMat vector) {
    return sort(vector, 0);
  }

  /**
   * Sorts the vector either in ascending ({@code sortType} = 0) or descending ({@code sortType} = 1) order.
   * 
   * @param vector The vector
   * @param sortType The sortType
   * @return The vector
   */
  public static Mat sort(AbstractMat vector, int sortType) {
    vector.isNonVectorDetection();
    AbstractMat.isNonBinaryParameterDetection(sortType);

    Mat result = new Mat(vector);
    Arrays.sort(result._matrix);

    if (sortType == 1) {
      // Why is there no native implementation of a descending sort on primitives in Java ...
      for (int n = 0; n < vector.n_elem / 2; n++) {
        double temp = result._matrix[n];
        result._matrix[n] = result._matrix[vector.n_elem - (n + 1)];
        result._matrix[vector.n_elem - (n + 1)] = temp;
      }
    }

    return result;
  }

  /**
   * Sorts the matrix either in ascending order per column.
   * 
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat sortMat(AbstractMat matrix) {
    return sortMat(matrix, 0, 0);
  }

  /**
   * Sorts the matrix either in ascending ({@code sortType} = 0) or descending ({@code sortType} = 1) order per column.
   * 
   * @param matrix The matrix
   * @param sortType The sortType
   * @return The matrix
   */
  public static Mat sortMat(AbstractMat matrix, int sortType) {
    return sortMat(matrix, sortType, 0);
  }

  /**
   * Sorts the matrix either in ascending ({@code sortType} = 0) or descending ({@code sortType} = 1) order either per
   * column ({@code dimension} = 0) or row ({@code dimension} = 1).
   * 
   * @param matrix The matrix
   * @param sortType The sortType
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat sortMat(AbstractMat matrix, int sortType, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result = new Mat();
    result.copy_size(matrix);
    if (dimension == 0) {
      for (int j = 0; j < matrix.n_cols; j++) {
        result.col(j, Op.EQUAL, sort(matrix.colInternal(j), sortType));
      }
    } else {
      for (int i = 0; i < matrix.n_rows; i++) {
        result.row(i, Op.EQUAL, sort(matrix.rowInternal(i), sortType));
      }
    }

    return result;
  }

  /**
   * Returns the corresponding indexes of the vector sorted in ascending order.
   * <p>
   * **Note:** The vector remains unchanged.
   * 
   * @param vector The vector
   * @return The vector
   */
  public static Mat sort_index(AbstractMat vector) {
    return sort_index(vector, 0);
  }

  /**
   * Returns the corresponding indexes of the vector sorted in ascending ({@code sortType} = 0) or descending (
   * {@code sortType} = 1) order.
   * <p>
   * **Note:** The vector remains unchanged.
   * 
   * @param vector The vector
   * @param sortType The sortType
   * @return The vector
   */
  public static Mat sort_index(AbstractMat vector, int sortType) {
    return stable_sort_index(vector, sortType);
  }

  /**
   * Returns the corresponding indexes of the vector stable sorted in ascending order.
   * <p>
   * **Note:** The vector remains unchanged.
   * 
   * @param vector The vector
   * @return The vector
   */
  public static Mat stable_sort_index(AbstractMat vector) {
    return stable_sort_index(vector, 0);
  }

  /**
   * Returns the corresponding indexes of the vector stable sorted in ascending ({@code sortType} = 0) or descending (
   * {@code sortType} = 1) order.
   * <p>
   * **Note:** The vector remains unchanged.
   * 
   * @param vector The vector
   * @param sortType The sortType
   * @return The vector
   */
  public static Mat stable_sort_index(AbstractMat vector, int sortType) {
    vector.isNonVectorDetection();
    AbstractMat.isNonBinaryParameterDetection(sortType);

    Map<Integer, Double> map = new HashMap<Integer, Double>(vector.n_elem);
    vector.iteratorReset();
    for (int n = 0; n < vector.n_elem; n++) {
      map.put(n, vector._matrix[vector.iteratorNext()]);
    }

    List<Entry<Integer, Double>> list = new ArrayList<Entry<Integer, Double>>(map.entrySet());

    if (sortType == 0) {
      Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
        @Override
        public int compare(Entry<Integer, Double> entry1, Entry<Integer, Double> entry2) {
          Double value1 = entry1.getValue();
          Double value2 = entry2.getValue();

          if (value1.equals(value2) || (value1 == 0 && value2 == 0)) {
            return entry1.getKey().compareTo(entry2.getKey());
          } else {
            return value1.compareTo(value2);
          }
        }
      });
    } else {
      Collections.sort(list, new Comparator<Entry<Integer, Double>>() {
        @Override
        public int compare(Entry<Integer, Double> entry1, Entry<Integer, Double> entry2) {
          Double value1 = entry1.getValue();
          Double value2 = entry2.getValue();

          if (value1.equals(value2) || (value1 == 0 && value2 == 0)) {
            return entry1.getKey().compareTo(entry2.getKey());
          } else {
            return value2.compareTo(value1);
          }
        }
      });
    }

    Mat result = new Mat();
    result.copy_size(vector);

    int n = 0;
    for (Entry<Integer, Double> entry : list) {
      result._matrix[n++] = entry.getKey();
    }

    return result;
  }

  /**
   * Returns a matrix with its size set to {@link Mat#n_rows} rows and {@link Mat#n_cols} columns.
   * <p>
   * Elements are accessed column-wise, while the matrix is also filled column-wise.
   * <p>
   * Guarantees to reuse the values of the elements but not their positions.
   * 
   * @param matrix The matrix
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of rows
   * @return The matrix
   */
  public static Mat reshape(Mat matrix, int numberOfRows, int numberOfColumns) {
    return reshape(matrix, numberOfRows, numberOfColumns, 0);
  }

  /**
   * Returns a matrix with its size set to {@link Mat#n_rows} rows and {@link Mat#n_cols} columns.
   * <p>
   * Elements can be accessed either column-wise ({@code dimension} = 0) or row-wise ({@code dimension} = 1), while the
   * matrix is always filled column-wise.
   * <p>
   * Guarantees to reuse the values of the elements but not their positions.
   * 
   * @param matrix The matrix
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat reshape(Mat matrix, int numberOfRows, int numberOfColumns, int dimension) {
    Mat temp = new Mat(matrix);
    temp.reshape(numberOfRows, numberOfColumns, dimension);
    return temp;
  }

  /**
   * Returns a matrix with its size set to {@link Mat#n_rows} rows and {@link Mat#n_cols} columns.
   * <p>
   * Elements are accessed column-wise, while the matrix is also filled column-wise.
   * <p>
   * Guarantees to reuse the values of the elements and their positions.
   * 
   * @param matrix The matrix
   * @param numberOfRows The number of rows
   * @param numberOfColumns The number of columns
   * @return The matrix
   */
  public static Mat resize(Mat matrix, int numberOfRows, int numberOfColumns) {
    Mat temp = new Mat(matrix);
    temp.resize(numberOfRows, numberOfColumns);
    return temp;
  }

  /**
   * Returns the correlation of two vectors.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param vector1 The first vector
   * @param vector2 The second vector
   * @param normType The normalisation
   * @return The correlation
   */
  protected static double corInternal(AbstractMat vector1, AbstractMat vector2, int normType) {
    return covInternal(vector1, vector2, normType) / Math.sqrt(covInternal(vector1, vector1, normType) * covInternal(vector2, vector2, normType));
  }

  /**
   * Returns the correlation matrix with normalisation by {@code count} - 1.
   * 
   * @param matrix The matrix
   * @return The correlation matrix
   */
  public static Mat cor(AbstractMat matrix) {
    return cor(matrix, 0);
  }

  /**
   * Returns the correlation matrix.
   * <p>
   * Performs either normalisation by {@code count} - 1 ({@code normType} = 0) or {@code count} ({@code normType} = 1).
   * 
   * @param matrix The matrix
   * @param normType The normalisation
   * @return The correlation matrix
   */
  public static Mat cor(AbstractMat matrix, int normType) {
    return cor(matrix, matrix, normType);
  }

  /**
   * @param matrix1 The first matrix
   * @param matrix2 The second matrix
   * @return The correlation matrix
   */
  public static Mat cor(AbstractMat matrix1, AbstractMat matrix2) {
    return cor(matrix1, matrix2, 0);
  }

  /**
   * @param matrix1 The first matrix
   * @param matrix2 The second matrix
   * @param normType The normalisation
   * @return The correlation matrix
   */
  public static Mat cor(AbstractMat matrix1, AbstractMat matrix2, int normType) {
    matrix1.isEmptyDetection();
    matrix2.isEmptyDetection();

    if (matrix1.is_vec() && matrix2.is_vec()) {
      AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_elem, matrix2.n_elem);

      return new Mat(new double[]{covInternal(matrix1, matrix2, normType) / Math.sqrt(covInternal(matrix1, matrix1, 0) * covInternal(matrix2, matrix2, 0))});
    } else {
      AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_rows, matrix2.n_rows);
      AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_cols, matrix2.n_cols);

      Mat covariance = cov(matrix1, matrix2, normType);

      Mat selfCovariance;
      if (normType != 0) {
        selfCovariance = cov(matrix1, matrix2, 0);
      } else {
        selfCovariance = covariance;
      }

      Mat result = new Mat(matrix1.n_cols, matrix1.n_cols);

      int n = 0;
      for (int j = 0; j < matrix1.n_cols; j++) {
        for (int jj = 0; jj < matrix1.n_cols; jj++) {
          result._matrix[n++] = covariance.at(j, jj) / Math.sqrt(selfCovariance.at(j, j) * selfCovariance.at(jj, jj));
        }
      }

      return result;
    }
  }

  /**
   * @param vector1 The first vector
   * @param vector2 The second vector
   * @param normType The normalisation
   * @return The covariance
   */
  protected static double covInternal(AbstractMat vector1, AbstractMat vector2, int normType) {
    vector1.isEmptyDetection();
    vector2.isEmptyDetection();
    vector1.isNonVectorDetection();
    vector2.isNonVectorDetection();
    AbstractMat.isNonEqualNumberOfElementsDetection(vector1.n_elem, vector2.n_elem);
    AbstractMat.isNonBinaryParameterDetection(normType);

    double covariance = accu(vector1.minus(mean(vector1)).elemTimes(vector2.minus(mean(vector2))));
    if (normType == 0) {
      covariance /= (vector1.n_elem - 1);
    } else {
      covariance /= vector1.n_elem;
    }

    return covariance;
  }

  /**
   * @param matrix The matrix
   * @return The covariance matrix
   */
  public static Mat cov(AbstractMat matrix) {
    return cov(matrix, 0);
  }

  /**
   * @param matrix The matrix
   * @param normType The normalisation
   * @return The covariance matrix
   */
  public static Mat cov(AbstractMat matrix, int normType) {
    return cov(matrix, matrix, normType);
  }

  /**
   * @param matrix1 The first matrix
   * @param matrix2 The second matrix
   * @return The covariance matrix
   */
  public static Mat cov(AbstractMat matrix1, AbstractMat matrix2) {
    return cov(matrix1, matrix2, 0);
  }

  /**
   * @param matrix1 The first matrix
   * @param matrix2 The second matrix
   * @param normType The normalisation
   * @return The covariance matrix
   */
  public static Mat cov(AbstractMat matrix1, AbstractMat matrix2, int normType) {
    matrix1.isEmptyDetection();
    matrix2.isEmptyDetection();

    if (matrix1.is_vec() && matrix2.is_vec()) {
      AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_elem, matrix2.n_elem);

      return new Mat(new double[]{covInternal(matrix1, matrix2, normType)});
    } else {
      AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_rows, matrix2.n_rows);
      AbstractMat.isNonEqualNumberOfElementsDetection(matrix1.n_cols, matrix2.n_cols);

      Mat result = new Mat(matrix1.n_cols, matrix1.n_cols);

      int n = 0;
      for (int j = 0; j < matrix1.n_cols; j++) {
        for (int jj = 0; jj < matrix1.n_cols; jj++) {
          result._matrix[n++] = covInternal(matrix1.colInternal(j), matrix2.colInternal(jj), normType);
        }
      }

      return result;
    }
  }

  /**
   * @param vector The vector
   * @return The histogram
   */
  public static Mat hist(AbstractMat vector) {
    return hist(vector, 10);
  }

  /**
   * @param vector The vector
   * @param numberOfBins The number of bins
   * @return The histogram
   */
  public static Mat hist(AbstractMat vector, int numberOfBins) {
    vector.isNonVectorDetection();
    vector.isEmptyDetection();

    Mat edges = new Mat(numberOfBins + 1, 1);

    vector.iteratorReset();
    double element = vector._matrix[vector.iteratorNext()];
    double minimum = element;
    double maximum = element;
    while (vector.iteratorHasNext()) {
      element = vector._matrix[vector.iteratorNext()];
      minimum = Math.min(maximum, element);
      maximum = Math.max(maximum, element);
    }

    if (Double.isInfinite(minimum)) {
      minimum = -Double.MAX_VALUE;
    }

    if (Double.isInfinite(maximum)) {
      maximum = Double.MAX_VALUE;
    }

    edges._matrix[0] = Double.NEGATIVE_INFINITY;
    double stepLength = (maximum - minimum) / edges.n_elem;
    for (int n = 1; n < edges.n_elem - 1; n++) {
      // Increasing a value step by step by stepLength might be faster, but also reduces precision
      edges._matrix[n] = minimum + stepLength * (n + 1);
    }
    edges._matrix[edges.n_elem - 1] = Double.POSITIVE_INFINITY;

    Mat histc = histc(vector, edges);
    Mat result = histc.subvec(0, numberOfBins - 1);
    result._matrix[numberOfBins - 1] += histc._matrix[numberOfBins];

    return result;
  }

  // Actually, this is not supported by Armadillo C++ despited notes in the API
  // Informed Conrad Sanderson about this.
  //
  // /**
  // * @param matrix The matrix
  // * @return The histogramm
  // */
  // public static Mat histMat(AbstractMat matrix) {
  // return histMat(matrix, 10);
  // }
  //
  // /**
  // * @param matrix The matrix
  // * @param numberOfBins The number of bins
  // * @return vThe histogramm
  // */
  // public static Mat histMat(AbstractMat matrix, int numberOfBins) {
  // return histMat(matrix, numberOfBins, 0);
  // }
  //
  //
  //
  // /**
  // * @param matrix The matrix
  // * @param numberOfBins The number of bins
  // * @param dimension The dimension
  // * @return The histogramm
  // */
  // public static Mat histMat(AbstractMat matrix, int numberOfBins, int dimension) {
  // matrix.isEmptyDetection();
  // AbstractMat.isNonBinaryParameterDetection(dimension);
  //
  // Mat result;
  // if (dimension == 0) {
  // result = new Mat(matrix.n_cols, 1);
  // for (int j = 0; j < matrix.n_cols; j++) {
  // result.col(j, Op.EQUAL, hist(matrix.colInternal(j), numberOfBins));
  // }
  // } else {
  // result = new Mat(matrix.n_rows, 1);
  // for (int i = 0; i < matrix.n_rows; i++) {
  // result.row(i, Op.EQUAL, hist(matrix.rowInternal(i), numberOfBins));
  // }
  // }
  //
  // return result;
  // }

  /**
   * @param vector The vector
   * @param centers The centres
   * @return The histogram
   */
  public static Mat hist(AbstractMat vector, AbstractMat centers) {
    vector.isNonVectorDetection();
    vector.isEmptyDetection();

    Mat edges = new Mat(centers.n_elem + 1, 1);

    centers.iteratorReset();
    edges._matrix[0] = Double.NEGATIVE_INFINITY;
    int n = 1;
    double lastElement = centers._matrix[centers.iteratorNext()];
    while (centers.iteratorHasNext()) {
      double currentElement = centers._matrix[centers.iteratorNext()];

      edges._matrix[n++] = lastElement + (currentElement - lastElement) / 2;
      lastElement = currentElement;
    }
    // The last element of edges
    edges._matrix[centers.n_elem] = Double.POSITIVE_INFINITY;

    Mat histc = histc(vector, edges);
    Mat result = histc.subvec(0, centers.n_elem - 1);
    result._matrix[centers.n_elem - 1] += histc._matrix[centers.n_elem];

    return result;
  }

  /**
   * @param matrix The matrix
   * @param centers The centres
   * @return The histogram
   */
  public static Mat histMat(AbstractMat matrix, AbstractMat centers) {
    return histMat(matrix, centers, 0);
  }

  /**
   * @param matrix The matrix
   * @param centers The centres
   * @param dimension The dimension
   * @return The histogram
   */
  public static Mat histMat(AbstractMat matrix, AbstractMat centers, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(centers.n_elem, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result.col(j, Op.EQUAL, hist(matrix.colInternal(j), centers));
      }
    } else {
      result = new Mat(matrix.n_rows, centers.n_elem);
      for (int i = 0; i < matrix.n_rows; i++) {
        result.row(i, Op.EQUAL, hist(matrix.rowInternal(i), centers));
      }
    }

    return result;
  }

  /**
   * @param vector The vector
   * @param edges The edges
   * @return The histogram
   */
  public static Mat histc(AbstractMat vector, AbstractMat edges) {
    vector.isNonVectorDetection();
    vector.isEmptyDetection();

    Mat result;
    if (vector.is_colvec()) {
      result = new Mat(edges.n_elem, 1);
    } else {
      result = new Mat(1, edges.n_elem);
    }

    vector.iteratorReset();
    while (vector.iteratorHasNext()) {
      double element = vector._matrix[vector.iteratorNext()];

      edges.iteratorReset();
      double edge = edges._matrix[edges.iteratorNext()];

      if (element < edge) {
        continue;
      }

      for (int n = 1; n < edges.n_elem; n++) {
        edge = edges._matrix[edges.iteratorNext()];

        if (element < edge) {
          result._matrix[n - 1]++;
          break;
        }
      }

      if (element == edge) {
        result._matrix[edges.n_elem - 1]++;
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @param edges The edges
   * @return The histogram
   */
  public static Mat histcMat(AbstractMat matrix, AbstractMat edges) {
    return histcMat(matrix, edges, 0);
  }

  /**
   * @param matrix The matrix
   * @param edges The edges
   * @param dimension The dimension
   * @return The histogram
   */
  public static Mat histcMat(AbstractMat matrix, AbstractMat edges, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result;
    if (dimension == 0) {
      result = new Mat(edges.n_elem, matrix.n_cols);
      for (int j = 0; j < matrix.n_cols; j++) {
        result.col(j, Op.EQUAL, histc(matrix.colInternal(j), edges));
      }
    } else {
      result = new Mat(matrix.n_rows, edges.n_elem);
      for (int i = 0; i < matrix.n_rows; i++) {
        result.row(i, Op.EQUAL, histc(matrix.rowInternal(i), edges));
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat fliplr(AbstractMat matrix) {
    Mat result = new Mat(matrix);

    for (int j = 0; j < result.n_cols / 2; j++) {
      result.swap_cols(j, result.n_cols - (j + 1));
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat flipud(AbstractMat matrix) {
    Mat result = new Mat(matrix);

    for (int i = 0; i < result.n_rows / 2; i++) {
      result.swap_rows(i, result.n_rows - (i + 1));
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat diagmat(AbstractMat matrix) {
    Mat result;
    if (matrix.is_vec()) {
      result = new Mat(matrix.n_elem, matrix.n_elem, Fill.ZEROS);
      result.diag(Op.EQUAL, matrix);
    } else {
      result = new Mat(matrix.n_rows, matrix.n_cols, Fill.ZEROS);
      result.diag(Op.EQUAL, matrix.diagInternal(0));
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat trimatu(AbstractMat matrix) {
    matrix.isNotSquareDetection();

    Mat result = new Mat();
    result.copy_size(matrix);

    int n = 0;
    for (int j = 0; j < matrix.n_cols; j++) {
      for (int i = 0; i < matrix.n_rows; i++) {
        if (i <= j) {
          result._matrix[n] = matrix._matrix[matrix.getElementIndex(n)];
        }
        n++;
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat trimatl(AbstractMat matrix) {
    matrix.isNotSquareDetection();

    Mat result = new Mat();
    result.copy_size(matrix);

    int n = 0;
    for (int j = 0; j < matrix.n_cols; j++) {
      for (int i = 0; i < matrix.n_rows; i++) {
        if (i >= j) {
          result._matrix[n] = matrix._matrix[matrix.getElementIndex(n)];
        }
        n++;
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat symmatu(AbstractMat matrix) {
    matrix.isNotSquareDetection();

    Mat result = new Mat();
    result.copy_size(matrix);

    int n = 0;
    for (int j = 0; j < matrix.n_cols; j++) {
      for (int i = 0; i < matrix.n_rows; i++) {
        if (i < j) {
          result._matrix[n] = matrix._matrix[matrix.getElementIndex(n)];
          result._matrix[matrix.getElementIndex(j, i)] = matrix._matrix[matrix.getElementIndex(n)];
        } else if (i == j) {
          result._matrix[n] = matrix._matrix[matrix.getElementIndex(n)];
        }

        n++;
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat symmatl(AbstractMat matrix) {
    matrix.isNotSquareDetection();

    Mat result = new Mat();
    result.copy_size(matrix);

    int n = 0;
    for (int j = 0; j < matrix.n_cols; j++) {
      for (int i = 0; i < matrix.n_rows; i++) {
        if (i > j) {
          result._matrix[n] = matrix._matrix[matrix.getElementIndex(n)];
          result._matrix[matrix.getElementIndex(j, i)] = matrix._matrix[matrix.getElementIndex(n)];
        } else if (i == j) {
          result._matrix[n] = matrix._matrix[matrix.getElementIndex(n)];
        }

        n++;
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat trans(AbstractMat matrix) {
    return matrix.t();
  }

  /**
   * Creates a column vector containing indices of all non-zero elements. Contains all indices of elements that satisfy
   * an operation if used together with {@link Op#evaluate(Mat, Op, Mat)}.
   * 
   * @param matrix The provided matrix.
   * @return The created column vector.
   */
  public static Mat find(AbstractMat matrix) {
    return find(matrix, 0, "first");
  }

  /**
   * Creates a column vector containing indices of all non-zero elements. Contains all indices of elements that satisfy
   * an operation if used together with {@link Op#evaluate(Mat, Op, Mat)}.
   * 
   * @param matrix The provided matrix.
   * @param k The number of elements to be evaluated.
   * @return The created column vector.
   */
  public static Mat find(AbstractMat matrix, int k) {
    return find(matrix, k, "first");
  }

  /**
   * Creates a column vector containing indices of all non-zero elements. Contains all indices of elements that satisfy
   * an operation if used together with {@link Op#evaluate(Mat, Op, Mat)}.
   * <p>
   * Contains all {@code k = 0} (default) or the first ({@code s = "first"}, default) respectively last (
   * {@code s = "last"}) {@code k} indices at most.
   * <p>
   * Only relational operators are supported.
   * 
   * @param matrix The provided matrix.
   * @param k The number of elements to be evaluated.
   * @param s The element to to begin with.
   * @return The created column vector.
   * 
   * @throws IllegalArgumentException The parameter s needs to be either 'first' or 'last', but was {@code s}.
   */
  public static Mat find(AbstractMat matrix, int k, String s) throws IllegalArgumentException {
    matrix.isElementOutOfBoundsDetection(k);

    if (!s.equals("first") && !s.equals("last")) {
      throw new IllegalArgumentException("The parameter s needs to be either 'first' or 'last', but was " + s);
    }

    Mat result = new Mat(matrix.n_elem, 1);

    int limit;
    if (k > 0) {
      limit = k;
    } else {
      limit = matrix.n_elem;
    }

    int index = 0;
    if (s.equals("first")) {
      for (int n = 0; n < matrix.n_elem && index < limit; n++) {
        if (matrix.at(n) != 0) {
          result._matrix[index++] = n;
        }
      }
    } else {
      for (int n = matrix.n_elem - 1; n >= 0 && index < limit; n--) {
        if (matrix.at(n) != 0) {
          result._matrix[index++] = n;
        }
      }
    }

    // Saves current values of the elements since length <= n_elem
    result.reshape(index, 1);

    if (s.equals("last")) {
      for (int n = 0; n < result.n_elem / 2; n++) {
        double temp = result._matrix[n];
        result._matrix[n] = result._matrix[result.n_elem - (n + 1)];
        result._matrix[result.n_elem - (n + 1)] = temp;
      }
    }

    return result;
  }

  /**
   * Returns the main diagonal of the matrix as a column vector.
   * 
   * @param matrix The matrix
   * @return The diagonal
   */
  public static Mat diagvec(AbstractMat matrix) {
    return diagvec(matrix, 0);
  }

  /**
   * Returns the {@code k}th diagonal of the matrix as a column vector.
   * <p>
   * <ul>
   * <li>For {@code k} = 0, the main diagonal is returned
   * <li>For {@code k} > 0, the {@code k}th super-diagonal is returned.
   * <li>For {@code k} < 0, the {@code k}th sub-diagonal is returned.
   * </ul>
   * 
   * @param matrix The matrix
   * @param k The diagonal position
   * @return The diagonal
   */
  public static Mat diagvec(AbstractMat matrix, int k) {
    return matrix.diag(k);
  }

  /**
   * @param vector The vector
   * @return The cumulative sum
   */
  public static Mat cumsum(AbstractMat vector) {
    vector.isNonVectorDetection();
    vector.isEmptyDetection();

    Mat result = new Mat();
    result.copy_size(vector);

    vector.iteratorReset();
    result._matrix[0] = vector._matrix[vector.iteratorNext()];
    for (int n = 1; n < result.n_elem; n++) {
      result._matrix[n] = result._matrix[n - 1] + vector._matrix[vector.iteratorNext()];
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The cumulative sum
   */
  public static Mat cumsumMat(AbstractMat matrix) {
    return cumsumMat(matrix, 0);
  }

  /**
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The matrix
   */
  public static Mat cumsumMat(AbstractMat matrix, int dimension) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result = new Mat();
    result.copy_size(matrix);
    if (dimension == 0) {
      for (int j = 0; j < matrix.n_cols; j++) {
        result.col(j, Op.EQUAL, cumsum(matrix.colInternal(j)));
      }
    } else {
      for (int i = 0; i < matrix.n_rows; i++) {
        result.row(i, Op.EQUAL, cumsum(matrix.rowInternal(i)));
      }
    }

    return result;
  }

  /**
   * @param vector1 The sample vector
   * @param vector2 The kernel vector
   * @return The matrix
   */
  public static Mat conv(AbstractMat vector1, AbstractMat vector2) {
    vector1.isNonVectorDetection();
    vector2.isNonVectorDetection();
    vector1.isEmptyDetection();
    vector2.isEmptyDetection();

    Mat result;

    if (vector1.is_colvec()) {
      result = new Mat(vector1.n_elem + vector2.n_elem - 1, 1);
    } else {
      result = new Mat(1, vector1.n_elem + vector2.n_elem - 1);
    }

    for (int n = 0; n < result.n_elem; n++) {
      int min = Math.max(0, n - vector2.n_elem + 1);
      int max = Math.min(vector1.n_elem, n + 1);

      for (int nn = min; nn < max; nn++)
      {
        result._matrix[n] += vector1._matrix[vector1.getElementIndex(nn)] * vector2._matrix[vector2.getElementIndex(n - nn)];
      }
    }

    return result;
  }

  /**
   * @param vector1 The first vector
   * @param vector2 The second vector
   * @return The cross product
   * 
   * @throws IllegalArgumentException Both vectors must be 3-dimensional, but where {@link AbstractMat#n_elem
   *           vector1.n_elem} and {@code vector2.n_elem}-dimensional.
   */
  public static Mat cross(AbstractMat vector1, AbstractMat vector2) throws IllegalArgumentException {
    vector1.isNonVectorDetection();
    vector2.isNonVectorDetection();

    if (vector1.n_elem != 3 || vector2.n_elem != 3) {
      throw new IllegalArgumentException("Both vectors must be 3-dimensional, but where " + vector1.n_elem + " and " + vector2.n_elem + "-dimensional.");
    }

    Mat result;
    if (vector1.is_colvec()) {
      result = new Mat(3, 1);
    } else {
      result = new Mat(1, 3);
    }

    result._matrix[0] = vector1._matrix[1] * vector2._matrix[2] - vector1._matrix[2] * vector2._matrix[1];
    result._matrix[1] = vector1._matrix[2] * vector2._matrix[0] - vector1._matrix[0] * vector2._matrix[2];
    result._matrix[2] = vector1._matrix[0] * vector2._matrix[1] - vector1._matrix[1] * vector2._matrix[0];

    return result;
  }

  /**
   * @param A The first matrix
   * @param B The second matrix
   * @return The konecker product
   */
  public static Mat kron(AbstractMat A, AbstractMat B) {
    Mat result = repmat(B, A.n_rows, A.n_cols);

    int n = 0;
    A.iteratorReset();
    for (int j = 0; j < A.n_cols; j++) {
      for (int i = 0; i < A.n_rows; i++) {
        result.submatInternal(i * B.n_rows, (i + 1) * B.n_rows - 1, j * B.n_cols, (j + 1) * B.n_cols - 1).inplace(Op.TIMES, A._matrix[n++]);
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @param rng The pseudorandom generator
   * @return The matrix
   */
  public static Mat shuffle(AbstractMat matrix, Random rng) {
    return shuffle(matrix, 0, rng);
  }

  /**
   * @param matrix The matrix
   * @param dimension The dimension
   * @param rng The pseudorandom generator
   * @return The matrix
   */
  public static Mat shuffle(AbstractMat matrix, int dimension, Random rng) {
    matrix.isEmptyDetection();
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result = new Mat();
    result.copy_size(matrix);

    Integer[] indicies;
    if (dimension == 0) {
      indicies = new Integer[matrix.n_rows];
    } else {
      indicies = new Integer[matrix.n_cols];
    }

    for (int n = 0; n < indicies.length; n++) {
      indicies[n] = n;
    }
    Collections.shuffle(Arrays.asList(indicies), rng);

    if (dimension == 0) {
      for (int i = 0; i < matrix.n_rows; i++) {
        result.row(i, Op.EQUAL, matrix.rowInternal(indicies[i]));
      }
    } else {
      for (int j = 0; j < matrix.n_cols; j++) {
        result.col(j, Op.EQUAL, matrix.colInternal(indicies[j]));
      }
    }

    return result;
  }

  /**
   * @param matrix The matrix
   * @return The matrix
   */
  public static Mat unique(AbstractMat matrix) {

    Mat sorted;
    if (!matrix.is_vec()) {
      sorted = sort(vectorise(matrix));
    } else {
      sorted = sort(matrix);
    }

    Mat result = new Mat();
    result.copy_size(sorted);
    int index = 0;
    double lastElement = Datum.inf;
    for (int n = 0; n < sorted.n_elem; n++) {
      double element = sorted._matrix[n];

      if (element > lastElement || Double.isInfinite(element)) {
        result._matrix[index++] = element;
      }

      lastElement = element;
    }

    if(matrix.is_rowvec()) {
      result.reshape(1, index);
    } else {
      result.reshape(index, 1);
    }
    
    return result;
  }

  /**
   * @param matrix The matrix
   * @return The vector
   */
  public static Mat vectorise(AbstractMat matrix) {
    return vectorise(matrix, 0);
  }

  /**
   * @param matrix The matrix
   * @param dimension The dimension
   * @return The vector
   */
  public static Mat vectorise(AbstractMat matrix, int dimension) {
    AbstractMat.isNonBinaryParameterDetection(dimension);

    Mat result = new Mat(matrix);

    if (dimension == 0) {
      result.updateAttributes(matrix.n_elem, 1);
    } else {
      result.reshape(1, matrix.n_elem, 1);
    }

    return result;
  }

  /**
   * @param X The matrix
   * @return The matrix
   */
  public static Mat chol(AbstractMat X) {
    X.isNotSymmetricDetection();
    X.isIllConditionedDectetion();

    CholeskyDecomposition<DenseMatrix64F> chol = DecompositionFactory.chol(X.n_rows, true);
    chol.decompose(AbstractMat.convertMatToEJMLMat(X));
    return AbstractMat.convertEJMLToMat(chol.getT(null));
  }

  /**
   * @param R The matrix
   * @param X The matrix
   */
  public static void chol(Mat R, AbstractMat X) {
    Mat temp = chol(X);
    R.copy_size(temp);
    System.arraycopy(temp._matrix, 0, R._matrix, 0, temp.n_elem);
  }

  /**
   * @param X The matrix
   * @return The matrix
   */
  public static Mat eig_sym(AbstractMat X) {
    X.isNotSymmetricDetection();
    X.isIllConditionedDectetion();

    EigenDecomposition<DenseMatrix64F> eig = DecompositionFactory.eig(X.n_rows, false);
    eig.decompose(AbstractMat.convertMatToEJMLMat(X));

    Mat result = new Mat(eig.getNumberOfEigenvalues(), 1);
    for (int n = 0; n < result.n_elem; n++) {
      result._matrix[n] = eig.getEigenvalue(n).real;
    }

    return sort(result);
  }

  /**
   * @param eigenValue The matrix
   * @param X The matrix
   */
  public static void eig_sym(Mat eigenValue, AbstractMat X) {
    Mat temp = eig_sym(X);
    eigenValue.copy_size(temp);
    System.arraycopy(temp._matrix, 0, eigenValue._matrix, 0, temp.n_elem);
  }

  /**
   * @param eigenValue The matrix
   * @param eigenVector The matrix
   * @param X The matrix
   */
  public static void eig_sym(Mat eigenValue, Mat eigenVector, AbstractMat X) {
    X.isNotSquareDetection();
    X.isNotSymmetricDetection();
    X.isIllConditionedDectetion();

    EigenDecomposition<DenseMatrix64F> eig = DecompositionFactory.eig(X.n_cols, true);
    eig.decompose(AbstractMat.convertMatToEJMLMat(X));

    Mat tempEigenValue = new Mat(eig.getNumberOfEigenvalues(), 1);
    Mat tempEigenVector = new Mat(eig.getNumberOfEigenvalues(), X.n_cols);
    int destColumnPointer = 0;
    for (int n = 0; n < tempEigenValue.n_elem; n++) {
      tempEigenValue._matrix[n] = eig.getEigenvalue(n).real;
      System.arraycopy(eig.getEigenVector(n).data, 0, tempEigenVector._matrix, destColumnPointer, X.n_rows);
      destColumnPointer += X.n_rows;
    }

    Mat indices = sort_index(tempEigenValue);

    eigenValue.copy_size(tempEigenValue);
    eigenVector.copy_size(tempEigenVector);
    destColumnPointer = 0;
    for (int n = 0; n < tempEigenValue.n_elem; n++) {
      int nn = (int) indices._matrix[n];

      eigenValue._matrix[n] = tempEigenValue._matrix[nn];
      System.arraycopy(tempEigenVector._matrix, nn * X.n_cols, eigenVector._matrix, destColumnPointer, X.n_rows);
      destColumnPointer += X.n_rows;
    }
  }

  /**
   * @param A The matrix
   * @return The matrix
   */
  public static Mat inv(AbstractMat A) {
    A.isIllConditionedDectetion();

    return A.i();
  }

  /**
   * @param B The matrix
   * @param A The matrix
   */
  public static void inv(Mat B, AbstractMat A) {
    Mat temp = inv(A);

    B.copy_size(A);
    System.arraycopy(temp._matrix, 0, B._matrix, 0, temp.n_elem);
  }

  /**
   * @param L The matrix
   * @param U The matrix
   * @param X The matrix
   * @returnThe matrix
   */
  public static void lu(Mat L, Mat U, AbstractMat X) {
    X.isIllConditionedDectetion();

    LUDecomposition<DenseMatrix64F> lu = DecompositionFactory.lu(X.n_rows, X.n_rows);
    lu.decompose(AbstractMat.convertMatToEJMLMat(X));

    Mat temp;
    temp = AbstractMat.convertEJMLToMat(lu.getLower(null));
    L.copy_size(temp);
    System.arraycopy(temp._matrix, 0, L._matrix, 0, temp.n_elem);

    temp = AbstractMat.convertEJMLToMat(lu.getUpper(null));
    U.copy_size(temp);
    System.arraycopy(temp._matrix, 0, U._matrix, 0, temp.n_elem);
  }

  /**
   * @param L The matrix
   * @param U The matrix
   * @param P The matrix
   * @param X The matrix
   */
  public static void lu(Mat L, Mat U, Mat P, AbstractMat X) {
    X.isIllConditionedDectetion();

    LUDecomposition<DenseMatrix64F> lu = DecompositionFactory.lu(X.n_rows, X.n_rows);
    lu.decompose(AbstractMat.convertMatToEJMLMat(X));

    Mat temp;
    temp = AbstractMat.convertEJMLToMat(lu.getLower(null));
    L.copy_size(temp);
    System.arraycopy(temp._matrix, 0, L._matrix, 0, temp.n_elem);

    temp = AbstractMat.convertEJMLToMat(lu.getUpper(null));
    U.copy_size(temp);
    System.arraycopy(temp._matrix, 0, U._matrix, 0, temp.n_elem);

    temp = AbstractMat.convertEJMLToMat(lu.getPivot(null));
    P.copy_size(temp);
    System.arraycopy(temp._matrix, 0, P._matrix, 0, temp.n_elem);
  }

  /**
   * @param A The matrix
   * @return The matrix
   */
  public static Mat pinv(AbstractMat A) {
    return pinv(A, Math.max(A.n_rows, A.n_cols) * Math.ulp(norm(A, 2)));
  }

  /**
   * @param A The matrix
   * @param tolerance The tolerance
   * @return The matrix
   */
  public static Mat pinv(AbstractMat A, double tolerance) {
    A.isIllConditionedDectetion();

    SolvePseudoInverseSvd pinv = (SolvePseudoInverseSvd) LinearSolverFactory.pseudoInverse(true);
    pinv.setThreshold(tolerance);
    pinv.setA(AbstractMat.convertMatToEJMLMat(A));

    DenseMatrix64F result = new DenseMatrix64F(A.n_cols, A.n_rows);
    pinv.invert(result);

    return AbstractMat.convertEJMLToMat(result);
  }

  /**
   * @param B The matrix
   * @param A The matrix
   */
  public static void pinv(Mat B, AbstractMat A) {
    pinv(B, A, Math.max(A.n_rows, A.n_cols) * Math.ulp(norm(A, 2)));
  }

  /**
   * @param A The matrix
   * @param B The matrix
   * @param tolerance The tolerance
   */
  public static void pinv(Mat B, AbstractMat A, double tolerance) {
    Mat temp = pinv(A);
    B.copy_size(temp);
    System.arraycopy(temp._matrix, 0, B._matrix, 0, temp.n_elem);
  }

  /**
   * Performs a <a href="http://en.wikipedia.org/wiki/QR_decomposition">QR decomposition</a> on the matrix {@code x}.
   * <p>
   * The matrix {@code x} is decomposed into a orthogonal matrix {@code q} and upper triangular matrix {@code r}, such
   * that {@code x = qr}.
   * <p>
   * The provided matrices {@code q} and {@code r} are not touched if the decomposition fails.
   * 
   * @param Q An orthogonal matrix.
   * @param R An upper triangular matrix.
   * @param X The matrix to be decomposed.
   * @return False if the decomposition fails and true otherwise.
   */
  public static boolean qr(Mat Q, Mat R, AbstractMat X) {
    X.isIllConditionedDectetion();

    QRDecomposition<DenseMatrix64F> qr = DecompositionFactory.qr(X.n_rows, X.n_cols);
    qr.decompose(AbstractMat.convertMatToEJMLMat(X));

    Mat tempQ;
    Mat tempR;
    try {
      tempQ = AbstractMat.convertEJMLToMat(qr.getQ(null, false));
      tempR = AbstractMat.convertEJMLToMat(qr.getR(null, false));
    } catch(IllegalArgumentException exception) {
      return false;
    }

    Q.copy_size(tempQ);
    System.arraycopy(tempQ._matrix, 0, Q._matrix, 0, tempQ.n_elem);

    R.copy_size(tempR);
    System.arraycopy(tempR._matrix, 0, R._matrix, 0, tempR.n_elem);

    return true;
  }

  /**
   * Solves a system of linear equations {@code ax = b} with unknown {@code x}.
   * 
   * @param A The matrix {@code a}.
   * @param B The matrix {@code b}.
   * @return The matrix {@code x}.
   * 
   * @throws RuntimeException The algorithm was unable to solve the matrix.
   */
  public static Mat solve(AbstractMat A, AbstractMat B) throws RuntimeException {
    A.isNotSquareDetection();

    AbstractMat.isNonEqualNumberOfElementsDetection(A.n_rows, B.n_rows);
    A.isIllConditionedDectetion();

    DenseMatrix64F X = new DenseMatrix64F(A.n_cols, B.n_cols);

    if (!CommonOps.solve(AbstractMat.convertMatToEJMLMat(A), AbstractMat.convertMatToEJMLMat(B), X)) {
      throw new RuntimeException("The algorithm was unable to solve the matrix.");
    };

    return AbstractMat.convertEJMLToMat(X);
  }

  /**
   * Solves a system of linear equations {@code ax = b} with unknown {@code x}.
   * <p>
   * Return false if no solution was found and true otherwise. The provided matrices {@code x} is not touched if the
   * decomposition fails.
   * 
   * @param A The matrix {@code a}.
   * @param B The matrix {@code b}.
   * @param X The matrix {@code x}.
   * @return The boolean value.
   */
  public static boolean solve(Mat X, AbstractMat A, AbstractMat B) {
    try {
      Mat temp = solve(A, B);
      X.copy_size(temp);
      System.arraycopy(temp._matrix, 0, X._matrix, 0, temp.n_elem);
    } catch(RuntimeException exception) {
      return false;
    }
    return true;
  }

  /**
   * Performs a <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">singular value decomposition</a> on
   * the matrix {@code x}.
   * <p>
   * The matrix {@code x} is decomposed into vector {@code s} such that {@code x = u * diagmat(s) * v'}. The matrices
   * {@code u} and {@code v} are not returned.
   * 
   * @param X The matrix to be decomposed.
   * @return The matrix {@code s}.
   * 
   * @throws RuntimeException Thrown if the algorithm was unable to solve the provided matrix.
   */
  public static Mat svd(AbstractMat X) throws RuntimeException {
    X.isIllConditionedDectetion();

    SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(X.n_rows, X.n_cols, false, false, false);
    svd.decompose(AbstractMat.convertMatToEJMLMat(X));

    DenseMatrix64F S;
    try {
      S = svd.getW(null);
      // check catch
    } catch(IllegalArgumentException exception) {
      throw new RuntimeException("The algorithm was unable to decompose the matrix.");
    }

    return AbstractMat.convertEJMLToMat(S).diag();
  }

  /**
   * Performs a <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">singular value decomposition</a> on
   * the matrix {@code x}.
   * <p>
   * The matrix {@code x} is decomposed into vector {@code s} such that {@code x = u * diagmat(s) * v'}. The matrices
   * {@code u} and {@code v} are not returned.
   * <p>
   * The provided matrix {@code s} is not touched if the decomposition fails.
   * 
   * @param S The matrix {@code s}.
   * @param X The matrix to be decomposed.
   * @return False if the decomposition fails and true otherwise.
   */
  public static boolean svd(Mat S, AbstractMat X) {
    try {
      Mat temp = svd(X);
      S.copy_size(temp);
      System.arraycopy(temp._matrix, 0, S._matrix, 0, temp.n_elem);
    } catch(RuntimeException exception) {
      return false;
    }
    return true;
  }

  /**
   * Performs a <a href="http://en.wikipedia.org/wiki/Singular_value_decomposition">singular value decomposition</a> on
   * the matrix {@code x}.
   * <p>
   * The matrix {@code x} is decomposed into matrix {@code u}, {@code v} and a vector {@code s} such that
   * {@code x = u * diagmat(s) * v'}.
   * <p>
   * The provided matrices {@code u}, {@code s} and {@code v} are not touched if the decomposition fails.
   * 
   * @param U The matrix {@code u}.
   * @param S The matrix {@code s}.
   * @param V The matrix {@code v}.
   * @param X The matrix to be decomposed.
   * @return False if the decomposition fails and true otherwise.
   */
  public static boolean svd(Mat U, Mat S, Mat V, AbstractMat X) {
    X.isIllConditionedDectetion();

    SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(X.n_rows, X.n_cols, true, true, false);
    svd.decompose(AbstractMat.convertMatToEJMLMat(X));

    Mat tempU;
    Mat tempS;
    Mat tempV;
    try {
      tempU = AbstractMat.convertEJMLToMat(svd.getU(null, false));
      tempS = AbstractMat.convertEJMLToMat(svd.getW(null)).diag();
      tempV = AbstractMat.convertEJMLToMat(svd.getV(null, false));
    } catch(IllegalArgumentException exception) {
      return false;
    }

    U.copy_size(tempU);
    System.arraycopy(tempU._matrix, 0, U._matrix, 0, tempU.n_elem);

    S.copy_size(tempS);
    System.arraycopy(tempS._matrix, 0, S._matrix, 0, tempS.n_elem);

    V.copy_size(tempV);
    System.arraycopy(tempV._matrix, 0, V._matrix, 0, tempV.n_elem);

    return true;
  }

  /**
   * Returns true if the matrix contains only finite values and false otherwise.
   * 
   * @param matrix The matrix
   * @return Whether the matrix is finite
   */
  public static boolean is_finite(AbstractMat matrix) {
    return matrix.is_finite();
  }

  /**
   * Returns true if the value is neither NaN nor +/-infinity.
   * 
   * @param value The value
   * @return Whether the value is finite
   */
  public static boolean is_finite(double value) {
    return (!Double.isInfinite(value) && !Double.isNaN(value));
  }
}