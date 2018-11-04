# MindlyMaps
This project was made in RunIO hackathon that took place in NIT Rourkela from 3rd to 4th November 2018. The app is a fresh spin on the 
usual navigation algorithms we've witnessed so far. While the usual navigation apps prioritize more on minimizing the transit time between 
2, we aim at finding a sweet spot where we can also save fuel during transit

### Principle of operation
The algorithm is designed to take into consideration the left turns we take during a journey. The reason being as follows
* As India is a left hand side driving country, we take a tight left turn at intersections, which doesn't cause our vehicle to slow down.
It is during the acceleration when the engine effeciency is at its lowest.
* As we are not going against the traffic, there's a lesser chance of accidents
* The wait time at Traffic Lights is decreased in some cases.
All these factors come together and make for a faster transit leaving behind a smaller carbon footprint.

The above facts have been proven by UPS, a logistical company based in US. Here are some research articles to validate that
https://www.informs.org/Impact/O.R.-Analytics-Success-Stories/UPS-On-Road-Integrated-Optimization-and-Navigation-ORION-Project

http://transp-or.epfl.ch/courses/decisionAid2018/labs/Lab_1/Paper_Lab1_2018.pdf

### Implementation
* Google maps basically uses 2 parameters to have a bias for route choice. We have taken the number of left turns into consideration to 
calculate the bias
* We've taken the right turn:left turn ratio as a preliminary parameter and assigned biases accordingly
*The app compares the distance and time to be taken according to Google vs that of our algorithm


