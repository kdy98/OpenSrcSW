package scripts;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class indexer {

	private String input_file;
	private String output_flie = "./index.post";

	public indexer(String file) {
		this.input_file = file;
	}

	public void makeHashMap() throws Exception {
		// index.xml 파일 파싱
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document index = docBuilder.parse(input_file);

		int docN = index.getElementsByTagName("doc").getLength(); // 전체 문서 개수
		NodeList bodyList = index.getElementsByTagName("body");

		// Hashmap 만들기
		HashMap<String, Object> hash = new HashMap<String, Object>();

		for (int i = 0; i < docN; i++) {
			String bodyData = bodyList.item(i).getTextContent();
			String[] datas = bodyData.split("#");
			for (int j = 0; j < datas.length; j++) {
				String[] data = datas[j].split(":");
				String key = data[0];
				int tfxy = Integer.parseInt(data[1]);

				// 처음 등장하는 키워드
				if (!hash.containsKey(key)) {
					ArrayList<Object> numberData = new ArrayList<Object>();

					// hashmap에 value로 저장할 arrayList 초기화
					for (int k = 0; k < docN; k++) {
						numberData.add(k);
						numberData.add(0);
					}
					numberData.set(i * 2 + 1, tfxy);
					numberData.add(1); // 배열 마지막 index에서 dfx 카운트
					hash.put(key, numberData);

				} else { // 이미 존재하는 키워드
					ArrayList<Object> tmpArr = (ArrayList<Object>) hash.get(key);
					tmpArr.set(i * 2 + 1, tfxy);
					tmpArr.set(docN * 2, (int) tmpArr.get(docN * 2) + 1);
				}
			}
		}

		// 최종으로 구해진 dfx로 가중치 계산
		Iterator<String> it = hash.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			int dfx = (int) ((ArrayList<Object>) hash.get(key)).get(docN * 2);

			ArrayList<Object> tmpArr = (ArrayList<Object>) hash.get(key);
			for (int i = 0; i < docN; i++) {
				int tf = (int) tmpArr.get(2 * i + 1);
				double idf = Math.log((double) docN / dfx);
				double w = tf * idf;
				w = ((double) Math.round(w * 100)) / 100; // 소수점 셋째 자리에서 반올림
				tmpArr.set(i * 2 + 1, w);
			}
			tmpArr.remove(docN * 2);
			hash.replace(key, tmpArr);
		}

		// 저장하기
		FileOutputStream fileStream = new FileOutputStream(output_flie);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
		objectOutputStream.writeObject(hash);
		objectOutputStream.close();

		// 읽어와서 출력 테스트
		FileInputStream InputfileStream = new FileInputStream("index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(InputfileStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();

		HashMap testHash = (HashMap) object;
		Iterator<String> testIt = testHash.keySet().iterator();

		System.out.println("테스트 출력");
		while (testIt.hasNext()) {
			String key = testIt.next();
			System.out.print(key + " -> ");
			for (int i = 0; i < ((ArrayList<Object>) testHash.get(key)).size(); i++) {
				System.out.print(((ArrayList<Object>) testHash.get(key)).get(i) + " ");
			}
			System.out.println();
		}
		System.out.println("4주차 실행완료");
	}
}
