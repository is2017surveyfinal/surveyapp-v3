package it.survey.survey_is;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import java.util.List;

import it.survey.survey_is.model.Domanda;
import it.survey.survey_is.model.Risposta;

public class RispostaAdapter extends ArrayAdapter<Risposta> {

    private Domanda domanda;

    public RispostaAdapter(@NonNull Context context, @NonNull Domanda domanda) {
        super(context, R.layout.activity_risposta_item, domanda.getRisposte());
        this.domanda = domanda;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View rispostaView = inflater.inflate(R.layout.activity_risposta_item, parent, false);

        final Risposta risposta = getItem(position);

        RadioButton testoRisposta = (RadioButton) (rispostaView.findViewById(R.id.checkBox));
        testoRisposta.setText(risposta.getRisposta());

        if (risposta.getSelezionata() != null && risposta.getSelezionata()) {
            testoRisposta.setChecked(true);
        } else {
            testoRisposta.setChecked(false);
        }

        testoRisposta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                for (Risposta r : domanda.getRisposte()) {
                    r.setSelezionata(false);
                }
                risposta.setSelezionata(b);

                notifyDataSetChanged();
            }
        });

        return rispostaView;
    }
}
