package com.example.guo.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.guo.R;
import com.example.guo.bean.ImgBean;
import com.example.guo.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class FirstFragment extends Fragment {
    private String baseUrl;
    private SwipeRefreshLayout swipe;
    private MyAdapter adapter;
    //private final String url = "http://m.photo.67.com/";
    //private final String url = "http://www.deviantart.com/browse/all/?offset=0";
    //private final String url = "https://www.artstation.com/artwork?sorting=best_of_2015";
    private final String url = "http://konachan.net/post?page=1&tags=";
    private static int currentPage = 0;
    private ArrayList<ImgBean> totalList = new ArrayList<ImgBean>();
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        swipe = (SwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                    }
                }, 3000);
            }
        });
        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        initData(url);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return view;
    }

    private void initData(final String url) {
        loadNetData(url);
    }


    /**
     * 加载网络数据
     *
     * @param url
     */
    private void loadNetData(final String url) {
        new Thread(new Runnable() {
            public void run() {
                // Document doc = Jsoup.connect(url)
//                            .header("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36")
//                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
//                            .header("Accept-Encoding","gzip, deflate, sdch")
//                            .header("Accept-Language","zh-CN,zh;q=0.8")
//                            .header("Cache-Control","max-age=0")
//                            .header("Connection","keep-alive")
//                            .header("Host","www.artstation.com")
//                            .timeout(10000).get();
                //先用HttpUrlConnection/OkHttp等获取到网页的源码,再使用Jsoup.parse()解析得的到网页内容,未测试
                //该网站内容是延迟加载的,该如何解决?
                //Q:如何爬去延迟加载的网页?
                parseHtml(url);
            }
        }).start();
    }

    /**
     * 获取网页源码
     *
     * @param url
     */
    private void getHtml(final String url) {

        try {
            URL url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            if (conn.getResponseCode() == 200) {
                InputStream in = conn.getInputStream();
                int len = 0;
                byte[] b = new byte[1024];
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while ((len = in.read(b)) != -1) {
                    bos.write(b, 0, len);
                }
                in.close();
                String str = new String(bos.toByteArray(), "UTF-8");
                Log.e("gg", "网页源码" + str);
                parseHtml(str);

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * jsoup解析
     *
     * @param html
     */
    private void parseHtml(String html) {
        //Document doc = Jsoup.parse(html);
        Document doc = null;
        try {
            doc = Jsoup.connect(html).timeout(5000).get();

            //https://www.artstation.com/artwork?sorting=best_of_2015
            Elements els = doc.select("#post-list-posts");
            Log.e("gg","doc wendang:"+doc.html());
            Log.e("gg", "element count:" + els.size() + "");
            ArrayList<ImgBean> currentList = new ArrayList<ImgBean>();
            ImgBean bean = null;
            for (Element element : els) {
                bean = new ImgBean();
                String src = element.select("a.thumb").select("img").attr("src");
                bean.setSrc(src);
                currentList.add(bean);
            }
            Log.e("gg", "currentList" + currentList.size() + "");
            totalList.addAll(currentList);
            save2Local(totalList, "all_backup");//应该放在子线程
            if (currentList != null) {

                CommonUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        Log.e("gg", "adapter count" + adapter.getItemCount() + "");
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * 保存数据到本地
     *
     * @param totalList
     * @param str
     */
    private void save2Local(ArrayList<ImgBean> totalList, String str) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = getActivity().openFileOutput(str, Context.MODE_PRIVATE);

            oos = new ObjectOutputStream(fos);
            oos.writeObject(totalList);
            oos.flush();
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    fos = null;

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                    oos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

    /***
     * 反序列化文件
     *
     * @param filename
     * @return
     */
    private ArrayList decodeFromLocal(String filename) {
        if (!isCacheExist(filename)) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = getActivity().openFileInput(filename);
            ois = new ObjectInputStream(fis);

            return (ArrayList) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            if (e instanceof InvalidClassException) {
                // 反序列化失败 - 删除缓存文件
                File data = getActivity().getFileStreamPath(filename);
                data.delete();
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 判断文件是否存在
     *
     * @param filename
     * @return
     */
    private boolean isCacheExist(String filename) {
        boolean isExsit = false;
        File cache = getActivity().getFileStreamPath(filename);
        if (cache.exists()) {
            isExsit = true;
        }
        return isExsit;
    }


    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View convertView = View.inflate(getActivity(), R.layout.item_firstfrag, null);

            return new MyViewHolder(convertView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
//            File imgFile = ImageLoader.getInstance().getDiskCache().get(totalList.get(position).getSrc());
//            if(imgFile.exists()){
//                holder.img.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
//                System.out.println("从本地加载的图片");
//            }
            ImageLoader.getInstance().displayImage(totalList.get(position).getSrc(), holder.img);
            holder.tv.setText(totalList.get(position).getDetail());
        }

        @Override
        public int getItemCount() {
            return totalList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public ImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

}
