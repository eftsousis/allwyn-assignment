# Bookstore API Assessment

The API testing assessment was developed as a Maven project, with Java version 21 , RESTAssured and TestNG as test runner. All versions used are defined in properties of pom.xml

## Project structure

Project is divided it two main src folders:

src/main/java 
contains centralized configuration , client classes for authors and books which extend baseapi class (contains contructors, timeouts etc) for reusability and easy maintenance.
Also contains POJOPs of authors and book classes for deserialization & serialization of objects during GET / SET etc requests.

src/test/java 
contains tests which extend BaseTest , some utils and resources to produce fake data for testing.

## Github configuration

Under .github folder pipeline is configured in api-bookstore-tests.yml
The project is configured to trigger workflow (default run all tests) in case of Push (either main or develop) or in pull request of main. 
There is also qodana provided by Jetbrains for statis code analysis.

## Running instructions 

Suite has a parallel execution setup as defined in testng.xml

git clone https://github.com/eftsousis/allwyn-assignment.git
cd assignment

### To run all tests:
```bash
mvn clean test
```

### Override base URL:

You can override base url since its given as parameter inside  testng.xml:
```bash
mvn clean test -Dbase.url=https://foo-bar.com
```
### Run only Authors  tests
```bash
mvn clean test -Pauthors
```

### Run tests by group
```bash
# Smoke tests only
mvn clean test -Dgroups=smoke
```

## Reporting

By default Surefire produces a basic HTML report under targe/surefire-reports
However, I used Allure for a more fancy reporting. To run locally:
```bash
allure:serve
```
On github actions after executing the workflow results are in:
https://eftsousis.github.io/allwyn-assignment/

