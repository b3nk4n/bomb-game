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
		sut = new FragmentData(11, 10f, 20f, 10f);
	}

	@Test
	public void delta() {
		assertEquals(1.0f, sut.getDelta(), EPSILON);
	}

	@Test
	public void removeCircleInCorner() {
		Circle circle = new Circle(10f, 20f, 3f);
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
		Circle circle = new Circle(0f, 0f, 9f);
		boolean[][] expected = new boolean[sut.getResolution()][sut.getResolution()];
		ArrayUtils.fill2D(expected, T);

		boolean updated = sut.remove(circle);

		assertEquals(false, updated);
		assertEquals(true, Arrays.deepEquals(expected, sut.getGridData()));
	}

	@Test
	public void computeOutlines() {
		FragmentData sut = new FragmentData(10f, 20f, 10f, new boolean[][] {
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
				11f, 22f,
				11f, 23f,
				12f, 22f
		}, polygon0, EPSILON);

		float[] polygon1 = result.get(1);
		assertEquals(2 * 5, polygon1.length);
		assertArrayEquals(new float[] {
				15f, 24f,
				15f, 25f,
				15f, 26f,
				16f, 26f,
				16f, 25f
		}, polygon1, EPSILON);

		float[] polygon2 = result.get(2);
		assertEquals(2 * 6, polygon2.length);
		assertArrayEquals(new float[] {
				16f, 28f,
				17f, 29f,
				18f, 29f,
				19f, 28f,
				18f, 28f,
				17f, 28f
		}, polygon2, EPSILON);
	}

}
