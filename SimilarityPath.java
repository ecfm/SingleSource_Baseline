package icde.dblp;

//VLDB
import java.util.*;

import share.DoubleInt;

public class SimilarityPath {

	public ArrayList<NodePair> path = new ArrayList<NodePair>();
	// decided by the end node of a random walk step
	// >0: in-links
	// <0: out-link
	public ArrayList<Integer> edgeDirection = new ArrayList<Integer>();
	public double probability;

	public double getSimRankProb(int[] inSize) {
		double prob = 1;
		int length = path.size();
		for (int i = 0; i < length - 1; i++) {
			NodePair pair = path.get(length - 1 - i);
			prob = prob / (inSize[pair.slave] * inSize[pair.master]);

		}
		return prob * Math.pow(Setting.C, path.size() - 1);
	}

	public double getPRankProb(int[] inSize, int[] outSize) {
		double prob = 1;
		int length = path.size();
		for (int i = 0; i < length - 1; i++) {
			NodePair pair = path.get(length - 1 - i);
			if (edgeDirection.get(length - 2 - i) > 0)
				prob = Setting.PRank_lambda * prob
						/ (inSize[pair.slave] * inSize[pair.master]);
			else
				prob = (1 - Setting.PRank_lambda) * prob
						/ (outSize[pair.slave] * outSize[pair.master]);
		}
		return prob * Math.pow(Setting.C, path.size() - 1);
	}

	public double getSimRankAllProb(int[] inSize, int[] outSize) {
		double prob = 1;
		int length = path.size();
		for (int i = 0; i < length - 1; i++) {
			NodePair pair = path.get(length - 1 - i);
			prob = prob
					/ (inSize[pair.slave] * inSize[pair.master] + outSize[pair.slave]
							* outSize[pair.master]);
		}
		return prob * Math.pow(Setting.C, path.size() - 1);
	}

	// order by desc
	public int compareTo(SimilarityPath sp) {
		if (probability > sp.probability)
			return -1;
		else if (probability == sp.probability)
			return 0;
		else
			return 1;
	}

}
