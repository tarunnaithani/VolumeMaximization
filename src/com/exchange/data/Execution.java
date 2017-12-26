package com.exchange.data;

/**
 * Class to store execution information
 */
public class Execution {
	private static int masterExecutionId = 0;

	/** Unique id for the execution */
	private final int executionId;
	
	/** Order id for the execution */
	private final int orderId;
	
	/** Quantity executed */
	private final long quantity;
	/** Price at which execution occurred  as long with last three digits as decimal*/
	private final long price;
	
	/** Whether full execution or partial execution */
	private final ExecutionType execType;

	public Execution(int orderId, long quantity, long price, ExecutionType execType) {

		// Quantity validations
		if (quantity <= 0)
			throw new IllegalArgumentException("Quantity must be > 0");
		
		//price validations
		if (price <= 0)
			throw new IllegalArgumentException("Price must be > 0");

		this.executionId = masterExecutionId++;
		this.orderId = orderId;
		this.quantity = quantity;
		this.price = price;
		this.execType = execType;
	}

	public int getExecutionId() {
		return this.executionId;
	}

	public long getQuantity() {
		return quantity;
	}

	public long getPrice() {
		return price;
	}

	public int getOrderId() {
		return orderId;
	}

	public ExecutionType getExecType() {
		return execType;
	}

	@Override
	public String toString() {
		return "Execution [executionId=" + executionId + ", orderId=" + orderId + ", quantity=" + quantity + ", price="
				+ price + ", execType=" + execType + "]";
	}
	
	
}
