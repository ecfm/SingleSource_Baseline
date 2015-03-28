package icde.dblp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import share.DoubleInt;

public class SimMap implements Comparable<SimMap> {

	public HashMap<Integer, Double> EM;
	public int master;

	public SimMap(int root) {
		master = root;
		EM = new HashMap<Integer, Double>();
	}

	// order by desc
	public int compareTo(SimMap em) {
		if (master > em.master)
			return -1;
		else if (master == em.master)
			return 0;
		else
			return 1;
	}

	public static SimMap prioritize(SimMap map) {
		Iterator<Map.Entry<Integer, Double>> it = map.EM.entrySet().iterator();
		ArrayList<DoubleInt> CL = new ArrayList<DoubleInt>();
		while (it.hasNext()) {
			Map.Entry<Integer, Double> entry = it.next();
			CL.add(new DoubleInt(entry.getValue(), entry.getKey()));
		}
		Collections.sort(CL);
		SimMap newmap = new SimMap(map.master);
		for (int i = 0; i < Math.min(CL.size(), Setting.priority); i++) {
			newmap.EM.put(CL.get(i).slave, CL.get(i).master);
		}
		return newmap;
	}
}
