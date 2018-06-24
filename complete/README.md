# About this project

This is a project based on Spring Boot + JPA, running over a H2 Database.
The goal of this project is creating APIs to create, retrieve, update and list records. 

# How run this project 
go to ${projectRoot}/complete/


execute the script mvnw using the command below:

```
./mvnw spring-boot:run
```


Project will run over the URL below:
http://localhost:8080/


### Generating an executable jar

At the ${projectRoot}/complete/ run the command below:

```
mvn package java -jar target/bankslips.jar
```

# Running test cases (Integration test and unit tests)
At the ${projectRoot}/complete/ run the command below:

```
$ mvn test
```

# Available API Calls


#### Create a new BanSlips
```
POST http://localhost:8080/rest/bankslips/
```
Used to create a new BankSlips
Restrictions:
* due_date must be a future or present date
* total_in_cents must be bigger than 10000 (banks doesn't allow banking tickets with lower payment amount than $ 10,00)
* customer must be informed a name with at least 3 characters and no more than 255 characters
* Status Allowed String values (PENDING, CANCELED, PAID) in UpperCase 


All of those attributes are required to create a new BankSlips.


```
{
"due_date":"2018-01-01",
"total_in_cents":"100000",
"customer":"Trillian Company",
"status":"PENDING"
}
```


Response status:

● 201 : Bankslip created

● 400 : Bankslip not provided in the request body

● 422 : Invalid bankslip provided.The possible reasons are:
	 A field of the provided bankslip was null or with invalid values


#### List all BankSlips

```
GET http://localhost:8080/rest/bankslips/
```
Used to list records created.
Is optional to paginate the list by using page and size request parameters.

Example:

```
GET http://localhost:8080/rest/bankslips?page=1&size=20 
```
Response status:

● 200 : Ok


#### Show a specific BankSlips

```
GET http://localhost:8080/rest/bankslips/{id}
```

Used to show details of a BankSlips. 
If this record is not paid and not canceled and is overdue, the API will calculate a fine for this record.
The fine rate from 1 to 10 days overdue is 0.5%
The fine rate above 10 days overdue is 1%

It is required the ID of this record. And the ID must be a valid UUID.
If it is not a valid UUID, API will return an error.

If the provided ID doens't match any record, API will return an error.

Response status:

● 200 : Ok

● 400 : Invalid id provided - it must be a valid UUID

● 404 : Bankslip not found with the specified id
 


#### Pay a BankSlips

```
PUT http://localhost:8080/rest/bankslips/{id}
```

Used to update a status from a BankSlips.
It is required to inform the new status of this BankSlips.

```
{
	"status":"PAID"
}
```

If the provided ID doens't match any record, API will return an error.

Response status:

● 200 : Bankslip paid

● 404 : Bankslip not found with the specified id


#### Cancel a BankSlips


```
PUT http://localhost:8080/rest/bankslips/{id}
```

Used to update a status from a BankSlips.
It is required to inform the new status of this BankSlips.

```
{
	"status":"CANCELED"
}
```

If the provided ID doens't match any record, API will return an error.

Response status:

● 200 : Bankslip paid

● 404 : Bankslip not found with the specified id
