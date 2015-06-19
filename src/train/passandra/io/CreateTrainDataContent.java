package train.passandra.io;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class CreateTrainDataContent {
	private static CreateTrainDataContent instance;
	private String wikiUri;
	private String regexTag; // html tag 제거
	private String regexBraket; // [%d] 제거
	private String regexEng; // 영어 이외에 문자 제거

	public static synchronized CreateTrainDataContent getInstance() {
		if (instance == null) {
			instance = new CreateTrainDataContent();
		}

		return instance;
	}

	private CreateTrainDataContent() {
		this.wikiUri = "https://en.wikipedia.org/?title=";
		this.regexTag = "<[^>]*>";
		this.regexBraket = "[\\[\\d\\]]";
		this.regexEng = "[^\\x00-\\x7F]";
	}

	private String removeRex(String target) {
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

	public String get(String search) {
		try {
			Document doc = Jsoup.connect(wikiUri + search).userAgent("Mozilla/5.0").get();
			Elements selectByTag = doc.select("li"); // li 태그 내용 선택

			String content = selectByTag.html(); // 해당 태그 제거
			content = removeRex(content); // 정규식 적용

			return content;
		} catch (IOException e) {
			return "";
		}
	}
}
