BRIEF SUMMARY
This is basic implementation of Market simulator catering to single stock and new limit orders only.
It accepts orders and prints executions as well as order book whenever execution occurs. 
It prints only one execution per match based on requirements. It can easily be altered to print execution per order
The simulator runs as single threaded application and matching is triggered when order is received by the system.

ConsoleMarketSimulator is the main class which brings together Console reader, Order cache and Order Book.
It contains the Simulator logic,
 Step 1. Reads order using receiver
 Step 2. Tries to execute order based on available orders in the book
 Step 3. Prints executions and order book, if any executions occur
  
 OrderBook class contains functionality to accept order and maintain order book.
 It also matches order and creates executions. 
 The order Book is maintained using a doubly linked list for each price level at Bid and Ask sides.
 

OrderReceiver interface has been defined to standardize Input operations for order entry.
ConsoleOrderReceiver is the sole implementation of the above interface to read order as console input.
More implementation using network IO can be written when Simulator is used by OMS for testing.

Output interface has not been defined as right now execution and printed along with order book when executions occurs.
If needed printing of execution can be carried out by interface similar to input interface to cater to multiple input channels.


SYSTEM LIMITATIONS
-> Current system does lot of Object creation during operation which will cause GC issues if used in load testing.
If capacity requirements are deterministic then further tuning can be done to avoid Object creation during Simulator operation entirely.

-> Heap implementation uses Java priority queues which are not meant for low latency jobs. 
A custom implementation of Heap will increase the performance.   


TESTING
->TestOrderBook can be used to run and test scenarios for OrderBook implementation

-> TestConsoleMarketSimulator can be used for testing console based input

-> ConsoleMarketSimulator contains main method to run simulator as Java application as well.