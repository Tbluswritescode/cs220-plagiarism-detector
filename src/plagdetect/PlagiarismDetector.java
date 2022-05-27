package plagdetect;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class PlagiarismDetector implements IPlagiarismDetector {
	private int N = 3;
	private Map<String, Map<String, Integer>> results;
	private Map<String, Set<String>> allNgrams;
	private Set<String> recentFile = new HashSet<String>();

	public PlagiarismDetector(int n) {
		results = new HashMap<String, Map<String, Integer>>();
		allNgrams = new HashMap<String, Set<String>>();
		N = n;
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
		return allNgrams.get(filename);
	}

	@Override
	public int getNumNgramsInFile(String filename) {
		return allNgrams.get(filename).size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		return results;
	}

	public void readFile(String filename) {
		File file = new File(filename);
		try {
			readFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void update(String filename) {
		allNgrams.put(filename, new HashSet<String>(recentFile));
		recentFile.clear();
		HashMap<String, Integer> a = new HashMap<String, Integer>();
		for (Map.Entry<String, Set<String>> e : allNgrams.entrySet()) {
			a.put(e.getKey(), getNumNGramsInCommon(filename, e.getKey()));
			results.put(filename, a);
		}
	}
	
	private void updateResults() {
		for (Map.Entry<String, Set<String>> e : allNgrams.entrySet()) {
			for (Map.Entry<String, Set<String>> f : allNgrams.entrySet()) {
				if (e.getKey() != f.getKey() && results.get(e.getKey()) != null) {
					results.get(e.getKey()).put(f.getKey(), getNumNGramsInCommon(e.getKey(), f.getKey()));
				}
				else {
					Map<String, Integer> m = new HashMap<String, Integer>();
					m.put(f.getKey(), getNumNGramsInCommon(e.getKey(), f.getKey()));
					results.put(e.getKey(), m);
				}
			}
		}
	}

	@Override
	public void readFile(File file) throws IOException {
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] words = line.split(" ");
				for (int i = 0; i <= words.length-N; i++) {
					String s = "";
					for (int j = 0; j < N; j++) {
						s += words[i+j] + " ";
					}
					recentFile.add(s);
					s="";
				}
			}
			update(file.getName());
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		

	}

	@Override
	public int getNumNGramsInCommon(String filename1, String filename2) {
		Set<String> s = new HashSet<String>(allNgrams.get(filename1));
		s.retainAll(allNgrams.get(filename2));
		return s.size();
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		// TODO Auto-generated method stub
		updateResults();
		Set<String> sus = new HashSet<String>();
		for (Map.Entry<String, Map<String, Integer>> e : results.entrySet()) {
			for (Map.Entry<String, Integer> s : e.getValue().entrySet()) {
				List<String> filenames = new ArrayList<String>();
				if (s.getValue() >= minNgrams && e.getKey() != s.getKey()) {
					filenames.add(e.getKey());
					filenames.add(s.getKey());
					Collections.sort(filenames);
					sus.add(filenames.get(0) + " " + filenames.get(1) + " " + s.getValue() + " - " + (allNgrams.get(filenames.get(0)).size() + allNgrams.get(filenames.get(1)).size()) / 2 );
				}
			}
		}
		return sus;
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
