package tcc.pairings;

public class DutyLeg extends Leg {
	private boolean deadHead;
	
	public boolean isDeadHead() {
		return deadHead;
	}

	public void setDeadHead(boolean deadHead) {
		this.deadHead = deadHead;
	}
	
	public DutyLeg(Leg leg) {
		this(leg, false);
	}

	public DutyLeg(Leg leg, boolean deadHead) {
		super(leg.getNumber(), leg.getFrom(), leg.getTo(), leg.getDeparture(), leg.getArrival(), leg.getTrack());
		this.deadHead = deadHead;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString());
		if (deadHead)
			sb.append("  ").append("DH");
		return sb.toString();
	}	
}
