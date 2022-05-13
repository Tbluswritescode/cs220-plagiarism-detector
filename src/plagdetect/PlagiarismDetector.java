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
		for (Map.Entry<String, Set<String>> e : allNgrams.entrySet()) {
			for (Map.Entry<String, Set<String>> f : allNgrams.entrySet()) {
				if (e.getKey() != f.getKey()) {

				}
			}
		}
		return null;
	}

	public void readFile(String filename) {
		File file = new File(filename);
		try {
			readFile(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		allNgrams.put(filename, recentFile);
		HashMap<String, Integer> a = new HashMap<String, Integer>();
		for (Map.Entry<String, Set<String>> e : allNgrams.entrySet()) {
			a.put(e.getKey(), getNumNGramsInCommon(filename, e.getKey()));
			results.put(filename, a);
		}
		recentFile = new HashSet<String>();
	}

	@Override
	public void readFile(File file) throws IOException {
		// TODO Auto-generated method stub
		// most of your work can happen in this method
		try {
			Scanner scan = new Scanner(file);
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				for (int i = 0; i < line.length() - N; i++) {
					recentFile.add(line.substring(i, i + N));
				}

			}
			scan.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int getNumNGramsInCommon(String filename1, String filename2) {
		int rv = 0;
		if (allNgrams.get(filename1) != null && allNgrams.get(filename2) != null) {
			for (String a : allNgrams.get(filename1)) {
				if (allNgrams.get(filename2).contains(a)) {
					rv += 1;
				}
			}
		}
		return rv;
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		// TODO Auto-generated method stub
		Set<String> sus = new HashSet<String>();
		for (Map.Entry<String, Map<String, Integer>> e : results.entrySet()) {
			for (Map.Entry<String, Integer> s : e.getValue().entrySet()) {
				List<String> filenames = new ArrayList<String>();
				if (s.getValue() > minNgrams) {
					filenames.add(e.getKey());
					filenames.add(s.getKey());
					Collections.sort(filenames);
					sus.add(filenames.get(0) + " " + filenames.get(1) + " " + s.getValue());
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
