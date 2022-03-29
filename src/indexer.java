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

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// index.xml ���� �Ľ�
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document index = docBuilder.parse("index.xml");

		int docN = index.getElementsByTagName("doc").getLength(); // ��ü ���� ����
		NodeList bodyList = index.getElementsByTagName("body");

		// Hashmap �����
		HashMap<String, Object> hash = new HashMap<String, Object>();

		for (int i = 0; i < docN; i++) {
			String bodyData = bodyList.item(i).getTextContent();
			String[] datas = bodyData.split("#");
			for (int j = 0; j < datas.length; j++) {
				String[] data = datas[j].split(":");
				String key = data[0];
				int tfxy = Integer.parseInt(data[1]);

				// ó�� �����ϴ� Ű����
				if (!hash.containsKey(key)) {
					ArrayList<Object> numberData = new ArrayList<Object>();

					// hashmap�� value�� ������ arrayList �ʱ�ȭ
					for (int k = 0; k < docN; k++) {
						numberData.add(k);
						numberData.add(0);
					}
					numberData.set(i * 2 + 1, tfxy);
					numberData.add(1); // �迭 ������ index���� dfx ī��Ʈ
					hash.put(key, numberData);

				} else { // �̹� �����ϴ� Ű����
					ArrayList<Object> tmpArr = (ArrayList<Object>) hash.get(key);
					tmpArr.set(i * 2 + 1, tfxy);
					tmpArr.set(docN * 2, (int) tmpArr.get(docN * 2) + 1);
				}
			}
		}

		// �������� ������ dfx�� ����ġ ���
		Iterator<String> it = hash.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			int dfx = (int) ((ArrayList<Object>) hash.get(key)).get(docN * 2);

			ArrayList<Object> tmpArr = (ArrayList<Object>) hash.get(key);
			for (int i = 0; i < docN; i++) {
				int tfxy = (int) tmpArr.get(2 * i + 1);
				double wxy = tfxy * Math.log((double) docN / dfx);
				wxy = ((double) Math.round(wxy * 100)) / 100; // �Ҽ��� ��° �ڸ����� �ݿø�
				tmpArr.set(i * 2 + 1, wxy);
			}
			tmpArr.remove(docN * 2);
			hash.replace(key, tmpArr);
		}

		// �����ϱ�
		FileOutputStream fileStream = new FileOutputStream("index.post");
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileStream);
		objectOutputStream.writeObject(hash);
		objectOutputStream.close();

		// �о�ͼ� ��� �׽�Ʈ
		FileInputStream InputfileStream = new FileInputStream("index.post");
		ObjectInputStream objectInputStream = new ObjectInputStream(InputfileStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();

		HashMap testHash = (HashMap) object;
		Iterator<String> testIt = testHash.keySet().iterator();

		while (testIt.hasNext()) {
			String key = testIt.next();
			System.out.print(key + " -> ");
			for (int i = 0; i < ((ArrayList<Object>) testHash.get(key)).size(); i++) {
				System.out.print(((ArrayList<Object>) testHash.get(key)).get(i) + " ");
			}
			System.out.println();
		}
	}
}
