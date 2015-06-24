package train.passandra.preprocessing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class WordSelect {
    private ArrayList<String> words, exceptKeyword;
    private String inputString, stopWordsPath;
    
    public WordSelect(String inputString, String stopWordsPath) {
        this.words = new ArrayList<String>();
        this.exceptKeyword = new ArrayList<String>();
        this.inputString = inputString;
        this.stopWordsPath = stopWordsPath;
        readStopWordList();
        selectOCRString();
    }
    
    /**
     * 불용어 목록 읽어와서 저장한다.
     */
    private void readStopWordList() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(stopWordsPath));
            
            String inBuf = "";
            while ((inBuf = in.readLine()) != null) {
                exceptKeyword.add(inBuf.trim());
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 스트링이 예외 키워드에 포함되는지를 검사한다
     *
     * @param word 검사할 키워드
     * @return 포함 유무
     */
    private boolean processingKeyword(String word) {
        Iterator<String> it = exceptKeyword.iterator();
        
        while (it.hasNext()) {
            String except = it.next();
            if (word.equals(except)) {
                return false;
            }
            
            if (word.length() <= 1 || word.matches(".*\\d.*")) {
                return false;
            }
        }
        
        return true;
    }
    
    private void selectOCRString() {
        String[] lines = inputString.split("\n");
        for (String line : lines) {
            String[] splitWhiteSpace = line.split(" ");
            for (String element : splitWhiteSpace) {
                element = exceptionWord(element);
                
                if (element != null) {
                    words.add(element);
                }
                
                /*
                 * if (typeFlag) { words.add(element);
                 * System.out.println(element); }
                 */
            }
        }
        
        combineWord();
        //		words = new ArrayList<String>(new HashSet(words)); // 중복 단어 제거
        
        // System.out.println(words.toString());
    }
    
    public void setInputString(String inputString) {
        this.inputString = inputString;
        words.clear();
        selectOCRString();
    }
    
    public ArrayList<String> getSelectedWord() {
        return words;
    }
    
    private void combineWord() { // 개행으로 인해 분해된 단어 합치기 ex) argo-(\n)rithm ->argorithm
        String tmp, tmp2;
        for (int i = words.size() - 1; i > 0; i--) {
            tmp = words.get(i);
            if (tmp.charAt(tmp.length() - 1) == '-' && i != words.size() - 1) {
                tmp2 = words.get(i + 1);
                tmp = tmp.substring(0, tmp.length() - 1) + tmp2;
                
                words.set(i, tmp);
                words.remove(i + 1);
            }
        }
    }
    
    private String exceptionWord(String element) {
        String originElement;
        
        char lastLetter = 0;
        if (element.length() > 2)
            lastLetter = element.charAt(element.length() - 1);
        if (!Character.isLetterOrDigit(lastLetter))
            if (element.length() > 2)
                if (lastLetter != '-')
                    element = element.substring(0, element.length() - 1);
        
        originElement = element;
        originElement = originElement.trim();
        
        for (int i = 0; i < element.length(); i++) {
            char tmp = element.charAt(i);
            
            if (!Character.isLetterOrDigit(tmp)) {
                if (!(tmp == '-' || tmp == '_'))
                    return null;
                else if (element.length() == 1)
                    return null;
            }
        }
        
        element = element.toLowerCase();
        element = element.trim();
        if (processingKeyword(element) && element.length() > 0) {
            return originElement;
        } else {
            return null;
        }
    }
}