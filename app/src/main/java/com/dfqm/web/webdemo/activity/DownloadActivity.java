package com.dfqm.web.webdemo.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dfqm.web.webdemo.R;
import com.dfqm.web.webdemo.constants.Constant;
import com.dfqm.web.webdemo.download.DownloadInfo;
import com.dfqm.web.webdemo.download.DownloadManager;
import com.dfqm.web.webdemo.download.DownloadState;
import com.dfqm.web.webdemo.download.DownloadViewHolder;
import com.dfqm.web.webdemo.utils.FileUtils;

import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import static com.dfqm.web.webdemo.constants.Constant.CANNOT_CHANGE;


public class DownloadActivity extends BaseActivity {

    private ListView downloadList;
    private DownloadManager downloadManager;
    private DownloadListAdapter downloadListAdapter;
    //用于存储播放下载到本地的视频列表
    private ArrayList<String> download_list = new ArrayList<>();
    private DoanloadListReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        //显示弹窗
        progressDialog.showProgressDialog(this, "正在准备视频...");

        //发送广播，告诉后台正在进行加载视频，不能修改方案
        Intent intent = new Intent(CANNOT_CHANGE);
        sendBroadcast(intent);

        //xutils3初始化
        x.view().inject(this);
        //初始化view
        initView();


    }

    private void initView() {
        downloadList = (ListView) findViewById(R.id.lv);
        downloadManager = DownloadManager.getInstance();
        downloadListAdapter = new DownloadListAdapter();
        downloadList.setAdapter(downloadListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册广播，关闭页面
        receiver = new DoanloadListReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.CLOSE_VIDEO);
        filter.addAction(Constant.CLOSE_DOWNLOAD_VIDEO);
        registerReceiver(receiver, filter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(receiver);
    }

    private class DownloadListAdapter extends BaseAdapter {

        private Context mContext;
        private final LayoutInflater mInflater;

        private DownloadListAdapter() {
            mContext = getBaseContext();
            mInflater = LayoutInflater.from(mContext);
        }

        @Override
        public int getCount() {
            if (downloadManager == null) return 0;
            return downloadManager.getDownloadListCount();
        }

        @Override
        public Object getItem(int i) {
            return downloadManager.getDownloadInfo(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            DownloadItemViewHolder holder = null;
            DownloadInfo downloadInfo = downloadManager.getDownloadInfo(position);
            if (view == null) {
                view = mInflater.inflate(R.layout.download_item, null);
                holder = new DownloadItemViewHolder(view, downloadInfo, position);
                view.setTag(holder);
                holder.refresh();
            } else {
                holder = (DownloadItemViewHolder) view.getTag();
                holder.update(downloadInfo);
            }

            if (downloadInfo.getState().value() < DownloadState.FINISHED.value()) {
                try {
                    downloadManager.startDownload(
                            downloadInfo.getUrl(),
                            downloadInfo.getLabel(),
                            downloadInfo.getFileSavePath(),
                            downloadInfo.isAutoResume(),
                            downloadInfo.isAutoRename(),
                            holder);
                } catch (DbException ex) {
                    Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                }
            }

            return view;
        }
    }

    public class DownloadItemViewHolder extends DownloadViewHolder {


        private final TextView label;
        private final TextView mstate;
        private final ProgressBar progressBar;
        private final Button stopBtn;
        private final int position;
        private final TextView tv_percent;
        private final TextView tv_total;

        public DownloadItemViewHolder(View view, DownloadInfo downloadInfo, int positon) {
            super(view, downloadInfo);
            this.position = positon;

            label = (TextView) view.findViewById(R.id.download_label);
            mstate = (TextView) view.findViewById(R.id.download_state);
            progressBar = (ProgressBar) view.findViewById(R.id.download_pb);
            stopBtn = (Button) view.findViewById(R.id.download_stop_btn);
            tv_percent = (TextView) view.findViewById(R.id.tv_percent);
            tv_total = (TextView) view.findViewById(R.id.tv_total);

            refresh();
        }

        @Event(R.id.download_stop_btn)
        private void toggleEvent(View view) throws DbException {
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                case STARTED:
                    downloadManager.stopDownload(downloadInfo);
                    break;
                case ERROR:
                case STOPPED:
                    try {
                        downloadManager.startDownload(
                                downloadInfo.getUrl(),
                                downloadInfo.getLabel(),
                                downloadInfo.getFileSavePath(),
                                downloadInfo.isAutoResume(),
                                downloadInfo.isAutoRename(),
                                this);
                    } catch (DbException ex) {
                        Toast.makeText(x.app(), "添加下载失败", Toast.LENGTH_LONG).show();
                    }
                    break;
                case FINISHED:
                    downloadManager.removeDownload(downloadInfo);
                    downloadListAdapter.notifyDataSetChanged();
                    Toast.makeText(x.app(), "已经下载完成", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

        @Event(R.id.download_remove_btn)
        private void removeEvent(View view) {
            try {
                downloadManager.removeDownload(downloadInfo);
                downloadListAdapter.notifyDataSetChanged();
            } catch (DbException e) {
                Toast.makeText(x.app(), "移除任务失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void update(DownloadInfo downloadInfo) {
            super.update(downloadInfo);
            refresh();
        }

        @Override
        public void onWaiting() {
            refresh();
        }

        @Override
        public void onStarted() {
            refresh();
        }

        @Override
        public void onLoading(long total, long current) {
            refresh();
            tv_percent.setText(current*100/total + "%");
            tv_total.setText(FileUtils.bytes2kb(total) + "");
        }

        @Override
        public void onSuccess(File result) throws DbException {

            download_list.add(result.toString());

            //下载成功后移除当前那一列
            downloadManager.removeDownload(downloadInfo);
            downloadListAdapter.notifyDataSetChanged();
            refresh();
            if (downloadManager.getDownloadListCount() == 0) {

                //下载完成发送广播
                Intent intent2 = new Intent(Constant.CHANGE);
                sendBroadcast(intent2);

                //下载完后进入播放视频页
                Intent intent = new Intent(DownloadActivity.this, PlayVideoActivity.class);
                intent.putExtra(Constant.VIDEO_URL, download_list);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onError(Throwable ex, boolean isOnCallback) {
            refresh();
            DownloadActivity.this.finish();
        }

        @Override
        public void onCancelled(Callback.CancelledException cex) {
            refresh();
        }

        public void refresh() {
            label.setText(downloadInfo.getLabel());
            mstate.setText(downloadInfo.getState().toString());
            progressBar.setProgress(downloadInfo.getProgress());
            stopBtn.setVisibility(View.VISIBLE);
            stopBtn.setText("停止");
            DownloadState state = downloadInfo.getState();
            switch (state) {
                case WAITING:
                    mstate.setText("等待");
                case STARTED:
                    stopBtn.setText("停止");
                    break;
                case ERROR:
                    mstate.setText("出错了");
                case STOPPED:
                    stopBtn.setText("开始下载");
                    break;
                case FINISHED:
                    mstate.setText("完成");
                    stopBtn.setVisibility(View.INVISIBLE);
                    break;
                default:
                    stopBtn.setText("开始下载");
                    break;
            }
        }
    }


    class DoanloadListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.CLOSE_DOWNLOAD_VIDEO == action) {
//                //关闭下载
                downloadManager.stopAllDownload();
                //关闭页面
                finish();
            } else if (Constant.CLOSE_VIDEO == action) {
                //关闭页面
                finish();
            }
        }
    }


}
