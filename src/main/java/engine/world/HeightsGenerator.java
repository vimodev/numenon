package engine.world;

import java.util.Random;

public class HeightsGenerator {

    private static final float AMPLITUDE = 15f;
    private static final int OCTAVES = 3;
    private static final float ROUGHNESS = 0.3f;
    private static final float FREQUENCY = 8f;

    private static Random random = new Random();
    private static int seed = random.nextInt(1000000000);

    public static float generateHeight(float x, float z) {
        float total = 0;
        for (int i = 1; i <= OCTAVES; i++) {
            float f = FREQUENCY / (float) Math.pow(2f, i - 1);
            float a = AMPLITUDE * (float) Math.pow(ROUGHNESS, i - 1);
            total += getInterpolatedNoise(x / f, z / f) * a;
        }
        return total;
    }

    private static float getInterpolatedNoise(float x, float z) {
        int intX = (int) x;
        int intZ = (int) z;
        float fX = x - intX;
        float fZ = z - intZ;

        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fX);
        float i2 = interpolate(v3, v4, fX);
        return interpolate(i1, i2, fZ);
    }

    private static float getSmoothNoise(int x, int z) {
        float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1) + getNoise(x + 1, z + 1)) / 16f;
        float sides = (getNoise(x, z - 1) + getNoise(x, z + 1) + getNoise(x - 1, z) + getNoise(x + 1, z)) / 8f;
        float middle = getNoise(x, z) / 4f;
        return corners + sides + middle;
    }

    private static float interpolate(float a, float b, float blend) {
        double theta = blend * Math.PI;
        float f = (1f - (float) Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private static synchronized float getNoise(int x, int z) {
        random.setSeed((x * 3252) + (z * 325176) + seed);
        return random.nextFloat() * 2f - 1f;
    }

}
