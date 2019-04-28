package rr.tests;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import rr.dao.IManageApartments;
import rr.dao.ManageApt;
import rr.domain.Address;
import rr.domain.Apartment;
import rr.domain.Customer;
import rr.domain.RentRecord;
import rr.dto.ApartmentReturnCode;
import rr.dto.StateApt;

public class ManagerAptTests implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Address ADDRESS1 = new Address("Pinsker", 2, 5);
	private static final Address ADDRESS2 = new Address("Ben Yehuda", 175, 302);
	private static final Address ADDRESS3 = new Address("Allenby", 36, 11);
	private static final int RENT_DAYS1 = 5;
	private static final LocalDate RENT_DATE1 = LocalDate.of(2019, 4, 20);
	private static final String APT_NAME1 = "Allenby Bau House";
	private static final String APT_NAME2 = "Cosy Pinsker";
	private static final String APT_NAME3 = "Ben Yehuda Eco";
	private static final long ID1 = 11111l;
	private static final long ID2 = 2222l;

	private static final LocalDate CURRENT_DATE = LocalDate.of(2020, 2, 1);
	private static final LocalDate RETURN_DATE_EST = RENT_DATE1.plusDays(RENT_DAYS1);
	private static final LocalDate RETURN_DATE_ACT = RENT_DATE1.plusDays(RENT_DAYS1);
	private static final int CLEAR_DAYS = 31;

	IManageApartments company = ManageApt.getRentCompany();

	Apartment apt1 = new Apartment(APT_NAME1, 3, ADDRESS3, 200, null);
	Apartment apt2 = new Apartment(APT_NAME2, 3, ADDRESS1, 150, null);
	Apartment apt3 = new Apartment(APT_NAME3, 3, ADDRESS2, 100, null);

	Customer customer1 = new Customer("Rami", "Rosenblium", ID1, LocalDate.of(1988, 4, 10), 'M', 538890838,
			"rosenblium.rs@gnmail.com");
	Customer customer2 = new Customer("Alex", "Waxman", ID2, LocalDate.of(1987, 7, 1), 'M', 523232293,
			"alex@tlv2.rent");
	RentRecord rentRecord;

	@Before
	public void setUp() throws Exception {

		company = ManageApt.restoreFromFile();
		company.save("data_rent_apt");
		company.addApt(apt1);
		company.addCustomer(customer1);
		company.rentApt(apt1.getNameApt(), customer1.getIdNumber(), RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST);
		rentRecord = new RentRecord(customer1.getIdNumber(), apt1.getNameApt(), RENT_DATE1, RENT_DAYS1,
				RETURN_DATE_EST);

	}

	@Test
	public void testAddApt() {
		assertEquals(ApartmentReturnCode.APT_EXISTS, company.addApt(apt1));
		assertEquals(ApartmentReturnCode.OK, company.addApt(apt2));
	}

	@Test
	public void testGetApt() {
		assertNull(company.getApt(apt3.getNameApt()));
		assertEquals(apt1, company.getApt(apt1.getNameApt()));
	}

	@Test
	public void testRentApt() {
		assertEquals(ApartmentReturnCode.APT_IN_USE,
				company.rentApt(apt1.getNameApt(), customer1.getIdNumber(), RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST));
		assertEquals(ApartmentReturnCode.NO_APT,
				company.rentApt(apt2.getNameApt(), customer1.getIdNumber(), RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST));
		company.addApt(apt2);
		assertEquals(ApartmentReturnCode.NO_CUSTOMER,
				company.rentApt(apt2.getNameApt(), customer2.getIdNumber(), RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST));
		company.addCustomer(customer2);
		assertEquals(ApartmentReturnCode.OK,
				company.rentApt(apt2.getNameApt(), customer2.getIdNumber(), RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST));
		RentRecord record1 = getRecord(apt1.getNameApt());
		assertEquals(customer1.getIdNumber(), record1.getIDCutomer());
		assertTrue(company.getApt(record1.getAptName()).isInUse());
		assertEquals(rentRecord, record1);
	}

	@Test
	public void testReturnAptNoDamagesNoAdditionalCost() {
		company.returnApt(apt1.getNameApt(), customer1.getIdNumber(), RETURN_DATE_ACT, 1);
		assertFalse(apt1.isInUse());
		assertEquals(StateApt.GOOD, apt1.getState());
		assertFalse(apt1.isFlRemoved());
		rentRecord.setDamages(1);
		rentRecord.setReturnDateACT(RETURN_DATE_ACT);
		rentRecord.setTotalCost(RENT_DAYS1 * apt1.getCostPerDay());
		RentRecord actual = getRecord(apt1.getNameApt());
		assertEquals(rentRecord, actual);
		assertEquals(rentRecord.getDamages(), actual.getDamages());
		assertEquals(rentRecord.getReturnDateACT(), actual.getReturnDateACT());
		assertEquals(rentRecord.getTotalCost(), actual.getTotalCost(), 0.01);
		assertEquals(StateApt.GOOD, company.getApt(actual.getAptName()).getState());
	}

	private RentRecord getRecord(String aptName) {
		return company.getAllRecords().filter(r -> r.getAptName().equals(aptName)).findFirst().orElse(null);
	}

	@Test
	public void testRemoveApt() {
		assertEquals(ApartmentReturnCode.APT_IN_USE, company.removeApt(apt1.getNameApt()));
		assertEquals(ApartmentReturnCode.NO_APT, company.removeApt(apt2.getNameApt()));
		// company.returnApt(apt1.getNameApt(), customer1.getIdNumber(), RETURN_DATE,
		// 100, 0);
		// assertEquals(ApartmentReturnCode.OK, company.removeApt(apt1.getNameApt()));
		// assertTrue(company.getApt(apt1.getNameApt()).isFlRemoved());
	}

	@Test
	public void testGetAptCustomers() {

		company.getAptCustomers(apt1.getNameApt()).forEach(d -> assertEquals(customer1, d));
		assertNull(company.getAptCustomers(apt2.getNameApt()));
	}

	@Test
	public void testGetCutomerApts() {
		company.getCutomerApts(customer1.getIdNumber()).forEach(d -> assertEquals(apt1, d));
		assertNull(company.getCutomerApts(customer2.getIdNumber()));
	}

	@Test
	public void testGetAllApartments() {
		company.addApt(apt2);
		company.addApt(apt3);
		Object[] expecteds = { apt1, apt3, apt2 };
		Object[] actuals = company.getAllApartments().sorted((c1, c2) -> c1.getNameApt().compareTo(c2.getNameApt()))
				.toArray();
		assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void testGetAllCustomers() {
		company.addCustomer(customer2);

		Object[] expecteds = { customer1, customer2 };
		Object[] actuals = company.getAllCustomers().sorted((c1, c2) -> c1.getLastName().compareTo(c2.getLastName()))
				.toArray();
		assertArrayEquals(expecteds, actuals);
	}

	@Test
	public void testGetAllRecords() {
		company.getAllRecords().forEach(r -> assertEquals(rentRecord, r));
	}

	@Test
	public void testGetMostPopularAptNames() {
		setupStatistics();
		// assumed apt1 and apt2 most popular
		String[] expecteds = { apt1.getNameApt(), apt2.getNameApt() };
		List<String> actuals = company.getMostPopularAptNames();
		actuals.sort(String::compareTo);
		assertArrayEquals(expecteds, actuals.toArray());
	}

	private void setupStatistics() {
		company.returnApt(apt1.getNameApt(), customer1.getIdNumber(), RETURN_DATE_ACT, 0);
		rentReturn(apt1.getNameApt(), 2);
		company.addApt(apt2);
		rentReturn(apt2.getNameApt(), 3);

	}

	private void rentReturn(String aptName, int n) {
		for (int i = 0; i < n; i++) {
			company.rentApt(aptName, customer1.getIdNumber(), RENT_DATE1, RENT_DAYS1, RETURN_DATE_ACT);
			company.returnApt(aptName, customer1.getIdNumber(), RETURN_DATE_ACT, 0);
		}

	}

	@Test
	public void testGetAptProfit() {
		setupStatistics();
		assertEquals(200 * 5 * 3, company.getAptProfit(apt1.getNameApt()), 0.01);
		assertEquals(150 * 5 * 3, company.getAptProfit(apt2.getNameApt()), 0.01);
	}

	@Test
	public void testGetMostProfitAptNames() {
		setupStatistics();
		String[] expecteds = { apt1.getNameApt() };
		assertArrayEquals(expecteds, company.getMostProfitAptNames().toArray());
	}

	@Test
	public void testClear() {
		setUpClear();
		// assumed apt1 and apt2 are deleted.
		// apt3 is not deleted
		List<Apartment> aptActual = company.aptClear(CURRENT_DATE, CLEAR_DAYS);
		Apartment[] aptExpected = { apt1, apt2 };
		aptActual.sort((x, y) -> x.getNameApt().compareTo(y.getNameApt()));
		assertArrayEquals(aptExpected, aptActual.toArray());
		assertNull(company.getApt(apt1.getNameApt()));
		assertNull(company.getApt(apt2.getNameApt()));
		assertNull(getRecord(apt1.getNameApt()));
		assertNull(getRecord(apt2.getNameApt()));
		assertNotNull(company.getApt(apt3.getNameApt()));
		assertNotNull(getRecord(apt3.getNameApt()));
	}

	private void setUpClear() {
		company.returnApt(APT_NAME1, ID1, RETURN_DATE_ACT, 90);
		company.addApt(apt2);
		company.addApt(apt3);
		company.rentApt(APT_NAME2, ID1, RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST);
		company.rentApt(APT_NAME3, ID1, RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST);
		company.returnApt(APT_NAME2, ID1, RETURN_DATE_ACT, 0);
		company.removeApt(APT_NAME2);
		company.returnApt(APT_NAME3, ID1, RETURN_DATE_ACT, 0);

	}
	
	@Test
	public void testGetApatToReturn() {
		company.addApt(apt2);
		company.addApt(apt3);
		company.rentApt(APT_NAME2, ID1, RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST);
		company.rentApt(APT_NAME3, ID1, RENT_DATE1, RENT_DAYS1, RETURN_DATE_EST);
		Apartment[] expecteds = { apt1, apt3 , apt2};
		List<Apartment> actuals = company.getApatToReturn(RETURN_DATE_EST);
		actuals.sort(Apartment::compareTo);
		assertArrayEquals(expecteds, actuals.toArray());
		
	}
	
	
}
