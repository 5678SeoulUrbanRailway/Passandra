package train.passandra.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import train.passandra.io.CreateTrainData;
import train.passandra.preprocessing.WordSelect;

public class NaiveBayesian {
	private NaiveBayesianModel model;

	public NaiveBayesian(String keyword) {
		model = creatModel(keyword);
	}

	/**
	 *  
	 */
	public NaiveBayesianModel creatModel(String keyword) {
		WordSelect ws = new WordSelect(CreateTrainData.getInstance()
				.getContent(keyword), "StopWordList.data");

		// �떒�뼱 由ъ뒪�듃濡� 蹂��솚
		ArrayList<String> keywordList = ws.getSelectedWord();

		// <�떒�뼱,媛��닔> �삎�깭�쓽 �빐�돩留� 媛� �떒�뼱媛� 紐뉕컻 �룷�븿�맟�뒗吏�...
		HashMap<String, Integer> wordMap = new HashMap<String, Integer>();

		for (int i = 0; i < keywordList.size(); i++) {
			String word = keywordList.get(i);
			int cnt;
			try {
				cnt = wordMap.get(word);
				wordMap.put(word, cnt + 1);
			} catch (Exception e) {
				wordMap.put(word, 1);
			}
			// System.out.println(word);
		}

		return new NaiveBayesianModel(wordMap);

	}

	/**
	 * @param keyword
	 * @return correlation
	 */
	public int getCorrelation(String keyword) {
		// model.print();

		int searchPoint = compareModel(keyword);
		int modelPoint = model.getTotalWordSize();

		System.out.println("searchPoint : " + searchPoint + ", modelPoint : "
				+ modelPoint);
		// return compareModel(keyword)*100/model.getTotalWordSize();
		return searchPoint * 100 / modelPoint;
	}

	private int compareModel(String keyword) {
		HashMap<String, Integer> modelMap = model.getWordMap();
		HashMap<String, Integer> searchMap = creatModel(keyword).getWordMap();

		int searchMapPoint = 0;

		Iterator<String> it = searchMap.keySet().iterator();

		while (it.hasNext()) {
			String key = (String) it.next();
			if (modelMap.get(key) != null)
				searchMapPoint += modelMap.get(key);
		}

		return searchMapPoint;
	}

	public ArrayList<String> getRecommendWords(ArrayList<String> content) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (int i = 0; i < content.size(); i++)
			map.put(content.get(i), getCorrelation(content.get(i)));

		return sortByValue(map);
	}

	public ArrayList<String> sortByValue(final HashMap<String, Integer> map) {
		ArrayList<String> list = new ArrayList<String>();
		list.addAll(map.keySet());

		Collections.sort(list, new Comparator<Object>() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			public int compare(Object o1, Object o2) {
				Object v1 = map.get(o1);
				Object v2 = map.get(o2);

				return ((Comparable) v1).compareTo(v2);
			}

		});
		Collections.reverse(list);
		return list;
	}
}
