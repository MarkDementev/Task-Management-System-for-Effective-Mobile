# Overview

Task Management System by Mark Dementev (JavaMarkDem) for Effective Mobile.

## How to start local

```sh
Make start
```

After this, the application and database will be ready to process your requests on http://localhost:5001

## First step

Authentication is required for operations. An admin is created in the application at startup, start working through it, then you can change his data or add other users.

```sh
POST http://localhost:5001/login
```

```sh
{
    "email": "admin_mail@mail.ru",
    "password": "1q2w3e"
}
```

## Next steps

You can view possible requests here after launching the application

```sh
http://localhost:5001/swagger.html
```