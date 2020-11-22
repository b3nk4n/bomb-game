package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.utils.Array;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import de.bsautermeister.bomb.utils.ArrayUtils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class FragmentDataTest {
	private static final float EPSILON = 1e-5f;
	private static final boolean T = true;
	private static final boolean F = false;

	private FragmentData sut;

	@Before
	public void setup() {
		sut = new FragmentData(11, 10f);
	}

	@Test
	public void delta() {
		assertEquals(1.0f, sut.getDelta(), EPSILON);
	}

	@Test
	public void getRelativeX() {
		assertEquals(0.0f, sut.getRelativeX(0), EPSILON);
		assertEquals(1.0f, sut.getRelativeX(1), EPSILON);
		assertEquals(10.0f, sut.getRelativeX(10), EPSILON);
	}

	@Test
	public void getRelativeY() {
		assertEquals(0.0f, sut.getRelativeY(0), EPSILON);
		assertEquals(1.0f, sut.getRelativeY(1), EPSILON);
		assertEquals(10.0f, sut.getRelativeY(10), EPSILON);
	}

	@Test
	public void removeCircleInCorner() {
		Circle circle = new Circle(0f, 0f, 3f);
		boolean[][] expected = new boolean[][]{
				{F, F, F, F, T, T, T, T, T, T, T},
				{F, F, F, T, T, T, T, T, T, T, T},
				{F, F, F, T, T, T, T, T, T, T, T},
				{F, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T},
				{T, T, T, T, T, T, T, T, T, T, T}
		};

		boolean updated = sut.remove(circle);

		assertEquals(true, updated);
		assertEquals(true, Arrays.deepEquals(expected, sut.getGridData()));
	}

	@Test
	public void removeCircleNoIntersection() {
		Circle circle = new Circle(-10f, -20f, 9f);
		boolean[][] expected = new boolean[sut.getResolution()][sut.getResolution()];
		ArrayUtils.fill2D(expected, T);

		boolean updated = sut.remove(circle);

		assertEquals(false, updated);
		assertEquals(true, Arrays.deepEquals(expected, sut.getGridData()));
	}

	@Test
	public void removeCircleFullIntersection() {
		Circle circle = new Circle(-0f, -0f, 15f);
		boolean[][] expected = new boolean[sut.getResolution()][sut.getResolution()];
		ArrayUtils.fill2D(expected, F);

		boolean updated = sut.remove(circle);

		assertEquals(true, updated);
		assertEquals(true, Arrays.deepEquals(expected, sut.getGridData()));
	}

	@Test
	public void computeOutlines() {
		FragmentData sut = new FragmentData(10f, new boolean[][] {
				{F, F, F, F, F, F, F, F, F, F, F},
				{F, F, T, T, F, F, F, F, F, F, F},
				{F, F, T, F, F, F, F, T, T, F, F},
				{F, F, F, F, F, F, F, F, F, F, F},
				{F, F, F, F, F, F, F, F, F, F, F},
				{F, F, T, T, T, T, T, F, T, F, F},
				{F, F, F, F, F, T, T, F, T, F, F},
				{F, F, F, F, F, F, F, F, T, T, F},
				{F, F, F, F, F, F, F, F, T, T, F},
				{F, F, T, F, F, F, F, F, T, F, F},
				{F, F, T, F, F, F, F, F, T, F, F}
		});

		Array<float[]> result = sut.computeOutlines();

		assertEquals(3, result.size);

		float[] polygon0 = result.get(0);
		assertEquals(2 * 3, polygon0.length);
		assertArrayEquals(new float[] {
				1f, 2f,
				1f, 3f,
				2f, 2f
		}, polygon0, EPSILON);

		float[] polygon1 = result.get(1);
		assertEquals(2 * 4, polygon1.length);
		assertArrayEquals(new float[] {
				5f, 4f,
				//5f, 5f,
				5f, 6f,
				6f, 6f,
				6f, 5f
		}, polygon1, EPSILON);

		float[] polygon2 = result.get(2);
		assertEquals(2 * 4, polygon2.length);
		assertArrayEquals(new float[] {
				6f, 8f,
				7f, 9f,
				8f, 9f,
				9f, 8f,
				//8f, 8f,
				//7f, 8f
		}, polygon2, EPSILON);
	}

	@Test
	public void computeOutlinesDefaultGrid() {
		FragmentData sut = new FragmentData(4, 3f);

		Array<float[]> result = sut.computeOutlines();

		assertEquals(1, result.size);

		float[] polygon = result.get(0);
		assertEquals(2 * 4, polygon.length);
		assertArrayEquals(new float[] {
				0f, 0f,
				//0f, 1f,
				//0f, 2f,
				0f, 3f,
				//1f, 3f,
				//2f, 3f,
				3f, 3f,
				//3f, 2f,
				//3f, 1f,
				3f, 0f,
				//2f, 0f,
				//1f, 0f
		}, polygon, EPSILON);
	}

}
