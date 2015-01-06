Welcome to the LexEVS Distributed Client
=====================================

LexEVS is a Java based Open Source project the provides the functionalities of a robust
terminology service API based on common terminolgy standards.

The goal of LexEVS Distributed Client is to provide a small sample of example code along
with a lib folder containing the necessary supporting third party jars to get a minimal
client set up for RMI invocation against the distributed LexEVS api which can be hosted
on any tomcat server.  See more information and the code base for this service at 
https://github.com/lexevs/lexevs-remote.git

LexEVS is distributed under a modified Eclipse Public License. Please see the NOTICE and 
LICENSE files for details.

You will find more details about LexEVS in the here:

https://wiki.nci.nih.gov/display/LexEVS/1+-+LexEVS+6.x+API

The distributed client project can be pulled into a Java IDE as a project  (Some settings 
exist for an eclipse project) and run using ant. It will also run using ant from the 
command line in the root of the project folder on any system where Java 1.7 and ant 
are installed.  

It is not designed as full demonstration of access using the the distributed API, 
however a few very common use cases are covered in the snippet.  

Users may adjust the ServiceURL field in the ServiceHolder to point to any distributed 
LexEVS API desired.  Check documentation at the National Cancer Institute for what version 
and URL is currently available on their production server.  
