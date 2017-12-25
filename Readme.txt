BRIEF SUMMARY

Volume Maximization Algorithm
	This is basic implementation of Volume Maximization Algorithm.
	It starts by creating a single sorted TreeSet with all bid and ask prices sorted in descending order
	to give priority to matching at higher price. It traverses the new price set for each price and 
	calculates buy volume and sell volume available at that price. 
	Matching volume at a price is minimum of buy and sell volume at that price. If the matching volume is 
	greater 	than previously found matching volume then it is picked as highest volume seen so far and price
	is stored as matching price.
	At the end of traversal highest matching volume, if found, is returned along with price for the match 
	as successful result.
	
	Buy volume at a price, is sum of available quantity from all orders at price equal or greater
	than the given price. Similarly sell volume, is sum of available quantity from all orders at price 
	equal or lower than given price.   
	
OrderBook data-structure
	OrderBook maintains bid and ask prices in separate TreeMap along with OrderEntry for first order received 
	at that price. Bid TreeMap is sorted in descending order 	while ask TreeMap is sorted in ascending order 
	(potentially to display top bid and ask price if needed).
	The first OrderEntry in Map acts like HEAD of linked-list and contains link to next OrderEntry at that price level. 
	Any new order when added to book is added to end of the linked list to maintain time priority.
	
	For each Order added to OrderBook an OrderEntry object is created and a map of Order Id with OrderEntry 
	is maintained for both Buy and Sell orders.   
	
	For example, if buy orders were received as b1, b2 and b3 at same price P.
	Then price P will have mapping to OrderEntry for b1 and internally b1 will contain link to b2 and 
	similarly b2 will contain link to b3. 

Problem Simulation	
	VolumeMaximizerSimulation class runs as Java application in single thread and is responsible for running simulation 
	requested in problem set.
	It starts by creating an exchange instance with configured decimal precision, then sends buy and sell orders 
	with configured time delay in between. Quantity and price for each order is drawn from Normal distribution with 
	configured mean and standard deviation.
	Once order sending operations to exchange are complete, VolumeMaximization Algorithm is run and if match is 
	found, result is printed on console.
	All simulation parameters are defined as static and can be modified to change behavior of simulation. 


IMPORTANT CLASSES
	VolumeMaximizerSimulation - The main class which simulates the given problem set. 
	Parameters are defined as static variables and can be modified to change simulation
  
	OrderBook - Maintains prices and order entries for buy and sell orders for a symbol at those price based on time priority
	within each price level.
		
	Exchange - Provides basic order operations for the simulation like ensuring order book for different symbols, decimal precision
	and validating incoming order. It also provides central place to run matching algorithms on order books.  

SYSTEM LIMITATIONS
	-> The code logs everything to Console 

	-> Current system does lot of Object creation during operation which will cause GC issues if used in load testing.If capacity 
	requirements are deterministic then further tuning can be done to avoid Object creation during simulation operation entirely.

FURTHER DEVELOPMENT
	-> Execution printing to fill orders
	-> Tick bands in prices as penalty to beat time priority is limited by decimal precision configured at exchange level 

TESTING
-> VolumeMaximizationAlgTest contains test scenarios which simulates different order books and Matching Algorithm running on them

-> ExchangeTest runs scenarios to test Exchange operations

-> OrderBookTest contains all scenarios test for Order book implementation

-> ExchangeutilsTest verifies the decimal to long and vice versa conversion in the code

-> TestBase has common code like asserting order book after orders have been added to it, common create order methods for Buy and Sell orders

SYSTEM REQUIREMENT
	-> Minimum JDK version 1.7, 1.8 preferred as it is configured at project level
	-> Minimum Junit 4, Junit 5 preferred as it is configured at project level 
