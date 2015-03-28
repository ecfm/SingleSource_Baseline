package icde.dblp;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import share.DoubleInt;
import share.MyIO;

public class SimRank_WeightedPrioTopSimSM {

	/**
	 * @param args
	 */
	static int median;
	static PrintWriter pw;
	static int[] no;
	static long count;
	static long cln;

	public static void main(String[] args) throws Exception {

		Setting.initializeWeightedDBLPAuthor();

		String[] names = (String[]) MyIO.readObj("X:/Papers/ICDE2012/Experiments/Dataset/DBLPAuthor/AllAuthorName");
		int v = Arrays.binarySearch(names, "Christos Faloutsos");

		ArrayList<DoubleInt> result = topk(v);
		for (int i = 0; i < result.size(); i++) {
			System.out.println(names[result.get(i).slave] + " " + result.get(i).master);
		}

		Random ran = new Random();
		double time1 = 0;
		double time2 = 0;
		ArrayList<Long> list = new ArrayList<Long>();
		ArrayList<Long> num = new ArrayList<Long>();
		ArrayList<Long> counts = new ArrayList<Long>();
		ArrayList<Long> clns = new ArrayList<Long>();

		for (int q = 0; q < 50; q++) {
			long t1 = System.currentTimeMillis();
			v = ran.nextInt(Setting.size);
			v = 6;
			topk(v);
			long t2 = System.currentTimeMillis();
			long time = t2 - t1;
			time1 += time;
			list.add(time);
			System.out.println(q + " time: " + time);
			long nos = 0;
			for (int i : no)
				nos += i;
			num.add(nos);
			counts.add(count);
			clns.add(cln);
		}
		System.out.println("Ave time: " + (time1 / 50) + " " + (time2 / 50));
		Collections.sort(list);
		System.out.println(list.get(0) + " " + list.get(list.size() - 1) + " " + list.get(list.size() / 2));
		System.out.println(num.get(0) + " " + num.get(num.size() - 1) + " " + num.get(num.size() / 2));
		Collections.sort(num);
		System.out.println("unique:" + num.get(0) + " " + num.get(num.size() - 1) + " " + num.get(num.size() / 2));
		System.out.println(getAvg(num));
		Collections.sort(counts);
		System.out.println("count:" + counts.get(0) + " " + counts.get(counts.size() - 1) + " " + counts.get(counts.size() / 2));
		System.out.println(getAvg(counts));
		Collections.sort(clns);
		System.out.println("candidate:" + clns.get(0) + " " + clns.get(clns.size() - 1) + " " + clns.get(clns.size() / 2));
		System.out.println(getAvg(clns));

	}

	public static void setVisit(int a) {
		count++;
		if (no[a] == 0)
			no[a] = 1;
	}

	public static long getAvg(ArrayList<Long> num) {
		long snum = 0;
		for (int i = 0; i < num.size(); i++)
			snum += num.get(i);
		return snum / num.size();
	}

	public static ArrayList<DoubleInt> topk(int v) {
		count = 0;
		no = new int[Setting.size];
		int n = Setting.MAX;
		TraverseTree tree = new TraverseTree(Setting.OUTAdjacency, Setting.INAdjacency);
		tree.buildPrioSimRankTree(v, n);

		ArrayList<SimMap> currentMap = new ArrayList<SimMap>();
		for (int l = n - 1; l >= 0; l--) {
			int[] neighbors = tree.neighbors[l];
			if (l == n - 1) {
				for (int t : neighbors) {
					setVisit(t);
					SimMap em = new SimMap(t);
					for (int xi = 0; xi < Setting.INAdjacency[t].length; xi++) {
						int i = Setting.INAdjacency[t][xi];
						setVisit(i);
						for (int xj = 0; xj < Setting.OUTAdjacency[i].length; xj++) {
							int j = Setting.OUTAdjacency[i][xj];
							setVisit(j);
							Double value = em.EM.get(j);
							int[] debug = Setting.INAdjacency[j];
							Arrays.sort(debug);
							int iInJ = debug.length - 1 - Arrays.binarySearch(debug, i);
							double P = Setting.C * Setting.OUTProb[t][xi] * Setting.OUTProb[j][iInJ];
							if (value == null) {
								em.EM.put(j, P);
							} else
								em.EM.put(j, P + value);
						}
					}
					em = SimMap.prioritize(em);
					em.EM.put(t, 1.);
					currentMap.add(em);
				}
			} else {
				Collections.sort(currentMap);
				ArrayList<SimMap> mergeMap = new ArrayList<SimMap>();
				for (int t : neighbors) {
					setVisit(t);
					SimMap em = new SimMap(t);

					for (int xi = 0; xi < Setting.INAdjacency[t].length; xi++) {
						int i = Setting.INAdjacency[t][xi];
						setVisit(i);
						int idx = Collections.binarySearch(currentMap, new SimMap(i));
						if (idx < 0){
							//System.out.println("Cannot find in current map!");
						}
						else {
							Iterator<Map.Entry<Integer, Double>> it = currentMap.get(idx).EM.entrySet().iterator();
							while (it.hasNext()) {
								Map.Entry<Integer, Double> entry = it.next();
								for (int xj = 0; xj < Setting.OUTAdjacency[entry.getKey()].length; xj++) {
									int j = Setting.OUTAdjacency[entry.getKey()][xj];
									setVisit(j);
									Double value = em.EM.get(j);
									int[] debug = Setting.INAdjacency[j];
									Arrays.sort(debug);
									int iInJ = debug.length - 1 - Arrays.binarySearch(debug, entry.getKey());
									double P = entry.getValue() * Setting.C * Setting.OUTProb[t][xi] * Setting.OUTProb[j][iInJ];
									if (value == null) {
										em.EM.put(j, P);
									} else
										em.EM.put(j, P + value);
								}
							}
						}
					}

					if (l > 0)
						em = SimMap.prioritize(em);
					em.EM.put(t, 1.);
					mergeMap.add(em);
				}
				currentMap = mergeMap;
			}
		}

		int idx = Collections.binarySearch(currentMap, new SimMap(v));
		if (idx < 0) {
			//System.out.println("Cannot find in current map!");
			return null;
		} else {
			Iterator<Map.Entry<Integer, Double>> it = currentMap.get(idx).EM.entrySet().iterator();
			ArrayList<DoubleInt> CL = new ArrayList<DoubleInt>();
			while (it.hasNext()) {
				Map.Entry<Integer, Double> entry = it.next();
				CL.add(new DoubleInt(entry.getValue(), entry.getKey()));
			}
			Collections.sort(CL);
			ArrayList<DoubleInt> L = new ArrayList<DoubleInt>();

			System.out.println("------------- " + n + " ------------- Candidates: " + CL.size());
			for (int i = 0; i < Math.min(CL.size() - 1, Setting.k); i++) {
				L.add(CL.get(i + 1));
				System.out.println(L.get(i).slave + " " + L.get(i).master);
			}
			cln = CL.size();
			return L;
		}
	}

	public static double SimRankUB(int n) {
		return Math.pow(Setting.C / median, n + 1);
	}

}
