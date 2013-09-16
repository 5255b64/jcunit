package com.github.dakusui.jcunit.generators;

import java.util.Map;

public class SimpleTestArrayGenerator<T, U> extends BaseTestArrayGenerator<T, U>{
	@Override
	public void init(Map<T, U[]> domains) {
		super.init(domains);
		if (this.domains == null) throw new NullPointerException();
		assert this.size < 0;
		assert this.cur < 0;
		
		this.size = 1;
		for (T f : this.domains.keySet()) {
			this.size += Math.max(0, this.domains.get(f).length - 1); 
		}
		this.cur = 0;
	}

	@Override
	public int getIndex(T key, long cur) {
		////
		// Initialize the returned map with the default values.
		int ret = 0;
		////
		// If cur is 0, the returned value should always be 0.
		if (cur == 0) return 0;
		cur--;
		for (T f : this.domains.keySet()) {
			long index = cur;
			U[] d = domains.get(f);
			if ((cur -= (d.length - 1)) < 0) {
				if (key.equals(f)) ret = (int)(index + 1) ;
				break;
			}
		}
		return ret;
	}
}