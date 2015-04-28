package CA05.prod.pkg.trader;

import java.util.ArrayList;
import java.util.HashMap;

import CA05.prod.pkg.exception.StockMarketExpection;
import CA05.prod.pkg.market.Market;
import CA05.prod.pkg.order.BuyOrder;
import CA05.prod.pkg.order.Order;
import CA05.prod.pkg.order.OrderType;
import CA05.prod.pkg.order.SellOrder;
import CA05.prod.pkg.stock.Stock;
import CA05.prod.pkg.util.OrderUtility;

public class Trader {
	String name;
	double cashInHand;
	ArrayList<Stock> position; // changed
	ArrayList<Order> ordersPlaced;

	public Trader(String name, double cashInHand) {
		super();
		this.name = name;
		this.cashInHand = cashInHand;
		this.position = new ArrayList<Stock>(); // changed
		this.ordersPlaced = new ArrayList<Order>();
	}

	public void buyFromBank(Market currentMarket, String symbol, int volume)
			throws StockMarketExpection {
		Stock currentStock = currentMarket.getStockForSymbol(symbol);
		if (currentStock == null) {
			System.out.println("Invalid stock given for symbol: " + symbol);
		} else {
			double stockPrice = (double) currentStock.getPrice() * volume;
			if (stockPrice > cashInHand) {
				throw new StockMarketExpection(
						"Cannot place order for stock: " + symbol + " since there is not enough money. Trader: " + this.name);
			} else {
				position.add(currentStock);
				cashInHand = cashInHand - stockPrice;
			}
		}
	}

	public void placeNewOrder(Market currentMarket, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {

		if (price > cashInHand) {
			throw new StockMarketExpection(
					"Stock price is larger than cash in hand.");
		}

		if (orderType == OrderType.BUY) {
			if (OrderUtility.isAlreadyPresent(ordersPlaced, new BuyOrder(
					symbol, volume, price, this)))
				throw new StockMarketExpection(
						"Trader is trying to place two orders for the same stock.");
                  	Order newOrder = new BuyOrder(symbol, volume, price, this);
			position.add(new Stock("", newOrder.getStockSymbol(), price));
			ordersPlaced.add(newOrder);
			currentMarket.addOrder(newOrder);
		} else {
			if (OrderUtility.isAlreadyPresent(ordersPlaced, new SellOrder(
					symbol, volume, price, this)))
				throw new StockMarketExpection(
						"Trader is trying to place two orders for the same stock.");
                  	boolean ownsStock = false;
			for (int i = 0; i < position.size(); i++) {
				if (position.get(i).getSymbol().equals(symbol))
					ownsStock = true;
			}
			if (!ownsStock)
				throw new StockMarketExpection(
						"Trader is trying to sell a stock that he doesn't own.");
		}
	}

	public void placeNewMarketOrder(Market currentMarket, String symbol, int volume,
			double price, OrderType orderType) throws StockMarketExpection {
		// Similar to the other method, except the order is a market order
		if (price > cashInHand) {
			throw new StockMarketExpection(
					"Stock price is larger than cash in hand.");
		}

		if (orderType == OrderType.BUY) {
			if (OrderUtility.isAlreadyPresent(ordersPlaced, new BuyOrder(
					symbol, volume, true, this)))
				throw new StockMarketExpection(
						"Trader is trying to place two orders for the same stock.");
			} else {
			if (OrderUtility.isAlreadyPresent(ordersPlaced, new SellOrder(
					symbol, volume, true, this)))
				throw new StockMarketExpection(
						"Trader is trying to place two orders for the same stock.");
			}
        	} else {
			boolean ownsStock = false;
			for (int i = 0; i < position.size(); i++) {
				if (position.get(i).getSymbol().equals(symbol))
					ownsStock = true;
			}
			if (!ownsStock)
				throw new StockMarketExpection(
						"Trader is trying to sell a stock that he doesn't own.");
		}

		if (orderType == OrderType.BUY) {
			Order newOrder = new BuyOrder(symbol, volume, true, this);
			// position.add(new Stock("", newOrder.getStockSymbol(), price));
			ordersPlaced.add(newOrder);
			currentMarket.addOrder(newOrder);
		} else {
			Order newOrder = new SellOrder(symbol, volume, true, this);
			// position.add(new Stock("", newOrder.getStockSymbol(), price));
			ordersPlaced.add(newOrder);
			currentMarket.addOrder(newOrder);
		}
	}

	public void tradePerformed(Order currentOrder, double matchPrice)
			throws StockMarketExpection {
		try {
		if (currrentOrder.getType() == OrderType.BUY) {
			ordersPlaced.remove(currrentOrder);
			cashInHand -= matchPrice * currrentOrder.getSize();
		} else if (currrentOrder.getType() == OrderType.SELL) {
			ordersPlaced.remove(currrentOrder);
			cashInHand += matchPrice * currrentOrder.getSize();
		}
		}
		catch (NullPointerException e ) {}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCashInHand() {
		return cashInHand;
	}

	public void setCashInHand(double cashInHand) {
		this.cashInHand = cashInHand;
	}

	public ArrayList<Stock> getPosition() {
		return position;
	}

	public void setPosition(ArrayList<Stock> position) {
		this.position = position;
	}

	public ArrayList<Order> getOrdersPlaced() {
		return ordersPlaced;
	}

	public void setOrdersPlaced(ArrayList<Order> ordersPlaced) {
		this.ordersPlaced = ordersPlaced;
	}

	public void printTrader() {
		System.out.println("Trader Name: " + name);
		System.out.println("=====================");
		System.out.println("Cash: " + cashInHand);
		System.out.println("Stocks Owned: ");
		for (Stock s : position) {
			// o.printStockNameInOrder();
			if (!s.getSymbol().equals(""))
				System.out.println(s.getSymbol());
		}
		System.out.println("Stocks Desired: ");
		for (Order o : ordersPlaced) {
			o.printOrder();
		}
		System.out.println("+++++++++++++++++++++");
		System.out.println("+++++++++++++++++++++");
	}
}
