package engine.world;

public class IndicesJob implements Runnable {

    private int resolution;
    private int[] indices;
    private int offset;
    private int numberOfThreads;

    public IndicesJob(int resolution, int[] indices, int offset, int numberOfThreads) {
        this.resolution = resolution;
        this.indices = indices;
        this.offset = offset;
        this.numberOfThreads = numberOfThreads;
    }

    @Override
    public void run() {
        for(int gz=offset;gz<resolution-1; gz += numberOfThreads){
            int pointer = 6 * gz * (resolution - 1);
            for(int gx=0;gx<resolution-1;gx++){
                int topLeft = (gz*resolution)+gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz+1)*resolution)+gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
    }

}
