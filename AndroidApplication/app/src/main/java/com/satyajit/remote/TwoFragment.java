package com.satyajit.remote;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;

public class TwoFragment extends Fragment {
    Context con;
    private OnFragmentInteractionListener listener;
    Toast toast;
    TextView text;
    LayoutInflater inflater;
    View layout;
    String cmd;
    Button c1;
    AlertDialog dialog;
    public static TwoFragment newInstance() {
        return new TwoFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();

        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View v= inflater.inflate(R.layout.fragment_two, container, false);

        c1=v.findViewById(R.id.send);

        c1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                cmd="send.php?cmd=execute"+c1.getText()+"cmd50";
                new Load().execute();


            }
        });

        inflater = getLayoutInflater();

        layout = inflater.inflate(R.layout.toast,
                (ViewGroup) v.findViewById(R.id.custom_toast_container));

        text = layout.findViewById(R.id.TMsg);


        toast = new Toast(v.getContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        return  v;

    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/Cav.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());

        con=this.getView().getContext();

    }



    public interface OnFragmentInteractionListener {
    }



    class Load extends AsyncTask<String, Integer, String> {


        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {


            // Do something like display a progress bar


            dialog = new SpotsDialog.Builder()
                    .setContext(con)
                    .setTheme(R.style.regis)
                    .build();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();



        }

        // This is run in a background thread
        @Override
        protected String doInBackground(String... params) {


            String res = "";
            try {


                res=new HttpRequest("http://url/RemoteWin/"+cmd).prepare(HttpRequest.Method.GET).sendAndReadString();


            } catch (Exception e) {

                Log.d("CSE", String.valueOf(e));
            }


            return res;
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (dialog.isShowing()) {
                dialog.dismiss();

                msg("Send Success");
                // Do things like hide the progress bar or change a TextView
            }
        }

    }
    void msg(String message) {

        text.setText(message);


        toast.show();
    }

}
