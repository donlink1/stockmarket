package CA05.prod.pkg.order;

import java.util.*;

import CA05.prod.pkg.exception.StockMarketExpection;
import CA05.prod.pkg.market.Market;
import CA05.prod.pkg.market.api.PriceSetter;
import CA05.prod.pkg.stock.Stock;

public class OrderBook {
	private Market currentMArket;
	private HashMap<String, ArrayList<Order>> buyOrders;
	private HashMap<String, ArrayList<Order>> sellOrders;

	public OrderBook(Market currentMarket) {
		this.currentMarket = currentMarket;
		buyOrders = new HashMap<String, ArrayList<Order>>();
		sellOrders = new HashMap<String, ArrayList<Order>>();
	}

	public void addToOrderBook(Order order) {
		// Populate the buyOrders and sellOrders data structures, whichever
		// appropriate
		if (order.getType() == OrderType.BUY) {
			if (buyOrders.get(order.getStockSymbol()) == null) {
				ArrayList<Order> newOrderArray = new ArrayList<Order>();
				newOrderArray.add(order);
				buyOrders.put(order.getStockSymbol(), newOrderArray);
			} else
				buyOrders.get(order.getStockSymbol()).add(order);
		} else if (sellOrders.get(order.getStockSymbol()) == null) {
			ArrayList<Order> newOrderArray = new ArrayList<Order>();
			newOrderArray.add(order);
			sellOrders.put(order.getStockSymbol(), newOrderArray);
		} else
			sellOrders.get(order.getStockSymbol()).add(order);
	}

	public void trade() {
		BuyOrder marketBuyOrder = null;
		SellOrder marketSellOrder = null;

		// for every buyOrder and sellOrder that match, save them into their corresponding market order
		for (ArrayList<Order> buyOrder : buyOrders.values()) {
			for (Order currentBuyOrder : buyOrder) {
				if (currentBuyOrder.isMarketOrder()) {
					marketBuyOrder = (BuyOrder) currentBuyOrder;
					break;
				}

			}
		}

		for (ArrayList<Order> sellOrder : sellOrders.values()) {
			for (Order currentSellOrder : sellOrder) {
				if (currentSellOrder.isMarketOrder()) {
					marketSellOrder = (SellOrder) currentSellOrder;
					break;
				}
			}
		}

		int marketBuyOrderSize = marketBuyOrder.getSize();
		int marketSellOrderSize = marketSellOrder.getSize();

		List<ArrayList<Order>> buyOrdersFakeList = new ArrayList(
				buyOrders.values());
		List<Order> buyOrdersList = new ArrayList(buyOrdersFakeList.get(0));
		Collections.sort(buyOrdersList, new OrderPrices());
		Collections.reverse(buyOrdersList);
		for (int i = 0; i < buyOrdersList.size(); i++) {
			if (buyOrdersList.get(i).isMarketOrder()) {
				marketBuyOrder = (BuyOrder) buyOrdersList.get(i);
				buyOrdersList.remove(i);
				break;
			}
		}

		List<ArrayList<Order>> sellOrdersFakeList = new ArrayList(
				sellOrders.values());
		List<Order> sellOrdersList = new ArrayList(sellOrdersFakeList.get(0));
		Collections.sort(sellOrdersList, new OrderPrices());
		sellOrdersList.remove(marketSellOrder);
		for (int i = 0; i < sellOrdersList.size(); i++) {
			if (sellOrdersList.get(i).isMarketOrder()) {
				marketSellOrder = (SellOrder) sellOrdersList.get(i);
				sellOrdersList.remove(i);
				break;
			}
		}

		double matchedPrice = 0.0;
		Order lastBuyOrder = null;
		int i;
		for (i = 0; i < buyOrdersList.size(); i++) {

			marketBuyOrderSize += buyOrdersList.get(i).getSize();
			marketSellOrderSize += sellOrdersList.get(i).getSize();
			
			if (marketBuyOrderSize >= marketSellOrderSize && sellOrdersList.get(i).getPrice() > buyOrdersList.get(i).getPrice() && lastBuyOrder != null)
			{
				matchedPrice = lastBuyOrder.getPrice();
				PriceSetter ps = new PriceSetter();
				ps.registerObserver(currentMarket.getMarketHistory());
				m.getMarketHistory().setSubject(ps);
				ps.setNewPrice(currentMarket, lastBuyOrder.getStockSymbol(),
						lastBuyOrder.getPrice());
				int size = buyOrdersList.size();
				try {
					while (i < size) {
						buyOrdersList.remove(i);
						sellOrdersList.remove(i);
					}
				} catch (Exception e) {
				}
				break;
			}
			else
			{
				lastBuyOrder = buyOrdersList.get(i);
			}

			if (marketBuyOrderSize >= marketSellOrderSize) {
				matchedPrice = buyOrdersList.get(i).getPrice();
				PriceSetter ps = new PriceSetter();
				ps.registerObserver(currentMarket.getMarketHistory());
				currentMarket.getMarketHistory().setSubject(ps);
				ps.setNewPrice(currentMarket, buyOrdersList.get(i).getStockSymbol(),
						buyOrdersList.get(i).getPrice());
				int size = buyOrdersList.size();
				i++;
				try {
					while (i < size) {
						buyOrdersList.remove(i);
						sellOrdersList.remove(i);
					}
				} catch (Exception e) {
				}
				break;
			}

		}

		buyOrdersList.add(marketBuyOrder);
		sellOrdersList.add(marketSellOrder);

		Collections.sort(buyOrdersList, new OrderPrices());
		Collections.reverse(buyOrdersList);

		Collections.sort(sellOrdersList, new OrderPrices());

		for (Order currentOrder : buyOrdersList) {
			try {
				currentOrder.getTrader().getPosition().add(currentMarket.getStockForSymbol(sellOrdersList.get(i).getStockSymbol()));
				currentOrder.getTrader().tradePerformed(currentOrder, matchedPrice);
			} catch (StockMarketExpection e) {
				e.printStackTrace();
			}
		}

		Stock stockSymbolToRemove = currentMarket.getStockForSymbol(sellOrdersList.get(i).getStockSymbol());
		for (Order currentOrder : sellOrdersList) {
			try {
				currentOrder.getTrader().getPosition().remove(stockSymbolToRemove);
				currentOrder.getTrader().tradePerformed(currentOrder, matchedPrice);
			} catch (StockMarketExpection e) {
				e.printStackTrace();
			}
		}
		buyOrders.clear();
		sellOrders.clear();
	}

	private Market getCurrentMarket() {
		return currentMarket;
	}

	private void setM(Market currentMarket) {
		this.currentMarket = currentMarket;
	}

	private HashMap<String, ArrayList<Order>> getBuyOrders() {
		return buyOrders;
	}

	private void setBuyOrders(HashMap<String, ArrayList<Order>> buyOrders) {
		this.buyOrders = buyOrders;
	}

	private HashMap<String, ArrayList<Order>> getSellOrders() {
		return sellOrders;
	}

	private void setSellOrders(HashMap<String, ArrayList<Order>> sellOrders) {
		this.sellOrders = sellOrders;
	}

	private class OrderPrices implements Comparator<Order> {

		@Override
		public int compare(Order o1, Order o2) {
			if (o1.getPrice() > o2.getPrice())
				return 1;
			else if (o2.getPrice() > o1.getPrice())
				return -1;
			else
				return 0;
		}
	}

}
