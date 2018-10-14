package kz.kaznu.lucene;

import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.index.*;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import java.io.IOException;

public class BasicSearchExamples {
    public static final int DEFAULT_LIMIT = 10;
    private final IndexReader reader;

    public BasicSearchExamples(IndexReader reader) {
        this.reader = reader;
    }

    /**
     * Search using TermQuery
     * @param toSearch string to search
     * @param searchField field where to search. We have "body" and "title" fields
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void searchIndexWithTermQuery(final String toSearch, final String searchField, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final Term term = new Term(searchField, toSearch);
        final Query query = new TermQuery(term);
        final TopDocs search = indexSearcher.search(query, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * This is method for count all terms in Indexed documents
     * @throws IOException
     */
    public void countTerm() throws IOException {

        long countBody = reader.getSumDocFreq("body");
        long countTitle = reader.getSumDocFreq("title");

        System.out.println("Body: " + countBody);
        System.out.println("Title: " + countTitle);
        System.out.println("Total count Terms: " + (countTitle + countBody));

    }

    /**
     * This is wrapper to searchIndexWithTermQuery
     * It executes searchIndexWithTermQuery using "body" field and limiting to 10 results
     *
     * @param toSearch string to search in the "body" field
     * @throws IOException
     * @throws ParseException
     */
    public void searchIndexWithTermQueryByBody(final String toSearch) throws IOException, ParseException {
        searchIndexWithTermQuery(toSearch, "body", DEFAULT_LIMIT);
    }

    /**
     * Search in body using QueryParser
     * @param toSearch string to search
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void searchInBody(final String toSearch, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final QueryParser queryParser = new QueryParser("body", new RussianAnalyzer());
        final Query query = queryParser.parse(toSearch);
        System.out.println("Type of query: " + query.getClass().getSimpleName());

        final TopDocs search = indexSearcher.search(query, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * Search in several fields using MultiFieldQueryParser
     * @param toSearch string to search
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void SearchInMultiField(final String toSearch, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"body","title"}, new RussianAnalyzer());
        final Query query = queryParser.parse(toSearch);
        System.out.println("Type of query: " + query.getClass().getSimpleName());

        final TopDocs search = indexSearcher.search(query, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * Search all documents before this Date(creationDate) with using NumericRangeQuery
     * @param date long to search
     * @param limit how many results to return
     * @throws IOException
     */
    public void searchByBeforeDate(long date, final int limit) throws IOException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        Query q = NumericRangeQuery.newLongRange("creationDate", 0L, date, true, true);
        System.out.println("Type of query: " + q.getClass().getSimpleName());

