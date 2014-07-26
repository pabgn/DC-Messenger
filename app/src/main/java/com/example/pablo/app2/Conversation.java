package com.example.pablo.app2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.pablo.app2.R;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class Conversation extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.conversation, menu);
        ArrayList<Model> models = new ArrayList<Model>();
        final Adapter adapter;
        final ListView listView = (ListView)findViewById(R.id.listViewConversation);
        adapter = new Adapter(getApplicationContext(), models, this);
        listView.setAdapter(adapter);
        List<MessageDB> msg = MessageDB.listAll(MessageDB.class);
        Bundle b = getIntent().getExtras();
        String username = "["+b.getString("team")+"]"+b.getString("username");
        //String username = "["+((MyActivity) this.getActivity()).team+"]"+((MyActivity) this.getActivity()).username;

        List<MessageDB> messages = Select.from(MessageDB.class)
                .where(Condition.prop("tousername").eq(username),Condition.prop("username").eq(b.getString("from"))).list();


        for (MessageDB element : messages) {
            adapter.add(new Model(R.drawable.ic_launcher, element.username, "00h", element.message));
        }
        listView.setSelection(adapter.getCount() - 1);

        return true;
    }
    public void sendMessageConversation(View v){

        EditText t = (EditText)findViewById(R.id.editTextConversation);

        //client.send(String.format("BMSG %s %s", client.mySid, t.getText().toString().replace(" ", "\\s")));

        t.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
