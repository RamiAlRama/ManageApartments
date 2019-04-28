package rr.domain;

import java.io.Serializable;
import java.time.LocalDate;

import rr.dao.ManageApt;

public class RentRecord implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long idCustomer;
	private String aptName;
	private LocalDate rentDate;
	private int nDays;
	private LocalDate returnDateEST;
	private LocalDate returnDateACT;
	private double totalCost;
	private int damages;

	public RentRecord(Long idCustomer, String aptName, LocalDate rentDate, int nDays, LocalDate returnDateEST) {
		this.idCustomer = idCustomer;
		this.aptName = aptName;
		this.rentDate = rentDate;
		this.nDays = nDays;
		this.returnDateEST = returnDateEST;

	}

	public Long getIDCutomer() {
		return idCustomer;
	}

	public void setIDCutomer(Long idCustomer) {
		this.idCustomer = idCustomer;
	}

	public String getAptName() {
		return aptName;
	}

	public void setAptNAme(String aptName) {
		this.aptName = aptName;
	}

	public LocalDate getRentDay() {
		return rentDate;
	}

	public void setRentDay(LocalDate rentDate) {
		this.rentDate = rentDate;
	}

	public int getnDays() {
		return nDays;
	}

	public void setnDays(int nDays) {
		this.nDays = nDays;
	}

	public LocalDate getReturnDateEST() {
		return returnDateEST;
	}

	public void setReturnDate(LocalDate returnDateEST) {
		this.returnDateEST = returnDateEST;

	}

	public LocalDate getReturnDateACT() {
		return returnDateACT;
	}

	public void setReturnDateACT(LocalDate returnDateACT) {
		this.returnDateACT = returnDateACT;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double cost) {
		this.totalCost = cost;
	}

	public int getDamages() {
		return damages;
	}

	public void setDamages(int damages) {
		this.damages = damages;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aptName == null) ? 0 : aptName.hashCode());
		result = prime * result + ((idCustomer == null) ? 0 : idCustomer.hashCode());
		result = prime * result + ((rentDate == null) ? 0 : rentDate.hashCode());
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
		if (!(obj instanceof RentRecord)) {
			return false;
		}
		RentRecord other = (RentRecord) obj;
		if (aptName == null) {
			if (other.aptName != null) {
				return false;
			}
		} else if (!aptName.equals(other.aptName)) {
			return false;
		}
		if (idCustomer == null) {
			if (other.idCustomer != null) {
				return false;
			}
		} else if (!idCustomer.equals(other.idCustomer)) {
			return false;
		}
		if (rentDate == null) {
			if (other.rentDate != null) {
				return false;
			}
		} else if (!rentDate.equals(other.rentDate)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "RentRecord | idCustomer = " + idCustomer + " | aptName = " + aptName + " | rentDate = " + rentDate
				+ " | nDays = " + nDays + " | returnDateEST = " + returnDateEST + " | returnDateACT = " + returnDateACT
				+ " | totalCost = " + totalCost + " | damages = " + damages;
	}

}
