# Frontend to Backend API Mapping

| Area | Method and path | Frontend class |
|---|---|---|
| Register | `POST /auth/register` | `AuthService` |
| Login | `POST /auth/login` | `AuthService` |
| Create product | `POST /api/products` | `ProductService` |
| Active products | `GET /api/products` | `ProductService` |
| Search | `GET /api/products/search` | `ProductService` |
| Product details | `GET /api/products/{id}` | `ProductService` |
| Edit product | `PUT /api/products/{id}` | `ProductService` |
| Owner delete | `DELETE /api/products/{id}` | `ProductService` |
| Mark sold | `PUT /api/products/{id}/sold` | `ProductService` |
| Pending products | `GET /api/admin/products/pending` | `AdminService` |
| Approve | `PUT /api/admin/products/{id}/approve` | `AdminService` |
| Reject | `PUT /api/admin/products/{id}/reject` | `AdminService` |
| Admin delete | `DELETE /api/admin/products/{id}` | `AdminService` |
| Users | `GET /api/admin/users` | `AdminService` |
| Block | `PUT /api/admin/users/{id}/block` | `AdminService` |
| Unblock | `PUT /api/admin/users/{id}/unblock` | `AdminService` |
| Promote | `POST /api/admin/users/{id}/promote` | `AdminService` |
| Dashboard | `GET /api/admin/dashboard` | `AdminService` |
| Categories | `GET /categories` | `LookupService` |
| Create category | `POST /categories` | `LookupService` |
| Delete category | `DELETE /categories/{id}` | `LookupService` |
| Cities | `GET /cities` | `LookupService` |
| Create city | `POST /cities` | `LookupService` |
| Add favorite | `POST /api/favorites/{productId}` | `FavoriteService` |
| Remove favorite | `DELETE /api/favorites/{productId}` | `FavoriteService` |
| Favorites | `GET /api/favorites` | `FavoriteService` |
| Send message | `POST /api/conversations/{productId}/messages` | `ConversationService` |
| Conversations | `GET /api/conversations` | `ConversationService` |
| Messages | `GET /api/conversations/{id}/messages` | `ConversationService` |
| Submit rating | `POST /api/ratings` | `RatingService` |
| Seller ratings | `GET /api/ratings/{userId}` | `RatingService` |
