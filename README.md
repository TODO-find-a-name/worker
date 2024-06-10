# Worker
Documentation is my mind, tests are performed by trial and error, perdoname madre por mi vida loca.

## Project structure
This project contains actually various subprojects, trying to partition in different contexts that can be maintained separately.  
The directories are structured as follows:
- __app__  
Contains the actual application
- __libs__  
Everything that is shared goes here
  - ___common___  
  The common stuff like modules interfaces, p2p msg definitions, ecc... (everything that stands between app, core and modules)
  - ___core___  
  The core mechanisms that connect to the Broker, negotiate connections with Recruiters and manage p2p messages exchange with said Recruiters
- __modules__  
  Contains the various modules used by core, injected by app
  - ___js-module___  
  You guessed it, module for javascript

## Building the project
Just use the Gradle wrapper from the project's main directory.

## TODOs
Basically everything