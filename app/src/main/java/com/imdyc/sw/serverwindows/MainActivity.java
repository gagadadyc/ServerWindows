package com.imdyc.sw.serverwindows;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.imdyc.sw.serverwindows.application.ResInfoApplication;
import com.imdyc.sw.serverwindows.bean.SysPoint;
import com.imdyc.sw.serverwindows.utility.ArrayListPojo;
import com.imdyc.sw.serverwindows.utility.MapIntent;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //控制台服务器列表
//        listView = (ListView)findViewById(R.id.listView_Console);
//        dataList = new ArrayList<Map<String, Object>>();
//        simpleAdapter = new SimpleAdapter(this,getData(),R.layout.console_server_icon,
//                new String[]{"console_server_icon","server_text","server_ip"},
//                new int[]{R.id.console_server_icon,R.id.server_text,R.id.server_ip});
//        listView.setAdapter(simpleAdapter);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//之前悬浮的邮件按钮，现已删除
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

//    //将服务器传过来的主机信息包装成List
//    private  List<Map<String, Object>> getData() {
//        //服务器个数
//        int ServerCon = 20;
//        for(int i=1;i<=ServerCon;i++){
//            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("console_server_icon",R.mipmap.ic_launcher);
//            map.put("server_text", " " + "Server"+i);
//            map.put("server_ip", " " + "192.168.0.1");
//
//            dataList.add(map);
//        }
//        return dataList;
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Handle the camera action
        //menu/activity_main_drawer.xml(左拉菜单)中的列表项，一项对应一行
        if (id == R.id.nav_console) {
            //网络请求
            volley_Post();
            //等待请求跳转
            threadWaitJump();

        } else if (id == R.id.nav_security) {

            Intent intent = new Intent(this,SecurityActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_ServerQuantityManagement) {

//        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void volley_Post() {
        Toast.makeText(MainActivity.this, "数据正在拉取,请稍后", Toast.LENGTH_SHORT).show();
        String url = "http://192.168.0.101:8080/sw/ServerList";
        //请求数据,第一次请求的是服务器列表,无须带参数

        JsonObjectRequest JOrequest = new JsonObjectRequest(Request.Method.POST,url,null,new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject arg0) {
                //获取存储文件
                SharedPreferences sharedPreferences = getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("sysPointList",arg0.toString());//将Json格式的服务器数据存入SharedPreferences中
                editor.apply();
            }
        },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError arg0){
                Toast.makeText(MainActivity.this,arg0.toString()+"请求失败",Toast.LENGTH_LONG).show();
            }
        });
        JOrequest.setTag("ServerInfoPost");

        ResInfoApplication.getHttpQueues().add(JOrequest);
    }

    private void threadWaitJump() {
        //若未取回数据，则休眠等待
        Thread thread1 = new Thread(){
            @Override
            public void run(){

                boolean threadbl = false;
                try {
                    int i = 0;
                   //轮询方式查找getSharedPreferences，Server无数据则继续查找
                    while (!threadbl && getSharedPreferences("ServerWindows", Context.MODE_PRIVATE).getString("sysPointList", "") == "") {
                        Thread.sleep(100);//如果找不到，则休眠100毫秒再访问。
                        i++;
                        System.out.println("i:"+i);
                        if(i>100){
                            Looper.prepare();
                            Toast.makeText(MainActivity.this, "访问超时", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            threadbl = Thread.interrupted();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //若线程未被设置中断，则说明访问到了数据
                if(!threadbl) {
                    SharedPreferences preferences=getSharedPreferences("ServerWindows", Context.MODE_PRIVATE);
                    String serverMapJson = preferences.getString("sysPointList", "");  // getString()第二个参数为缺省值，如果preference中不存在该key，将返回缺省值

                    //Json反序列化
                    ArrayListPojo listPojo = gson.fromJson(serverMapJson, ArrayListPojo.class);
                    ArrayList<SysPoint> sysPointList = listPojo.getList();

                    Intent intent = new Intent(MainActivity.this,ConsoleActivity.class);
                    System.out.println("getHost233:"+serverMapJson);
                    intent.putExtra("sysPointList", sysPointList);

                    //线程内跳转应使用Looper
                    Looper.prepare();
                    startActivity(intent);
                    Looper.loop();

                }
            }
        };
        thread1.start();
    }


}
