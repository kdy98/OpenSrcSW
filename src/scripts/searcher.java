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

	public void CalcSim(String query) throws Exception {

		// query 분해
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);

		ArrayList<String> keywords = new ArrayList<String>();
		ArrayList<Integer> keyWeights = new ArrayList<Integer>();

		for (int j = 0; j < kl.size(); j++) {
			Keyword kwrd = kl.get(j);
			keywords.add(kwrd.getString());
			keyWeights.add(kwrd.getCnt());
		}

		// index.post 불러오기
		FileInputStream InputfileStream = new FileInputStream("index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(InputfileStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();

		HashMap hash = (HashMap) object;

		// index.xml 불러오기
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document index = docBuilder.parse("index.xml");

		int docN = index.getElementsByTagName("doc").getLength(); // 전체 문서 개수
		NodeList titleList = index.getElementsByTagName("title"); // title 목록

		// Sim 계산
		ArrayList<Double> sims = new ArrayList<Double>();

		double queryVectorSize = 0;
		for (int i = 0; i < keywords.size(); i++) {
			queryVectorSize += keyWeights.get(i) * keyWeights.get(i);
		}
		queryVectorSize = Math.sqrt(queryVectorSize); // query vector 크기

		for (int i = 0; i < docN; i++) {
			double docVectorSize = 0;
			for (int j = 0; j < keywords.size(); j++) {
				String key = keywords.get(j);
				double docKeyWeight;

				if (hash.get(key) == null) {
					docKeyWeight = 0;
				} else {
					docKeyWeight = (double) ((ArrayList<Object>) hash.get(key)).get(i * 2 + 1);
				}
				docVectorSize += docKeyWeight * docKeyWeight;
			}
			docVectorSize = Math.sqrt(docVectorSize); // index.post의 keyweight vector 크기

			double innerProduct = InnerProduct(keywords, keyWeights, hash, i);
			double sim = innerProduct / (queryVectorSize * docVectorSize);
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

	double InnerProduct(ArrayList<String> keys, ArrayList<Integer> keyWeights, HashMap hashMap, int docNumber)
			throws Exception {
		// 내적 계산
		double sim = 0;
		for (int j = 0; j < keys.size(); j++) {
			String key = keys.get(j);
			double keyWeight = keyWeights.get(j);
			double hashWeight;
			if (hashMap.get(key) == null) {
				hashWeight = 0;
			} else {
				hashWeight = (double) ((ArrayList<Object>) hashMap.get(key)).get(docNumber * 2 + 1);
			}
			sim += keyWeight * hashWeight;
		}
		return sim;
	}
}
