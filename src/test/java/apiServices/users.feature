Feature: sample karate test script
  for help, see: https://github.com/intuit/karate/wiki/IDE-Support

  Background:
    * url 'https://jsonplaceholder.typicode.com'
    # Create test case in extent report instance
#    * def ExtentReportingClass = Java.type('examples.ExtentReporting')
#    * def createTest = ExtentReportingClass.createTest(karate.info.scenarioName)
#    * def CreateErrorMessage = ExtentReportingClass.createErrorMessage(karate.info.errorMessage)
  #  * configure afterFeature = function(){ karate.call('afterfeature.feature'); }

@scenario1
  Scenario: get all users and then get the first user by id
    Given path 'users'
    When method get
    Then status 202

    * def first = response[0]

    Given path 'users', first.id
    When method get
    Then status 200
@scenario2
  Scenario: create a user and then get it by id
    * def user =
      """
      {
        "name": "Test User",
        "username": "testuser",
        "email": "test@user.com",
        "address": {
          "street": "Has No Name",
          "suite": "Apt. 123",
          "city": "Electri",
          "zipcode": "54321-6789"
        }
      }
      """

    Given url 'https://jsonplaceholder.typicode.com/users'
    And request user
    When method post
    Then status 201

    * def id = response.id
    * print 'created id is: ', id

    Given path id
    # When method get
    # Then status 200
    # And match response contains user
  