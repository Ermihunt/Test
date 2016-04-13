package ru.test.ya.test6;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class ServiceDeletePhoto extends Service {
    String[] deletedNames;

    public ServiceDeletePhoto() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (startId == 1) {
            Toast.makeText(this, "Сервис запущен", Toast.LENGTH_LONG).show();
            new Thread(new Runnable() {
                public void run() {
                    File f = getFilesDir();
                    Intent intent = new Intent(MainActivity.BROADCAST_ACTION);

                    for (; ; ) {
                        try {
                            TimeUnit.SECONDS.sleep(60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        f = getFilesDir();
                        int strmin;
                        if (f.list().length > 0) {
                            strmin = 0;
                            for (int i = 0; i < f.list().length; i++)
                                if (f.list()[strmin].compareTo(f.list()[i]) > 0)
                                    strmin = i;
                            intent.putExtra(MainActivity.PARAM, f.list()[strmin]);
                            if (f.listFiles()[strmin].exists())
                                f.listFiles()[strmin].delete();
                            sendBroadcast(intent);
                        }
                        if (f.list().length == 0)
                            break;
                    }
                    stopSelf();
                }
            }).start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Сервис выполнил свою работу", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
