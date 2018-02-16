package it.survey.survey_is;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import it.survey.survey_is.model.Sondaggio;

public class SurveyAdapter extends ArrayAdapter<Sondaggio>{

    public SurveyAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<Sondaggio> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public SurveyAdapter(@NonNull Context context, @NonNull List<Sondaggio> objects) {
        super(context, R.layout.activity_surveys_item, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View surveyView = inflater.inflate(R.layout.activity_surveys_item, parent, false);

        final Sondaggio survey = getItem(position);

        TextView surveyTitle = (TextView) (surveyView.findViewById(R.id.SurveysTitle));
        surveyTitle.setText(survey.getTitle());

        surveyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getContext(), Questionario.class);
                i.putExtra("sondaggio", survey);
                getContext().startActivity(i);
            }
        });

        return surveyView;
    }
}
