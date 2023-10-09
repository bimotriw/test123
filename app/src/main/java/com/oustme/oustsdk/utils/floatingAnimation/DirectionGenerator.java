package com.oustme.oustsdk.utils.floatingAnimation;

import android.app.Activity;

import java.util.Random;

import static com.oustme.oustsdk.utils.floatingAnimation.Direction.BOTTOM;
import static com.oustme.oustsdk.utils.floatingAnimation.Direction.LEFT;
import static com.oustme.oustsdk.utils.floatingAnimation.Direction.RIGHT;
import static com.oustme.oustsdk.utils.floatingAnimation.Direction.TOP;

public class DirectionGenerator {


    public int[] getPointsInDirection(Activity activity, Direction direction) {

        switch (direction) {

            case LEFT:
                return getRandomLeft(activity);
            case RIGHT:
                return getRandomRight(activity);
            case BOTTOM:
                return getRandomBottom(activity);
            case TOP:
                return getRandomTop(activity);

            default:
                Direction[] allDirections = new Direction[]{LEFT, TOP, BOTTOM, RIGHT};
                int index = new Random().nextInt(allDirections.length);
                return getPointsInDirection(activity, allDirections[index]);

        }

    }

    public int[] getRandomLeft(Activity activity) {

        int x = 0;

        int height = activity.getResources().getDisplayMetrics().heightPixels;

        Random random = new Random();
        int y = random.nextInt(height);

        return new int[]{x, y};
    }

    public int[] getRandomTop(Activity activity) {

        int y = 0;

        int width = activity.getResources().getDisplayMetrics().widthPixels;

        Random random = new Random();
        int x = random.nextInt(width);

        return new int[]{x, y};
    }


    public int[] getRandomRight(Activity activity) {


        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;

        int x = width ;

        Random random = new Random();
        int y = random.nextInt(height);

        return new int[]{x, y};
    }

    public int[] getRandomBottom(Activity activity) {


        int width = activity.getResources().getDisplayMetrics().widthPixels;
        int height = activity.getResources().getDisplayMetrics().heightPixels;


        int y = height ;
        Random random = new Random();
        int x = random.nextInt(width);

        return new int[]{x, y};
    }


    public Direction getRandomDirection() {
        Direction[] allDirections = new Direction[]{LEFT, TOP, BOTTOM, RIGHT};
        int index = new Random().nextInt(allDirections.length);
        return (allDirections[index]);
    }


    public Direction getRandomDirection(Direction toSkip) {
        Direction[] allExceptionalDirections;
        switch (toSkip) {

            case LEFT:
                allExceptionalDirections = new Direction[]{TOP, BOTTOM, RIGHT};
                break;
            case RIGHT:
                allExceptionalDirections = new Direction[]{TOP, BOTTOM, LEFT};
                break;
            case BOTTOM:
                allExceptionalDirections = new Direction[]{TOP, LEFT, RIGHT};
                break;
            case TOP:
                allExceptionalDirections = new Direction[]{LEFT, BOTTOM, RIGHT};
                break;

            default:
                allExceptionalDirections = new Direction[]{LEFT, TOP, BOTTOM, RIGHT};


        }

        int index = new Random().nextInt(allExceptionalDirections.length);
        return (allExceptionalDirections[index]);
    }
}
