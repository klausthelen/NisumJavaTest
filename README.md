
# User Creation API

API developed with Spring Boot to create users, adding a list of phone numbers, authenticate them and use a JWT token to add more phone numbers and list all the phone numbers that the user has.

## How to test the api
1. First download the repository into your local
    ```bash
      git clone https://github.com/klausthelen/NisumJavaTest.git
    ```
2. With your console go to where you downloaded the repo and run this command:

    ```bash
       .\gradlew bootRun
    ```
   This command will take care of running the application locally, create the database tables and populate it with some test data, 
so you don't have to do it manually.
3. You can access the integrated database manager  in this URL: `http://localhost:8080/h2-console` , the credentials are 

```text
        JDBC URL: jdbc:h2:mem:testdb
        username: sa
        password: pass
 ```

### API Reference

To see the use of the api you can go to this 
[**_URL_**](https://app.swaggerhub.com/apis/KlausThelen/UserCreationApi/v0) 

contains all **_Swagger API documentation_**

## API usage steps

### Create a user

You can create a user along with their phone numbers

```bash
   curl -X 'POST' \
  'http://localhost:8080/auth/register' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
        "name": "Klaus Thelen",
        "email": "klaus@thelen.org",
        "password": "PassWord%456",
        "phones": [
          {
            "number": "789456123",
            "city_code": "123",
            "country_code": "987"
          }
        ]
  }'
```
![User Creation](/static/user_creation.png)

### Login with email and password

To log in with the user you just created or with another existing one

```bash
   curl -X 'POST' \
  'http://localhost:8080/auth/login' \
  -H 'accept: application/json' \
  -H 'Content-Type: application/json' \
  -d '{
  "email": "sofia@example.com",
  "password": "PassWord%456"
  }'
```
Users created by default all have this password _**PassWord%456**_.

![User Login](/static/user_login.png)

### List all the user's phones
By sending the token with which you authenticated, you can see all the registered numbers of that user.


```bash
   curl -X 'GET' \
   'http://localhost:8080/api/secured/v1/phone_list' \
   -H 'accept: application/json' \
   -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE2MDA3NjU5LCJleHAiOjE3MTYwMDkwOTl9.jtH5Kel11HgfWS5ol0MRd2E-lMJK89WfQohBEah55uk'
```

![Phone List](/static/phone_list.png)

### Add new numbers to the user
The list of phone numbers will be added to the user related to the token.

```bash
   curl -X 'POST' \
     'http://localhost:8080/api/secured/v1/add_phone' \
     -H 'accept: application/json' \
     -H 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJrbGF1c0B0aGVsZW4ub3JnIiwiaWF0IjoxNzE2MDA3NjU5LCJleHAiOjE3MTYwMDkwOTl9.jtH5Kel11HgfWS5ol0MRd2E-lMJK89WfQohBEah55uk' \
     -H 'Content-Type: application/json' \
     -d '{
     "phones": [
       {
         "number": "654123987",
         "city_code": "147",
         "country_code": "963"
       }
     ]
   }'
```

![Phone List](/static/register_new_phones.png)

## Json Web Token management
The following diagram represents the entire flow by which the token is created and used:
![JWT management](/static/JWTManagementFlow.png)

* Token creation: The user accesses two different resources that do not have security within the system, 
where the JwtService is responsible for creating a token, with all the required metadata.
* User authentication: The user enters their credentials, the AuthenticationManager is responsible for validating the information entered against the user database, 
and if it is not correct it generates an exception, if it is successful, it authenticates it by returning the created token.
* Token filtering for secured endpoints: The JwtAuthenticationFilter is responsible for validating the token, 
if it is valid it gives access to the protected endpoints and is also responsible for updating the SecurityContextHolder that manages the entire application.


## System layers
The application consists of different layers:
* Controllers Layer: This layer contains the controllers that handle incoming HTTP requests.
* Services Layer: The services layer contains the business logic of the application.
* Repositories Layer: The repositories are the connection to the database.
* Models Layer: The models layer defines the structure of the data with which the application works, especially the domain models.
* Spring Security: Not a layer in the traditional sense, but rather a security framework that is built into the application to provide authentication and authorization.

![Spring Security ](/static/spring_layers.png)


## Tools used

The app uses the following resources to correctly perform different actions:

* Spring Security: The library adds several dependencies and configurations related to security, 
in addition to handling authentication with JWT. It is the main tool for the SecurityConfig and AuthenticationConfig configuration classes.
* Json Web Token: crucial for API security as they provide a stateless method to authenticate users.
* Lombok: Provides annotations and functions to automatically generate methods such as getters/setters, constructors and others, thus facilitating development.
* Springdoc Openapi: Helps to automate the generation of API documentation using spring boot projects. springdoc-openapi works 
by examining an application at runtime to infer API semantics based on spring configurations, class structure and various annotations.
* H2 Database: H2 is especially useful during development and testing because it can run as an in-memory database, meaning data is not persisted to disk.
* JUnit: Unit testing framework for the Java. It is the most popular and is used to write and run repeatable automated tests.







