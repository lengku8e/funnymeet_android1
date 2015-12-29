package com.mtcent.funnymeet.ui.helper;

import android.graphics.Bitmap;

import com.mtcent.funnymeet.SOApplication;
import com.mtcent.funnymeet.util.BitmapUtil;
import com.mtcent.funnymeet.util.JSONUtil;
import com.mtcent.funnymeet.util.SafeThread;
import com.mtcent.funnymeet.util.StrUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//下载管理
public class RequestHelper {
    public static final int Type_Image = 1;
    // public static final int Type_JsonString = 2;
    // public static final int Type_CheckJsonString = 3;
    public static final int Type_DownJsonString = 4;
    public static final int Type_PostParam = 5;
    ArrayList<Pdtask> pList = new ArrayList<Pdtask>();
    SafeThread downThread = null;
    MYLruCache<String, Bitmap> mBitmapMemoryCache = new MYLruCache<String, Bitmap>(
            1);

    public RequestHelper() {
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mBitmapMemoryCache = new MYLruCache<String, Bitmap>(cacheSize) {

            // 必须重写此方法，来测量Bitmap的大小
            @Override
            protected int sizeOf(String key, Bitmap value) {
                int size = value.getRowBytes() * value.getHeight();
                return size;
            }

            public void remove(String key, Bitmap value) {
                if (value.isRecycled() == false) {
                    value.recycle();
                }
            }
        };
    }

    public void cancle() {
        if (downThread != null) {
            downThread.stopWaitFor(100);
            // downThread.cancle();
        }
        // mBitmapMemoryCache.trimToSize(0);
        mBitmapMemoryCache.evictAll();
    }

    public void remove(Object owner) {
        synchronized (pList) {
            ArrayList<Pdtask> removeList = new ArrayList<Pdtask>();
            for (Pdtask task : pList) {
                if (task.owner != null && task.owner.equals(owner)) {
                    removeList.add(task);
                }
            }

            for (Pdtask pdtask : removeList) {
                pList.remove(pdtask);
            }
        }
    }

    public void startTask(final Pdtask task) {
        new SafeThread() {
            @Override
            public boolean runUntilFalse() {
                //执行请求
                doPdtask(task);
                //dBack通过接口，指向了一个Activity类，可以在网络请求结束后，触发Activity的后续操作。
                task.dBack.onFinish(task);
                if (task.waitForCheck) {
                    JSONObject json = JSONUtil.getJstrFormNet(task.url, task.params,
                            task.paramJstr, task.time);
                    if (json != null) {
                        if (json.has(JSONUtil.TimeInfo) && json.optLong(JSONUtil.TimeInfo) != 0) {
                            JSONUtil.saveJstr(task.getUrlEx(), json);
                        } else {
                            String name = StrUtil.md5(task.getUrlEx());
                            SOApplication.getAppContext().deleteFile(name);
                        }
                        task.json = json;
                        task.dBack.onUpdate(task);
                    }
                }
                return false;
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
            }
        }.start();
    }

    public void addTask(Pdtask t) {
        synchronized (pList) {
            boolean exsit = false;
            for (Pdtask task : pList) {
                if (task != null) {
                    exsit = task.eq(t);
                    if (exsit)
                        break;
                }
            }

            if (!exsit) {
                pList.add(t);
            }
        }

        if (downThread == null) {
            downThread = new SafeThread() {
                @Override
                public boolean runUntilFalse() {
                    if (getTaskSize() > 0) {
                        Pdtask task = getTask();

                        doPdtask(task);

                        boolean include = false;
                        synchronized (pList) {
                            include = pList.contains(task);
                        }
                        if (task != null && include) {
                            task.dBack.onFinish(task);
                            removeTask(task);
                        }

                        return true;
                    } else {
                        return false;
                    }
                }

                @Override
                public void onFinsh() {
                    downThread = null;
                    super.onFinsh();
                }
            };
            downThread.start();
        }
    }

