package awesome.com.cardreader;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import awesome.com.cardreader.R;

public class SharedUtils {
	static protected String formatBCDAmount(byte[] amount) {
		StringBuilder res = new StringBuilder();
		if (amount[0] != 0) res.append(Integer.toHexString(amount[0] >= 0 ? amount[0] : 256 + amount[0]));
		if (amount[1] == 0) {
			if (res.length() > 0) {
				res.append("00");
			} else {
				res.append("0");
			}
		} else {
			if (res.length() > 0 && amount[1] <= 9) {
				res.append("0");
			}
			res.append(Integer.toHexString(amount[1] >= 0 ? amount[1] : 256 + amount[1]));
		}
		res.append(",");
		String cents = Integer.toHexString(amount[2] >= 0 ? amount[2] : 256 + amount[2]);
		if (cents.length() == 1) res.append("0");
		res.append(cents);
		res.append("€");
		return res.toString();
	}

	static protected String parseLogState(byte logstate) {
		switch (logstate & 0x60 >> 5) {
			case 0: return new String("Laden");
			case 1: return new String("Entladen");
			case 2: return new String("Abbuchen");
			case 3: return new String("Rückbuchen");
		}
		return new String("");
	}

	static protected String Byte2Hex(byte[] input) {
		return Byte2Hex(input, " ");
	}

	static protected String Byte2Hex(byte[] input, String space) {
		StringBuilder result = new StringBuilder();

		for (Byte inputbyte : input) {
			result.append(String.format("%02X" + space, inputbyte));
		}
		return result.toString();
	}
}