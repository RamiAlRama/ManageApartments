package rr.dao;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import rr.dto.ApartmentReturnCode;
import rr.domain.Apartment;
import rr.domain.Customer;
import rr.domain.RentRecord;

public interface IManageApartments extends Serializable {
	ApartmentReturnCode addApt(Apartment apt);

	Apartment getApt(String aptName);

	ApartmentReturnCode addCustomer(Customer customer);

	ApartmentReturnCode rentApt(String aptName, long idCustomer, LocalDate rentDay, int rentDays, LocalDate returnDate);

	ApartmentReturnCode returnApt(String aptName, long idCustomer, LocalDate returnDate, int damages);

	List<Apartment> aptClear(LocalDate currentDate, int days);

	ApartmentReturnCode removeApt(String aptName);

	List<Customer> getAptCustomers(String aptName);

	List<Apartment> getCutomerApts(long idNumber);

	Stream<Apartment> getAllApartments();

	Stream<Customer> getAllCustomers();

	Stream<RentRecord> getAllRecords();

	List<String> getMostPopularAptNames();

	double getAptProfit(String modelName);

	List<String> getMostProfitAptNames();

	void save(String fileName);
	
	List<Apartment> getApatToReturn(LocalDate dayToReturn);

}
