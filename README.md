# Worker
Documentation is my mind, tests are performed by trial and error, perdoname madre por mi vida loca.

## Project structure
Trying to partition the project in different contexts that can be maintained separately, the source directories are structured as follows:
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

## Reminder
- running it on openjdk21 on macos with M3 processor seems to produce problems
- it is necessary to do some shit for the dependencies depending on the architecture in build.gradle.kts

## TODOs
Basically everything