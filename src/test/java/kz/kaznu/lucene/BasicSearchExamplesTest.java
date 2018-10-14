package kz.kaznu.lucene;

import kz.kaznu.lucene.constants.Constants;
import kz.kaznu.lucene.index.MessageIndexer;
import kz.kaznu.lucene.utils.Helper;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.junit.After;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class BasicSearchExamplesTest {
    private final Random rnd = new Random(); // to generate safe name for index folder. After tests we removing folders
    private final MessageIndexer indexer = new MessageIndexer(Constants.TMP_DIR + "/tutorial_test" + rnd.nextInt());
    final ClassLoader classLoader = getClass().getClassLoader();
    final File file = new File(classLoader.getResource("tutorial.json").getFile());
    final List<Document> documents;

    public BasicSearchExamplesTest() throws FileNotFoundException {
        documents = Helper.readDocumentsFromFile(file);
    }

    @Test
    public void testSearch() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.searchInBody("корреспондент");
    }

    @Test
    public void searchByManyQueriesWithBeforeDate() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());

        searchWith.searchByManyQueries("Тима", new String[]{"Астана", "ALMATY_REGION"},
                new long[]{1454507602L}, 2, 10);
    }

    @Test
    public void searchByManyQueriesWithAfterDate() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());

        searchWith.searchByManyQueries("Тима", new String[]{"Астана", "ALMATY_REGION"},
                new long[]{1454853202L}, 1, 10);
    }

    @Test
    public void searchByManyQueriesWithRangeDate() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());

        searchWith.searchByManyQueries("Тима", new String[]{"Астана", "ALMATY_REGION"},
                new long[]{1454507602L, 1454853202L}, 3, 10);
    }

    @Test
    public void searchByBeforeDate() throws Exception {
        indexer.index(true, documents); // create index

        long unixTime = convertDateToUnix("Feb 7, 2016 7:53:22 PM");
        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.searchByBeforeDate( unixTime, 10);
    }

    @Test
    public void searchByAfterDate() throws Exception {
        indexer.index(true, documents); // create index

        long unixTime = convertDateToUnix("Feb 7, 2016 7:53:22 PM");

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.searchByAfterDate( unixTime, 10);
    }

    @Test
    public void searchByRangeDate() throws Exception {
        indexer.index(true, documents); // create index

        long firstUnixTime = convertDateToUnix("Feb 7, 2016 7:53:22 PM");
        long secondUnixTime = convertDateToUnix("Feb 3, 2016 7:53:22 PM");

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.searchByRangeDate( firstUnixTime, secondUnixTime, 10);
    }

    @Test
    public void SearchByRegion() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.searchInRegion(new String[]{"Астана", "ALMATY_REGION"}, 10);
    }

    @Test
    public void moreLikeThisQuery() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.moreLikeThisQuery("Тима",new String[]{"title", "body"}, "title", 10);

    }

    @Test
    public void indexCount() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.countTerm();
    }

    @Test
    public void MultiFieldSearch() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.SearchInMultiField("Тима", 10);
    }

    @Test
    public void testSearchWithMistake() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.searchInBody("кореспондент");
    }

    @Test
    public void fuzzySearchWithMistake() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.fuzzySearch("кореспондент");
    }

    @Test
    public void fuzzySearch() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        searchWith.fuzzySearch("дорога");
    }

    @Test
    public void consoleSearch() throws Exception {
        indexer.index(true, documents); // create index

        final BasicSearchExamples searchWith = new BasicSearchExamples(indexer.readIndex());
        System.out.println("Введите строчку запрос, либо слово 'нет' для отмены поиска");
        System.out.print("Введите ваш запрос: ");
        Scanner in = new Scanner(System.in);
        String query = in.nextLine();

        while(!query.equals("нет")) {
            searchWith.fuzzySearch(query);
            System.out.print("Введите ваш запрос: ");
            query = in.nextLine();
        }

    }

    @After
    public void removeIndexes() {
        FileUtils.deleteQuietly(new File(indexer.getPathToIndexFolder())); // remove indexes
    }


    private static long convertDateToUnix(String strDate) {

        DateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a", Locale.ENGLISH);
        Date date = null;
        long unixTime;
        try {
            date = dateFormat.parse(strDate);
            unixTime = date.getTime()/1000;
        } catch (ParseException e) {
            e.printStackTrace();
            unixTime = 0L;
        }

        return unixTime;
    }
}