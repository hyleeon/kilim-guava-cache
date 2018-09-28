# kilim guava cache

Guava cache with kilim 1.x

`KilimCache` is a thin wrapper around a Guava `LoadingCache` to provide fully asynchronous lookup and loading

# Example Usage

from `GuavaCacheDemo.java`:
```
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
```
End...
