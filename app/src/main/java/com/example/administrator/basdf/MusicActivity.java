package com.example.administrator.basdf;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    Button button1,button2,button3; // 뒤로가기 재생 앞으로가기 버튼
    SeekBar seekbar; // 시크바
    MediaPlayer music = null; // 현재 재생되는 MediaPlayer
    ListView lvFileControl; // 공유할 음악 리스트
    ImageView mimage; // 앨범이미지 출력할 이미지뷰
    int Marg,Lleng=0;

    private BackPressCloseHandler backPressCloseHandler; // Back키 눌렀을때 처리하는 핸들러
    private List mFileList = new ArrayList(); // List뷰에서 음악파일 리스트
    private List mList = new ArrayList();    //   위와 같음.
    private File Musicfolder1 = new File(Environment.getExternalStorageDirectory() + "/Music","");  // 뮤직폴더에서 찾기위해
    private File Musicfolder2 = new File(Environment.getExternalStorageDirectory() + "/Download",""); // 다운로드폴더에서 찾기위해
    private static final String[] FTYPE = {"mp3","wav"}; // 찾는타입 (.mp3 , .wav)형식 찾음
    private static String file_nm = null;  //음악파일의 uri를 string으로 받음

    private File targetFile;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        Toast.makeText(this,"공유할 음악을 선택해 주세요.",Toast.LENGTH_SHORT).show();
        backPressCloseHandler = new BackPressCloseHandler(this);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        seekbar = (SeekBar) findViewById(R.id.seekBar1);
        mimage = (ImageView) findViewById(R.id.Mimage);

        button1.setEnabled(false);
        button2.setEnabled(false);
        button3.setEnabled(false);
        seekbar.setEnabled(false);
        mimage.setVisibility(View.INVISIBLE);

        lvFileControl = (ListView)findViewById(R.id.lvFileControl);

        targetFile = null;

        mFileList.clear();
        loadAllAudioList(Musicfolder1);// music
        loadAllAudioList(Musicfolder2);// download

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, mFileList);
        lvFileControl.setAdapter(adapter);
        lvFileControl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Lleng = mList.size();
                file_nm = (String) mList.get(arg2);

                File f = new File(file_nm);
                targetFile = f;
                returnTarget();


                Marg = arg2;
                setFilename(file_nm);
                mediaCreate();
                mimage.setVisibility(View.VISIBLE);

                button1.setEnabled(true);
                button2.setEnabled(true);
                button3.setEnabled(true);
                seekbar.setEnabled(true);
                button1.callOnClick();

            }
        });





        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                if (fromUser)
                    music.seekTo(progress);
            }
        });


    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    public void button1(View v){
            if(music.isPlaying()){
// 재생중이면 실행될 작업 (일시 정지)
                music.pause();
                mimage.setVisibility(View.INVISIBLE);
                try {
                    music.prepare();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                music.getCurrentPosition();

                button1.setBackgroundResource(R.drawable.play);
                seekbar.setProgress(music.getCurrentPosition());
            }else {
// 재생중이 아니면 실행될 작업 (재생)

                music.start();
                mimage.setVisibility(View.VISIBLE);
                button1.setBackgroundResource(R.drawable.stop);

                Thread();


            }

    }

    public void button2(View v) {
        if(Marg==0){
            Marg=Lleng;
        }
        music.stop();
        Marg = Marg - 1;
        music = MediaPlayer.create(getApplicationContext(), Uri.parse((String) mList.get(Marg)));
        seekbar.setMax(music.getDuration());
        button1.callOnClick();
        setFilename((String) mList.get(Marg));
    }

    public void button3(View v){
        if(Marg==Lleng-1){
            Marg=-1;
        }
        music.stop();
        Marg = Marg + 1;
        music = MediaPlayer.create(getApplicationContext(), Uri.parse((String) mList.get(Marg)));
        seekbar.setMax(music.getDuration());
        button1.callOnClick();
        setFilename((String) mList.get(Marg));
    }

    public void Thread(){
        Runnable task = new Runnable(){
            public void run(){
                /**
                 * while문을 돌려서 음악이 실행중일때 게속 돌아가게
                 */
                while(music.isPlaying()){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    /**
                     * music.getCurrentPosition()은 현재 음악 재생 위치를 가져오는 구문
                     */
                    seekbar.setProgress(music.getCurrentPosition());
                    music.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                        public void onCompletion(MediaPlayer mp) {
                            if(Marg==Lleng-1){
                                Marg=-1;
                            }
                            Marg = Marg + 1;
                            music = MediaPlayer.create(getApplicationContext(), Uri.parse((String) mList.get(Marg)));
                            seekbar.setMax(music.getDuration());
                            button1.callOnClick();
                            setFilename((String) mList.get(Marg));

                        }
                    });
                }
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }


    private void loadAllAudioList(File file){
        if (file != null && file.isDirectory())
        {
            File[] children = file.listFiles();
            if (children != null)
            {

                for(int i = 0; i < children.length; i++)
                {
                    if (children[i] != null)
                    {
                        for(int j=0;j<FTYPE.length;j++){
                            if(FTYPE[j].equals(children[i].getName().substring(children[i].getName().lastIndexOf(".")+1,
                                    children[i].getName().length()))){
                                mFileList.add(children[i].getName());
                                mList.add(children[i].getAbsolutePath());

                            }
                        }
                    }
                    loadAllAudioList(children[i]);
                }
            }
        }
    }


    public void setFilename(String file_name){
        TextView  tx = (TextView)findViewById(R.id.tvPath);
        String path = file_name;
        String fileName = new File(path).getName();
        tx.setText(fileName);
    }


    public void mediaCreate() {
        if(music != null) {
            music.stop();
            button1.setBackgroundResource(R.drawable.play);
        }
        music = MediaPlayer.create(getApplicationContext(),Uri.parse(file_nm));
        music.setLooping(false);

        seekbar.setMax(music.getDuration());



    }


    public class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;


        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
                System.exit(0);
            }
        }

        private void showGuide() {
            toast = Toast.makeText(activity, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void returnTarget()
    {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("file", targetFile);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

}



