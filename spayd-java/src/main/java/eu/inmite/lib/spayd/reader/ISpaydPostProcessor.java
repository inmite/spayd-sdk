package eu.inmite.lib.spayd.reader;

import java.util.Map;

/**
 * @author Tomas Vondracek
 */
public interface ISpaydPostProcessor {

	void processAttributes(Map<String, String> mutableAttrs);
}
