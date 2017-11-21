
# Automatic Teller Machine
Test for Suncorp  November 2017

##### Implements Mandatory Feature Set

1. Device has a supply of bank notes

2. Device can be asked how many of each type of note it currently has

3. Device can be initialised, but only once.

4. Notes can be added

5. Notes can be withdrawn

6. $20 and $50 notes are supported

7. If a requested withdrawal cannot be met a message will be provided

8. Dispensing money reduces the amount of money remaining

9. If a requested withdrawal cannot be met the amount of money remaining is unchanged

##### Implements Optional Feature Set

1. Data is persisted (in a H2 in memory database which can be swapped for permanent persistence for other environments
by configuration.)

##Technologies

- Java
- JPA with hibernate provider
- Spring MVC, Data, Boot
- Spock
- Maven
- H2
- Thymeleaf
- Bootstrap

## URLS

### Local

http://localhost:8081

## Usage

### User Interface

The application starts at the Initialise Automatic Teller Machine screen. After this is used to load money, 
you are taken to the Automatic Teller Ready screen, where you can use the drop down buttons to perform 
various tasks. The link in the top left hand corner will take you back to the first screen. 

### API Usage
	   
	   Content-Type: application/json
	   
	   POST  http://localhost:8081/api/initialise  Body   Initialises the ATM with specified amount unless it is already initialised
	   
	   GET  http://localhost:8081/api/help/money   Returns an example of the JSON that needs to be sent to initialise the ATM
	   
	   GET http://localhost:8081/api/withdraw/100  Returns a Cash object containing a list of Money objects
	    e.g. {"money":[{"type":"FIFTY","number":1},{"type":"TWENTY","number":3}]}
	   
	   PUT http://localhost:8081/api/load/{type}/{amount}  Type can be either 20 or 50, amount is the number of notes to load. 
	   Responds with confirmation of the number and type of notes loaded e.g 20/20 would respond with
	    {"money":[{"type":"FIFTY","number":0},{"type":"TWENTY","number":20}]}
	   
	   GET http://localhost:8081/api/check/{type}  Type can be either 20 or 50. Response is the number avialable.


## Running Locally

From an IDE run the AtmApplication class and then navigate to
localhost:8081 

Note that we are using Spring Profiles for configuration, this means
you will need to make sure "application.properties" is pointing at
"application-local.properties" when running locally.

You are running with an in-memory database in this environment and therefore any changes will be lost on reboot.

### Run from command line

java -jar atm-1.0.jar

The application will be available in your HTML5 compliant browser at http://localhost:8081

This project uses Maven as its build tool.

## Tests

### Comand line

mvn test 


## Notes

1. Although the user interface is basic, you should get good re-sizing results as Bootstrap gives you good mobile-first features out of the box.
2.Error handling has not been implemented, need more tests, there are also some hard-coded strings that need to be moved, either to property files, loaded by the model or from more interactive UI. There is duplicate code in the 2 controllers that needs to be refactored.
3. The cash dispensing algorithm needs to be replaced with a better more succinct one.
4. Dealing with currency - because we are dealing with whole numbers using BigDecimal is probably overkill in this
 instance. I'm thinking the Java Money and Currency API is worth a look if extending functionality.
5. The ATM controller could be complemented by a RestController API serving up JSON data - using ResponseBody 
and Jackson.
6. To build this project you need Maven. To run this project you need Java 8.













