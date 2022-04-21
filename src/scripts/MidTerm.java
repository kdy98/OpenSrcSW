package scripts;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class MidTerm {
	private String input_file;

	public MidTerm(String path) {
		this.input_file = path;
	}

	public void showSnippet(String query) throws Exception {
		KeywordExtractor ke = new KeywordExtractor();
		KeywordList kl = ke.extractKeyword(query, true);

		ArrayList<String> keywords = new ArrayList<String>();

		for (int j = 0; j < kl.size(); j++) {
			Keyword kwrd = kl.get(j);
			keywords.add(kwrd.getString());
			// str += kwrd.getString() + ":" + kwrd.getCnt() + "#";
		}
		
//		for(int i=0;i<keywords.size();i++) {
//			System.out.println(keywords.get(i));
//		}

		// collection.xml 파일 파싱
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		
		Document collection = docBuilder.parse(input_file);

		NodeList docList = collection.getElementsByTagName("doc");
		NodeList titleList = collection.getElementsByTagName("title");
		NodeList bodyList = collection.getElementsByTagName("body");
		int docN=docList.getLength();
		
		ArrayList<Integer> counts=new ArrayList<Integer>();
		ArrayList<String> snippets=new ArrayList<String>();
			
		for(int i=0;i<docN;i++) {
			String bodyData=bodyList.item(i).getTextContent();
			int maxCount=0;
			String maxSnippet="";
			
			for(int j=0;j<bodyData.length();j++) {
				String snippet="";
				int lastindex;
				if(j+30>bodyData.length()) {
					lastindex=bodyData.length();
				}
				else {
					lastindex=j+30;
				}
				snippet=bodyData.substring(j, lastindex);

				int count=0;
				
				for(int k=0;k<keywords.size();k++) {
	
					if(snippet.contains(keywords.get(k))) {
						count++;
					}
				}

				if(count>maxCount) {
					maxCount=count;
					maxSnippet=snippet;
				}			
			}			
			counts.add(maxCount);
			snippets.add(maxSnippet);
		}

		
		for(int i=0;i<docN;i++) {
			if(counts.get(i)>0) {
				System.out.println(titleList.item(i).getTextContent()+","+snippets.get(i)+","+counts.get(i));
			}
		}
	}
}
