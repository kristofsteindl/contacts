# contacts
Rest API for managing contacts<br>

Please make sure to have a properly configured Postgres database up and running, and modify the `application.yml` according to this (if needed). 
To run the application run `mvn spring-boot:run` command from project root.

In order to use `contact` endpoints, create a user, log in, and use the returned `token` in the header of the request (`key=Authorization`, `value=<token>`)
### Create User

### `POST: <app-url>/api/users/register`<br>
```
{
    "username": "john.doe",
    "password": "very-secret-password"
}
```


### Log in

### `POST: <app-url>/api/users/login`<br>
```
{
    "username": "john.doe",
    "password": "very-secret-password"
}
```
Response
```
{
"success": true,
"token": "eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IjkiLCJpYXQiOjE2MzM4NzU4NzQsInVzZXJuYW1lIjoiaGVsbG82In0.kWko-KKJosXTP1CSHIddD3lwVrP57JT990v5k8YRGfdZHeTp4Zw37B5iRBwzLqKkE7DGJ__fRe8n1kVxqtwmJA"
}
```
### Create contact
### `POST: <app-url>/api/contact`<br>
Creates a new contact according to the input values and send a welcome e-mail to the e-mail address of the new contact. For the input requirements, please see  [SPECIFICATION_HU.md](SPECIFICATION_HU.md).
```
{
    "firstName": "Lajos",
    "lastName": "Kossuth",
    "email": "lajos.kossuth@hungary.hu",
    "phone": "+3612345678",
    "companyId": 1,
    "comment": "Talpra magyar!",
    "status": "ACTIVE"
}
```
Response 
201 if everything is OK (input validation is succeded), plus the created contact
```
{
    "id": 15,
    "firstName": "Lajos",
    "lastName": "Kossuth",
    "email": "lajos.kossuth@hungary.hu",
    "phone": "+3612345678",
    "company": {
        "id": 1,
        "name": "Company #1"
    },
    "comment": "Talpra magyar!",
    "status": "ACTIVE",
    "createdAt": "2021-10-10T16:33:43.594544+02:00",
    "updatedAt": null
}
```
400 if anything goes wrong, plus an error message
```
{
    "message": "+12345 is unknown phone number format"
}
```
### Update contact
### `PUT: <app-url>/api/contact/{id}`<br>
Updates an existing contact according to the input values. Every attribute can be changed while these fit for the input requirements ([SPECIFICATION_HU.md](SPECIFICATION_HU.md)).
#### Parameter
`id ` - the id of the updating contact
```
{
    "firstName": "Alajos",
    "lastName": "Kossuth",
    "email": "lajos.kossuth@hungary.hu",
    "phone": "+3612345678",
    "companyId": 1,
    "comment": "Talpra magyar!",
    "status": "ACTIVE"
}
```
Response
201 if everything is OK (input validation is succeded), plus the created contact
```
{
    "id": 15,
    "firstName": "ALajos",
    "lastName": "Kossuth",
    "email": "lajos.kossuth@hungary.hu",
    "phone": "+3612345678",
    "company": {
        "id": 1,
        "name": "Company #1"
    },
    "comment": "Talpra magyar!",
    "status": "ACTIVE",
    "createdAt": "2021-10-10T16:33:43.594544+02:00",
    "updatedAt": null
}
```
400 if anything goes wrong, plus an error message
```
{
    "message": "+12345 is unknown phone number format"
}
```
### Delete contact
###`DELETE: <app-url>/api/contact/{id}`<br>
Sets the status of an existing contact to DELETED.
#### Parameter
`id ` - the id of the contact to be deleted
Response
200
```
{
    "id": 15,
    "firstName": "Lajos",
    "lastName": "Kossuth",
    "email": "lajos.kossuth@hungary.hu",
    "phone": "+3612345678",
    "company": {
        "id": 1,
        "name": "Company #1"
    },
    "comment": "Talpra magyar!",
    "status": "DELETED",
    "createdAt": "2021-10-10T16:33:43.594544+02:00",
    "updatedAt": "2021-10-10T16:45:45.475944+02:00"
}
```
### Get contact details
###`GET: <app-url>/api/contact/{id}`<br>
Returns the corresponding contact to the id.
#### Parameter
`id ` - the id of the contact
Response
200
```
{
    "id": 15,
    "firstName": "Lajos",
    "lastName": "Kossuth",
    "email": "lajos.kossuth@hungary.hu",
    "phone": "+3612345678",
    "company": {
        "id": 1,
        "name": "Company #1"
    },
    "comment": "Talpra magyar!",
    "status": "DELETED",
    "createdAt": "2021-10-10T16:33:43.594544+02:00",
    "updatedAt": "2021-10-10T16:45:45.475944+02:00"
}
```
### List contacts
###`GET: <app-url>/api/contact`<br>
Returns a paged list of contacts according to the query parameters
#### Query Parameters
`sortBy ` - the list will be sorted, according to this value. Possible values: 'company', 'email', 'phone'. Leaving out this param will cause default sorting, sorting by name (lastName + firstName)  <br>
`queryString ` - the returned list will only contain contacts, that any of its attribute contain the given word<br>
`page` - returns this page of the paged list<br>
Example<br>
`localhost:8080/api/contact?page=0&sortBy=company&queryString=e`<br>
200
```
{
    "content": [
        {
            "id": 1,
            "firstName": "John",
            "lastName": "Doe",
            "email": null,
            "phone": "+14155552671",
            "company": {
                "id": 1,
                "name": "Company #1"
            },
            "comment": null,
            "status": "ACTIVE",
            "createdAt": "2021-10-05T19:14:48.140309+02:00",
            "updatedAt": null
        },
        {
            "id": 2,
            "firstName": "Horváth",
            "lastName": "Ferdinánd",
            "email": "horvath.ferenc@company2.com",
            "phone": "+14155552671",
            "company": {
                "id": 2,
                "name": "Company #2"
            },
            "comment": "what\nme?",
            "status": "ACTIVE",
            "createdAt": "2021-10-05T19:24:29.413705+02:00",
            "updatedAt": "2021-10-05T19:26:46.479022+02:00"
        },
        {
            "id": 3,
            "firstName": "Horváth",
            "lastName": "Ferenc",
            "email": "horvath.ferenc@company2.com",
            "phone": "+14155552671",
            "company": {
                "id": 2,
                "name": "Company #2"
            },
            "comment": "what\nme?",
            "status": "ACTIVE",
            "createdAt": "2021-10-05T19:46:30.445335+02:00",
            "updatedAt": null
        },
        {
            "id": 13,
            "firstName": "Endre",
            "lastName": "Company",
            "email": "endre.company@company2.com",
            "phone": "+3692345678",
            "company": {
                "id": 2,
                "name": "Company #2"
            },
            "comment": "tttt",
            "status": "ACTIVE",
            "createdAt": "2021-10-06T18:12:16.19184+02:00",
            "updatedAt": null
        }
    ],
    "currentPage": 0,
    "totalItems": 4,
    "totalPages": 1
}
```


