# Second Hand Marketplace — JavaFX Frontend

A complete JavaFX client for the second-hand advertisement project. The project follows a layered frontend architecture and communicates with the backend only through HTTP requests.

## Technology stack

- Java 21
- JavaFX 21 (Controls + FXML)
- Maven
- `java.net.http.HttpClient`
- Jackson for JSON and Java time values
- FXML + CSS
- JWT Bearer authentication

## Implemented flows

### Authentication

- Register
- Login
- JWT storage in an in-memory session
- Automatic role detection from JWT claims, with an admin-dashboard probe as fallback
- Logout

### Advertisements

- List active advertisements
- Search by keyword
- Filter by category, city, minimum price and maximum price
- Sort by newest, lowest price or highest price
- View advertisement details, images, seller and seller rating
- Create an advertisement with multipart images
- Edit an owned advertisement
- Delete an owned advertisement
- Mark an active owned advertisement as sold

### Favorites

- Add an active advertisement to favorites
- View favorites
- Remove an advertisement from favorites

### Conversations

- Start a conversation from an advertisement
- List the authenticated user's conversations
- Load messages in a selected conversation
- Send another message through the product-message endpoint

### Ratings

- Submit a score from 1 to 5 with an optional comment
- Display the seller's average score and rating count

### Administrator

- Dashboard counters
- List pending advertisements
- View pending advertisement details
- Approve, reject with a reason, or delete an advertisement
- List users
- Block and unblock users
- Promote a user to administrator
- List/create/delete categories
- List/create cities

## Project structure

```text
src/main/java/ir/ac/aut/secondhand/frontend
├── client          HTTP client, multipart builder, API exception
├── config          application configuration
├── context         dependency wiring and controller factory
├── controller      JavaFX controllers
│   └── admin       administrator controllers
├── dto             response/domain DTOs
│   └── request     request DTOs
├── model/enums     UserType, ProductStatus, ReviewResult, SortOption
├── navigation      scene and content navigation
├── service         one service per backend API group
├── session         JWT/user session
├── ui              reusable JavaFX components
└── util            validation, alerts, images, JWT, query strings, async executor
```

## Configure backend URL

Edit:

```text
src/main/resources/frontend.properties
```

Default:

```properties
backend.base-url=http://localhost:8080
```

You can also set the `BACKEND_URL` environment variable without editing source files.

## Run

Install JDK 21 and Maven, start the backend, then run:

```bash
mvn clean javafx:run
```

Run tests:

```bash
mvn test
```

## Default administrator

```text
username: admin
password: adminPass
```

## Important backend-contract notes

1. The API guide states that login returns a raw JWT string. The frontend also accepts common object responses such as `{ "token": "..." }`.
2. DTOs use `@JsonIgnoreProperties(ignoreUnknown = true)` and aliases to tolerate common backend property-name variations.
3. The guide does not define an endpoint for "my advertisements". Therefore, the frontend does not invent one. An owner can edit/delete/mark sold when an owned advertisement is available through the product endpoints, but pending advertisements cannot be listed separately without a backend endpoint.
4. The guide does not define a city-delete endpoint, so the administrator city view supports creation only.
5. The guide does not define a profile/current-user endpoint. The frontend gets username, user id and role from JWT claims when available. If the role claim is missing, it probes `/api/admin/dashboard`.
6. The guide does not fully specify JSON shapes for list responses. Product, favorite and conversation DTOs are intentionally tolerant, but if the backend uses a wrapper such as `{ "data": [...] }`, adjust the matching service method.
7. Registration currently sends `fullName` and `phoneNumber`, which are the standard Java/Spring JSON names. If the backend literally expects keys containing spaces, update `RegisterRequest` with `@JsonProperty` annotations.

## Suggested backend additions

These are not required by this frontend source, but they close gaps in the supplied API guide:

```text
GET /api/users/me
GET /api/products/mine
DELETE /cities/{id}
PUT /api/conversations/{id}/seen
```

## Error format

The API client supports the documented structure:

```json
{
  "message": "Advertisement not found",
  "status": 404
}
```

Errors are converted to `ApiException` and displayed with JavaFX alerts instead of terminating the application.
