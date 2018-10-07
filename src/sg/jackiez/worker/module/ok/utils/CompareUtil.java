package sg.jackiez.worker.module.ok.utils;

public class CompareUtil {

	public static boolean equal(Object t1, Object t2) {
		if (t1 == t2) {
			return true;
		}
		if (t1 == null || t2 == null) {
			return false;
		}
		return t1.equals(t2);
	}
}
