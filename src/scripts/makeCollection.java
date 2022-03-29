package scripts;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class makeCollection {

	private String data_path;
	private String output_file = "./collection.xml";

	public makeCollection(String path) {
		this.data_path = path;
	}

	public void makeXml() throws Exception {
		// 폴더 내 파일 리스트 생성
		File dir = new File(data_path);
		File[] files = dir.listFiles();

		// 새로운 XML 생성
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document collection = docBuilder.newDocument();

		// docs element
		Element docs = collection.createElement("docs");
		collection.appendChild(docs);

		for (int i = 0; i < files.length; i++) {
			File file = new File(files[i].getPath());
			org.jsoup.nodes.Document html = Jsoup.parse(file, "utf-8");

			String titleData = html.title();
			String bodyData = html.body().text();

			// doc element
			Element doc = collection.createElement("doc");
			docs.appendChild(doc);
			doc.setAttribute("id", Integer.toString(i));

			// title element
			Element title = collection.createElement("title");
			title.appendChild(collection.createTextNode(titleData));
			doc.appendChild(title);

			// body element
			Element body = collection.createElement("body");
			body.appendChild(collection.createTextNode(bodyData));
			doc.appendChild(body);
		}

		// XML 파일로 쓰기
		TransformerFactory transformerFactory = TransformerFactory.newInstance();

		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");

		DOMSource source = new DOMSource(collection);
		StreamResult result = new StreamResult(new FileOutputStream(new File(output_file)));

		transformer.transform(source, result);
		System.out.println("2주차 실행완료");
	}
}