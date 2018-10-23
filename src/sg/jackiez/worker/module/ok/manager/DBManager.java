package sg.jackiez.worker.module.ok.manager;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sg.jackiez.worker.module.ok.handler.DBDataHandler;
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
			return DBUtil.insertAll(table, data);
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
			DBUtil.insert(TradeHistory.TABLE_NAME, dataItem);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
