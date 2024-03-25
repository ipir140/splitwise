## Setup

### Implementation Decisions and Considerations
- **Serialized strings were used to store user IDs in groups and transaction amounts to borrowers within the 
`Group` and `Transaction` entities, respectively, simplifying the database schema and reducing the need for 
additional memory. This also mitigated the use of transaction of multiple userId in `group` table and borrower info's in `transaction` table**
- **Scope was limited to core functionalities like creating groups and recording transactions,
omitting resource update and delete operations to prioritize development resources and maintain simplicity.
While optimistic locking mechanisms could enhance data consistency while group update operations and
minimizing of transaction on multi user was restricted because its NP complete problem. thus, restricting minimization 
of transaction between two users to themselves only**
### Requirements
- **Install postgreSQL (v14.11 preferred)**: 
- **Create database `splitwise` by `postgres=# create database splitwise;`**:
- **Install Java8 and maven to build jar in `/target` path**
- **Install docker**
- **create jar by `mvn clean package`**
- **build docker image `docker build -t splitwise .`**
- **run container `docker run --network="host" -d splitwise`**
- **Default runs on port `8080`**

## API Documentation

### User Endpoints

#### Get User by ID
- **Method**: GET
- **URL**: `/api/users/{userId}`
- **Description**: Retrieves user details by ID.

#### Create User
- **Method**: POST
- **URL**: `/api/users/create`
- **Description**: Creates a new user.
- **Request Payload**:
  ```json
  {
    "name": "a5"
  }


### Group Endpoints

#### Get Group by ID
- **Method**: GET
- **URL**: `/api/groups/{groupId}`
- **Description**: Retrieves group details by ID.

#### Get Balances of Group
- **Method**: GET
- **URL**: `/api/groups/{groupId}/balance`
- **Description**: Retrieves balances of users within a group.

#### Get Groups of User
- **Method**: GET
- **URL**: `/api/groups/user/{userId}`
- **Description**: Retrieves all groups associated with a user.

#### Create Group
- **Method**: POST
- **URL**: `/api/groups`
- **Description**: Creates a new group.
- **Request Payload**:
  ```json
  {
    "userIds": ["long"]
  }

#### Record Transaction
- **Method**: POST
- **URL**: `/api/groups/record`
- **Description**: Records a transaction within a group.
- **Request Payload**:
- **Direct Transaction**:
  ```json
  {
    "type": "direct",
    "lenderId": "long",
    "totalAmountLent": "double",
    "borrowerId": "long",
    "timestamp": "long"
  }
- **Specified Split Transaction**:
  ```json
  {
    "type": "specifiedSplit",
    "lenderId": "long",
    "groupId": "string",
    "borrowerIdToAmount": {
       "long": "double"
    },
    "timestamp": "long"
  }

- **Percentage Split Transaction**:
  ```json
  {
    "type": "percentageSplit",
    "lenderId": "long",
    "groupId": "string",
    "totalAmountLent": "double",
    "borrowerIdToPercentage": {
    "long": "int"
    },
    "timestamp": "long"
  }

- **Equal Split Transaction**:
  ```json
  {
    "type": "equalSplit",
    "lenderId": "long",
    "groupId": "string",
    "totalAmountLent": "double",
    "borrowers": ["long"],
    "timestamp": "long"
  }