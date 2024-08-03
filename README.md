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

## Environment variables
| Name                       | Mandatory | Default | Accepted values                    |
|----------------------------|-----------|---------|------------------------------------|
| BROKER_ADDR                | true      | /       | a non-empty valid address          |
| ORGANIZATION               | true      | /       | a non-empty organization name      |
| LOGGING_LVL                | false     | MID     | DISABLED, LOW, MID, HIGH, COMPLETE |
| ENV_P2P_PAYLOAD_SIZE_BYTES | false     | 20000   | greater than zero integers         |
| RECRUITMENT_TIMEOUT_MS     | false     | 5000    | greater than zero integers         |
| P2P_MSG_TIMEOUT_MS         | false     | 60000   | greater than zero integers         |


## If you are on linux
If you want to launch the gui and then run the podman container contacting a local broker, you need to pass the ip of the machine, not host.containers.internal (works only on macos and windows). You can get it with "ip a".

## TODOs
Basically everything