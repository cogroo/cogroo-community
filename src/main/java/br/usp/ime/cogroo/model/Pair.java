package br.usp.ime.cogroo.model;

public final class Pair<A, B> {
    private A a;
    private B b;

    public A getA() {
		return a;
	}

	public void setA(A a) {
		this.a = a;
	}

	public B getB() {
		return b;
	}

	public void setB(B b) {
		this.b = b;
	}

	public Pair(A a, B b) {
      this.a = a;
      this.b = b;
    }

    public String toString() { return "["+a+"/"+b+"]"; }
}
