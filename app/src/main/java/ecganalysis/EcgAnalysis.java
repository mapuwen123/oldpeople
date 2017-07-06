package ecganalysis;

public class EcgAnalysis {
	static {
		System.loadLibrary("EcgAnalysis");
	}
	public native void EcgAnalysisInit(int fs,int baseline,int range1mv);
	public native int[] EcgFilterMain(int[] ecg,int length);
	public native int[][] EcgAnalysisMain(int[] ecg,int length);
	public native AnalysisResult EcgAnalysisMain2(int[] ecg, int length);
	public native void Finish();
}

