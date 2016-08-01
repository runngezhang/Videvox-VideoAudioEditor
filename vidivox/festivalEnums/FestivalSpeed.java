package festivalEnums;

/**
 * The FestivalSpeed class handles the selection of the speed for synthesized
 * text through festival and has methods to extract command settings for setting
 * the speed when playing festival or creating wav files with the text2wave
 * command.
 * @author jay
 *
 */
public enum FestivalSpeed {
	FAST, NORMAL, SLOW, HAPPY, SAD;
	
	/**
	 * Returns the string with command settings to be piped into festival to set
	 * speed
	 * 
	 * @return String festivalCommand
	 */
	public String getValue() {
		switch (this) {
		case FAST:
			return "(Parameter.set 'Duration_Stretch 0.6)";
		case NORMAL:
			return "(Parameter.set 'Duration_Stretch 1)";
		case SLOW:
			return "(Parameter.set 'Duration_Stretch 1.4)";
		case HAPPY:
			return "(Parameter.set 'Duration_Stretch 0.8)";
		case SAD:
			return "(Parameter.set 'Duration_Stretch 1.2)";
		}
		return "(Parameter.set 'Duration_Stretch 1)";
	}
	
	/**
	 * Gets the appropriate enum for a string
	 * @param pitch
	 * @return FestivalSpeed
	 */
	public static FestivalSpeed asString(String speed) {
		switch (speed) {
		case "FAST":
			return FestivalSpeed.FAST;
		case "NORMAL":
			return FestivalSpeed.NORMAL;
		case "SLOW":
			return FestivalSpeed.SLOW;
		case "HAPPY":
			return FestivalSpeed.HAPPY;
		case "SAD":
			return FestivalSpeed.SAD;
		}
		return FestivalSpeed.NORMAL;
	}
}
