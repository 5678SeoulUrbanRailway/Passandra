package train.passandra.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import train.passandra.io.CreateTrainDataContent;
import train.passandra.io.FileObjectIO;
import train.passandra.preprocessing.WordSelect;

public class NaiveBayesian {
	NaiveBayesianModel model;
	String path;

	public NaiveBayesian(String keyword, String modelSavePath) {
		path = modelSavePath;

		model = (NaiveBayesianModel) FileObjectIO.FileToObject(modelSavePath);

		if (model == null) {
			model = creatModel(keyword);
			saveModel();
		}

	}

	/**
	 *  
	 */
	public NaiveBayesianModel creatModel(String keyword) {

		WordSelect ws = new WordSelect(CreateTrainDataContent.getInstance().get(keyword), "StopWordList.data");

		// 단어 리스트로 변환
		ArrayList<String> keywordList = ws.getSelectedWord();

		// <단어,갯수> 형태의 해쉬맵 각 단어가 몇개 포함됬는지...
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
			//			System.out.println(word);
		}

		return new NaiveBayesianModel(wordMap);

	}

	public void saveModel() {
		FileObjectIO.ObjectToFile(path, model);
	}

	public int getCorrelation(String keyword) {
		//		model.print();

		int searchPoint = compareModel(keyword);
		int modelPoint = model.getTotalWordSize();

		System.out.println("searchPoint : " + searchPoint + ", modelPoint : " + modelPoint);
		//		return compareModel(keyword)*100/model.getTotalWordSize();
		return searchPoint * 100 / modelPoint;
	}

	public int compareModel(String keyword) {
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
