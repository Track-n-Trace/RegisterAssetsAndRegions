Purpose
========
Use this android app to register assets and regions, once you have been registered on the cloud app as a user.

Requirements
=============
- For Android phones, the minimum version of the operating system should be 4.3 (JellyBean).
- An IBM Bluemix account with Track-N-Trace app running
- You should be registered as a user on the cloud app to use this android app.

Outcome
========
At the end of this tutorial you'll be able to register assets. You'll also be able to define and register regions

Target Audience
================
Anyone who is using the Track-N-Trace cloud app 

Steps to be followed
=====================
- Clone the "https://github.com/Track-n-Trace/RegisterAssetsAndRegions.git" repository. To do this, from a command window, navigate to the directory on your computer in which you want to clone the repository, and then enter:
	git clone https://github.com/Track-n-Trace/RegisterAssetsAndRegions.git

- Install Android Studio (if you have not already done so)

- Open Android Studio.
Click on File > Open Project

- Browse to the location where you have cloned the repository and select the project.

- Open the project and deploy the app on your android phone.

- Make sure you have an active internet connection on your phone.

- To log in, enter the name you had given to your app at the time of initial app creation on your Bluemix account. 
Also, enter the username and password with which you were registered on the Track-N-Trace app as a user.

- To add assets, enter asset name, asset Id and the type of deice (GPS, Bluetooth or RFID) and click 'Register Asset'. On successful registration, you'll receive a 'Success' Toast.

- To add region, enter region name and region Id. To define the geospatial boundary, move around the intended polygonal boundary and add points as corners of the polygon.
When you are done adding points, click on Add gateways. Check the non-GPS gateways that will define your region, that is, the gateways that will be present in the region and click on Register Region. On successful registration, you'll receive a 'Success' Toast.



