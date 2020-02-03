# DroneNavigation

This code is responsible for implementing the ‘Held-Karp’ algorithm for finding the shortest route and provide the DJI Tello-EDU drone with the appropriate commands to autonomously visit a set of predefined statios in a 2D coordinate system. 

The “DJI Tello-EDU” drone is a quadcopter and programmable drone, powered by RYZE. It is equipped with two cameras, one in the front and another in the bottom of the drone, and its flight time is estimated at 13 minutes. The connection between the CS and the drone is achieved through a Wi-Fi UDP port, which allows the users to control the drone sending text command. User Datagram Protocols (UDP) enable the sending of bits of data over the Internet, which are known as packets and are sent to an IP address, in this case the drone’s IP address (192.168.10.1) through its UDP port (8889). Furthermore, the Tello-EDU is able to identify and orientate around specialized “Mission Pads”.

The Hel-Karp algorithm is a variation of the Travelling Salesman Problem (TSP). The problem can be described as the intent of finding the shortest route among N stations, visiting each station just once and returning to the starting point.

Video-demo link: https://www.youtube.com/watch?v=95Lx6dLHpSw

It is a joint effort of the RUC students: Athanasios Kandylas, Dylan Rayner and Juan Blanco.
