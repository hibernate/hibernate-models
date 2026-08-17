package org.hibernate.models.accessor.tck.tests.beans;

public class FinalFieldBean {

	private String alpha;
	private final int beta;
	private String gamma;

	public FinalFieldBean() {
		this.beta = 0;
	}

	public FinalFieldBean(int beta) {
		this.beta = beta;
	}

	public String getAlpha() {
		return alpha;
	}

	public void setAlpha(String alpha) {
		this.alpha = alpha;
	}

	public int getBeta() {
		return beta;
	}

	public String getGamma() {
		return gamma;
	}

	public void setGamma(String gamma) {
		this.gamma = gamma;
	}
}
