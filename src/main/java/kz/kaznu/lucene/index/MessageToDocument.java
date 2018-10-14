package kz.kaznu.lucene.index;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * We will use this class to convert messages to Lucene documents
 */
public class MessageToDocument {

    /**
     * Creates Lucene Document using two strings: body and title
     *
     * @return resulted document
     */
    public static Document createWith(final String titleStr, final String bodyStr,
                                      final String[] regionStr, final String createDateStr) {
        final Document document = new Document();

        final FieldType textIndexedType = new FieldType();
        textIndexedType.setStored(true);
        textIndexedType.setIndexOptions(IndexOptions.DOCS);
        textIndexedType.setTokenized(true);

        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        long unixTime;
        try {
            date = dateFormat.parse(createDateStr);
            unixTime = date.getTime()/1000;

        } catch (ParseException e) {
            e.printStackTrace();
            unixTime = 0L;
        }

        String regionString = String.join(" ", regionStr);


        //index title
        Field title = new Field("title", titleStr, textIndexedType);
        //index body
        Field body = new Field("body", bodyStr, textIndexedType);
        //index region
        Field region = new Field("region", regionString, textIndexedType);
        //index date
        LongField creationDate = new LongField("creationDate", unixTime, Field.Store.YES);

        document.add(title);
        document.add(body);
        document.add(region);
        document.add(creationDate);
        return document;
    }
}
