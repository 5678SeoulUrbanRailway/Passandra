package client;

import java.util.ArrayList;

import train.passandra.model.NaiveBayesian;

public class PassandraTest {
	public static void main(String[] args) {
<<<<<<< HEAD
		NaiveBayesian test = new NaiveBayesian("man", "C:\\Passandra\\navie.model");
=======
		NaiveBayesian test = new NaiveBayesian("man");
>>>>>>> origin/PBC

		System.out.println("Correlation : " + test.getCorrelation("woman"));
		System.out.println("Correlation : " + test.getCorrelation("rain"));

		ArrayList<String> al = new ArrayList<String>();

		al.add("woman");
		al.add("sex");
		al.add("wolf");
		al.add("cup");
		al.add("bust");
		al.add("hip");

		System.out.println(test.getRecommendWords(al));
	}
}
