package scripts;

public class kuir {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		String command = args[0];
		String path = args[1];

		if (command.equals("-c")) {
			makeCollection collection = new makeCollection(path);
			collection.makeXml();
		} else if (command.equals("-k")) {
			makeKeyword keyword = new makeKeyword(path);
			keyword.convertXml();
		} else if (command.equals("-i")) {
			indexer keyword = new indexer(path);
			keyword.makeHashMap();
		} else if (command.equals("-s")) {
			searcher searcher = new searcher(path);
			searcher.CalcSim(args[3]);
		} else if (command.equals("-m")) {
			MidTerm midterm = new MidTerm(path);
			midterm.showSnippet(args[3]);
		}
	}
}