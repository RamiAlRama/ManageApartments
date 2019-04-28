package rr.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import rr.domain.Apartment;
import rr.domain.Customer;
import rr.domain.RentRecord;
import rr.dto.ApartmentReturnCode;
import rr.dto.StateApt;

public class ManageApt extends AbstractManageApt implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_FILE_NAME = "data_rent_apt";
	private HashMap<String, Apartment> apts;
	private HashMap<Long, Customer> customers;
	private HashMap<String, List<RentRecord>> aptRecords;
	private HashMap<Long, List<RentRecord>> cutomerRecords;
	private TreeMap<LocalDate, List<RentRecord>> returnedApts;

	private ManageApt() {
		apts = new HashMap<>();
		customers = new HashMap<>();
		aptRecords = new HashMap<>();
		cutomerRecords = new HashMap<>();
		returnedApts = new TreeMap<>();

	}

	public static ManageApt getRentCompany() { // #3

		return new ManageApt();
	}

	public static ManageApt restoreFromFile() {
		return restoreFromFile(DEFAULT_FILE_NAME);

	}

	public static ManageApt restoreFromFile(String fileName) {
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
			ManageApt rentCompany = (ManageApt) in.readObject();

			return rentCompany;
		} catch (FileNotFoundException e) {
			System.out.println("There is no such a file like : " + fileName);

		} catch (IOException e1) {

			e1.printStackTrace();
		} catch (ClassNotFoundException e) {

			e.printStackTrace();
		}
		return ManageApt.getRentCompany();

	}

	@Override
	public ApartmentReturnCode addApt(Apartment apt) {
		return apts.putIfAbsent(apt.getNameApt(), apt) == null ? ApartmentReturnCode.OK
				: ApartmentReturnCode.APT_EXISTS;
	}

	@Override
	public Apartment getApt(String aptName) {
		return apts.get(aptName);
	}

	@Override
	public ApartmentReturnCode rentApt(String aptName, long idCutomer, LocalDate rentDate, int rentDays,
			LocalDate returnDateEST) {
		ApartmentReturnCode code = checkRentApt(aptName, idCutomer);
		if (code == ApartmentReturnCode.OK) {
			RentRecord record = new RentRecord(idCutomer, aptName, rentDate, rentDays, returnDateEST);
			double estimCost = calcEstimateCost(aptName, rentDate, returnDateEST);
			record.setTotalCost(estimCost);
			addAptRecords(record);
			addCustomerRecords(record);
			setInUse(record);
		}
		return code;
	}

	@Override
	public ApartmentReturnCode returnApt(String aptName, long idCutomer, LocalDate returnDateACT, int damages) {
		RentRecord record = getRentRecord(aptName, idCutomer);
		ApartmentReturnCode code = checkReturnApt(aptName, idCutomer, returnDateACT, record);

		if (code == ApartmentReturnCode.OK) {
			Apartment apt = aptSettings(aptName, damages);
			record.setReturnDateACT(returnDateACT);
			record.setDamages(damages);
			double actualCost = calcActualCosts(apt.getNameApt(), record.getRentDay(), returnDateACT);
			record.setTotalCost(actualCost);
			List<RentRecord> records = returnedApts.get(returnDateACT);
			if (records == null) {
				records = new ArrayList<>();
				returnedApts.put(returnDateACT, records);
			}
			records.add(record);
		}
		return code;
	}

	private Apartment aptSettings(String aptName, int damages) {
		Apartment apt = apts.get(aptName);
		apt.setInUse(false);
		if (damages > 0 && damages <= 10) {
			apt.setState(StateApt.GOOD);
		}
		if (damages > 10 && damages <= 30) {
			apt.setState(StateApt.BAD);
		}
		if (damages > 30) {
			apt.setFlRemoved(true);
		}
		return apt;
	}

	private double calcActualCosts(String aptName, LocalDate rentDate, LocalDate returnDate) {
		double costPerDay = apts.get(aptName).getCostPerDay();

		return costPerDay * (ChronoUnit.DAYS.between(rentDate, returnDate));

	}

	private ApartmentReturnCode checkReturnApt(String aptName, long idCutomer, LocalDate returnDateACT,
			RentRecord rentRecord) {
		if (customers.get(idCutomer) == null) {
			return ApartmentReturnCode.NO_CUSTOMER;
		}
		if (rentRecord == null) {
			return ApartmentReturnCode.APT_NOT_RENTED;
		}
		if (returnDateACT.isBefore(rentRecord.getReturnDateEST())) {
			return ApartmentReturnCode.RETURN_DATE_WRONG;
		}
		return ApartmentReturnCode.OK;
	}

	private RentRecord getRentRecord(String aptName, long idCustomer) {
		List<RentRecord> records = aptRecords.get(aptName);
		return records == null ? null
				: records.stream().filter(r -> r.getReturnDateACT() == null && r.getIDCutomer() == idCustomer)
						.findFirst().orElse(null);
	}

	public double calcEstimateCost(String aptName, LocalDate rentDate, LocalDate returnDate) {
		double costPerDay = apts.get(aptName).getCostPerDay();

		return costPerDay * (ChronoUnit.DAYS.between(rentDate, returnDate));
	}

	private void addAptRecords(RentRecord record) {
		String aptName = record.getAptName();
		List<RentRecord> records = aptRecords.get(aptName);
		if (records == null) {
			records = new ArrayList<>();
			aptRecords.put(aptName, records);
		}
		records.add(record);

	}

	private void addCustomerRecords(RentRecord record) {
		long licenseId = record.getIDCutomer();
		List<RentRecord> records = cutomerRecords.get(licenseId);
		if (records == null) {
			records = new ArrayList<>();
			cutomerRecords.put(licenseId, records);
		}
		records.add(record);

	}

	private void setInUse(RentRecord record) {
		Apartment apt = apts.get(record.getAptName());
		apt.setInUse(true);

	}

	private ApartmentReturnCode checkRentApt(String aptName, Long idCustomer) {
		Apartment apt = apts.get(aptName);
		if (apt == null || apt.isFlRemoved()) {
			return ApartmentReturnCode.NO_APT;
		}
		if (apt.isInUse()) {
			return ApartmentReturnCode.APT_IN_USE;
		}
		if (customers.get(idCustomer) == null) {
			return ApartmentReturnCode.NO_CUSTOMER;
		}
		return ApartmentReturnCode.OK;
	}

	@Override
	public ApartmentReturnCode removeApt(String aptName) {
		Apartment apt = apts.get(aptName);
		if (apt == null) {
			return ApartmentReturnCode.NO_APT;
		}
		if (apt.isInUse()) {
			return ApartmentReturnCode.APT_IN_USE;
		}
		apt.setFlRemoved(true);
		return ApartmentReturnCode.OK;
	}

	@Override
	public List<Customer> getAptCustomers(String aptName) {
		List<RentRecord> records = aptRecords.get(aptName);
		if (records == null) {
			return null;
		}
		return records.stream().map(r -> r.getIDCutomer()).map(r -> customers.get(r)).distinct()
				.collect(Collectors.toList());

	}

	@Override
	public List<Apartment> getCutomerApts(long idNumber) {
		List<RentRecord> records = cutomerRecords.get(idNumber);
		if (records == null) {
			return null;
		}
		return records.stream().map(r -> r.getAptName()).map(rn -> apts.get(rn)).distinct()
				.collect(Collectors.toList());
	}

	@Override
	public Stream<Apartment> getAllApartments() {

		return apts.values().stream();
	}

	@Override
	public Stream<Customer> getAllCustomers() {

		return customers.values().stream();
	}

	@Override
	public Stream<RentRecord> getAllRecords() {

		return aptRecords.values().stream().flatMap(Collection::stream);
	}

	@Override
	public List<String> getMostPopularAptNames() {
		Map<String, Long> aptOccurrences = getAllRecords()
				.collect(Collectors.groupingBy(this::aptNameFromRecord, Collectors.counting()));
		long maxOccurrences = aptOccurrences.values().stream().max(Long::compare).orElse(0l);

		return aptOccurrences.entrySet().stream().filter(x -> x.getValue() == maxOccurrences).map(x -> x.getKey())
				.collect(Collectors.toList());
	}

	private String aptNameFromRecord(RentRecord r) {
		return apts.get(r.getAptName()).getNameApt();
	}

	@Override
	public double getAptProfit(String aptName) {
		return returnedApts.values().stream().flatMap(Collection::stream)
				.filter(r -> aptFromRecord(r).equals(apts.get(aptName))).mapToDouble(RentRecord::getTotalCost).sum();
	}

	@Override
	public List<String> getMostProfitAptNames() {
		Map<String, Double> modelOccurrences = returnedApts.values().stream().flatMap(Collection::stream).collect(
				Collectors.groupingBy(this::aptNameFromRecord, Collectors.summingDouble(RentRecord::getTotalCost)));

		double maxCost = modelOccurrences.values().stream().max(Double::compare).orElse(0.0);

		return modelOccurrences.entrySet().stream().filter(x -> x.getValue() == maxCost).map(x -> x.getKey())
				.collect(Collectors.toList());
	}

	@Override
	public void save(String fileName) {
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
			out.writeObject(this);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public List<Apartment> aptClear(LocalDate currentDate, int days) {
		Set<String> nameApts = returnedApts.headMap(currentDate.minusDays(days)).values().stream()
				.flatMap(l -> l.stream()).filter(r -> apts.get(r.getAptName()).isFlRemoved()).map(r -> r.getAptName())
				.collect(Collectors.toSet());
		List<Apartment> res = apts.entrySet().stream().filter(e -> nameApts.contains(e.getKey())).map(e -> e.getValue())
				.collect(Collectors.toList());
		nameApts.stream().forEach(r -> {
			apts.remove(r);
			aptRecords.remove(r);
			cutomerRecords.values().stream().forEach(l -> l.removeIf(lr -> nameApts.contains(lr.getAptName())));
			returnedApts.values().stream().forEach(l -> l.removeIf(lr -> nameApts.contains(lr.getAptName())));
		});

		return res;
	}

	private Apartment aptFromRecord(RentRecord r) {
		return apts.get(r.getAptName());
	}

	@Override
	public ApartmentReturnCode addCustomer(Customer customer) {
		return customers.putIfAbsent(customer.getIdNumber(), customer) == null ? ApartmentReturnCode.OK
				: ApartmentReturnCode.CUSTOMER_EXISTS;
	}

	@Override
	public List<Apartment> getApatToReturn(LocalDate dayToReturn) {

		return aptRecords.values().stream().flatMap(Collection::stream)
				.filter(x -> x.getReturnDateEST().equals(dayToReturn)).map(rn -> apts.get(rn.getAptName())).distinct()
				.collect(Collectors.toList());
	}

}
