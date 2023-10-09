package com.oustme.oustsdk.utils.floatingAnimation;

import java.util.Random;

public class RandomUtil {

    public static int generateRandomBetween(int start, int end) {

        Random random = new Random();
        int rand = random.nextInt(Integer.MAX_VALUE - 1) % end;

        if (rand < start) {
            rand = start;
        }
        return rand;
    }
}
