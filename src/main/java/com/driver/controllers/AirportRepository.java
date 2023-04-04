package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.*;

@Repository
public class AirportRepository {

    public HashMap<String,Airport> airportDatabase = new HashMap<>();

    public HashMap<Integer, Flight> flightDatabase = new HashMap<>();

    public HashMap<Integer,List<Integer>> flightToPassengerDatabase = new HashMap<>();


    public HashMap<Integer,Passenger> passengerDatabase = new HashMap<>();


    public String addAirportDetails(@RequestBody Airport airport){

        airportDatabase.put(airport.getAirportName(),airport);

        return "SUCCESS";
    }

    public String getAirportNameLargest()
    {

        String largestName = "";
        int terminals = 0;
        for(Airport airport : airportDatabase.values()){

            if(airport.getNoOfTerminals()>terminals){
                largestName = airport.getAirportName();
                terminals = airport.getNoOfTerminals();
            }else if(airport.getNoOfTerminals()==terminals){
                if(airport.getAirportName().compareTo(largestName)<0){
                    largestName = airport.getAirportName();
                }
            }
        }
        return largestName;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity,City destinationCity){

        //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.

        double distance = 1000000000;

        for(Flight flight: flightDatabase.values()){
            if((flight.getFromCity().equals(fromCity))&&(flight.getToCity().equals(destinationCity))){
                distance = Math.min(distance,flight.getDuration());
            }
        }

        if(distance==1000000000){
            return -1;
        }
        return distance;

    }

    public int getNumberOfPeopleOn(Date date,String airportName)
    {
        Airport airport = airportDatabase.get(airportName)
        if(airport==null){
            return 0;
        }
        City city = airport.getCity();
        int count = 0;
        for(Flight flight: flightDatabase.values()){
            if(date.equals(flight.getFlightDate()))
                if(flight.getToCity().equals(city)||flight.getFromCity().equals(city)){

                    int flightId = flight.getFlightId();
                    count = count + flightToPassengerDatabase.get(flightId).size();
                }
        }
        return count;
    }
    public int calculateFlightFare(Integer flightId)
    {
        int noOfPeopleBooked = flightToPassengerDatabase.get(flightId).size();
        return noOfPeopleBooked*50 + 3000;
    }

    public String bookATicket(Integer flightId,Integer passengerId)
    {
        if(flightToPassengerDatabase.get(flightId)!=null &&(flightToPassengerDatabase.get(flightId).size()< flightDatabase.get(flightId).getMaxCapacity())){


            List<Integer> passengers =  flightToPassengerDatabase.get(flightId);

            if(passengers.contains(passengerId)){
                return "FAILURE";
            }

            passengers.add(passengerId);
            flightToPassengerDatabase.put(flightId,passengers);
            return "SUCCESS";
        }
        else if(flightToPassengerDatabase.get(flightId)==null)
        {
            flightToPassengerDatabase.put(flightId,new ArrayList<>());
            List<Integer> passengers =  flightToPassengerDatabase.get(flightId);

            if(passengers.contains(passengerId)){
                return "FAILURE";
            }

            passengers.add(passengerId);
            flightToPassengerDatabase.put(flightId,passengers);
            return "SUCCESS";

        }
        return "FAILURE";
    }

    public String cancelATicket(Integer flightId,Integer passengerId)
    {
        List<Integer> passengers = flightToPassengerDatabase.get(flightId);
        if(passengers == null){
            return "FAILURE";
        }

        if(passengers.contains(passengerId)){
            passengers.remove(passengerId);
            return "SUCCESS";
        }
        return "FAILURE";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId)
    {
        int count = 0;
        for(Map.Entry<Integer,List<Integer>> entry: flightToPassengerDatabase.entrySet()){

            List<Integer> passengers  = entry.getValue();
            for(Integer passenger : passengers){
                if(passenger==passengerId){
                    count++;
                }
            }
        }
        return count;
    }

    public String addFlight(Flight flight)
    {
        flightDatabase.put(flight.getFlightId(),flight);
        return "SUCCESS";
    }

    public String getAirportNameFromFlightId(Integer flightId){


        if(flightDatabase.containsKey(flightId)){
            City city = flightDatabase.get(flightId).getFromCity();
            for(Airport airport: airportDatabase.values()){
                if(airport.getCity().equals(city)){
                    return airport.getAirportName();
                }
            }
        }
        return null;
    }

    public int calculateTotalRevenueOfAFlight(Integer flightId){

        int noOfPeopleBooked = flightToPassengerDatabase.get(flightId).size();
        int totalFare = (25 * noOfPeopleBooked * noOfPeopleBooked) + (2975 * noOfPeopleBooked);

        return totalFare;
    }

    public String addPassengerDetails(Passenger passenger){

        passengerDatabase.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }
}
