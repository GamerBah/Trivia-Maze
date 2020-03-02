package org.flockofseagles;

import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

public class DatabaseQuestionUtility implements QuestionUtility {

    private static Connection connection = null;

    public DatabaseQuestionUtility() {
        createTables();
        addInitialQuestionSets();
    }

    @Override
    public List<Question> loadQuestionSet() {
        connection = getConnection();
        var questionSet = new ArrayList<Question>();

        try {

            String sqlStatement = "SELECT *" +
                                  "FROM question";

            ResultSet rs = connection.prepareStatement("SELECT count(*) FROM question").executeQuery();

            int rows = rs.getInt(1);

            rs = connection.prepareStatement(sqlStatement).executeQuery();

            System.out.println("Num rows: " + rows);


            for (int i = 0; rs.next() && i < rows; i++) {
                String questionId = rs.getString(1);
                var answerArr = new String[4];

                String answerSqlStatement = String.format("SELECT answer_string " +
                                                          "FROM answer WHERE question_id = %s LIMIT 4", questionId);

                ResultSet answerResultSet = connection.prepareStatement(answerSqlStatement).executeQuery();

                for (int j = 0; j < answerArr.length && answerResultSet.next(); j++) {
                    String answer = answerResultSet.getString(1);
                    answerArr[j] = answer;
                }

                String questionString = rs.getString(2);

                questionSet.add(new Question(answerArr, 0, questionString));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();

        return questionSet;
    }

    @Override
    public int getQuestionId(String question) throws NoSuchElementException {
        connection = getConnection();

        try {
            String sqlStatement = String.format("SELECT question_id from question WHERE question_string = '%s'", question);

            ResultSet rs = connection.prepareStatement(sqlStatement).executeQuery();

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();

        }
        throw new NoSuchElementException("question does not exist");
    }

    @Override
    public void addQuestion(final String question, String[] answers) {
        connection = getConnection();

        try {
            String sqlStatement = String.format("SELECT COUNT(*) FROM question WHERE question_string = '%s'",
                    question);

            if (connection.prepareStatement(sqlStatement).executeQuery().getInt(1) > 0)
                return;

            sqlStatement = String.format("INSERT INTO question(question_string, question_category) values('%s', '%s')",
                    question,
                    "multiple choice");

            connection.prepareStatement(sqlStatement).execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {

            for (String answer : answers) {
                String sqlStatement = String.format("INSERT INTO answer(answer_string, question_id) values('%s', '%s')", answer,
                        getQuestionId(question));
                connection = getConnection();
                connection.prepareStatement(sqlStatement).execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    @Override
    public void removeQuestion(String question) {
        connection = getConnection();

        try {
            String sqlStatement = String.format("DELETE FROM question WHERE question_string = '%s'", question);
            connection.prepareStatement(sqlStatement).executeUpdate();

            System.out.println("question removed");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    @Override
    public void editQuestion(String oldQuestion, String newQuestion) {
        connection = getConnection();

        try {
            String sqlStatement = String.format("UPDATE question SET question_string = '%s' WHERE question_string = '%s'",
                    newQuestion,
                    oldQuestion);

            connection.prepareStatement(sqlStatement).execute();

            System.out.println("question changed");

        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    @Override
    public void addAnswer(String question, String answer) {
        connection = getConnection();

        try {
            String sqlStatement = String.format("INSERT INTO answer(answer_string, question_id) values('%s', '%s')", answer,
                    getQuestionId(question));
            connection.prepareStatement(sqlStatement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    @Override
    public void editAnswer(String question, String oldAnswer, String newAnswer) {
        connection = getConnection();

        try {
            String sqlStatement = String.format("UPDATE answer SET answer_string = '%s' WHERE question_id = '%s' AND answer_string = '%s'",
                    newAnswer, getQuestionId(question), oldAnswer);
            connection.prepareStatement(sqlStatement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    @Override
    public void removeAnswer(String question, String answer) {
        connection = getConnection();

        try {
            String sqlStatement = String.format("DELETE FROM answer WHERE question_id = '%s' AND answer_string = '%s'",
                    getQuestionId(question),
                    answer);
            connection.prepareStatement(sqlStatement).execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }


    public void createTables() {
        Connection connection = getConnection();

        try {
            connection.prepareStatement("CREATE TABLE IF NOT EXISTS question (" +
                                        "question_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                        "question_string TEXT NOT NULL," +
                                        "question_category TEXT NOT NULL)").execute();

            connection.prepareStatement("CREATE TABLE IF NOT EXISTS answer (" +
                                        "answer_string TEXT NOT NULL," +
                                        "question_id INTEGER REFERENCES question(question_id) ON DELETE CASCADE" +
                                        ")").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
    }

    public void addInitialQuestionSets() {
        ArrayList<String> questionSetList = new ArrayList<>();

        questionSetList.add("A carnivorous animal eats flesh, what does a nucivorous animal eat?");
        questionSetList.add("Nuts");
        questionSetList.add("Nothing");
        questionSetList.add("Fruit");
        questionSetList.add("Seaweed");
        questionSetList.add("Where is the train station?");
        questionSetList.add("Wales");
        questionSetList.add("Moldova");
        questionSetList.add("Czech Republic");
        questionSetList.add("Denmark");
        questionSetList.add("When was Adolf Hitler appointed as Chancellor of Germany?");
        questionSetList.add("January 30, 1933");
        questionSetList.add("September 1, 1939");
        questionSetList.add("February 27, 1933");
        questionSetList.add("October 6, 1939");
        questionSetList.add("The novel Jane Eyre was written by what author? ");
        questionSetList.add("Charlotte Bronte");
        questionSetList.add("Emily Bronte");
        questionSetList.add("Jane Austen");
        questionSetList.add("Louisa May Alcott");
        questionSetList.add(
                "This album, now considered to be one of the greatest of all time, was a commercial failure when it was released.");
        questionSetList.add("The Velvet Underground and Nico");
        questionSetList.add("Abbey Road");
        questionSetList.add("Led Zeppelin IV");
        questionSetList.add("Pet Sounds");
        questionSetList.add("Which of these programming languages is a low-level language?");
        questionSetList.add("Assembly");
        questionSetList.add("Python");
        questionSetList.add("C#");
        questionSetList.add("Pascal");
        questionSetList.add("In Super Mario Bros., who informs Mario that the princess is in another castle?");
        questionSetList.add("Toad");
        questionSetList.add("Luigi");
        questionSetList.add("Yoshi");
        questionSetList.add("Bowser");
        questionSetList.add("Who wrote the song You Know You Like It?");
        questionSetList.add("AlunaGeorge");
        questionSetList.add("DJ Snake");
        questionSetList.add("Steve Aoki");
        questionSetList.add("Major Lazer");
        questionSetList.add("Which famous singer was portrayed by actor Kevin Spacey in the 2004 biographical film Beyond the Sea?");
        questionSetList.add("Bobby Darin");
        questionSetList.add("Louis Armstrong");
        questionSetList.add("Frank Sinatra");
        questionSetList.add("Dean Martin");
        questionSetList.add("Which character in the Animal Crossing series uses the phrase zip zoom when talking to the player?");
        questionSetList.add("Scoot");
        questionSetList.add("Drake");
        questionSetList.add("Bill");
        questionSetList.add("Mallary");

        for (int i = 0; i < 10; i++) {
            int questionIndex = i * 5;

            var answersArr = new String[4];

            int k = 0;

            for (int j = questionIndex + 1; j < questionIndex + 5; j++) {
                answersArr[k] = questionSetList.get(j);
                k++;
            }

            this.addQuestion(questionSetList.get(questionIndex), answersArr);
        }

        closeConnection();
    }

    private boolean dbIsEmpty() {
        connection = getConnection();
        try {
            boolean isClosed = connection.prepareStatement("select count(*) from question").executeQuery().getInt(1) <= 0;
            closeConnection();

            return isClosed;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        closeConnection();
        return false;
    }

    private Connection getConnection() {

        Properties connectionProperties;
        String connectionString = String.format("jdbc:sqlite:%s", "questions.sqlite");
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        connectionProperties = config.toProperties();

        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(connectionString, connectionProperties);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
