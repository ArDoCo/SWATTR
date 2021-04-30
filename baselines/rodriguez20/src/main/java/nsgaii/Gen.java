package nsgaii;

public class Gen {

	private int reqId;
	private int srcCodeId;
	private double cosineSimilarity;
	private double JaccardiSimilarity;

	public int getReqId() {
		return reqId;
	}

	public void setReqId(int reqId) {
		this.reqId = reqId;
	}

	public int getSrcCodeId() {
		return srcCodeId;
	}

	public void setSrcCodeId(int srcCodeId) {
		this.srcCodeId = srcCodeId;
	}

	public double getCosineSimilarity() {
		return cosineSimilarity;
	}

	public void setCosineSimilarity(double cosineSimilarity) {
		this.cosineSimilarity = cosineSimilarity;
	}

	public double getJaccardiSimilarity() {
		return JaccardiSimilarity;
	}

	public void setJaccardiSimilarity(double jaccardiSimilarity) {
		JaccardiSimilarity = jaccardiSimilarity;
	}

}
