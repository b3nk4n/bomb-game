package de.bsautermeister.bomb.objects;

import com.badlogic.gdx.math.Circle;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import de.bsautermeister.bomb.utils.ArrayUtils;

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
				{T, T, T, T, T, T, T, T, T, T, T},
		};

		boolean updated = sut.remove(circle);

		assertEquals(true, updated);
		assertEquals(true, Arrays.deepEquals(expected, sut.getData()));
	}

	@Test
	public void removeCircleNoIntersection() {
		Circle circle = new Circle(0f, 0f, 9f);
		boolean[][] expected = new boolean[sut.getResolution()][sut.getResolution()];
		ArrayUtils.fill2D(expected, T);

		boolean updated = sut.remove(circle);

		assertEquals(false, updated);
		assertEquals(true, Arrays.deepEquals(expected, sut.getData()));
	}

}
