# Shopping-Cart

## Build and Deploy : 
Do a "mvn clean install" and deploy the jar or else run the CartApplication.java file

## DB:
Embedded H2 DB is setup with following tables:
### Product: 
For storing the products available in the market.

### OrderConfirmation: 
For storing the Confirmed Orders.

### Spring_Session: 
To store the session information

### Spring_Session_Attributes: 
To store the cart details for the specified sessions.
Each session has a max inactive period of 30min.

## Start the application
To Start go to this url : localhost:8080/cart : this should route you to the swagger page.

Start with **/addcart** to add your product one at a time. Calling the same api for same product will increment the quantity.

**/viewcart** : view your current cart.

**/updatecart** :  to update the quantities for multiple products. Here quantities will change to new value rather than been incremented and simultaneously check if required quantities are available in market.

**/order**:  to confirm your order. This will automatically kill existing session. 

## Misc API:
**/retrieve** : to see all products available in market

**/list/{name}** : to find products containing the keyword

**/deletecart** : to delete cart and session

**/deleteproduct** : to remove the product from the cart

## Custom Exceptions defined:
**AvailabilityException** : In case the required quantity is not available.

**ProductNotFoundException** : If product is not available.

**EmptyCartException** : If the cart is empty and you are trying to submit an order or view your cart
