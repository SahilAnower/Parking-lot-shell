package com.sahilanower.parkinglotassignment.commands;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

@ShellComponent
public class ParkingLotCommands {
    // number of slots
    private int n;
    // array of color strings of cars
    private String[] colors;
    // array of registrationNo strings of cars
    private String[] registrationNos;
    // map to store:
    // {color1} -> [slot1, slot2,...]
    // {color2} -> [slot3, slot4,...]
    private HashMap<String, List<Integer>> colorToSlot;
    // map to store:
    // {registrationNoString1} -> slot1
    // {registrationNoString2} -> slot2
    private HashMap<String, Integer> registrationNoToSlot;
    // min priority queue for storing the least slot index which is available
    private PriorityQueue<Integer> priorityQueue;

    @ShellMethod(key = "create_parking_lot", value = "creates a parking lot with passed number 'n' as size")
    public String createParkingLot(@ShellOption(defaultValue = "0") int n) {
        if (n == 0) {
            return "Invalid slot number";
        }
        this.n = n;
        colors = new String[n + 1];
        registrationNos = new String[n + 1];
        priorityQueue = new PriorityQueue<>();
        colorToSlot = new HashMap<>();
        registrationNoToSlot = new HashMap<>();
        for (int i = 1; i <= n; i++) {
            priorityQueue.add(i);
            colors[i] = "";
            registrationNos[i] = "";
        }
        return "Created a parking lot with " + n + " slots";
    }

    @ShellMethod(key = "park", value = "park method for parking car - park {registrationNumber} {Color}")
    public String park(@ShellOption(defaultValue = "") String registrationNumber, @ShellOption(defaultValue = "") String color) {
        if (registrationNumber == null || color == null || registrationNumber.isEmpty() || color.isEmpty()) {
            return "Not a valid registrationNumber and/or color";
        }
        if (priorityQueue.isEmpty()) {
            return "Sorry, parking lot is full";
        }
        Integer topIndex = priorityQueue.poll();
        colors[topIndex] = color;
        registrationNos[topIndex] = registrationNumber;
        if (!colorToSlot.containsKey(color)) {
            colorToSlot.put(color, new ArrayList<>());
        }
        colorToSlot.get(color).add(topIndex);
        registrationNoToSlot.put(registrationNumber, topIndex);
        return "Allocated slot number: " + topIndex;
    }

    @ShellMethod(key = "leave", value = "leave method for removing car - leave {slot}")
    public String leave(@ShellOption(defaultValue = "0") int slot) {
        if (slot == 0) {
            return "Invalid slot number";
        }
        if (priorityQueue.contains(slot)) {
            return "Slot already empty!";
        }
        priorityQueue.add(slot);
        colorToSlot.get(colors[slot]).remove(Integer.valueOf(slot));
        registrationNoToSlot.remove(registrationNos[slot]);
        colors[slot] = "";
        registrationNos[slot] = "";
        return "Slot number " + slot + " is free";
    }

    @ShellMethod(key = "status", value = "status method for getting all slots currently - output format -> {slot} {registration no} {color}")
    public String status() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-9s%-18s%s\n", "Slot No.", "Registration No.", "Colour"));

        for (int i = 1; i <= n; i++) {
            if (!colors[i].isEmpty()) {
                sb.append(String.format("%-9d%-18s%s\n", i, registrationNos[i], colors[i]));
            }
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    @ShellMethod(key = "registration_numbers_for_cars_with_colour", value = "registration_numbers_for_cars_with_colour {color}")
    public String registrationNumbersForCarsWithColor(@ShellOption(defaultValue = "") String color) {
        if (color.isEmpty()) {
            return "No colour provided";
        }
        if (!colorToSlot.containsKey(color)) {
            return "No cars found of this colour";
        }
        StringBuilder sb = new StringBuilder();
        for (Integer slot : colorToSlot.get(color)) {
            sb.append(registrationNos[slot]).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    @ShellMethod(key = "slot_number_for_car_with_registrationNo", value = "slot_number_for_car_with_registrationNo {registrationNumber}")
    public String slotNumberForCarWithRegistrationNo(@ShellOption(defaultValue = "") String registrationNumber) {
        if (registrationNumber.isEmpty()) {
            return "No registration number provided";
        }
        if (!registrationNoToSlot.containsKey(registrationNumber)) {
            return "Invalid registration no.";
        }
        return registrationNoToSlot.get(registrationNumber).toString();
    }

    @ShellMethod(key = "slot_numbers_for_cars_with_colour", value = "slot_numbers_for_cars_with_colour {color}")
    public String slotNumbersForCarsWithColour(@ShellOption(defaultValue = "") String color) {
        if (color.isEmpty()) {
            return "No color provided";
        }
        if (!colorToSlot.containsKey(color)) {
            return "No slots found for this colour";
        }
        return colorToSlot.get(color).toString();
    }
}
