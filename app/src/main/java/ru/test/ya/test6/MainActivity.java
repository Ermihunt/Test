package ru.test.ya.test6;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;




public class MainActivity extends AppCompatActivity {

    public final static String PARAM = "DeletedNames";

    CreatedPhoto crP;
    DeletedPhoto delP;
    FragmentTransaction fTrans;
    BroadcastReceiver br;
    public final static String BROADCAST_ACTION = "ru.test.ya.test6";
    String[] deletedNames=new String[0];
    File f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        f = getFilesDir();
        crP = new CreatedPhoto();
        delP = new DeletedPhoto();
        fTrans = getFragmentManager().beginTransaction();
        fTrans.add(R.id.layout1, crP);
        fTrans.add(R.id.layout2, delP);
        fTrans.commit();

        br = new BroadcastReceiver() {
            // действия при получении сообщений
            public void onReceive(Context context, Intent intent) {

                String[] arrStr = new String[deletedNames.length + 1];
                for (int i = 0; i < deletedNames.length; i++)
                    arrStr[i] = deletedNames[i];
                arrStr[deletedNames.length] = intent.getStringExtra(PARAM);;
                deletedNames=arrStr;
                ListView listViewDel = (ListView) findViewById(R.id.listViewDel);
                ArrayAdapter<String> adapterDel = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, deletedNames);
                assert listViewDel != null;
                listViewDel.setAdapter(adapterDel);

                arrStr = f.list();
                String strTemp;
                for (int i = 0; i < arrStr.length - 1; i++)
                    for (int j = 0; j < arrStr.length - 1 - i; j++)
                        if (arrStr[j].compareTo(arrStr[j + 1]) > 0) {
                            strTemp = arrStr[j];
                            arrStr[j] = arrStr[j + 1];
                            arrStr[j + 1] = strTemp;
                        }

                ListView listViewCreat = (ListView) findViewById(R.id.listViewCreat);
                ArrayAdapter<String> adapterCreat = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, arrStr);
                assert listViewCreat != null;
                listViewCreat.setAdapter(adapterCreat);

            }
        };
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
// регистрируем (включаем) BroadcastReceiver
        registerReceiver(br, intFilt);
    }

    public void onStart() {
        File f = getFilesDir();
        String[] names = f.list();
        String nametemp;
        for (int i = 0; i < names.length - 1; i++)
            for (int j = 0; j < names.length - 1 - i; j++)
                if (names[j].compareTo(names[j + 1]) > 0) {
                    nametemp = names[j];
                    names[j] = names[j + 1];
                    names[j + 1] = nametemp;
                }

        ListView listViewCreat = (ListView) findViewById(R.id.listViewCreat);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, names);
        assert listViewCreat != null;
        listViewCreat.setAdapter(adapter);

        ListView listViewDel = (ListView) findViewById(R.id.listViewDel);
        ArrayAdapter<String> adapterDel = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, deletedNames);
        assert listViewDel != null;
        listViewDel.setAdapter(adapterDel);
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_photo) {
            Intent cameraIntent = new Intent(this, CameraActivity.class);
            onPause();
            startActivity(cameraIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }
}
