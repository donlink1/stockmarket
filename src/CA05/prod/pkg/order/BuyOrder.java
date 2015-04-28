package CA05.prod.pkg.order;

import CA05.prod.pkg.exception.StockMarketExpection;
import CA05.prod.pkg.trader.Trader;

public class BuyOrder extends Order {
	

	public BuyOrder(String stockSymbol, int size, double price, Trader trader) {
		// TODO:
		// Create a new buy order
		setStockSymbol(stockSymbol);
		setSize(size);
		setPrice(price);
		setTrader(trader);
		setMarketOrder(false);
		setType(OrderType.BUY);
	}

	public BuyOrder(String stockSymbol, int size, boolean isMarketOrder,
			Trader trader) throws StockMarketExpection {
		// TODO:
		// Create a new buy order which is a market order
		// Set the price to 0.0, Set isMarketOrder attribute to true
		//
		// If this is called with isMarketOrder == false, throw an exception
		// that an order has been placed without a valid price.
		 
		if (!isMarketOrder)
			throw new StockMarketExpection("Order has been placed without a valid price");

		setStockSymbol(stockSymbol);
		setSize(size);
		setPrice(0.0);
		setTrader(trader);
		setMarketOrder(isMarketOrder);
		setType(OrderType.BUY);
	}

	public void printOrder() {
		System.out.println("Stock: " + stockSymbol + " $" + price + " x "
				+ size + " (Buy)");
	}

}
