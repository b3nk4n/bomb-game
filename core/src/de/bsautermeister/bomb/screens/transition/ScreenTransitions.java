package de.bsautermeister.bomb.screens.transition;

import com.badlogic.gdx.math.Interpolation;

public interface ScreenTransitions {
    SlideScreenTransition SLIDE_UP = new SlideScreenTransition(.5f, Interpolation.smooth,
            SlideScreenTransition.SlideType.SLIDE_PARALLEL, Direction.UP);
    SlideScreenTransition SLIDE_DOWN = new SlideScreenTransition(.5f, Interpolation.smooth,
            SlideScreenTransition.SlideType.SLIDE_PARALLEL, Direction.DOWN);
}
