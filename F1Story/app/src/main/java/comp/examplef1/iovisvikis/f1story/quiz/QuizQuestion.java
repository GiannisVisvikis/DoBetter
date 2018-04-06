package comp.examplef1.iovisvikis.f1story.quiz;

/**
 * Created by giannis on 21/2/2018.
 */

class QuizQuestion {

    private String link;
    private String assetPath;
    private String question;
    private String correctAnswer;
    private String wrongAnswer1;
    private String wrongAnswer2;
    private String wrongAnswer3;


    public String getLink() {
        return link;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getWrongAnswer1() {
        return wrongAnswer1;
    }

    public String getWrongAnswer2() {
        return wrongAnswer2;
    }

    public String getWrongAnswer3() {
        return wrongAnswer3;
    }


    public QuizQuestion(String link, String assetPath, String question, String correctAnswer, String wrongAnswer1, String wrongAnswer2, String wrongAnswer3) {
        this.link = link;
        this.assetPath = assetPath;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.wrongAnswer1 = wrongAnswer1;
        this.wrongAnswer2 = wrongAnswer2;
        this.wrongAnswer3 = wrongAnswer3;
    }
}
