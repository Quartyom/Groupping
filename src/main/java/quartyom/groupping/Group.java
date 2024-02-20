package quartyom.groupping;

import java.util.ArrayList;
import java.util.HashSet;

public class Group {
    public ArrayList<HashSet<String>> columns;
    public HashSet<ArrayList<String>> lines;
    public final int capacity;

    public Group(int capacity, ArrayList<String> line) {
        this(capacity);
        addLine(line);
    }

    public Group(int capacity) {
        this.capacity = capacity;
        columns = new ArrayList<>(capacity);
        lines = new HashSet<>();
        for (int i = 0; i < capacity; i++) {
            columns.add(new HashSet<>());
        }
    }

    public void mergeWith(Group other) {
        if (this == other) {
            return;
        }
        for (int i = 0; i < capacity; i++) {
            columns.get(i).addAll(other.columns.get(i));
        }
        lines.addAll(other.lines);
    }

    public void addLine(ArrayList<String> line) {
        for (int i = 0; i < line.size(); i++) {
            String value = line.get(i);
            if (!value.equals("\"\"")) {
                columns.get(i).add(line.get(i));
            }
        }
        lines.add(line);
    }

    public String print() {         // print group lines
        StringBuilder s = new StringBuilder();
        for (ArrayList<String> line : lines) {
            for (int i = 0; i < line.size() - 1; i++) {
                s.append(line.get(i)).append(";");
            }
            s.append(line.getLast()).append("\n");
        }
        return s.toString();
    }

    public static String print(ArrayList<String> line) {    // print one line as group
        StringBuilder s = new StringBuilder();
        for (int i = 0 ; i < line.size()-1; i++) {
            s.append(line.get(i)).append(";");
        }
        s.append(line.getLast()).append("\n\n");

        return s.toString();
    }

}
