package com.driver.services.impl;

import com.driver.model.*;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		Customer customer = customerRepository2.findById(customerId).get();
		List<TripBooking> tripBookingList = customer.getTripBookingList();

		for(TripBooking tripBooking : tripBookingList){
			if(tripBooking.getStatus() == TripStatus.CONFIRMED){
				tripBooking.setStatus(TripStatus.CANCELED);
			}
		}
		customerRepository2.delete(customer);
	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
		TripBooking newTrip = new TripBooking();
		Driver driver =null;
		List<Driver> driverList = driverRepository2.findAll();
		for(Driver driver_temp:driverList){
			if(driver_temp.getCab().getAvailable()==true){
				if(driver == null || driver.getDriverId()>driver_temp.getDriverId()){
					driver = driver_temp;
				}
			}
		}
		if(driver == null){
			throw new Exception("No cab available!");
		}
		newTrip.setFromLocation(fromLocation);
		newTrip.setToLocation(toLocation);
		newTrip.setDistanceInKm(distanceInKm);
		Customer customer = customerRepository2.findById(customerId).get();
		newTrip.setCustomer(customer);
		newTrip.setDriver(driver);
		driver.getCab().setAvailable(false);

		newTrip.setBill(distanceInKm * driver.getCab().getPerKmRate());

		driver.getTripBookingList().add(newTrip);
		driverRepository2.save(driver);
		customer.getTripBookingList().add(newTrip);
		customerRepository2.save(customer);


		return newTrip;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		tripBooking.getDriver().getCab().setAvailable(true);
		tripBooking.setStatus(TripStatus.CANCELED);
		tripBooking.setBill(0);
		tripBookingRepository2.save(tripBooking);
	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		TripBooking tripBooking = tripBookingRepository2.findById(tripId).get();
		Driver driver = tripBooking.getDriver();
		Cab cab = driver.getCab();

		tripBooking.setStatus(TripStatus.COMPLETED);
		tripBooking.setBill(tripBooking.getDistanceInKm() * cab.getPerKmRate());
		tripBooking.getDriver().getCab().setAvailable(true);


		tripBookingRepository2.save(tripBooking);
	}
}
