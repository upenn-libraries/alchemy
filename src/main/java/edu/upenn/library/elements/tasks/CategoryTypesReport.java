package edu.upenn.library.elements.tasks;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import edu.upenn.library.elements.api.resources.Feed;
import edu.upenn.library.elements.api.resources.FeedEntry;
import edu.upenn.library.elements.api.resources.category.Category;
import edu.upenn.library.elements.api.resources.category.Types;
import edu.upenn.library.elements.csv.CSV;
import edu.upenn.library.elements.csv.Field;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.jdom2.Content;
import org.jdom2.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CategoryTypesReport extends Task {

  private final Logger logger = LoggerFactory.getLogger(CategoryTypesReport.class);

  public static String flattenContent(List<Content> content) {
    return content.stream().filter(s -> s.getCType() == Content.CType.Text).map(s -> s.getValue()).reduce("", (acc, i) -> acc + i);
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().length < 2) {
      logger.error("You must specify an Elements category and filename argument.");
      return;
    }
    String categoryName = getArgs()[0];
    String filename = getArgs()[1];

    FileWriter writer = new FileWriter(filename);
    CSVPrinter printer = CSVFormat.DEFAULT.print(writer);

    List<Field> header = new ArrayList<>();
    header.add(new Field("activity name"));
    header.add(new Field("name", "field name"));
    header.add(new Field("display-name"));
    header.add(new Field("type"));
    header.add(new Field("field-group"));
    header.add(new Field("is-mandatory"));
    header.add(new Field("restricted-to"));

    CSV csv = new CSV(header);

    Category category = Category.getByName(categoryName);
    if(category == null) {
      logger.error(categoryName + " isn't a valid category");
      return;
    }

    Feed feed = getApi().getFeed(new Types(category));

    for(FeedEntry entry : feed.getEntries()) {
      for(Element typeField : entry.getElementsContent().getChildren()) {
        if("heading-singular".equals(typeField.getName())) {
          Map<String, String> row = new HashMap<>();
          row.put("activity name", flattenContent(typeField.getContent()));
          csv.addRow(row);
        } else if("fields".equals(typeField.getName())) {
          for(Element field : typeField.getChildren()) {
            Map<String, String> row = field.getChildren().stream().collect(Collectors.toMap(
              c -> c.getName(), c -> flattenContent(c.getContent())));
            csv.addRow(row);

            field.getChildren().stream().filter(c -> "restricted-to".equals(c.getName())).forEach(rt -> {
              rt.getChildren().forEach(rtItem -> {
                Map<String, String> rtRow = new HashMap<>();
                rtRow.put("restricted-to", flattenContent(rtItem.getContent()));
                csv.addRow(rtRow);
              });
            });
          }
        }
      }
    }

    printer.printRecords(csv.getAll());

    printer.flush();
    printer.close();
  }

}
