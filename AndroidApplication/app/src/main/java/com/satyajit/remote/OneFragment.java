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
import android.widget.TextView;
import android.widget.Toast;

import dmax.dialog.SpotsDialog;


public class OneFragment extends Fragment {
    AlertDialog dialog;
    CardView c1,c2,c3,c4;
    String cmd="";
    Context con;
    private OnFragmentInteractionListener listener;
    Toast toast;
    TextView text;
    LayoutInflater inflater;
    View layout;


    public static OneFragment newInstance() {
        return new OneFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);


        c1=view.findViewById(R.id.crd1);
        c2=view.findViewById(R.id.crd2);
        c3=view.findViewById(R.id.crd3);
        c4=view.findViewById(R.id.crd4);




        c1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                cmd="send.php?cmd=executeshutdown10cmd1";
                new MyTask().execute();


            }
        });

        c2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                cmd="send.php?cmd=screenshot";
                new MyTask().execute();


            }
        });


        c3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                cmd="send.php?cmd=CancelShut";
                new MyTask().execute();


            }
        });


        c4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                cmd="stat.php";
                new MyTask().execute();


            }
        });


        inflater = getLayoutInflater();

        layout = inflater.inflate(R.layout.toast,
                (ViewGroup) view.findViewById(R.id.custom_toast_container));

        text = layout.findViewById(R.id.TMsg);


        toast = new Toast(view.getContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);



        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getActivity().getAssets(), "fonts/Cav.ttf");
        fontChanger.replaceFonts((ViewGroup) this.getView());

        con=this.getView().getContext();
    }

    void msg(String message) {

        text.setText(message);


        toast.show();
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

    public interface OnFragmentInteractionListener {
    }



    class MyTask extends AsyncTask<String, Integer, String> {


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

                if(cmd.equals("stat.php")) msg("Last Seen "+result);
               else msg("Send Success");
                // Do things like hide the progress bar or change a TextView
            }
        }

    }


}


