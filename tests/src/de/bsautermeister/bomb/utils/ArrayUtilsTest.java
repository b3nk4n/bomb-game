package de.bsautermeister.bomb.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ArrayUtilsTest {

	@Test
	public void fill2DBoolean() {
		boolean[][] data = new boolean[3][2];
		ArrayUtils.fill2D(data, true);

		for (boolean[] slice : data) {
			for (boolean value : slice) {
				assertEquals(true, value);
			}
		}
	}

}
