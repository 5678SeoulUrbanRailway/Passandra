package train.passandra.io;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CreateTrainData {
	private static CreateTrainData instance;

	private String wikiEngUri, wikiKorUri;
	private String regexTag, regexBraket, regexEng;

	public static synchronized CreateTrainData getInstance() {
		if (instance == null) {
			instance = new CreateTrainData();
		}
		return instance;
	}

	private CreateTrainData() {
		this.wikiEngUri = "https://en.wikipedia.org/?title=";
		this.wikiKorUri = "https://ko.wikipedia.org/wiki/";
		this.regexTag = "<[^>]*>"; // html tag 제거
		this.regexBraket = "[\\[\\d\\]]"; // [%d] 제거
		this.regexEng = "[^\\x00-\\x7F]"; // 영어 이외에 문자 제거
	}

	private String acceptRegex(String target) {
		Pattern pattern = Pattern.compile(regexTag);
		Matcher mat = pattern.matcher("");
		mat.reset(target);
		target = mat.replaceAll("");

		pattern = Pattern.compile(regexBraket);
		mat = pattern.matcher("");
		mat.reset(target);
		target = mat.replaceAll("");

		pattern = Pattern.compile(regexEng);
		mat = pattern.matcher("");
		mat.reset(target);
		target = mat.replaceAll("");
		return target;
	}

	/**
	 * 검색어 search의 타입 판별
	 * @param search
	 * @return 한글이면 F, 영어면 T
	 */
	private boolean checkStringType(String search) {
		for (int i = 0; i < search.length(); i++) {
			if (Character.getType(search.charAt(i)) == Character.OTHER_LETTER) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 웹 사이트에서 search에 대한 내용을 파싱하여 String형태로 반환한다.
	 * @param 검색할 내용
	 * @return search의 대한 검색 결과
	 */
	public String getContent(String search) {
		try {
			String uri = wikiKorUri;
			if (checkStringType(search)) {
				uri = wikiEngUri;
			}
			Document doc = Jsoup.connect(uri + search).userAgent("Mozilla/5.0").get();
			Elements selectByTag = doc.select("li"); // li 태그 내용 선택

			String content = selectByTag.html(); // 해당 태그 제거
			content = acceptRegex(content); // 정규식 적용

			return content;
		} catch (IOException e) {
			return "";
		}
	}
}
