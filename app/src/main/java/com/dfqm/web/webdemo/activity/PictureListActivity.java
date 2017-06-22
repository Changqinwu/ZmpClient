package com.dfqm.web.webdemo.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.utils.FileUtils;
import com.dfqm.web.webdemo.utils.SharedPreferencesUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PictureListActivity extends BaseActivity implements View.OnClickListener {


    private static HashMap<Integer, Boolean> isSelected;
    private static ArrayList<String> Filelists = new ArrayList<>();
    private static ArrayList<String> pictureLists = new ArrayList<>();
    private Map<Integer, View> map = new HashMap<>();
    private GridView gv;
    private ImageView mImaExit;
    private long exitTime;
    //立即播放
    private TextView mTvPlay;
    //空数据布局
    private RelativeLayout mRlEmpty;
    private ImageView mImaOpenVideoList;
    //遍历的图片格式
    private String[] fileType = new String[]{".jpg", ".png"};
    private String path;
    private ImageView mImaRightExitApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_list_layout);

        initView();

        //获取usb的文件夹的图片
        getUsbFolder();

        initData();

    }

    private void getUsbFolder() {
        String path = SharedPreferencesUtils.getString(PictureListActivity.this, "path", "");
        if (!"".equals(path)) {
            //初始化文件列表
            Filelists.clear();
            File file = new File(path);
            if (file != null) {
                //遍历文件
                search(file);
            }
        }else {
            //初始化文件列表
            Filelists.clear();
        }
    }


    private void initData() {
//        Filelists.clear();
//        //遍历图片
//        ArrayList<String> specificTypeOfFile = FileUtils.getSpecificTypeOfFile(this, fileType, Filelists);
        //设置适配器
        MyAdapter adapter = new MyAdapter(this, Filelists);
        gv.setAdapter(adapter);
    }

    private void initView() {
        gv = (GridView) findViewById(R.id.gv);
        mImaExit = (ImageView) findViewById(R.id.ima_exit_app);
        mTvPlay = (TextView) findViewById(R.id.tv_play);
        mRlEmpty = (RelativeLayout) findViewById(R.id.re_empty);
        mImaOpenVideoList = (ImageView) findViewById(R.id.ima_open_videolist);
        mImaRightExitApp = (ImageView) findViewById(R.id.ima_rignt_top_exit_app);
        mImaRightExitApp.setOnClickListener(this);
        mImaExit.setOnClickListener(this);
        mTvPlay.setOnClickListener(this);
        mImaOpenVideoList.setOnClickListener(this);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.cb.toggle();
                // 将CheckBox的选中状况记录下来
                getIsSelected().put(position, holder.cb.isChecked());

            }
        });
    }


    private void search(File fileold) {
        try {
            File[] files = fileold.listFiles();
            if (files.length > 0) {
                for (int j = 0; j < files.length; j++) {
                    if (!files[j].isDirectory()) {
                        if (files[j].getName().indexOf(".png") > -1) {
                            path = files[j].getPath();
                            String replace = path.replace("null", "");
                            Filelists.add(replace);
                            //shuju.putString(files[j].getName().toString(),files[j].getPath().toString());
                        }else if (files[j].getName().indexOf(".jpg") > -1) {
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



    @Override
    protected void onResume() {
        super.onResume();
        pictureLists.clear();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            //退出app弹窗
            case R.id.ima_exit_app:
                exitAppDialog();
                break;
            //播放图片
            case R.id.tv_play:
                Intent intent = new Intent(this, PlayPictureActivity.class);
                intent.putExtra("path", pictureLists);
                startActivity(intent);
                finish();
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
                mRlEmpty.setVisibility(View.GONE);
                gv.setVisibility(View.VISIBLE);
                return files.size();
            } else {
                mRlEmpty.setVisibility(View.VISIBLE);
                gv.setVisibility(View.GONE);
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
                view = getLayoutInflater().inflate(R.layout.check_item_picture_layout, null);
                hodler.ima = (ImageView) view.findViewById(R.id.image);
                hodler.cb = (CheckBox) view.findViewById(R.id.cb);
                map.put(position, view);
                view.setTag(hodler);
            } else {
                view = map.get(position);
                hodler = (ViewHolder) view.getTag();
            }

            File pic_file = new File(files.get(position));
            Glide.with(context).load(pic_file).into(hodler.ima);
            hodler.cb.setChecked(getIsSelected().get(position));
            hodler.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    getIsSelected().put(position, isChecked);
                    String s = Filelists.get(position);
                    //显示选中状态
                    if (isChecked) {
                        pictureLists.add(s);
                    } else {
                        pictureLists.remove(s);
                    }
                    //取消按钮
                    if (pictureLists.size() == 0) {
                        pictureLists.clear();
                        mTvPlay.setVisibility(View.GONE);
                    } else {
                        mTvPlay.setVisibility(View.VISIBLE);
                    }
                    mTvPlay.setText("立即播放"+"("+"已选择了"+pictureLists.size()+"张图"+")");
                }
            });

            return view;
        }

    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    class ViewHolder {
        private ImageView ima;
        private CheckBox cb;
    }


}
