package com.test.mediaplayer;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private final String DATA_SD = String.valueOf(Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC));

    private ImageButton buttonForward, buttonPause, buttonPlay, buttonRewind;
    private TextView tx1, tx2, songTitle;
    private SeekBar seekbar;
    private ImageView iv;

    private MediaPlayer mediaPlayer;

    private double startTime = 0;
    private double finalTime = 0;

    private Handler myHandler = new Handler();

    private int forwardTime = 5000;
    private int backwardTime = 5000;

    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializationViewComponents();

        songTitle.setText("Song.mp3");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {

                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_REQUEST_CODE);
            }
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(DATA_SD);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = mediaPlayer.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",
                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            seekbar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };

    public void play() {
        // пишем в тоаст что "Playing sound"
        Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
        // стартуем песню
        mediaPlayer.start();
        // кнопку плей скрываем
        buttonPlay.setVisibility(View.INVISIBLE);
        // кнопку пауса показываем
        buttonPause.setVisibility(View.VISIBLE);
        // получаем длинну трека и присваеваем ее finalTime
        finalTime = mediaPlayer.getDuration();
        // получаем текущую позицию воспроизведения и присваиваем ее startTime
        startTime = mediaPlayer.getCurrentPosition();

        // если первый раз проигрывается
        if (oneTimeOnly == 0) {
            // настраиваем seekbar на основе длины трека
            seekbar.setMax((int) finalTime);
            // и меняем значение, что уже проигрывался
            oneTimeOnly = 1;
        }
        // тут присваеваем сколько идет трек
        tx2.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );
        // тут присваеваем его длительность
        tx1.setText(String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                startTime)))
        );
        // присваиваем каждые 100 миллисекунд прогресс текущего воспроизведения
        seekbar.setProgress((int) startTime);
        // задаем шаг
        myHandler.postDelayed(UpdateSongTime, 100);
        // кнопку пауза делаем активной
        buttonPause.setEnabled(true);
        // кнопку плей делаем неактивной
        buttonPlay.setEnabled(false);
    }

    public void pause() {
        Toast.makeText(getApplicationContext(), "Pausing sound", Toast.LENGTH_SHORT).show();
        mediaPlayer.pause();
        buttonPause.setVisibility(View.INVISIBLE);
        buttonPlay.setVisibility(View.VISIBLE);
        buttonPause.setEnabled(false);
        buttonPlay.setEnabled(true);
    }

    public void forward() {
        int temp = (int) startTime;

        if ((temp + forwardTime) <= finalTime) {
            startTime = startTime + forwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(), "You have Jumped forward 5 seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump forward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    public void rewind() {
        int temp = (int) startTime;

        if ((temp - backwardTime) > 0) {
            startTime = startTime - backwardTime;
            mediaPlayer.seekTo((int) startTime);
            Toast.makeText(getApplicationContext(), "You have Jumped backward 5 seconds", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Cannot jump backward 5 seconds", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_play:
                mediaPlayer.start();
                play();
                break;
            case R.id.button_pause:
                pause();
                break;
            case R.id.button_forward:
                forward();
                break;
            case R.id.button_rewind:
                rewind();
                break;
        }
    }

    public void initializationViewComponents() {
        buttonForward = (ImageButton) findViewById(R.id.button_forward);
        buttonPause = (ImageButton) findViewById(R.id.button_pause);
        buttonPlay = (ImageButton) findViewById(R.id.button_play);
        buttonRewind = (ImageButton) findViewById(R.id.button_rewind);
        iv = (ImageView) findViewById(R.id.imageView);
        tx1 = (TextView) findViewById(R.id.textView2);
        tx2 = (TextView) findViewById(R.id.textView3);
        songTitle = (TextView) findViewById(R.id.textView4);
        seekbar = (SeekBar) findViewById(R.id.seekBar);
        seekbar.setClickable(false);
        buttonPause.setEnabled(false);
        buttonForward.setOnClickListener(this);
        buttonPause.setOnClickListener(this);
        buttonPlay.setOnClickListener(this);
        buttonRewind.setOnClickListener(this);
    }

    public void searchMedia() {
        // получаем собственно сам контент провайдер
        ContentResolver contentResolver = getContentResolver();
        // получаем uri где хранятся аудио треки ( в нашем случае папка Music) <- круто если и правда работает
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        // передаем в объект курсор путь к этой "папке" (репозиторию), чтобы он принял в себя всю таблицу, без указания защиты,
        // выбора секции, массива(?) и сортировки)
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor == null) {
            // если вернулся пустой курсор - перехватываем ошибку или выводим сообщение
            // если нет
        } else if (!cursor.moveToFirst()) {
            // если перенос курсора не срабатывает на первую строчку первого столбца
            // no media on the device
        } else { // иначе
            // вытаскиваем из курсора номера столбца названия печни
            int titleColumn = cursor
                    .getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            // вытаскиваем из курсора номера столбца названия печни
            int idColumn = cursor
                    // ID таблицы (но он вроде на фиг не нужен)
                    .getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            // вытаскиваем из курсора номера столбца с именем автора
            int autor = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do {
                // а вот тут уже вытаскиваем инфу
                // тут по номеру вытаскиваем ID
                long thisId = cursor.getLong(idColumn);
                // тут название название
                String thisTitle = cursor.getString(titleColumn);
                // ...process entry...
                // по любому можно еще что-то вытащить
                // пока курсор двигается в начало следующей строки
            } while (cursor.moveToNext());
        }
    }
}

