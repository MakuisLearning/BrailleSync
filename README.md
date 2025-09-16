# BrailleSync  

BrailleSync is an IoT-powered accessibility solution that enables real-time text-to-Braille translation and Braille printing. It leverages ESP32 hardware, Firebase cloud services, and an Android mobile application to deliver a reliable, user-friendly system for visually impaired individuals.  

## Project Objectives  
- Provide an intuitive user interface for seamless text input and interaction.  
- Implement IoT-powered translation using ESP32.  
- Facilitate accurate and consistent Braille printing.  
- Refine features through usability testing with visually impaired users.  
- Collaborate with Braille experts and organizations to meet real-world needs.  

## Tech Stack  
- **IoT Hardware**: ESP32  
- **Mobile Application**: Android Studio (Java)  
- **Backend**: Firebase (Realtime Database, Authentication)  
- **Connectivity**: Wi-Fi & Bluetooth  

## Features  
- Real-time text-to-Braille translation.  
- Wireless communication between Android app and ESP32.  
- Cloud storage and sync using Firebase.  
- Braille embossing/printing hardware integration.  

## System Architecture  
1. **Mobile App** (Java, Android Studio): text input and device control.  
2. **Firebase**: cloud storage, sync, and authentication.  
3. **ESP32**: receives translated data and controls Braille printer hardware.  

## Installation & Setup  

### Prerequisites  
- ESP32 development board  
- Android Studio (latest version)  
- Firebase project configured  
- Braille printer/embosser connected to ESP32  

### Steps  
1. Clone Repository:  
   ```bash
   git clone https://github.com/your-username/braillesync.git
   cd braillesync
