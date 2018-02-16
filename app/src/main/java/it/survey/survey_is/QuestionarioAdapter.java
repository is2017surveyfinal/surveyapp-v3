package it.survey.survey_is;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import it.survey.survey_is.model.AuthorizedImageRequest;
import it.survey.survey_is.model.Domanda;

import static android.content.Context.MODE_PRIVATE;

public class QuestionarioAdapter extends ArrayAdapter<Domanda> {

    private Context context;
    private RequestQueue queue;

    public QuestionarioAdapter(Context context, List<Domanda> objects) {
        super(context, R.layout.activity_questionario_item, objects);
        this.context = context;
        queue = Volley.newRequestQueue(context);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View domandaView = inflater.inflate(R.layout.activity_questionario_item, parent, false);

        Domanda domanda = getItem(position);
        TextView domandaText= (TextView) domandaView.findViewById(R.id.testoDomanda);
        domandaText.setText(domanda.getTesto());

        TextView txtArg = (TextView)domandaView.findViewById(R.id.textArgomento);
        txtArg.setText(domanda.getArgomento());

        RispostaAdapter adapter = new RispostaAdapter(getContext(), domanda);
        ListView l = domandaView.findViewById(android.R.id.list);
        l.setAdapter(adapter);

        final ImageView img = (ImageView) domandaView.findViewById(R.id.imgImmagine);
        if (domanda.getImmagine() == null || domanda.getImmagine().equals("")) {
            img.setVisibility(View.INVISIBLE);
        } else {
            ImageRequest request = new AuthorizedImageRequest(context, domanda.getImmagine(),
                    new Response.Listener<Bitmap>() { // Bitmap listener
                        @Override
                        public void onResponse(Bitmap response) {
                            img.setVisibility(View.VISIBLE);

                            img.setImageBitmap(response);

                            // Save this downloaded bitmap to internal storage
                            //Uri uri = saveImageToInternalStorage(response);

                            // Display the internal storage saved image to image view
                            //img.setImageURI(uri);
                        }
                    },
                    0, // Image width
                    0, // Image height
                    ImageView.ScaleType.CENTER_CROP, // Image scale type
                    Bitmap.Config.RGB_565, //Image decode configuration
                    new Response.ErrorListener() { // Error listener
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something with error response
                            error.printStackTrace();

                            img.setVisibility(View.INVISIBLE);
                        }
                    }
            );

            queue.add(request);
        }

        return domandaView;
    }

    // Custom method to save a bitmap into internal storage
    private Uri saveImageToInternalStorage(Bitmap bitmap) {
        // Initialize ContextWrapper
        ContextWrapper wrapper = new ContextWrapper(context);

        // Initializing a new file
        // The bellow line return a directory in internal storage
        File file = wrapper.getDir("Images", MODE_PRIVATE);

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // Create a file to save the image
        file = new File(file, uuid + ".jpg");

        try {
            // Initialize a new OutputStream
            OutputStream stream = null;

            // If the output file exists, it can be replaced or appended to it
            stream = new FileOutputStream(file);

            // Compress the bitmap
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);

            // Flushes the stream
            stream.flush();

            // Closes the stream
            stream.close();

        }catch (IOException e) // Catch the exception
        {
            e.printStackTrace();
        }

        // Parse the gallery image url to uri
        Uri savedImageURI = Uri.parse(file.getAbsolutePath());

        // Return the saved image Uri
        return savedImageURI;
    }

}



