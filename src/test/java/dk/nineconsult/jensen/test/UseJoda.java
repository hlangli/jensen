package dk.nineconsult.jensen.test;

import org.joda.time.DateTime;

/**
 * @author Rune Molin, rmo@nineconsult.dk
 */
public class UseJoda {
	protected static final String TEST_DATE = "2015-03-26T15:29:00.000+01:00";
	private final DateTime theDate;

	public UseJoda() {
		theDate = DateTime.parse(TEST_DATE);
	}

	public DateTime getTheDate() {
		return theDate;
	}
}
