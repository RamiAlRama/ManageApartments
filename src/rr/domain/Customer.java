package rr.domain;

import java.io.Serializable;
import java.time.LocalDate;

public class Customer implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String firstName;
	private String lastName;
	private Long idNumber;
	private LocalDate dateBirth;
	private char sex;
	private Long telNumber;
	private String email;

	public Customer(String firstName, String lastName, Long idNumber, LocalDate dateBirth, char sex, long l, String email) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.idNumber = idNumber;
		this.dateBirth = dateBirth;
		this.sex = sex;
		this.telNumber = l;
		this.email = email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public Long getIdNumber() {
		return idNumber;
	}

	public LocalDate getDateBirth() {
		return dateBirth;
	}

	public char getSex() {
		return sex;
	}

	public Long getTelNumber() {
		return telNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setTelNumber(Long telNumber) {
		this.telNumber = telNumber;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Customer | firstName = " + firstName + " | lastName = " + lastName + " | idNumber = " + idNumber
				+ " | dateBirth = " + dateBirth + " | sex = " + sex + " | telNumber = " + telNumber + " | email = "
				+ email;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idNumber == null) ? 0 : idNumber.hashCode());
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
		if (!(obj instanceof Customer)) {
			return false;
		}
		Customer other = (Customer) obj;
		if (idNumber == null) {
			if (other.idNumber != null) {
				return false;
			}
		} else if (!idNumber.equals(other.idNumber)) {
			return false;
		}
		return true;
	}
	
	

}
