package com.example.pablo.app2;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;

import java.util.ArrayList;

import hu.dcwatch.embla.protocol.adc.command.AdcCommand;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Hub.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Hub#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class Hub extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public AdcClient client;

    private Adapter adapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private boolean scrolling;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Hub.
     */
    // TODO: Rename and change types and number of parameters
    public static Hub newInstance(String param1, String param2) {
        Hub fragment = new Hub();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }
    public Hub() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hub, container, false);
        final ListView listView = (ListView)v.findViewById(R.id.listView);
        listView.setOnScrollListener(new ListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (++firstVisibleItem + visibleItemCount > totalItemCount) {
                    scrolling=false;
                }else{
                    scrolling=true;
                }
                }


        });
        ArrayList<Model> models = new ArrayList<Model>();
        final Adapter adapter;
        adapter = new Adapter(this.getActivity(), models, (MyActivity) this.getActivity());

        listView.setAdapter(adapter);
        listView.setDivider(null);

        final  Activity a = this.getActivity();
        client = new AdcClient(){
            @Override
            protected void chatReceived(AdcCommand adcCommand) {
                super.chatReceived(adcCommand);
                AdcCommand user = users.get(adcCommand.getHeader().getSender());
                if(user != null){
                    final String from =AdcCommand.unescape(user.getContent().getNamedField("NI"));
                    final String message =  AdcCommand.unescape(adcCommand.getContent().getParameter(0));
                    a.runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.add(new Model(R.drawable.ic_launcher,from,"00h", message));

                            if(!scrolling) {
                                listView.setSelection(adapter.getCount() - 1);
                            }

                        }
                    });
                    //hubMain.append(String.format(" <%s> %s\n", AdcCommand.unescape(user.getContent().getNamedField("NI")), AdcCommand.unescape(adcCommand.getContent().getParameter(0))));
                    //Toast.makeText(getApplicationContext(), "Hola", Toast.LENGTH_SHORT).show();

                    //adapter.add(String.format(" <%s> %s\n", AdcCommand.unescape(user.getContent().getNamedField("NI")), AdcCommand.unescape(adcCommand.getContent().getParameter(0))));
                }
            }
            @Override
            protected void privateReceived(AdcCommand adcCommand) {
                super.privateReceived(adcCommand);

                AdcCommand user = users.get(adcCommand.getHeader().getSender());
                if(user != null){
                   // final String str = String.format(" [PRIVATE] <%s> %s\n", AdcCommand.unescape(user.getContent().getNamedField("NI")), AdcCommand.unescape(adcCommand.getContent().getParameter(0)));
                    final String from =AdcCommand.unescape(user.getContent().getNamedField("NI"));
                    final String message =  "PRIVADO: "+AdcCommand.unescape(adcCommand.getContent().getParameter(0));

                    MessageDB messageDB = new MessageDB(from, client.username, message, "", "");
                    messageDB.save();

                    //book.save();
                    a.runOnUiThread(new Runnable() {
                        public void run() {
                            adapter.add(new Model(R.drawable.ic_launcher,from,"00h", message));
                            listView.setSelection(adapter.getCount() - 1);



                        }
                    });


                }
            }
            @Override
            protected void userConnected(AdcCommand adcCommand) {
                super.userConnected(adcCommand);
                //userList.setListData(sortUsers(users.values()));
            }
            @Override
            protected void userDisconnected(AdcCommand adcCommand) {
                super.userDisconnected(adcCommand);
                //userList.setListData(sortUsers(users.values()));
            }
        };
        try{
            String team = ((MyActivity)this.getActivity()).team;
            if(team.length()>0) {
                client.username = "[" + ((MyActivity) this.getActivity()).team + "]" + ((MyActivity) this.getActivity()).username;
            }else{
                client.username = ((MyActivity) this.getActivity()).username;

            }
            client.connect("dc.ekparty.org", 2783);
        }catch(Exception exception){
            exception.printStackTrace();
        }
        final Button btn = (Button) v.findViewById(R.id.loginButton);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               sendMessage(v);
            }
        });
        return v;

    }
    public void sendMessage(View button){

        EditText t = (EditText)this.getActivity().findViewById(R.id.editText);

        client.send(String.format("BMSG %s %s", client.mySid, t.getText().toString().replace(" ", "\\s")));

        t.setText("");
        ListView listView = (ListView)this.getActivity().findViewById(R.id.listView);

        listView.setSelection(listView.getAdapter().getCount()-1);


    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
