package rr.domain;

import java.io.Serializable;

public class Address implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String street;
	private int nBuilding;
	private int nApt;

	public Address(String street, int nBuilding, int nApt) {
		this.street = street;
		this.nBuilding = nBuilding;
		this.nApt = nApt;
	}

	public String getStreet() {
		return street;
	}

	public int getnBuilding() {
		return nBuilding;
	}

	public int getnApt() {
		return nApt;
	}

	@Override
	public String toString() {
		return "Address | street = " + street + " | nBuilding = " + nBuilding + " | nApt = " + nApt;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + nApt;
		result = prime * result + nBuilding;
		result = prime * result + ((street == null) ? 0 : street.hashCode());
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
		if (!(obj instanceof Address)) {
			return false;
		}
		Address other = (Address) obj;
		if (nApt != other.nApt) {
			return false;
		}
		if (nBuilding != other.nBuilding) {
			return false;
		}
		if (street == null) {
			if (other.street != null) {
				return false;
			}
		} else if (!street.equals(other.street)) {
			return false;
		}
		return true;
	}
	
	

}
