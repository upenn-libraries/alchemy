package edu.upenn.library.elements.csv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Facilitates output to CSV. Written with the goal in mind of passing data to
 * Apache Commons' CSVPrinter.
 */
public class CSV {

  private List<Map<String, String>> rows = new ArrayList<>();
  private List<Field> header;
  private List<Object[]> all = new ArrayList<>();

  /**
   * @param header fields in the order they should be written to CSV
   */
  public CSV(List<Field> header) {
    this.header = header;
  }

  /**
   * @param row map keys should correspond to header field's 'key' value
   */
  public void addRow(Map<String, String> row) {
    rows.add(row);
  }

  /**
   * Get all the data in this CSV object as an Iterable, to pass to a
   * org.apache.commons.csv.CSVPrinter.
   * @return
   */
  public Iterable<Object[]> getAll() {
    if(all.size() != rows.size() + 1) {
      all = new ArrayList<>();

      all.add(header.stream().map(h -> h.getDisplay()).toArray());

      rows.stream().map(row -> {
        return header.stream().map(h -> row.get(h.getKey())).toArray();
      }).forEach(row -> all.add(row));
    }
    return all;
  }

}
