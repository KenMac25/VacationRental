# VacationRental

A Java Swing application for booking and calculating vacation rental costs at various destinations. The GUI allows users to select a destination, specify room and guest details, and generate a detailed bill. Background music and themed images enhance the user experience.

## Features

- Select from 5 unique vacation destinations
- View destination images and descriptions
- Specify bedrooms, bathrooms, guests, nights, and pets
- Dynamic calculation of rental bill with breakdown (rooms, base fee, pet fee, cleaning, service, taxes)
- Capacity validation based on room selection
- Background music playback
- Modern Nimbus look and feel with custom palette

## Requirements

- Java 8 or higher
- Images for destinations in `Images/` folder:
  - Disney.jpg
  - Savannah.jpg
  - Mountains2.jpg
  - Mountains.jpg
  - Dunes.jpg
- Background music file in `Music/Background.wav`
- All source code is in [VacationRentalGUI.java](VacationRentalGUI.java)

## How to Run

1. Ensure all required images and music files are present in their respective folders.
2. Compile the Java file:
   ```sh
   javac VacationRentalGUI.java
   ```
3. Run the application:
   ```sh
   java VacationRentalGUI
   ```

## File Structure

- `VacationRentalGUI.java` — Main application source code
- `Images/` — Destination images
- `Music/Background.wav` — Background music

## Notes

- The application uses Java Swing and Nimbus Look & Feel for a modern UI.
- All logic and UI are contained in a single
