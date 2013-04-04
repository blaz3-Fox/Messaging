/**
 * 
 */
package hchi590.nameserver;

/**
 * @author hchi590 Determines the index of the address to return. Simply loops
 *         and returns the next index as one is requested.
 * 
 */
public class Counter {

	private int _counter;
	private int _max = -1;

	private static Counter _instance;

	protected Counter() {
		_counter = 0;
	}

	public synchronized static Counter getInstance() {
		if (_instance == null) {
			_instance = new Counter();
		}
		return _instance;
	}

	public void setMax(int max) throws Exception {
		if (_max == -1) {
			_max = max - 1;
		} else {
			throw new Exception();
		}
	}

	private synchronized void increment() {
		if (_counter < _max) {
			_counter++;
		} else {
			_counter = 0;
		}
	}

	public synchronized int getCounter() {
		int counter = _counter;
		increment();
		return counter;
	}
}
