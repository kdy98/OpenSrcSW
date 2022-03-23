import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class kkma {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		// 새로운 XML 생성 (for index.xml)
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document index = docBuilder.newDocument();

		// collection.xml 파일 파싱
		String path = "C:\\Users\\kim\\Desktop\\openSW\\SimpleIR\\collection.xml";
		Document collection = docBuilder.parse(path);

		NodeList docList = collection.getElementsByTagName("doc");
		NodeList titleList = collection.getElementsByTagName("title");
		NodeList bodyList = collection.getElementsByTagName("body");

		// docs element
		Element docs = index.createElement("docs");
		index.appendChild(docs);

		for (int i = 0; i < docList.getLength(); i++) {
			// doc element
			Element doc = index.createElement("doc");
			doc.setAttribute("id", Integer.toString(i));
			docs.appendChild(doc);

			// title element
			String titleData = titleList.item(i).getTextContent();

			Element title = index.createElement("title");
			title.appendChild(index.createTextNode(titleData));
			doc.appendChild(title);

			// collection.xml 파일의 body 내용을 형태소 분석
			String bodyData = bodyList.item(i).getTextContent();

			KeywordExtractor ke = new KeywordExtractor();
			KeywordList kl = ke.extractKeyword(bodyData, true);

			String str = "";
			for (int j = 0; j < kl.size(); j++) {
				Keyword kwrd = kl.get(j);
				str += kwrd.getString() + ":" + kwrd.getCnt() + "#";
			}

			// body element
			Element body = index.createElement("body");
			body.appendChild(index.createTextNode(str));
			doc.appendChild(body);
		}

		// XML 파일로 쓰기
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(index);
		StreamResult result = new StreamResult(
				new FileOutputStream(new File("C:\\Users\\kim\\Desktop\\openSW\\SimpleIR\\index.xml")));

		transformer.transform(source, result);
		System.out.println("xml파일 생성 완료");
	}
}