This is the full guide of all API's used in backend and also our UML diagram for the whole platform
at the end.

My teammate,Ms moslehAbadi and I,are responsible for this second hand project.
developing beckend and interacting with data base(we used sqlite for this project) is mine and implementing frontend
and GUI is the part for Ms.moslehAbadi.

this is our API guide in following:
every error for any request that is sent to backend has a response in the format below:
{
"message":message
"status":error code
}


commit 3:
So I forgot that there is no way to add an admin without accesing the database directly.
I have created a defult admin

{"username":admin,"password":adminPass}

so I have added 
POST  /api/admin/users/{id}/promote/

Responses:
200 User have been promoted to admin

Error:

400 user is already admin
401 unauthiorized
403 you are not admin
404 user not found


1-Auth
POST /auth/register *no need for authorization
Body:
{"username":String
"password":String
"full name":String
"phone number":String}

Responses:
200 "Success"
Errors:
400 the username is already used,password or username cannot be empty

Post /auth/login *no need for authorization
Body:
{"username":String
"password":String
}

Responses:
200 {a long String which is the JWT key for our user.this key should be kept for next operations}

Errors:
404 username not found
401 invalid password or username
403 user is deactivated
400 username or password cannot be empty

2-Advertisements

Post api/products *Header:Bearer {JWT key}
Body(form data)
title-text-mandatory
description-text-optional
price-long-mandatory
categoryID-int-mandatory
cityID-int-mandatory
images-file-optional

Responses:
200 advertisement accepted and its waiting for admin approval
*this puts ProductStatus to PENDING

Errors:
400 title cannot be empty,price should be higher than 0,unable to proccess image
401 UNAUTHIORIZED
403 user is not active

GET /api/products *no need for authorization

Responses:
200 [Product1,Product2,.....]

Get /api/products/search *no need for authorization

Body(Query params)
keyword:String
categoryId:int
cityId:int
minPrice:int
maxPrice:int
sortBy:"price_asc"|"price_desc"|"newest"

Responses:
200 {list of products matching the search}

Errors:
400 minPrice cannot be more than maxPrice

Get /api/products/{id} *no need fot authorization

Responses:
200 {Product}

Errors:
400 advertisement not found

PUT /api/products/{id} *only admin or the product owner
this  api is for editing a product which is not in statuses DELETED or SOLD

Body:
title: String,
description: String,
price: int,
categoryId: int,
cityId: int
images:files
*every single on of them is optional

Responses:
200 advertisement edited
*product satus will be changed to pending

Errors:
400 advertiesment is sold or deleted,price should be higher than 0
401 unauthiorized
403 you do not own this advertisement
404 advertisement,city,category not  found

DELETE /api/products/{id} *only the owner

Responses:
200 advertisement deleted
*this puts advertisement status to deleted

Error:
400 advertiesment already deleted
401 unauthiorized
403 you are not the owner of this advertisement
404 advertisement not found


PUT /api/products/{id}/sold *only owner
*this puts advertisement status to sold

Responses:
200 advertisement sold

Error
400 advertiesment is not ACTIVE
401 unauthiorized
403 you are not the owner of this advertisement
404 advertisement not found

3-admin panel
GET /api/admin/products/pending *only admin
*this shows a list of all pending advertisements

Responses:
200 {list of products}

Error:
401 unauthiorized
403 the user is not admin

PUT api/admin/products/{id}/approve *only admin

Responses:
200 advertisement approved

Error:
400 advertiesment status is not pending
401 unauthiorized
403 usr is not admin
404 advertiesment not found

PUT api/admin/products/{id}/reject *only admin

Responses:
200 {"reson":String}

Error:
400 reason cannot be empty
401 unauthiorized
403 user is not admin
404 advertiesment not found

DELETE api/admin/products/{id} *only admin
I created two different API's for deleting a product(one for the owner and one for the admin) because it will be easier in developing procces
this puts advertiesment's status to deleted

Responses:
200 advertiesment deleted

Error:
401 unauthiorized
403 user is not admin
404 advertiesment not found

GET api/admin/users *only admin
this displays all of users

Responses:
200 list of users

Errors:
401 unauthiorized
403 user is not admin

PUT api/admin/users/{id}/block *only admin
this puts isActive variable in user to false

Responses:
200 user blocked


Error:
400 you cannot block an admin,this user is already blocked
401 unauthiorized
403 you are not admin
404 user not found

PUT api/admin/users/{id}/unblock
this puts isActive variable in user to true

Responses:
200 user unblocked

Error:
400 user is already unblocked
401 unauthiorized
403 you are not admin
404 user not found

GET api/admin/dashboard *only admin
this is about having a dashboard which was included in the bonus part of the project

Responses:
200
{ "totalUsers": int,
"totalProducts": int,
"pendingProducts":int,
"activeProducts": int,
"blockedUsers": int}

