package rr.domain;

import java.io.Serializable;

import rr.dto.StateApt;

public class Apartment implements Comparable<Apartment>, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nameApt;
	private int nRoom;
	private Address address;
	private Properties propert;
	private boolean inUse;
	private boolean flRemoved;
	private double costPerDay;
	private StateApt state;

	public Apartment(String nameApt, int nRoom, Address address, double costPerDay, Properties propert) {
		this.nameApt = nameApt;
		this.nRoom = nRoom;
		this.address = address;
		this.costPerDay = costPerDay;
		this.propert = propert;
		state = StateApt.EXELLENT;
		inUse = false;
		flRemoved = false;
	}

	public void setPropert(Properties propert) {
		this.propert = propert;
	}

	public String getNameApt() {
		return nameApt;
	}

	public int getnRoom() {
		return nRoom;
	}

	public Address getAddress() {
		return address;
	}

	public Properties getPropert() {
		return propert;
	}

	@Override
	public String toString() {
		return "Apartment | nameApt = " + nameApt + " | nRoom = " + nRoom + " | address = " + address + " | propert = "
				+ propert;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		result = prime * result + ((nameApt == null) ? 0 : nameApt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Apartment)) {
			return false;
		}
		Apartment other = (Apartment) obj;
		if (address == null) {
			if (other.address != null) {
				return false;
			}
		} else if (!address.equals(other.address)) {
			return false;
		}
		if (nameApt == null) {
			if (other.nameApt != null) {
				return false;
			}
		} else if (!nameApt.equals(other.nameApt)) {
			return false;
		}
		return true;
	}

	public boolean isInUse() {

		return this.inUse;
	}

	public boolean isFlRemoved() {
		return flRemoved;
	}

	public void setFlRemoved(boolean flRemoved) {
		this.flRemoved = flRemoved;

	}

	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}

	public double getCostPerDay() {
		return costPerDay;
	}

	public void setCostPerDay(double costPerDay) {
		this.costPerDay = costPerDay;
	}

	public StateApt getState() {
		return state;
	}

	public void setState(StateApt state) {
		this.state = state;
	}

	@Override
	public int compareTo(Apartment o) {

		return this.nameApt.compareTo(o.nameApt);
	}

}
