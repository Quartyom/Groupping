package quartyom.groupping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Group {
    public List<Set<String>> columns;
    public Set<List<String>> lines;
    public final int capacity;

    public Group(int capacity, List<String> line) {
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

    public void addLine(List<String> line) {
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
        for (List<String> line : lines) {
            for (int i = 0; i < line.size() - 1; i++) {
                s.append(line.get(i)).append(";");
            }
            s.append(line.getLast()).append("\n");
        }
        return s.toString();
    }

    public static String print(List<String> line) {    // print one line as group
        StringBuilder s = new StringBuilder();
        for (int i = 0 ; i < line.size()-1; i++) {
            s.append(line.get(i)).append(";");
        }
        s.append(line.getLast()).append("\n\n");

        return s.toString();
    }

}
