package vidivoxManagers;

/**
 * Used to determine the progress of swing worker processes through process
 * publish methods
 * 
 * @author jay
 *
 */
public class ExportProgressMonitor {
	private int EDITING_PROGRESS = 0;
	private boolean _done = true;

	public void publishEditProgress(int i) {
		EDITING_PROGRESS = i;
	}

	public int getEditProgress() {
		return EDITING_PROGRESS;
	}

	public void setDone(boolean done) {
		_done = done;
	}

	public boolean isDone() {
		return _done;
	}
}
