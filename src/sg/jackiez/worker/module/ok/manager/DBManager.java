package sg.jackiez.worker.module.ok.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.handler.DBDataHandler;
import sg.jackiez.worker.module.ok.handler.vendor.FutureVendorV3;
import sg.jackiez.worker.module.ok.model.TradeHistoryItem;
import sg.jackiez.worker.utils.SLogUtil;
import sg.jackiez.worker.utils.algorithm.bean.KlineInfo;
import sg.jackiez.worker.utils.db.DBUtil;

/**
 * @Author JackieZ
 * @Date Created on 2018/10/21
 */
public class DBManager {

	private static final String TAG = "DBManager";

	private interface TradeHistory {
		String TABLE_NAME = "trade_history";
		String TRADE_ID = "trade_id";
		String TIMESTAMP = "timestamp";
		String PRICE = "price";
		String QTY = "qty";
		String SIDE = "side";
	}

	private interface KlineDetail {
		String TIME = "time";
		String LOW = "low";
		String HIGH = "high";
		String OPEN = "open";
		String CLOSE = "close";
		String VOLUME = "volume";
		String CURRENCY_VOLUME = "currency_volume";
	}

	private interface Kline1MinDeatil extends KlineDetail {
		String TABLE_NAME = "kline_1min";
	}

	private interface Kline15MinDeatil extends KlineDetail {
		String TABLE_NAME = "kline_15min";
	}

	private interface TradeInfo {
		String TABLE_NAME = "trade_info";
		String CLIENT_OID = "client_oid";
		String INSTRUMENT_ID = "instrument_id";
		String ORDER_ID = "order_id";
		String TYPE = "type";
		String PRICE = "price";
		String SIZE = "size";
		String MATCH_PRICE = "match_price";
		String LEVERAGE = "leverage";
		String STATE = "state";
	}

	private DBDataHandler mHandler = new DBDataHandler();

	private static final class SingletonHolder {
		static final DBManager sInstance = new DBManager();
	}

	private DBManager() {}

	public static DBManager get() {
		return SingletonHolder.sInstance;
	}

	public void startGrab() {
		mHandler.startDBThread();
	}

	public void stopGrab() {
		mHandler.startDBThread();
	}

	private int batchInsertData(String table, List<Map<String, Object>> data) {
		try {
			return DBUtil.insertAll(table, data, DBUtil.FLAG_INSERT_REPLACE);
		} catch (SQLException e) {
			SLogUtil.d(TAG, e);
		}
		return 0;
	}

	/**
	 * 历史交易记录
	 */
	public int saveTradeHistory(List<TradeHistoryItem> historyList) {
		if (historyList == null || historyList.isEmpty()) {
			return 0;
		}
		List<Map<String, Object>> data = new ArrayList<>();
		Map<String, Object> dataItem;
		for (TradeHistoryItem item : historyList) {
			dataItem = new HashMap<>();
			dataItem.put(TradeHistory.TRADE_ID, item.trade_id);
			dataItem.put(TradeHistory.TIMESTAMP, item.timestamp.getTime());
			dataItem.put(TradeHistory.PRICE, item.price);
			dataItem.put(TradeHistory.QTY, item.qty);
			dataItem.put(TradeHistory.SIDE, item.side);
			data.add(dataItem);
		}
		return batchInsertData(TradeHistory.TABLE_NAME, data);
	}

	private int saveKlineData(String table, List<KlineInfo> klineInfoList) {
		if (klineInfoList == null || klineInfoList.isEmpty()) {
			return 0;
		}
		List<Map<String, Object>> data = new ArrayList<>();
		Map<String, Object> dataItem;
		for (KlineInfo item : klineInfoList) {
			dataItem = new HashMap<>();
			dataItem.put(KlineDetail.TIME, item.time);
			dataItem.put(KlineDetail.LOW, item.lowest);
			dataItem.put(KlineDetail.HIGH, item.highest);
			dataItem.put(KlineDetail.OPEN, item.open);
			dataItem.put(KlineDetail.CLOSE, item.close);
			dataItem.put(KlineDetail.VOLUME, item.volume);
			dataItem.put(KlineDetail.CURRENCY_VOLUME, item.currency_volume);
			data.add(dataItem);
		}
		return batchInsertData(table, data);
	}

	public int saveKline1minData(List<KlineInfo> klineInfoList) {
		return saveKlineData(Kline1MinDeatil.TABLE_NAME, klineInfoList);
	}

	public int saveKline15minData(List<KlineInfo> klineInfoList) {
		return saveKlineData(Kline15MinDeatil.TABLE_NAME, klineInfoList);
	}

	public int saveTrade(FutureVendorV3.FutureTradeInfo item,
								String leverage,
								int state) {
		if (item == null) {
			return 0;
		}
		Map<String, Object> dataItem = new HashMap<>();
		dataItem.put(TradeInfo.INSTRUMENT_ID, item.instrumentId);
		dataItem.put(TradeInfo.ORDER_ID, item.orderId);
		dataItem.put(TradeInfo.PRICE, item.price);
		dataItem.put(TradeInfo.TYPE, item.trendType);
		dataItem.put(TradeInfo.MATCH_PRICE, item.priceType);
		dataItem.put(TradeInfo.SIZE, item.amount);
		dataItem.put(TradeInfo.LEVERAGE, leverage);
		dataItem.put(TradeInfo.STATE, state);
		try {
			return DBUtil.insert(TradeInfo.TABLE_NAME, dataItem, DBUtil.FLAG_INSERT_REPLACE);
		} catch (SQLException e) {
			SLogUtil.d(TAG, e);
		}
		return 0;
	}

	public int updateTradeState(String clientOId, String orderId, int state) {
		Map<String, Object> dataItem = new HashMap<>();
		dataItem.put(TradeInfo.STATE, state);
		dataItem.put(TradeInfo.ORDER_ID, orderId);
		Map<String, Object> whereMap = new HashMap<>();
		whereMap.put(TradeInfo.CLIENT_OID, clientOId);
		try {
			return DBUtil.update(TradeInfo.TABLE_NAME, dataItem, whereMap);
		} catch (SQLException e) {
			SLogUtil.d(TAG, e);
		}
		return 0;
	}

	public int updateCancelTradeState(String instrumentId, String orderId, int state) {
		Map<String, Object> dataItem = new HashMap<>();
		dataItem.put(TradeInfo.STATE, state);
		Map<String, Object> whereMap = new HashMap<>();
		whereMap.put(TradeInfo.ORDER_ID, orderId);
		whereMap.put(TradeInfo.INSTRUMENT_ID, instrumentId);
		try {
			return DBUtil.update(TradeInfo.TABLE_NAME, dataItem, whereMap);
		} catch (SQLException e) {
			SLogUtil.d(TAG, e);
		}
		return 0;
	}

	public void test() {
		TradeHistoryItem item = new TradeHistoryItem();
		item.price = 5.30;
		item.qty = 1341235L;
		item.side = "short";
		item.timestamp = new Date(System.currentTimeMillis());
		item.trade_id = 1234123412333L;

		Map<String, Object> dataItem = new HashMap<>();
		dataItem.put(TradeHistory.TRADE_ID, item.trade_id);
		dataItem.put(TradeHistory.TIMESTAMP, item.timestamp.getTime());
		dataItem.put(TradeHistory.PRICE, item.price);
		dataItem.put(TradeHistory.QTY, item.qty);
		dataItem.put(TradeHistory.SIDE, item.side);
		try {
			DBUtil.insert(TradeHistory.TABLE_NAME, dataItem, DBUtil.FLAG_INSERT_REPLACE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
