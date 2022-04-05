package scripts;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class searcher {

	private String input_file;

	public searcher(String file) {
		this.input_file = file;
	}

	void CalcSim(String query) throws Exception {

		// index.post 불러오기
		FileInputStream InputfileStream = new FileInputStream(input_file);
		ObjectInputStream objectInputStream = new ObjectInputStream(InputfileStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();

		HashMap hashMap = (HashMap) object;

		// index.xml 불러오기
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document index = docBuilder.parse("index.xml");

		int docN = index.getElementsByTagName("doc").getLength(); // 전체 문서 개수
		NodeList titleList = index.getElementsByTagName("title"); // title 목록

		// 쿼리 분해
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);

		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Integer> keyWeights = new ArrayList<Integer>();
		ArrayList<Double> sims = new ArrayList<Double>();

		for (int i = 0; i < kl.size(); i++) {
			Keyword kwrd = kl.get(i);
			keys.add(kwrd.getString());
			keyWeights.add(kwrd.getCnt());
		}

		// 내적 계산
		for (int i = 0; i < docN; i++) {
			double sim = 0;
			for (int j = 0; j < keys.size(); j++) {
				String key = keys.get(j);
				double keyWeight = keyWeights.get(j);
				double hashWeight;
				if (hashMap.get(key) == null) {
					hashWeight = 0;
				} else {
					hashWeight = (double) ((ArrayList<Object>) hashMap.get(key)).get(i * 2 + 1);
				}
				sim += keyWeight * hashWeight;
			}
			sims.add(sim);
		}

		// 가장 높은 3개 출력
		int maxIndex;
		double maxValue;

		for (int i = 0; i < 3; i++) {
			maxIndex = -1;
			maxValue = 0;
			for (int j = 0; j < docN; j++) {
				if (sims.get(j) > maxValue) {
					maxIndex = j;
					maxValue = sims.get(j);
				}
			}
			if (maxValue != 0) {
				System.out.print(titleList.item(maxIndex).getTextContent());
				System.out.printf("(유사도:%.2f)\n", maxValue);
				sims.set(maxIndex, 0.0);
			} else {
				if (i == 0) {
					System.out.println("검색된 문서가 없습니다.");
				}
				break;
			}
		}
	}
}
