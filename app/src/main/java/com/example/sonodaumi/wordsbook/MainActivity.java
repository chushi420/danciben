package com.example.sonodaumi.wordsbook;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sonodaumi.wordsbook.Words.WordsContent;
import com.example.sonodaumi.wordsbook.Words.WordsContent.WordsItem;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends Activity{
    //FragmentManager fragmentManager;
    //FragmentTransaction transaction;

    Map<String,String> map;
    String[] words;
    String[] meanings;
    private String content;

    //static SharedPreferences sharedPreferences;
    //public static SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FragmentManager fragmentManager = getFragmentManager();
        //FragmentTransaction transaction = fragmentManager.beginTransaction();

        content = "";

        SharedPreferences sharedPreferences = getSharedPreferences("WordsList", MODE_PRIVATE);
        final SharedPreferences.Editor editor= sharedPreferences.edit();
        //test
        editor.putString("apple", "苹果");
        editor.putString("banana", "香蕉");
        editor.apply();

        SetData(sharedPreferences);

        final ListView wordList = (ListView)findViewById(R.id.wordList);
        // ListView容器 用于初始化（显示）列表
        // 参数：this， 单个元素的布局， 用于初始化的字符串数组（里面放姓名）
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.singleview, words);
        wordList.setAdapter(adapter);

        wordList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View itemView, final int i, final long l) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                DetailFragment detailFragment = new DetailFragment();
                transaction.replace(R.id.worddetail, detailFragment);
                transaction.commit();
                fragmentManager.executePendingTransactions();

                View view1 = detailFragment.getView();
                TextView textView = (TextView)view1.findViewById(R.id.detail);
                textView.setText(meanings[i]);

                Button deleteBtn = (Button)view1.findViewById(R.id.delete);
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextView textView1 = (TextView)itemView.findViewById(R.id.singleText);
                        String wordStr = textView1.getText().toString();

                        SharedPreferences sharedPreferences = getSharedPreferences("WordsList", MODE_PRIVATE);
                        final SharedPreferences.Editor editor= sharedPreferences.edit();
                        editor.remove(wordStr);
                        editor.apply();
                        //wordList.removeView(itemView);
                        SetData(sharedPreferences);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.singleview, words);
                        //adapter.notifyDataSetChanged();
                        wordList.setAdapter(adapter);

                        FragmentManager fragmentManager = getFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.worddetail, new Fragment());
                        transaction.commit();

                    }
                });
            }
        });
        /*
        Button btn[] = new Button[size];
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setId(R.id.my_view);

        int i = 0;
        for (final Map.Entry<String,String> entry : map.entrySet()) {
            btn[i]=new Button(this);
            btn[i].setId(2000+i);
            btn[i].setText(entry.getKey());
            btn[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DetailFragment detailFragment = new DetailFragment();

                    transaction.replace(R.id.worddetail, detailFragment);
                    transaction.commit();
                    TextView textView = (TextView)findViewById(R.id.detail);
                    textView.setText(entry.getValue());
                }
            });
            linearLayout.addView(btn[i]);
            i++;
        }
        LinearLayout layout = new LinearLayout(this);
        layout.addView(linearLayout);

        */
        Button btn1 = (Button)findViewById(R.id.word1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                SearchAddFragment searchAddFragment = new SearchAddFragment();
                transaction.replace(R.id.worddetail, searchAddFragment);
                transaction.commit();
                fragmentManager.executePendingTransactions();

                View view1 = searchAddFragment.getView();
                final EditText wordText = (EditText)view1.findViewById(R.id.word);
                final TextView resultText = (TextView)view1.findViewById(R.id.result);
                Button btnSearch = (Button)view1.findViewById(R.id.search);
                Button btnAdd = (Button)view1.findViewById(R.id.addItem);

                btnSearch.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(wordText.getText().toString().isEmpty()){
                            Toast.makeText(MainActivity.this, "输入为空,请重新输入!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final String path = "http://dict-co.iciba.com/search.php?word=" +  wordText.getText().toString() + "&submit=查询";
                        Toast.makeText(MainActivity.this, path, Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                try {
                                    URL url = new URL(path);
                                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                                    httpURLConnection.setRequestMethod("GET");
                                    int code = httpURLConnection.getResponseCode();
                                    if(code == 200){
                                        InputStream in = httpURLConnection.getInputStream();
                                        InputStreamReader isr = new InputStreamReader(in,"utf-8");
                                        BufferedReader reader = new BufferedReader(isr);
                                        String line = null;
                                        int i = 0;
                                        while((line = reader.readLine()) != null) {
                                            i++;
                                            if(i == 22) {
                                                content += line;
                                                break;
                                            }
                                        }
                                        resultText.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                content = content.replaceAll("&nbsp;","\n");
                                                content = content.replaceAll("<br />","");
                                                resultText.setText(content);
                                                content = "";
                                            }
                                        });
                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "连接网络失败,请重试！", Toast.LENGTH_SHORT);
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }).start();
                    }
                });

                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String wordTextStr = wordText.getText().toString();
                        String resultTextStr = resultText.getText().toString();
                        if(wordTextStr.isEmpty() || resultTextStr.isEmpty()){
                            Toast.makeText(MainActivity.this, "查询结果为空！不能添加！", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("WordsList", MODE_PRIVATE);
                        final SharedPreferences.Editor editor= sharedPreferences.edit();
                        editor.putString(wordTextStr, resultTextStr);
                        editor.apply();
                        SetData(sharedPreferences);
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, R.layout.singleview, words);
                        wordList.setAdapter(adapter);
                    }
                });
            }
        });



    }

    void SetData(SharedPreferences sharedPreferences){
        map = (Map<String, String>)sharedPreferences.getAll();
        //int size = map.size();
        Set keySet = map.keySet();
        Collection<String> valueCol = map.values();
        words = (String[])keySet.toArray(new String[keySet.size()]);
        meanings = (String[])valueCol.toArray(new String[valueCol.size()]);
    }

}
