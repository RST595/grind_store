# Maksim_Rostislavskiy pet project: GrindStore

## Getting started
This is pet project for BMX store e-commerce application.
At this project I was focusing on backend part, that's why UI was added only for admin registration and 
editing goods categories.
Key achievements:
- Learned to work with Spring framework and IntelliJ IDEA.
- Connected project with PostgreSQL database.
- Added separate configuration file: local with Postgres db, 
and main with inMemory db (to provide application work on any machine).
- Used maven to build the project.
- Added basic authentication to the project.
- Covered project with Unit tests, coverage more than 80%.
- Connected outside client with @FeignClient annotation to get currency rates.
- Added pagination and sorting off result for categories of goods.
- Added pagination, sorting and filtering to list of all users.
- Added exception handler to ensure stable operation of the application.
- Deployed application at aws instance with docker: **link**.

## Available functions
Bellow I described all available functions of application.
To fill project with some data, you could uncomment line 32 in
src/main/java/com/bmxstore/grind_store/configuration/InitData.java and restart the application.

### Admin registration
At main page you have 2 options: sign in for testing and leave (link to google).
At sign in page, you could sign in to the application and will be redirected to admin/panel page.
Or you could sign up new account, and you will be redirected back to login page.
To register new admin you should provide:
- some first and last name (not less than 1 character or digit).
- key word should be GRIND (that's how I'm protecting service from adding new admins from outside).
  This word could be changed at application.yml file.
- valid email: 
  - y@x.f, where y - not less than 1 character or digit;
  - x, f - not less than 2 characters;
- Password and confirm password should be equals and not less than 5 symbols.

### Admin panel
- All categories - display all added db categories.
- Edit categories - display editable table with category titles and links to picture.
  You could save changes by save button, or return to original condition by reset button.
- Add new category - category title should be unique. If the title is correct You will be redirected to all categories.
 If title is duplicated, system will display json with error message.
- Delete category - category title should present in db. If the title is in db system will delete it, and You will be 
redirected to all categories. If title isn't found will system will display json with error message.
- Swagger-ui - to test other features: add and edit user, products, put products into basket, 
create orders etc.

### Swagger-UI
#### User
- Add - you could add new user with unique email. User role should be USER or ADMIN. Password and confirm
password should be equals.
- Update - use user id from "/user/list" to update specific user.
Field role should be "USER", or "ADMIN". For other fields, place null if you don't want to update them.
Password and confirm password should be equals. Email should be valid:
   - y@x.f, where y, x - not less than 1 character or digit;
   - f - not less than 2 characters;
- List - show all users from db.
- Delete - change variable "enabled" for user with provided id from true to false.
- Search - list of user with pagination, sorting and filtering.

#### Order
- Create - coping all positions from cart of provided user id into order. Calculates total sum and exchange this sum 
to rubles (currency of products in usd). User cart will be cleared.
- Payment - changing order status from NEW or PAYMENT_FAILED to PAID (other statuses not allowed).
You should provide not null cardId, expiration date in future (from next month onwards), correct order id.
- List - showing all store orders.
- Change status - changing status of order by id.


#### Cart
- Add - create new cart item by user id, product id, and quantity.
- List - show all cart items (product x quantity) of provided user.
- Update - change quantity of cart item (card id available from "/cart/list").
- Remove - delete cart item from user.

#### Categories
- Add - you need to provide unique title, not empty string and not null.
- Update - changing URL to category picture. You need to field category by title.
- List - showing all categories.
- Sort - showing categories with pagination and sorting by specified field (picUrl or title).
- Delete - delete category (by title not id). For success deleting category should be empty (without products).

#### Products
- Add - you need to provide unique productCode, not empty string and not null.
- Update - updating product info by product id. If you don't want to change category,
you should leave category field with "", or type not existing category. If you don't want to update other fields
you should fill them with null.
- List - showing all products.
- Delete - deleting product by id.


