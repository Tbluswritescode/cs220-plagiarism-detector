package plagdetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class PlagiarismDetector implements IPlagiarismDetector {
	private int N = 3;
	private Map<String, Map<String, Integer>> results;
	private Map<String, Set<String>> allNgrams;
	private Set<String> recentFile;

	public PlagiarismDetector(int n) {
		results = new HashMap<String, Map<String, Integer>>();
		allNgrams = new HashMap<String, Set<String>>();
	}
	
	@Override
	public int getN() {
		return N;
	}

	@Override
	public Collection<String> getFilenames() {
		return allNgrams.keySet();
	}

	@Override
	public Collection<String> getNgramsInFile(String filename) {
		readFile(filename);
		return allNgrams.get(filename);
	}

	@Override
	public int getNumNgramsInFile(String filename) {
		return allNgrams.get(filename).size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		// TODO Auto-generated method stub
		return null;
	}

	public void readFile(String filename){
		File file = new File(filename);
		readFile(file);
		allNgrams.add(filename, recentFile);
		recentFile = new Set<String>();
	}

	@Override
	public void readFile(File file) throws IOException {
		// TODO Auto-generated method stub
		// most of your work can happen in this method
		try {
			Set<String> fileNgrams = new HashSet<String>();
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
			  String line = scan.nextLine();
			  for (int i = 0; i < line.length() - N; i++){
				recentFile.add(line.substring(i, i+N));
			  }
			
			for (Set<String> e : allNgrams.values()){
				for (String s : e){
					if (recentFile.contains(s)){
						count += 1;
					}
				}

			}
		}
		}
		catch (FileNotFoundException e) {

		}


		
	}

	@Override
	public int getNumNGramsInCommon(String file1, String file2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readFilesInDirectory(File dir) throws IOException {
		// delegation!
		// just go through each file in the directory, and delegate
		// to the method for reading a file
		for (File f : dir.listFiles()) {
			readFile(f);
		}
	}
}