Error:
401 unauthiorized
403 you are not admin

4-Category

Get /categories *no need for authiorization

Responses:
200 {list of categories}

POST /categories *only admin

Body:
 { "id": int,
"name": String,
"superCategory": null/{id:int}
}
*superCategory can be empty

Responses:
200 category created

Error:
400 category name can not be empty,Category name already used,superCategory_id is invalid
401 unauthiorized
403 user is not admin

DELETE /categories/{id} *only admin

Responses:
200 category deleted

Error:
400 this category is the superCategory of other Categories and cannot be deleted
401unauthiorized
403 user is not admin
404 category not found

5-Cities

GET /cities *no need for authiorization

Responses:
200 {list of cities}

POST /cities *only admin

Body:
{"name":String}

Responses:
200 city created

Error:
400 city name cannot be empty
401 unauthiorized
403 user is not admin

6-favorites

POST api/favorites/{productid} *user

Response:
200 added to favorites

Errors:
400 product is not active,product is already a favorite
401 unauthiorized
404 advertiesment not found

DELETE api/favorites/{productid} *user

Response:
200 deleted from favorites

Errors:
400 product is not a favorite
401 unauthiorized
404 advertisement not found

GET api/favorites *user

Resposes:
200 {list of favorites}

Errors:
401 unauthiorized

7-chat

POST /api/conversations/{productId}/messages *user

Body:
{"content":String}

Responses:
if there is no conversation between the two,a new conversation will be created
200 { "conversationId": int,
"messageId": int,
"sentAt": Local time and date }

Error:
400 advertiesment in not active,content cannot be empty,you cannot message yourself
401 unauthiorized
404 advertiesment not found

GET /api/conversations *user
this returns list of all user conversations

Responses:
200 {list of conversations}

Error:
401 unauthiorized

GET /api/conversations/{id}/messages *user
this returns a list of messages 

Response:
200  
{ "id": int,
"senderUsername": String,
"content": String,
"sentAt": Local date and time,
"seen": boolean } 

Error:
401 unauthiorized
403 you are not a part of this conversation
404 conversation not found

8-Rating
POST /api/ratings *user
{ "productId": int, "score": int, "comment": String }

Response:
200 rating has been recorded

Error:
400 score should be between 1 to 5,you can not score yourself,you have already scored this product
401 unauthiorized
404 advertiesment not found

GET /api/ratings/{userId} *no need for authiorization

Response:
200 { "averageScore": int, "totalRatings": int, "ratings":  { list of all ratings}  }

Error:
404 user not found


the UML design:

classDiagram
direction TB


```mermaid
    class User {
        -int id
        -String username
        -String password
        -String fullName
        -String phoneNumber
        -UserType type
        -boolean isActive
    }

    class UserType {
        <<enumeration>>
        ADMIN
        USER
    }

    class Product {
        -int id
        -String title
        -String description
        -long price
        -ProductStatus status
        -String rejectReason
    }

    class ProductStatus {
        <<enumeration>>
        PENDING
        ACTIVE
        DENIED
        DELETED
        SOLD
    }

    class ProductImage {
        -Long id
        -byte[] imageData
    }

    class Category {
        -Long id
        -String name
    }

    class City {
        -int id
        -String name
    }

  

    class Favorite {
        -Long id

    }

    class Conversation {
        -Long id
  
    }

    class ChatMessage {
        -Long id
        -String content
        -LocalDateTime sentAt
        -boolean seen
    }

    class Rating {
        -Long id
        -int score
        -String comment
       
    }

    class AdReview {
        -Long id
        -ReviewResult result
        -String note
        -LocalDateTime reviewedAt
    }

    class ReviewResult {
        <<enumeration>>
        APPROVED
        REJECTED
    }



    User "" *-- "" Product : owner
    Product "" *-- "" ProductImage : images
    Product "" --> "" Category : category
    Product "" --> "" City : city
    Category "" --> "" Category : superCategory
    User "" -- "" UserType
    Product "" -- "" ProductStatus

    User "" --> "" Favorite : buyer
    Favorite "" --> "" Product

    Product "" --> "" Conversation
    User "" --> "" Conversation : buyer
    User "" --> "" Conversation : seller
    Conversation "" *-- "" ChatMessage
    User "" --> "" ChatMessage : sender

    Product "" --> "" Rating
    User "" --> "" Rating : seller
    User "" --> "" Rating : buyer

    Product "" -- "" AdReview
    User "" --> "" AdReview : admin
    AdReview "" -- "" ReviewResult
```

A big shout out to freecodecamp .They created free tutorials which made this projects easier for me
https://www.youtube.com/watch?v=5rNk7m_zlAg&t=12446s
https://www.youtube.com/watch?v=oGhc5Z-WJSw&t=463s&pp=ygUQSldUIGZyZWVjb2RlY2FtcA%3D%3D














