package kilim.guava;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;

import kilim.Pausable;
import kilim.Task;
import kilim.guava.KilimCache.Reloadable;

/**
 * stress test of the kilim-guava-cache-integration.
 * all access is performed using the pausable getCache
 */
public class GuavaCacheDemo {
    
    public void run() {

        final Random random = new Random();
        int numTasks = 200;
        final int maxIters = 1000;
        final int maxKey = 1100;
        final int maxDelay = 100;
        int maxSize = 1000;
        final int maxWait = 100;
        int refresh = 10000;
        final int maxNever = 100;
        
        final KilimCache<Integer,Double> loader = new KilimCache(
                CacheBuilder.newBuilder()
                        .refreshAfterWrite(refresh,TimeUnit.MICROSECONDS)
                        .maximumSize(maxSize));
        loader.register(new Reloadable<Integer, Double>() {
			@Override
			public Double reload(Integer key, Object prev) throws Pausable {
                if (key < maxNever & prev != null)
                    return null;
                Task.sleep(random.nextInt(maxDelay));
                return key+random.nextDouble();
			}
		});
        Task tasks[] = new Task[numTasks];
        for (int jj=0; jj < numTasks; jj++) {
            final int ktask = jj;
            tasks[jj] = new Task() {
            	@Override
            	public void execute() throws Pausable, Exception {
            		int numIters = random.nextInt(maxIters);
                    double sum = 0;
                    for (int ii=1; ii <= numIters; ii++) {
                        Task.sleep(random.nextInt(maxWait));
                        int key = random.nextInt(maxKey);
                        sum += loader.get(key) - key;
                        if (ii==numIters) {
                            System.out.format("cache: %4d -> %8.3f\n",ktask,sum/numIters);
                        }
                    }
                
            	}
			};
			tasks[jj].start();
        }
        
        for (int jj=0; jj < numTasks; jj++) {
        	tasks[jj].joinb();
        }

    }

}
