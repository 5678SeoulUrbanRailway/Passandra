package train.passandra.model;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class NaiveBayesianModel implements Serializable {

	private static final long serialVersionUID = 1L;
	HashMap<String, Integer> wordMap;
	int totalWordSize;

	public NaiveBayesianModel(HashMap<String, Integer> wordMap) {
		this.wordMap = wordMap;
		setTotalWordSize();
	}

	public int getTotalWordSize() {
		return totalWordSize;
	}

	private void setTotalWordSize() {
		Iterator<String> it = wordMap.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			totalWordSize += wordMap.get(key);
		}
	}

	public void print() {
		Iterator<String> it = wordMap.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			
			System.out.println(key + ": " + wordMap.get(key));
		}
		System.out.println("@totalSzie : "+this.totalWordSize);
	}

	public HashMap<String, Integer> getWordMap() {
		return wordMap;
	}

	public void setWordMap(HashMap<String, Integer> wordMap) {
		this.wordMap = wordMap;
	}

}
