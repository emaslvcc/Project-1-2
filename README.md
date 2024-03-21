# Maastricht Walking and Cycling Map Calculator

## Description

This program calculates the walking or cycling time between two locations specified by zip codes. It opens a window where the user can input the starting and final zip codes and choose between walking or cycling. The output includes the aerial distance and estimated time for the chosen mode of transportation. Additionally, the program displays both points on the map and draws a connecting line.

## Features

- Input two zip codes and choose between walking or cycling.
- Calculates distance and time based on geographical coordinates.
- Utilizes the Haversine method for distance calculation.
- Stores postcode coordinates in a CSV file and retrieves missing data from an API.

## Installation

1. Download and install the JavaFX SDK library.
2. Clone this repository to your local machine.

## Usage

1. Compile and run the `Launcher.java` file.
2. Enter the starting and final zip codes in the input fields.
3. Choose between walking or cycling.
4. View the calculated distance, time, and map display in the output window.

## Notes

- This program uses average speeds calculated for a person living in the Netherlands.

## Dependencies

- JavaFX SDK

