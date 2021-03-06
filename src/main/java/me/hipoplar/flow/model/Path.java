package me.hipoplar.flow.model;

public class Path {
	private String from;
	private String to;

	public Path() {
		super();
	}

	public Path(String from, String to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public String toString() {
		return "Path - " + from + " => " + to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
}
