package com.example.pablo.app2;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import com.androidquery.AQuery;
import java.util.regex.*;

import javax.xml.datatype.Duration;

public class Adapter extends ArrayAdapter<Model> {

    private final Context context;
    private final ArrayList<Model> modelsArrayList;
    private MyActivity main;
    public Adapter(Context context, ArrayList<Model> modelsArrayList, MyActivity m) {

        super(context, R.layout.target_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
        this.main=m;
    }

    public Adapter(Context context, ArrayList<Model> modelsArrayList, Conversation m) {

        super(context, R.layout.target_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater

        View rowView = null;
            AQuery aq = new AQuery(this.getContext());
            rowView = inflater.inflate(R.layout.target_item, parent, false);
            //MD5 user
            String user = modelsArrayList.get(position).getUser();
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(user.getBytes());
            byte[] digest = md.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }

        String re1="(\\[.*?\\])";	// Square Braces 1

        Pattern p = Pattern.compile(re1,Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        final Matcher m = p.matcher(user);
        String team = null;
        TextView teamV = (TextView) rowView.findViewById(R.id.team);
        if (m.find())
        {
            team = m.group(1).toString();
            user = user.replace(team, "");
            teamV.setText(team.replace("[", "").replace("]", ""));

        }else{
            teamV.setVisibility(View.INVISIBLE);
        }

            ImageView imgView = (ImageView) rowView.findViewById(R.id.imageView);


           aq.id(imgView).image("http://vanillicon.com/"+sb.toString()+"_200.png");
           // aq.id(imgView).image("https://vanillicon.com/c4ca4238a0b923820dcc509a6f75849b_200.png", true, true, 0, R.drawable.ic_launcher);

            TextView userView = (TextView) rowView.findViewById(R.id.user);
            TextView messageView = (TextView) rowView.findViewById(R.id.message);
            TextView hourView = (TextView) rowView.findViewById(R.id.hour);
            final String original = modelsArrayList.get(position).getUser();
        // 4. Set the text for textView
            //imgView.setImageResource(modelsArrayList.get(position).getIcon());

            userView.setText(user);
            messageView.setText(modelsArrayList.get(position).getMessage());
            hourView.setText(modelsArrayList.get(position).getHour());

        final View finalRowView = rowView;
        final String msg = modelsArrayList.get(position).getMessage();

        View nx = ((ViewGroup)rowView).getChildAt(0);
        if(msg.indexOf(main.username)!=-1){
            nx.setBackgroundColor(Color.parseColor("#366dbfff"));
        }
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = compiledPattern.matcher(msg);
        if(matcher.find()) {
            String id = matcher.group(1).toString();
            ImageView youtube = (ImageView) rowView.findViewById(R.id.youtube);


            aq.id(youtube).image("http://img.youtube.com/vi/"+id+"/mqdefault.jpg");
            youtube.setVisibility(View.VISIBLE);
            youtube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(matcher.group(0)));
                    main.startActivity(browserIntent);
                }
            });
        }
        rowView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Case private preview
                if(main!=null) {

                    EditText e= (EditText)main.findViewById(R.id.editText);
                    e.setText(original + ": ");
                    e.setSelection(e.getText().length());
                    main.mViewPager.setCurrentItem(0);

                }

            }
        });
        rowView.setOnLongClickListener(new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View v) {
                FavoriteDB messageDB = new FavoriteDB(original, msg, "", "");
                messageDB.save();
                Toast.makeText(main.getApplicationContext(), "Mensaje guardado en favoritos", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        return rowView;
    }
}