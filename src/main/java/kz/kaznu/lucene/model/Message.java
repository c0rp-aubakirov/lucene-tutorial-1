package kz.kaznu.lucene.model;

import kz.kaznu.lucene.index.MessageToDocument;
import org.apache.lucene.document.Document;

public class Message {
    private String body;
    private String title;
   private String[] region;
    private String creationDate;

    public Document convertToDocument() {
        return MessageToDocument.createWith(title, body, region, creationDate);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getRegion() {
        return region;
    }

    public void setRegion(String[] region) {
        this.region = region;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
