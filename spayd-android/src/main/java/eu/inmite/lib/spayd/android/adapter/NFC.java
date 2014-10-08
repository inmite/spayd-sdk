package eu.inmite.lib.spayd.android.adapter;

import android.annotation.TargetApi;
import android.content.Intent;
import android.nfc.*;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Parcelable;
import android.text.TextUtils;
import eu.inmite.lib.spayd.android.IntentConstants;

import java.io.IOException;

/**
 * @author carnero
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
public class Nfc {

	/**
	 * Return NDEF data from Intent
	 *
	 * @param intent
	 * @return
	 */
	public static String read(Intent intent) {
		if (intent == null) {
			return null;
		}

		String content = null;

		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
			Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

			if (rawMsgs != null && rawMsgs.length > 0) {
				final NdefMessage msg = (NdefMessage) rawMsgs[0];

				final NdefRecord[] records = msg.getRecords();
				final StringBuilder sb = new StringBuilder();

				if (records != null && records.length > 0) {
					for (NdefRecord record : records) {
						if (record == null || record.getPayload() == null) {
							continue;
						}
						sb.append(new String(record.getPayload()));
					}
				}

				if (sb.length() > 0) {
					content = sb.toString();
				}
			}
		}
		return content;
	}

	/**
	 * Write SPAYD payload to NFC tag (just for testing purposes)
	 *
	 * @param intent
	 * @param payload
	 * @return
	 */
	public static boolean write(Intent intent, String payload) {
		if (intent == null || TextUtils.isEmpty(payload)) {
			return false;
		}

		if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
			final Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

			final NdefRecord[] records = {createRecord(payload)};
			final NdefMessage message = new NdefMessage(records);

			boolean isFormatable = false;
			for (String type : tag.getTechList()) {
				if (type.equals("android.nfc.tech.NdefFormatable")) {
					isFormatable = true;
				}
			}

			Ndef ndef = Ndef.get(tag);
			if (ndef == null && isFormatable) {
				NdefFormatable ndefForm = NdefFormatable.get(tag);

				if (ndefForm != null) {
					try {
						ndefForm.connect();
						ndefForm.format(message);
						ndefForm.close();

						return true;
					} catch (IOException | FormatException e) {
						throw new RuntimeException(e);
					}
				}
			}

			if (ndef != null) {
				try {
					ndef.connect();
					ndef.writeNdefMessage(message);
					ndef.close();

					return true;
				} catch (IOException | FormatException e) {
					throw new RuntimeException(e);
				}
			}
		}

		return false;
	}

	private static NdefRecord createRecord(String payload) {
		NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
				IntentConstants.MIME_TYPE.getBytes(),
				new byte[0],
				payload.getBytes());

		return record;
	}
}
