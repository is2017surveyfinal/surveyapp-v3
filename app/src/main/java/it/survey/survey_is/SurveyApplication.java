package it.survey.survey_is;

import android.app.Application;

public class SurveyApplication extends Application {

    private String userName = null;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
