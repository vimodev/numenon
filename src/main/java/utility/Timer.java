package utility;

/**
 * Timer to be used to determine fps and such
 * @Author Vincent Moonen
 */
public class Timer {

    private double alpha = 0.2;
    private double previousTime;
    private double fps = 0;

    public Timer() {
        init();
    }

    /**
     * Initializes the timer by looking at time of calling
     */
    public void init() {
        previousTime = ((double) System.nanoTime()) / (double) 1000000000L;
        fps = 0;
    }

    /**
     * Gets the delta time in seconds since last call or init() if no last call exists
     * @return delta time
     */
    public double dt() {
        double currentTime = ((double) System.nanoTime()) / (double) 1000000000L;
        double dt = currentTime - previousTime;
        previousTime = currentTime;
        fps = (1 - alpha) * fps + alpha / dt;
        return dt;
    }

    public double readDt() {
        double currentTime = ((double) System.nanoTime()) / (double) 1000000000L;
        double dt = currentTime - previousTime;
        return dt;
    }

    /**
     * Returns exponential average of calls of dt() per second
     * new_fps = (1 - a) * old_fps + (a  / dt)
     * So new fps value is 100-a*100=80% old fps and 20% most recent dt()
     * @return calls per second of dt()
     */
    public double fps() {
        return fps;
    }

}
