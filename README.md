# Implementation of Authentication and Authorization using Okta

#### This repository houses a multi-module Spring Security application comprising two main modules:
- OAuth-Client
- OAuth-Resource-Server

#### The project is developed using Java, Spring Boot, and SQL and implements Authentication and Authorization servers based on OAuth 2.0 and OpenID Connect standards within a distributed system. In this setup, Okta is used as the Authentication server.

### Okta

[Okta](https://www.okta.com/) is a popular identity and access management platform that provides authentication and authorization services. It simplifies user identity verification, secure user authentication, and seamless access to applications.

### OAuth 2.0

[OAuth 2.0](https://oauth.net/2/) is an authorization framework that enables secure third-party access to resources without exposing user credentials. It is widely used for delegated access and is a key component in securing APIs.

### OpenID Connect (OIDC)

[OpenID Connect](https://openid.net/connect/) is an identity layer built on top of OAuth 2.0. It adds an authentication layer to OAuth, providing a standardized way for clients to authenticate users. OpenID Connect is commonly used for single sign-on (SSO) scenarios.


## Table of Contents

- [Modules](#modules)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Usage](#usage)
- [Contributing](#contributing)
- [License](#license)
- [Okta, OAuth 2.0, and OpenID Connect](#okta-oauth-20-and-openid-connect)

## Modules

1. **OAuth-Client**: This module serves as the client-side implementation of OAuth 2.0. It facilitates user authentication and authorization using OAuth.

2. **OAuth-Resource-Server**: This module acts as a resource server that hosts protected resources. It validates access tokens and provides secure access to protected resources.

## Prerequisites

Before you begin, ensure you have the following prerequisites installed:

- Okta Developer Account ([ref]())
- Java Development Kit (JDK) 17 or later
- Spring Boot version 3.2.2
- Maven (for building and managing dependencies)

## Getting Started

## Setting up Okta Developer Account

1. **Create an Okta Developer Account:**
   - Go to [Okta Developer](https://developer.okta.com/).
   - Click on the "Sign Up" button.
   - Follow the registration process to create your Okta Developer account.

2. **Log in to Okta Developer Console:**
   - Once your account is created, log in to the [Okta Developer Console](https://developer.okta.com/login/).

3. **Create an Okta Application:**
   - In the Okta Developer Console, navigate to "Applications" and click on "Add Application."
   - Choose the application type that fits your use case (e.g., Web Application).
   - Configure the necessary settings for your application.

4. **Retrieve Okta Configuration Details:**
   - After creating the Okta application, you will get details such as `Client ID` and `Client Secret`. These details will be used in your Spring Security application configuration.

5. **Configure Redirect URIs:**
   - Set up the redirect URIs for your application. This is crucial for handling the OAuth 2.0 authorization flow.

6. **Explore Okta Documentation:**
   - Refer to the [Okta Documentation](https://developer.okta.com/docs/) for detailed guides and documentation on using Okta for authentication and authorization.

Now, you are ready to integrate Okta authentication into your Spring Boot project.

## To get started with the project, follow these steps:

1. Clone the repository:

   ```bash
   git clone https://github.com/YourUsername/okta-auth-impl.git

2. Build the project using Maven:
   ```bash
   cd okta-auth-impl
   mvn clean install

## Configuration

### Create SQL Schema and Update Configuration:
- Create the necessary SQL schema for your application.
- Update the application.yml files in each module with the database configuration, ensuring that the connection details match your setup.

#### Each module in the project has its own configuration options please follow below links for "application.yml" file for each module.

- [OAuth-Resource-Server Configuration](https://github.com/BytePiston/okta-auth-impl/blob/master/oauth-resource-server/src/main/resources/application.yml)
- [Spring-Security-Client Configuration](https://github.com/BytePiston/okta-auth-impl/blob/master/spring-security-client/src/main/resources/application.yml)


## Usage

### Postman Collection for User Management:

- To facilitate user management, I have provided a Postman collection that includes endpoints for creating users, verifying user token, regenerating user tokens, and managing passwords. Import the Postman Collection into your Postman workspace.

## Token Verification Expiration Time And Regeneration:

#### **_NOTE: Please verify Token using the URL received in the response of `/register`, `/resetPassword`, and `/updatePassword` endpoint; Without Token Verification User will be marked as "Disabled" in Okta;_**

- For "Newly Created Users" and "Password Reset," the token verification expiration time is currently set to 10 minutes. You can customize this by changing the EXPIRATION_TIME_IN_MINUTES variable in the Constants.java file.
- Ensure that user tokens are validated within the specified expiration time (**Default is 10 minutes**). If the validation time exceeds, regenerate the token using the appropriate endpoint:
- For "Newly Created Users": Use `/resendVerificationToken` endpoint.
- For "Password Reset": Use `/resendResetPasswordToken` endpoint.
- To "Verify Token": Use `/verifyToken` endpoint.

## Contributions
Contributions are welcome! If you find any issues or have suggestions for improvements, please open an issue or submit a pull request.
