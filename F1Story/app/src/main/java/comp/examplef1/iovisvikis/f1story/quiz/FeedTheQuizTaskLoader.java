package comp.examplef1.iovisvikis.f1story.quiz;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by giannis on 21/2/2018.
 */



   /*
        IMPORTANT

        Make sure you have checked there is a database installed in the app before calling this loader
     */


public class FeedTheQuizTaskLoader extends AsyncTaskLoader<ArrayList<QuizQuestion>> {

    private String quizDatabaseName;

    //store and not reload if done
    private ArrayList<QuizQuestion> cachedData;



    public FeedTheQuizTaskLoader(Context context, String quizDatabaseName) {
        super(context);

        this.quizDatabaseName = quizDatabaseName;

    }


    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if(cachedData == null)
            forceLoad();
        else
            super.deliverResult(cachedData);

    }



    @Override
    public ArrayList<QuizQuestion> loadInBackground() {

        //access the quiz database and collect questions randomly from every table

        ArrayList<QuizQuestion> quizQuestions = new ArrayList<>();

        SQLiteDatabase quizDatabase = getContext().openOrCreateDatabase(quizDatabaseName, Context.MODE_PRIVATE, null);

        //get one questions about helmets
        Cursor helmetsCursor = getRandomEntries(quizDatabase, "helmets_table", 1);
        addQuestions(quizQuestions, helmetsCursor);

        //get one questions about figures
        Cursor figuresCursor = getRandomEntries(quizDatabase, "figures_table", 1);
        addQuestions(quizQuestions, figuresCursor);

        //get one questions about cars
        Cursor carsCursor = getRandomEntries(quizDatabase, "cars_table", 1);
        addQuestions(quizQuestions, carsCursor);

        //get one question about circuits
        Cursor circuitsCursor = getRandomEntries(quizDatabase, "circuits_table", 1);
        addQuestions(quizQuestions, circuitsCursor);

        //get one question about constructors
        Cursor constructorsCursor = getRandomEntries(quizDatabase, "constructors_table", 1);
        addQuestions(quizQuestions, constructorsCursor);

        //get 10 questions about drivers
        Cursor driversCursor = getRandomEntries(quizDatabase, "drivers_table", 10);
        addQuestions(quizQuestions, driversCursor);

        return quizQuestions;

    }



    //store the data and use this instead of reloading again
    @Override
    public void deliverResult(ArrayList<QuizQuestion> data){

        if(cachedData == null)
            cachedData = data;

        super.deliverResult(data);
    }




    /**
     *
     * Query the selected table_name in the quizDatabase and get howMany discrete rows in a Cursor object
     *
     * @param quizDatabase
     * @param tableName
     * @param howMany
     * @return a Cursor object containing howMany random discrete rows of results. No duplicates.
     */
    private Cursor getRandomEntries(SQLiteDatabase quizDatabase, String tableName, int howMany){

        HashMap<Integer, Integer> trackSelected = new HashMap<>();
        Random random = new Random();


        long allEntries = DatabaseUtils.longForQuery(quizDatabase, "select count(*) from " + tableName + ";", null);

        StringBuilder selectionRange = new StringBuilder("(");

        int counter = 0;
        int randomRow;

        while (counter < howMany){

            randomRow = 1 + random.nextInt( (int) allEntries );

            while (trackSelected.get(randomRow) != null){ //don't get duplicate rows, retry until you get a not selected row

                //System.out.println( randomRow + " is a duplicate");
                randomRow = 1 + random.nextInt( (int) allEntries );

            }

            trackSelected.put(randomRow, 1);

            //System.out.println(randomRow + " selected");

            counter++;

            selectionRange.append(randomRow + ",");
        }


        String finalSelection = selectionRange.substring(0, selectionRange.length() - 1) + ");"; //get rid of the last comma and close parenthesis

        return quizDatabase.rawQuery("select link, photo_path, question, answer, false_1, false_2, false_3 from " + tableName + " where id in " + finalSelection, null);

    }



    /**
     *
     * Transform each Cursor row to a QuizQuestion object and add it to the results of the Task
     *
     * @param result
     * @param dataResults
     */
    private void addQuestions(ArrayList<QuizQuestion> result, Cursor dataResults){

        if(dataResults.moveToFirst()){

            do{

                String link = dataResults.getString(0);
                String assetPath = dataResults.getString(1);
                String question = dataResults.getString(2);
                String correct = dataResults.getString(3);
                String false1 = dataResults.getString(4);
                String false2 = dataResults.getString(5);
                String false3 = dataResults.getString(6);

                QuizQuestion quizQuestion = new QuizQuestion(link, assetPath, question, correct, false1, false2, false3);

                result.add(quizQuestion);

            }while (dataResults.moveToNext());


        }


    }


}
