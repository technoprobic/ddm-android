# Reference Android client for sensor data for a I3 Decentralized Data Marketplace

This is a reference Android client implementation (requires Android Oreo or later operating system) for a Decentralized Data Marketplace instance.

The Intelligent IoT Integrator (I3) has created code ( https://github.com/ANRGUSC/DDM ) and deployed an instance for Decentralized Data Marketplaces (DDM) in order to encourage the sharing of data from IoT devices. It is version 1.0, DDM is still under development. It uses blockchain and other distributed ledger technologies in order to allow a seller of IoT data and a buyer to effect an exchange of IoT data without a central authority involved in the exchange. Sellers post descriptions of data products to a DDM instance. Buyers can search and browse a DDM to find data of interest. The buyer can transfer payment using the IOTA cryptocurrency and then connect to a server hosted by the seller to retrieve the data.

Sellers use DDM to post descriptions of data products using IPFS and an Ethereum smart contracts, which buyers can search and browse to find data of interest to them. In the current implementation, the buyer can then use SDPP (Streaming data payment protocol) to connect to an data server hosted by the seller to get and pay for streaming data, useful for IoT applications. Currently SDPP (and hence DDM) supports payment for data using the IOTA cryptocurrency, though this could be extended to other cryptocurrencies. In principle the seller data registry could also be implemented using alternatives to IPFS and Ethereum. The following picture illustrates shows how DDM works:

![DDM architecture illustration](https://raw.githubusercontent.com/technoprobic/ddm-android/master/DDM_architecture_thisApp.jpg?token=ATzSjrIar8iUxdPSGEvcGN4Gghhg_nLyks5bqpLZwA%3D%3D)

This reference Android application is also under development and enables its user to capture data from one of the sensors on the Android device, which can then be posted to a DDM instance for offer for sale as a data product. To use the application after installation, a user enters a (currently Ropsten) contract address for a DDM instance, a (currently Ropsten) Ethereum key, and an IOTA key in Settings. A list of sensors on the Android device is presented to the user. The user can select a sensor and capture its data stream. Afterward, the user can view stored sensor data capture sessions. For a sensor data capture session, the user can enter information to be posted to the marketplace, including an IP address for which the Android device can be accessed. The user can Listen For Buyers in order to listen for and accept requests for sensor data.

## Video Demo
A [video demo of the app](https://www.youtube.com/watch?v=yeOcmwrlpEE&feature=youtu.be)

## Requirements
* A contract address for a deployed DDM instance
* A funded Ropsten ethereum private key
* An IOTA seed
* An Android device (>=SDK 26 aka Oreo) with sensors
* Android device (>=SDK 26 aka Oreo)

## Instructions
Clone the repository
Deploy to an Android device
Enter DDM contract address, Ropsten Ethereum private key, and IOTA seed 

