package com.dfqm.web.webdemo.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.utils.FileUtils;
import com.dfqm.web.webdemo.utils.ProgressDialogUtil;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;
import com.dfqm.web.webdemo.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class VideoListActivity extends BaseActivity implements View.OnClickListener {


    private static HashMap<Integer, Boolean> isSelected;
    private ListView lv;
    private static ArrayList<String> Filelists = new ArrayList<>();
    private static ArrayList<String> videoLists = new ArrayList<>();
    private ImageView mImaExit;
    private long exitTime;
    private ImageView mImaOpenVideoList;
    //保存position对应的view
    private Map<Integer, View> map = new HashMap<>();
    //立即播放
    private TextView mTvPlay;
    //没数据空布局
    private RelativeLayout mRlEmpty;
    //遍历的视频格式
    private String[] fielType = new String[]{".3gp", ".mp4", ".mpg", ".mkv", ".avi", ".wmv", ".flv", ".mov"};
    private String path;
    private MyAdapter adapter;
    private UsbBroadcastReceiver mBroadcastReceiver;
    private ProgressDialogUtil dialogUtil;
    private ImageView mImaExitRightApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list_layout);

        initView();
        //获取usb文件夹下的视频
        getUsbFolder();

        initData();
    }


    private void getUsbFolder() {
        dialogUtil = new ProgressDialogUtil();
        dialogUtil.showProgressDialog(this, "正在查找...");
        String path = SharedPreferencesUtils.getString(VideoListActivity.this, "path", "");
        if (!"".equals(path)) {
            //初始化文件列表
            Filelists.clear();
            Toast.makeText(VideoListActivity.this, "保存了usb路径" + path + "", Toast.LENGTH_LONG).show();
            File file = new File(path);
            if (file != null) {
                //遍历文件
                search(file);
            }
        } else {
            //初始化文件列表
            Filelists.clear();
        }
    }


    private void search(File fileold) {
        try {
            File[] files = fileold.listFiles();
            if (files.length > 0) {
                for (int j = 0; j < files.length; j++) {
                    if (!files[j].isDirectory()) {
                        if (files[j].getName().indexOf(".mp4") > -1) {
                            path = files[j].getPath();
                            String replace = path.replace("null", "");
                            Filelists.add(replace);
                            //shuju.putString(files[j].getName().toString(),files[j].getPath().toString());
                        } else if (files[j].getName().indexOf(".MP4") > -1) {
                            path = files[j].getPath();
                            String replace = path.replace("null", "");
                            Filelists.add(replace);
                        }
                    } else {
                        this.search(files[j]);
                    }
                }
            }
        } catch (Exception e) {
        }
    }


    private void initData() {

        //遍历出视频文件
//        ArrayList<String> specificTypeOfFileLists = FileUtils.getSpecificTypeOfFile(this, fielType, Filelists);
        //设置适配器
        dialogUtil.dismissProgressDialog();
        adapter = new MyAdapter(this, Filelists);
        lv.setAdapter(adapter);
    }

    private void initView() {
        lv = (ListView) findViewById(R.id.lv);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mTvPlay = (TextView) findViewById(R.id.tv_play);
        mRlEmpty = (RelativeLayout) findViewById(R.id.re_empty);
        mImaExitRightApp = (ImageView) findViewById(R.id.ima_rignt_top_exit_app);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaExitRightApp.setOnClickListener(this);
        mTvPlay.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
        mImaExit.setOnClickListener(this);
        mRlEmpty.setOnClickListener(this);
        //列表点击事件
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.cb.toggle();
                // 将CheckBox的选中状况记录下来
                getIsSelected().put(position, holder.cb.isChecked());

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoLists.clear();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                exitAppDialog();
                break;

            //立即播放
            case R.id.tv_play:
                Intent intent = new Intent(this, PlayVideoActivity.class);
                intent.putExtra(Constant.VIDEO_URL, videoLists);
                startActivity(intent);
                finish();
                break;

            //点击刷新
            case R.id.re_empty:
                initData();
                break;

            //选择列表
            case R.id.ima_open_videolist:
                showSelecFileLists(this,this);
                break;

            //右上角退出app
            case R.id.ima_rignt_top_exit_app:
                exitAppDialog();
                break;
        }
    }

    private void exitAppDialog() {
        if ((System.currentTimeMillis() - exitTime) > 500) {
            exitTime = System.currentTimeMillis();
        } else {
            dialogExit(this);
        }
    }

    class MyAdapter extends BaseAdapter {
        private final ArrayList<String> files;
        private final Context context;

        public MyAdapter(Context context, ArrayList<String> files) {
            this.files = files;
            this.context = context;
            isSelected = new HashMap<Integer, Boolean>();
            //初始化数据
            initDate();
        }

        // 初始化isSelected的数据
        private void initDate() {
            for (int i = 0; i < files.size(); i++) {
                getIsSelected().put(i, false);
            }
        }

        @Override
        public int getCount() {
            if (files.size() != 0) {
                lv.setVisibility(View.VISIBLE);
                mRlEmpty.setVisibility(View.GONE);
                return files.size();
            } else {
                lv.setVisibility(View.GONE);
                mRlEmpty.setVisibility(View.VISIBLE);
                return 0;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            final ViewHolder hodler;
            if (map.get(position) == null) {
                hodler = new ViewHolder();
                view = getLayoutInflater().inflate(R.layout.check_item_video_layout, null);
                hodler.tv = (TextView) view.findViewById(R.id.tv);
                hodler.cb = (CheckBox) view.findViewById(R.id.cb);
                map.put(position, view);
                view.setTag(hodler);
            } else {
                view = map.get(position);
                hodler = (ViewHolder) view.getTag();
            }

            hodler.tv.setText(files.get(position) + "");
            hodler.cb.setChecked(getIsSelected().get(position));
            hodler.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    getIsSelected().put(position, isChecked);
                    String s = Filelists.get(position);
                    //显示选中状态
                    if (isChecked) {
                        videoLists.add(s);
                    } else {
                        videoLists.remove(s);
                    }

                    //取消按钮
                    if (videoLists.size() == 0) {
                        videoLists.clear();
                        mTvPlay.setVisibility(View.GONE);
                    } else {
                        mTvPlay.setVisibility(View.VISIBLE);
                    }
                    mTvPlay.setText("立即播放" + "(" + "已选择了" + videoLists.size() + "个视频" + ")");

                }
            });
            return view;
        }


    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    class ViewHolder {
        private TextView tv;
        private CheckBox cb;
    }


}
