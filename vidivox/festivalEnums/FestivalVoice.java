package festivalEnums;

/**
 * The FestivalSpeed class handles the selection of the voices for synthesized
 * text through festival and has methods to extract command settings for setting
 * the voice when playing festival or creating wav files with the text2wave
 * command.
 * @author jay
 *
 */
public enum FestivalVoice {
	KAL, DON, KED, RAB;
	
	/**
	 * Returns the string with command settings to be piped into festival to set
	 * voice
	 * 
	 * @return String festivalCommand
	 */
	public String getValue() {
		switch (this) {
		case KAL:
			return "(voice_kal_diphone)";
		case DON:
			return "(voice_don_diphone)";
		case KED:
			return "(voice_ked_diphone)";
		case RAB:
			return "(voice_rab_diphone)";
		}
		return "(voice_kal_diphone)";
	}
	
	/**
	 * Gets the appropriate enum for a string
	 * @param pitch
	 * @return FestivalSpeed
	 */
	public static FestivalVoice asString(String voice) {
		switch (voice) {
		case "KAL":
			return FestivalVoice.KAL;
		case "DON":
			return FestivalVoice.DON;
		case "KED":
			return FestivalVoice.KED;
		case "RAB":
			return FestivalVoice.RAB;
		}
		return FestivalVoice.KAL;
	}
}
