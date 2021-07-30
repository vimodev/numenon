package utility;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Global {

    public static long WINDOW_IDENTIFIER;

    public static final Lock terrain_queue_mutex = new ReentrantLock(true);
    public static final Lock entity_queue_mutex = new ReentrantLock(true);

}
