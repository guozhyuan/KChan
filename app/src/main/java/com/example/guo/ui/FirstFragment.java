package com.example.guo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
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
import android.widget.TextView;

import com.example.guo.R;
import com.example.guo.bean.ImgBean;
import com.example.guo.util.CommonUtil;
import com.example.guo.util.RecyclerItemClickListener;
import com.example.guo.util.SuperSwipeRefreshLayout;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

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

/**
 * Create by g
 */
public class FirstFragment extends Fragment {
    private static String TAG = "FirstFragment";
    private String baseUrl;
    private SuperSwipeRefreshLayout swipe;
    private MyAdapter adapter;
    //private final String url = "http://m.photo.67.com/";
    //private final String url = "http://www.deviantart.com/browse/all/?offset=0";
    //private final String url = "https://www.artstation.com/artwork?sorting=best_of_2015";
    //private final String url = "http://konachan.net/post?page=1&tags=";
    private static int currentPage = 1;
    //    private String url = "http://konachan.net/post?page="+currentPage+"&tags=";
    private String url = "http://konachan.net/post?page=";
    private ArrayList<ImgBean> totalList = new ArrayList<ImgBean>();
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        swipe = (SuperSwipeRefreshLayout) view.findViewById(R.id.swipe);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        //SpacesItemDecoration decoration=new SpacesItemDecoration(16);


        swipe.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                loadNetData(url + currentPage);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);
                        adapter.notifyDataSetChanged();
                        Log.e(TAG, "run: currentPage = " + currentPage);
                    }
                }, 3000);
            }

            @Override
            public void onPullDistance(int distance) {

            }

            @Override
            public void onPullEnable(boolean enable) {

            }
        });
        swipe.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                currentPage++;
                loadNetData(url + currentPage);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        totalList.clear();
                        swipe.setLoadMore(false);
                        adapter.notifyDataSetChanged();
                        Log.e(TAG, "run: currentPage = " + currentPage);
                    }
                }, 3000);
            }

            @Override
            public void onPushDistance(int distance) {

            }

            @Override
            public void onPushEnable(boolean enable) {

            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //TODO
            }
        }));

        adapter = new MyAdapter();
        recyclerView.setAdapter(adapter);
        loadNetData(url + currentPage);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new SpacesItemDecoration(5));

        return view;
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
                parseHtml(url);
            }
        }).start();
    }

    /**
     * 获取网页源码
     * 未使用
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

        Document doc = null;
        try {
            doc = Jsoup.connect(html).timeout(5000).get();
            //Log.e("gg",doc.html());
            // Elements els = doc.select("ul#post-list-posts");
            Elements els = doc.select("div.inner");
            ArrayList<ImgBean> currentList = new ArrayList<ImgBean>();
            ImgBean bean = null;
            for (Element element : els) {
                bean = new ImgBean();
                String src = element.select("a.thumb").select("img").attr("src");
                int width = Integer.parseInt(element.select("a.thumb").select("img").attr("width"));
                int height = Integer.parseInt(element.select("a.thumb").select("img").attr("height"));
                bean.setSrc(src);
                bean.setWidth(width);
                bean.setHeight(height);
                //bean.setDetail(detail);
                currentList.add(bean);

                Log.e("gg", "imgURL" + src);
            }


            Log.e("gg", "currentList:" + currentList.size() + "条");
            totalList.addAll(currentList);

            save2Local(totalList, "all_backup");//应该放在子线程
            if (currentList != null) {

                CommonUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("gg", "报异常了!?");
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
            int height = totalList.get(position).getHeight();
            int width = totalList.get(position).getWidth();
            holder.img.setOriginalSize(width, height);
            holder.img.setImageResource(R.drawable.bg_card);//必须设置默认的图片!卧了个大槽
            ImageLoader.getInstance().displayImage(totalList.get(position).getSrc(), holder.img, ImageLoaderOptions.list_options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            holder.tv.setText(totalList.get(position).getDetail());
        }

        @Override
        public int getItemCount() {
            return totalList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv;
        public RatioImageView img;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv = (TextView) itemView.findViewById(R.id.tv);
            img = (RatioImageView) itemView.findViewById(R.id.img);

        }
    }

    /**
     * jiange
     */
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpacesItemDecoration(int space) {

            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            }
        }
    }

    interface RecyclerItemOnClickListener {
        public void OnItemClickListener();

        public void OnItemLongClickListener();
    }


}
