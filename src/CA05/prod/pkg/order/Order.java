package CA05.prod.pkg.order;

import CA05.prod.pkg.trader.Trader;

public abstract class Order {
	int size;
	double price;
	boolean isMarketOrder = false;
	Trader trader;
	int orderNumber;
	String stockSymbol;
	OrderType type;


	public static final Object LOCK = new Object();

	private static int lastOrderNumber = -1;

	protected static int getNextOrderNumber() {
		synchronized (LOCK) {
			lastOrderNumber++;
			return lastOrderNumber;
		}
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public boolean isMarketOrder() {
		return isMarketOrder;
	}

	public void setMarketOrder(boolean isMarketOrder) {
		this.isMarketOrder = isMarketOrder;
	}

	public Trader getTrader() {
		return trader;
	}

	public void setTrader(Trader trader) {
		this.trader = trader;
	}

	public int getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getStockSymbol() {
		return stockSymbol;
	}

	public void setStockSymbol(String stockSymbol) {
		this.stockSymbol = stockSymbol;
	}

	/** */
	public boolean equals(Object currentOrder) {
		return ((Order) currentOrder).getOrderNumber() == this.getOrderNumber();
	}

	public void printStockNameInOrder() {
		System.out.println(stockSymbol);
	}

	public abstract void printOrder();

}
