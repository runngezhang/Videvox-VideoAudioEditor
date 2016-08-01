package festivalEnums;

/**
 * The FestivalPitch class handles the selection of the pitch for synthesized
 * text through festival and has methods to extract command settings for setting
 * the pitch when playing festival or creating wav files with the text2wave
 * command.
 * 
 * @author jay
 *
 */
public enum FestivalPitch {
	HIGH, NORMAL, LOW, HAPPY, SAD;

	/**
	 * Returns the string with command settings to be piped into festival to set
	 * pitch
	 * 
	 * @return String festivalCommand
	 */
	public String getFestValue() {
		switch (this) {
		case HIGH:
			return "(set! duffint_params '((start 130) (end 130))) (Parameter.set 'Int_Method 'DuffInt) (Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case NORMAL:
			return "(set! duffint_params '((start 105) (end 105))) (Parameter.set 'Int_Method 'DuffInt) (Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case LOW:
			return "(set! duffint_params '((start 80) (end 80))) (Parameter.set 'Int_Method 'DuffInt) (Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case HAPPY:
			return "(set! duffint_params '((start 135) (end 110))) (Parameter.set 'Int_Method 'DuffInt) (Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case SAD:
			return "(set! duffint_params '((start 100) (end 85))) (Parameter.set 'Int_Method 'DuffInt) (Parameter.set 'Int_Target_Method Int_Targets_Default)";
		}
		return "(set! duffint_params '((start 105) (end 105))) (Parameter.set 'Int_Method 'DuffInt) (Parameter.set 'Int_Target_Method Int_Targets_Default)";
	}

	/**
	 * Returns the string with command settings to be piped into the text2wave
	 * command when creating wav files from festival
	 * 
	 * @return String text2waveCommand
	 */
	public String get2WaveValue() {
		switch (this) {
		case HIGH:
			return "(set! duffint_params '((start 130) (end 130))) (Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case NORMAL:
			return "(set! duffint_params '((start 105) (end 105))) (Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case LOW:
			return "(set! duffint_params '((start 80) (end 80))) (Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case HAPPY:
			return "(set! duffint_params '((start 135) (end 110))) (Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)";
		case SAD:
			return "(set! duffint_params '((start 100) (end 85))) (Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)";
		}
		return "(set! duffint_params '((start 105) (end 105))) (Parameter.set 'Int_Method 'DuffInt)\" -eval \"(Parameter.set 'Int_Target_Method Int_Targets_Default)";
	}
	
	/**
	 * Gets the appropriate enum for a string
	 * @param pitch
	 * @return FestivalPitch
	 */
	public static FestivalPitch asString(String pitch) {
		switch (pitch) {
		case "HIGH":
			return FestivalPitch.HIGH;
		case "NORMAL":
			return FestivalPitch.NORMAL;
		case "LOW":
			return FestivalPitch.LOW;
		case "HAPPY":
			return FestivalPitch.HAPPY;
		case "SAD":
			return FestivalPitch.SAD;
		}
		return FestivalPitch.NORMAL;
	}
}
