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

import static arma.TestUtil.assertMatElementWiseEquals;

import java.io.IOException;
import java.util.*;

import org.junit.Test;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kiechle <kiechle@sra.uni-hannover.de>
 * @author Sebastian Niemann <niemann@sra.uni-hannover.de>
 */
@RunWith(Parameterized.class)
public class TestArmaMatrixValuedElementWiseFunctionsTrigonometric {

  /**
   * Returns the matrices to be tested.
   * 
   * @return The test matrices
   * 
   * @throws IOException An I/O error occurred
   */
  @Parameters
  public static Collection<Object[]> getTestMatrices() throws IOException {
    Collection<Object[]> testMatrices = new ArrayList<Object[]>();

    Mat input = new Mat();

    input.load("./test/data/input/elementwise.trigonometric.mat");
    testMatrices.add(new Object[]{new Mat(input)});

    return testMatrices;
  }

  /**
   * The test matrix
   */
  @Parameter
  public Mat _testMatrix;

  /**
   * Test method for {@link arma.Arma#sin(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testSin() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testSin.mat");
    assertMatElementWiseEquals("", expected, Arma.sin(_testMatrix), 1e-7);
  }

  /**
   * Test method for {@link arma.Arma#asin(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testAsin() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testAsin.mat");
    assertMatElementWiseEquals("", expected, Arma.asin(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#sinh(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testSinh() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testSinh.mat");
    assertMatElementWiseEquals("", expected, Arma.sinh(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#asinh(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testAsinh() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testAsinh.mat");
    assertMatElementWiseEquals("", expected, Arma.asinh(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#cos(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testCos() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testCos.mat");
    assertMatElementWiseEquals("", expected, Arma.cos(_testMatrix), 1e-7);
  }

  /**
   * Test method for {@link arma.Arma#acos(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testAcos() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testAcos.mat");
    assertMatElementWiseEquals("", expected, Arma.acos(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#cosh(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testCosh() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testCosh.mat");
    assertMatElementWiseEquals("", expected, Arma.cosh(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#acosh(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testAcosh() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testAcosh.mat");
    assertMatElementWiseEquals("", expected, Arma.acosh(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#tan(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testTan() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testTan.mat");
    assertMatElementWiseEquals("", expected, Arma.tan(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#atan(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testAtan() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testAtan.mat");
    assertMatElementWiseEquals("", expected, Arma.atan(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#tanh(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testTanh() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testTanh.mat");
    assertMatElementWiseEquals("", expected, Arma.tanh(_testMatrix));
  }

  /**
   * Test method for {@link arma.Arma#atanh(arma.AbstractMat)}.
   * 
   * @throws IOException An I/O error occurred
   */
  @Test
  public void testAtanh() throws IOException {
    Mat expected = new Mat();
    expected.load("./test/data/expected/TestArmaMatrixValuedElementWiseFunctionsTrigonometric/testAtanh.mat");
    assertMatElementWiseEquals("", expected, Arma.atanh(_testMatrix));
  }
}