    /*
        执行请求，获取json返回值或图片位图
     */
    void doPdtask(Pdtask task) {
        if (task.tpye == Type_Image) {
            task.bitmap = getBitmapFormMemoryCache(task.url);
            if (task.bitmap == null) {
                if (StrUtil.isLocalFile(task.url)) {
                    String localName = StrUtil.getLocalFileName(task.url);
                    task.bitmap = BitmapUtil.getBitmapFromFile(localName);
                } else {
                    File bitmapFile = BitmapUtil
                            .getSaveBitmapFile(task.url);
                    task.bitmap = BitmapUtil.getBitmapFromFile(bitmapFile);

                    if (task.bitmap == null) {
                        task.bitmap = BitmapUtil.downloadBitmap(task.url);
                        BitmapUtil.saveBitmap(task.bitmap, bitmapFile);
                    }
                }
                saveBitmapToMemoryCache(task.url, task.bitmap);
            }

        } else if (task.tpye == Type_DownJsonString) {
            task.json = JSONUtil.getJstr(task.getUrlEx());
            if (task.json == null) {
                task.json = JSONUtil.getJstrFormNet(task.url, task.params,
                        task.paramJstr, task.time);
                if (task.json != null && task.json.has(JSONUtil.TimeInfo)
                        && task.json.optLong(JSONUtil.TimeInfo) != 0) {
                    JSONUtil.saveJstr(task.getUrlEx(), task.json);
                }
            } else {
                if (task.json.has(JSONUtil.TimeInfo) && task.check == true) {
                    try {
                        task.time = task.json.getLong(JSONUtil.TimeInfo);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    task.waitForCheck = true;
                }
            }

        } else if (task.tpye == Type_PostParam) {
            task.json = JSONUtil.getJstrFormNet(task.url, task.params, task.paramJstr,
                    task.time);
            System.out.println("链接：" + task.url);
            for (int i = 0; i < task.params.size(); i++) {
                System.out.println("参数" + i + ":" + task.params.get(i).getName() + "/" + task.params.get(i).getValue());
            }

            System.out.println("参数字符串：" + task.paramJstr);
            System.out.println("json字符串：" + task.json);
        }

    }

    private void saveBitmapToMemoryCache(String url, Bitmap bitmap) {
        if (url != null && bitmap != null) {
            mBitmapMemoryCache.put(url, bitmap);
        }
    }

    public Bitmap getBitmapFormMemoryCache(String url) {
        Bitmap bitmap = null;
        if (url != null) {
            bitmap = mBitmapMemoryCache.get(url);
        }
        if (bitmap != null && bitmap.isRecycled()) {
            mBitmapMemoryCache.remove(url, bitmap);
            bitmap = null;
        }
        return bitmap;
    }

    public Bitmap getBitmapFormMemoryCacheORadd(String url) {
        Bitmap bitmap = null;
        if (url != null) {
            bitmap = mBitmapMemoryCache.get(url);

            if (bitmap != null && bitmap.isRecycled()) {
                mBitmapMemoryCache.remove(url, bitmap);
                bitmap = null;
            }

            if (bitmap == null) {
                if (StrUtil.isLocalFile(url)) {
                    String localName = StrUtil.getLocalFileName(url);
                    bitmap = BitmapUtil.getBitmapFromFile(localName);
                } else {

                    File bitmapFile = BitmapUtil.getSaveBitmapFile(url);
                    bitmap = BitmapUtil.getBitmapFromFile(bitmapFile);
                }
                if (bitmap != null) {
                    saveBitmapToMemoryCache(url, bitmap);
                }
            }
        }
        return bitmap;
    }

    int getTaskSize() {
        int size = 0;
        synchronized (pList) {
            size = pList.size();
        }
        return size;
    }

    Pdtask getTask() {
        Pdtask task = null;
        synchronized (pList) {
            task = pList.get(0);
        }
        return task;
    }

    void removeTask(Pdtask task) {
        synchronized (pList) {
            pList.remove(task);
        }
    }


    public interface DownBack {
        void onFinish(Pdtask t);

        void onUpdate(Pdtask t);
    }

    static public class Pdtask {
        public String url;
        public Bitmap bitmap;
        public JSONObject json;
        DownBack dBack;
        int tpye;
        String name;
        long time;
        Object owner;
        String paramJstr;
        boolean check;
        boolean waitForCheck;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        public Pdtask(Object _owner, DownBack back, String _url, String _pjstr,
                      int _type, String _name, long _time, boolean _check) {
            dBack = back;
            url = _url;
            tpye = _type;
            name = _name;
            time = _time;
            owner = _owner;
            paramJstr = _pjstr;
            check = _check;
            waitForCheck = false;

        }

        public Pdtask addParam(String name, String value) {
            params.add(new BasicNameValuePair(name, value));
            return this;
        }

        public String getParam(String name) {
            String value = null;

            for (NameValuePair nvp : params) {
                if (nvp.getName().equals(name)) {
                    return nvp.getValue();
                }
            }
            return value;
        }

        public String getUrlEx() {
            StringBuffer buff = new StringBuffer(url);
            buff.append("?");
            for (NameValuePair nvp : params) {
                if (!nvp.getName().equals(JSONUtil.TimeInfo)) {
                    buff.append(nvp.getName());
                    buff.append(nvp.getValue());
                }
            }
            if (paramJstr != null) {
                buff.append(paramJstr);
            }
            return buff.toString();
        }

        public void setOnFinshBack(DownBack back) {
            dBack = back;
        }

        public boolean eq(Pdtask t) {
            boolean e = false;
            if (t != null) {
                if (t.tpye == tpye) {
                    if (t.dBack != null && t.dBack.equals(dBack)) {
                        if (t.owner != null && t.owner.equals(owner)) {
                            if (t.url != null && t.url.equals(url)) {
                                e = true;
                            }
                        }
                    }
                }
            }

            return e;

        }
    }

    class MYLruCache<T1, T2> {
        int mSize = 0;
        ArrayList<Node> list = new ArrayList<Node>();

        public MYLruCache(int maxSize) {
            mSize = maxSize;
        }

        protected int sizeOf(T1 key, T2 value) {
            return 1;
        }

        public void remove(T1 key, T2 value) {

        }

        public T2 get(T1 t1) {
            T2 t2 = null;
            synchronized (list) {
                Node tNode = null;
                for (Node node : list) {
                    if (node.t1.equals(t1)) {
                        tNode = node;
                        break;
                    }
                }
                if (tNode != null) {
                    t2 = tNode.t2;
                    list.remove(tNode);
                    list.add(0, tNode);
                }
            }
            return t2;
        }

        public void put(T1 t1, T2 value) {
            synchronized (list) {
                for (Node node : list) {
                    if (node.t1.equals(t1)) {
                        remove(node.t1, node.t2);
                        list.remove(node);
                        break;
                    }
                }
                Node tNode = new Node();
                tNode.t1 = t1;
                tNode.t2 = value;
                tNode.s = sizeOf(tNode.t1, tNode.t2);
                list.add(0, tNode);

                int size = 0;
                ArrayList<Node> listDelete = new ArrayList<Node>();
                for (Node node : list) {
                    if (size + node.s > mSize && !tNode.equals(node)) {
                        listDelete.add(node);
                    } else {
                        size += node.s;
                    }
                }
                for (Node node : listDelete) {
                    list.remove(node);
                    remove(node.t1, node.t2);
                }
            }

            return;
        }

        public void evictAll() {
            synchronized (list) {
                for (Node node : list) {
                    remove(node.t1, node.t2);
                }
                list.clear();
            }
        }

        class Node {
            T1 t1;
            T2 t2;
            int s = 0;
        }
    }


}