        final TopDocs search = indexSearcher.search(q, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * Search all documents after this Date(creationDate) with using NumericRangeQuery
     * @param date long to search
     * @param limit how many results to return
     * @throws IOException
     */
    public void searchByAfterDate(long date, final int limit) throws IOException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        long now = System.currentTimeMillis() / 1000L;

        Query q = NumericRangeQuery.newLongRange("creationDate", date, now, true, true);
        System.out.println("Type of query: " + q.getClass().getSimpleName());

        final TopDocs search = indexSearcher.search(q, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * Search all documents in the range of two Dates(creationDate) with using NumericRangeQuery
     * @param firstDate from this date search starts(long)
     * @param secondDate on this date search finishes(long)
     * @param limit how many results to return
     * @throws IOException
     */
    public void searchByRangeDate(long firstDate, long secondDate, final int limit) throws IOException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        Query q = NumericRangeQuery.newLongRange("creationDate", secondDate, firstDate, true, true);
        System.out.println("Type of query: " + q.getClass().getSimpleName());

        final TopDocs search = indexSearcher.search(q, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }


    /***
     * Search all documents by regions with using BooleanQuery
     * @param toSearch regions to search(String[])
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void searchInRegion(final String[] toSearch, final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        BooleanQuery bq = new BooleanQuery();

        BooleanQuery bq1 = new BooleanQuery();
        final QueryParser queryParser = new QueryParser("region", new RussianAnalyzer());

        for(int i = 0; i < toSearch.length; i++) {

            final Query query = queryParser.parse(toSearch[i]);
            bq1.add((query), BooleanClause.Occur.SHOULD);

            System.out.println(toSearch[i]);

        }

        bq.add((bq1), BooleanClause.Occur.MUST);

        final QueryParser queryParser1 = new QueryParser("title", new RussianAnalyzer());
        final Query query = queryParser1.parse("Тима");
        bq.add((query), BooleanClause.Occur.MUST);

        Query q2 = new ConstantScoreQuery(bq);

        final TopDocs search = indexSearcher.search(q2, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * Search all documents by regions, date and string to search in body, title with using BooleanQuery
     * @param strSearch string to search
     * @param strArrayRegion string array of regions
     * @param searchDate long array of dates
     * @param howSearchInDate number of type how to search by Date
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */

    public void searchByManyQueries(final String strSearch, final String[] strArrayRegion,
                                    final long[] searchDate, final int howSearchInDate,
                                    final int limit) throws IOException, ParseException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);
        final long firstDate;
        final long secondDate;

        BooleanQuery mainBooleanQuery = new BooleanQuery();

        BooleanQuery regionBooleanQuery = new BooleanQuery();
        final QueryParser queryParserRegion = new QueryParser("region", new RussianAnalyzer());

        for(int i = 0; i < strArrayRegion.length; i++) {

            final Query query = queryParserRegion.parse(strArrayRegion[i]);
            regionBooleanQuery.add((query), BooleanClause.Occur.SHOULD);

            System.out.println(strArrayRegion[i]);

        }

        mainBooleanQuery.add((regionBooleanQuery), BooleanClause.Occur.MUST);

        final MultiFieldQueryParser queryParser = new MultiFieldQueryParser(new String[]{"body","title"}, new RussianAnalyzer());
        final Query query = queryParser.parse(strSearch);
        mainBooleanQuery.add((query), BooleanClause.Occur.MUST);

        switch(howSearchInDate) {
            case 1:
                firstDate = 0L;
                secondDate = searchDate[0];
                break;
            case 2:
                firstDate = searchDate[0];
                secondDate = System.currentTimeMillis() / 1000L;
                break;
            case 3:
                firstDate = searchDate[0];
                secondDate = searchDate[1];
                break;
            default:
                firstDate = 0L;
                secondDate = System.currentTimeMillis() / 1000L;
                break;
        }

        Query searchByDate = NumericRangeQuery.newLongRange("creationDate", firstDate, secondDate, true, true);
        mainBooleanQuery.add(searchByDate, BooleanClause.Occur.MUST);

        Query q2 = new ConstantScoreQuery(mainBooleanQuery);

        final TopDocs search = indexSearcher.search(q2, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /***
     * Search using MoreLikeThisQuery.
     * Actually dont work right now
     *
     * @param likeText
     * @param moreLikeFields
     * @param searchField
     * @param limit
     * @throws IOException
     */
    public void moreLikeThisQuery(final String likeText, final String[] moreLikeFields,
                                  final String searchField, final int limit) throws IOException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final MoreLikeThisQuery test = new MoreLikeThisQuery(likeText, moreLikeFields,  new RussianAnalyzer(), searchField);

        final TopDocs search = indexSearcher.search(test, limit);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /**
     * This is wrapper to searchInBody function
     * it executes searchInBody with default limiting to 10 results
     *
     * @param toSearch
     * @throws IOException
     * @throws ParseException
     */
    public void searchInBody(final String toSearch) throws IOException, ParseException {
        searchInBody(toSearch, DEFAULT_LIMIT);
    }

    /**
     * Search using FuzzyQuery.
     * @param toSearch string to search
     * @param searchField field where to search. We have "body" and "title" fields
     * @param limit how many results to return
     * @throws IOException
     * @throws ParseException
     */
    public void fuzzySearch(final String toSearch, final String searchField, final int limit) throws IOException {
        final IndexSearcher indexSearcher = new IndexSearcher(reader);

        final Term term = new Term(searchField, toSearch);

        final int maxEdits = 2; // This is very important variable. It regulates fuzziness of the query
        final Query query = new FuzzyQuery(term, maxEdits);
        final TopDocs search = indexSearcher.search(query, limit);
        System.out.println(search);
        final ScoreDoc[] hits = search.scoreDocs;
        showHits(hits);
    }

    /**
     * Wrapper to fuzzySearch function.
     * It executed fuzzySearch with default limit and body field as target field
     *
     * @param toSearch string to search
     * @throws IOException
     * @throws ParseException
     */
    public void fuzzySearch(final String toSearch) throws IOException {
        fuzzySearch(toSearch, "body", DEFAULT_LIMIT);
    }

    private void showHits(final ScoreDoc[] hits) throws IOException {
        if (hits.length == 0) {
            System.out.println("\n\tНичего не найдено");
            return;
        }
        System.out.println("\n\tРезультаты поиска:");
        for (ScoreDoc hit : hits) {
            final String title = reader.document(hit.doc).get("title");
            final String body = reader.document(hit.doc).get("body");
            final String region = reader.document(hit.doc).get("region");
            final String creationDate = reader.document(hit.doc).get("creationDate");
            final String test = reader.document(hit.doc).get("test");
            final Float score = hit.score;
            System.out.println(score);
            System.out.println("\n\tDocument Id = " + hit.doc + "\n\ttitle = " + title + "\n\tbody = " + body + "\n\tregion = " + region + "\n\tcreationDate = " + creationDate + "\n\ttest = " + test);
        }
    }
}
