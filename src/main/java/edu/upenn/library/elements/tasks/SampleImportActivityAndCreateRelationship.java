package edu.upenn.library.elements.tasks;

import edu.upenn.library.elements.Task;
import edu.upenn.library.elements.api.Category;
import edu.upenn.library.elements.api.Feed;
import edu.upenn.library.elements.api.FeedEntry;
import edu.upenn.library.elements.api.XML;
import edu.upenn.library.elements.api.resources.Relationships;
import edu.upenn.library.elements.api.resources.category.CategoriesRecordsDataSourceProprietaryId;
import edu.upenn.library.elements.api.xml.v49.ImportRecord;
import edu.upenn.library.elements.api.xml.v49.ImportRelationship;
import edu.upenn.library.elements.api.xml.v49.UpdateRecord;
import org.jdom2.Element;

/**
 * Sample code to illustrate how to create a record and a relationship linking it to a user
 */
public class SampleImportActivityAndCreateRelationship extends Task {

  @Override
  public String getDescription() {
    return "Sample task for purely illustrative purposes";
  }

  @Override
  public String getHelp() {
    return "Usage: SampleImportActivityAndCreateRelationship USERNAME\n" +
      "\n" +
      getDescription() + "\n" +
      "\n" +
      "This tasks exists only to illustrate what the code looks like to\n" +
      "use the API for record creation.";
  }

  @Override
  public void execute() throws Exception {
    if(getArgs().size() < 1) {
      getLogger().error("You must specify a username.");
      return;
    }
    String username = getArgs().get(0);

    // arbitrary source and proprietary ID to use
    String source = "manual";
    String proprietaryId = "666";

    // create the activity
    ImportRecord record = new ImportRecord("fellowship");
    record.addText("title", "some prize");
    record.addText("description", "sample test activity created via API");

    CategoriesRecordsDataSourceProprietaryId resource =
      new CategoriesRecordsDataSourceProprietaryId(Category.ACTIVITY, source, proprietaryId);

    Feed feedFromPut = getApi().putDocument(resource, record);

    System.out.println("Record created with these fields: ");
    for(FeedEntry feedEntry : feedFromPut.getEntries()) {
      for(Element resultRecord : feedEntry.getElementsContent().getChild("records", XML.apiNs).getChildren()) {
        for(Element field : resultRecord.getChild("native", XML.apiNs).getChildren()) {
          System.out.println(field.getAttribute("name").getValue() + "=" + field.getValue());
        }
      }
    }

    // use PATCH to update fields selectively
    UpdateRecord updateRecord = new UpdateRecord();
    updateRecord.addTextField(UpdateRecord.Operation.Set, "title", "changed prize");

    Feed feedfromPatch = getApi().patchDocument(resource, updateRecord);

    Feed feedFromGet = getApi().getFeed(resource);
    System.out.println("Record after patch: ");
    for(FeedEntry feedEntry : feedFromGet.getEntries()) {
      for(Element resultRecord : feedEntry.getElementsContent().getChild("records", XML.apiNs).getChildren()) {
        for(Element field : resultRecord.getChild("native", XML.apiNs).getChildren()) {
          System.out.println(field.getAttribute("name").getValue() + "=" + field.getValue());
        }
      }
    }

    // create a relationship between user and activity
    ImportRelationship relationship = new ImportRelationship(23);
    // note: NO space after comma, or API will choke.
    relationship.setFromObject(String.format("activity(source-%s,pid-%s)", source, proprietaryId));
    relationship.setToObject(String.format("user(username-%s)", username));

    Relationships relationshipsResource = new Relationships();
    getApi().postDocument(relationshipsResource, relationship);

    // delete it
    getApi().delete(resource);
  }

}
