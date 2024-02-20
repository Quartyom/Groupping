package quartyom.groupping;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

// optimized memory usage
public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Pass a file as an argument");
            return;
        }

        long startTime = System.currentTimeMillis();

        solve(args[0]);

        System.out.println("time: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
    }

    public static void solve(String filename) {
        // creating hashsets and hashmap
        ArrayList<HashSet<String>> columns = new ArrayList<>();
        ArrayList<HashSet<String>> repeats = new ArrayList<>();
        ArrayList<HashMap<String, Group>> groups = new ArrayList<>();

        // open file to read
        int maxColumns = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {    // line by line
            String str;
            while ((str = br.readLine()) != null) {
                ArrayList<String> line = parseLine(str);   // parsing
                if (line != null) {
                    if (line.size() > maxColumns) {
                        maxColumns = line.size();

                        while (columns.size() < maxColumns) {
                            columns.add(new HashSet<>());
                            repeats.add(new HashSet<>());
                            groups.add(new HashMap<>());
                        }
                    }

                    for (int i = 0; i < line.size(); i++) {
                        String item = line.get(i);
                        if (item.equals("\"\"")) { continue; }

                        if (!columns.get(i).add(item)) {
                            // if presents in hash set
                            // add to repeats
                            repeats.get(i).add(item);
                        }
                    }

                }
            }
        }
        catch (IOException e) {
            System.err.println("Error with " + filename);
        }

        System.out.println("done repeats");


        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {    // line by line
            String str;
            while ((str = br.readLine()) != null) {
                ArrayList<String> line = parseLine(str);   // parsing
                if (line != null) {

                    HashMap<Group, Integer> matches = new HashMap<>();

                    boolean hasRepeats = false;

                    for (int i = 0; i < line.size(); i++) {
                        String item = line.get(i);
                        if (item.equals("\"\"")) { continue; }

                        if (repeats.get(i).contains(item)) {
                            hasRepeats = true;

                            Group group = groups.get(i).get(item);      // search for matching groups for columns
                            if (group != null) {
                                matches.put(group, i);
                            }
                        }
                    }

                    if (!hasRepeats) {
                        continue;
                    }

                    if (matches.isEmpty()) {
                        Group group = new Group(maxColumns, line);  // no matches, create new group
                        for (int i = 0; i < line.size(); i++) {
                            String item = line.get(i);
                            if (item.equals("\"\"")) { continue; }

                            groups.get(i).put(item, group);
                        }
                    }
                    else {
                        Group[] m = matches.keySet().toArray(new Group[0]);
                        Group main = m[0];
                        if (m.length > 1) {
                            for (int i = 1; i < m.length; i++) {    // merging
                                Group mi = m[i];
                                main.mergeWith(m[i]);

                                for (int j = 0; j < mi.columns.size(); j++) {     // remapping
                                    HashSet<String> col = mi.columns.get(j);
                                    for (String k : col) {
                                        groups.get(j).put(k, main);
                                    }
                                }

                            }
                        }
                        main.addLine(line);

                        for (int i = 0; i < line.size(); i++) {     // mapping for line
                            String item = line.get(i);
                            if (item.equals("\"\"")) { continue; }
                            groups.get(i).put(item, main);
                        }
                    }
                }
            }
        }
        catch (IOException e) {
            System.err.println("Error with " + filename);
        }

        System.out.println("done grouping");

        // get all groups without repeats
        HashSet<Group> g = new HashSet<>();
        for (HashMap<String, Group> col : groups) {
            g.addAll(col.values());
        }
        repeats.clear();

        ArrayList<Group> groupsList = new ArrayList<>(g);
        g.clear();

        groupsList.sort((a, b) -> b.lines.size() - a.lines.size());

        try (FileOutputStream fos = new FileOutputStream("result.txt")) {
            fos.write(("Групп с более чем одним элементом: " + groupsList.size() + "\n\n").getBytes());
            int i = 1;
            for (Group group : groupsList) {
                fos.write(("Группа " + i + "\n").getBytes());
                fos.write(group.print().getBytes());
                fos.write("\n".getBytes());
                i++;
            }

            System.out.println("result saved in result.txt");
        }
        catch (IOException e) {
            System.out.println("error while writing to file");
        }

    }

    // pre compile pattern
    private static final Pattern pattern = Pattern.compile("^\"-?\\d+(\\.\\d+)?\"$");
    public static ArrayList<String> parseLine(String line) {
        String[] numbers = line.split(";");
        ArrayList<String> out = new ArrayList<>();

        for (String num : numbers) {
            if (num.equals("\"\"") || pattern.matcher(num).matches()) {
                out.add(num);
            }
            else {
                return null;
            }
        }

        while (!out.isEmpty() && out.getLast().equals("\"\"")) {        // because "123";"45" and "123";"45";"" are equal
            out.removeLast();
        }
        return out.isEmpty()? null : out;
    }
}

